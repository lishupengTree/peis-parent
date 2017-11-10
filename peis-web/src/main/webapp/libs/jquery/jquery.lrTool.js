/**
* $.lrTool
* @extends jquery.1.6.1
* @fileOverview 扩展工具类 
* @author 传说中的旅人
* @email Travellerlegend@gmail.com
* @site http://lengend.iteye.com/blog/1005980
* @version 1.0
* @date 2011-07-26
* Copyright (c) 2011-2011 传说中的旅人
*/
(function($)
	{
		$.extend({
			//获取鼠标当前坐标
			mouseCoords:function($ev){
				if($ev.pageX || $ev.pageY){//$ev是EVENT对象
					return {x:$ev.pageX, y:$ev.pageY};
				}else{
					return {x:$ev.offset().left,y:$ev.offset().top};
				}
			},
			//设置文本焦点定位(参数 focusObj:将设置聚焦的控件对象;pos:焦点定位位置）
			setFocus:function($focusObj,pos){
				$focusObj.focus(); //默认使用focus方法聚焦 
				if ($.browser.msie) {//判断是否为Ie浏览器
					var textRange = $focusObj[0].createTextRange(); //将传入的控件对象转换为Dom对象，并创建一个TextRange对象
					textRange.moveStart('character', pos);// 设置光标显示的位置
					textRange.collapse(true);
					textRange.select(); 
				}
			}
		});		

	})(jQuery);   