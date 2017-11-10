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
    <title>预约体检</title>
    <base href="<%=basePath%>"/>
    <link href="css/register.css" type="text/css" rel="stylesheet"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxcombo.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid_dhx_custom.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxcalendar.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxcalendar_dhx_skyblue.css"/>
    <!--
    <link rel="stylesheet" href="js/uploadify/uploadify.css" type="text/css"></link>
     -->
    <script type="text/javascript" src="js/dhtmlxcommon.js"></script>
    <script type="text/javascript" src="js/jquery-1.6.1.js"></script>
    <script type="text/javascript" src="js/jquery.blockUI.js"></script>
    <script type="text/javascript" src="js/jquery.lrTool.js"></script>
    <script type="text/javascript" src="js/jquery-ui-1.8.21.custom.min.js"></script>

    <!-- combo -->
    <link rel="stylesheet" type="text/css" href="css/dhtmlxcombo.css"/>
    <script type="text/javascript" src="js/dhtmlxcombo.js"></script>

    <!-- grid -->
    <script type="text/javascript" src="js/dhtmlxgrid.js"></script>
    <script type="text/javascript" src="js/dhtmlxgridcell.js"></script>
    <!--
    <script type="text/javascript" src="js/gl/dhtmlxgrid_excell_combo.js"></script>
     -->

    <script type="text/javascript" src="js/pexam/appointment.js"></script>
    <script type="text/javascript" src="js/gl/util.js"></script>
    <script type="text/javascript" src="js/util.js"></script>
    <script type="text/javascript" src="js/window.js"></script>
    <script type="text/javascript" src="js/wpCalendar.js"></script>
    <script type="text/javascript" src="js/dhtmlxcalendar.js"></script>

    <!-- 分页 -->
    <link rel="stylesheet" type="text/css" href="js/jquery.pagination/pagination.css"/>
    <script type="text/javascript" src="js/jquery.pagination/jquery.pagination.js"></script>


    <style type="text/css">
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
    </style>
</head>

<body onload="doOnLoad()">
<input type="hidden" value="${isDishDept}" id="isDishDept"/><!-- 是否区分科室“Y”是 -->
<input type="hidden" value="${isPrintA5}" id="isPrintA5"/><!-- 是否打印A5 -->
<input type="hidden" value="${isPrintCervical}" id="isPrintCervical"/><!-- 退休是否打印宫颈刮片 -->
<input type="hidden" value="${ifstartcrm}" id="ifstartcrm" title="是否启用CRM预约"/>
<div class="top">
    <jsp:include page="../top.jsp"/>
</div>
<div class="jiej_1">
    <div class="floatl jiej_4">
        <div>
            <table width="276" height="15" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td width="34"><img src="img/tp1.jpg"/></td>
                    <td width="238" class="bgr1"><span class="font3">预约列表</span></td>
                    <td width="4"><img src="img/tp3.jpg"/></td>
                </tr>
            </table>
            <div class="bord jiej_5 tijian_10" id="dleft">
                <div class="tijian_6">
                    <table width="240" height="39" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td width="45">名称：</td>
                            <td><input name="findname" type="text" class="tijian_7 text_field7" id="findname"/></td>
                            <td width="45"><img src="img/jj_s.gif" onclick="findAppointment(1)"
                                                style='cursor: pointer;'/></td>
                        </tr>
                    </table>
                </div>
                <div style="height:580px;OVERFLOW-y:auto;OVERFLOW-x:hidden;" id="leftnav">
                    <ul class="tijian_8" style="width:100%;"></ul>
                </div>
                <!--分页DIV-->
                <div style='width: 100%;text-align: center;height: 30px;line-height: 30px;margin-bottom:-10px;position:relative;'
                     id='fypage'>
                </div>
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
    <div class="floatl jiej_6">
        <div class="jiej_10">
            <ul class="xx1">
                <li class="li11" id="tjxx" onclick="changeli()" style="cursor: pointer;"><span class="xxhover"
                                                                                               id="tjxxspan">体检信息</span>
                </li>
                <li class="li33" id="rylb" onclick="changeli2()" style="cursor: pointer;"><span
                        id="rylbspan">人员列表</span></li>
                <div class="clear"></div>
            </ul>
        </div>
        <div class="jiej_11" id="main_info">
            <div>
                <table width="688" height="15" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td width="34"><img src="img/tp1.jpg"/></td>
                        <td width="650" class="bgr1"><span class="font3">基本信息</span></td>
                        <td width="4"><img src="img/tp3.jpg"/></td>
                    </tr>
                </table>
                <div class="bord tijian_1">
                    <table width="660" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td width="70" height="28">体检编号：</td>
                            <td width="150"><input name="examid" type="text" class="jiej_8 text_field3" id="examid"
                                                   onfocus="setExamid()"/></td>
                            <td width="70">体检名称：</td>
                            <td colspan="3"><input name="examname" type="text" class="tijian_2 text_field4"
                                                   id="examname" readonly/></td>
                        </tr>
                        <tr>
                            <td width="70">体检类型：</td>
                            <td width="150">
                                <!--
                                <input name="examType" type="text" class="jiej_8 text_field3" id="examType"/>
                                -->
                                <div id="combo_examType" style="float:left;width:140px;"></div>
                                <script>
                                    var combo_examType = new dhtmlXCombo("combo_examType", "alfa3", 140);
                                </script>
                            </td>
                            <td width="70">体检单位：</td>
                            <td colspan="3"><input name="unitname" type="text" class="tijian_2 text_field4"
                                                   id="unitname"/></td>
                        </tr>
                        <tr>
                            <td height="28">预约时间：</td>
                            <td><input name="bookdate" type="text" class="jiej_8 text_field3" id="bookdate" readonly/>
                            </td>
                            <td height="28">折&emsp;&emsp;扣：</td>
                            <td><input name="discount" type="text" class="jiej_8 text_field3" id="discount"/></td>
                            <td width="70">人&emsp;&emsp;数：</td>
                            <td width="150"><input name="examqty" type="text" class="jiej_8 text_field3" id="examqty"/>
                            </td>
                        </tr>
                    </table>
                    <div style="display:none">
                        <tr>
                            <td>单&emsp;&emsp;价：</td>
                            <td width="150"><input name="unitprice" type="text" class="jiej_8 text_field3"
                                                   id="unitprice"/></td>
                            <td>金&emsp;&emsp;额：</td>
                            <td><input name="discamt" type="text" class="jiej_8 text_field3" id="discamt"/></td>
                            <td>计划任务：</td>
                            <td>
                                <table width=100% border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <td>
                                            <div id="combo_appointType" style="float:left;width:45px;"></div>
                                            <script>
                                                var combo_appointType = new dhtmlXCombo("combo_appointType", "alfa3", 45);
                                            </script>
                                        </td>
                                        <td>&nbsp;</td>
                                        <td>年份：</td>
                                        <td>
                                            <div id="combo_tjyear" style="float:left;width:45px;"></div>
                                            <script>
                                                var combo_tjyear = new dhtmlXCombo("combo_tjyear", "alfa3", 45);
                                            </script>
                                        </td>
                                    </tr>
                                </table>

                            </td>
                        </tr>
                    </div>
                </div>
                <table width="688" height="4" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td width="4"><img src="img/bt1.jpg"/></td>
                        <td width="680" class="bgr"></td>
                        <td width="4"><img src="img/bt3.jpg"/></td>
                    </tr>
                </table>
            </div>
            <table width="688" border="0" cellspacing="0" cellpadding="0" class="mar3">
                <tr>
                    <td>&nbsp;</td>
                    <td width="388">
                        <ul class="ant">
                            <li onclick="doPrintY()" style="cursor: pointer;">预约单打印</li>
                            <li onclick="clearScreen()" style="cursor: pointer;">新建</li>
                            <li onclick="saveAppointment()" style="cursor: pointer;">保存</li>
                        </ul>
                    </td>
                </tr>
            </table>
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
                                <div style="height:280px;OVERFLOW-y:auto;OVERFLOW-x:hidden;" id="ditem0">
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
                                <li class="tijian_hover" onclick="changetab()" id="tcxm" style="cursor: pointer;">套餐项目
                                </li>
                                <!--
                                <li onclick="changetab2()" id="dgxm" style="cursor: pointer;">单个项目</li>
                                -->
                            </ul>
                            <div id="groupitems">
                                <div class="jiej_13"
                                     style="padding-top: 5px;height:280px;OVERFLOW-y:auto;OVERFLOW-x:hidden;"
                                     id="dsigle0">
                                    <table width="300" border="0" cellspacing="0" cellpadding="0">
                                        <c:forEach items="${listgroup}" var="group" varStatus="i">
                                            <tr onclick="addgroup2('${group.groupid}','${group.groupname}','${group.cost}')"
                                                style="cursor: pointer;">
                                                <td width="20" height="28"><img src="img/jiej5.jpg"/></td>
                                                <td width="20">(${i.index+1})</td>
                                                <td>${group.groupname}</td>
                                            </tr>
                                        </c:forEach>
                                    </table>
                                </div>
                            </div>
                            <div id="sigelitems" style="display:none;">
                                <div style="padding-top: 5px;height:280px;OVERFLOW-y:auto;OVERFLOW-x:hidden;"
                                     id="dsigle" class="jiej_13">
                                    <table width="300" border="0" cellspacing="0" cellpadding="0">
                                        <c:forEach items="${listitems}" var="eitems" varStatus="i">
                                            <tr onclick="additems2('${eitems.comid}','${eitems.comname}')"
                                                style="cursor: pointer;">
                                                <td width="20" height="28"><img src="img/jiej5.jpg"/></td>
                                                <td width="20">(${i.index+1})</td>
                                                <td>${eitems.comname}</td>
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
        </div>

        <div class="jiej_11" style="display:none;height:540px;" id="grid_patient_appoint">
            <div id="grid_patient_appoint2" style="height: 500px;">
            </div>
            <div id="pagination1"
                 style="position:relative;top:7px;left:5px;float:right;display:block;margin-top:-30px"></div>


            <c:choose>
                <c:when test="${1==1}">
                    <ul class="ant" style="margin-top:5px;margin-left:200px;" id="func_ul">
                        <li style='cursor: pointer;' id="pullin" onclick="pullIn()">引入</li>
                        <li style='cursor: pointer;' id="newpatient" onclick="creatNew()">新建</li>
                        <li style='cursor: pointer;' id="importPersonnel">导入人员</li>
                        <li style='cursor: pointer;' onclick="delteRow()">删除</li>
                    </ul>
                </c:when>
                <c:otherwise>
                    <ul class="ant" style="margin-top:5px;margin-left:200px;" id="func_ul">
                        <li style='cursor: pointer;' onclick="delteRow()">删除</li>
                    </ul>
                </c:otherwise>
            </c:choose>
        </div>
        <table width="710" height="4" border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td width="4"><img src="img/bt1.jpg"/></td>
                <td width="702" class="bgr"></td>
                <td width="4"><img src="img/bt3.jpg"/></td>
            </tr>
        </table>
    </div>
</div>

<div style="display: none;">
    <form action="pexam/showmainlist.htm" method="post" id="searchForm">
        <input type="hidden" name="currentPage" id="currentPage"/>
    </form>
</div>
</body>
</html>
