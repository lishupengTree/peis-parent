/**------------
 * combolist : 下拉框
 * type：                 类型
 * filter:   bealoon,是否开启按键过滤模式,部分实现，不好意思
 * --------------------**/
function fillCombo(combolist,type,filter){
	
	
	var combo = eval(combolist);
	//--------------用户--------users---
	if(type == "users"){
		 // 过滤的数据类型
		 var data_type = {
				 value:"elem.id",
				 text:"elem.name",
				 inputcpy:"elem.input_cpy",
				 inputcwb:"elem.input_cwb"
		 };
		$.get("maintenance/chg_manage/load_operator.htm", function(json){
			if(filter){  //开启过滤模式
				filterOpen(combo,json,data_type);
			}else{
				 $(eval(json)).each(function(index,elem){
					 	combo.addOption(elem.sn,elem.fqname);
				 });
			}
		});
	}

	//------------- 制剂类型-------------------------
	if(type == "dosageForm"){
		 // 过滤的数据类型
		 var data_type = {
				 value:"elem.name",
				 text:"elem.name",
				 inputcpy:"elem.input_cpy",
				 inputcwb:"elem.input_cwb"
		 };
		$.get("maintenance/chg_manage/load_dosageFormr.htm", function(json){
			if(filter){  //开启过滤模式
				filterOpen(combo,json,data_type);
			}else{
				 $(eval(json)).each(function(index,elem){
					 	combo.addOption(elem.sn,elem.fqname);
				 });
			}
		});
	}
	
	//--------------挂号科室------------
	if(type == "deptname"){
	   	 $.get("schedule_mode.htm?method=getdept",function(json){
			 $(eval(json)).each(function(index,elem){
			 	combo.addOption(elem.deptcode,elem.deptname);
			 	return;
			});
	 	});
	}
	//-----------保险类别--------------
	if(type == "instype"){
	   	 $.get("maintenance/patients_manage/get_instypes.htm",function(json){
			 $(eval(json)).each(function(index,elem){
			 	combo.addOption(elem.nevalue,elem.contents);
			 	return;
			});
	 	});
	}
	//-----------优惠类别--------------
	if(type == "discount"){
	   	 $.get("maintenance/patients_manage/get_discount.htm",function(json){
			 $(eval(json)).each(function(index,elem){
			 	combo.addOption(elem.nevalue,elem.contents);
			 	return;
			});
	 	});
	}
	//-----------发药人员--------------
	if(type == "excmname"){
	   	 $.get("clinicpharmacy/getexcmname.htm",function(json){
			 $(eval(json)).each(function(index,elem){
			 	combo.addOption(elem.id,elem.name);
			 	return;
			});
	 	});
	}

	
	//-----------------------------
	if(type == "post"){fromDict(combo,"6");}
	else if(type == "orgtype"){fromDict(combo,"12");}    //机构分类(医院类别--待定)
	else if(type == "inptype"){fromDict(combo,"19");}	 //收费项目
	else if(type == "clctype"){fromDict(combo,"20");}	 //门诊类型
	else if(type == "hosdegree"){fromDict(combo,"24");}  //医院级别
	else if(type == "degreelevel"){fromDict(combo,"25");}//医院级别等级
	else if(type == "sample"){  //检查样本
		//fromDict(combo,"35");
		$.post("b2b/getLabSample.htm",function(json){
			$(eval(json)).each(function(index,elem){
			 	combo.addOption(elem.sample,elem.sample);
			});
	 	});
	}	
	else if(type == "dist"){
		 var data_type = {
				 value:"elem.nevalue",
				 text:"elem.contents",
				 inputcpy:"elem.inputcpy",
				 inputcwb:"elem.inputcwb"
		 };
		// var condition = "and 1 = ? and t.nevalue like '33%'";
		// var params = "1 1 ";
		 $.get("maintenance/chg_manage/get_bas_dicts_by_conditions.htm?nekey=1",function(json){
				if(filter){  //开启过滤模式
					filterOpen(combo,json,data_type);
				}else{
					 $(eval(json)).each(function(index,elem){
					 	combo.addOption(elem.sn,elem.fqname);
					 });
				}
	 	});
		
	}
	
	//-----------频次--------------
	if(type == "freq"){
		 // 过滤的数据类型
		 var data_type = {
				 value:"elem.sn",
				 text:"elem.fqname",
				 inputcpy:"elem.inputcpy",
				 inputcwb:"elem.inputcwb"
		 };
	   	 $.get("maintenance/doc_advice_main/get_freq.htm",function(json){
				if(filter){  //开启过滤模式
					filterOpen(combo,json,data_type);
				}else{
					 $(eval(json)).each(function(index,elem){
					 	combo.addOption(elem.sn,elem.fqname);
					 });
				}
	 	});
	}
	//-----------38  检查单位--------------
	if(type == "testunit"){
		 // 过滤的数据类型
		 var data_type = {
				 value:"elem.contents",
				 text:"elem.contents",
				 inputcpy:"elem.inputcpy",
				 inputcwb:"elem.inputcwb"
		 };
	   	 $.get("maintenance/chg_manage/get_bas_dicts.htm?nekey=38",function(json){

				if(filter){  //开启过滤模式
					filterOpen(combo,json,data_type);
				}else{
					 $(eval(json)).each(function(index,elem){
					 	combo.addOption(elem.contents,elem.contents);
					 });
				}
	 	});
	}
	
	//-----------83  检查类别--------------
	if(type == "sheettype"){
		 // 过滤的数据类型
		 var data_type = {
				 value:"elem.contents",
				 text:"elem.contents",
				 inputcpy:"elem.inputcpy",
				 inputcwb:"elem.inputcwb"
		 };
	   	 $.get("maintenance/chg_manage/get_bas_dicts.htm?nekey=83",function(json){

				if(filter){  //开启过滤模式
					filterOpen(combo,json,data_type);
				}else{
					 $(eval(json)).each(function(index,elem){
					 	combo.addOption(elem.contents,elem.contents);
					 });
				}
	 	});
	}
	
	//-----------6002  血管颜色--------------
	if(type == "arterycolor"){
		 // 过滤的数据类型
		 var data_type = {
				 value:"elem.contents",
				 text:"elem.contents",
				 inputcpy:"elem.inputcpy",
				 inputcwb:"elem.inputcwb"
		 };
	   	 $.get("maintenance/chg_manage/get_bas_dicts.htm?nekey=6002",function(json){

				if(filter){  //开启过滤模式
					filterOpen(combo,json,data_type);
				}else{
					 $(eval(json)).each(function(index,elem){
					 	combo.addOption(elem.contents,elem.contents);
					 });
				}
	 	});
	}

}

//从字典表取得数据并填充
function fromDict(combo,nekey){
  	 $.get("maintenance/chg_manage/get_bas_dicts.htm?nekey="+nekey,function(json){
		 $(eval(json)).each(function(index,elem){
		 	combo.addOption(elem.nevalue,elem.contents);
		 	return;
		});
 	});
}
//开启过滤模式开关
function  filterOpen(combo,json,data_type){
	combo.attachEvent("onOpen", function(){
		comboFilter1(39, json, combo, 'hp', false, true,false,data_type);
	});
	combo.attachEvent("onKeyPressed", function(keyCode){
			comboFilter1(keyCode, json, combo, 'hp', false, true,false,data_type);
	});
	combo.attachEvent("onBlur", function(){
		if(combo.getSelectedValue() == null){
			combo.setComboText('');
		}
	});
}

/**
 * dhtmlXCombo过滤器，参数描述：，，，
 * @param keyCode 键值
 * @param json：JSON格式数据源
 * @param comboName dhtmlXCombo引用名
 * @param filterMode 匹配模式
 * @param quickFilled 是否快速填充（匹配项只有一项，且完全匹配）
 * @param required 是否必填
 * @param checked 是否校验
 * @param data_type 过滤的数据类型
 * @returns {Number} 匹配情况，0：完全匹配；1：部分匹配；2：不匹配
 */
function comboFilter1(keyCode, json, comboName, filterMode, quickFilled, required, checked,data_type){
	var combo = eval(comboName);
	if(keyCode==13){//13:enter
		var flag = 2;//匹配情况，0：完全匹配；1：部分匹配；2：不匹配
		var value = combo.getComboText();
		if(value=='' && required==false){//非必填项，且未填入任何数据
			return 0;
		}
	
		if(value!='' && checked){
			for(var i=0;i<combo.optionsArr.length;i++){
				if(value==combo.optionsArr[i].text){
					//combo.setComboValue(combo.optionsArr[i].value);
					flag = 0;
					break;
				}else if(combo.optionsArr[i].text.indexOf(value)>=0){
					flag = 1;
				}
			}
		}
		
		if(quickFilled && combo.optionsArr.length==1){//快速填充模式
			var text = combo.getComboText();
			text = text.toUpperCase();
			var datas = eval(json);
			
			$(datas).each(function(index,elem){
				var con = fullMatchRule1(filterMode,data_type);
				if(eval(con)){
					combo.setComboValue(eval(data_type.value));
					combo.setComboText(eval(data_type.text));
					flag = 0;
					return false;
				}
			});
		}
		return flag;
	}else{
		var opts = new Array();
		var text = combo.getComboText();
		text = text.toUpperCase();
		var datas = eval(json);
	
		$(datas).each(function(index,elem){
			if($.trim(text)==''){//没有输入值
				var opt = new Array();
				opt[0] = eval(data_type.value);
				opt[1] = eval(data_type.text);
				opts.push(opt);
			}else{
				var con = matchRule1(filterMode,data_type);
				if(eval(con)){
					var opt = new Array();
					opt[0] = eval(data_type.value);
					opt[1] = eval(data_type.text);
					opts.push(opt);
				}
			}
		});
	
		combo.clearAll();
		combo.addOption(opts);
	}
}

function matchRule1(filterMode,data_type){
	if(filterMode=='py'){//拼音
		return "(" + data_type.text + ".toUpperCase().indexOf(text)==0 || " + data_type.inputcpy + ".toUpperCase().indexOf(text)==0)";
	}else if(filterMode=='wb'){//五笔
		return "(" + data_type.text + ".toUpperCase().indexOf(text)==0 || " + data_type.inputcwb + ".toUpperCase().indexOf(text)==0)";
	}else if(filterMode=='hp'){//混拼
		return "(" + data_type.text + ".toUpperCase().indexOf(text)==0 || " + data_type.inputcwb + ".toUpperCase().indexOf(text)==0 || " + data_type.inputcpy + ".toUpperCase().indexOf(text)==0)";
	}else{
		return "(" + data_type.text + ".toUpperCase().indexOf(text)==0)";
	}
}
function fullMatchRule1(filterMode,data_type){
	if(filterMode=='py'){//拼音
		return "(" + data_type.text + ".toUpperCase()==text || " + data_type.inputcpy + ".toUpperCase()==text)";
	}else if(filterMode=='wb'){//五笔
		return "(" + data_type.text + ".toUpperCase()==text || " + data_type.inputcwb + ".toUpperCase()==text)";
	}else if(filterMode=='hp'){//混拼
		return "(" + data_type.text + ".toUpperCase()==text || " + data_type.inputcwb + ".toUpperCase()==text || " + data_type.inputcpy + ".toUpperCase()==text)";
	}else{
		return "(" + data_type.text + ".toUpperCase()==text)";
	}
}
function autoForward1(event){
	this.src = event.target;
	if(event.keyCode==13){//13:enter
		var obj = this;
		var that = this.src;
		$(":input").not($(":hidden")).each(function(index, domEle){
			if(that==null || that==domEle){
				obj.src = $(":input").not($(":hidden"))[index+1];
				$(":input").not($(":hidden"))[index+1].select();
				return false;
			}
		});
	}
}