package com.jingyou.main.sys.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jingyou.main.sys.dto.RegisterDto;
import com.jingyou.main.sys.dto.UserDto;
import com.jingyou.main.sys.model.User;
import com.jingyou.main.sys.mapper.UserInfoMapper;
import com.jingyou.main.sys.mapper.UserMapper;
import com.jingyou.main.sys.service.IUserService;
import com.jingyou.main.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl
        extends ServiceImpl<UserMapper, User> implements IUserService{

    @Autowired
    UserInfoMapper userInfoRepository;

    @Override
    public List<UserDto> userAll() {
        List<User> users = selectList(null);

        return null;
    }

    @Transactional
    @Override
    public Object register(RegisterDto dto) {
        User user = new User();
        String salt = "a";
        user.setPassword(Md5Util.md5Encode(dto.getPass(), salt));

        userInfoRepository.insert(null);
        return user;
    }
}
