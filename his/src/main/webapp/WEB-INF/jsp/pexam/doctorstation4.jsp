<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ include file="../include.jsp" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <base href="<%=basePath%>"/>
    <title>医生站</title>
    <!--初始化日期控件的js和css  -->
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7"/>

    <link rel="stylesheet" type="text/css" href="dhtmlxGrid/codebase/dhtmlxgrid.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid_dhx_custom.css"/>
    <link href="css/top.css" rel="stylesheet" type="text/css"/>
    <link href="css/register.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" href="js/jquery.pagination/pagination.css" type="text/css"></link>
    <link rel="stylesheet" type="text/css" href="css/dictionary.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxcalendar.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxcalendar_dhx_skyblue.css"/>
    <!-- 引入编辑器插件的 css文件  -->
    <link rel="stylesheet" href="kindeditor-4.0.3/plugins/code/prettify.css" type="text/css"></link>

    <script type="text/javascript" src="js/jquery-1.6.1.js"></script>
    <script type="text/javascript" src="js/dhtmlxcommon.js"></script>
    <script type="text/javascript" src="js/dhtmlxgrid.js"></script>
    <script type="text/javascript" src="js/dhtmlxgridcell.js"></script>
    <script type="text/javascript" src="dhtmlxGrid/sources/ext/dhtmlxgrid_srnd.js"></script>
    <script type="text/javascript" src="js/util.js"></script>
    <script type="text/javascript" src="js/jquery.pagination/jquery.pagination.js"></script>
    <script type="text/javascript" src="js/dhtmlxcalendar.js"></script>
    <!-- 初始化日期控件的js和css -->
    <link href="css/register.css" type="text/css" rel="stylesheet"/>
    <link rel="stylesheet" href="zTreeStyle/zTreeStyle.css" type="text/css"/>
    <link rel="stylesheet" href="zTreeStyle/zTreeIcons.css" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxGrid/codebase/dhtmlxgrid.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid_dhx_custom.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxcombo.css"/>
    <link rel="stylesheet" type="text/css" href="js/jquery.pagination/pagination.css"/>
    <script type="text/javascript" src="js/window.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcommon.js"></script>
    <script type="text/javascript" src="js/jquery-1.6.1.js"></script>
    <script type="text/javascript" src="js/jquery.blockUI.js"></script>
    <script type="text/javascript" src="js/jquery.lrTool.js"></script>
    <script type="text/javascript" src="js/clc/comboTool.js"></script>
    <script type="text/javascript" src="js/dhtmlxcombo.js"></script>
    <script type="text/javascript" src="js/dhtmlxgrid.js"></script>
    <script type="text/javascript" src="js/dhtmlxgridcell.js"></script>
    <script type="text/javascript" src="dhtmlxGrid/codebase/excells/dhtmlxgrid_excell_cntr.js"></script>
    <script type="text/javascript" src="dhtmlxGrid/codebase/ext/dhtmlxgrid_filter.js"></script>
    <script type="text/javascript" src="js/pexam/doctorstation_ys4.js"></script>
    <script type="text/javascript" src="js/jquery.ztree-2.6.js"></script>
    <script type="text/javascript" src="js/window.js"></script>
    <script type="text/javascript" src="js/gl/util.js"></script>
    <script type="text/javascript" src="js/jquery.pagination/jquery.pagination.js"></script>
    <script type="text/javascript" src="js/inp/review_warning_tjysz.js"></script>
    <!-- 引入编辑器插件的 js文件  -->
    <script type="text/javascript" src="kindeditor-4.0.3/kindeditor.js"></script>
    <script type="text/javascript" src="kindeditor-4.0.3/lang/zh_CN.js"></script>
    <script type="text/javascript" src="kindeditor-4.0.3/plugins/code/prettify.js"></script>
    <style type="text/css">
        .luru_1 {
            width: 276px;
        }

        .luru_2 {
            width: 258px;
            padding: 8px;
        }

        .luru_3 {
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
            margin-left: 20px;
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

        .dhx_combo_list {
            border: 1px solid #93afba;
            height: 80px;
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

        .jsbr_5 {
            background: url(img/tjysz2.gif) repeat-x;
        }

        .xdb14 {
            width: 105px;
            height: 47px;
            background: url(img/xdb08.gif) no-repeat;
            font-size: 18px;
            color: #f00;
            text-align: center;
            line-height: 47px;
            font-weight: bold;
        }

        .bord1 {
            border: 1px solid #93afba;
            border-bottom: none;
            border-top: none;
            padding: 5px 0px;
            margin-top: -3px;
        }

        .bord2 {
            border: 1px solid #93afba;
            border-bottom: none;
            border-top: none;
        }

        .jzdl_1 {
            width: 252px;
            height: 534px;
            background: url(imgs/hjdl.png) no-repeat;
            padding-top: 10px;
            margin-left: -10px;
        }

        .jzdl_2 {
            width: 235px;
            height: 519px;
        }

        .jzdl_3 {
            width: 235px;
            height: 26px;
            background: url(imgs/jzdl_img1.gif) no-repeat;
        }

        .jzdl_3 ul {
            margin: 0px;
            padding: 0px;
            list-style: none;
        }

        .jzdl_3 ul li {
            width: 77px;
            height: 26px;
            line-height: 26px;
            text-align: center;
            float: right;
            display: inline;
            color: #7199a8;
        }

        .jzdl_3 ul li.hover {
            color: #000;
            background: url(imgs/jzdl_img2.png) no-repeat;
        }

        .jzdl_3 ul li.hover span {
            color: #f00;
        }

        .jzdl_4 {
            margin: 10px -10px 5px 0px;
        }

        .jzdl_5 {
            width: 83px;
            height: 15px;
            line-height: 15px;
            font-family: "微软雅黑";
            vertical-align: middle;
        }

        .jzdl_6 {
            width: 215px;
            height: 390px;
            background-color: #f1f1f1;
            padding: 0px;
            margin: 0 auto;
            list-style: none;
            margin-top: 10px;
        }

        .jzdl_6 li {
            width: 205px;
            border-bottom: 1px solid #fff;
            text-align: center;
            padding: 5px 5px 2px 5px;
            color: #646464;
        }

        .jzdl_6 li.hover {
            width: 205px;
            background-color: #eedab5;
        }

        .jzdl_7 {
            margin: 0 auto;
            margin-top: 8px;
        }

        .hand {
            cursor: pointer;
        }

        .textbk {
            border: 1px solid #93AFBA;
        }

        .new_btn {
            width: 64px;
            height: 27px;
            font-size: 13px;
            text-align: center;
            background: url(img/ss.gif) no-repeat left 1px;
            border: 0 none;
            cursor: pointer;
            position: relative
        }

        .jsq1 {
            width: 268px;
            height: 270px;
            margin: 0 auto;
            background: url(images/jsqbg.jpg) no-repeat;
            padding-top: 13px;
        }

        .jsq2 {
            width: 239px;
            margin: 0 auto;
        }

        .jsq3 {
            width: 229px;
            height: 60px;
            line-height: 60px;
            background: url(images/jsqtext.jpg) no-repeat;
            border: 0px;
            text-align: right;
            padding-right: 10px;
            font-size: 30px;
            font-weight: bold;
        }

        .indtd_bt {
            border-right: 1px solid #93afba;
            border-bottom: 1px solid #93afba;
            text-align: center;
        }

        .indtd {
            border-right: 1px solid #93afba;
            border-bottom: 1px solid #93afba;
        }

        .indinput {
            border-left: 1px;
            border-top: 1px;
            border-right: 1px;
            border-bottom: 1px;
            width: 99%
        }

        .indinputhalf {
            border-left: 1px;
            border-top: 1px;
            border-right: 1px;
            border-bottom: 1px;
            width: 45%
        }

        .dhx_combo_box div {
            margin-top: -200px;
            height: 20px;
        }

        .textbk {
            border: 1px solid #93AFBA;
            background-color: #fff;
        }

        #faqbg {
            background-color: #666666;
            position: absolute;
            z-index: 99;
            left: 0;
            top: 0;
            display: none;
            width: 100%;
            height: 1000px;
            opacity: 0.5;
            filter: alpha(opacity=50);
            -moz-opacity: 0.5;
        }

        #faqdiv {
            position: absolute;
            width: 550px;
            left: 50%;
            top: 260px;
            margin-left: -100px;
            height: 370px;;
            z-index: 100;
            background-color: #fff;
            border: 1px #8FA4F5 solid;
            padding: 1px;
        }

        #faqdiv h2 {
            height: 25px;
            font-size: 14px;
            background-color: #d9eaee;
            position: relative;
            padding-left: 10px;
            line-height: 25px;
        }

        #faqdiv a {
            position: absolute;
            right: 5px;
            font-size: 12px;
            color: #FF0000;
        }

        #faqdiv .form {
            padding: 10px;
        }

        .hiddenimg {
            display: none;
        }
    </style>
    <script type="text/javascript">
        var path = "<%=path%>";
        ENTER = "if(node.id=='search_value'){topSearch();}";

    </script>
</head>

<body style="overflow-y:auto" onload="ff();">
<input type="hidden" value="${isDishDept}" id="isDishDept"/><!-- 是否区分科室“Y”是 -->
<input type="hidden" value="${doctorName}" id="doctorName"/>
<input type="hidden" value="${doctorId}" id="doctorId"/>
<input type="hidden" value="${lisType}" id="lisType"/><!-- lis系统厂商类型 -->
<input type="hidden" value="${typeflag}" id="typeflag"/>
<input type="hidden" value="${hosnum}" id="hosnum"/>
<input type="hidden" value="" id="pexamid"/>
<input type="hidden" value="" id="errorMsg"/>
<input type="hidden" value="${login_dept.deptcode }" title="登录科室id" id="deptcode"/>
<div class="top">
    <jsp:include page="../top.jsp"/>
</div>
<!--<div id="review_warning" ></div>    top提示框 -->
<div id="faqbg"></div>
<div id="faqdiv" style="display:none">
    <h2></h2>
    <div class="form">
        <img src="img/tooth.jpg" border="0" usemap="#Map"/>
        <map name="Map" id="Map">
            <area shape="rect" id="a1" coords="13,7,45,94" href="javascript:void(0);" title="右上8"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="a2" coords="46,8,77,93" href="javascript:void(0);" title="右上7"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="a3" coords="77,8,111,93" href="javascript:void(0);" title="右上6"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="a4" coords="111,8,138,92" href="javascript:void(0);" title="右上5"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="a5" coords="138,7,166,91" href="javascript:void(0);" title="右上4"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="a6" coords="165,7,193,90" href="javascript:void(0);" title="右上3"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="a7" coords="193,8,221,93" href="javascript:void(0);" title="右上2"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="a8" coords="221,9,249,92" href="javascript:void(0);" title="右上1"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="a9" coords="257,10,285,93" href="javascript:void(0);" title="左上1"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="a10" coords="289,11,315,93" href="javascript:void(0);" title="左上2"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="a11" coords="316,7,341,92" href="javascript:void(0);" title="左上3"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="a12" coords="341,9,367,94" href="javascript:void(0);" title="左上4"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="a13" coords="367,9,394,94" href="javascript:void(0);" title="左上5"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="a14" coords="395,9,428,94" href="javascript:void(0);" title="左上6"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="a15" coords="429,14,460,94" href="javascript:void(0);" title="左上7"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="a16" coords="460,17,497,94" href="javascript:void(0);" title="左上8"
                  onclick="getyachi(this);"/>

            <area shape="rect" id="aa1" coords="9,161,43,241" href="javascript:void(0);" title="右下8"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="aa2" coords="44,160,82,239" href="javascript:void(0);" title="右下7"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="aa3" coords="83,162,122,239" href="javascript:void(0);" title="右下6"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="aa4" coords="123,160,147,241" href="javascript:void(0);" title="右下5"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="aa5" coords="149,160,175,241" href="javascript:void(0);" title="右下4"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="aa6" coords="176,160,205,241" href="javascript:void(0);" title="右下3"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="aa7" coords="205,161,226,241" href="javascript:void(0);" title="右下2"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="aa8" coords="225,162,249,240" href="javascript:void(0);" title="右下1"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="aa9" coords="258,162,281,240" href="javascript:void(0);" title="左下1"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="aa10" coords="281,163,304,239" href="javascript:void(0);" title="左下2"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="aa11" coords="305,162,330,247" href="javascript:void(0);" title="左下3"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="aa12" coords="330,162,355,245" href="javascript:void(0);" title="左下4"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="aa13" coords="354,162,381,245" href="javascript:void(0);" title="左下5"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="aa14" coords="383,160,422,244" href="javascript:void(0);" title="左下6"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="aa15" coords="422,158,459,239" href="javascript:void(0);" title="左下7"
                  onclick="getyachi(this);"/>
            <area shape="rect" id="aa16" coords="458,160,497,237" href="javascript:void(0);" title="左下8"
                  onclick="getyachi(this);"/>
        </map>
        <br/>
        <button id="ycBtn" class="btn2" onclick="ycBtnClick(this);" style="margin-left: 170px;">确定</button>
        <button id="ycBtn" class="btn2" onclick="ycBtnClickCalcle();" style="margin-left: 10px;">取消</button>
    </div>
</div>

<!-- 病人列表 开始-->
<div class="jzdl_1" id="patientList" onmouseover="showPatientList(50,10)" onmouseout="hiddenPatientList(50,10)"
     style="margin-top:17px;display:block;">
    <div class="jzdl_2">
        <table id="patientList_table01" width="215" border="0" cellspacing="0" cellpadding="0" class="jzdl_4">

            <tr>
                <td onclick="">时间:</td>
                <td><input type="text" name="textfield" id="starttime" style="width:50px" class="textbk"/></td>
                <td>-</td>
                <td><input type="text" name="textfield2" id="endtime" style="width:50px" class="textbk"/></td>
                <td><img id="search_img" src="img/top7.jpg" onclick="timeSearch1111()"
                         style="cursor: pointer; margin-top:0px;*margin-top:-1px;margin-left:7px;border:solid #fff 2px;"/>
                </td>
            </tr>
            <!--
            <tr>
                <td>状态：</td>
                <td colspan="3"><div id="isTest" style = "margin-left:0px"></div></td>
                <td><img id="search_img" src="img/top7.jpg" onclick = "topSearch()" style="margin-top:0px;*margin-top:-1px;margin-left:7px;border:solid #fff 2px;"/></td>
            </tr>
               -->
        </table>
        <div style="padding:0px 0px 0px 10px">
            <div id="grid_doctorstation" style="margin-left:-20px;margin-top:5px;width:237px;"></div>
            <div id="pagination" style="margin-top:5px;margin-right:10px;float:right;"></div>
        </div>

    </div>
</div>
<script>
    $("#patientList").css({
        "position": "absolute",
        "top": "" + (document.documentElement.clientHeight - parseInt($("#patientList").css("height"))) / 2 + "px",
        "left": "10px"
    });
</script>

<!-- 病人列表 结束-->

<div class="tj_ysz1">

    <!-- 中间树 -->
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
            <div class="ysz_12">体检项目</div>
            <div class="ysz_14" id="trc">
                <div id="mytree">
                    <ul id="menuTree" class="tree"></ul>
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
    <!-- 中间树 -->

    <div class="floatl luru_3" style="margin-top: 10px;" id="right_all">
        <table width="98%" border="0" cellspacing="0" cellpadding="0" style="margin-bottom:10px;">
            <tr>
                <td style="">
                    <div style="width:100%;">
                        <table width="100%" height="15" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td width="34"><img src="img/tp1.jpg"/></td>
                                <td width="100%" class="bgr1"><span class="font3" id="basinfo">基本信息</span></td>
                                <td width="4"><img src="img/tp3.jpg"/></td>
                            </tr>
                        </table>
                        <div class="bord1">
                            <table width="93%" border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td width="105" rowspan="2">
                                        <div class="xdb14" id="patname"></div>
                                    </td>
                                    <td align="left" width="50">性别：</td>
                                    <td align="left" class="font4" width="50" id="sex"></td>
                                    <td align="left" width="50">年龄：</td>
                                    <td align="left" class="font4" width="50" id="age"></td>
                                    <td align="left" width="75">身份证号：</td>
                                    <td align="left" class="font4" width="150" id="inscardno"></td>
                                    <td align="left" width="75">口味咸淡</td>
                                    <td align="left" class="font4" width="115" id="kwxd"></td>
                                    <td align="left" width="75">体检时间：</td>
                                    <td align="left" class="font4" width="110" id="bdate"></td>
                                    <td align="left" width="75"> 体检类别：</td>
                                    <td align="left" class="font4" width="110" id="examtype"></td>
                                </tr>
                                <tr>
                                    <td align="left" width="50">国籍：</td>
                                    <td align="left" class="font4" width="50" id="guoji"></td>
                                    <td align="left" width="50">民族：</td>
                                    <td align="left" class="font4" width="50" id="minzu"></td>
                                    <td align="left" width="75">宗教信仰：</td>
                                    <td align="left" class="font4" id="zjxy"></td>
                                    <td align="left" width="75">睡&emsp;&emsp;眠</td>
                                    <td align="left" class="font4" id="shuimian"></td>
                                    <td align="left" width="75">电脑工作：</td>
                                    <td align="left" class="font4" width="110" id="dnqgz"></td>
                                    <td align="left" width="75"> 饮食习惯：</td>
                                    <td align="left" class="font4" width="110" id="ysxg"></td>
                                </tr>
                            </table>
                        </div>
                    </div>
                    <table width="100%" height="4" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td width="4"><img src="img/bt1.jpg"/></td>
                            <td width="100%" class="bgr"></td>
                            <td width="4"><img src="img/bt3.jpg"/></td>
                        </tr>
                    </table>
                </td>
                <!--
                <td style="">
                   <div >
                        <button id="saveResultButton" type="button" class="btn2" onclick="saveitemdetils()" disabled>
                            保存
                        </button>
                    </div>
                </td>
                -->
            </tr>
        </table>
        <table width="98%" border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td id="main_text">
                    <table width="100%" height="15" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td width="34"><img src="img/tp1.jpg"/></td>
                            <td width="100%" class="bgr1"><span class="font3">检查结果登记&emsp;&emsp;<button
                                    id="report_tjysz" class="new_btn">危急值上报</button></span></td>
                            <td width="4"><img src="img/tp3.jpg"/></td>
                        </tr>
                    </table>
                    <div id="inddiv" class="bord tj_ysz4" style="width:99.7%; ">
                        <table id="indtable" border="0" cellspacing="1" cellpadding="0" bgcolor="#93afba" class="mar3">
                            <tr bgcolor="#e4edf9" align="center">
                                <td width="80" height="30" class="tj_ysz2">分类</td>
                                <td width="150" class="tj_ysz2" id="ep1">体检项目</td>
                                <td class="tj_ysz2" id="ep2">体检结果</td>
                                <td width="75" class="tj_ysz2" id="ep3">单位</td>
                            </tr>
                            <tr align="center">
                                <td bgcolor="#f6faff" width="80px"><strong id="examtypep"> </strong></td>
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
                                    <div>
                                        <textarea id='LogC' rows='6' wrap="virtual"
                                                  style="overflow-x:auto;overflow-y:auto;BORDER-BOTTOM: 0px solid; BORDER-LEFT: 0px solid; BORDER-RIGHT: 0px solid; BORDER-TOP: 0px solid;"
                                                  onkeydown="enter(this);"> </textarea>
                                    </div>
                                </td>
                            </tr>
                            <tr align="right">
                                <td colspan="3" bgcolor="#f6faff" height="28" align="right">
                                    <table width="100%" height="48" border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <td align="center" width="90"><input id="saveResultButton" type="button"
                                                                                 value="保存" onclick="saveitemdetils()"
                                                                                 disabled="true"/></td>

                                            <td align="center" width="90"><input id="errorMsgButton" type="button"
                                                                                 value="匹配日志" onclick="getErrorMsg()"
                                                                                 disabled="true"/></td>

                                            <td align="center" width="90"><input id="rollBackButton" type="button"
                                                                                 value="清空小结"
                                                                                 onclick="rollbackitemdetils()"
                                                                                 disabled="false"/></td>
                                            <td align="left" id="addcommonresulttd"><input id="addcommonresult"
                                                                                           type="button"
                                                                                           style="margin-right:10px;"
                                                                                           value="新增常见结果"
                                                                                           onclick="addcommonresult()"
                                                                                           disabled="true"/></td>
                                            <td align="left"><input id="afrSugBut" type="button" value="自动生成小结"
                                                                    onclick="afreshSuggest()" disabled="true"
                                                                    onkeydown="enter(this);"/></td>
                                            <c:if test="${isDishDept!='Y'}">
                                                <td align="right" width="45">医生：</td>
                                                <td width="90">
                                                    <div id="comboDoctorName" style="width:85px;float:left;"></div>
                                                    <!-- <script>
                                                       combo_doctorName = new dhtmlXCombo("comboDoctorName","alfa3",85);
                                                   </script>  -->
                                                </td>
                                            </c:if>
                                            <c:if test="${isDishDept=='Y'}">
                                                <td align="right" width="100">医生：${doctorName}</td>
                                            </c:if>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <table width="100%" height="4" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td width="4"><img src="img/bt1.jpg"/></td>
                            <td width="100%" class="bgr"></td>
                            <td width="4"><img src="img/bt3.jpg"/></td>
                        </tr>
                    </table>
                </td>

                <td width="290px;">
                    <div width="100%" style="margin-left:10px;">
                        <table width="100%" height="15" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td width="34"><img src="img/tp1.jpg"/></td>
                                <td width="100%" class="bgr1"><span class="font3">常见结果-<span
                                        style="color:red;cursor:pointer;" onclick="topHigh()">筛选</span></span></td>
                                <td width="4"><img src="img/tp3.jpg"/></td>
                            </tr>
                        </table>
                        <!--
                               <table width="100%" height="4" border="0" cellspacing="0" cellpadding="0" style="">
                                    <tr height="3">
                                       <td width="34"><img src="img/tp1.jpg" /></td>
                                       <td style="background:url(img/tp2.jpg) repeat-x 0px 4px;">
                                           <span class="font3" id="dname">常见结果</span>
                                           <span class="font3"><input style="vertical-align:middle;"
                                               onclick="topHigh()" type="button" value="筛选" class="gjss1"/></span>
                                       </td>
                                       <td width="4"><img src="img/tp3.jpg" /></td>
                                 </tr>
                               </table>  -->
                        <div style="position:absolute;z-index:2">
                            <table id="choose" height="57" border="0" cellspacing="0" cellpadding="0" class="gjss2"
                                   style="display:none;">
                                <tr>
                                    <td width="10"><img src="img/gjss1.png"/></td>
                                    <td background="img/gjss2.png">
                                        <table width="270" border="0" cellspacing="0" cellpadding="0" class="gjss3">
                                            <tr>
                                                <td align="center">常见结果：</td>
                                                <td><input class="textbk" type="text" name="textfield" id="resultname"
                                                           style="width:75px"/></td>
                                                <td height="28"><img id="search_img" src="img/top7.jpg"
                                                                     onclick="timeSearch()"
                                                                     style="margin-top:0px;*margin-top:-1px;margin-left:7px;border:solid #fff 2px;cursor:pointer;"/>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                    <td width="10"><img src="img/gjss3.png"/></td>
                                </tr>
                            </table>
                        </div>
                        <div class="bord" id="results_box">
                            <div id="comResult">
                                <div id="grid_Results" style="width:267px;"></div>
                                <div id="resultsPagination"
                                     style="margin-top:10px;margin-right:10px;float:right;"></div>
                                <div id="result_details">
                                    <textarea id="result_details_text" rows="10" cols="32" readonly="readonly"
                                              class="textbk"></textarea>
                                </div>
                            </div>
                            <div class="jsq1" id="calculator" style="display:none;">
                                <table width="239" border="0" cellspacing="0" cellpadding="0" class="jsq2">
                                    <tr>
                                        <td colspan="7" height="60"><input type="text" name="textfield"
                                                                           id="calculatordisplay" class="jsq3"/></td>
                                    </tr>
                                    <tr>
                                        <td colspan="7" height="8"></td>
                                    </tr>
                                    <tr>
                                        <td height="40" width="49"><img src="images/jsq7.jpg" id="value7"
                                                                        onclick="add(this)"/></td>
                                        <td width="5"></td>
                                        <td width="49"><img src="images/jsq8.jpg" id="value8" onclick="add(this)"/></td>
                                        <td width="5"></td>
                                        <td width="49"><img src="images/jsq9.jpg" id="value9" onclick="add(this)"/></td>
                                        <td width="5"></td>
                                        <td width="73" rowspan="5"><img src="images/jsqac.jpg" id="deleteall"
                                                                        onclick="add(this)"/></td>
                                    </tr>
                                    <tr>
                                        <td height="8"></td>
                                        <td height="8"></td>
                                        <td height="8"></td>
                                        <td height="8"></td>
                                        <td height="8"></td>
                                        <td height="8"></td>
                                    </tr>
                                    <tr>
                                        <td height="40"><img src="images/jsq4.jpg" id="value4" onclick="add(this)"/>
                                        </td>
                                        <td>&nbsp;</td>
                                        <td><img src="images/jsq5.jpg" id="value5" onclick="add(this)"/></td>
                                        <td>&nbsp;</td>
                                        <td><img src="images/jsq6.jpg" id="value6" onclick="add(this)"/></td>
                                        <td>&nbsp;</td>
                                    </tr>
                                    <tr>
                                        <td height="8"></td>
                                        <td height="8"></td>
                                        <td height="8"></td>
                                        <td height="8"></td>
                                        <td height="8"></td>
                                        <td height="8"></td>
                                    </tr>
                                    <tr>
                                        <td height="40"><img src="images/jsq1.jpg" id="value1" onclick="add(this)"/>
                                        </td>
                                        <td>&nbsp;</td>
                                        <td><img src="images/jsq2.jpg" id="value2" onclick="add(this)"/></td>
                                        <td>&nbsp;</td>
                                        <td><img src="images/jsq3.jpg" id="value3" onclick="add(this)"/></td>
                                        <td>&nbsp;</td>
                                    </tr>
                                    <tr>
                                        <td colspan="7" height="8"></td>
                                    </tr>
                                    <tr>
                                        <td height="40" colspan="3"><img id="value0" onclick="add(this)"
                                                                         src="images/jsq0.jpg"/></td>
                                        <td>&nbsp;</td>
                                        <td><img src="images/jsqdot.jpg" id="point" onclick="add(this)"/></td>
                                        <td>&nbsp;</td>
                                        <td><img src="images/jsqdel.jpg" id="deleteone" onclick="add(this)"/></td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                        <table width="100%" height="4" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td width="4"><img src="img/bt1.jpg"/></td>
                                <td width="100%" class="bgr"></td>
                                <td width="4"><img src="img/bt3.jpg"/></td>
                            </tr>
                        </table>
                    </div>
                </td>

            </tr>
        </table>
    </div>
</div>
</body>
</html>