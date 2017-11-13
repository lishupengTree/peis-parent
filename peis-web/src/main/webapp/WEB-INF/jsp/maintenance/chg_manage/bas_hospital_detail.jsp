<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ include file="../../include.jsp" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <base href="<%=basePath%>"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="pragma" content="no-cache"/>
    <meta http-equiv="cache-control" content="no-cache"/>
    <meta http-equiv="expires" content="0"/>
    <title>卫生站详细信息</title>
    <link href="css/top.css" rel="stylesheet" type="text/css"/>
    <link href="css/register.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxCombo/codebase/dhtmlxcombo.css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxGrid/codebase/dhtmlxgrid.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid_dhx_custom.css"/>
    <link rel="stylesheet" type="text/css" href="css/dictionary.css"/>

    <script>window.dhx_globalImgPath = "dhtmlxCombo/codebase/imgs/";</script>
    <script type="text/javascript" src="js/jquery-1.6.1.js"></script>
    <script type="text/javascript" src="js/util.js"></script>
    <script type="text/javascript" src="js/dhtmlxcommon.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcombo.js"></script>
    <script type="text/javascript" src="js/gl/util.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/ext/dhtmlxcombo_whp.js"></script>
    <script type="text/javascript" src="js/clc/comboTool.js"></script>
    <script type="text/javascript" src="js/gl/dhtmlxgrid.js"></script>
    <script type="text/javascript" src="js/gl/dhtmlxgridcell.js"></script>
    <script type="text/javascript" src="js/gl/dhtmlxgrid_excell_combo.js"></script>
    <script type="text/javascript" src="js/maintenance/maintenance_tool.js"></script>
    <script type="text/javascript" src="js/dept/verify.js"></script>
    <script type="text/javascript" src="js/maintenance/system_manage/bas_hospital_detail.js"></script>
    <style type="text/css">
        body {
            margin-top: 10px;
        }

        .qdiv {
            width: 630px;
        }

        td {
            font-size: 13px;
        }

        .txt {
            height: 18px;
            vertical-align: middle;
            border: 1px solid #93AFBA;
        }

        .txtarea {
            border: 1px solid #93AFBA;
        }

        p {
            line-height: 17px;
            text-align: right;
        }

        .show_img {
            border: 1px solid #93AFBA;
            width: 98px;
            height: 98px;
            margin-top: 2px;
        }

        .btn {
            background: url(img/btn.jpg) no-repeat;
            width: 92px;
            height: 32px;
            text-align: center;
            border: 0;
            line-height: 32px;
            font-size: 14px;
            font-family: Microsoft YaHei, Lucida Grande, Helvetica, Tahoma, Arial, sans-serif;
            color: #000;
            cursor: pointer;
            margin: 5px 20px 0 0;
        }

        .show_select_tree {
            height: 21px;
            width: 39px;
            background: url("img/xz.gif");
            cursor: pointer;
            margin-left: 3px;
        }

        .float_left {
            float: left;
        }

        .msg {
            font-size: 12px;
            color: red;
        }

        .not_editable {
            background-color: #eaeaea
        }

        .red_line {
            border: 1px solid red;
        }

        ;
        .font3 {
            font-size: 12px;
            color: #44839a;
            font-weight: bold;
            background: #fff;
        }
    </style>
    <script type="text/javascript">
        function doClose() {
            $("#hidden_iframe").attr("src", "");
            try {
                $("div")[0].focus();
            } catch (e) {
            }
            parent.$.unblockUI();
        }

    </script>
</head>
<body>
<!-- frist div begin -->
<div class="qdiv">
    <form id="the_form" name="the_form" onsubmit="return false">
        <input type="hidden" name="supunit" id="supunit" value="${param.supunit}"/>
        <input type="hidden" name="showtype" id="showtype" value="${param.showtype}"/>
        <input type="hidden" name="oldhosnum" id="oldhosnum" value="${bh.hosnum}"/>
        <input type="hidden" name="oldnodecode" id="oldnodecode" value="${bh.nodecode}"/>
        <table width="100%" cellspacing="0" cellpadding="0">
            <tr>
                <td width="10"><img src="img/new_yuan1.jpg"/></td>
                <td width="100%" background="img/new_yuan2.jpg"><img src="img/new_tp1.jpg"/><span class="font3"
                                                                                                  style="position:relative;top:-2px;">卫生站信息</span>
                </td>
                <td width="10"><img src="img/new_yuan3.jpg"/></td>
            </tr>
            <tr>
                <td background="img/new_yuan4.jpg">&nbsp;</td>
                <td>
                    <!-- parent info begin-->
                    <table id="p_table" width="100%" border="0" cellspacing="5" cellpadding="0">
                        <tr>
                            <td width="85" align="right">名&emsp;&emsp;称：</td>
                            <td align="left"><input class="txt red_line" name="hosname" type="text" id="hosname"
                                                    style="width:100px" value="${bh.hosname}"/></td>
                            <td width="80" align="right">简&emsp;&emsp;称：</td>
                            <td align="left"><input class="txt red_line" name="shortname" type="text" id="shortname"
                                                    maxlength="10" style="width:100px" value="${bh.shortname}"/></td>
                            <td width="80" align="right">医院编码：</td>
                            <td width="110" align="left">
                                <input maxlength="4"
                                       class="txt red_line <c:if test="${bh.nodetype=='院区'}">not_editable</c:if>
							      " name="hosnum" type="text" id="hosnum" style="width:100px"
                                       <c:if test="${bh.nodetype=='院区'}">readonly='readonly'</c:if>
                                       value="${bh.hosnum}"/>
                            </td>
                        </tr>
                        <tr>
                            <td width="85" align="right">上级单位：</td>
                            <td colspan="3" align="left"><input class="txt not_editable" name="parentname" type="text"
                                                                id="parentname" readonly="readonly" style="width:310px"
                                                                value="${parentname}"/></td>
                            <td width="80" align="right">节点编码：</td>
                            <td width="110" align="left"><input
                                    class="txt red_line <c:if test="${bh.nodetype=='医院'}">not_editable</c:if>
							    " name="nodecode" type="text" id="nodecode" style="width:100px" maxlength="4"
                                    <c:if test="${bh.nodetype=='医院'}">readonly='readonly'</c:if>
                                    value="${bh.nodecode}"/></td>
                        </tr>
                        <tr>
                            <td width="85" align="right">节点类别：</td>
                            <td width="102" align="left" id="nodetype_pos">
                                <select style="width:102px" name="nodetype" id="nodetype">
                                    <option value="${bh.nodetype}">${bh.nodetype}</option>
                                </select>
                            </td>
                            <td align="right">机构分类：</td>
                            <td align="left" id="orgtype_pos">
                                <select style="width:102px" name="orgtype_t" id="orgtype">
                                    <option value="1">${bh.orgtype}</option>
                                </select>
                                <input type="hidden" id="orgtype" name="orgtype" value="${bh.orgtype}"/>
                            </td>
                            <td align="right">行政区划：</td>
                            <td width="102" align="left">
                                <select style="width:102px" name="distcode" id="distcode">
                                    <option value="${bh.distcode}">${distname}</option>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <td width="85" align="right">医院级别：</td>
                            <td width="102" align="left" id="hosdegree_pos">
                                <select style="width:102px" name="hosdegree" id="hosdegree">
                                    <option value="${bh.hosdegree}">${bh.hosdname}</option>
                                </select>
                                <input type="hidden" id="hosdname" name="hosdname" value="${bh.hosdname}"/>
                            </td>
                            <td align="right">级别等级：</td>
                            <td align="left" id="degreelevel_pos">
                                <select style="width:102px" name="degreelevel" id="degreelevel">
                                    <option value="${bh.degreelevel}">${bh.degreelname}</option>
                                </select>
                                <input type="hidden" id="degreelname" name="degreelname" value="${bh.degreelname}"/>
                            </td>
                            <td align="right">全院人数：</td>
                            <td align="left"><input class="txt" name="empnumber" type="text" id="empnumber"
                                                    style="width:100px" value="${bh.empnumber}"/></td>
                        </tr>
                        <tr>
                            <td width="85" align="right">床&ensp;位&ensp;数：</td>
                            <td width="120" align="left"><input class="txt" name="beds" type="text" id="beds"
                                                                style="width:100px" value="${bh.beds}"/></td>
                            <td width="80" align="right">医&ensp;生&ensp;数：</td>
                            <td align="left"><input class="txt" name="doctors" type="text" id="doctors"
                                                    style="width:100px" value="${bh.doctors}"/></td>
                            <td width="80" align="right">护&ensp;士&ensp;数：</td>
                            <td width="110" align="left"><input class="txt" name="nurses" type="text" id="nurses"
                                                                style="width:100px" value="${bh.nurses}"/></td>
                        </tr>
                        <tr>
                            <td width="85" align="right">地&emsp;&emsp;址：</td>
                            <td colspan="3" align="left"><input class="txt" name="address" type="text" id="address"
                                                                style="width:310px" value="${bh.address}"/></td>
                            <td align="right">电&emsp;&emsp;话：</td>
                            <td align="left"><input class="txt" name="tel" type="text" id="tel" style="width:100px"
                                                    value="${bh.tel}"/></td>
                        </tr>
                        <tr>
                            <td width="85" align="right">医保编号：</td>
                            <td width="120" align="left"><input class="txt" name="ycentercode" type="text"
                                                                id="ycentercode" style="width:100px"
                                                                value="${bh.ycentercode}"/></td>
                            <td width="80" align="right">农保编号：</td>
                            <td align="left"><input class="txt" name="ncentercode" type="text" id="ncentercode"
                                                    style="width:100px" value="${bh.ncentercode}"/></td>
                            <td width="80" align="right">排&ensp;序&ensp;号：</td>
                            <td width="110" align="left"><input class="txt" name="sn" type="text" id="sn"
                                                                style="width:100px" value="${bh.sn}"/></td>
                        </tr>
                        <tr>
                            <td width="85" align="right">简&emsp;&emsp;介：</td>
                            <td colspan="5" align="left">
                                <textarea name="introduction" rows="3" class="txtarea" id="introduction"
                                          style="width:505px">${bh.introduction}</textarea>
                            </td>
                        </tr>
                    </table>

                </td>
                <td background="img/new_yuan5.jpg">&nbsp;</td>
            </tr>
            <tr>
                <td><img src="img/new_yuan60.jpg"/></td>
                <td background="img/new_yuan70.jpg"></td>
                <td><img src="img/new_yuan80.jpg"/></td>
            </tr>
        </table>
    </form>
</div>
<!-- frist div end -->
<!-- cancel begin-->
<div class="qdiv" style="margin-top:10px;">
    <div style="float:left;width:170px">
        <span id="msg" class="msg">&nbsp;</span>
    </div>
    <div id="save" class="btn" style="float:left;margin-left:40px;" onclick="dosave()">保存</div>
    <div id="close" class="btn" style="float:left" onclick="doClose()">关闭</div>
</div>
<!-- cancel end-->
</body>

</html>
