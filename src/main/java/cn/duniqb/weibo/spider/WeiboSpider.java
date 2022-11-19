package cn.duniqb.weibo.spider;

import cn.duniqb.weibo.entity.WeiboEntity;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 解析微博的爬虫
 *
 * @author zhanglb
 * @version v1.0.0
 * @date 2021/6/19 019 13:44
 * @since 1.8
 */
@Component
public class WeiboSpider {

    /**
     * 解析微博关注分组
     *
     * @param sub
     * @param listId
     * @param count
     * @return
     */
    public List<WeiboEntity> parseWeiboFocusGroup(String sub, String listId, String count) {
        String url = "https://weibo.com/ajax/feed/groupstimeline?list_id=" + listId + "&refresh=4&fast_refresh=1&count=" + count;
        HttpResponse response = HttpRequest.get(url)
                .header(Header.ACCEPT, "application/json, text/plain, */*")
                .header(Header.ACCEPT_ENCODING, "gzip, deflate, br")
                .header(Header.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                .header("sec-ch-ua", "\" Not;A Brand\";v=\"99\", \"Microsoft Edge\";v=\"91\", \"Chromium\";v=\"91\"")
                .header(Header.COOKIE, "SUB=" + sub + ";")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-fetch-dest", "empty")
                .header("sec-fetch-mode", "cors")
                .header("sec-fetch-site", "same-origin")
                .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.101 Safari/537.36 Edg/91.0.864.48")
                .header("x-requested-with", "XMLHttpRequest")
                .timeout(20000)
                .method(Method.GET)
                .execute();
        List<WeiboEntity> weiboEntityList = new ArrayList<>();
        if (response.getStatus() == 200) {
            System.out.println(response.body());
            JSONObject body = JSONUtil.parseObj(response.body());
            JSONArray weiboList = JSONUtil.parseArray(body.get("statuses"));

            for (Object o : weiboList) {
                WeiboEntity weiboEntity = new WeiboEntity();
                JSONObject weibo = JSONUtil.parseObj(o);
                System.out.println(weibo);
                if (!"null".equals(String.valueOf(weibo.get("tag_struct")))) {
                    weiboEntity.setAddress(String.valueOf(JSONUtil.parseObj(JSONUtil.parseArray(weibo.get("tag_struct")).get(0)).get("tag_name")));
                }
                weiboEntity.setId(Long.parseLong(String.valueOf(weibo.get("id"))));
                weiboEntity.setCreatedTime(LocalDateTime.parse(String.valueOf(weibo.get("created_at")), DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss Z yyyy", Locale.US)));
                weiboEntity.setNickName(String.valueOf(JSONUtil.parseObj(weibo.get("user")).get("screen_name")));
                weiboEntity.setRemark(String.valueOf(JSONUtil.parseObj(weibo.get("user")).get("remark")));
                // 如果 String.valueOf(weibo.get("text")) 包含“展开”，则请求全文
                weiboEntity.setContent(String.valueOf(weibo.get("text_raw")));
                weiboEntity.setSource(String.valueOf(weibo.get("source")));
                weiboEntity.setPicNum(Integer.parseInt(String.valueOf(weibo.get("pic_num"))));
                weiboEntity.setPicList(ListUtil.toList(JSONUtil.parseArray(weibo.get("pic_ids"))));

                weiboEntityList.add(weiboEntity);
            }
        }
        return weiboEntityList;
    }

    /**
     * 解析微博关注用户
     *
     * @param sub
     * @param userId
     * @param count
     * @return
     */
    public List<WeiboEntity> parseWeiboFocusUser(String sub, String userId, String count) {
        String url = "https://weibo.com/ajax/statuses/mymblog?uid=" + userId + "&page=0&feature=0&count=" + count;
        HttpResponse response = HttpRequest.get(url)
                .header(Header.ACCEPT, "application/json, text/plain, */*")
                .header(Header.ACCEPT_ENCODING, "gzip, deflate, br")
                .header(Header.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                .header("sec-ch-ua", "\" Not;A Brand\";v=\"99\", \"Microsoft Edge\";v=\"91\", \"Chromium\";v=\"91\"")
                .header(Header.COOKIE, "SUB=" + sub + ";")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-fetch-dest", "empty")
                .header("sec-fetch-mode", "cors")
                .header("sec-fetch-site", "same-origin")
                .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.101 Safari/537.36 Edg/91.0.864.48")
                .header("x-requested-with", "XMLHttpRequest")
                .timeout(20000)
                .method(Method.GET)
                .execute();
        List<WeiboEntity> weiboEntityList = new ArrayList<>();
        if (response.getStatus() == 200) {
            System.out.println(response.body());
            JSONObject body = JSONUtil.parseObj(response.body());
            JSONArray weiboList = JSONUtil.parseArray(JSONUtil.parseObj(body.get("data")).get("list"));

            for (Object o : weiboList) {
                WeiboEntity weiboEntity = new WeiboEntity();
                JSONObject weibo = JSONUtil.parseObj(o);
                System.out.println(weibo);
                if (!"null".equals(String.valueOf(weibo.get("tag_struct")))) {
                    weiboEntity.setAddress(String.valueOf(JSONUtil.parseObj(JSONUtil.parseArray(weibo.get("tag_struct")).get(0)).get("tag_name")));
                }
                weiboEntity.setId(Long.parseLong(String.valueOf(weibo.get("id"))));
                weiboEntity.setCreatedTime(LocalDateTime.parse(String.valueOf(weibo.get("created_at")), DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss Z yyyy", Locale.US)));
                weiboEntity.setNickName(String.valueOf(JSONUtil.parseObj(weibo.get("user")).get("screen_name")));
                weiboEntity.setRemark(String.valueOf(JSONUtil.parseObj(weibo.get("user")).get("remark")));
                weiboEntity.setContent(String.valueOf(weibo.get("text_raw")));
                weiboEntity.setSource(String.valueOf(weibo.get("source")));
                weiboEntity.setPicNum(Integer.parseInt(String.valueOf(weibo.get("pic_num"))));
                weiboEntity.setPicList(ListUtil.toList(JSONUtil.parseArray(weibo.get("pic_ids"))));

                weiboEntityList.add(weiboEntity);
            }
        }
        return weiboEntityList;
    }

    public static void main(String[] args) throws InterruptedException {
        WeiboSpider weiboSpider = new WeiboSpider();
//        List<WeiboEntity> weiboEntityList = new WeiboSpider().parseWeiboFocusGroup("", "4263192240412997", "5");

        LocalDateTime now = LocalDateTime.now().minusMinutes(1);
        List<WeiboEntity> weiboEntityList = weiboSpider.parseWeiboFocusUser("_2A25MpdbMDeRhGedG6VEU9y_IwzyIHXVv008ErDV8PUNbmtB-LRH1kW9NUXxpG4ofYMebFae0DULHQ6z-WxldSdBj", "6060418736", "2");
        for (WeiboEntity weiboEntity : weiboEntityList) {
            System.out.println(weiboEntity);
        }

        System.out.println("比较：");
        List<WeiboEntity> filterList = weiboEntityList.stream().filter(entity -> entity.getCreatedTime().isAfter(now)).collect(Collectors.toList());
        System.out.println(filterList);
    }
}
