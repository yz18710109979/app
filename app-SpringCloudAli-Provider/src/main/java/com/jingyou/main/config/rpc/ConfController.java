package com.jingyou.main.config.rpc;

import com.jingyou.main.core.common.exception.BizException;
import com.jingyou.main.core.model.ResultBody;
import com.jingyou.main.utils.JedisUtil;
import com.jingyou.main.config.Conf;
import com.jingyou.main.sys.dto.UserDto;
import com.jingyou.main.sys.service.IUserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RefreshScope
@RestController
public class ConfController {

    @Autowired
    private IUserService userService;

    @ApiOperation(value = "post请求调用示例", notes = "invokePost说明", httpMethod = "POST")
    @GetMapping(value = "/get")
    public ResultBody get() {
        String code = JedisUtil.getString("aaaa");
//        List<UserDto> users = userService.userAll();
        if (true) { throw  new BizException("-1","用户姓名不能为空！"); }
        Conf.getInstance().properties.getProperty("appMsg");

        return null;
    }
}
