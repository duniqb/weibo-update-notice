package cn.duniqb.weibo.spider;

import cn.duniqb.weibo.entity.WeiboEntity;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.sql.Condition;
import cn.hutool.db.sql.Direction;
import cn.hutool.db.sql.Order;
import cn.hutool.db.sql.SqlBuilder;
import cn.hutool.extra.mail.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhanglb
 * @version v1.0.0
 * @date 2021/6/24 024 23:55
 * @since 1.8
 */
@Component
public class ScheduleTask {

    private static final String WEIBO_COOKIE = "weibo_cookie";
    private static final String WEIBO_FOCUS = "weibo_focus";

    @Autowired
    private WeiboSpider weiboSpider;

    /**
     * 定时查询群组
     */
    @Scheduled(fixedRate = 60 * 1000L)
    public void groupScheduleTask() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("\n" + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        Db db = Db.use();
        // 读取 cookie
        try {
            SqlBuilder sqlBuilder = SqlBuilder.create()
                    .select("*")
                    .from(WEIBO_COOKIE)
                    .orderBy(new Order("create_time", Direction.DESC));
            List<Entity> cookieList = db.query(sqlBuilder.build());
            if (cookieList.size() > 0) {
                String sub = cookieList.get(0).getStr("cookie");
                System.out.println("读取到sub：" + sub);

                // 读取群组信息
                List<Entity> groupList = db.findBy(WEIBO_FOCUS, new Condition("type", "=", "1"));

                for (Entity group : groupList) {
                    List<WeiboEntity> weiboEntityList = weiboSpider.parseWeiboFocusGroup(sub, group.getStr("serial_id"), group.getStr("request_num"));
                    System.out.println("查询到群组总条数" + weiboEntityList.size());

                    List<WeiboEntity> filterList = weiboEntityList.stream().filter(entity -> entity.getCreatedTime().isAfter(now.minusMinutes(1))).collect(Collectors.toList());
                    System.out.println("过滤后条数" + filterList.size());
                    if (filterList.size() > 0) {
                        filterList.forEach(this::sendEmail);
                    }

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 定时查询用户
     */
    @Scheduled(fixedRate = 60 * 1000L)
    public void userScheduleTask() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("\n" + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Db db = Db.use();
        // 读取 cookie
        try {
            SqlBuilder sqlBuilder = SqlBuilder.create()
                    .select("*")
                    .from(WEIBO_COOKIE)
                    .orderBy(new Order("create_time", Direction.DESC));
            List<Entity> cookieList = db.query(sqlBuilder.build());
            if (cookieList.size() > 0) {
                String sub = cookieList.get(0).getStr("cookie");
                System.out.println("读取到sub：" + sub);

                // 读取群组信息
                List<Entity> userList = db.findBy(WEIBO_FOCUS, new Condition("type", "=", "2"));

                for (Entity user : userList) {
                    List<WeiboEntity> weiboEntityList = weiboSpider.parseWeiboFocusUser(sub, user.getStr("serial_id"), user.getStr("request_num"));
                    System.out.println("查询到用户总条数" + weiboEntityList.size());

                    List<WeiboEntity> filterList = weiboEntityList.stream().filter(entity -> entity.getCreatedTime().isAfter(now.minusMinutes(1))).collect(Collectors.toList());
                    System.out.println("过滤后条数" + filterList.size());
                    if (filterList.size() > 0) {
                        filterList.forEach(this::sendEmail);
                    }

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void sendEmail(WeiboEntity weiboEntity) {
        List<Object> picList = weiboEntity.getPicList();
        List<String> picUrlList = picList.stream().map(pic -> "https://wx1.sinaimg.cn/orj360/" + pic + ".jpg").collect(Collectors.toList());

        String reMark = (weiboEntity.getRemark() == null || "null".equals(weiboEntity.getRemark())) ? "无" : weiboEntity.getRemark();
        String source = (weiboEntity.getSource() == null || "".equals(weiboEntity.getSource()) ? "无" : weiboEntity.getSource());
        String address = weiboEntity.getAddress() == null ? "无" : weiboEntity.getAddress().contains("ENERGYFUELSINC") ? "无" : weiboEntity.getAddress();

        String subjectTemplate = "{}：{}";
        String str = StrUtil.format(subjectTemplate, weiboEntity.getNickName(), weiboEntity.getContent());
        StringBuilder contentTemplate = new StringBuilder(""
                + "<text style=\"font-weight: bold\">内容：</text>{}</br> "
                + "<text style=\"font-weight: bold\">时间：</text>{}</br>"
                + "<text style=\"font-weight: bold\">昵称：</text>{}</br>"
                + "<text style=\"font-weight: bold\">备注：</text>{}</br>"
                + "<text style=\"font-weight: bold\">来自：</text>{}</br>"
                + "<text style=\"font-weight: bold\">定位：</text>{}</br>"
                + "<text style=\"font-weight: bold\">图片：</text>{}</br>"
        );

        for (String s : picUrlList) {
            contentTemplate.append("<img src='").append(s).append("' alt=''>&nbsp;");
        }
        String contentSend = StrUtil.format(contentTemplate.toString(), weiboEntity.getContent(), weiboEntity.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                weiboEntity.getNickName(), reMark, source, address, picUrlList.size() + " 张");
        MailUtil.send("duniqb@qq.com", str, contentSend, true);
    }
}
