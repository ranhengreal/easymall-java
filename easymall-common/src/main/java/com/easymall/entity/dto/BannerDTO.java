package com.easymall.entity.dto;

import com.easymall.entity.po.Banner;
import lombok.Data;
import java.util.Date;

public class BannerDTO {

    @Data
    public static class Add {
        private String title;
        private String imageUrl;
        private String linkUrl;
        private Integer sort;
        private Integer status;
    }

    @Data
    public static class Update {
        private Integer id;
        private String title;
        private String imageUrl;
        private String linkUrl;
        private Integer sort;
        private Integer status;
    }

    @Data
    public static class Response {
        private Integer id;
        private String title;
        private String imageUrl;
        private String linkUrl;
        private Integer sort;
        private Integer status;
        private Date createTime;
        private Date updateTime;

        public static Response fromPO(Banner banner) {
            Response resp = new Response();
            resp.setId(banner.getId());
            resp.setTitle(banner.getTitle());
            resp.setImageUrl(banner.getImageUrl());
            resp.setLinkUrl(banner.getLinkUrl());
            resp.setSort(banner.getSort());
            resp.setStatus(banner.getStatus());
            resp.setCreateTime(banner.getCreateTime());
            resp.setUpdateTime(banner.getUpdateTime());
            return resp;
        }
    }
}