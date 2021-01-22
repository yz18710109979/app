package com.jingyou.main.sys.service;


import com.baomidou.mybatisplus.service.IService;
import com.jingyou.main.sys.dto.RegisterDto;
import com.jingyou.main.sys.dto.UserDto;
import com.jingyou.main.sys.model.User;
import com.jingyou.main.sys.model.UserInfo;

import java.util.List;

public interface IUserService extends IService<User> {

    List<UserDto> userAll();
    /**
     * 注册
     * @param dto
     * @return
     */
    Object register(RegisterDto dto);
}
