package com.easymall.service;

import com.easymall.entity.po.Banner;
import java.util.List;

public interface BannerService {
    List<Banner> getList();
    List<Banner> getEnabledList();
    boolean add(Banner banner);
    boolean update(Banner banner);
    boolean delete(Integer id);
    boolean updateStatus(Integer id, Integer status);
}