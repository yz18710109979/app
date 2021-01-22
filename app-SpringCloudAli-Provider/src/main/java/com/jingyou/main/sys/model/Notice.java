package com.jingyou.main.sys.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

@Data
public class Notice extends Model<Notice> {

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
    private @TableId(value = "id", type = IdType.AUTO) Integer id;

}
