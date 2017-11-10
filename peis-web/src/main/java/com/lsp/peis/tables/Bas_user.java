package com.lsp.peis.tables;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/6 2:15
 */
public class Bas_user implements Serializable {
    private static final long serialVersionUID = -7868247090127964841L;

    private String nodecode;//
    private String id;//
    private String hosnum;//
    private String user_key;//
    private String password;//
    private String name;//
    private String idcard;//
    private String sex;//
    private Date birthdate;//
    private String phone;//
    private String mobile;//
    private String short_mobile;//
    private String email;//
    private String post;//
    private String post_code;//
    private Long index_no;//
    private Date reg_date;//
    private String stop_sign;//
    private String del_sign;//
    private String remark;//
    private String input_cpy;//
    private String input_cwb;//
    private String input_custom;//
    private String job_no;//
    private String person_dept;//人事关系
    private String businessNo; //异地交易流水号
    private String ehrUser_key;
    private String ehrPassword;
    private String ehrRole;
    private String posid;//终端id
    private String businessnum; 	//业务周期号
    private String console_sign; 	//是否使用分诊台 Y|N
    private String userlevel;  //人员级别


    //外部登陆接口使用
    private String deptcode;
    private String wardcode;

    public String getNodecode() {
        return nodecode;
    }

    public void setNodecode(String nodecode) {
        this.nodecode = nodecode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHosnum() {
        return hosnum;
    }

    public void setHosnum(String hosnum) {
        this.hosnum = hosnum;
    }

    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getShort_mobile() {
        return short_mobile;
    }

    public void setShort_mobile(String short_mobile) {
        this.short_mobile = short_mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPost_code() {
        return post_code;
    }

    public void setPost_code(String post_code) {
        this.post_code = post_code;
    }

    public Long getIndex_no() {
        return index_no;
    }

    public void setIndex_no(Long index_no) {
        this.index_no = index_no;
    }

    public Date getReg_date() {
        return reg_date;
    }

    public void setReg_date(Date reg_date) {
        this.reg_date = reg_date;
    }

    public String getStop_sign() {
        return stop_sign;
    }

    public void setStop_sign(String stop_sign) {
        this.stop_sign = stop_sign;
    }

    public String getDel_sign() {
        return del_sign;
    }

    public void setDel_sign(String del_sign) {
        this.del_sign = del_sign;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getInput_cpy() {
        return input_cpy;
    }

    public void setInput_cpy(String input_cpy) {
        this.input_cpy = input_cpy;
    }

    public String getInput_cwb() {
        return input_cwb;
    }

    public void setInput_cwb(String input_cwb) {
        this.input_cwb = input_cwb;
    }

    public String getInput_custom() {
        return input_custom;
    }

    public void setInput_custom(String input_custom) {
        this.input_custom = input_custom;
    }

    public String getJob_no() {
        return job_no;
    }

    public void setJob_no(String job_no) {
        this.job_no = job_no;
    }

    public String getPerson_dept() {
        return person_dept;
    }

    public void setPerson_dept(String person_dept) {
        this.person_dept = person_dept;
    }

    public String getBusinessNo() {
        return businessNo;
    }

    public void setBusinessNo(String businessNo) {
        this.businessNo = businessNo;
    }

    public String getEhrUser_key() {
        return ehrUser_key;
    }

    public void setEhrUser_key(String ehrUser_key) {
        this.ehrUser_key = ehrUser_key;
    }

    public String getEhrPassword() {
        return ehrPassword;
    }

    public void setEhrPassword(String ehrPassword) {
        this.ehrPassword = ehrPassword;
    }

    public String getEhrRole() {
        return ehrRole;
    }

    public void setEhrRole(String ehrRole) {
        this.ehrRole = ehrRole;
    }

    public String getPosid() {
        return posid;
    }

    public void setPosid(String posid) {
        this.posid = posid;
    }

    public String getBusinessnum() {
        return businessnum;
    }

    public void setBusinessnum(String businessnum) {
        this.businessnum = businessnum;
    }

    public String getConsole_sign() {
        return console_sign;
    }

    public void setConsole_sign(String console_sign) {
        this.console_sign = console_sign;
    }

    public String getUserlevel() {
        return userlevel;
    }

    public void setUserlevel(String userlevel) {
        this.userlevel = userlevel;
    }

    public String getDeptcode() {
        return deptcode;
    }

    public void setDeptcode(String deptcode) {
        this.deptcode = deptcode;
    }

    public String getWardcode() {
        return wardcode;
    }

    public void setWardcode(String wardcode) {
        this.wardcode = wardcode;
    }
}
