package com.lsp.his.tables;

import java.io.Serializable;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/8 22:41
 */
public class Bas_role implements Serializable {
    private static final long serialVersionUID = -6455888829191450954L;

    private String id;//主键
    private String code;//代码
    private String name;//名称
    private String remark;//备注
    private Long index_no;//排序号
    private String lvl;//级别
    private String sts;//状态

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getIndex_no() {
        return index_no;
    }

    public void setIndex_no(Long index_no) {
        this.index_no = index_no;
    }

    public String getLvl() {
        return lvl;
    }

    public void setLvl(String lvl) {
        this.lvl = lvl;
    }

    public String getSts() {
        return sts;
    }

    public void setSts(String sts) {
        this.sts = sts;
    }
}
