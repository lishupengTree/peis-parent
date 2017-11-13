<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ include file="../include.jsp" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <base id="base" href="<%=basePath%>"/>
    <title>医生站</title>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Cache-Control" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
    <link href="css/register.css" type="text/css" rel="stylesheet"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxGrid/codebase/dhtmlxgrid.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid_dhx_custom.css"/>
    <link rel="stylesheet" href="zTreeStyle/zTreeStyle.css" type="text/css"/>
    <link rel="stylesheet" href="zTreeStyle/zTreeIcons.css" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="js/jquery.pagination/pagination.css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxCalendar/codebase/dhtmlxcalendar.css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxCalendar/codebase/skins/dhtmlxcalendar_dhx_skyblue.css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxCalendar/codebase/skins/dhtmlxcalendar_dhx_web.css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxCalendar/codebase/skins/dhtmlxcalendar_omega.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxcalendar_dhx_skyblue.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxcombo.css"/>


    <script type="text/javascript" src="js/jquery-1.6.1.js" charset="utf-8"></script>

    <script type="text/javascript" src="js/jquery.blockUI.js" charset="utf-8"></script>
    <script type="text/javascript" src="js/jquery.lrTool.js" charset="utf-8"></script>
    <script type="text/javascript" src="js/LodopFuncs.js" charset="utf-8"></script>
    <script type="text/javascript" src="js/clc/comboTool.js" charset="utf-8"></script>
    <script type="text/javascript" src="js/dhtmlxcommon.js"></script>
    <script type="text/javascript" src="js/dhtmlxcombo.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcommon.js" charset="utf-8"></script>
    <script type="text/javascript" src="js/dhtmlxgrid.js" charset="utf-8"></script>
    <script type="text/javascript" src="js/dhtmlxgridcell.js" charset="utf-8"></script>
    <script type="text/javascript" src="dhtmlxGrid/codebase/excells/dhtmlxgrid_excell_cntr.js" charset="utf-8"></script>
    <script type="text/javascript" src="dhtmlxGrid/codebase/ext/dhtmlxgrid_filter.js" charset="utf-8"></script>
    <script type="text/javascript" src="js/jquery.ztree-2.6.js" charset="utf-8"></script>
    <script type="text/javascript" src="js/gl/util.js" charset="utf-8"></script>
    <script type="text/javascript" src="js/dhtmlxcalendar.js"></script>
    <script type="text/javascript" src="js/window.js"></script>
    <script type="text/javascript" src="js/jquery.pagination/jquery.pagination.js"></script>
    <script type="text/javascript" src="js/pexam/maindoctorcheck.js"></script>
    <script type="text/javascript" src="js/pexam/mainDoctorCheckPrint.js"></script>

    <style type="text/css">
        .luru_1 {
            width: 276px;
        }

        .luru_2 {
            width: 258px;
            padding: 8px;
        }

        .luru_3 {
            padding-left: 14px;
        }

        .btn01 {
            width: 64px;
            height: 26px;
            background: url(img/ss.gif) no-repeat;
            border: 0px;
            font-size: 13px;
            color: #6ba3b6;
            font-family: 微软雅黑;
            font-weight: bold;
        }

        .btn01:focus {
            background: url(img/ssfocus.gif) no-repeat;
        }

        .luru_4 {
            width: 710px;
            height: 44px;
            background: url(img/luru.jpg) no-repeat 0px 36px;
        }

        .luru_5 {
            width: 688px;
            border: 1px solid #93afba;
            border-bottom: none;
            border-top: none;
            padding: 5px 10px;
        }

        .luru_6 {
            width: 90px;
            height: 20px;
        }

        .luru_7 {
            border-bottom: 1px solid #93afba;
        }

        .luru_8 {
            border-top: 1px solid #93afba;
        }

        .luru_9 {
            border-right: 2px solid #93afba;
        }

        .luru_10 {
            border-bottom: 1px solid #dfeaf7;
        }

        .luru_11 {
            color: #3d7f98;
            font-weight: bold;
        }

        .luru_12 {
            padding-left: 10px;
        }

        .luru_13 {
            width: 40px;
            height: 20px;
            vertical-align: middle;
        }

        .luru_14 {
            width: 80px;
            height: 18px;
        }

        .autohe {
            height: 318px;
        }

        .tj_ysz1 {
            width: 100%;
        }

        .tj_ysz2 {
            color: #44839a;
            font-weight: bold;
        }

        .tj_ysz3 {
            width: 130px;
            height: 20px;
            line-height: 20px;
        }

        .tj_ysz4 {
            padding: 10px 0px;
        }

        .tj_ysz5 {
            height: 351px;
            padding: 5px 10px;
        }

        .tj_ysz6 {
            border-bottom: 1px solid #93afba;
        }

        .tj_ysz7 {
            border-right: 1px solid #93afba;
        }

        .tj_ysz8 {
            width: 195px;
            margin-left: 10px;
            float: left;
            padding-top: 12px;
        }

        .inputs {
            background: none;
            border: 0px;
        }

        .inputs2 {
            background: none;
            border: 0px;
            border-bottom: 1px solid #7E9DB9;
            width: 150px;
        }

        .inputs1 {
            border-bottom: 1px solid #7E9DB9;
        }

        .ysz_11 {
            width: 195px;
            padding-top: 5px;
        }

        .ysz_12 {
            background: url(img/tjxm.gif) no-repeat center bottom;
            height: 50px;
            width: 193px;
            border-left: 1px solid #97b1be;
            border-right: 1px solid #97b1be;
            text-align: center;
            line-height: 40px;
            font-size: 14px;
            font-weight: bold;
        }

        .ysz_13 {
            height: 4px;
            overflow: hidden;
        }

        .ysz_14 {
            width: 173px;
            border-left: 1px solid #97b1be;
            border-right: 1px solid #97b1be;
            padding: 0px 10px 10px 10px;
        }

        .ysz_18 {
            background: url(img/tjysz2.gif) repeat-x;
        }

        .text_field3 {
            border: 1px solid #93AFBA;
            line-height: 17px;
            width: 70px;
        }

        .ds4 {
            width: 125px;
            height: 59px;
            text-align: center;
            color: #f00;
            line-height: 59px;
            font-size: 22px;
            font-weight: bold;
            background: url(img/drug_1.gif) no-repeat;
        }

        body {
            font-family: "微软雅黑";
            font-size: 13px;
        }

        .tjbg_1 {
            border: 1px solid #93AFBA;
            padding: 5px 10px;
        }

        .tjbg_2 {
            border: 1px solid #93AFBA;
            padding: 5px 10px;
        }

        .tjbg_3 {
            border-bottom: 1px solid #000;
        }

        .tjbg_4 {
            color: red;
            font-weight: bold;
        }

        .tjbg_6 {
            margin: 10px 0px;
        }

        #showresult input {
            border: none;
            width: 70%;
            height: 25px;
        }

        #showresult textarea {
            border: none;
            width: 80%;
            min-height: 25px;
        }

        #deptsum {
            margin-left: 0px;
            width: 95%;
            height: 300px;
            border: 0 !important;
            overflow-y: hidden;
            overflow: none;
            line-height: 25px;
        }

        #suggestion {
            float: right;
            cursor: pointer;
        }

        #suggestiontext {
            width: 95%;
            height: 300px;
            border: 0 !important;
            overflow-y: hidden;
            overflow: none;
        }

        #printMessageBox {
            position: fixed;
            _position: absolute;
            top: 50%;
            left: 50%;
            text-align: center;
            margin: -60px 0 0 -155px;
            width: 310px;
            height: 120px;
            font-size: 16px;
            padding: 10px;
            color: #222;
            font-family: helvetica, arial;
            opacity: 0;
            background: #fff url(data:image/gif;base64,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) center 40px no-repeat;
            *background: #fff url(print_icon.gif) center 40px no-repeat;
            border: 6px solid #555;
            border-radius: 8px;
            -webkit-border-radius: 8px;
            -moz-border-radius: 8px;
            box-shadow: 0px 0px 10px #888;
            -webkit-box-shadow: 0px 0px 10px #888;
            -moz-box-shadow: 0px 0px 10px #888;
        }

        .nmtj_1 {
            background: none;
            border: none;
            border-bottom: 2px solid #000;
            font-size: 24px;
            font-weight: bold;
            height: 25px;
        }

        .nmtj_2 {
            width: 80px;
        }

        .nmtj_3 {
            width: 30px;
        }

        .nmtj_4 {
            background: none;
            border: none;
            border-bottom: 1px solid #000;
        }

        .nmtj_5 {
            background-color: #444;
            text-align: center;
        }

        .nmtj_5 tr {
            background-color: #fff;
        }

        .nmtj_6 {
            width: 98%;
            height: 100%;
            background: none;
            border: none;
            text-align: center;
            vertical-align: bottom;
        }

        .tjfk_1 {
            font-size: 16px;
            font-weight: bold;
        }

        .tjfk_2 input {
            text-align: left;
        }

        .tjfk_3 {
            width: 99%;
            height: 70px;
            background: none;
            border: none;
        }

        .box {
            width: 640px;
            height: 970px;
        }

        .box2 {
            font-size: 24px;
            font-weight: bold;
            text-align: center;
            height: 45px;
        }

        .mar1 {
            margin-top: 15px;
            margin-left: 0px;
        }

        input {
            background: none;
            border: none;
            font-size: 14px;
            vertical-align: middle;
            height: 17px;
            line-height: 17px;
            margin-top: -2px;
            *margin-top: 0px;
        }

        textarea {
            background: none;
            border: none;
            font-size: 14px;
            line-height: 26px;
            overflow-y: hidden;
        }

        .test {
            border: 1px red solid;
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
            font-family: Microsoft YaHei, Lucida Grande, Helvetica, Tahoma, Arial, sans-serif;
            margin-left: 8px;
            color: #000;
            padding: 0
        }

        .textbk {
            border: 1px solid #93AFBA;
            background-color: #fff;
        }

        input {
            vertical-align: middle;
        }

        .gjss1 {
            color: #44839a;
            font-family: '微软雅黑';
            font-size: 13px;
            text-decoration: underline;
            background: none;
            border: none;
            cursor: pointer;
            position: relative;
            z-index: 1;
            font-weight: bold;
            margin-right: 0px;
            margin-left: 5px;
        }

        .gjss2 {
            position: absolute;
            z-index: 10000;
            margin-left: -3px;
        }

        .gjss3 {
            color: #000;
            font-family: "微软雅黑";
            font-size: 13px;
        }

        .bgr4 {
            background: url(img/tp2.jpg) repeat-x 0px 2px;
        }
    </style>
    <script>
        ENTER = "if(node.id=='search_value'){topSearch();}";
    </script>
</head>

<body style="overflow-y:hidden;">
<input type="hidden" id="basePath" value="<%=basePath%>"/>
<input type="hidden" id="hosname" value="${hosname}"/>
<input type="hidden" id="doctorname" value="${doctorname}"/>
<input type="hidden" id="doctorId" value="${doctorId}"/>
<input type="hidden" id="DoubleReport" value="${DoubleReport}"/>
<input type="hidden" id="ZJDoctor" value="${ZJDoctor}"/>
<input type="hidden" id="isPrintCover" value="${isPrintCover}"/>
<input type="hidden" id="PrintOneReport" value="${PrintOneReport}"/>
<input type="hidden" id="pexamid"/>
<div class="top">
    <jsp:include page="../top.jsp"/>
</div>
<div class="tj_ysz1">
    <div class="luru_1 floatl" style="margin-top: 10px;">
        <div>
            <table width="276" height="15" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td width="34"><img src="img/tp1.jpg"/></td>
                    <td width="238" class="bgr1"><span class="font3" id="infobase">基本信息</span></td>
                    <td width="4"><img src="img/tp3.jpg"/></td>
                </tr>
            </table>
            <div class="bord luru_2" id='aaaa'>
                <table width="255" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td colspan="2" rowspan="2">
                            <div class="ds4" id="patname"></div>
                        </td>
                        <td width="97" align="right">&nbsp;性&emsp;&emsp;别：</td>
                        <td width="50" align="center" class="font4" id="sex"></td>
                    </tr>
                    <tr>
                        <td align="right">&nbsp;年&emsp;&emsp;龄：</td>
                        <td class="font4" align="center" id="age"></td>
                    </tr>
                    <tr>
                        <td colspan="4" align="center">
                            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td height="28" width="80">身份证号：</td>
                                    <td class="font4" align="left" id="inscardno"></td>
                                </tr>
                                <tr>
                                    <td height="28">体检类别：</td>
                                    <td align="left" class="font4" id="examtype">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </div>
            <table width="276" height="4" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td width="4"><img src="img/bt1.jpg"/></td>
                    <td width="268" class="bgr"></td>
                    <td width="4"><img src="img/bt3.jpg"/></td>
                </tr>
            </table>
        </div>
        <div class="mar3">
            <table width="276" height="15" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td width="34"><img src="img/tp1.jpg"/></td>
                    <td width="238"><span class="font3">候检名单</span><img src="img/tp0.jpg"/><input
                            style="vertical-align:middle;" onclick="timeSearch()" type="button" value="搜索"
                            class="gjss1"/></td>
                    <td width="4"><img src="img/tp3.jpg"/></td>
                </tr>
            </table>

            <div style="position:absolute;z-index:2">
                <table id="choose" height="57" border="0" cellspacing="0" cellpadding="0" class="gjss2"
                       style="display:none;">
                    <tr>
                        <td width="10"><img src="img/gjss1.png"/></td>
                        <td background="img/gjss2.png">
                            <table width="400" border="0" cellspacing="0" cellpadding="0" class="gjss3">
                                <tr>
                                    <td align="center">体检时间：</td>
                                    <td><input class="textbk" type="text" name="textfield" id="starttime1"
                                               style="width:75px"/>-
                                        <input type="text" class="textbk" name="textfield2" id="endtime1"
                                               style="width:75px"/></td>
                                    <td>状态：</td>
                                    <td>
                                        <div id="isTest1" style="margin-left:0px"></div>
                                    </td>
                                    <td height="28"><img id="search_img" src="img/top7.jpg" onclick="timeSearch1()"
                                                         style="margin-top:0px;*margin-top:-1px;margin-left:7px;border:solid #fff 2px;"/>
                                    </td>
                                </tr>
                            </table>

                        </td>
                        <td width="10"><img src="img/gjss3.png"/></td>
                    </tr>
                </table>
            </div>

            <div class="bord tj_ysz5" id="dleft">
                <div>
                    <table width="180">
                        <tr>
                            <td>
                                <input class="textbk" type="text" name="textfield" id="starttime" style="width:75px"/>
                            </td>
                            <td>
                                <input type="text" class="textbk" name="textfield2" id="endtime" style="width:75px"/>
                            </td>
                            <td>
                                <div id="isTest" style="margin-left:0px;"></div>
                            </td>
                        </tr>
                    </table>
                </div>
                <div id="grid_doctorstation" style="width: 100%;height:340px;margin-top: 5px;"></div>
                <div id="pagination"
                     style="position:relative; top:7px;left:5px;float:right;display:block;margin-top: -25px"></div>
            </div>
            <table width="276" height="4" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td width="4"><img src="img/bt1.jpg"/></td>
                    <td width="268" class="bgr"></td>
                    <td width="4"><img src="img/bt3.jpg"/></td>
                </tr>
            </table>
        </div>
    </div>

    <div class="tj_ysz8">
        <div class="ysz_11">
            <div class="ysz_13">
                <table width="195" height="4" border="0" cellspacing="0" cellpadding="0">
                    <tr height="4">
                        <td width="4" valign="top"><img src="img/tjysz1.gif"/></td>
                        <td width="187" class="ysz_18">&nbsp;</td>
                        <td width="4" valign="top"><img src="img/tjysz3.gif"/></td>
                    </tr>
                </table>
            </div>
            <div class="ysz_12">总检项目</div>
            <div class="ysz_14" id="trc" style="overflow-y:auto;">
                <td bgcolor="#f6faff" valign="top" width="80">
                    <div style="height:465px;" id="mytree">
                        <ul id="menuTree" class="tree">
                        </ul>
                    </div>
            </div>
            <div class="ysz_13">
                <table width="195" height="4" border="0" cellspacing="0" cellpadding="0">
                    <tr height="4">
                        <td width="4" valign="top"><img src="img/bt1.jpg"/></td>
                        <td width="187" class="bgr">&nbsp;</td>
                        <td width="4" valign="top"><img src="img/bt3.jpg"/></td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
    <table id='zjbgbk' class="floatl luru_3" style="margin-top: 10px;" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td>
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td>&nbsp;</td>
                        <td align="right">
                            <div id="but1" style="display:none;">
                                <!-- <button id="oneKeyButton" type="button" class="btn2" onclick="showOneKeySuggest()" disabled>
                                 一键总检
                             </button> -->
                                <button id="backButton" type="button" class="btn2" onclick="backButtonClick()" disabled>
                                    体检项目回退
                                </button>
                                <button id="contagionrptButton" type="button" class="btn2" onclick="contagionrptPut()"
                                        disabled>
                                    重大阳性上报
                                </button>
                                <button id="suggestButton" type="button" class="btn2" onclick="showdetail()" disabled>
                                    总检报告
                                </button>
                                <button id="examResultButton" type="button" class="btn2" onclick="showresu()" disabled>
                                    检查结果
                                </button>
                                <button id="saveButton" type="button" class="btn2" onclick="doSave()" disabled>
                                    保存
                                </button>
                                <button id="completeButton" type="button" class="btn2" onclick="click_completeButton()"
                                        disabled>
                                    完成
                                </button>
                                <button id="previewButton" name="previewButton" type="button" class="btn2"
                                        onclick="doPrintA('0');" disabled>
                                    预览
                                </button>
                                <button id="printButton" name="one" type="button" class="btn2" onclick="doPrint(this)"
                                        disabled style="display: none;">
                                    打印
                                </button>
                                <button id="printSomeButton" type="button" class="btn2" style="display: none;"
                                        onclick="doSomePrintA();$('#backButton').attr('disabled',true);">
                                    批量打印
                                </button>
                            </div>

                            <div id="but2" style="display:none;">
                                <div>
                                    <!-- <button id="oneKeyButton" type="button" class="btn2" onclick="showOneKeySuggest()">
                                      一键总检
                                  </button> -->
                                    <button id="contagionrptButton" type="button" class="btn2"
                                            onclick="contagionrptPut()">
                                        传染病上报
                                    </button>
                                    <button id="suggestButton" type="button" class="btn2" onclick="showdetail()">
                                        总检报告
                                    </button>
                                    <button id="examResultButton" type="button" class="btn2" onclick="showresu()">
                                        检查结果
                                    </button>
                                    <button id="saveButton" type="button" class="btn2" onclick="doSave()">
                                        保存
                                    </button>
                                </div>
                                <div>
                                    <button id="printButton" name="one" type="button" class="btn2"
                                            onclick="doPrint(this)">
                                        打印
                                    </button>
                                    <button id="printSomeButton" type="button" class="btn2" onclick="doSomePrintA()">
                                        批量打印
                                    </button>
                                </div>
                            </div>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>

        <tr>
            <td>
                <table width="100%" height="15" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td width="34"><img src="img/tp1.jpg"/></td>
                        <td width="100%" class="bgr1"><span class="font3">检查结果登记</span></td>
                        <td width="4"><img src="img/tp3.jpg"/></td>
                    </tr>
                </table>

                <div class="bord tj_ysz4"><!-- style="border:1px solid red" -->
                    <table width="95%" style="margin-right: 10px;" border="0" cellspacing="1" cellpadding="0"
                           bgcolor="#93afba" class="mar3" id="resu">
                        <tr bgcolor="#e4edf9" align="center">
                            <td width="30" height="30" class="tj_ysz2" id="ep0">分类</td>
                            <td colspan="3">
                                <table border="0" cellspacing="1" cellpadding="0">
                                    <tr id="jghead">
                                        <td width="80" class="tj_ysz2" id="ep1">体检项目</td>
                                        <td class="tj_ysz2" id="ep2">体检结果</td>
                                        <td width="75" class="tj_ysz2" id="ep3">单位</td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                        <tr align="center">
                            <td bgcolor="#f6faff" width="30"><strong id="examtypep"></strong></td>
                            <td colspan="3" bgcolor="#ffffff">
                                <div style="OVERFLOW-y:auto;OVERFLOW-x:hidden;" id="dtitems" class="autohe">
                                    <table width="100%" border="0" cellspacing="0" cellpadding="3" id="titems">
                                    </table>
                                </div>
                            </td>
                        </tr>
                        <tr align="center">
                            <td rowspan="2" bgcolor="#f6faff"><strong>科室小结</strong></td>
                            <td colspan="3" bgcolor="#ffffff" height="80">
                                <div style="">
                                    <textarea id='LogC' rows='5'
                                              style="overflow-x:auto;overflow-y:auto;width:98%"> </textarea>
                                </div>
                            </td>
                        </tr>
                        <tr align="right">
                            <td colspan="3" bgcolor="#f6faff" height="28" align="right">
                                <table width="100%" height="28" border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <td>&nbsp;</td>
                                        <td align="left" width="45">医生：</td>
                                        <td width="50" id="excdoctorname">&nbsp;&nbsp;</td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>

                    <!-- 体检报告 start-->
                    <div id="tjbg_1_div" class="tjbg_1" style="width:92%"><!-- style="border:1px solid blue" -->
                        <iframe id="myIframe" name="myIframe" src='' width='100%' height='100%' topmargin='0'
                                leftmargin='0'
                                marginheight='0' scrolling='auto' marginwidth='0' frameborder='no'></iframe>

                    </div>
                    <div id="tjbg_2_div" class="tjbg_2" style="width:92%;display: none;">
                        <!-- style="border:1px solid blue" -->
                        <iframe id="myIframe1" name="myIframe1" src='' width='100%' height='100%' topmargin='0'
                                leftmargin='0'
                                marginheight='0' scrolling='auto' marginwidth='0' frameborder='no'></iframe>

                    </div>
                    <!-- 体检报告 end-->
                </div>
                <table width="100%" height="4" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td width="4"><img src="img/bt1.jpg"/></td>
                        <td width="100%" class="bgr"></td>
                        <td width="4"><img src="img/bt3.jpg"/></td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>

</div>
</body>
</html>
