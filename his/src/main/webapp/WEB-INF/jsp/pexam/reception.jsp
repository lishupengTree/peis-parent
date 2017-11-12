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
    <title>接检</title>
    <base href="<%=basePath%>"/>
    <link href="css/register.css" type="text/css" rel="stylesheet"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid_dhx_custom.css"/>
    <link rel="stylesheet" type="text/css" href="js/jquery.pagination/pagination.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxcombo.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxcalendar.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxcalendar_dhx_skyblue.css"/>
    <link href="css/register.css" type="text/css" rel="stylesheet"/>
    <script>window.dhx_globalImgPath = "dhtmlxCombo/codebase/imgs/";</script>
    <script type="text/javascript" src="js/dhtmlxcommon.js"></script>
    <script type="text/javascript" src="js/dhtmlxgrid.js"></script>
    <script type="text/javascript" src="js/dhtmlxgridcell.js"></script>
    <script type="text/javascript" src="js/jquery-1.6.1.js"></script>
    <script type="text/javascript" src="js/jquery.blockUI.js"></script>
    <script type="text/javascript" src="js/jquery.lrTool.js"></script>
    <script type="text/javascript" src="js/pexam/reception.js"></script>
    <script type="text/javascript" src="js/gl/util.js"></script>
    <script type="text/javascript" src="js/window.js"></script>
    <script type="text/javascript" src="js/jquery.pagination/jquery.pagination.js"></script>
    <script type="text/javascript" src="js/pexam/examSheetPrint.js"></script>
    <script type="text/javascript" src="js/yhybReader.js"></script>
    <script type="text/javascript" src="js/clc/comboTool.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcommon.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcombo.js"></script>
    <script type="text/javascript" src="js/dhtmlxcalendar.js"></script>


    <style type="text/css">
        .jiej_11 .objbox {
            overflow: hidden;
        }

        .gjss2 {
            position: absolute;
            z-index: 10000;
            background-color: #f4fbfe;
            width: 580px;
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

        .tijian_1 {
            width: 686px;
        }

        .tijian_2 {
            width: 360px;
            height: 18px;
            line-height: 18px;
            border: 1px solid #93AFBA;
        }

        .tijian_3 {
            width: 580px;
            height: 18px;
            line-height: 18px;
            border: 1px solid #93AFBA;
        }

        .tijian_4 {
            width: 330px;
            height: 336px;
            background: url(img/tijian_3.gif) no-repeat;
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
            width: 190px;
            height: 18px;
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

        .jiej_1 {
            width: 1000px;
        }

        .jiej_2 {
            width: 490px;
            height: 32px;
            background: url(img/jiejian1.jpg) no-repeat;
        }

        .jiej_3 {
            width: 290px;
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

        .jiej_7 {
            width: 689px;
            padding: 5px 10px;
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
            min-height: 320px;
        }

        .jiej_12 {
            width: 330px;
            min-height: 300px;
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
            min-height: 270px;
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

        select {
            margin: -1px;
        }

        #contain {
            width: 200px;
            height: 18px;
        }

        #t_selected {
            background-position: 100% 50% !important;
            background-repeat: no-repeat !important;
            font-size: 12px;
            border: #DFDFDF 1px solid;
        }

        #selectList {
            border: #DFDFDF 1px solid;
            font-size: 12px;
            width: 200px;
            text-align: left;
            display: none;
        }

        #selectList span {
            width: 200px
        }

        .weer_r {
            color: #44839a;
            font-weight: bold;
        }

        .btn2 {
            cursor: pointer;
            background: url(img/btn.jpg) no-repeat;
            width: 92px;
            height: 32px;
            text-align: center;
            border: 0;
            line-height: 32px;
            border: 0;
            font-size: 14px;
            font-family: Microsoft YaHei, Lucida Grande, Helvetica, Tahoma, Arial,
            sans-serif;
            margin-left: 8px;
            color: #000;
            padding: 0
        }
    </style>
    <script>
        ENTER = "if(node.id=='search_value'){topSearch();}";


    </script>
    <OBJECT classid="clsid:F1317711-6BDE-4658-ABAA-39E31D3704D3" codebase="SDRdCard.cab#version=1,3,5,0" width=330
            height=360
            align=center display=none hspace=0 vspace=0 id=idcard name=rdcard></OBJECT>
</head>

<body>
<input type="hidden" id="village"/>
<input type="hidden" id="age"/>
<input type="hidden" id="codePath"/>
<input type="hidden" id="hosnum" value="${hosnum}"/>
<input type="hidden" id="basePath" value="<%=basePath%>"/>
<input type="hidden" value="${isDishDept}" id="isDishDept"/>
<!-- 是否区分科室“Y”是 -->
<input type="hidden" value="${hosname}" id="hosname"/>
<!-- 医院名字 -->
<input type="hidden" value="${isPrintA5}" id="isPrintA5"/>
<!-- 是否打印A5 -->
<input type="hidden" value="${isPrintCervical}" id="isPrintCervical"/>
<!-- 退休是否打印宫颈刮片 -->
<input type="hidden" value="${isPrintDGT}" id="isPrintDGT"/>
<!-- 是否打印登记表 -->
<input type="hidden" value="${isPrintBarcode}" id="isPrintBarcode"/>
<!-- 是否打印检验条码 -->
<input type="hidden" value="${isPrintSelfInfo}" id="isPrintSelfInfo"/>
<!-- 是否打印个人信息条码 -->
<div class="top">
    <jsp:include page="../top.jsp"/>
</div>
<div class="jiej_1" style="margin-top: 10px;">
    <table width="1000" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td width="390">
                <div class="jiej_2">
                    <table width="470px" height="32" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td align="right" width="80">
                                体检名称：
                            </td>
                            <td id="tjxm">
                                <input name="findtime" type="text" class="tijian_7 text_field7" style="width: 80px;"
                                       id="findtime"/>
                                <input name="enddate" type="text" class="tijian_7 text_field7" style="width: 80px;"
                                       id="enddate"/>
                                <select id="select" class="jiej_3 text_field7" style="width: 190px;"
                                        onchange="if(this.options[this.selectedIndex].value!='default'){loadgrid(this.options[this.selectedIndex].value);$('#newcreat').removeAttr('disabled');}else{selectexamid='default';$('#newcreat').attr({disabled: true });};if(this.options[this.selectedIndex].value!='default' && this.options[this.selectedIndex].value!='0000'){$('#startCard').removeAttr('disabled');$('#saveupdate').attr({disabled: false });}else{$('#startCard').attr({disabled: true });}">
                                    <!--
											<option value="default">--------请选择体检项目---------</option>
											<c:forEach items="${listmain}" var="Pexam_main" varStatus="i">
												<option value="${Pexam_main.examid}">${Pexam_main.examname}</option>
											</c:forEach>
											 -->
                                </select>
                            </td>
                        </tr>
                    </table>
                </div>
            </td>
            <td>
                &nbsp;
            </td>
            <td width="630" align="right">
                <!--
                <button id="startCard" type="button" class="btn2" onclick="getCard()" disabled>
                    刷&emsp;卡
                </button>

                <button id="newcreat" type="button" class="btn2" onclick="newcreat()" disabled>
                    新&emsp;建
                </button>
                -->
                <button id="saveupdate" type="button" class="btn2" onclick="saveupdate()" disabled>
                    修改/保存
                </button>
                <button id="startExamButton" type="button" class="btn2" onclick="startExam2()" disabled>
                    开始体检
                </button>
                <button id="backoutExamButton" type="button" class="btn2" onclick="backoutExam()" disabled>
                    撤销体检
                </button>
                <button id="AgainButton" type="button" onclick="doAgainPrint()" class="btn2" disabled>
                    重打登记表
                </button>
                <button id="AgainCode" type="button" onclick="rebarcode()" class="btn2" disabled>
                    重打条码
                </button>

                <!--
                <button id="sbbq" type="button" onclick="doSbbq()" class="btn2" disabled>
                    社保补签
                </button>

                <button id="BackButton" type="button" onclick="doBack()" class="btn2" disabled>
                    回&emsp;滚
                </button>

                <button id="finishExamButton" type="button" class="btn2" disabled>
                        完成体检
                </button>
                -->
            </td>
        </tr>
    </table>
    <!-- 动态表格 checkbox -->
    <div style="margin-left: 410px; position: absolute; z-index: 2">
        <table id="choose" border="1" cellspacing="0" cellpadding="0"
               class="gjss2" style="display: none;">
            <tr>
                <td>
                    <input type="checkbox" name="judge" id="judge" onclick="selectAll()"/>
                    全选
                    <div id="items">
                        <!-- 动态注入表格内容 -->
                    </div>
                    <input class="btn01" type="button" value="开始打印" onclick="doACodePrint()"/>
                </td>
            </tr>
        </table>
    </div>
    <!--  动态生成表格-->
    <div>
        <div class="floatl jiej_4">
            <div class="mar3">
                <table width="276" height="15" border="0" cellspacing="0"
                       cellpadding="0">
                    <tr>
                        <td width="34"><img src="img/tp1.jpg"/></td>
                        <td width="238" class="bgr1"><span class="font3">基本信息</span></td>
                        <td width="4"><img src="img/tp3.jpg"/></td>
                    </tr>
                </table>
                <div class="bord jiej_5">
                    <table width="260" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td height="28" width="80">
                                预约时间：
                            </td>
                            <td class="font4" id="bookdate"></td>
                        </tr>
                        <tr>
                            <td height="28">
                                体检人数：
                            </td>
                            <td>
                                <span class="weer_r" id="tjrs"></span><span class="weer_f1"><strong
                                    id="zjrs"></strong>
										</span><span class="weer_f"><strong id="wjrs"></strong>
										</span>
                            </td>
                        </tr>
                        <tr>
                            <td height="28">
                                体检单位：
                            </td>
                            <td class="font4" id="unitname"></td>
                        </tr>
                        <tr>
                            <td height="28">
                                体检类别：
                            </td>
                            <td class="font4" id="examtype"></td>
                        </tr>
                    </table>
                </div>
                <table width="276" height="4" border="0" cellspacing="0"
                       cellpadding="0">
                    <tr>
                        <td width="4"><img src="img/bt1.jpg"/></td>
                        <td width="268" class="bgr"></td>
                        <td width="4"><img src="img/bt3.jpg"/></td>
                    </tr>
                </table>
            </div>
            <div class="mar3">
                <table width="276" height="15" border="0" cellspacing="0"
                       cellpadding="0">
                    <tr>
                        <td width="34"><img src="img/tp1.jpg"/></td>
                        <td width="238" class="bgr1"><span class="font3">体检名单</span></td>
                        <td width="4"><img src="img/tp3.jpg"/></td>
                    </tr>
                </table>
                <div class="bord jiej_5 jiej_16" id="dleft">
                    <div id="grid_patient_recption"
                         style="width: 100%; height: 340px;"></div>
                    <div id="pagination"
                         style="position: relative; top: 7px; left: 0px; float: right; display: block; margin-top: -25px"></div>
                </div>
                <table width="276" height="4" border="0" cellspacing="0"
                       cellpadding="0" style="clear: both;">
                    <tr>
                        <td width="4"><img src="img/bt1.jpg"/></td>
                        <td width="268" class="bgr"></td>
                        <td width="4"><img src="img/bt3.jpg"/></td>
                    </tr>
                </table>
            </div>
        </div>
        <div class="floatl jiej_6">
            <div class="mar3">
                <table width="711" height="15" border="0" cellspacing="0"
                       cellpadding="0">
                    <tr>
                        <td width="34"><img src="img/tp1.jpg"/></td>
                        <td width="673" class="bgr1"><span class="font3">体检名单</span><span class="font3" style=""
                                                                                          id="sstarttime_">开始时间：</span>
                        </td>
                        <td width="4"><img src="img/tp3.jpg"/></td>
                    </tr>
                </table>
                <div class="bord jiej_7">
                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td width="70" height="29">
                                &nbsp;姓&emsp;&emsp;名：
                            </td>
                            <td width="156">
                                <input name="input12" type="text" class="jiej_8  text_field3"
                                       id="patname" onclick="tishi()" readonly/>
                            </td>
                            <td width="70">
                                &nbsp;身份证号：
                            </td>
                            <td width="156">
                                <input name="input3" type="text" class="jiej_8 text_field3 "
                                       id="idnum" onclick="tishi()" readonly/>
                            </td>
                            <td width="80">
                                出生日期&emsp;：
                            </td>
                            <td>
                                <input name="input7" type="text" class="jiej_8 text_field3"
                                       id="dateofbirth" readonly/>
                            </td>
                        </tr>
                        <tr>
                            <td width="70" height="29">
                                &nbsp;性&emsp;&emsp;别：
                            </td>
                            <td width="156">
                                <input name="input3" type="text" class="jiej_8 text_field3 "
                                       id="sex" readonly/>
                            </td>
                            <!--
                            <td>医疗卡号：</td>
                            <td><input name="input4" type="text" class="jiej_8 text_field3" id ="inscardno"/></td>
                             -->
                            <td>
                                联系电话：
                            </td>
                            <td>
                                <input name="input4" type="text" class="jiej_8 text_field3"
                                       id="phonecall"/>
                            </td>
                            <td>
                                体检日期&emsp;：
                            </td>
                            <td>
                                <input name="input7" type="text" class="jiej_8 text_field3"
                                       id="bdate" readonly/>
                            </td>
                        </tr>
                        <tr>
                            <td height="29">
                                &nbsp;体检类别：
                            </td>
                            <td>
                                <input name="input" type="text" class="jiej_8 text_field3"
                                       id="pexamtype" readonly/>
                            </td>
                            <td>
                                体检编号：
                            </td>
                            <td>
                                <input name="input2" type="text" class="jiej_8 text_field3"
                                       id="pexamid" readonly/>
                            </td>
                            <td height="29">
                                家庭地址：
                            </td>
                            <td style="align: left">
                                <input type="text" id="address"
                                       class="jiej_8 text_field3"/>
                            </td>
                        </tr>
                    </table>
                </div>
                <div style="display:none;">
                    <tr>
                        <td height="29">
                            村（社区）：
                        </td>
                        <td colspan="3">
                            <div id="shequ" style="float: left; width: 135px;"></div>
                        </td>
                        <td>
                            社保登记&emsp;：
                        </td>
                        <td>
                            <input type="checkbox" id="isInsurRig"/>
                        </td>
                    </tr>
                </div>
                <table width="711" height="4" border="0" cellspacing="0"
                       cellpadding="0">
                    <tr>
                        <td width="4"><img src="img/bt1.jpg"/></td>
                        <td width="703" class="bgr"></td>
                        <td width="4"><img src="img/bt3.jpg"/></td>
                    </tr>
                </table>
            </div>
            <div class="jiej_10 mar3">
                <ul class="xx1">
                    <li class="li11" onclick="changeli()" style='cursor: pointer;'>
                        <span id="tabxm" class="xxhover">项目</span>
                    </li>
                    <li class="li33" onclick="changeli2()" style='cursor: pointer;'>
                        <span id="tabmx">明细</span>
                    </li>
                    <div class="clear"></div>
                </ul>
            </div>
            <div class="jiej_11" id="dxm">
                <table width="680" border="0" cellspacing="0" cellpadding="5">
                    <tr>
                        <td width="330" valign="top">
                            <div class="jiej_12" id="rleftd">
                                <div>
                                    <img src="img/tou2.jpg"/>
                                </div>
                                <div class="jiej_17" id="dxm2" style="margin-top:-4px;">
                                    <div class="jiej_15">
                                        <table border="0" width="300" cellspacing="0"
                                               cellpadding="0">
                                            <tr>
                                                <td>
                                                    共体检项目
                                                    <span class="font2" id="exnum">0</span>项
                                                </td>
                                                <td align="right">
                                                    合计：
                                                    <span class="font2" id="totalprice">0.00</span>元
                                                </td>
                                            </tr>
                                        </table>
                                    </div>
                                    <div
                                            style="height: 280px; OVERFLOW-y: auto; OVERFLOW-x: hidden;"
                                            id="ditem0">
                                        <table width="300" border="0" cellspacing="0"
                                               cellpadding="0" id="examitems">
                                            <tbody id="texamitems">
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                                <div style="margin-top:-6px;">
                                    <img src="img/tou3.jpg"/>
                                </div>
                            </div>
                        </td>
                        <td valign="top">
                            <div class="tijian_4" id="drightr">
                                <ul class="tijian_5">
                                    <li class="tijian_hover" onclick="changetab()" id="tcxm"
                                        style="cursor: pointer;">
                                        套餐项目
                                    </li>
                                    <!--<li onclick="changetab2()" id="dgxm" style="cursor: pointer;">单个项目</li> -->
                                </ul>
                                <div id="groupitems">
                                    <div class="jiej_13"
                                         style="padding-top: 5px; height: 240px; OVERFLOW-y: auto; OVERFLOW-x: hidden;"
                                         id="dsigle0">
                                        <table width="300" border="0" cellspacing="0"
                                               cellpadding="0">
                                            <c:forEach items="${listgroup}" var="group" varStatus="i">
                                                <tr
                                                        onclick="addgroup2-----('${group.groupid}','${group.groupname}','${group.cost}')"
                                                        style="cursor: pointer;">   <!-- 原来的点击事件 addgroup2()     -->
                                                    <td width="20" height="28">
                                                        <img src="img/jiej5.jpg"/>
                                                    </td>
                                                    <td width="20">
                                                        (${i.index+1})
                                                    </td>
                                                    <td>
                                                            ${group.groupname}
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </table>
                                    </div>
                                </div>
                                <div id="sigelitems" style="display: none;">
                                    <div
                                            style="padding-top: 5px; height: 260px; OVERFLOW-y: auto; OVERFLOW-x: hidden;"
                                            id="dsigle" class="jiej_13">
                                        <table width="300" border="0" cellspacing="0"
                                               cellpadding="0">
                                            <c:forEach items="${listitems}" var="eitems" varStatus="i">
                                                <tr
                                                        onclick="additems2('${eitems.comid}','${eitems.comname}')"
                                                        style="cursor: pointer;">
                                                    <td width="20" height="28">
                                                        <img src="img/jiej5.jpg"/>
                                                    </td>
                                                    <td width="20">
                                                        (${i.index+1})
                                                    </td>
                                                    <td>
                                                            ${eitems.comname}
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </table>
                                    </div>
                                </div>
                                <div style="margin-top:-6px;">
                                    <img src="img/tou4.jpg"/>
                                </div>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>

            <div id="dmx" class="jiej_11" style="display: none;">
                <div id="grid_items_details" style="height: 300px;width:460"></div>
            </div>
            <table width="710" height="4" border="0" cellspacing="0"
                   cellpadding="0">
                <tr>
                    <td width="4"><img src="img/bt1.jpg"/></td>
                    <td width="702" class="bgr"></td>
                    <td width="4"><img src="img/bt3.jpg"/></td>
                </tr>
            </table>
        </div>
    </div>
</div>
</body>
</html>
