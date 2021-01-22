package com.jingyou.main.sys.controller;

import com.jingyou.main.core.constants.GlobalConstant;
import com.jingyou.main.sys.dto.LoginDto;
import com.jingyou.main.sys.dto.RegisterDto;
import com.jingyou.main.sys.service.IUserService;
import com.jingyou.main.utils.JedisUtil;
import com.jingyou.main.utils.CustomPrinter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;

@RestController
public class LoginController {
    @Autowired IUserService userService;

    private static final ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);
    private static final RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();
    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(9, 9, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);

    //TODO 注册相关
    /** 个人注册 **/
    @PostMapping(value = "/adopt/personalSignUp")
    public Object personalSignUp(@RequestBody RegisterDto dto, BindingResult result) {
        return register(dto, 1);
    }

    private Object register(RegisterDto dto, Integer type) {
        Map<String,String> printerMap = new LinkedHashMap<>();
        String redisKey = GlobalConstant.SMS_LOGIN_PREFIX + dto.getMobile();
        String code = JedisUtil.getString(redisKey);
        printerMap.put(CustomPrinter.CREATE_PART_COPPER + 0, "注册打印");
        if (StringUtils.isEmpty(code)
                || ! dto.getCode().equals(code)) {
        }
        CustomPrinter.printInfo("注册", printerMap);
        return userService.register(dto);
    }

    /** 企业注册 **/
    @PostMapping(value = "/adopt/companySignUp")
    public Object companySignUp(@RequestBody RegisterDto dto, BindingResult result) {
        return register(dto, 2);
    }

    //TODO 登录相关
    /** 手机验证码登录 */
    @PostMapping(value = "/adopt/phoneLoginUp")
    public Object personalLoginUp(@RequestBody LoginDto dto) {
        return login(dto, 1);
    }

    /** 账号密码登录 */
    @PostMapping(value = "/adopt/accountLoginUp")
    public Object accountLoginUp(@RequestBody LoginDto dto) {
        return login(dto, 2);
    }

    private Object login(LoginDto dto, int loginType) {

        return null;
    }

    /**
     * 发送短信验证码
     * @param mobile
     * @param type
     * @return
     */
    @GetMapping(value = "/adopt/sendCode")
    public Object sendCode(@RequestParam("mobile") String mobile,
            @RequestParam("type") String type) {
        return null;
    }

    /**
     * 短信验证码校验
     * @param requestMap
     * @return
     */
    @PostMapping(value = "/adopt/checkCode")
    public Object checkCode(@RequestBody Map<String,Object> requestMap) {
        return null;
    }
}
