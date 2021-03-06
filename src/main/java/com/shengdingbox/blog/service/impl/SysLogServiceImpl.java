package com.shengdingbox.blog.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shengdingbox.blog.entity.Log;
import com.shengdingbox.blog.entity.User;
import com.shengdingbox.blog.enums.LogLevelEnum;
import com.shengdingbox.blog.enums.LogTypeEnum;
import com.shengdingbox.blog.enums.PlatformEnum;
import com.shengdingbox.blog.persistence.beans.SysLog;
import com.shengdingbox.blog.persistence.mapper.SysLogMapper;
import com.shengdingbox.blog.service.SysLogService;
import com.shengdingbox.blog.utils.SessionUtil;
import com.shengdingbox.blog.vo.LogConditionVO;
import com.zhouzifei.tool.util.RequestUtil;
import com.zhouzifei.tool.util.WebSpiderUtils;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
@Service
public class SysLogServiceImpl implements SysLogService {

    @Autowired
    private SysLogMapper sysLogMapper;

    @Override
    public PageInfo<Log> findPageBreakByCondition(LogConditionVO vo) {
        PageHelper.startPage(vo.getPageNumber(), vo.getPageSize());
        List<SysLog> list = sysLogMapper.findPageBreakByCondition(vo);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<Log> boList = new ArrayList<>();
        for (SysLog sysLog : list) {
            boList.add(new Log(sysLog));
        }
        PageInfo bean = new PageInfo<SysLog>(list);
        bean.setList(boList);
        return bean;
    }

    @Async
    @Override
    public void asyncSaveSystemLog(PlatformEnum platform, String bussinessName) {
        String ua = RequestUtil.getUa();
        Log sysLog = new Log();
        sysLog.setLogLevel(LogLevelEnum.INFO);
        sysLog.setType(platform.equals(PlatformEnum.WEB) ? LogTypeEnum.VISIT : LogTypeEnum.SYSTEM);
        sysLog.setIp(RequestUtil.getIp());
        sysLog.setReferer(RequestUtil.getReferer());
        sysLog.setRequestUrl(RequestUtil.getRequestUrl());
        sysLog.setUa(ua);
        sysLog.setSpiderType(WebSpiderUtils.parseUa(ua));
        sysLog.setParams(JSONObject.toJSONString(RequestUtil.getParametersMap()));
        User user = SessionUtil.getUser();
        if (user != null) {
            sysLog.setUserId(user.getId());
            sysLog.setContent(String.format("用户: [%s] | 操作: %s", user.getUsername(), bussinessName));
        } else {
            sysLog.setContent(String.format("访客: [%s] | 操作: %s", sysLog.getIp(), bussinessName));
        }

        try {
            UserAgent agent = UserAgent.parseUserAgentString(ua);
            sysLog.setBrowser(agent.getBrowser().getName());
            sysLog.setOs(agent.getOperatingSystem().getName());
            this.insert(sysLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Log insert(Log entity) {
        Assert.notNull(entity, "Log不可为空！");
        entity.setUpdateTime(new Date());
        entity.setCreateTime(new Date());
        sysLogMapper.insertSelective(entity.getSysLog());
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByPrimaryKey(Integer primaryKey) {
        return sysLogMapper.deleteByPrimaryKey(primaryKey) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSelective(Log entity) {
        Assert.notNull(entity, "Log不可为空！");
        entity.setUpdateTime(new Date());
        return sysLogMapper.updateByPrimaryKeySelective(entity.getSysLog()) > 0;
    }

    @Override
    public Log getByPrimaryKey(Integer primaryKey) {
        Assert.notNull(primaryKey, "PrimaryKey不可为空！");
        SysLog entity = sysLogMapper.selectByPrimaryKey(primaryKey);
        return null == entity ? null : new Log(entity);
    }
}
