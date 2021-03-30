package org.msr.masterslave.slave.mapper;

import org.msr.masterslave.model.UserInfo;

public interface SlaveUserInfoMapper {
    UserInfo selectByPrimaryKey(Integer id);
}