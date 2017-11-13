<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ include file="../include.jsp" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="Expires" content="0"/>
    <meta http-equiv="kiben" content="no-cache"/>
    <title>新建体检人员</title>
    <base href="<%=basePath%>"/>
    <link href="css/register.css" type="text/css" rel="stylesheet"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxcombo.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxcalendar.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxcalendar_dhx_skyblue.css"/>
    <style type="text/css">
        input {
            height: 16px;
        }

        select {
            height: 20px;
        }

        .textbk {
            border: 1px solid #93AFBA;
        }

        .not_editable {
            background-color: #EAEAEA;
        }

        .msg {
            font-size: 12px;
            color: red;
        }

        .mustwrt .dhx_combo_box {
            border-color: red;
            border: 1px solid red;
        }

        .test {
            border: 1px red solid
        }

        .dhx_combo_list {
            border: 1px solid #BAC2CD;
            height: 60px;
            font-family: 微软雅黑;
            font-size: 12px;
            scrollbar-face-color: #E3EBF8;
            scrollbar-shadow-color: #c6d8f0;
            scrollbar-highlight-color: #FFFFFF;
            scrollbar-3dlight-color: #E3EBF8;
            scrollbar-darkshadow-color: #d8e4f3;
            scrollbar-track-color: #FFFFFF;
            scrollbar-arrow-color: #9bb8de;
        }

        .dhx_combo_list div {
            padding: 0px;
            height: 20px;
        }

        .dhx_combo_box {
            height: 20px;
            border: 1px solid #93AFBA;
        }

        .dhx_combo_box input {
            height: 19px;
            line-height: 20px;
        }

        .jiej_1 {
            width: 1000px;
        }

        .jiej_3 {
            width: 470px;
            height: 22px;
        }

        .jiej_4 {
            width: 276px;
        }

        .jiej_5 {
            width: 258px;
            padding: 8px;
        }

        .jiej_6 {
            width: 711px;
            padding-left: 13px;
        }

        .jiej_8 {
            width: 140px;
            height: 18px;
            line-height: 18px;
        }

        .jiej_9 {
            width: 366px;
            height: 18px;
            line-height: 18px;
        }

        .jiej_10 {
            width: 710px;
            height: 44px;
            background: url(img/luru.jpg) no-repeat 0px 36px;
        }

        .jiej_11 {
            width: 688px;
            border: 1px solid #93afba;
            border-bottom: none;
            border-top: none;
            padding: 5px 10px;
        }

        .jiej_12 {
            width: 330px;
        }

        .jiej_13 {
            width: 328px;
            background-color: #f4fbfe;
            border-left: 1px solid #93afba;
            border-right: 1px solid #93afba;
        }

        .jiej_14 {
            padding-left: 45px;
            color: #44839a;
            font-weight: bold;
            position: absolute;
            z-index: 22;
            top: -3px;
            left: 0px;
        }

        .jiej_15 {
            height: 40px;
            line-height: 40px;
            border-bottom: 1px solid #d3d3d3;
            width: 300px;
        }

        .jiej_16 {
            height: 335px;
        }

        .jiej_17 {
            width: 328px;
            background-color: #f7f7f7;
            border-left: 1px solid #d3d3d3;
            border-right: 1px solid #d3d3d3;
        }

        .tijian_1 {
            width: 686px;
        }

        .tijian_2 {
            width: 360px;
            height: 18px;
            line-height: 18px;
        }

        .tijian_3 {
            width: 580px;
            height: 18px;
            line-height: 18px;
        }

        .tijian_4 {
            width: 330px;
        }

        .tijian_5 {
            width: 330px;
            height: 48px;
            list-style: none;
            background: url(img/tou6_1.gif) no-repeat;
        }

        .tijian_5 li {
            width: 104px;
            height: 48px;
            text-align: center;
            line-height: 48px;
            float: left;
            display: inline;
            color: #6ba3b6;
            font-weight: bold;
        }

        .tijian_hover {
            width: 104px;
            height: 48px;
            background: url(img/tijian_2.gif) no-repeat;
            display: block;
            font-size: 16px;
        }

        .tijian_6 {
            width: 256px;
            height: 39px;
            background: url(img/tijian1.gif) no-repeat;
        }

        .tijian_7 {
            width: 140px;
            height: 15px;
            line-height: 18px;
        }

        .tijian_8 {
            width: 256px;
            background-color: #f4fbfe;
            list-style: none;
        }

        .tijian_8 li {
            padding: 5px 0px;
            border-bottom: 1px solid #99b4be;
            height: 50px;
        }

        .tijian_9 {
            color: #5c88b5;
        }

        .tijian_10 {
            height: 568px;
        }

        .tijian_11 {
            background-color: #fffede;
        }

        .autohe {
            height: 400px;
        }

        .text_field3 {
            border: 1px solid #93AFBA;
            line-height: 17px;
            width: 140px;
        }

        .text_field4 {
            border: 1px solid #93AFBA;
            line-height: 17px;
            width: 360px;
        }

        .text_field5 {
            border: 1px solid #93AFBA;
            line-height: 17px;
            width: 580px;
        }

        .text_field6 {
            border: 1px solid #93AFBA;
            line-height: 17px;
            width: 180px;
        }

        .text_field7 {
            border: 1px solid #93AFBA;
            line-height: 17px;
        }

        .cen1 {
            position: relative;
            z-index: 1px;
        }

        .cen2 {
            width: 191px;
            position: absolute;
            z-index: 100px;
            margin-top: -9px;
        }

        .cen3 {
            padding-left: 20px;
        }

        .exm_1 {
            width: 98%;
            margin: auto;
            margin-top: 5px
        }

        .exm_l3 {
            border-left: 1px solid #93afba;
            border-right: 1px solid #93afba;
        }

        .exm_bg {
            background: url(img/exm2.gif) repeat-x;
        }

        .span01 {
            font-family: "微软雅黑";
            font-size: 13px;
            font-weight: bold;
            color: #44839a;
            line-height: 24px;
            background: #fff;
            padding: 0px 5px 0px 5px;
        }
    </style>
    <script>window.dhx_globalImgPath = "dhtmlxCombo/codebase/imgs/";</script>
    <script type="text/javascript" src="js/clc/comboTool.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcommon.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcombo.js"></script>
    <script type="text/javascript" src="js/jquery-1.6.1.js"></script>
    <script type="text/javascript" src="js/jquery.blockUI.js"></script>
    <script type="text/javascript" src="js/gl/util.js"></script>
    <script type="text/javascript" src="js/phyexam/personInfoEntry.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/ext/dhtmlxcombo_whp.js"></script>
    <script type="text/javascript" src="js/dhtmlxcalendar.js"></script>
    <script type="text/javascript">
        ${listinfo}//获取体检名称及体检类型
        ${listbase}//获取体检人信息
    </script>
    <OBJECT classid="clsid:F1317711-6BDE-4658-ABAA-39E31D3704D3" codebase="SDRdCard.cab#version=1,3,5,0" width=330
            height=360 align=center display=none hspace=0 vspace=0 id=idcard name=rdcard></OBJECT>
</head>
<body>
<input id="Province_R1" title="常住省" type="hidden"/>
<input id="City_R1" title="常住市" type="hidden"/>
<input id="County_R1" title="常住县" type="hidden"/>
<input id="township_R1" title="常住街道" type="hidden"/>
<input id="unit_R1" type="hidden" value="${unit}"/>
<input id="examtype_R1" type="hidden" value="${examtype}"/>
<input id="examid_R1" type="hidden" value="${examid}"/>
<input id="str" type="hidden" value="${str}"/>
<div class="exm_1">
    <table width="675" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td width="34"><img src="img/tp1.jpg"/></td>
            <td style="background:url(img/tp2.jpg) repeat-x 0px 4px;"><span class="span01">基本信息</span></td>
            <td width="4"><img src="img/tp3.jpg"/></td>
        </tr>
    </table>
    <div class="exm_l3" style="margin-top:-5px;width:674px;">
        <table width="650" border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td width="70" height="30px">编&emsp;&emsp;号：</td>
                <td><input type="text" id="pexamid" class="txt ro textbk not_editable" value="${pexamid}" readonly
                           style="width:125px;"/></td>
                <td width="70">体检名称：</td>
                <td>
                    <div id="unit" style="width:125px;float:left" class="mustwrt"></div>
                </td>
                <td width="70">体检类别：</td>
                <td>
                    <div id="examType" style="width:125px;float: left;" class="mustwrt"></div>
                </td>
            </tr>
            <tr>
                <td height="30px">姓&emsp;&emsp;名：</td>
                <td><input type="text" name="patname" id="patname" style="width:125px;border:1px solid red"
                           class="textbk test"/></td>
                <td>证件类别：</td>
                <td>
                    <div id="cardType" style="width:125px;float:left;" class="mustwrt"></div>
                    <script>
                        combo_idtype = new dhtmlXCombo("cardType", "alfa3", 127);//卡类型
                    </script>
                </td>
                <td>证&ensp;件&ensp;号：</td>
                <td><input type="text" name="idnum" id="idnum" style="width:125px;" class="textbk "
                           onBlur="idnumcheck()"/></td>
            </tr>

            <tr>
                <td height="30px">民&emsp;&emsp;族：</td>
                <td>
                    <div id="minzu" style="width:125px;float: left;"></div>
                    <script>
                        combo_minzu = new dhtmlXCombo("minzu", "minzu", 127);
                    </script>
                </td>
                <td>国&emsp;&emsp;籍：</td>
                <td>
                    <div id="guoji" style="width:125px;float: left;"></div>
                    <script>
                        combo_guoji = new dhtmlXCombo("guoji", "guoji", 127);
                    </script>
                </td>
                <td>文化程度：</td>
                <td>
                    <div id="whcd" style="width:125px;float: left;"></div>
                    <script>
                        combo_whcd = new dhtmlXCombo("whcd", "whcd", 127);
                    </script>
                </td>
            </tr>
            <tr>
                <td height="30px">宗教信仰：</td>
                <td>
                    <div id="zjxy" style="width:125px;float: left;" class="mustwrt"></div>
                    <script>
                        combo_zjxy = new dhtmlXCombo("zjxy", "zjxy", 127);
                    </script>
                </td>
                <td>电脑工作：</td>
                <td>
                    <input type="text" name="wordincomputer" id="wordincomputer" style="width:125px;border:1px solid "
                           class="textbk"/>
                </td>
                <td>睡&emsp;&emsp;眠：</td>
                <td><input type="text" name="shuimian" id="shuimian" style="width:125px;border:1px solid"
                           class="textbk "/></td>
            </tr>
            <tr>
                <td height="30px">夜&emsp;&emsp;尿：</td>
                <td><input type="text" name="yeniao" id="yeniao" style="width:125px;border:1px solid" class="textbk "/>
                </td>
                <td>饮食习惯：</td>
                <td>
                    <input type="text" name="tsys" id="tsys" style="width:125px;border:1px solid " class="textbk "/>
                </td>
                <td>口味咸淡：</td>
                <td>
                    <div id="kwxd" style="width:125px;float: left;"></div>
                    <script>
                        combo_kwxd = new dhtmlXCombo("kwxd", "kwxd", 127);
                        combo_kwxd.readonly(true);
                        combo_kwxd.addOption('一般', '一般');
                        combo_kwxd.addOption('偏淡', '偏淡');
                        combo_kwxd.addOption('偏咸', '偏咸');
                        combo_kwxd.addOption('偏辣', '偏辣');
                    </script>
                </td>
            </tr>
            <tr>
                <td height="30px">出生日期：</td>
                <td><input type="text" name="dateofbirth" id="dateofbirth" style="width:125px;border:1px solid"
                           class="textbk"/></td>
                <td>性&emsp;&emsp;别：</td>
                <td>
                    <div id="sex" style="width:125px;float:left" class="mustwrt"></div>
                    <script>
                        var combo_sex = new dhtmlXCombo("sex", "alfa3", 127);
                    </script>
                </td>
                <td>病人编号：</td>
                <td>
                    <input type="text" name="patientid" id="patientid" style="width:125px;" class="textbk"
                           disabled="disabled"/>
                </td>
                <td style="display: none;">农&ensp;保&ensp;号：</td>
                <td style="display: none;"><input type="text" name="inscardno" id="inscardno" style="width:125px;"
                                                  class="textbk"/></td>
            </tr>
            <tr>
                <td>联系电话：</td>
                <td><input type="text" name="phoneNum" id="phoneNum" style="width:125px;" class="textbk test"/></td>
                <td>婚姻状况：</td>
                <td>
                    <div id="maritalStatus" style="width:125px;float:left"></div>
                    <script>
                        var combo_maritalstatus = new dhtmlXCombo("maritalStatus", "alfa3", 127);
                    </script>
                </td>
                <td>职&emsp;&emsp;业：</td>
                <td>
                    <div id="professional" style="width:127px;float:left"></div>
                    <script>
                        var combo_professional = new dhtmlXCombo("professional", "alfa3", 127);
                    </script>
                </td>
            </tr>
            <tr>
                <td>
                    家庭住址：
                </td>
                <td colspan="3" style="height:30px">
                    <input id="homeaddress" style="width:94%;border:1px solid" class="textbk"/>
                </td>
                <td>折&ensp;扣&ensp;率：</td>
                <td>
                    <input type="text" name="zkl" id="zkl" style="width:125px;border:1px solid red" class="textbk"
                           onblur="checkzkl111()" value="0"
                    />
                </td>
            </tr>
            <tr>
                <td>
                    工作单位：
                </td>
                <td colspan="3" style="height:30px">
                    <input id="wordaddress" style="width:94%;border:1px solid" class="textbk"/>
                </td>
                <td>预留字段</td>
                <td>
                    <input type="text" name="ylzd" id="ylzd" style="width:125px;" class="textbk"
                    />
                </td>
            </tr>
            <tr>
                <td height="30px">省&emsp;&emsp;份：</td>
                <td>
                    <div name="Province_R" id="Province_R" style="width:92px;margin-left:0px;"></div>
                </td>
                <td>市&emsp;&emsp;级：</td>
                <td>
                    <div name="City_R" id="City_R" style="width:92px;margin-left:0px;"></div>
                </td>
                <td>&emsp;&emsp;&emsp;县：</td>
                <td>
                    <div name="County_R" id="County_R" style="width:92px;margin-left:0px;"></div>
                </td>
            </tr>
            <tr>
                <td height="30px">乡（镇）：</td>
                <td>
                    <div name="township_R" id="township_R" style="width:92px;margin-left:0px;"></div>
                </td>
                <td>&emsp;&emsp;&emsp;村：</td>
                <td>
                    <div name="village_R" id="village_R" style="width:92px;margin-left:0px;"></div>
                </td>
                <td>路&emsp;&emsp;牌：</td>
                <td><input type="text" name="add_R" id="add_R" style="width:125px" class="textbk"/></td>
            </tr>
            <script>
                combo_Province_R = new dhtmlXCombo("Province_R", "Province_Rs", 127);
                combo_City_R = new dhtmlXCombo("City_R", "City_Rs", 127);
                combo_County_R = new dhtmlXCombo("County_R", "County_Rs", 127);
                combo_township_R = new dhtmlXCombo("township_R", "township_Rs", 127);
                combo_village_R = new dhtmlXCombo("village_R", "village_Rs", 127);
            </script>

        </table>
    </div>
    <table width="675" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td width="4" valign="top"><img src="img/bt1.jpg"/></td>
            <td class="bgr" width="100%"></td>
            <td width="4" valign="top"><img src="img/bt3.jpg"/></td>
        </tr>
    </table>
</div>
<table width="688" border="0" cellspacing="0" cellpadding="8" class="mar3">
    <tr>
        <td width="330" valign="top">
            <div class="jiej_12" id="rleftd">
                <div><img src="img/tou2.jpg"/></div>
                <div class="jiej_17" id="examitems0">
                    <div class="jiej_15">
                        <table border="0" width="300" cellspacing="0" cellpadding="0">
                            <tr>
                                <td>共体检项目<span class="font2" id="exnum">0</span>项</td>
                                <td align="right">合计：<span class="font2" id="totalprice">0.00</span>元</td>
                            </tr>
                        </table>
                    </div>
                    <div style="height:200px;OVERFLOW-y:auto;OVERFLOW-x:hidden;" id="ditem0">
                        <table width="300" border="0" cellspacing="0" cellpadding="0" id="examitems">
                            <tbody id="texamitems">
                            </tbody>
                        </table>
                    </div>
                </div>
                <div><img src="img/tou3.jpg"/></div>
            </div>
        </td>
        <td>
            <div class="tijian_4" id="drightr">
                <ul class="tijian_5">
                    <li class="tijian_hover" onclick="changetab()" id="tcxm" style="cursor: pointer;">套餐项目</li>
                    <li onclick="changetab2()" id="dgxm" style="cursor: pointer;">单个项目</li>
                    <li>
                        <div name="searchItems" id="searchItems" style="width:100px;margin-top: 15px;"></div>
                        <script>
                            var searchItems = new dhtmlXCombo("searchItems", "searchItems", 115);
                            searchItems.setOptionHeight(120);
                        </script>
                    </li>
                </ul>
                <div id="groupitems">
                    <div class="jiej_13" style="padding-top: 5px;height:200px;OVERFLOW-y:auto;OVERFLOW-x:hidden;"
                         id="dsigle0">
                        <table width="300" border="0" cellspacing="0" cellpadding="0">
                            <c:forEach items="${listgroup}" var="group" varStatus="i">
                                <tr onclick="addgroup2('${group.groupid}','${group.groupname}','${group.cost}')"
                                    style="cursor: pointer;">
                                    <td width="20" height="28"><img src="img/jiej5.jpg"/></td>
                                    <td width="20">(${i.index+1})</td>
                                    <td>${group.groupname}</td>
                                    <td>${group.cost}元</td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                </div>
                <div id="sigelitems" style="display:none;">
                    <div style="padding-top: 5px;height:200px;OVERFLOW-y:auto;OVERFLOW-x:hidden;" id="dsigle"
                         class="jiej_13">
                        <table width="300" border="0" cellspacing="0" cellpadding="0">
                            <c:forEach items="${listitems}" var="eitems" varStatus="i">
                                <tr onclick="additems2('${eitems.comid}','${eitems.comname}')" style="cursor: pointer;">
                                    <td width="20" height="28"><img src="img/jiej5.jpg"/></td>
                                    <td width="20">(${i.index+1})</td>
                                    <td>${eitems.comname}</td>
                                    <td>${eitems.cost}元</td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                </div>
                <div><img src="img/tou4.jpg"/></div>
            </div>
        </td>
    </tr>
</table>
<table width="100%" border="0" cellspacing="0" cellpadding="0" style="padding-bottom:10px;">
    <tr>
        <td>
            <div class="exm_bot ">
                <ul class="ant" style="width:300px;padding-right:10px">
                    <!--
                    <li style="cursor: pointer;" >读卡</li>
                    <li style="cursor: pointer;" onclick="readCardInfo()" id="cardRead">读卡</li>
                     -->

                    <li style="cursor: pointer;" onclick="savepatient()" id="save">保存</li>
                    <!--   <li style="cursor: pointer;" onclick="continueEnter()" id="continueEnter">继续录入</li> -->
                    <!-- <li style="cursor: pointer;" onclick="clearScreen()">清屏</li>   -->
                    <li style="cursor: pointer;" onclick="javascript:doclose();">关闭</li>
                </ul>
            </div>
        </td>
    </tr>
</table>

</body>
</html>
