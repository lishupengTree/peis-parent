<%@ page language="java" import="java.util.*,java.net.URLDecoder" pageEncoding="UTF-8" %>
<%@ include file="../../include.jsp" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <base href="<%=basePath%>">
    <title>菜单项详细信息</title>
    <meta http-equiv="pragma" content="no-cache">

    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <link rel="stylesheet" href="js/uploadify/uploadify.css" type="text/css" />
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid_dhx_custom.css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxCombo/codebase/dhtmlxcombo.css"/>

    <script>window.dhx_globalImgPath = "dhtmlxCombo/codebase/imgs/";</script>
    <script type="text/javascript" src="js/dhtmlxcommon.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcombo.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/ext/dhtmlxcombo_whp.js"></script>

    <script type="text/javascript" src="js/jquery-1.6.1.js"></script>
    <script type="text/javascript" src="js/uploadify/swfobject.js"></script>
    <script type="text/javascript" src="js/uploadify/jquery.uploadify.v2.1.4.js"></script>
    <script type="text/javascript" src="js/util.js"></script>
    <style type="text/css">
        .big_div {
            margin: 10px;
        }

        td {
            font-size: 13px;
        }

        .txt {
            height: 22px;
            vertical-align: middle;
            border: 1px solid #93AFBA;
            line-height: 22px;
        }

        p {
            line-height: 17px;
            text-align: right;
        }

        .check_box {

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
            padding-top: 7px;
        }
    </style>
    <script type="text/javascript">
        var menu_type_combo;   //菜单类型下拉
        var hotkeys_combo;		//热键下拉
        var open_type_combo;   //打开类型下拉

        $(document).ready(function () {
            $('#name').focus();

            menu_type_combo = new dhtmlXCombo("menu_type", "menu_type", 100);
            hotkeys_combo = new dhtmlXCombo("hotkeys", "hotkeys", 45);
            open_type_combo = new dhtmlXCombo("open_type", "open_type", 100);
            /*-- 保存 按钮 ---BEGIN---------------*/
            $('#save').click(function () {
                //验证

                var showtype = $('#showtype').val();
                var id = $('#id').val();
                var pid = $('#pid').val();
                var name = $('#name').val();
                var url = $('#url').val();
                var image = $('#image').val();
                var menu_type = menu_type_combo.getSelectedValue();
                var open_type = open_type_combo.getSelectedValue();
                var hotkeys = hotkeys_combo.getSelectedValue();

                var index_no = $('#index_no').val();
                var default_open = "Y";//是否默认打开
                if ($('#default_open').attr("checked") == "checked") {
                    default_open = "Y";
                } else {
                    default_open = "N";
                }

                if (id == null || id == "") {
                    alert("ID不能为空!");
                    return false;
                }
                if (!index_no.match("^\\d+$")) {
                    alert("索引只能为数字!");
                    return false;
                }
                /*
                 if(name == null || name == ""){
                 alert("名称不能为空!");
                 return false;
                 }
                 */

                $.ajax({
                    url: "maintenance/add_system_menu.htm",
                    type: "get",
                    async: false,
                    data: "showtype=" + showtype + "&id=" + id + "&pid=" + pid + "&name=" + encodeURI(encodeURI(name))
                    + "&url=" + url + "&image=" + image + "&menu_type=" + encodeURI(encodeURI(menu_type))
                    + "&open_type=" + open_type + "&default_open=" + default_open + "&index_no=" + index_no + "&hotkeys=" + hotkeys,
                    success: function (msg) {
                        //alert(msg);
                        if ("添加成功!" == msg) {
                            $('#showtype').val(0);
                            $('#msg').html("已添加!");
                            var node = "{id:\"" + id + "\", pid:\"" + pid + "\", name:\"" + name + "\",index_no:\"" + index_no + "\",menu_type:\"" + menu_type + "\"}";
                            parent.window.mywin.addTreeNode(pid, eval("(" + node + ")"));

                        } else if ("更新成功!" == msg) {
                            $('#msg').html("已更新!");
                            var node = "{id:\"" + id + "\", pid:\"" + pid + "\", name:\"" + name + "\",index_no:\"" + index_no + "\",menu_type:\"" + menu_type + "\"}";
                            parent.window.mywin.updateThisGrid(eval("(" + node + ")"));
                        }
                    }
                });
            });
            /*-- 保存按钮 ---END-------------*/

            //重写partent关闭层
            parent.doClose = function () {
                parent.$.unblockUI();
            };

        });
    </script>
</head>
<body style="overflow:hidden">
<div class="big_div">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td width="70" height="28" align="right">编号：</td>
            <td colspan="3"><input type="text" style="width:140px" id="id" readonly="readonly" class=" txt not_editable"
                                   value="${id}"/>
            </td>
            <td width="70" align="right">索引：</td>
            <td colspan="2"><input type="text" style="width:100px" id="index_no" class="txt" value="${param.index_no}"/>
            </td>
        </tr>
        <tr align="right">
            <td height="28" align="right">父节点：</td>
            <td colspan="3" align="left"><input type="text" style="width:140px" id="pid" readonly="readonly"
                                                class=" txt not_editable" value="${param.pid}"/></td>
            <td height="20" align="right">菜单类型：</td>
            <td colspan="2" align="left">
                <select id="menu_type" style="width:100px">
                    <option value="">无</option>
                    <option value="子系统" <c:if test="${menu_type=='子系统'}">selected="selected"</c:if>>子系统</option>
                    <option value="模块" <c:if test="${menu_type=='模块'}">selected="selected"</c:if>>模块</option>
                    <option value="功能" <c:if test="${menu_type=='功能'}">selected="selected"</c:if>>功能</option>
                </select>
            </td>
        </tr>
        <tr>
            <td height="28" align="right">名称：</td>
            <td colspan="3"><input type="text" style="width:140px" id="name" class="txt" value="${name}"/></td>
            <td align="right">打开类型：</td>
            <td colspan="2" align="left">
                <select id="open_type" style="width:100px">
                    <option value="">无</option>
                    <option value="0" <c:if test="${param.open_type=='0'}">selected="selected"</c:if>>右边窗口</option>
                    <option value="1" <c:if test="${param.open_type=='1'}">selected="selected"</c:if>>当前窗口</option>
                    <option value="2" <c:if test="${param.open_type=='2'}">selected="selected"</c:if>>弹出窗口</option>
                </select>
            </td>
        </tr>
        <tr>
            <td height="28" align="right">热键：</td>
            <td width="45">
                <select id="hotkeys" style="width:45px">
                    <option value="">无</option>
                    <option value="F2" <c:if test="${param.hotkeys=='F2'}">selected="selected"</c:if>>F2</option>
                    <option value="F3" <c:if test="${param.hotkeys=='F3'}">selected="selected"</c:if>>F3</option>
                    <option value="F4" <c:if test="${param.hotkeys=='F4'}">selected="selected"</c:if>>F4</option>
                    <option value="F5" <c:if test="${param.hotkeys=='F5'}">selected="selected"</c:if>>F5</option>
                    <option value="F6" <c:if test="${param.hotkeys=='F6'}">selected="selected"</c:if>>F6</option>
                    <option value="F7" <c:if test="${param.hotkeys=='F7'}">selected="selected"</c:if>>F7</option>
                    <option value="F8" <c:if test="${param.hotkeys=='F8'}">selected="selected"</c:if>>F8</option>
                </select>
            </td>
            <td width="70" align="right">默认打开：</td>
            <td width="28"><input type="checkbox" id="default_open"
                                  <c:if test="${param.default_open == 'Y'}">checked=true</c:if>/>
            </td>
            <td align="right">图片预览：</td>
            <td colspan="2" rowspan="3">
                <img id="show_img" class="show_img" src="${param.image}" onerror="javascript:this.src='img/noImg.png'"/>
            </td>
        </tr>
        <tr>
            <td height="33" align="right" nowrap>链接：</td>
            <td colspan="4"><input type="text" style="width:200px" id="url" class="txt" value="${param.url}"/></td>
        </tr>
        <tr>
            <td height="24" align="right" nowrap>图片链接：</td>
            <td colspan="4"><input type="text" style="width:200px" id="image" class="txt" value="${param.image}"/></td>
        </tr>
        <tr>
            <td align="center" height="45"><span id="msg" class="msg"></span></td>
            <td colspan="4">
                <div id="save" class="btn">保存</div>
            </td>
            <td width="100">
                <div id="fileQueue" style="display:none"></div>
                <input style="width:100px" type="file" name="uploadify" id="uploadify"/>
            </td>
            <td width="9">&nbsp;</td>
        </tr>
    </table>
</div>
<input type="hidden" id="showtype" value="${param.showtype}"/>
</body>
<script type="text/javascript">
    //---图片上传----BEGIN-------------
    $(document).ready(function () {
        $("#uploadify").uploadify({
            'uploader': 'js/uploadify/uploadify.swf',
            'script': 'system_menu_img_upload.htm',
            'cancelImg': 'js/uploadify/cancel.png',
            'queueID': 'fileQueue',
            'auto': true,
            'multi': false,
            'buttonImg': 'img/update_tp.gif',
            'wmode': 'transparent',
            'fileDesc': '支持格式:jpg/gif/jpeg/png/bmp',
            'fileExt': '*.jpg;*.bmp;*.png;*.gif;*.jpeg',
            'onComplete': function (event, queueID, fileObj, response, data) {
                $('#show_img').attr("src", response);
                $('#image').val(response);
                //alert("图片上传成功!");
            }
        });
    });
    //---图片上传----END---------------
</script>
</html>
