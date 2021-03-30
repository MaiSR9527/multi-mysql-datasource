package org.msr.masterslave.master.mapper;

import org.msr.masterslave.model.UserInfo;


public interface MasterUserInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);
}