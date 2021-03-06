package com.shengdingbox.blog.service.impl;

import com.shengdingbox.blog.entity.ArticleLook;
import com.shengdingbox.blog.persistence.mapper.BizArticleLookMapper;
import com.shengdingbox.blog.service.BizArticleLookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * 文章浏览记录
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
@Service
public class BizArticleLookServiceImpl implements BizArticleLookService {

    @Autowired
    private BizArticleLookMapper bizArticleLookMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleLook insert(ArticleLook entity) {
        Assert.notNull(entity, "ArticleLook不可为空！");
        entity.setUpdateTime(new Date());
        entity.setCreateTime(new Date());
        bizArticleLookMapper.insertSelective(entity.getBizArticleLook());
        return entity;
    }
}
