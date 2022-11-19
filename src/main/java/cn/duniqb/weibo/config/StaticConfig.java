package cn.duniqb.weibo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zhanglb
 * @version v1.0.0
 * @date 2021/6/24 024 20:17
 * @since 1.8
 */
@Configuration
public class StaticConfig implements WebMvcConfigurer {

    /**
     * 图片的存放路径
     */
    @Value("${path.qrcode}")
    private String imagePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 和页面有关的静态目录都放在项目的static目录下
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/");
        // 其中OTA表示访问的前缀。"file:D:/OTA/"是文件真实的存储路径
        registry.addResourceHandler("/image/**").addResourceLocations("file:" + imagePath);
    }
}
