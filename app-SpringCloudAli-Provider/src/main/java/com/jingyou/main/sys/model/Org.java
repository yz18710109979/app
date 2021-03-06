package com.jingyou.main.sys.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

@Data
public class Org extends Model<Org>{

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
    private String name;

    private @TableId(value = "id", type = IdType.AUTO) Integer id;
}
