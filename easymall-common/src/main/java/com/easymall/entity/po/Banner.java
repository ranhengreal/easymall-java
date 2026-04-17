package com.easymall.entity.po;

import lombok.Data;
import java.util.Date;

@Data
public class Banner {
    private Integer id;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private Integer sort;
    private Integer status;  // 0禁用 1启用
    private Date createTime;
    private Date updateTime;
}