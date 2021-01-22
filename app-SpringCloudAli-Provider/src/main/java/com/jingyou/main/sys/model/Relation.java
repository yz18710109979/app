package com.jingyou.main.sys.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tb_app_relation")
public class Relation extends Model<Relation> {

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
    private Integer menuid;//菜单id
    private Integer roleid;//角色id
    private @TableId(value = "id", type = IdType.AUTO) Integer id;
}
