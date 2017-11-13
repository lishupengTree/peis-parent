//空校验
function checkNull(id, objname){
	var obj = $("#"+id);
	if(obj){
		if(obj.val() === ""){
			window.alert("【" + objname + "】不能为空！");
			return true;
		}
	}
	return false;
}

//空校验
function checkNullSelect(id, objname){
	var obj = $("#"+id);
	if(obj){
		if(obj.val() === ""){
			window.alert("请选择【" + objname + "】！");
			return true;
		}
	}
	return false;
}

//计算字符串长度
function countLen(str){
	var len = str.length;
	var len1 = str.replace(/[\x00-\xff]/g,"").length;//去掉所有单字符后的长度
	return len + len1;
}

//只有字母、数字、下划线和横线组成
function checkChar(str, msg){
	if(str != str.replace(/[^A-Za-z0-9_\-]/g,'')){
		window.alert("【"+msg+"】只能由字母、数字、下划线和横线组成！");
		return true;
	}else{
		return false;
	}
}

//只有数字组成
function checkNum(str, msg){
	if(str != str.replace(/[^\d]/g,'')){
		window.alert("【"+msg+"】只能是数字！");
		return true;
	}else{
		return false;
	}
}

//只有数字和点号组成
function checkDouble(str, msg, decimalNum){
	if(str != str.replace(/[^0-9\.]/g,'')){
		window.alert("【"+msg+"】只能是数字和点号！");
		return true;
	}else{
		var parStr = "[1-9]\\d*(\\.\\d{1,"+decimalNum+"})?$|0(\\.\\d{1,"+decimalNum+"})?";
		var pattern = eval("/^"+parStr+"$/");
		if(pattern.test(str)){
			return false;
		}else{
			window.alert("【"+msg+"】不能以0开头（只有小数例外），小数点后一定要跟数字且最多只能有"+decimalNum+"位小数！");
			return true;
		}
	}
}

//取得单选框选择的值
function fetchRadioVal(rad){
	var radios = document.getElementsByName(rad);
	for(var i = 0; i < radios.length; i = i + 1){
		if(radios[i].checked){
			return radios[i].value;
		}
	}
	return "";
}

//邮箱验证
function checkEmail(email){
	var pattern = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+/;
	if(pattern.test(email)){
		return true;
	}else{ 
		return false;
  	}
}

//手机验证
function checkMobile(mobile){
	var pattern = /^[1][3-8]\d{9}/;
	if(pattern.test(mobile)){
		return true;
	}else{ 
		return false;
  	}
}

//电话验证
function checkPhone(mobile){
	var pattern = /^(\d{3,4}-)?\d{7,8}/;
	if(pattern.test(mobile)){
		return true;
	}else{ 
		return false;
  	}
}

/***********输入时验证***************/
//只能输入数字
function inputNum(obj){
	obj.value = obj.value.replace(/[^\d]/g,'');
}

//只能输入数字和横杠
function inputNum1(obj){
	obj.value = obj.value.replace(/[^0-9\-]/g,'');
}

//只能输入单字符
function inputDouble(obj){
	obj.value = obj.value.replace(/[^0-9\.]/g,'');
}

//只能输入字母、数字、下划线和横线
function inputChar(obj){
	obj.value = obj.value.replace(/[^A-Za-z0-9_\-]/g,'');
}

//只能输入单字符
function inputChar1(obj){
	obj.value = obj.value.replace(/[^\x00-\xff]/g,'');
}


//校验日期
function checkDate(obj, objname){
	if(obj){
	}else{
		return true;
	}
	var ereg;
	var birth = obj.value.replace(/-/g,"");
	if(birth === ""){
		return true;
	}
	if(birth.length == 8){
		if ( parseInt(birth.substr(0,4)) % 4 === 0 || (parseInt(birth.substr(0,4)) % 100 === 0 && parseInt(birth.substr(0,4))%4 === 0 )){//闰年
			ereg=/^(19|20)[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))$/;//闰年出生日期的合法性正则表达式
		} else {
			ereg=/^(19|20)[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))$/;//平年出生日期的合法性正则表达式
		}
		if(ereg.test(birth)){//测试日期的合法性
	
		}else{
			obj.style.color = "red";
			obj.onblur = function(){obj.style.color = "black";};
			window.alert(objname + "非法");
			obj.focus();
			return false;
		}
	}else{
		window.alert(objname + "必须为8位");
		obj.style.color = "red";
			obj.onblur = function(){obj.style.color = "black";};
		obj.focus();
		return false;
	}
	return true;
}

//校验身份证
function checkSid(obj){
	var idcard = obj.value;
	var Errors=new Array("验证通过!","【身份证】位数不对!","【身份证】出生日期超出范围或含有非法字符!","【身份证】校验错误!");
	
	var ereg;
	var idcard_array = new Array();
	idcard_array = idcard.split("");

	//身份号码位数及格式检验
	switch(idcard.length){
	case 15:
		if ( (parseInt(idcard.substr(6,2))+1900) % 4 === 0 || ((parseInt(idcard.substr(6,2))+1900) % 100 === 0 && (parseInt(idcard.substr(6,2))+1900) % 4 === 0 )){
			ereg=/^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}$/;//测试出生日期的合法性
		} else {
			ereg=/^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}$/;//测试出生日期的合法性
		}
		if(!ereg.test(idcard)){
			//obj.style.color = "red";
			//obj.onblur = function(){obj.style.color = "black";};
			window.alert(Errors[2]);
			obj.focus();
			return false;
		}
		break;
	case 18:
		//18位身份号码检测
		//出生日期的合法性检查 
		//闰年月日:((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))
		//平年月日:((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))
		if ( parseInt(idcard.substr(6,4)) % 4 === 0 || (parseInt(idcard.substr(6,4)) % 100 === 0 && parseInt(idcard.substr(6,4))%4 === 0 )){
			ereg=/^[1-9][0-9]{5}(19|20)[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}[0-9Xx]$/;//闰年出生日期的合法性正则表达式
		} else {
			ereg=/^[1-9][0-9]{5}(19|20)[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}[0-9Xx]$/;//平年出生日期的合法性正则表达式
		}
		if(ereg.test(idcard)){//测试出生日期的合法性
		}
		else{ 
			//obj.style.color = "red";
			//obj.onblur = function(){obj.style.color = "black";};
			window.alert(Errors[2]);
			obj.focus();
			return false;
		}
		break;
		default:
			//obj.style.color = "red";
			//obj.onblur = function(){obj.style.color = "black";};
			window.alert(Errors[1]);
			obj.focus();
			return false;
	}
	return true;
}

/*
//检查只能输入名字和数字
function checkName(obj){
	var t = obj.value;
	if(t === ""){
		window.alert("姓名不能为空");
		obj.focus();
		obj.style.color="red";
		return false;
	}else{
		//~!@#$%^&*()_+
		if (t.replace(/\w[.]/g,'') != t.replace(/[^\u4E00-\u9FA5]/g,'')){
			obj.style.color = "red";
			obj.onblur = function(){obj.style.color = "black";};
  			 window.alert("姓名不能包含特殊符号");
  			 obj.focus();
  			 return false;
		}
	}
	return true;
}*/

/*
//验证年龄是否符合
function checkAge(obj){
	if(obj.value !== "" && obj.value >= 8){
		var birth=obj.value.substring(0,4);
		var date=new Date();
		var age=date.getYear()-birth;
		if(age<18||age>45){
			window.alert("年龄须在18至45岁之间");
			return false;
		}
	}
	return true;
}*/
			
//校验出生日期是否与身份证一致
function checkBirthCard(obj, cardobj){
	var str1=cardobj.value;
	if(str1!==""){
		if(str1.length === 18){
	 		if(obj.value.replace(/-/g,"")!=str1.substring(6,14)){
	 			obj.style.color = "red";
				obj.onblur = function(){obj.style.color = "black";};
	 			window.alert("【出生日期】与【身份证】对应信息不一致");
	 			cardobj.focus();
	 			return false;
	 		}
	 	}else if(str1.length === 15){
	 		if(obj.value.replace(/-/g,"")!="19"+str1.substring(6,12)){
	 			obj.style.color = "red";
				obj.onblur = function(){obj.style.color = "black";};
	 			window.alert("【出生日期】与【身份证】对应信息不一致");
	 			cardobj.focus();
	 			return false;
	 		}
	 	}
	 }
	 return true;
}

//根据身份证信息设置出生日期
function setBirth(objId, cardobj){
	var card = cardobj.value;
	if(card !== ""){
		if(card.length === 18){
			$("#"+objId).val(card.substring(6,10) + "-" + card.substring(10,12) + "-" + card.substring(12,14));
		}else if(card.length === 15){
			$("#"+objId).val("19" + card.substring(6,8) + "-" + card.substring(8,10) + "-" + card.substring(10,12));
		}
	}
}

//根据name设置不可操作
function changeDisable(name, val){
	var objs = document.getElementsByName(name);
	if(objs){
		for(var i = 0; i < objs.length; i ++){
			objs[i].disabled = val;
		}
	}
}
/******************操作层的一些方法**************************/
//计算居中时，距离左边的距离
function getLeftPos(width){
	return (document.documentElement.clientWidth-width)/2+"px";
}
//计算居中时，距离上边的距离
function getTopPos(height){
	return (document.documentElement.clientHeight-height)/2+"px";
}
//根据间隔时间distance自动关闭层	
function showMsgAutoClose(msg, distance){
	showMsg(msg);
	window.setTimeout(function(){
		$.unblockUI();
	},distance);
}
//打开层
function showMsg(msg){
	$.blockUI({
		message:msg,
		css:{width:"350px",height:"50px",border:"2px solid #b6cfd6",color:"#8eafbb",fontSize:"30px",top:getTopPos(50),left:getLeftPos(350),paddingTop:"8px"}
	});
}

//关闭层
function closeMsg(){
	$.unblockUI();
}