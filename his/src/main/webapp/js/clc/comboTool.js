/**
 * dhtmlXCombo过滤器，参数描述：，，，
 * @param keyCode 键值
 * @param json：JSON格式数据源
 * @param comboName dhtmlXCombo引用名
 * @param filterMode 匹配模式
 * @param quickFilled 是否快速填充（匹配项只有一项，且完全匹配）
 * @param required 是否必填
 * @param checked 是否校验
 * @returns {Number} 匹配情况，0：完全匹配；1：部分匹配；2：不匹配
 */
function comboFilter(keyCode, json, comboName, filterMode, quickFilled, required, checked){
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
				var con = fullMatchRule(filterMode);
				if(eval(con)){
					combo.setComboText(elem.contents);
					combo.setComboValue(elem.nevalue);
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
				opt[0] = elem.nevalue;
				opt[1] = elem.contents;
				opts.push(opt);
			}else{
				var con = matchRule(filterMode);
				if(eval(con)){
					var opt = new Array();
					opt[0] = elem.nevalue;
					opt[1] = elem.contents;
					opts.push(opt);
				}
			}
			
		});
	
		combo.clearAll();
		combo.addOption(opts);
	}
}
function matchRule(filterMode){
	if(filterMode=='py'){//拼音
		return "(elem.contents.toUpperCase().indexOf(text)==0 || elem.inputcpy.toUpperCase().indexOf(text)==0)";
	}else if(filterMode=='wb'){//五笔
		return "(elem.contents.toUpperCase().indexOf(text)==0 || elem.inputcwb.toUpperCase().indexOf(text)==0)";
	}else if(filterMode=='hp'){//混拼
		return "(elem.contents.toUpperCase().indexOf(text)==0 || elem.inputcwb.toUpperCase().indexOf(text)==0 || elem.inputcpy.toUpperCase().indexOf(text)==0)";
	}else{
		return "(elem.contents.toUpperCase().indexOf(text)==0)";
	}
}
function fullMatchRule(filterMode){
	if(filterMode=='py'){//拼音
		return "(elem.contents.toUpperCase()==text || elem.inputcpy.toUpperCase()==text)";
	}else if(filterMode=='wb'){//五笔
		return "(elem.contents.toUpperCase()==text || elem.inputcwb.toUpperCase()==text)";
	}else if(filterMode=='hp'){//混拼
		return "(elem.contents.toUpperCase()==text || elem.inputcwb.toUpperCase()==text || elem.inputcpy.toUpperCase()==text)";
	}else{
		return "(elem.contents.toUpperCase()==text)";
	}
}

function autoForward(event){
	this.src = event.target;
	if(event.keyCode==13){//13:enter
		var obj = this;
		var that = this.src;
		$(":input").not($(":radio")).not($(":hidden")).each(function(index, domEle){
			if(that==null || that==domEle){
				obj.src = $(":input").not($(":radio")).not($(":hidden"))[index+1];
				$(":input").not($(":radio")).not($(":hidden"))[index+1].select();
				return false;
			}
		});
	}
}