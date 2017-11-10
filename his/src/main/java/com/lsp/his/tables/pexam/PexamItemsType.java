package com.lsp.his.tables.pexam;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/9 12:53
 */
public class PexamItemsType {

    private String hosnum;
    private String typeid;
    private String typename;
    private long sn;
    private String descriptions;
    private String comments;
    public String getHosnum() {
        return hosnum;
    }
    public void setHosnum(String hosnum) {
        this.hosnum = hosnum;
    }
    public long getSn() {
        return sn;
    }
    public void setSn(long sn) {
        this.sn = sn;
    }
    public String getDescriptions() {
        return descriptions;
    }
    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public String getTypeid() {
        return typeid;
    }
    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }
    public String getTypename() {
        return typename;
    }
    public void setTypename(String typename) {
        this.typename = typename;
    }



}
