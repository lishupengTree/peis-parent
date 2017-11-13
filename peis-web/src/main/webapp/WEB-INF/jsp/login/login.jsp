<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>
<%
String path = request.getContextPath();

String showPath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<link href="css/login.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="dhtmlxCombo/codebase/dhtmlxcombo.css" />
<script type="text/javascript" src="js/jquery-1.6.1.js"></script>
    <script type="text/javascript" src="js/jquery.cookie.js"></script>
<script>window.dhx_globalImgPath = "dhtmlxCombo/codebase/imgs/";</script>
<script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcommon.js"></script>
<script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcombo.js"></script>
<script type="text/javascript" src="dhtmlxCombo/codebase/ext/dhtmlxcombo_whp.js"></script>
<style type="test/css">
  .hsp_xx{list-style:none;height:32px;}
</style>
<script>

	$(document).ready(function(){
		
	});
	var	combo_dept;
	var combo_shayne;
	function getDepts(key)
	{
		if(key=='') return;
		$.ajax({
				cache:false,   //是否使用缓存
                url: "getDepts.htm", 
                async : false,   //是否异步，false为同步
                type:"get",
                data:"key=" + key + "&time=" + new Date().getMilliseconds(),
                error:function(){
                	alert("获取部门失败");
                },
                success:function(reply){
                	if(reply=='[]')
                	{
                		alert("用户信息验证不通过!");
                		$('#key')[0].focus()
                		var   e   =  event.srcElement; 
				        var   r   =  e.createTextRange(); 
				        r.moveStart( 'character ', e.value.length); 
				        r.collapse(true); 
				        r.select(); 
                		return ;
                	}
                	var depts = eval('('+reply+')');
                	combo_dept.clearAll();
					
					var opts =  new Array();
                	for(var i=0;i<depts.length;i++)
                	{
                		var opt = new Array();
                		opt[0] = depts[i].deptcode;
						opt[1] = depts[i].deptname;
						opts.push(opt);
                	}
                	combo_dept.addOption(opts);
                	combo_dept.selectOption(0,false,true);
                	//getShaynes();
                }
            });
	}
	
	function getShaynes()
	{
           var userkey = $('#key').val();
           var deptcode = combo_dept.getActualValue();
            $.ajax({
				cache:true,   //是否使用缓存
                url: "getShaynes.htm", 
                async : false,   //是否异步，false为同步
                type:"get",
                data:"deptcode=" + deptcode + "&time=" + new Date().getMilliseconds(),
                error:function(){
                	alert("获取部门失败");
                },
                success:function(reply){
                	if(reply=='[]')
                	{
                		$("#shayne_div").css('display','none');
                		combo_dept.setSize(202);
                		return;
                	}
                	$("#shayne_div").css('display','');
                	combo_dept.setSize(100);
                	combo_shayne.setSize(100);
                	var depts = eval('('+reply+')');
                	combo_shayne.clearAll();
					var opts =  new Array();
                	for(var i=0;i<depts.length;i++)
                	{
                		var opt = new Array();
                		opt[0] = depts[i].deptcode;
						opt[1] = depts[i].deptname;
						opts.push(opt);
                	}
                	combo_shayne.addOption(opts);
                	combo_shayne.selectOption(0,false,true);
                	//return ;
                }
            });
	}
	
	function strToJson(str){  
	    var json = (new Function("return " + str))();  
	    return json;  
	} 
	
	function checkUser()
	{
		var key = $('#key').val();
		var password = $('#password').val();
		if(key == '')
		{
			alert("请输入登录信息!");
			$('#key')[0].focus()
			return;
		}
		if(password == '')
		{
			alert("请输入登录信息!");
			document.getElementById("password").focus();
			return;
		}
		var dept = combo_dept.getActualValue();
		//alert(dept);
		var shayne = combo_shayne.getActualValue();
		
		var mac = $("#mac").val();
		var ip = $("#ip").val();
		$.ajax({
				cache:false,   //是否使用缓存
                url: "checkUser.htm", 
                async : false,   //是否异步，false为同步
                type:"get",
                data:"key="+key+"&password="+password+"&dept="+dept+"&shayne=" + shayne +"&mac=" + mac + "&ip=" + ip + "&time=" + new Date().getMilliseconds(),
                error:function(){
                	alert("用户信息验证不通过!");
                },
                success:function(reply){
	                if(reply=='success')
	                {
	               
	                $.cookie("job_no", $('#key').val()); 
	               
         			document.location.href="platform.htm";
	                }else
	                {
                		alert("用户信息验证不通过!");
                	}
                }
            });
	}
	function set()
	{	
		//查看是否全屏,取消全屏
		doFullScreen();
		$('#key')[0].focus();
		if($.cookie('job_no')!=null){
			$('#key').val($.cookie('job_no'));
			$('#password')[0].focus();
		}
		if(parent.document.location.href.indexOf('login.htm')==-1)
		{
			parent.document.location.href='login.htm';
		}
		
		combo_dept = dhtmlXComboFromSelect("dept");
		combo_dept.enableFilteringMode(false);
		combo_dept.readonly(true);
		combo_dept.attachEvent("onChange",getShaynes);
		combo_dept.attachEvent("onKeyPressed", function(keyCode){
			if(keyCode==13){
				if($("#shayne_div").css('display')=='none')
				{
					checkUser();
				}else
				{
					$(combo_shayne.DOMelem_input).focus();
				}
			}
		});  
		
		combo_shayne = dhtmlXComboFromSelect("shayne");
		combo_shayne.enableFilteringMode(false);
		combo_shayne.readonly(true);
		combo_shayne.attachEvent("onKeyPressed", function(keyCode){
			if(keyCode==13){
				checkUser();
			}
		});  
		//var opts =  new Array();
		//var opt = new Array();
        //opt[0] ="1001";
		//opt[1] ="内科";
		//opts.push(opt);
		//combo_dept.addOption(opts);
        //combo_dept.selectOption(0,true,true);
        
        $("#mac").val(LODOP.GET_SYSTEM_INFO("NetworkAdapter.1.PhysicalAddress"));
		$("#ip").val(LODOP.GET_SYSTEM_INFO("NetworkAdapter.1.IPAddress"));
		return;
	}
	
	function enter(name)
	{
		if (event.keyCode == 13){
        	if(name=='password')
        	{
        		window.setTimeout(
        			function(){$(combo_dept.DOMelem_input).focus();}
        		,0)
        	}else
        	{
        		$('#password')[0].focus();
        	}
    	}
	}
	
	function doFullScreen(){
		if (document.documentElement && document.documentElement.clientHeight && document.documentElement.clientWidth){
			winHeight = document.documentElement.clientHeight;
			winWidth = document.documentElement.clientWidth;
		}
		if(winHeight==screen.height||winWidth==screen.width){
			
			window.focus();
			var WshShell = new ActiveXObject('WScript.Shell');			
			WshShell.SendKeys('{F11}');
		}
	}
</script>


</head>
<body style="background:url(img/login/login1.jpg) repeat-x" onload="set()">
<script type="text/javascript" src="js/LodopFuncs.js"></script> 
<object id="LODOP_OB" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0> 
	<embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0 pluginspage="install_lodop.exe"></embed>
</object> 
<div class="login">
	<table width="1000" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="267"><img src="img/login/login2.jpg" /></td>
        <td width="515"><img src="img/login/login3.jpg" /></td>
        <td width="218"><img src="img/login/login4.jpg" /></td>
      </tr>
      <tr>
        <td><img src="img/login/login5.jpg" /></td>
        <td><img src="img/login/login6.jpg" /></td>
        <td><img src="img/login/login7.jpg" /></td>
      </tr>
      <tr>
        <td><img src="img/login/login8.jpg" /></td>
        <td background="img/login/login9.jpg" valign="top">
        	<table width="370" border="0" cellspacing="0" cellpadding="0"  style="margin-top:80px;margin-left:45px;">
              <tr>
                <td height="26" width="60">用户名：</td>
                <td colspan="2"><input id ="key" type="text" class="login4" value="" onblur="javascript:getDepts(this.value);" tabindex="1" onkeydown="enter(this.id);"/></td>
                <td width="95" rowspan="3"><a href="javascript:checkUser()" tabindex="4"><img src="img/login/enter.jpg" border="0" /></a></td>
              </tr>
              <tr>
                <td height="26">密&emsp;码：</td>
                <td colspan="2"><input id="password" type="password" value="" class="login4" tabindex="2" style="font-size: 24px;"  onkeydown="enter(this.id);"/></td>
              </tr>
              <tr>
                <td height="26">科&emsp;室：</td>
                <td width="130" >
                <table cellpadding="0" cellspacing="0">
                	<tr>
                		<td><label><select id="dept" class="login5" tabindex="3"  style="width: 200px;"></select></label></td>
                		<td><label id="shayne_div" style="display: none;"><select id="shayne" class="login5" tabindex="3" ></select></label></td>
                	</tr>
                </table>
                </td>
                <td width="">&nbsp;</td>
              </tr>
            </table>
          	<table width="100%" border="0" cellspacing="0" cellpadding="0" >
              <tr>
              	<td  align="center" height="35">
              	<font color="red" style="font-size: 14px;"><b>请保管好自己设置的密码，并定期修改！</b></font>
              	</td> 
              </tr>
              
            </table>
          
        </td> 
        <td><img src="img/login/login10.jpg" /></td>
      </tr>
      <tr>
        <td><img src="img/login/login11.jpg" /></td>
        <td><img src="img/login/login12.jpg" /></td>
        <td><img src="img/login/login13.jpg" /></td>
      </tr>
      <tr>
        <td><img src="img/login/login14.jpg" /></td>
        <td><img src="img/login/login15.jpg" /></td>
        <td><img src="img/login/login16.jpg" /></td>
      </tr>
    </table>
    <input type="hidden" id="mac"/><input type="hidden" id="ip"/>
</div>
</body>
</html>
