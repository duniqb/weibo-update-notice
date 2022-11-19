package cn.duniqb.weibo.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.extra.mail.MailUtil;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author zhanglb
 * @version v1.0.0
 * @date 2021/6/24 024 23:30
 * @since 1.8
 */
public class Test {
    public static void main(String[] args) throws SQLException {
//        LocalDateTime parse = LocalDateTime.parse("Thu Feb 11 09:43:12 +0800 2021", DateTimeFormatter.ofPattern("E MMMM MM dd HH:mm:ss ZZZZ yyyy", Locale.US));
        LocalDateTime parse = LocalDateTime.parse("Sat Nov 27 00:55:57 +0800 2021", DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss Z yyyy", Locale.US));
        System.out.println(parse);


//        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", new DateFormatSymbols());//MMM dd hh:mm:ss Z yyyy
//        try {
//            System.out.println(sdf.parse("Thu Feb 11 09:43:12 +0800 2021"));
//        } catch (ParseException ex) {
//        }


        // 2021-11-27T12:15 GMT+08:00
        ZonedDateTime zdt = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm ZZZZ");
        System.out.println(formatter.format(zdt));
        // 2021 11月 27 周六 12:15
        DateTimeFormatter zhFormatter = DateTimeFormatter.ofPattern("yyyy MMM dd EE HH:mm", Locale.CHINA);
        System.out.println(zhFormatter.format(zdt));
        // Sat, November/27/2021 12:15
        DateTimeFormatter usFormatter = DateTimeFormatter.ofPattern("E, MMMM/dd/yyyy HH:mm", Locale.US);
        System.out.println(usFormatter.format(zdt));

    }
}
