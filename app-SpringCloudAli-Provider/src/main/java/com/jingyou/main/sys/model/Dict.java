package com.jingyou.main.sys.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;


public class Dict extends Model<Dict> {

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    private @TableId(value = "id", type = IdType.AUTO) Integer id;
}
