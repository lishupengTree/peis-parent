package com.lsp.his.tables;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/9 13:02
 */
public class Bas_parms {

    private String parmid;
    private String hosnum;
    private String scope;
    private String parmname;
    private String parmvalue;
    private Integer canedit;
    private String comments;
    private String sysname;
    private String descriptions;
    private String defaultparms;
    private String nodecode;


    public String getSysname() {
        return sysname;
    }
    public void setSysname(String sysname) {
        this.sysname = sysname;
    }
    public String getParmid() {
        return parmid;
    }
    public void setParmid(String parmid) {
        this.parmid = parmid;
    }
    public String getHosnum() {
        return hosnum;
    }
    public void setHosnum(String hosnum) {
        this.hosnum = hosnum;
    }
    public String getScope() {
        return scope;
    }
    public void setScope(String scope) {
        this.scope = scope;
    }
    public String getParmname() {
        return parmname;
    }
    public void setParmname(String parmname) {
        this.parmname = parmname;
    }
    public String getParmvalue() {
        return parmvalue;
    }
    public void setParmvalue(String parmvalue) {
        this.parmvalue = parmvalue;
    }
    public Integer getCanedit() {
        return canedit;
    }
    public void setCanedit(Integer canedit) {
        this.canedit = canedit;
    }
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public String getDescriptions() {
        return descriptions;
    }
    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }
    public void setDefaultparms(String defaultparms) {
        this.defaultparms = defaultparms;
    }
    public String getDefaultparms() {
        return defaultparms;
    }
    public void setNodecode(String nodecode) {
        this.nodecode = nodecode;
    }
    public String getNodecode() {
        return nodecode;
    }

}
