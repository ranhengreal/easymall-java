package com.easymall.entity.po;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Brand {
    private String brandId;
    private String brandName;
    private String brandLogo;
    private String description;
    private Integer sort;
    private Integer status;  // 0-禁用，1-启用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
