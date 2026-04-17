package com.easymall.service.impl;

import com.easymall.entity.po.Banner;
import com.easymall.mapper.BannerMapper;
import com.easymall.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerMapper bannerMapper;

    @Override
    public List<Banner> getList() {
        return bannerMapper.selectAll();
    }

    @Override
    public List<Banner> getEnabledList() {
        return bannerMapper.selectEnabled();
    }

    @Override
    public boolean add(Banner banner) {
        return bannerMapper.insert(banner) > 0;
    }

    @Override
    public boolean update(Banner banner) {
        return bannerMapper.update(banner) > 0;
    }

    @Override
    public boolean delete(Integer id) {
        return bannerMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateStatus(Integer id, Integer status) {
        return bannerMapper.updateStatus(id, status) > 0;
    }
}