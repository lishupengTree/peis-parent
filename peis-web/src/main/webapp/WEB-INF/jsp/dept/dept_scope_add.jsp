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
		<title>服务范围选择</title>
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
    //树的参数
var setting = {   
     isSimpleData : true,       
     treeNodeKey : "id",                
     treeNodeParentKey : "pId",
     checkable: true,
   //用户自定义checked列
   //checkedCol: "checked",
   //级联选择父节点p；级联选择子节点s
	checkType: {"Y":"ps", "N":"ps"},
  //checkStyle: "radio",//"checkbox"
    showLine: true,                  
    expandSpeed : "fast"//展开速度
  //callback:{
  // click: zTreeOnClick //点击事件
  //} 
};  
 
var zTree;   
var treeNodes;   
$(function(){ 
    $.ajax({   
        async : false,   //是否异步
        cache:false,   //是否使用缓存
        type: 'get',   //请求方式,post
        dataType : "json",   //数据传输格式
       	//data: $("#archive_form").serialize() 表单提交数据
       	//data:"method=loadAllColumnEname&code="+str  url传数据
        url: "dept/dept_scope_tree.htm?hosnum=${scope.hosnum}&yqCode=${yqCode}&yqName="+encodeURI(encodeURI("${yqName}"))
        	+"&checkedScope=${checkedScope}",   //请求链接
        error: function () {  
            alert('fail'); 
        },   
        success:function(data){     
            treeNodes = data; 
        }   
    });
    
    zTree = $("#menuTree").zTree(setting, treeNodes);   //前台树的位置
});  


	
	function doScopeAdded(){
		showMsg("数据保存中...");
		$.ajax({
			cache:false,   //是否使用缓存
            url: "dept/dept_scope_added.htm", 
            async : false,   //是否异步，false为同步
            type:"post",
            data:$('#dept_form').serialize(),
            error:function(){
            	alert("ajax请求失败");
            },
            success:function(reply){ 
             	 if(reply == "success"){
             	 	closeMsg();
             	 	alert("保存成功");
             	 }else if(reply == "fail"){
             	 	closeMsg();
             	 	alert("保存失败");
             	 }
            } 
		});
	}
	
	function doScopeSave(){
		var changeNodes = zTree.getChangeCheckedNodes();
		var len = changeNodes.length;
		var addScope = "";
		var removeScope = "";
		var checkedNodes = zTree.getCheckedNodes();
		var hidCodes = "";
		var showNames = "";
		
		if(len >= 1){
			for(var i = 0; i < len; i ++){
				if(changeNodes[i].checked){
					if(changeNodes[i].isLast == "Y"){//只添加叶子节点
					//if(changeNodes[i].isLast){//不是根节点
						addScope = addScope + changeNodes[i].id + ";";
					}
				}else{
					removeScope = removeScope + changeNodes[i].id + ";";
				}
			}
			
			for(var i = 0; i < checkedNodes.length; i ++){
				if(checkedNodes[i].isLast == "Y"){//只添加叶子节点
					showNames = showNames + checkedNodes[i].name + "；";
				}
				hidCodes = hidCodes + checkedNodes[i].id + ";";
			}
		}
		
		if(addScope != "" || removeScope != ""){
			//if(!window.confirm("是否确定保存变更的范围？")){
			//	return;
			//}
			$("#addscopes").val(addScope);
			$("#removescopes").val(removeScope);
			//$("#scopNames").val(showNames);
			$("#scopNames").val($("#scopetype").val());//update_0424
			$.ajax({
				cache:false,   //是否使用缓存
	            url: "dept/dept_scope_added.htm", 
	            async : false,   //是否异步，false为同步
	            type:"post",
	            data:$('#dept_scope_form').serialize(),
	            error:function(){
	            	alert("ajax请求失败");
	            },
	            success:function(reply){ 
	             	 if(reply == "success"){
	             	 	alert("保存成功");
	             	 	//parent.$("#checkedNames").val(showNames);
	             	 	//parent.$("#checkedCodes").val(hidCodes);
	             	 	window.dialogArguments.document.getElementById("checkedNames").value=showNames;
	             	 	window.dialogArguments.document.getElementById("checkedCodes").value=hidCodes;
	             	 	window.close();
	             	 	if(hidCodes == ""){//修改末级标记
							changeDisable("isleafRad", false);
	             	 	}else{
	             	 		changeDisable("isleafRad", true);
	             	 	}
	             	 	//parent.$.unblockUI();
						//parent.$("#hidden_iframe").attr("src","");
	             	 }else if(reply == "fail"){
	             	 	alert("保存失败");
	             	 }
	            } 
			});
		}else{
			//parent.$.unblockUI();
			//parent.$("#hidden_iframe").attr("src","");
			alert("范围没有变动");
		}
		//checkAllNodes();
		//var tmp = zTree.getCheckedNodes();
		//var tmp4 = zTree.getSelectedNode();
		//tmp4.checked = true;
		//zTree.updateNode(tmp4, true);
		//var tmp2 = zTree.getNodeByParam("id","1002");
		//alert(tmp2.name);
		//var tmp2 = zTree.getNodeByTId("menuTree_3");
		//alert(tmp2.checkedOld);
		//alert(zTree.transformToArray(tmp2)[0].name);
		//var tmp3 = zTree.getNodes();
		//alert(zTree.transformTozTreeNodes(tmp3))
	}
	
	function changeDisable(name, val){
		var objs = window.dialogArguments.document.getElementsByName(name);
		if(objs){
			for(var i = 0; i < objs.length; i ++){
				objs[i].disabled = val;
			}
		}
	}
	
	function winClose(){
		window.parent.close();
	}
</script>
	</head>

	<body>
		<div class="box1">
			<div class="box2">
				<div class="box3">
					<span>服务范围选择</span>
<!--					<span class="boxpad"><img id="close_iframe_img"-->
<!--							src="img/close.gif" align="middle" onclick="doClose()" />-->
<!--					</span>-->
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
						<input id="scopNames" name="scopNames" type="hidden"/>
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
							<li id="scopeButton" style="cursor: pointer; float: left; margin-left: 90px;" onclick="doScopeSave();">
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