var LODOP;
$(document).ready(function () {
    $("body").prepend("<div style='display:none'><object id='LODOP' classid='clsid:2105C259-1E0C-4534-8141-A753534CB4CA' width='0' height='0'><embed id='LODOP_EM' type='application/x-print-lodop' width='0' height='0'/></object></div>");
    LODOP = getLodop(document.getElementById('LODOP'), document.getElementById('LODOP_EM'));
    LODOP.SET_LICENSES("杭州清普信息技术有限公司", "964657080837383919278901905623", "", "");

});
function getLodop(oOBJECT, oEMBED) {
    /***************************************************************************
     * 本函数根据浏览器类型决定采用哪个对象作为控件实例： IE系列、IE内核系列的浏览器采用oOBJECT，
     * 其它浏览器(Firefox系列、Chrome系列、Opera系列、Safari系列等)采用oEMBED。
     **************************************************************************/
    var strHtml1 = "<br><font color='#FF00FF'>打印控件未安装!点击这里<a href='/his/install_lodop.exe'>执行安装</a>,安装后请刷新页面或重新进入。</font>";
    var strHtml2 = "<br><font color='#FF00FF'>打印控件需要升级!点击这里<a href='/his/install_lodop.exe'>执行升级</a>,升级后请重新进入。</font>";
    var strHtml3 = "<br><br><font color='#FF00FF'>(注：如曾安装过Lodop旧版附件npActiveXPLugin,请在【工具】->【附加组件】->【扩展】中先卸载它)</font>";
    var LODOP = oEMBED;

    try {
        if (navigator.appVersion.indexOf("MSIE") >= 0) {
            LODOP = oOBJECT;
        }
        if ((LODOP == null) || (typeof (LODOP.VERSION) == "undefined")) {
            if (navigator.userAgent.indexOf('Firefox') >= 0) {
                $("body").prepend(strHtml3);
            } else {
                $("body").prepend(strHtml1);
            }
        } else if (LODOP.VERSION < "6.0.1.0") {
            $("body").prepend(strHtml2);
        }
        // *****如下空白位置适合调用统一功能:*********

        // *******************************************
        return LODOP;
    } catch (err) {
        $("body").prepend("Error:" + strHtml1);
        return LODOP;
    }
}

function doSomePrintA() {
    //var obj=window.parent.frames["myIframe"].document.getElementById("deptsum");
    //if(obj==null||typeof obj=="undefind"){
    //	alert('该对象不能进行打印');
    //	return ;
    //}
    //var str=obj.innerHTML;

    //if(window.parent.frames["myIframe"].document.all){//IE
    //	arr=str.split("\r\n");
    //}else{//FireFox
    //	arr=str.split("\n");
    //}
    //alert(arr[3]+"-----len="+arr.length+",-----size="+str.length+",==cols="+$(obj).attr("cols")+",===rows="+$(obj).attr("rows"));
    //return ;
    openWin1('体检人员批量打印', '850', '540', 'pexamNew/getSomePrintData.htm?time=' + new Date().getTime());
}
function openWin1(title, width, height, url) {
    var top_ = ($(window).height() - height) / 2;
    var left_ = ($(window).width() - width) / 2;
    var width_ = width + "px";
    var height_ = height + "px";
    var css = "<style>.box1{ width:" + (width - 4) + "px; border:1px solid #b6cfd6; padding:1px; margin:0 auto;} .box22{ width:" + (width - 20) + "px;background-color:#d9eaee; padding:8px;}.box3{ width:" + (width - 20) + "px; margin:0 auto; }.box3 span{ font-size:13px; color:#6ba3b6; font-family:font-family:Microsoft YaHei; font-weight:bold;height: 24px;vertical-align: top;}.boxpad{float: right;}.boxpad img:hover{cursor: pointer;}.box4{ width:" + (width - 27) + "px;height:" + (height - 45) + "px; border:1px solid #b6cfd6; background-color:#fff;font-size:13px;font-family:font-family:Microsoft YaHei; line-height:22px;}</style>"
    var content = css + "<div class='box1'><div class='box22'><div class='box3' style='text-align: left;'><span id='reg_title' style='float: left;'>" + title + "</span><span class='boxpad'><img id='close_iframe_img' src='img/close.gif' align='middle'onclick='doClose1()' /></span></div><iframe class='box4' style='width:" + (width - 32) + "px' src='" + url + "' width='100%' height='100%' topmargin='0' leftmargin='0' marginheight='0' scrolling='auto' marginwidth='0' frameborder='no' ></iframe></div></div>";
    $.blockUI({
        message: content,
        css: {width: width_, height: height_, top: top_, left: left_, border: '0px solid #aaa'},
        overlayCSS: {backgroundColor: '#CCCCCC'}
    });
}

function doClose1() {
    $("#hidden_iframe").attr("src", "");
    try {
        $("div")[0].focus();
    } catch (e) {
    }
    $.unblockUI();
}

/**
 *
 * @param {} printtype  打印类型  type = 0 是预览  type = 1  是  打印
 */
function doPrintA(printtype) {
    //先保存--防止点击打印后忘记点击保存--而导致数据不再
    //doSaveA1();
    var examid = grid_doctorstation.cells(grid_doctorstation.getRowId(grid_doctorstation.row.rowIndex - 1), 5).getValue();
    var pexamid = grid_doctorstation.cells(grid_doctorstation.getRowId(grid_doctorstation.row.rowIndex - 1), 4).getValue();
    var wxts_1 = "";
    var wxts_2 = "";
    var wxts_3 = "";
    var deptsum_jkjy = "";
    //取得温馨提示
    $.ajax({
        async: false,
        type: "post",
        url: "pexamNew/GETWxts.htm",
        data: "pexamid=" + pexamid + "&time=" + (new Date()).valueOf(),
        error: function () {
            alert("ajax请求失败");
        },
        success: function (data) {
            if (data.indexOf('fail') > -1) {
                alert(data);
            } else {
                var js = eval('(' + data + ')');
                wxts_1 = js.wxts_1;   //温馨提示
                deptsum_jkjy = js.deptsum_jkjy;   //健康建议
            }
        }
    });

    var printname = "总检报告（" + $("#patname").text() + "）";
    LODOP.PRINT_INITA(0, 20, "210mm", "290mm", printname);
    LODOP.SET_PRINT_PAGESIZE(1, "210mm", "290mm", "");

    //封面
    LODOP.ADD_PRINT_URL(547, 28, "100%", "100%", "frontCover.htm?examid=" + examid + "&pexamid=" + pexamid);
    LODOP.ADD_PRINT_TEXT(50, 544, 200, 34, "体检档案号：" + pexamid);
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.ADD_PRINT_TEXT(276, 224, 334, 87, "健康体检报告");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 40);
    LODOP.ADD_PRINT_TEXT(226, 224, 334, 50, "PHYSICAL CHECKUP REPORT");
    LODOP.SET_PRINT_STYLEA(0, "FontName", "Calibri");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
    LODOP.ADD_PRINT_TEXT(983, 179, 412, 28, "以宽裕情怀待健康，以和爱之心待复原");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 13);
    LODOP.SET_PRINT_STYLEA(0, "Alignment", 2);
    LODOP.ADD_PRINT_TEXT(1011, 78, 635, 26, "expecting health with relaxing attitude and expecting rehabilitation with a loving heart");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
    //导读
    LODOP.NewPageA();
    LODOP.ADD_PRINT_TEXT(84, 314, 157, 58, "导   读");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 18);
    LODOP.SET_PRINT_STYLEA(0, "Bold", 1);
    LODOP.ADD_PRINT_TEXT(170, 51, 194, 34, "尊敬的" + (function () {
            if ($("#sex").text() == '女') {
                return $("#patname").text() + "女士";
            } else {
                return $("#patname").text() + "先生";
            }
        })() + ":");
    var top_line = -23;
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 14);
    LODOP.ADD_PRINT_TEXT(208, 34, 666, 82, "    首先感谢您积极配合我们完成了本次医学检查，也衷心地感谢您对我们工作的支持和信任。本报告是我健康管理中心对您体检结果的分析汇总及建议指导。");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.ADD_PRINT_TEXT(288 + top_line, 34, 666, 95, "    通过本报告，可以帮助您更好地了解自身的健康状况，掌握自身健康动向，及时发现存在的健康危险因素，进而通过健康指导、风险干预等方式进行疾病预防及健康管理，我们可提供一对一的专家对体检报告解读。");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.ADD_PRINT_TEXT(371 + top_line, 34, 666, 31, "    本报告包括以下六部分：");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.ADD_PRINT_TEXT(401 + top_line, 34, 666, 31, "    第一部分 结果汇总");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.ADD_PRINT_TEXT(429 + top_line, 34, 666, 31, "    第二部分 结论及建议");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.ADD_PRINT_TEXT(459 + top_line, 34, 666, 31, "    第三部分 各科检查");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.ADD_PRINT_TEXT(488 + top_line, 34, 666, 31, "    第四部分 检验项目");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.ADD_PRINT_TEXT(517 + top_line, 34, 666, 31, "    第五部分 医技项目");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.ADD_PRINT_TEXT(546 + top_line, 34, 666, 31, "    第六部分 附各项检查结果报告单（图）");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.ADD_PRINT_TEXT(576 + top_line, 34, 666, 158, "    在此我们要提请您注意的是由于医学技术发展的局限、个体间可能存在的生物差异以及您选择的检查项目并未涵盖全身所有脏器，因此医生所做的结论及医学建议仅仅是依据您的陈述和本次检查的结果而得出的，通常这对大多数人而言是确切的，但鉴于任何一次医学检查的手段和方法都不具备绝对的特异性和灵敏度（即不存在100%的可靠和准确）这一事实，我们建议您对异常的结果进行随访复查和其他相关的检查，便于医生有更多更详实的医学证据去建立医学判断。");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.ADD_PRINT_TEXT(715 + top_line, 34, 666, 60, "    最后，我们欢迎并建议您每年至少来我中心进行一次系统检查，我们将为您提供历次体检结果对比，让您能够直观地了解自身近期的健康变化。");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    //第一部分  结果总汇
    LODOP.NEWPageA();
    LODOP.ADD_PRINT_URL(73, 34, "100%", 925, "pexam_resultSumInfo.htm?name=" + encodeURI(encodeURI($("#patname").text())) + "&sex=" + encodeURI(encodeURI($("#sex").text())) + "&pexamid=" + $('#pexamid').val());
    LODOP.SET_PRINT_STYLEA(0, "Offset2Top", 16);

    //第二部分  结论及建议
    LODOP.NEWPageA();
    LODOP.ADD_PRINT_URL(73, 34, "100%", 925, "pexam_resultSumInfo1.htm?name=" + encodeURI(encodeURI($("#patname").text())) + "&sex=" + encodeURI(encodeURI($("#sex").text())) + "&pexamid=" + $('#pexamid').val()) + "&type=" + "1" + "&node=1";
    LODOP.SET_PRINT_STYLEA(0, "Offset2Top", 16);

    //第三部分  各科检查
    LODOP.NEWPageA();
    LODOP.ADD_PRINT_URL(73, 34, "100%", 925, "everyItemsResult.htm?name=" + encodeURI(encodeURI($("#patname").text())) + "&sex=" + encodeURI(encodeURI($("#sex").text())) + "&pexamid=" + $('#pexamid').val());
    LODOP.SET_PRINT_STYLEA(0, "Offset2Top", 16);

    //（温馨提示部分）
    if (wxts_1 != "") {
        LODOP.NEWPageA();
        LODOP.ADD_PRINT_URL(73, 34, "100%", 925, "pexam_resultSumInfo2.htm?name=" + encodeURI(encodeURI($("#patname").text())) + "&sex=" + encodeURI(encodeURI($("#sex").text())) + "&pexamid=" + $('#pexamid').val()) + "&type=" + "2" + "&node=2";
        LODOP.SET_PRINT_STYLEA(0, "Offset2Top", 16);
    }
    setYemei(pexamid);

    var pexamflag = false;
    if (pexamflag) {
        LODOP.SET_PRINTER_INDEX("pexam");
        LODOP.PRINT();
    } else {
        LODOP.PREVIEW();
    }
}

function setYemei(pexamid) {
    //设置页眉页脚
    LODOP.ADD_PRINT_IMAGE(7, 23, 345, 63, "<img border='0' width='210px' style='float:left' src='../img/tjbg_top1.png'/>");
    LODOP.SET_PRINT_STYLEA(0, "ItemType", 1);
    LODOP.SET_PRINT_STYLEA(0, "PageUnIndex", "");
    LODOP.ADD_PRINT_SHAPE(4, 69, 28, 700, 1, 0, 1, "#000000");
    LODOP.SET_PRINT_STYLEA(0, "ItemType", 1);
    LODOP.ADD_PRINT_TEXT(50, 544, 200, 34, "体检档案号：" + pexamid);
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.SET_PRINT_STYLEA(0, "ItemType", 1);
    LODOP.ADD_PRINT_TEXT(1050, 50, 1500, 22, "                                                                                  第#页/共&页");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
    LODOP.SET_PRINT_STYLEA(0, "ItemType", 2);    //值2  代表设置页眉页脚
    LODOP.SET_PRINT_STYLEA(0, "NumberStartPage", 2); //设置页号的起始页
}

/**
 * 返回 日期的 大写格式
 */
function getDaXieDate(today) {
    //var today=new Date();
    var chinese = ['〇', '一', '二', '三', '四', '五', '六', '七', '八', '九'];
    var y = today.getFullYear().toString();
    var m = (today.getMonth() + 1).toString();
    var d = today.getDate().toString();
    var result = "";
    for (var i = 0; i < y.length; i++) {
        result += chinese[y.charAt(i)];
    }
    result += "年";
    if (m.length == 2) {
        if (m.charAt(0) == "1") {
            result += ("十" + chinese[m.charAt(1)] + "月");
        }
    }
    else {
        result += (chinese[m.charAt(0)] + "月");
    }
    if (d.length == 2) {
        result += (chinese[d.charAt(0)] + "十" + chinese[d.charAt(1)] + "日");
    }
    else {
        result += (chinese[d.charAt(0)] + "日");
    }
    return result;
}


function getText(deptsumText, br) {
    var arr = deptsumText.split(br);
    var str1 = '', str2 = '', str = '';
    for (i = 0; i < arr.length; i++) {
        str += insertStr(arr[i], 27, br);
    }
    arr = str.split(br);
    for (i = 0; i < arr.length; i++) {
        if (i < 17) {
            str1 += arr[i] + br;
        } else {
            str2 += arr[i] + br;
        }
    }
    return [str1, str2];
}
function insertStr(deptsumText, n, br) {
    var len = deptsumText.length;
    var strTemp = '';
    if (len > n) {
        strTemp = deptsumText.substring(0, n);
        deptsumText = deptsumText.substring(n, len);
        return strTemp + br + insertStr(deptsumText, n, br);
    } else {
        return deptsumText + br;
    }
}
//日期格式化
Date.prototype.format = function (format) {
    var o = {
        "M+": this.getMonth() + 1, //month
        "d+": this.getDate(), //day
        "h+": this.getHours(), //hour
        "m+": this.getMinutes(), //minute
        "s+": this.getSeconds(), //second
        "q+": Math.floor((this.getMonth() + 3) / 3), //quarter
        "S": this.getMilliseconds() //millisecond
    }

    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }

    for (var k in o) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }
    return format;
}