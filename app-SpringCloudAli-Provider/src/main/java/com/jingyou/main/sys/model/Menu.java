package com.jingyou.main.sys.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "tb_app_menu")
public class Menu extends Model<Menu> {
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
    private String code;
    private String pcode;/**  菜单父编号 */
    private String name;/** 菜单名称 */
    private String url; /** url地址 */
    private Integer num;/** 菜单排序号 */
    private Integer levels;/** 菜单层级 */
    private String pcodes;/** 当前菜单的所有父菜单编号 */
    private Integer ismenu;/** 是否是菜单（1：是  0：不是） */
    private Integer status; /** 菜单状态 :  1:不启用   0:启用 */
    private String icon;/** 菜单图标 */
    private @TableId(value = "id", type = IdType.AUTO) Integer id;

}
