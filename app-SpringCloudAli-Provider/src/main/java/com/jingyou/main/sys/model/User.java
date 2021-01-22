package com.jingyou.main.sys.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "tb_app_user")
public class User extends Model<User>{
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
    private Long midify; // 最近更新时间
    private String account; /**账号**/
    private String password;/**密码**/
    private Integer type;/**状态**/
    private String salt;/** 密码加盐**/
    private String email;/**电子邮箱**/
    private Integer source;/**数据来源 **/
    private @TableField("card_type") Integer cardType;
    private String card; /** 证件号码**/
    private String mobile; /**手机号码**/
    private String roleid;/** 角色ids**/
    private Integer deptid; // 所属保险公司id
    private String openid; // 微信小程序开放平台id
    private String wxopenid;// 微信PC端开发平台id 用于授权微信PC端扫码登录
    private @TableId(value = "id", type = IdType.AUTO) Integer id;/** 用户主键 **/
}
