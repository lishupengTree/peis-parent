var F5_KEYCODE = 116;
var F6_KEYCODE = 117;
var F7_KEYCODE = 118;
var tabbar;
$(document).ready(function(){
	reloadTree();
	
	tabbar = new dhtmlXTabBar("tab_bar", "top");
	tabbar.setSkin("dhx_skyblue");
	tabbar.setImagePath("dhtmlxTabbar/codebase/imgs/");
	tabbar.enableTabCloseButton(true);
	tabbar.enableAutoReSize(true);
    
    $(document).bind("keydown",function(event){
		if(event.keyCode==F5_KEYCODE){
			$("#reg_li")[0].click();
			event.preventDefault();
		}else if(event.keyCode==F6_KEYCODE){
			$("#charge_li")[0].click();
			event.preventDefault();
		}else if(event.keyCode==F7_KEYCODE){
			$("#other_li")[0].click();
			event.preventDefault();
		}
	});
	
	$("ul.hsp_xx > li").click(function(){
		if(!$(this).hasClass("hsp_xxhover")){
			$(this).addClass("hsp_xxhover");
			$(this).siblings().removeClass("hsp_xxhover");
		}
		if($(this).attr("id")=="reg_li"){
			window.location = "register.htm";
		}else if($(this).attr("id")=="charge_li"){
			//window.location = "";
		}else if($(this).attr("id")=="other_li"){
			//window.location = "others.htm";
		}
	}).hover(function(){
			$(this).css("cursor","pointer");
		},function(){
			$(this).css("cursor","default");
		}
	);
	
	var isWarningSign = $('#isWarningSign').val(); //库存预警跳转标志
	if(isWarningSign == "Y"){
		var warningMid = $('#warningMid').val();
		//节点张开，选中
		var node = zTreeObj.getNodeByParam("id", warningMid);
		zTreeObj.selectNode(node);
		//node.onclick();
		//alert(warningMid);
		//alert(node.model);
		window.setTimeout(function(){
			addNewTab(node.id, node.name,node.model,node.type);
		}, 200);
		
	}
	var validto_warning = $('#validto_warning').val(); //效期跳转标志
	if(validto_warning == "Y"){
		//节点张开，选中
		var node = zTreeObj.getNodeByParam("id", '040110'); 
		zTreeObj.selectNode(node);
		window.setTimeout(function(){
			addNewTab(node.id, node.name,node.model,node.type);
		}, 200);
		
	}
	if($("#menuname").html()=='监控中心'){
		window.setTimeout(function(){
			addNewTab('3006', '系统监控','monitorCenter/sysmonitor.htm',null);
		}, 0);
	}
		
});

var zTreeObj;

var setting = {
	showLine:true,
	expandSpeed:"fast",
	nameCol:"name",
	treeNodeKey:"id",
	treeNodeParentKey:"pid",
	isSimpleData:true,
	callback:{
      click: zTreeOnClick
    }
};
function zTreeOnClick(event, treeId, treeNode) {
	var model=treeNode.model;
	if(model=='dailyStat/mzdaystat.htm'||model=='dailyStat/mzmonthstat.htm'||model=='dailyStat/mzstatlist.htm'){
		var flag="";
			$.ajax({
			url:"getNowServerTime.htm?&date="+new Date(),
			async:false,
			cache:false,
			error:function(){
				return false;
		},
		success:function(reply){
			flag=reply;
			}
		});
		if(flag!="false"){
			alert("系统正忙，该功能在"+flag+"后开放");
			return;
		}
	}

	if(!treeNode.isParent){
		addNewTab(treeNode.id, treeNode.name,treeNode.model,treeNode.type);
	}
}
function reloadTree() {
	var zNodes1 = clone(simpleNodes);//clone(zNodes)
	zTreeObj = $("#menuTree").zTree(setting, zNodes1);
	
}
function addNewTab(menuId, menuName,url,opentype) {
	if(url==null) {
	//alert('待实现!');
	return;};
	if(opentype==1)
	{
		if(url.indexOf('?')>-1)
		{
			document.location.href = url + "&menuid="+menuId;
		}else
		{
			document.location.href = url + "?menuid="+menuId;
		}
		return;
	}
	var $ma = $("#mainArea_"+menuId);
	if($ma[0]==undefined||$ma[0]==null){
		tabbar.addTab("tab_"+menuId, "<font size='2' face='微软雅黑'>"+menuName+"</font>", (menuName.length+3)*16+"px");
		var mainArea = $("#mainArea").clone(true);
		var mid = "mainArea_"+menuId;
		mainArea.attr("id",mid);
		mainArea.find("iframe")[0].src = url;
		mainArea.find("iframe")[0].height = $("#dhxMainCont").height();
		mainArea.show();
		var area = mainArea[0].outerHTML;
		//alert(12)
		$("#tab_bar").append(mainArea);
		tabbar.setContent("tab_"+menuId,mid);
		tabbar.setContentHTML("tab_"+menuId,area);//直接把网页加载进tab
	}
	tabbar.setTabActive("tab_"+menuId);
}
function delCurrentTab(){
	if(window.confirm('你确定要删除吗？')){
		tabbar.removeTab(tabbar.getActiveTab(),true);
	}
}
