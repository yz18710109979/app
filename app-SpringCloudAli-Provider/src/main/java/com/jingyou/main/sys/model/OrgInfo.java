package com.jingyou.main.sys.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tb_app_org_info")
public class OrgInfo extends Model<OrgInfo> {

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
    private Integer uid; /**账号ID**/
    private String avatar; /**头像**/
    private String name; /**真实姓名**/
    private String code; // 如果是业务员的编码
    private String amount; // 账户金额
    private Integer sex; /**性别**/
    private Integer age; /**年龄**/
    private String brithday; /** 生日**/
    private String face; /** 身份证正面照片 **/
    private String back; /** 身份证方面照片 **/
    private String address; /** 用户真实通讯地址（可根据身份证照片获取） **/
    private @TableId(value = "id", type = IdType.AUTO) Integer id;

}
