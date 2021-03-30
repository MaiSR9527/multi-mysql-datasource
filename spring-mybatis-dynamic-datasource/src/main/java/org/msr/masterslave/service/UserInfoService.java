package org.msr.masterslave.service;

import org.msr.masterslave.config.MyDataSource;
import org.msr.masterslave.mapper.UserInfoMapper;
import org.msr.masterslave.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: MaiShuRen
 * site: http://www.maishuren.top
 * since: 2021-03-28 23:17
 **/
@Service("orderService")
public class UserInfoService {


    @Autowired
    private UserInfoMapper userInfoMapper;

    public UserInfo read(Integer id) {
        MyDataSource.setDataSource("slave");
        return userInfoMapper.selectByPrimaryKey(id);
    }

    public int write(UserInfo userInfo) {
        MyDataSource.setDataSource("master");
        return userInfoMapper.insert(userInfo);
    }


}
