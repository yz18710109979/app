package com.jingyou.main.sys.vo;

import com.jingyou.main.sys.model.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UserVO {
    private String account;
    private Integer type;

    /** 属性拷贝**/
    public static UserVO copy(User user, Object o) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}
