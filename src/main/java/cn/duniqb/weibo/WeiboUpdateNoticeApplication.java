package cn.duniqb.weibo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WeiboUpdateNoticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeiboUpdateNoticeApplication.class, args);
    }

}
