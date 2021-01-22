package com.jingyou.main.sys.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tb_app_role")
public class Role extends Model<Role> {
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
    private Integer num;
    private Integer pid;//父角色id
    private String name;//角色名称
    private @TableId(value = "id", type = IdType.AUTO) Integer id;
}
