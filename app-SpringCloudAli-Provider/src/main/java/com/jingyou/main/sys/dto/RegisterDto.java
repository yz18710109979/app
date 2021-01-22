package com.jingyou.main.sys.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class RegisterDto {
    private Integer type;
    private @NotEmpty String name;
    private @ApiModelProperty(value = "证件号码") String cardNo;
    private @ApiModelProperty(value = "手机号码") String mobile;
    private @ApiModelProperty(value = "邀请码") String invitationCode;
    private @ApiModelProperty(value = "手机验证码") String code;
    private @ApiModelProperty(value = "密码") String pass;
    private @ApiModelProperty(value = "重复密码") String checkPass;
    private @ApiModelProperty(value = "微信开放平台Id") String wxPcOpenid;

}
