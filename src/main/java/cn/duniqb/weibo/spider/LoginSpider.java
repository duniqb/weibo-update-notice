package cn.duniqb.weibo.spider;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录相关的爬虫
 *
 * @author zhanglb
 * @version v1.0.0
 * @date 2021/6/21 021 23:11
 * @since 1.8
 */
@Component
public class LoginSpider {
    /**
     * 图片的存放路径
     */
    @Value("${path.qrcode}")
    private String imagePath;

    /**
     * 获取二维码
     */
    public String getQrcode(long current) {
        HttpResponse response = HttpRequest.get("https://login.sina.com.cn/sso/qrcode/image?entry=weibo&size=180&callback=STK_" + current)
                .timeout(20000)
                .method(Method.GET)
                .execute();
        Object data = JSONUtil.parseObj(response.body().split("\\(")[1].split("\\)")[0]).get("data");
        String qrId = String.valueOf(JSONUtil.parseObj(data).get("qrid"));
        String imageUrl = String.valueOf(JSONUtil.parseObj(data).get("image"));

        HttpResponse imageResponse = HttpRequest.get("https:" + imageUrl)
                .timeout(20000)
                .method(Method.GET)
                .execute();
//        imageResponse.writeBody("D:\\weibo-qrcode\\" + current + ".png");
        imageResponse.writeBody(imagePath + current + ".png");

        return qrId;
    }

    /**
     * 检测二维码使用情况
     */
    public Map<String, String> checkLogin(String qrId, long current) {
        HttpResponse response = HttpRequest.get("https://login.sina.com.cn/sso/qrcode/check?entry=weibo&qrid=" + qrId + "&callback=STK_" + current)
                .header(Header.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                .timeout(20000)
                .method(Method.GET)
                .execute();
        JSONObject result = JSONUtil.parseObj(response.body().split("\\(")[1].split("\\)")[0]);
        String msg = result.get("msg").toString();
        String data = result.get("data").toString();
        String retCode = result.get("retcode").toString();

        Map<String, String> map = new HashMap<>();
        map.put("msg", msg);
        map.put("data", data);
        map.put("retCode", retCode);
        return map;
    }

    /**
     * 发起登录
     *
     * @param alt
     * @param current
     * @return
     */
    public String login(String alt, long current) {
        String url = "https://login.sina.com.cn/sso/login.php?entry=weibo&returntype=TEXT&crossdomain=1&cdult=3&domain=weibo.com&alt=" + alt
                + "&savestate=30&callback=STK_" + current;
        HttpResponse response = HttpRequest.get(url)
                .timeout(20000)
                .method(Method.GET)
                .execute();
        // 存储键为 SUB 的 Cookie
        return response.getCookieValue("SUB");
    }

    public static void main(String[] args) throws InterruptedException {
        long current = System.currentTimeMillis();
        String qrId = new LoginSpider().getQrcode(current);

        boolean checkLogin = true;
        while (checkLogin) {
            Map<String, String> map = new LoginSpider().checkLogin(qrId, current);
            // 未使用
            if ("50114001".equals(map.get("retCode"))) {

            }
            // 成功扫描，请在手机点击确认以登录
            else if ("50114002".equals(map.get("retCode"))) {

            }
            // succ
            else if ("20000000".equals(map.get("retCode"))) {
                String alt = String.valueOf(JSONUtil.parseObj(map.get("data")).get("alt"));
                checkLogin = false;

                String sub = new LoginSpider().login(alt, current);
                new WeiboSpider().parseWeiboFocusGroup(sub, "4263192240412997", "5");
            }
            // 该二维码已登录，请重新扫描
            else if ("50114004".equals(map.get("retCode"))) {

            }
            Thread.sleep(1000);
        }
    }

}
