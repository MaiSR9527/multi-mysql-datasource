package org.msr.masterslave.service;

import org.msr.masterslave.master.mapper.MasterUserInfoMapper;
import org.msr.masterslave.model.UserInfo;
import org.msr.masterslave.slave.mapper.SlaveUserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author MaiShuRen
 * @site http://www.maishuren.top
 * @since 2021-03-28 23:17
 **/
@Service("orderService")
public class UserInfoService {


    @Autowired
    private MasterUserInfoMapper masterUserInfoMapper;

    @Autowired
    private SlaveUserInfoMapper slaveUserInfoMapper;

    public UserInfo read(Integer id) {
        return slaveUserInfoMapper.selectByPrimaryKey(id);
    }

    public int write(UserInfo userInfo){
        return masterUserInfoMapper.insert(userInfo);
    }


}
