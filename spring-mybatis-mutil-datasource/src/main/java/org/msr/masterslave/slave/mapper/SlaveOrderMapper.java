package org.msr.masterslave.slave.mapper;

import org.msr.masterslave.model.UserInfo;

public interface SlaveOrderMapper {
    UserInfo selectByPrimaryKey(Integer id);
}