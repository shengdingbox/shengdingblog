package com.shengdingbox.blog.persistence.mapper;

import java.util.List;

import com.shengdingbox.blog.plugin.BaseMapper;
import org.springframework.stereotype.Repository;

import com.shengdingbox.blog.persistence.beans.SysUserRole;

/**
 * 
 * @author Dabao (17611555590@163.com)
 * @version 1.0
 * @website https://www.shengdingbox.com
 * @date 2019年7月16日
 * @since 1.0
 */
@Repository
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    List<Integer> findUserIdByRoleId(Integer roleId);
}