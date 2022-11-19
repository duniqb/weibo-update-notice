package cn.duniqb.weibo.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 微博实体类
 *
 * @author zhanglb
 * @version v1.0.0
 * @date 2021/6/19 019 14:18
 * @since 1.8
 */
@Data
public class WeiboEntity {
    /**
     * 唯一标识
     */
    private long id;
    /**
     * 创建时间 created_at
     */
    private LocalDateTime createdTime;
    /**
     * 昵称 user.screen_name
     */
    private String nickName;
    /**
     * 备注 user.remark
     */
    private String remark;
    /**
     * 内容 text_raw
     */
    private String content;
    /**
     * 来源 source
     */
    private String source;
    /**
     * 图片数量 pic_num
     */
    private int picNum;
    /**
     * 图片地址列表：pic_ids
     */
    private List<Object> picList;
    /**
     * 地址 tag_struct.tag_name
     */
    private String address;

}
