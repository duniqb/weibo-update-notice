package cn.duniqb.weibo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author zhanglb
 * @version v1.0.0
 * @date 2021/6/24 024 21:27
 * @since 1.8
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }
}
