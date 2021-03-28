package org.msr.masterslave.service;

import org.msr.masterslave.master.mapper.MasterOrderMapper;
import org.msr.masterslave.model.UserInfo;
import org.msr.masterslave.slave.mapper.SlaveOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * author: MaiShuRen
 * site: http://www.maishuren.top
 * since: 2021-03-28 23:17
 **/
@Service("orderService")
public class OrderService {


    @Autowired
    private MasterOrderMapper masterOrderMapper;

    @Autowired
    private SlaveOrderMapper slaveOrderMapper;

    public UserInfo read(Integer id) {
        return slaveOrderMapper.selectByPrimaryKey(id);
    }

    public int write(UserInfo userInfo){
        return masterOrderMapper.insert(userInfo);
    }


}
