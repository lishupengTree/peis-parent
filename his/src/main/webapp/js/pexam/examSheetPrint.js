var LODOP;
var p_specimen_itemid;
var p_itemname;
$(document).ready(function () {
    $("body").prepend("<div style='display:none'><object id='LODOP' classid='clsid:2105C259-1E0C-4534-8141-A753534CB4CA' " +
        "width='0' height='0'><embed id='LODOP_EM' type='application/x-print-lodop' width='0' height='0'/></object></div>");
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

function doPrintABC(obj) {
    var itemtypes = $("#examtype").text();
    if (obj.pexamList.length > 0) {
        var patInfo = obj.pexamList[0];
        //var codePath=obj.codepath;
        LODOP_examSheet1(patInfo);
        /*
         $.ajax({
         async:false,
         cache:false,
         type:'post',
         url:"pexamNew/doPrintTM.htm",
         data:"pexamid="+selectpexamid+"&itemtypes="+itemtypes+"&time="+(new Date()).valueOf(),
         error:function(){
         alert('fail');
         },
         success:function(data){
         var jsons=eval('('+data+')');
         var json=[];
         var specimen_itemid;
         var specimen_itemid2;
         var itemname;
         var itemname2;
         alert(jsons.length);
         for(var i=0;i<jsons.length;i++){
         specimen_itemid=jsons[i].specimen_itemid;
         itemname=jsons[i].itemname;
         alert(specimen_itemid);
         alert(itemname);
         specimen_itemid2=jsons[i+1].specimen_itemid;
         itemname2=jsons[i+1].itemname;
         if(specimen_itemid2==specimen_itemid){
         itemname+='&nbsp;'+itemname2;
         }else{
         json.push({
         specimen_itemid:specimen_itemid,
         itemname:itemname
         });
         i+=-1;
         LODOP_barcode(obj,json);
         }
         }

         }

         });
         */
    }
}
//新的体检单子  --lsp

function LODOP_examSheet1(patInfo) {
    var obj;  //体检项目信息v
    var obj1;
    var obj2;
    var obj3;
    var pexamid = patInfo.pexamid; //体检编号
    var pt_info = {};//其他的信息
    //查询需要体检的项目
    $.ajax({
        async: false,//同步
        url: "pexamNew/getItemNameByPexamId.htm",
        type: "post",
        data: "pexamid=" + patInfo.pexamid + "&time=" + new Date(),
        error: function () {
            alert('ajax请求失败');
        },
        success: function (data) {
            if (data == 'fail') {
                alert('获取体检组合项目数据失败！');
                disabledButton(false);
            } else {
                var map = eval("(" + data + ")");
                obj = map.cq_list;  //餐前
                obj1 = map.ch_list; //餐后
                obj2 = map.jx_list;  //加项
                obj3 = map.cx_list;
            }
        }
    });
    //查询套餐的信息
    $.ajax({
        async: false,//同步
        url: "pexamNew/getGroupByPexamId.htm",
        type: "post",
        data: "pexamid=" + patInfo.pexamid + "&time=" + new Date(),
        error: function () {
            alert('ajax请求失败');
        },
        success: function (data) {
            if (data == 'fail') {
                alert('获取套餐等数据失败！');
                disabledButton(false);
            } else {
                var sp = data.split(',');
                pt_info.tjxh = sp[0];  //体检序号
                pt_info.getpage_time = sp[1];  //取报告时间
                pt_info.groupname = sp[2];  //套餐名称
            }
        }
    });

    LODOP.PRINT_INITA(0, 0, "210mm", "297mm", "体检导检单打印");
    LODOP.SET_PRINT_PAGESIZE(1, "210mm", "297mm", "");
    LODOP.NEWPAGE();
    //头部的图片和底纹
    LODOP.ADD_PRINT_IMAGE(7, 34, 700, 96, "URL:../img/print/a4.jpg");
    LODOP.SET_PRINT_STYLEA(0, "Stretch", 1);
    LODOP.ADD_PRINT_SETUP_BKIMG("<img border='0' src='../img/print/bgimg.png'/>");
    LODOP.SET_PRINT_MODE("NOCLEAR_AFTER_PRINT", true);
    LODOP.SET_SHOW_MODE("BKIMG_PRINT", 1);
    LODOP.ADD_PRINT_TEXT(98, 34, 435, 28, "套餐名称：" + pt_info.groupname);
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.SET_PRINT_STYLEA(0, "FontName", "Verdana, Geneva, sans-serif");
    LODOP.ADD_PRINT_TEXT(121, 34, 435, 28, "体检序号：" + pt_info.tjxh);
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.SET_PRINT_STYLEA(0, "FontName", "Verdana, Geneva, sans-serif");
    LODOP.ADD_PRINT_TEXT(144, 34, 500, 28, "姓    名：" + patInfo.patname + "               " + "年    龄：" + patInfo.age);
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.SET_PRINT_STYLEA(0, "FontName", "Verdana, Geneva, sans-serif");
    LODOP.ADD_PRINT_TEXT(167, 34, 500, 28, "性    别：" + patInfo.sex + "                   " + "联系电话：" + patInfo.phonecall);
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.SET_PRINT_STYLEA(0, "FontName", "Verdana, Geneva, sans-serif");
    LODOP.ADD_PRINT_TEXT(190, 34, 737, 27, "温馨提示：" + " 1.抽血后请务必按压5分钟，以免局部淤血。");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.SET_PRINT_STYLEA(0, "FontName", "Verdana, Geneva, sans-serif");
    LODOP.ADD_PRINT_TEXT(210, 34, 737, 27, "           2.近期备孕生育、疑似怀孕、怀孕女性及哺乳期女性请勿做X光检查及骨密度检查。");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.SET_PRINT_STYLEA(0, "FontName", "Verdana, Geneva, sans-serif");
    LODOP.ADD_PRINT_TEXT(230, 34, 737, 27, "           3.经期女性请您改期检查妇科及尿常规。");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.SET_PRINT_STYLEA(0, "FontName", "Verdana, Geneva, sans-serif");
    LODOP.ADD_PRINT_TEXT(250, 34, 737, 27, "           4.未婚或无性生活史的女性不做妇科检查及阴式彩超。");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.SET_PRINT_STYLEA(0, "FontName", "Verdana, Geneva, sans-serif");
    LODOP.ADD_PRINT_BARCODE(101, 543, 160, 65, "Code39", pexamid);
    //添加一个table
    var cssHtml = '<style type="text/css">'
        + '.cddzd{background:url(../img/print/bgimg.png); background-position:400px 300px;}'
        + 'table{border-right:1px solid #000;border-bottom:1px solid #000;border-top: 1px solid #000;border-left: 1px solid #000;}'
        //+'table{	margin:0px auto;font:Georgia 11px;color:#000;text-align:center;	border-collapse:collapse;font-size: 14px;font-family: Verdana, Geneva,sans-serif;background:url(bgimg.png);	background-position:-100px -100px;}'
        + 'table td{border:1px solid #000;}'
        + '</style>';
    var textHtml = "";
    var textHtml1 = "";//普通项目的html
    var textHtml2 = "";
    var textHtml3 = ""; //尾部的html
    var textHtml4 = "";  //加项的html
    textHtml3 = '<hr size="3" color="#000000" />' +
        '取报告时间：' + pt_info.getpage_time +
        '&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;客户签字：' +
        '<br/>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;如有延期项目，则取报告日顺延。    ' +
        '<br/>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&nbsp;&nbsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;' +
        '日期：' + new Date().format("yyyy-MM-dd");
    textHtml2 = '<p align="left" style="margin-left:7px;"><span style="font-weight: bold;">注意：</span><span>延期项目请于7个工作日内或预约延期内检查，逾期视为放弃该项目。</span></p>';
    var num = 0;
    var k = 0;
    //正常项目的 tr
    var nowdept = ''; //上一个科室的名字
    for (var i = 0; i < obj.length; i++) {
        if ((obj[i].deptname).indexOf('检验') > -1) {
            k++;
        }
        if (obj[i].groupname != null) {
            if (i == 0) {
                textHtml1 = textHtml1 + '<tr style="text-align:center;">'
                    + '<td width="50" rowspan="' + obj.length + '">餐前</td>'   //餐前餐后
                    + (function () {
                        if (obj[i].deptname == nowdept) {
                            return '';
                        } else {
                            return '<td width="80" rowspan="' + obj[i].num + '">' + (obj[i].deptname).substring(0, 4) + '</td>';
                        }
                    })()
                    + '<td width="210" align="left">' + ret_info1(obj[i].needtime, obj[i].itemname, obj[i].bookname) + '</td>'
                    + (function () {
                        if (obj[i].deptname == nowdept) {
                            if (nowdept.indexOf('检验') > -1) {
                                if (k >= obj3[0].cx + 1) {
                                    return '<td width="90" rowspan="1"></td>';
                                } else {
                                    return '';
                                }
                            } else {
                                return '';
                            }
                        } else {
                            nowdept = obj[i].deptname;
                            if (nowdept.indexOf('检验') > -1) {
                                return '<td width="90" rowspan="' + obj3[0].cx + '"></td>';
                            } else {
                                return '<td width="90" rowspan="' + obj[i].num + '"></td>';
                            }
                        }
                    })()
                    + '<td width="78"> </td>'
                    + '<td width="48"> </td>'
                    + '<td width="49"> </td>'
                    + '<td width="78"> </td>'
                    + '</tr>';
                num++;
            } else {
                textHtml1 = textHtml1 + '<tr style="text-align:center;">'
                    + (function () {
                        if (obj[i].deptname == nowdept) {
                            return '';
                        } else {
                            return '<td width="80" rowspan="' + obj[i].num + '">' + (obj[i].deptname).substring(0, 4) + '</td>';
                        }
                    })()
                    + '<td width="210" align="left">' + ret_info1(obj[i].needtime, obj[i].itemname, obj[i].bookname) + '</td>'
                    + (function () {
                        if (obj[i].deptname == nowdept) {
                            if (nowdept.indexOf('检验') > -1) {
                                if (k >= obj3[0].cx + 1) {
                                    return '<td width="90" rowspan="1"></td>';
                                } else {
                                    return '';
                                }
                            } else {
                                return '';
                            }
                        } else {
                            nowdept = obj[i].deptname;
                            if (nowdept.indexOf('检验') > -1) {
                                return '<td width="90" rowspan="' + obj3[0].cx + '">';
                            } else {
                                return '<td width="90" rowspan="' + obj[i].num + '"></td>';
                            }
                        }
                    })()
                    + '<td width="78"> </td>'
                    + '<td width="48"> </td>'
                    + '<td width="49"> </td>'
                    + '<td width="78"> </td>'
                    + '</tr>';
                num++;
            }
        }
    }
    //餐后的====
    nowdept = '';
    for (var i = 0; i < obj1.length; i++) {
        if (obj1[i].groupname != null) {
            if (i == 0) {
                textHtml1 = textHtml1 + '<tr style="text-align:center;">'
                    + '<td width="50" rowspan="' + obj1.length + '">餐后</td>'   //餐前餐后
                    + (function () {
                        if (obj1[i].deptname == nowdept) {
                            return '';
                        } else {
                            return '<td width="80" rowspan="' + obj1[i].num + '">' + (obj1[i].deptname).substring(0, 4) + '</td>';
                        }
                    })()
                    + '<td width="210" align="left">' + ret_info1(obj1[i].needtime, obj1[i].itemname, obj1[i].bookname) + '</td>'
                    + (function () {
                        if (obj1[i].deptname == nowdept) {
                            return '';
                        } else {
                            nowdept = obj1[i].deptname;
                            return '<td width="90" rowspan="' + obj1[i].num + '"></td>';
                        }
                    })()
                    + '<td width="78"> </td>'
                    + '<td width="48"> </td>'
                    + '<td width="49"> </td>'
                    + '<td width="78"> </td>'
                    + '</tr>';
                num++;
            } else {
                textHtml1 = textHtml1 + '<tr style="text-align:center;">'
                    + (function () {
                        if (obj1[i].deptname == nowdept) {
                            return '';
                        } else {
                            return '<td width="80" rowspan="' + obj1[i].num + '">' + (obj1[i].deptname).substring(0, 4) + '</td>';
                        }
                    })()
                    + '<td width="210" align="left">' + ret_info1(obj1[i].needtime, obj1[i].itemname, obj1[i].bookname) + '</td>'
                    + (function () {
                        if (obj1[i].deptname == nowdept) {
                            return '';
                        } else {
                            nowdept = obj1[i].deptname;
                            return '<td width="90" rowspan="' + obj1[i].num + '"></td>';
                        }
                    })()
                    + '<td width="78"> </td>'
                    + '<td width="48"> </td>'
                    + '<td width="49"> </td>'
                    + '<td width="78"> </td>'
                    + '</tr>';
                num++;
            }
        }
    }

    //加项的tr
    for (var i = 0; i < obj2.length; i++) {
        textHtml4 = textHtml4 + '<tr style="text-align:center;">'
            + '<td width="50">加项</td>'   //餐前餐后
            + '<td width="80">' + (obj2[i].deptname).substring(0, 4) + '</td>' 	//科室
            + '<td width="210" align="left">' + ret_info1(obj2[i].needtime, obj2[i].itemname, obj2[i].bookname) + '</td>'
            + '<td width="90"> </td>'
            + '<td width="78"> </td>'
            + '<td width="48"> </td>'
            + '<td width="49"> </td>'
            + '<td width="78"> </td>'
            + '</tr>';
    }
    if (obj2.length == 0) {
        for (var i = 0; i < 3; i++) {
            textHtml4 = textHtml4 + '<tr style="text-align:center;">'
                + '<td width="50">加项</td>'   //餐前餐后
                + '<td width="80"></td>' 	//科室
                + '<td width="210" align="left"></td>'
                + '<td width="90"> </td>'
                + '<td width="78"> </td>'
                + '<td width="48"> </td>'
                + '<td width="49"> </td>'
                + '<td width="78"> </td>'
                + '</tr>';
        }
    }
    if (obj2.length == 1) {
        for (var i = 0; i < 2; i++) {
            textHtml4 = textHtml4 + '<tr style="text-align:center;">'
                + '<td width="50">加项</td>'   //餐前餐后
                + '<td width="80"></td>' 	//科室
                + '<td width="210" align="left"></td>'
                + '<td width="90"> </td>'
                + '<td width="78"> </td>'
                + '<td width="48"> </td>'
                + '<td width="49"> </td>'
                + '<td width="78"> </td>'
                + '</tr>';
        }
    }
    if (obj2.length == 2) {
        textHtml4 = textHtml4 + '<tr style="text-align:center;">'
            + '<td width="50">加项</td>'   //餐前餐后
            + '<td width="80"></td>' 	//科室
            + '<td width="210" align="left"></td>'
            + '<td width="90"> </td>'
            + '<td width="78"> </td>'
            + '<td width="48"> </td>'
            + '<td width="49"> </td>'
            + '<td width="78"> </td>'
            + '</tr>';
    }

    textHtml = '<div class="cddzd"><table width="700" border="0" align="center" cellpadding="0" cellspacing="0">'
        + '<tr style="text-align:center;">'  //9个td
        + '<td width="50">&nbsp;</td>'   //餐前餐后
        + '<td width="80">科室</td>' 	//科室
        + '<td width="210">检查项目</td>'
        + '<td width="90">检查者签字</td>'
        + '<td width="78">导诊确认</td>'
        + '<td width="48">延期</td>'
        + '<td width="49">放弃</td>'
        + '<td width="78">客户签字</td>'
        + '</tr>'
        + textHtml1
        + '</table>'
        + '<br/>'
        + '<table width="700" border="0" align="center" cellpadding="0" cellspacing="0">'
        + textHtml4
        + '</table>'
        + textHtml2 + textHtml3 + '</div>';
    LODOP.ADD_PRINT_HTM(268, 15, 736, 800, cssHtml + textHtml);
    //页脚
    //LODOP.SET_SHOW_MODE("SETUP_ENABLESS","11111111001001");  //第11位是页脚设置

    LODOP.SET_LICENSES("杭州清普信息技术有限公司", "964657080837383919278901905623", "", "");
    var flag = LODOP.SET_PRINTER_INDEX("clc");
    if (flag) {
        LODOP.SET_PRINTER_INDEX("clc");
        LODOP.PRINT();
    } else {
        LODOP.PREVIEW();
        //LODOP.PRINT_DESIGN();
    }
}
//辅助函数 返回字符串
function ret_info(wxts, bookname) {
    var wxts1 = wxts == null ? "" : wxts;
    var bookname1 = bookname == null ? "" : bookname;
    if (wxts1 == '') {
        return wxts1 + "" + bookname1;
    } else {
        return wxts1 + "<br/>" + bookname1;
    }
}
function ret_info1(needtime, itemname, tys) {
    var needtime = needtime == null ? "" : '(' + needtime + '分钟)';
    var itemname = itemname == null ? "" : itemname;
    var tys = tys == null ? "" : '★';
    return tys + itemname + needtime;
}
//未检的时候打印导诊单
function LODOP_examSheet2(patInfo) {
    var obj;  //体检项目信息v
    var obj1;
    var obj2;
    var obj3;
    var pexamid = patInfo.pexamid; //体检编号
    var pt_info = {};//其他的信息
    //查询需要体检的项目
    $.ajax({
        async: false,//同步
        url: "pexamNew/getItemNameByPexamId.htm",
        type: "post",
        data: "pexamid=" + patInfo.pexamid + "&time=" + new Date(),
        error: function () {
            alert('ajax请求失败');
        },
        success: function (data) {
            if (data == 'fail') {
                alert('获取体检组合项目数据失败！');
                disabledButton(false);
            } else {
                var map = eval("(" + data + ")");
                obj = map.cq_list;  //餐前
                obj1 = map.ch_list; //餐后
                obj2 = map.jx_list;  //加项
                obj3 = map.cx_list;  //采血的数量
            }
        }
    });
    //查询套餐的信息
    $.ajax({
        async: false,//同步
        url: "pexamNew/getGroupByPexamId.htm",
        type: "post",
        data: "pexamid=" + patInfo.pexamid + "&time=" + new Date(),
        error: function () {
            alert('ajax请求失败');
        },
        success: function (data) {
            if (data == 'fail') {
                alert('获取套餐等数据失败！');
                disabledButton(false);
            } else {
                var sp = data.split(',');
                pt_info.tjxh = sp[0];  //体检序号
                pt_info.getpage_time = sp[1];  //取报告时间
                pt_info.groupname = sp[2];  //套餐名称
                pt_info.nowtime = sp[3];
            }
        }
    });

    LODOP.PRINT_INITA(0, 0, "210mm", "297mm", "体检导检单打印");
    LODOP.SET_PRINT_PAGESIZE(1, "210mm", "297mm", "");
    LODOP.NEWPAGE();
    //头部的图片和底纹
    LODOP.ADD_PRINT_IMAGE(7, 34, 700, 96, "URL:../img/print/a4.jpg");
    LODOP.SET_PRINT_STYLEA(0, "Stretch", 1);
    LODOP.ADD_PRINT_SETUP_BKIMG("<img border='0' src='../img/print/bgimg.png'/>");
    LODOP.SET_PRINT_MODE("NOCLEAR_AFTER_PRINT", true);
    LODOP.SET_SHOW_MODE("BKIMG_PRINT", 1);
    LODOP.ADD_PRINT_TEXT(98, 34, 435, 28, "套餐名称：" + pt_info.groupname);
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.SET_PRINT_STYLEA(0, "FontName", "Verdana, Geneva, sans-serif");
    LODOP.ADD_PRINT_TEXT(121, 34, 435, 28, "体检序号：" + pt_info.tjxh);
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.SET_PRINT_STYLEA(0, "FontName", "Verdana, Geneva, sans-serif");
    LODOP.ADD_PRINT_TEXT(144, 34, 500, 28, "姓    名：" + patInfo.patname + "               " + "年    龄：" + patInfo.age);
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.SET_PRINT_STYLEA(0, "FontName", "Verdana, Geneva, sans-serif");
    LODOP.ADD_PRINT_TEXT(167, 34, 500, 28, "性    别：" + patInfo.sex + "                   " + "联系电话：" + patInfo.phonecall);
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.SET_PRINT_STYLEA(0, "FontName", "Verdana, Geneva, sans-serif");
    LODOP.ADD_PRINT_TEXT(190, 34, 737, 27, "温馨提示：" + " 1.抽血后请务必按压5分钟，以免局部淤血。");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.SET_PRINT_STYLEA(0, "FontName", "Verdana, Geneva, sans-serif");
    LODOP.ADD_PRINT_TEXT(210, 34, 737, 27, "           2.近期备孕生育、疑似怀孕、怀孕女性及哺乳期女性请勿做X光检查及骨密度检查。");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.SET_PRINT_STYLEA(0, "FontName", "Verdana, Geneva, sans-serif");
    LODOP.ADD_PRINT_TEXT(230, 34, 737, 27, "           3.经期女性请您改期检查妇科及尿常规。");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.SET_PRINT_STYLEA(0, "FontName", "Verdana, Geneva, sans-serif");
    LODOP.ADD_PRINT_TEXT(250, 34, 737, 27, "           4.未婚或无性生活史的女性不做妇科检查及阴式彩超。");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 12);
    LODOP.SET_PRINT_STYLEA(0, "FontName", "Verdana, Geneva, sans-serif");
    LODOP.ADD_PRINT_BARCODE(101, 543, 160, 65, "Code39", pexamid);
    //添加一个table
    var cssHtml = '<style type="text/css">'
        + '.cddzd{background:url(../img/print/bgimg.png); background-position:400px 300px;}'
        + 'table{border-right:1px solid #000;border-bottom:1px solid #000;border-top: 1px solid #000;border-left: 1px solid #000;}'
        //+'table{	margin:0px auto;font:Georgia 11px;color:#000;text-align:center;	border-collapse:collapse;font-size: 14px;font-family: Verdana, Geneva,sans-serif;background:url(bgimg.png);	background-position:-100px -100px;}'
        + 'table td{border:1px solid #000;}'
        + '</style>';
    var textHtml = "";
    var textHtml1 = "";//普通项目的html
    var textHtml2 = "";
    var textHtml3 = ""; //尾部的html
    var textHtml4 = "";  //加项的html
    textHtml3 = '<hr size="3" color="#000000" />' +
        '取报告时间：' + pt_info.getpage_time +
        '&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;客户签字：' +
        '<br/>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;如有延期项目，则取报告日顺延。    ' +
        '<br/>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&nbsp;&nbsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;' +
        '日期：' + pt_info.nowtime;
    textHtml2 = '<p align="left" style="margin-left:7px;"><span style="font-weight: bold;">注意：</span><span>延期项目请于7个工作日内或预约延期内检查，逾期视为放弃该项目。</span></p>';
    var num = 0;
    //正常项目的 tr
    var nowdept = ''; //上一个科室的名字
    for (var i = 0; i < obj.length; i++) {
        if (obj[i].groupname != null) {
            if (i == 0) {
                textHtml1 = textHtml1 + '<tr style="text-align:center;">'
                    + '<td width="50" rowspan="' + obj.length + '">餐前</td>'   //餐前餐后
                    + (function () {
                        if (obj[i].deptname == nowdept) {
                            return '';
                        } else {
                            return '<td width="80" rowspan="' + obj[i].num + '">' + (obj[i].deptname).substring(0, 4) + '</td>';
                        }
                    })()
                    + '<td width="210" align="left">' + ret_info1(obj[i].needtime, obj[i].itemname, obj[i].bookname) + '</td>'
                    + (function () {
                        if (obj[i].deptname == nowdept) {
                            return '';
                        } else {
                            nowdept = obj[i].deptname;
                            return '<td width="90" rowspan="' + obj[i].num + '"></td>';
                        }
                    })()
                    + '<td width="78"> </td>'
                    + '<td width="48"> </td>'
                    + '<td width="49"> </td>'
                    + '<td width="78"> </td>'
                    + '</tr>';
                num++;
            } else {
                textHtml1 = textHtml1 + '<tr style="text-align:center;">'
                    + (function () {
                        if (obj[i].deptname == nowdept) {
                            return '';
                        } else {
                            return '<td width="80" rowspan="' + obj[i].num + '">' + (obj[i].deptname).substring(0, 4) + '</td>';
                        }
                    })()
                    + '<td width="210" align="left">' + ret_info1(obj[i].needtime, obj[i].itemname, obj[i].bookname) + '</td>'
                    + (function () {
                        if (obj[i].deptname == nowdept) {
                            return '';
                        } else {
                            nowdept = obj[i].deptname;
                            return '<td width="90" rowspan="' + obj[i].num + '"></td>';
                        }
                    })()
                    + '<td width="78"> </td>'
                    + '<td width="48"> </td>'
                    + '<td width="49"> </td>'
                    + '<td width="78"> </td>'
                    + '</tr>';
                num++;
            }
        }
    }
    //餐后的====
    nowdept = '';
    for (var i = 0; i < obj1.length; i++) {
        if (obj1[i].groupname != null) {
            if (i == 0) {
                textHtml1 = textHtml1 + '<tr style="text-align:center;">'
                    + '<td width="50" rowspan="' + obj1.length + '">餐后</td>'   //餐前餐后
                    + (function () {
                        if (obj1[i].deptname == nowdept) {
                            return '';
                        } else {
                            return '<td width="80" rowspan="' + obj1[i].num + '">' + (obj1[i].deptname).substring(0, 4) + '</td>';
                        }
                    })()
                    + '<td width="210" align="left">' + ret_info1(obj1[i].needtime, obj1[i].itemname, obj1[i].bookname) + '</td>'
                    + (function () {
                        if (obj1[i].deptname == nowdept) {
                            return '';
                        } else {
                            nowdept = obj1[i].deptname;
                            return '<td width="90" rowspan="' + obj1[i].num + '"></td>';
                        }
                    })()
                    + '<td width="78"> </td>'
                    + '<td width="48"> </td>'
                    + '<td width="49"> </td>'
                    + '<td width="78"> </td>'
                    + '</tr>';
                num++;
            } else {
                textHtml1 = textHtml1 + '<tr style="text-align:center;">'
                    + (function () {
                        if (obj1[i].deptname == nowdept) {
                            return '';
                        } else {
                            return '<td width="80" rowspan="' + obj1[i].num + '">' + (obj1[i].deptname).substring(0, 4) + '</td>';
                        }
                    })()
                    + '<td width="210" align="left">' + ret_info1(obj1[i].needtime, obj1[i].itemname, obj1[i].bookname) + '</td>'
                    + (function () {
                        if (obj1[i].deptname == nowdept) {
                            return '';
                        } else {
                            nowdept = obj1[i].deptname;
                            return '<td width="90" rowspan="' + obj1[i].num + '"></td>';
                        }
                    })()
                    + '<td width="78"> </td>'
                    + '<td width="48"> </td>'
                    + '<td width="49"> </td>'
                    + '<td width="78"> </td>'
                    + '</tr>';
                num++;
            }
        }
    }

    //加项的tr
    for (var i = 0; i < obj2.length; i++) {
        textHtml4 = textHtml4 + '<tr style="text-align:center;">'
            + '<td width="50">加项</td>'   //餐前餐后
            + '<td width="80">' + (obj2[i].deptname).substring(0, 4) + '</td>' 	//科室
            + '<td width="210" align="left">' + ret_info1(obj2[i].needtime, obj2[i].itemname, obj2[i].bookname) + '</td>'
            + '<td width="90"> </td>'
            + '<td width="78"> </td>'
            + '<td width="48"> </td>'
            + '<td width="49"> </td>'
            + '<td width="78"> </td>'
            + '</tr>';
    }
    if (obj2.length == 0) {
        for (var i = 0; i < 3; i++) {
            textHtml4 = textHtml4 + '<tr style="text-align:center;">'
                + '<td width="50">加项</td>'   //餐前餐后
                + '<td width="80"></td>' 	//科室
                + '<td width="210" align="left"></td>'
                + '<td width="90"> </td>'
                + '<td width="78"> </td>'
                + '<td width="48"> </td>'
                + '<td width="49"> </td>'
                + '<td width="78"> </td>'
                + '</tr>';
        }
    }
    if (obj2.length == 1) {
        for (var i = 0; i < 2; i++) {
            textHtml4 = textHtml4 + '<tr style="text-align:center;">'
                + '<td width="50">加项</td>'   //餐前餐后
                + '<td width="80"></td>' 	//科室
                + '<td width="210" align="left"></td>'
                + '<td width="90"> </td>'
                + '<td width="78"> </td>'
                + '<td width="48"> </td>'
                + '<td width="49"> </td>'
                + '<td width="78"> </td>'
                + '</tr>';
        }
    }
    if (obj2.length == 2) {
        textHtml4 = textHtml4 + '<tr style="text-align:center;">'
            + '<td width="50">加项</td>'   //餐前餐后
            + '<td width="80"></td>' 	//科室
            + '<td width="210" align="left"></td>'
            + '<td width="90"> </td>'
            + '<td width="78"> </td>'
            + '<td width="48"> </td>'
            + '<td width="49"> </td>'
            + '<td width="78"> </td>'
            + '</tr>';
    }

    textHtml = '<div class="cddzd"><table width="700" border="0" align="center" cellpadding="0" cellspacing="0">'
        + '<tr style="text-align:center;">'  //9个td
        + '<td width="50">&nbsp;</td>'   //餐前餐后
        + '<td width="80">科室</td>' 	//科室
        + '<td width="210">检查项目</td>'
        + '<td width="90">检查者签字</td>'
        + '<td width="78">导诊确认</td>'
        + '<td width="48">延期</td>'
        + '<td width="49">放弃</td>'
        + '<td width="78">客户签字</td>'
        + '</tr>'
        + textHtml1
        + '</table>'
        + '<br/>'
        + '<table width="700" border="0" align="center" cellpadding="0" cellspacing="0">'
        + textHtml4
        + '</table>'
        + textHtml2 + textHtml3 + '</div>';
    LODOP.ADD_PRINT_HTM(268, 15, 736, 800, cssHtml + textHtml);
    //页脚
    //LODOP.SET_SHOW_MODE("SETUP_ENABLESS","11111111001001");  //第11位是页脚设置

    LODOP.SET_LICENSES("杭州清普信息技术有限公司", "964657080837383919278901905623", "", "");
    var flag = LODOP.SET_PRINTER_INDEX("clc");
    if (flag) {
        LODOP.SET_PRINTER_INDEX("clc");
        LODOP.PRINT();
    } else {
        LODOP.PREVIEW();
        //LODOP.PRINT_DESIGN();
    }
}


function LODOP_examSheet(patInfo) {
    var basePath = $("#basePath").val();
    var codePath = patInfo.codepath;
    // var url =basePath+codePath;
    var newDate = new Date();
    var pexamtype = $("#pexamtype").val();//套餐类型
    var tipname = pexamtype.substring(0, pexamtype.length - 2);//类型表头
    var isDishDept = $("#isDishDept").val();//是否区分科室
    var isPrintA5 = $("#isPrintA5").val();//是否打印A5
    var isPrintCervical = $("#isPrintCervical").val();//退休宫颈刮片是否打印
    var hosname = $("#hosname").val();//医院名字
    var hosnum = $("#hosnum").val();
    //alert("isDishDept:"+isDishDept);
    //alert("isPrintA5:"+isPrintA5);
    //alert("pexamtype:"+pexamtype);
    pexamYeas = newDate.format('yyyy');
    pexamMonth = newDate.format('MM');
    pexamDay = newDate.format('dd');
    var flag = LODOP.SET_PRINTER_INDEX("A4");
    //var flag = true;
    //if(!flag){
    //alert("指定打印机不存在");
    //}
    var nowdate = new Date();
    var date = nowdate.format("yyyy-MM-dd");
    LODOP.PRINT_INITA(0, 0, 900, 2000, "");
    //LODOP.NEWPAGE();//强制分页
    var cssHtml = '<style type="text/css">'
        + '.div01{ width:720px; margin:0 auto; font-size:13px; font-family:Verdana, Geneva, sans-serif; overflow:hidden;}'
        + '.div02{ height:10px; font-size:25px; text-align:center;}'
        + '.div021{ font-size:14px;}'
        + '.div022{ height:50px;line-height:40px; font-size:25px; text-align:center;}'
        + '.div0222{ height:10px; font-size:22px; text-align:center;}'
        + '.div02222{ height:10px; font-size:25px; text-align:center;}'
        + '.div03 td{ height:24px; line-height:24px;font-size:14px}'
        + '.div033 td{ height:35px; line-height:35px;font-size:14px}'
        + '.div034 td{ height:24px; line-height:24px;font-size:14px}'
        + '.div0333 td{ height:20px; line-height:20px;font-size:14px}'
        + '.div04{ background:none; border: none; border-bottom:0px solid #000;font-size:16px;text-decoration:none}'
        + '.div044{ background:none; border: none; border-bottom:0px solid #000;font-size:14px;text-decoration:none}'
        + '.div05{ padding:0px 10px;}'
        + '.div06{ background-color:#7b7b7b;}'
        + '.div06 td{ background-color:#fff;}'
        + '.div07{ background:none; border:none; width:100%; overflow:hidden; height:25px;}'
        + 'input{ text-decoration:none }'
        + '.div08{ width:20px; background:none; border: none; border-bottom:0px solid #000;font-size:16px;text-decoration:none}'
        + '.div088{ width:20px; background:none; border: none; border-bottom:0px solid #000;font-size:14px;text-decoration:none}'
        + '.div09{ background:none; border:none; width:100%; overflow:hidden; height:90px;}'
        + '.div009{ background:none; border:none; width:100%; overflow:hidden; height:180px;}'
        + '.div091{ background:none; border:none; width:100%; overflow:hidden; height:60px;}'
        + '.div092{ background:none; border:none; width:100%; overflow:hidden; height:40px;}'
        + '.div093{ background:none; border:none; width:100%; overflow:hidden; height:50px;}'
        + '.div072{ background:none; border:none; width:100%; overflow:hidden; height:50px;}'
        + '.div10{ background:none; border:none; width:100%; overflow:hidden; height:20px;font-size:13px;}'
        + '.div100{ background:none; border:none; width:100%; overflow:hidden; height:40px;}'
        + '.div11{ font-size:14px}'
        + '.div12{ width:100px}'
        + '.div13{ width:50px}'
        + '.div14{ width:150px}'
        + '.div15{ width:70px}'
        + '.div16{ background:none; border:none; width:100%; overflow:hidden; height:10px;}'
        + '.td1{  border-left-style: none }'
        + '.td2{  border-right-style: none }'
        + '</style>';
    var textHtml = "";
    var textHtml2 = "";
    var textHtml3 = "";
    var textHtmlT1 = "";
    var textHtmlT2 = "";
    var textHtmlT3 = "";
    var textHtmlT4 = "";
    var textHtmlT5 = "";
    var textHtmlT6 = "";
    var textHtmlT7 = "";
    //查询需要体检的项目
    $.ajax({
        async: false,//同步
        cache: false,
        url: "pexamNew/getItemNameByPexamId.htm",
        type: "post",
        data: "pexamid=" + patInfo.pexamid + "&time=" + new Date(),
        error: function () {
            alert('ajax请求失败');
        },
        success: function (data) {
            if (data == 'fail') {
                alert('获取记录单数据失败！');
                disabledButton(false)
            } else {
                var obj = eval("(" + data + ")");
                textHtml = '<div class="div01">'
                    + '<table width="720" border="0" cellspacing="0" cellpadding="0" height="15" class="div06" >'
                    + '<tr height="10">'
                    + '<td style="width:80%;" >'
                    + '<div class="div022">' + hosname + '体检记录表</div>'
                    + '</td>'
                    + '<td algin="right">'
                    + '<div  style="float:right">'
                    + '<table  border="0" cellspacing="0" cellpadding="0" height="15" class="div06" >'
                    + '<tr>'
                    + '<td>&nbsp;</td>'
                    + '</tr>'
                    + '</table>'
                    + '</div>'
                    + '</td>'
                    + '</tr>'
                    + '</table>'
                    + '<table width="720" border="0" cellspacing="0" cellpadding="0" height="15" class="div06" >'
                    + '<tr height="20">'
                    + '<td width="80" align="left">姓&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;名:</td>'
                    + '<td width="150" align="left"><strong><input type="text" name="textfield13" id="textfield13"  class="div04" style="width:100%;text-decoration:none" value="' + patInfo.patname + '"/></strong></td>'
                    + '<td width="45"  align="left" style="padding-left:12px">性别:</td>'
                    + '<td width="40"  align="left"><input type="text" name="textfield" id="textfield"  class="div08" value="' + patInfo.sex + '"/></td>'
                    + '<td width="48"  align="left" style="padding-right:3px">年龄:</td>'
                    + '<td width="45"  align="left"><input type="text" name="textfield13" id="textfield13"  class="div04" style="width:20px" value="' + patInfo.age + '" /></td>'
                    + '<td width="80"  align="right">身份证号:</td>'
                    + '<td align="left"><input type="text" name="textfield13" id="textfield13"  class="div04" style="width:100%;text-align:left;" value="' + patInfo.idnum + '" /></td>'
                    + '</tr>'
                    + '<tr height="20">'
                    + '<td align="left">联系电话:</td>'
                    + '<td align="left"><strong><input type="text" name="textfield13" id="textfield13"  class="div04" style="width:100%" value="' + patInfo.phonecall + '"/></strong></td>'
                    + '<td align="left" style="padding-left:12px">地址:</td>'
                    + '<td   align="left" colspan="5"><input type="text" name="textfield" id="textfield"  style="width:98%;background:none; border: none; border-bottom:0px solid #000;font-size:16px;" value="' + patInfo.address + '"/></td>'
                    + '</tr>'
                    + '</table>'
                    + '<table width="720" border="0" cellspacing="1" cellpadding="3" class="div06 div03" style="margin-top:15px;">';
                var objDx = new Array("一、一般项目", "二、检验项目", "三、医技项目");
                var j = 0;
                var statusComclass = "";
                //特殊处理，一般检查项目增加视力。眼科医生录入系统，因为眼科无法放下机器
                for (var i = 0; i < obj.length; i++) {
                    var bookname = obj[i].bookname; //知情同意书
                    var itemname = bookname == null ? obj[i].itemname : obj[i].itemname + '<br/>&nbsp;&nbsp;&nbsp;&nbsp;' + '<' + bookname + '>'; //项目名称
                    if (statusComclass == "" || statusComclass != obj[i].comclass) {
                        textHtml += '<tr height="50px;">'
                            + '<td colspan="7" align="left" >' + objDx[j] + '</td>'
                            + '</tr>';
                        j++;
                        if (obj[i].comclass == "" || obj[i].comclass == "其他") {
                            statusComclass = "其他";
                        } else {
                            statusComclass = obj[i].comclass;
                        }
                    }
                    if (i != 0 && i % 16 == 0) {
                        textHtml += '</table><div style="height:80px;"></div>'
                            + '<table width="720" border="0" cellspacing="1" cellpadding="3" class="div06 div03" style="margin-top:15px;">';
                    }
                    if (obj[i].itemname == "一般检查" || obj[i].itemname == "一般检查(全)") {
                        textHtml += '<tr height="0px">'
                            + '<td align="left" width="200px;">&emsp;&emsp;' + itemname + '</td>'
                            + '<td colspan="6" align="right"><textarea name="textarea3" id="textarea3" cols="45" rows="1" class="div10">裸眼视力：左:   右 :    </textarea>'
                            + '<textarea cols="45" rows="1" class="div10" style="float:left;width:40%">矫正视力：左:   右 :    </textarea><div style="float:right;width:35%">医师签名：<input type="text" name="textfield10" id="textfield10"  class="div04" style="width:100px"/></div>'
                            + '</td>'
                            + '</tr>'
                    } else {
                        textHtml += '<tr height="0px">'
                            + '<td align="left" width="200px;">&emsp;&emsp;' + itemname + '</td>'
                            + '<td colspan="6" align="right"><textarea name="textarea3" id="textarea3" cols="45" rows="1" class="div10"></textarea>'
                            //+			obj[i].deptname
                            + '医师签名：'
                            + '<input type="text" name="textfield10" id="textfield10"  class="div04" style="width:100px"/>'
                            + '</td>'
                            + '</tr>'
                    }
                }
                textHtml += "</table></div>";
                LODOP.SET_PRINT_PAGESIZE(1, 0, 0, "A4");//纸张大小
                if (textHtml != "") {
                    LODOP.ADD_PRINT_HTM(30, 16, 821, 12000, cssHtml + textHtml);
                    LODOP.ADD_PRINT_BARCODE(46, 588, 150, 40, "Code39", patInfo.pexamid);
                }
                if (flag) {
                    LODOP.SET_PRINTER_INDEX("A4");
                    LODOP.PRINT();
                } else {
                    //LODOP.PRINT_DESIGN();
                    LODOP.PREVIEW();
                }
                //LODOP.PRINT_DESIGN();
            }
            //alert(textHtml);
        }
    });

}

function LODOP_womanExamSheet() {
    var cssHtml = '<style type="text/css">'
        + '.div01{ width:720px; margin:0 auto; font-size:14px; font-family:Verdana, Geneva, sans-serif;}'
        + '.div02{ height:60px; font-size:25px; text-align:center;}'
        + '.div03 td{ height:30px; line-height:30px;}'
        + '.div04{ width:40px; background:none; border: none; border-bottom:1px solid #000;}'
        + '.div05{ padding:0px 10px;}'
        + '.div06{ background-color:#7b7b7b;}'
        + '.div06 td{ background-color:#fff;}'
        + '.div07{ background:none; border:none; width:100%; overflow:hidden; height:25px;}'
        + '</style>';
    var textHtml = '<div class="div01">'
        + '<div class="div02"><strong>就业人员妇科检查记录表</strong></div>'
        + '<table width="650" border="0" cellspacing="0" cellpadding="0" class="div03">'
        + '<tr>'
        + '<td>一、既往病史</td>'
        + '</tr>'
        + '<tr>'
        + '<td>1.月经史：初潮年龄'
        + '<input type="text" name="textfield" id="textfield"  class="div04"/>'
        + '岁，月经周期'
        + '<input type="text" name="textfield2" id="textfield2"  class="div04"/>'
        + '/'
        + '<input type="text" name="textfield3" id="textfield3"  class="div04"/>'
        + '，末次月经'
        + '<input type="text" name="textfield4" id="textfield4"  class="div04"/>'
        + '年'
        + '<input type="text" name="textfield5" id="textfield5"  class="div04"/>'
        + '月'
        + '<input type="text" name="textfield6" id="textfield6"  class="div04"/>'
        + '日'
        + '</td>'
        + '</tr>'
        + '<tr>'
        + '<td>2.生育史：孕次'
        + '<input type="text" name="textfield7" id="textfield7"  class="div04"/>'
        + '，现有子女数'
        + '<input type="text" name="textfield8" id="textfield8"  class="div04"/>'
        + '，人流次数'
        + '<input type="text" name="textfield9" id="textfield9"  class="div04"/>'
        + '，自然流产'
        + '<input type="text" name="textfield10" id="textfield10"  class="div04"/>'
        + '，引产'
        + '<input type="text" name="textfield11" id="textfield11"  class="div04"/>'
        + '</td>'
        + '</tr>'
        + '<tr>'
        + '<td style="padding-left:70px">避孕措施：'
        + '<span class="div05">放环</span>'
        + '<span class="div05">工具</span>'
        + '<span class="div05">服药</span>'
        + '<span class="div05">结扎</span>其他'
        + '<input type="text" name="textfield12" id="textfield12"  class="div04" style=" width:200px"/>'
        + '</td>'
        + '</tr>'
        + '<tr>'
        + '<td></td>'
        + '</tr>'
        + '<tr>'
        + '<td>二、体检情况</td>'
        + '</tr>'
        + '</table>'
        + '<table width="650" border="0" cellspacing="1" cellpadding="3" class="div03 div06">'
        + '<tr>'
        + '<td rowspan="6" width="25" align="center">妇<br />科<br />检<br />查</td>'
        + '<td rowspan="3" width="50" align="center">常规</td>'
        + '<td width="80">外阴</td>'
        + '<td colspan="2">&nbsp;</td>'
        + '<td colspan="2" align="right">阴道及穹隆</td>'
        + '<td colspan="3">&nbsp;</td>'
        + '<td align="right">宫颈</td>'
        + '<td width="41">&nbsp;</td>'
        + '</tr>'
        + '<tr>'
        + '<td>子宫位置</td>'
        + '<td width="41">&nbsp;</td>'
        + '<td width="40">大小</td>'
        + '<td width="59">&nbsp;</td>'
        + '<td width="59" align="right">形状</td>'
        + '<td width="41">&nbsp;</td>'
        + '<td width="40">质地</td>'
        + '<td width="33" align="right">&nbsp;</td>'
        + '<td width="56" align="right">活动度</td>'
        + '<td>&nbsp;</td>'
        + '</tr>'
        + '<tr>'
        + '<td>子宫肿块</td>'
        + '<td colspan="2">&nbsp;</td>'
        + '<td colspan="2" align="right">左侧附件</td>'
        + '<td colspan="2">&nbsp;</td>'
        + '<td colspan="2" align="right">右侧附件</td>'
        + '<td>&nbsp;</td>'
        + '</tr>'
        + '<tr>'
        + '<td rowspan="3" align="center">阴道<br />分泌<br />物检<br />查</td>'
        + '<td>白带清洁度</td>'
        + '<td colspan="3">&nbsp;</td>'
        + '<td align="right">滴虫</td>'
        + '<td colspan="3">&nbsp;</td>'
        + '<td align="right">霉菌</td>'
        + '<td>&nbsp;</td>'
        + '</tr>'
        + '<tr>'
        + '<td>PH值</td>'
        + '<td colspan="3">&nbsp;</td>'
        + '<td align="right">细菌</td>'
        + '<td colspan="3">&nbsp;</td>'
        + '<td align="right">其它</td>'
        + '<td>&nbsp;</td>'
        + '</tr>'
        + '<tr>'
        + '<td colspan="10">妇科检查诊断建议：</td>'
        + '</tr>'
        + '<tr>'
        + '<td colspan="2" rowspan="2">宫颈癌筛查</td>'
        + '<td colspan="3" >脱落细胞巴氏涂片检查</td>'
        + '<td colspan="7" ></td>'
        + '</tr>'
        + '<tr>'
        + '<td colspan="3" >*宫颈液基薄层细胞检查</td>'
        + '<td colspan="7" ></td>'
        + '</tr>'
        + '<tr>'
        + '<td colspan="2">*阴道B超<br />检查：</td>'
        + '<td colspan="10" align="right">'
        + '<label>'
        + '<textarea name="textarea" id="textarea" cols="45" rows="5" class="div07"></textarea>'
        + '</label>'
        + '医师签名：'
        + '<input type="text" name="textfield13" id="textfield13"  class="div04" style="width:100px"/>'
        + '</td>'
        + '</tr>'
        + '<tr>'
        + '<td colspan="2">检查诊断<br />小结：</td>'
        + '<td colspan="10"><textarea name="textarea2" id="textarea2" cols="45" rows="5" class="div07"></textarea></td>'
        + '</tr>'
        + '<tr>'
        + '<td colspan="2">建议：</td>'
        + '<td colspan="10" align="right"><textarea name="textarea3" id="textarea3" cols="45" rows="5" class="div07"></textarea>'
        + '医师签名：'
        + '<input type="text" name="textfield13" id="textfield13"  class="div04" style="width:100px"/>'
        + '</td>'
        + '</tr>'
        + '<tr>'
        + '<td colspan="12" align="">心电图：'
        + '<textarea name="textarea4" id="textarea4" cols="45" rows="5" class="div07" style="height:150px;"></textarea>'
        + '<div style="text-align:right">医师签名：'
        + '<input type="text" name="textfield13" id="textfield13"  class="div04" style="width:100px"/></div>'
        + '</td>'
        + '</tr>'
        + '</table>'
        + '</div>';
    LODOP.NEWPAGE();//强制分页
    LODOP.PRINT_INITA(0, 0, 900, 2000, "");
    LODOP.ADD_PRINT_HTM(30, 33, 721, 2000, cssHtml + textHtml);
    LODOP.SET_PRINT_PAGESIZE(1, 0, 0, "A4");//纸张大小
    //LODOP.SET_PRINTER_INDEX("LJ3600D");
    //LODOP.PREVIEW();
    LODOP.PRINT_DESIGN();
    //LODOP.PRINT();
}

//检查条码打印
function LODOP_barcodeDEL(obj, json) {
    LODOP.PRINT_INIT("打印检验条码");
    //var flag = LODOP.SET_PRINTER_INDEX("TSC TTP-244 Plus");
    var flag = LODOP.SET_PRINTER_INDEX("barcode");
    p_specimen_itemid = json[0].specimen_itemid;
    p_itemname = json[0].itemname;
    //alert(p_itemname);
    LODOP.PRINT_INITA(5, 0, "50mm", "30mm", "");
    LODOP.SET_PRINT_PAGESIZE(1, "50mm", "30mm", "");
    LODOP.ADD_PRINT_TEXT(2, 5, 65, 25, "体检");//时间
    LODOP.SET_PRINT_TEXT_STYLEA(1, "宋体", 10, 1, 0, 0, 1, "");
    LODOP.ADD_PRINT_TEXT(2, 55, 140, 25, (new Date()).format('yyyy-MM-dd hh:mm'));//时间
    LODOP.SET_PRINT_STYLEA(0, "FontName", "宋体");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
    //LODOP.ADD_PRINT_IMAGE(19,5,128,46,"<img border='0' src='"+obj.codePath+"'/>");//条码图片地址
    LODOP.ADD_PRINT_BARCODE(19, 5, 128, 46, "Code39", p_specimen_itemid);
    LODOP.ADD_PRINT_IMAGE(50, 5, 128, 46, "<img border='0' src='../img/white.png'/>");
    LODOP.ADD_PRINT_TEXT(55, 5, 190, 20, obj.pexamList[0].pexamid + " " + obj.pexamList[0].patname + " " + obj.pexamList[0].sex + " " + obj.pexamList[0].age + "岁");
    LODOP.SET_PRINT_STYLEA(0, "FontName", "宋体");
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
    //LODOP.SET_PRINTER_INDEX("TSC TTP-244 Plus");
    //LODOP.PRINT();
    if (flag) {
        LODOP.SET_PRINTER_INDEX("barcode");
        LODOP.PRINT();
    } else {
        LODOP.PREVIEW();
    }
    //LODOP.PRINT_DESIGN();
    json.pop();
}

//日期处理
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

//----------2013-09-19 2.0版体检 xc-------------------------
//重打条形码

function doACodePrint() {
    var checkIds = "";
    var obj = document.getElementsByName("infos");
    for (var i = 0; i < obj.length; i++) {
        if (obj[i].checked) {
            checkIds += obj[i].itemuuid + "/";
        }
    }
    $.ajax({
        async: false,//同步
        cache: false,
        url: "pexamNew/doReplaceCodePrint.htm",
        type: "post",
        data: "pexamid=" + selectpexamid + "&examid=" + selectexamid + "&checkIds=" + checkIds,
        error: function () {
            alert('ajax请求失败');
        },
        success: function (data) {
            if (data == 'fail') {
                alert('失败！');
                disabledButton(false)
            } else {
                var obj = eval("(" + data + ")");
                //if($("#pexamtype").val()=="农民体检"){//打印条码
                var json = obj.barCodeList;//检验条码数据
                if (checkIds == "") {
                    alert("请选择要打印的条码");
                } else {
                    if (json.length > 0) {
                        LODOP_barcode(json);
                    }
                }
                //}
            }
        }
    });
}

/*
 Map barCodeMap = new HashMap();
 barCodeMap.put("shortname", shortname);//医院简写
 barCodeMap.put("pexamid", pexamid);//体检编号
 barCodeMap.put("excdeptname", excdeptname);//执行科室

 barCodeMap.put("itemname", itemname);//大项名称
 barCodeMap.put("itemuuid", tmcode);//主键--即条码
 barCodeMap.put("doctorname", username);//开单医生
 barCodeMap.put("patientname", patname);//体检人姓名
 barCodeMap.put("sex", sex);//性别
 barCodeMap.put("age", age);//年龄
 barCodeMap.put("printdate", dateFormat.format(new Date()));//打印时间
 */
//打印检验条码
function LODOP_barcode(json) {
    LODOP.PRINT_INIT("打印检验条码");
    var flag = LODOP.SET_PRINTER_INDEX("barcode");
    var isPrintSelfInfo = $("#isPrintSelfInfo").val();
    if (isPrintSelfInfo == 'Y') {
        //---打印个人信息---
        LODOP.PRINT_INITA(5, 0, "50mm", "30mm", "二维条码（小）");
        LODOP.SET_PRINT_PAGESIZE(1, "50mm", "30mm", "");

        LODOP.ADD_PRINT_IMAGE(30, 5, 155, 45, "<img border='0' src='../img/white.png'/>");//白色图片
        LODOP.ADD_PRINT_BARCODE(30, 5, 155, 45, "Code39", json[0].pexamid);

        LODOP.SET_PRINT_STYLE('FontSize', 13);
        LODOP.ADD_PRINT_TEXT(5, 10, 70, 20, json[0].patientname);//姓名

        LODOP.SET_PRINT_STYLE('FontSize', 13);
        LODOP.ADD_PRINT_TEXT(5, 90, 20, 20, json[0].sex);//性别

        LODOP.SET_PRINT_STYLE('FontSize', 13);
        LODOP.ADD_PRINT_TEXT(5, 110, 30, 20, json[0].age);//年龄

        LODOP.SET_PRINTER_INDEX("barcode");
        LODOP.PRINT();
        //LODOP.PRINT_DESIGN();
    }
    for (var i = 0; i < json.length; i++) {
        LODOP.PRINT_INITA(0, 0, "50mm", "30mm", "二维条码（小）");
        LODOP.SET_PRINT_PAGESIZE(1, "50mm", "30mm", "");
        var shortname = json[i].shortname;
        LODOP.SET_PRINT_STYLE('FontSize', 10);
        var endnum = json[i].excdeptname.indexOf("（");
        var excdeptname = (endnum == -1) ? json[i].excdeptname : json[i].excdeptname.substring(0, endnum);
        LODOP.ADD_PRINT_TEXT(2, 6, 300, 15, shortname + '  ' + excdeptname + "(体检)");//医院缩写  执行科室
        //LODOP.ADD_PRINT_IMAGE(20,5,155,45,"<img border='0' src='../img/white.png'/>");//白色图片
        LODOP.ADD_PRINT_BARCODE(20, 35, 120, 45, "EAN128C", +json[i].itemuuid);//条码
        var itemname = json[i].xgys == '' ? json[i].itemname : '(' + json[i].xgys + ')' + json[i].itemname;
        LODOP.ADD_PRINT_TEXT(68, 2, 188, 25, itemname);//项目名称
        LODOP.ADD_PRINT_TEXT(83, 2, 188, 20, json[i].pexamid + '  ' + json[i].patientname + '  ' + json[i].sex + ' ' + json[i].age);//体检编号  姓名 性别  年龄
        if (json[i].sfws == '是') {//如果是外送的话
            LODOP.ADD_PRINT_TEXT(19, 148, 22, 43, '外送');
        }
        LODOP.ADD_PRINT_TEXT(97, 4, 275, 20, "打印时间：" + new Date().format('yyyy-MM-dd hh:mm:ss'));
        LODOP.SET_PRINT_STYLEA(0, "FontSize", 9);
        if (flag) {
            //LODOP.PRINT_DESIGN();
            LODOP.SET_PRINTER_INDEX("barcode");
            LODOP.PRINT();
        } else {
            //LODOP.PRINT_DESIGN();
            LODOP.PREVIEW();
        }
    }
}

/*
 Map examBarCodeMap = new HashMap();
 examBarCodeMap.put("zsname", "体检");//主诉名称
 examBarCodeMap.put("itemname", itemname);//大项名称
 examBarCodeMap.put("doctorname", username);//开单医生
 examBarCodeMap.put("pexamid", pexamid);//体检人员编号
 examBarCodeMap.put("patientname", patname);//体检人姓名
 examBarCodeMap.put("sex", sex);//性别
 examBarCodeMap.put("age", age);//年龄
 examBarCodeMap.put("printdate", dateFormat.format(new Date()));//打印时间
 */
function LODOP_barcodeExem(json) {
    LODOP.PRINT_INIT("打印检查条码");
    var flag = LODOP.SET_PRINTER_INDEX("barcode");
    if (flag) {
        for (var i = 0; i < json.length; i++) {
            var zssize = 66;
            var zsname = json[i].zsname.length > 22 ? json[i].zsname.substring(0, 22) : json[i].zsname;//主诉
            //如果主诉名字大于13就会换行
            if (zsname.length > 13) {
                zssize = 56;
            }
            LODOP.PRINT_INITA(5, 0, "60mm", "40mm", "二维条码（小）");
            LODOP.SET_PRINT_PAGESIZE(1, "60mm", "40mm", "");
            var itemname = json[i].itemname;
            //说明是彩超多部位
            if ((itemname.indexOf("一部位") != -1 || itemname.indexOf("二部位") != -1) && itemname.indexOf("彩超") != -1) {
                itemname = itemname.replace("彩超常规检查(一部位)(", "彩常-").replace("彩超常规检查(≥二部位)(", "彩常-").replace("彩超浅表器官检查（一部位）(", "彩浅-").replace("彩超浅表器官检查（≥二部位）(", "彩浅-").replace(")", "").replace("）", "");
                //var itemname=json[i][0].length>11?json[i][0].substring(0,11):json[i][0];
                LODOP.ADD_PRINT_TEXT(8, 14, 215, 60, itemname);//项目名称
                LODOP.SET_PRINT_TEXT_STYLEA(1, "", 8, 1, 0, 0, 1, "");
            } else {
                //var itemname=json[i][0].length>11?json[i][0].substring(0,11):json[i][0];
                LODOP.ADD_PRINT_TEXT(8, 14, 205, 60, itemname);//项目名称
                LODOP.SET_PRINT_TEXT_STYLEA(1, "", 10, 1, 0, 0, 1, "");
            }
            LODOP.ADD_PRINT_TEXT(86, 156, 65, 25, json[i].pexamid);//处方号
            LODOP.SET_PRINT_TEXT_STYLEA(2, "", 10, 0, 0, 0, 1, "");
            LODOP.ADD_PRINT_TEXT(44, 17, 195, 25, "诊断：" + zsname);//诊断
            LODOP.SET_PRINT_TEXT_STYLEA(3, "", 9, 1, 0, 0, 1, "");
            LODOP.ADD_PRINT_TEXT(zssize, 57, 165, 25, zsname);//主诉
            LODOP.SET_PRINT_TEXT_STYLEA(4, "", 9, 0, 0, 0, 1, "");
            LODOP.ADD_PRINT_TEXT(86, 17, 190, 20, "开单医生：" + json[i].doctorname);//开单医生
            LODOP.SET_PRINT_TEXT_STYLEA(5, "", 9, 1, 0, 0, 1, "");
            LODOP.ADD_PRINT_TEXT(104, 16, 100, 24, json[i].patientname);//姓名
            LODOP.SET_PRINT_TEXT_STYLEA(6, "", 13, 1, 0, 0, 1, "");
            LODOP.ADD_PRINT_TEXT(108, 88, 100, 24, "性别：" + json[i].sex);//性别
            LODOP.SET_PRINT_TEXT_STYLEA(7, "", 10, 0, 0, 0, 1, "");
            LODOP.ADD_PRINT_TEXT(108, 150, 100, 24, "年龄：" + json[i].age);//年龄
            LODOP.SET_PRINT_TEXT_STYLEA(8, "", 10, 0, 0, 0, 1, "");
            LODOP.ADD_PRINT_TEXT(127, 17, 206, 24, "打印：" + json[i].printdate);//打印时间
            LODOP.SET_PRINT_TEXT_STYLEA(9, "", 10, 0, 0, 0, 1, "");
            LODOP.ADD_PRINT_TEXT(66, 17, 50, 25, "主诉：");//主诉
            LODOP.SET_PRINT_TEXT_STYLEA(10, "", 9, 1, 0, 0, 1, "");
            //线条
            LODOP.ADD_PRINT_RECT(100, 8, 215, 1, 0, 1);
            LODOP.SET_PRINTER_INDEX("barcode");
            LODOP.PRINT();
            //LODOP.PREVIEW();
        }
    }
}