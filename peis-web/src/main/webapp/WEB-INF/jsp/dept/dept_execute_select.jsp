<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<base href="<%=basePath%>" />
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>执行科室选择</title>
		<link href="css/register.css" rel="stylesheet" type="text/css" />
		<link href="css/top.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" href="My97DatePicker/skin/WdatePicker.css" type="text/css" />
		<link rel="stylesheet" type="text/css" href="dhtmlxCombo/codebase/dhtmlxcombo.css" />
		<link rel="stylesheet" type="text/css" href="dhtmlxGrid/codebase/dhtmlxgrid.css" />
		<link rel="stylesheet" type="text/css" href="css/dhtmlxgrid_dhx_custom.css" />
		<link rel="stylesheet" type="text/css" href="css/dictionary.css" />
		<link rel="stylesheet" href="zTreeStyle/zTreeStyle.css" type="text/css" />
		<link rel="stylesheet" href="zTreeStyle/zTreeIcons.css" type="text/css" />
		<style type="text/css">
.box1 {
	width: 350px;
	border: 1px solid #b6cfd6;
	padding: 1px;
	margin: 0 auto;
}

.box2 {
	width: 334px;
	background: url(img/tckbg.gif) repeat-x;
	background-color: #d9eaee;
	padding: 8px;
}

.box3 {
	width: 334px;
	margin: 0 auto;
	padding-bottom: 5px;
}

.box3 span {
	font-size: 13px;
	color: #6ba3b6;
	font-family: "微软雅黑";
	font-weight: bold;
}

.boxpad {
	float: right;
}

.box4 {
	width: 322px;
	border: 1px solid #b6cfd6;
	background-color: #fff;
	padding: 5px;
	font-size: 13px;
	font-family: "微软雅黑";
	line-height: 22px;
}

.text_field {
	margin-left: 4px
}

.text_field_required {
	margin-left: 4px
}

#instype_required .dhx_combo_box {
	border: 1px solid red
}

#sex_required .dhx_combo_box {
	border: 1px solid red
}

.pdl {
	padding-left: 1px
}

.pd2 {
	padding-left: 2px
}

div.gridbox_dhx_custom table.hdr td {
	font-family: 微软雅黑;
	font-size: 12px;
	font-weight: bold;
	vertical-align: top;
}

div.gridbox table.obj.row20px tr td {
	
}
.radioBtn {
	height: 16px;
	vertical-align: middle;
}
.tree li button.chk.checkbox_false_part { background:url("zTreeStyle/img/checkbox.png") no-repeat scroll 0 -24px transparent;}	
.tree li button.chk.checkbox_false_part_focus { background:url("zTreeStyle/img/checkbox.png") no-repeat scroll 0 -12px transparent;}	
.tree li button.chk.checkbox_true_part { background:url("zTreeStyle/img/checkbox.png") no-repeat scroll 0 -24px transparent;}	
.tree li button.chk.checkbox_true_part_focus { background:url("zTreeStyle/img/checkbox.png") no-repeat scroll 0 -36px transparent;}	
#scopeDiv1{ height:32px; padding: 0px; margin-top: 0px; margin-left: 5px; margin-top: 10px;}
#scopeDiv1 ul{ list-style: none;}
#scopeDiv1 ul li{width:64px; height:27px; text-align:center; line-height:26px; background:url(img/ss.gif) no-repeat left 1px; border: 0 none;}
</style>
		<script type="text/javascript" src="js/jquery-1.6.1.js"></script>
		<script type="text/javascript" src="js/jquery.lrTool.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/clc/comboTool.js"></script>
		<script>window.dhx_globalImgPath = "dhtmlxCombo/codebase/imgs/";</script>
		<script type="text/javascript"
			src="dhtmlxCombo/codebase/dhtmlxcommon.js"></script>
		<script type="text/javascript"
			src="dhtmlxCombo/codebase/dhtmlxcombo.js"></script>
		<script type="text/javascript"
			src="dhtmlxCombo/codebase/ext/dhtmlxcombo_whp.js"></script>
		<script type="text/javascript" src='My97DatePicker/WdatePicker.js'></script>

		<script type="text/javascript" src="js/dhtmlxgrid.js"></script>
		<script type="text/javascript" src="js/dhtmlxgridcell.js"></script>
		<script type="text/javascript" src="js/jquery.ztree-2.6.js"></script>
		<script type="text/javascript" src="js/dept/verify.js"></script>
		<script type="text/javascript">
		$(document).ready(function(){
		});
    //树的参数
var setting = {   
	isSimpleData : true,
	showLine: true,       
	treeNodeKey : "id",                
	treeNodeParentKey : "pId",
	addDiyDom: addDiyDom,
	fontCss: setFontCss,                  
	expandSpeed : "fast",//展开速度
	callback:{
		beforeClick: zTreeOnBeforeClick,
		beforeExpand: zTreeOnBeforeExpand,
		expand: zTreeOnExpand
	} 
};  
 
var zTree;   
var treeNodes;  
var IDMark_A = "_a"; 
var curExpandNode = null;
$(function(){ 
    $.ajax({   
        async : false,   //是否异步
        cache:false,   //是否使用缓存
        type: 'post',   //请求方式,post
        dataType : "json",   //数据传输格式
       	//data: $("#archive_form").serialize() 表单提交数据
       	data: "hosnum=${hosnum}",  //url传数据
        url: "dept/dept_execute_tree.htm",   //请求链接
        error: function () {  
            alert('fail'); 
        },   
        success:function(data){     
            treeNodes = data; 
        }   
    });
    
    zTree = $("#menuTree").zTree(setting, treeNodes);   //前台树的位置
    
    var checkedBrand = getCheckedRadio("radio_self_a");
	if(checkedBrand){
		var checkedNode = zTree.getNodeByParam("id", parseInt(checkedBrand.attr("id").replace("radio_", "")));
		if(checkedNode.parentNode){
			zTree.expandNode(checkedNode.parentNode, true, false);//展开节点
			curExpandNode = checkedNode.parentNode;//记录当前展开位置
			//addBrandName(getNoRadioNode(checkedNode));//更改对应上级名称
		}
	}
});  
	
	//设置字体颜色
	function setFontCss(treeId, treeNode) {
		if (!treeNode.isLast && treeNode.oldName && treeNode.oldName != treeNode.name) {
			//return {"color":"#2C8A24"};
			return {"color":"#000000"};
		} else {
			return {"color":"#000000"};
		}
	}
	
	//点击之前做的事情
	function zTreeOnBeforeClick(treeId, treeNode) {
		if (treeNode.isLast) {
			$("#radio_" + treeNode.id).attr("checked", true);
			//addBrandName(getNoRadioNode(treeNode));//更改对应上级名称
		}else if(!treeNode.open){
			singlePath(treeNode);//关闭同级的节点
			zTree.expandNode(treeNode, true, false);//展开节点
		}
		return false;
	}
	
	//展开节点时，记录对应展开节点
	function zTreeOnExpand(event, treeId, treeNode) {
		curExpandNode = treeNode;
	}
	
	//展开节点前，关闭同级别下另外展开的
	function zTreeOnBeforeExpand(treeId, treeNode) {
		singlePath(treeNode);
	}
	
	//只展开一个节点
	function singlePath(newNode) {
		if (curExpandNode && curExpandNode.open==true) {
			if (newNode.level === curExpandNode.level) {//同一级别
				zTree.expandNode(curExpandNode, false, true);
			} else if(newNode.level < curExpandNode.level){//新选择的级别高
				var oldNode = curExpandNode;
				while(oldNode){
					if(oldNode.level == newNode.level){
						zTree.expandNode(oldNode, false, true);
						break;
					}
					oldNode = oldNode.parentNode;
				}
			}else{//原来选择的级别高
				var parentNewNode = newNode;
				while (parentNewNode) {
					if (parentNewNode.level === curExpandNode.level) {
						if(parentNewNode !== curExpandNode){
							zTree.expandNode(oldNode, false, true);
						}
						break;
					}
					parentNewNode = parentNewNode.parentNode;
				}
			}
		}
		curExpandNode = newNode;
	}
	
	//自定义radio节点
	function addDiyDom(treeId, treeNode) {
		var aObj = $("#" + treeNode.tId + IDMark_A);
		var deptCode = "${deptcode}";
		if (treeNode.isLast) {//存在这个值
			var editStr = "<input type='radio' class='radioBtn' id='radio_" +treeNode.id
				+ "' name='radio_self_a' onfocus='this.blur();' ";
			if(deptCode == treeNode.id){
				editStr = editStr + "checked=true";
			}
			editStr = editStr + "></input>"
			aObj.before(editStr);
			/*var btn = $("#radio_"+treeNode.id);
			if (btn){ 
				btn.bind("click", function() {
					reBackName();
					addBrandName(getNoRadioNode(treeNode));
				});
			}*/
		}
	}
	
	//得到没有radio的上级节点
	function getNoRadioNode(treeNode){
		while(treeNode.isLast){
			treeNode = treeNode.parentNode;
		}
		return treeNode;
	}
	
	//更改没有radio的上级节点的名称
	function addBrandName(treeNode) {
		if (!treeNode.oldName) {
			treeNode.oldName = treeNode.name;
		}
		var brandName = "";
		var checkedBrand = getCheckedRadio("radio_self_a");
		var brandNode = zTree.getNodeByParam("id", parseInt(checkedBrand.attr("id").replace("radio_", "")));
		if (brandNode) brandName = " - " + brandNode.name;
		treeNode.name = treeNode.oldName + brandName;
		zTree.updateNode(treeNode);
	}
	
	//还原上级变更的名称
	function reBackName(){
		var nodes = zTree.transformToArray(zTree.getNodes());
		for(var i = 0; i < nodes.length; i ++){
			if(!nodes[i].isLast && nodes[i].oldName && nodes[i].oldName != nodes[i].name){
				nodes[i].name = nodes[i].oldName;
				zTree.updateNode(nodes[i]);
			}
		}
	}
		
	//得到选中的radio节点
	function getCheckedRadio(radioName) {  
		var r = document.getElementsByName(radioName);
		for(var i=0; i<r.length; i++)    { 
			if(r[i].checked)    { 
				return $(r[i]); 
			} 
		}         
	    return null;       
	}
	
	//确认选择操作
	function doSave(){
		var checkedBrand = getCheckedRadio("radio_self_a");
		var deptcode = "";
		var deptname = "";
		if(checkedBrand){
			var checkedNode = zTree.getNodeByParam("id", parseInt(checkedBrand.attr("id").replace("radio_", "")));
			deptcode = checkedNode.id;
			if(checkedNode.yqName){
				deptname = checkedNode.name + "（"+checkedNode.yqName+"）";
			}else{
				deptname = checkedNode.name;
			}
		}else{
			alert("请选择一个执行科室");
		}
		
		window.returnValue = deptcode + "#_#" + deptname;
		winClose();
	}
	
	//关闭窗口
	function winClose(){
		window.close();
	}
</script>
	</head>

	<body>
		<div class="box1">
			<div class="box2">
				<div class="box3">
					<span>执行科室选择</span>
				</div>
				<div class="box4" style="height: 413px">

					<form id="dept_scope_form" name="dept_scope_form"
						onsubmit="return false">
						<input id="hosnum" name="hosnum" type="hidden"
							value="${scope.hosnum}" />
						<input id="deptcode" name="deptcode" type="hidden"
							value="${scope.deptcode}" />
						<input id="scopetype" name="scopetype" type="hidden"
							value="${scope.scopetype}" />
						<input id="addscopes" name="addscopes" type="hidden" />
						<input id="removescopes" name="removescopes" type="hidden" />
					</form>
					<!-- 圆头******开始 -->
					<table width="98%" border="0" cellspacing="0" cellpadding="0"
						style="margin-top: 0px;">
						<tr>
							<td width="10"><img src="img/new_yuan1.jpg" /></td>
							<td background="img/new_yuan2.jpg"></td>
							<td width="10"><img src="img/new_yuan3.jpg" /></td>
						</tr>
						<tr>
							<td background="img/new_yuan4.jpg">
							</td>
							<td>
						<!-- 圆头******结束 -->
						
								<div id="tree_div"
									style="OVERFLOW-y: auto; overflow-x: hidden;width: 300px; height: 330px; margin-top: 0px; margin-bottom: -1px; border-top: 0px; border-bottom: 0px;">
									<ul id="menuTree" class="tree" ></ul>
									<!--					<input type="button" onclick="doScopeSave();" value="保存" />-->
								</div>
						
						<!-- 圆尾******开始 -->
							</td>
							<td background="img/new_yuan5.jpg">
								&nbsp;
							</td>
						</tr>
						<tr>
							<td><img src="img/new_yuan6.jpg" /></td>
							<td background="img/new_yuan7.jpg"></td>
							<td><img src="img/new_yuan8.jpg" /></td>
						</tr>
					</table>
					<!-- 圆尾******结束 -->
					
					<div id="scopeDiv1">
						<ul>
							<li id="scopeButton" style="cursor: pointer; float: left; margin-left: 90px;" onclick="doSave();">
								确定
							</li>
							<li id="scopeButton" style="cursor: pointer; float: left; margin-left: 10px;" onclick="winClose();">
								取消
							</li>
						</ul>
					</div>
					
					<input type="hidden" id="pos_x" />
					<input type="hidden" id="pos_y" />
				</div>
			</div>
		</div>
	</body>
</html>