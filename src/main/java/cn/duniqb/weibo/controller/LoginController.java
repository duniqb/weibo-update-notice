package cn.duniqb.weibo.controller;

import cn.duniqb.weibo.common.R;
import cn.duniqb.weibo.spider.LoginSpider;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 扫码登陆页面
 *
 * @author zhanglb
 * @version v1.0.0
 * @date 2021/6/24 024 19:32
 * @since 1.8
 */
@RestController
@RequestMapping("/login")
class LoginController {

    @Autowired
    private LoginSpider loginSpider;

    private static final String WEIBO_COOKIE = "weibo_cookie";

    /**
     * 获取二维码
     */
    @GetMapping("/getQrCode")
    public R getQrCode() {
        long current = System.currentTimeMillis();

        String qrId = loginSpider.getQrcode(current);
        System.out.println("\nqrId:" + qrId);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("imageName", "image/" + current + ".png");
        resultMap.put("qrId", qrId);
        resultMap.put("current", current);

        return R.ok().put("data", resultMap);
    }

    /**
     * 检测二维码使用情况，每秒调用一次
     *
     * @param qrId
     * @param current
     */
    @GetMapping("/checkLogin")
    public R checkLogin(@RequestParam String qrId, @RequestParam long current) {
        Map<String, String> map = loginSpider.checkLogin(qrId, current);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("msg", map.get("msg"));
        resultMap.put("data", map.get("data"));
        resultMap.put("retCode", map.get("retCode"));

        return R.ok().put("data", resultMap);
    }

    /**
     * 登录微博
     *
     * @return
     */
    @GetMapping("/login")
    public R login(@RequestParam String alt, @RequestParam long current, @RequestParam String qrId) {
        String sub = loginSpider.login(alt, current);
        Db db = Db.use();
        // 保存 sub
        try {
            db.insertOrUpdate(Entity.create(WEIBO_COOKIE).set("cookie", sub).set("qr_id", qrId).set("create_time", LocalDateTime.now()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return R.ok("登录成功");
    }
}
