var comboTimeout = null;
var combo_unit = null;//体检单位
var combo_examType = null;//体检类型
var myCalendar;//出生日期
var combo_idtype = null;
var str = "";

var combo_minzu, combo_guoji, combo_whcd, combo_zjxy, combo_kwxd;
var combo_Province_R, combo_City_R, combo_township_R, combo_village_R;//省市镇乡
var comboTimeOut;
var oldProvince;
var oldCity;
var oldCounty;
var oldTownship;

var issavetag = 'n';//是否保存过标志
var lastsavepexamid = "";//最后保存的人员体检编号
function combo_examTypeinit() {//体检类别
    combo_examType = new dhtmlXCombo("examType", "alfa3", 127);
    $.ajax({
        async: false,
        cache: false,
        ifModified: true,
        type: "post",
        url: "PexamStatistics/comboinit.htm",
        data: "now=" + new Date().getMilliseconds(),
        error: function () {
            alert("服务器内部错误！");
        },
        success: function (data) {
            if (data == "fial") {
                alert("服务器内部错误！");
            } else {
                var jsons = eval('(' + data + ')');
                for (var i = 0; i < jsons.length; i++) {
                    combo_examType.addOption(i, jsons[i].contents);
                }
            }
        }
    });
}
function teamExam() {  //获取团体项目套餐
    var examid = $("#examid_R1").val();
    $.ajax({
        async: false,
        cache: false,
        type: 'get',
        url: "phyexam/teamExamInfo.htm",
        data: "now=" + new Date().getMilliseconds() + "&examid=" + examid,
        error: function () {
            alert('fail');
        },
        success: function (data) {
            var jsons = eval('(' + data + ')');
            var price = 0;
            for (i = 0; i < jsons.length; i++) {
                var index = i + 1;
                var sprice = jsons[i].cost;
                price = FloatAdd(price, sprice);
                //var examHtml = "<tr style='cursor: pointer;'><td width='20' height='28'><img src='img/jiej4.jpg' /></td><td width='20' >"+index+"</td><td id='"+jsons[i].itemid+"' onmouseout='removedetails(\""+jsons[i].itemid+"\")' onmouseover='showdetails(\""+jsons[i].itemid+"\")'>"+jsons[i].itemname+"</td><td width='100' align='right'>"+jsons[i].cost+"</td><td width='100' align='right' style='display:none'>"+jsons[i].itemid+"</td><td width='100' align='right' style='display:none'>"+jsons[i].isgroup+"</td></tr>"
                var itemsHtml = "<tr onclick='remove_item(this)' style='cursor: pointer;'>"
                    + "<td width='20' height='28'><img src='img/jiej4.jpg' /></td>"
                    + "<td width='20' >" + index + "</td>"
                    //+"<td id='"+jsons[i].itemid+"' onmouseout='removedetails(\""+jsons[i].itemid+"\")' onmouseover='showdetails(\""+jsons[i].itemid+"\")'>"+jsons[i].itemname+"</td>"
                    + "<td id='" + jsons[i].itemid + "'>" + jsons[i].itemname + "</td>"
                    + "<td width='100' align='right'>" + jsons[i].cost + "</td>"
                    + "<td width='100' align='right' style='display:none'>" + jsons[i].itemid + "</td>"
                    + "<td width='100' align='right' style='display:none'>" + jsons[i].isgroup + "</td>"//是否是套餐
                    + "<td width='100' align='right' style='display:none'>" + (jsons[i].pexamid == null ? '' : jsons[i].pexamid) + "</td>"//是否是个人附加体检项目--为空则是团体预约的套餐
                    + "<td width='100' align='right' style='display:none'>" + jsons[i].startflag + "</td>"//是否已开始体检标志
                    + "</tr>";
                $('#examitems').append(itemsHtml);
            }
            $('#exnum').text(jsons.length);
            $('#totalprice').text(price);
        }
    });
}


$(document).ready(function () {
    $("#continueEnter").keydown(function (ev) {
        var event = ev || window.event;
        var keyCode = checkBrowser() == "FireFox" ? event.which : event.keyCode;
        if (keyCode == 13) {
            continueEnter();
        }
    });
    myCalendar = new dhtmlXCalendarObject("dateofbirth"); //出生日期
    //获取数据
    $.get("phyexam/getdict.htm", function (json) {
        var jsonarr = eval('(' + json + ')');
        var idtypeBds = jsonarr[0].idtypeBds;
        var contents = "";
        var nevalue = "";
        for (var i = 0; i < idtypeBds.length; i++) {//证件类型
            contents = idtypeBds[i].contents;
            nevalue = idtypeBds[i].nevalue;
            combo_idtype.addOption(nevalue, contents);
        }
        var sexBds = jsonarr[0].sexBds;
        for (var i = 0; i < sexBds.length; i++) {//性别
            var contents = sexBds[i].contents;
            var nevalue = sexBds[i].nevalue;
            combo_sex.addOption(nevalue, contents);
        }
        var maritalstatusBds = jsonarr[0].maritalstatusBds;
        for (var i = 0; i < maritalstatusBds.length; i++) {//结婚情况
            var contents = maritalstatusBds[i].contents;
            var nevalue = maritalstatusBds[i].nevalue;
            combo_maritalstatus.addOption(nevalue, contents);
        }

        var professionalBds = jsonarr[0].professionalBds;
        for (var i = 0; i < professionalBds.length; i++) {//职业
            var contents = professionalBds[i].contents;
            var nevalue = professionalBds[i].nevalue;
            combo_professional.addOption(nevalue, contents);
        }
        var zjxyBds = jsonarr[0].zjxyBds;
        for (var i = 0; i < zjxyBds.length; i++) {//宗教信仰
            var contents = zjxyBds[i].contents;
            var nevalue = zjxyBds[i].nevalue;
            combo_zjxy.addOption(nevalue, contents);
        }
        var guojiaBds = jsonarr[0].guojiaBds;
//        for( var i=0;i<guojiaBds.length;i++){//国家
//  		    var contents=guojiaBds[i].contents;
//  		    var nevalue=guojiaBds[i].nevalue;
//  		    combo_guoji.addOption(nevalue,contents);
//  		}
        combo_guoji.addOption('中国', '中国');
        combo_guoji.addOption('外籍', '外籍');

        var minzuBds = jsonarr[0].minzuBds;
        for (var i = 0; i < minzuBds.length; i++) {//民族
            var contents = minzuBds[i].contents;
            var nevalue = minzuBds[i].nevalue;
            combo_minzu.addOption(nevalue, contents);
        }
        var whcdBds = jsonarr[0].whcdBds;
        for (var i = 0; i < whcdBds.length; i++) {//文化程度
            var contents = whcdBds[i].contents;
            var nevalue = whcdBds[i].nevalue;
            combo_whcd.addOption(nevalue, contents);
        }

    });

    initCombo2(combo_idtype, "idtype");//卡类型
    initCombo2(combo_sex, "sex");//性别
    initCombo2(combo_maritalstatus, "maritalstatus");//婚姻状态
    initCombo2(combo_professional, "professional");//职业

    initCombo(combo_Province_R, "Province_R");
    initCombo(combo_City_R, "City_R");
    initCombo(combo_County_R, "County_R");
    initCombo(combo_township_R, "township_R");

    //组合搜索combo
    //按键事件
    searchItems.DOMelem_input.onkeydown = function (ev) {
        var event = ev || window.event;
        //keyCode是键盘键值
        var keyCode = checkBrowser() == "FireFox" ? event.which : event.keyCode;
        if (keyCode == 13) {
            if (searchItems.getComboText() != "") {
                $.post('phyexam/SearchItemsList.htm?now=' + new Date() + '&input=' + encodeURI(encodeURI(searchItems.getComboText())), function (data) {
                    searchItems.clearAll();
                    var js = eval('(' + data + ')');
                    for (var i = 0; i < js.length; i++) {
                        searchItems.addOption(js[i].comid, js[i].comname);
                    }
                    searchItems.openSelect();
                });
            }
        }
    }
    //点击列表事件
    searchItems.DOMlist.onclick = function (ev) {
        additems2(searchItems.getSelectedValue(), searchItems.getSelectedText());
    }
    searchItems.DOMlist.onmouseover = function (ev) {
        var event = ev || window.event;
        var node = checkBrowser() == "FireFox" ? event.target : event.srcElement;
        while (!node._self) {
            node = node.parentNode;
            if (!node) {
                return;
            }
        }
        ;
        for (var i = 0; i < searchItems.DOMlist.childNodes.length; i++) {
            if (searchItems.DOMlist.childNodes[i] == node) {
                var old = searchItems.getComboText();
                searchItems.selectOption(i, true, true);
                searchItems.setComboText(old);
            }
        }
    }


    //姓名失去焦点
    $("#patname").blur(function () {
        // combo_idtype.DOMelem_input.focus();//单位获取焦点
    });
    //医疗卡号失去焦点
    $("#idnum").blur(function () {
        //myCalendar.show()
    });

    str = $("#str").val();
    if (str != "dblclick") {
        $("#cardType").removeClass("mustwrt");
        var unit = $("#unit_R1").val();
        var examtype = $("#examtype_R1").val();
        if (unit == "个人体检") {
            var html = "<input type='text' id='unit_u' class='txt ro textbk not_editable' value='" + unit + "' readonly style='width:125px;'/>";
            $("#unit").html(html);
            combo_examTypeinit();//初始化体检类型combo
            initCombo2(combo_examType, "examtype");//体检单位
        } else if (unit != null && unit != "个人体检") {
            var html = "<input type='text' id='unit_u' class='txt ro textbk not_editable' value='" + unit + "' readonly style='width:125px;'/>";
            $("#unit").html(html);
            var html2 = "<input type='text' id='examType_u' class='txt ro textbk not_editable' value='" + examtype + "' readonly style='width:125px;'/>";
            $("#examType").html(html2);
            teamExam();
        } else {
            //单位combo初始化
            combo_unit = new dhtmlXCombo("unit", "alfa3", 127);
            combo_unit.readonly(true, true);
            combo_examTypeinit();//初始化体检类型combo
            initCombo2(combo_examType, "examtype");//体检单位
            initCombo2(combo_unit, "unit");//体检单位
        }
    } else { //修改
        $("#idnum").css("border", "1px solid red");
        $("#save").text("修改");
        $("#patientid").val(listbase[0].patientid);
        var unit = listinfo[0].examname;
        var examtype = listinfo[0].examtype;
        var html = "<input type='text' id='unit_u' class='txt ro textbk not_editable' value='" + unit + "' readonly style='width:125px;'/>";
        $("#unit").html(html);
        var html2 = "<input type='text' id='examType_u' class='txt ro textbk not_editable' value='" + examtype + "' readonly style='width:125px;'/>";
        $("#examType").html(html2);
        $("#patname").val(listbase[0].patname);
        combo_idtype.setComboText(listbase[0].idtype == null ? "" : listbase[0].idtype);
        $("#idnum").val(listbase[0].idnum);
        $("#dateofbirth").val(listbase[0].dateofbirth);
        combo_sex.setComboText(listbase[0].sex);
        $("#inscardno").val(listbase[0].ybbh);
        $("#phoneNum").val(listbase[0].phonecall);
        $("#homeaddress").val(listbase[0].address);
        combo_maritalstatus.setComboText(listbase[0].maritalstatus == null ? "" : listbase[0].maritalstatus);
        combo_professional.setComboText(listbase[0].professional == null ? "" : listbase[0].professional);
        combo_Province_R.setComboText(listbase[0].province == null ? "" : listbase[0].province);
        combo_City_R.setComboText(listbase[0].city == null ? "" : listbase[0].city);
        combo_County_R.setComboText(listbase[0].county == null ? "" : listbase[0].county);
        combo_township_R.setComboText(listbase[0].township == null ? "" : listbase[0].township);
        combo_village_R.setComboText(listbase[0].village == null ? "" : listbase[0].village);
        $("#add_R").val(listbase[0].laddress);
        //设置9个新增的字段的值
        combo_minzu.setComboText(listbase[0].minzu == null ? "" : listbase[0].minzu);
        combo_guoji.setComboText(listbase[0].guoji == null ? "" : listbase[0].guoji);
        combo_whcd.setComboText(listbase[0].whcd == null ? "" : listbase[0].whcd);
        combo_zjxy.setComboText(listbase[0].zjxy == null ? "" : listbase[0].zjxy);
        combo_kwxd.setComboText(listbase[0].kwxd == null ? "" : listbase[0].kwxd);
        $("#wordincomputer").val(listbase[0].wordincomputer);
        $("#shuimian").val(listbase[0].shuimian);
        $("#yeniao").val(listbase[0].yeniao);
        $("#tsys").val(listbase[0].tsys);
        $("#zkl").val(listbase[0].zkl);
        $("#wordaddress").val(listbase[0].wordaddress);

        //===================================
        var examid = listbase[0].examid;
        var pexamid = listbase[0].pexamid;
        $.ajax({
            async: false,
            cache: false,
            type: 'get',
            url: "phyexam/ExamInfo.htm",
            data: "now=" + new Date().getMilliseconds() + "&examid=" + examid + "&pexamid=" + pexamid,
            error: function () {
                alert('fail');
            },
            success: function (data) {
                var jsons = eval('(' + data + ')');
                var price = 0;
                for (i = 0; i < jsons.length; i++) {
                    var index = i + 1;
                    var sprice = jsons[i].cost;
                    price = FloatAdd(price, sprice);
                    //var examHtml = "<tr style='cursor: pointer;'><td width='20' height='28'><img src='img/jiej4.jpg' /></td><td width='20' >"+index+"</td><td id='"+jsons[i].itemid+"' onmouseout='removedetails(\""+jsons[i].itemid+"\")' onmouseover='showdetails(\""+jsons[i].itemid+"\")'>"+jsons[i].itemname+"</td><td width='100' align='right'>"+jsons[i].cost+"</td><td width='100' align='right' style='display:none'>"+jsons[i].itemid+"</td><td width='100' align='right' style='display:none'>"+jsons[i].isgroup+"</td></tr>"
                    var itemsHtml = "<tr onclick='remove_item(this)' style='cursor: pointer;'>"
                        + "<td width='20' height='28'><img src='img/jiej4.jpg' /></td>"
                        + "<td width='20' >" + index + "</td>"
                        //+"<td id='"+jsons[i].itemid+"' onmouseout='removedetails(\""+jsons[i].itemid+"\")' onmouseover='showdetails(\""+jsons[i].itemid+"\")'>"+jsons[i].itemname+"</td>"
                        + "<td id='" + jsons[i].itemid + "'>" + jsons[i].itemname + "</td>"
                        + "<td width='100' align='right'>" + jsons[i].cost + "</td>"
                        + "<td width='100' align='right' style='display:none'>" + jsons[i].itemid + "</td>"
                        + "<td width='100' align='right' style='display:none'>" + jsons[i].isgroup + "</td>"//是否是套餐
                        + "<td width='100' align='right' style='display:none'>" + (jsons[i].pexamid == null ? '' : jsons[i].pexamid) + "</td>"//是否是个人附加体检项目--为空则是团体预约的套餐
                        + "<td width='100' align='right' style='display:none'>" + jsons[i].startflag + "</td>"//是否已开始体检标志
                        + "</tr>";
                    $('#examitems').append(itemsHtml);
                }
                $('#exnum').text(jsons.length);
                $('#totalprice').text(price);
            }
        });
    }
});
//折扣率失去焦点的函数
function checkzkl() {
    var zkl = $('#zkl').val();
    var reg = /^0\.\d+$/g;
    var r = zkl.match(reg);
    if (r == null) {
        alert('折扣率必须是0-1之间的小数!');
        return;
    }
}
//保存
function savepatient() {
    var str = $("#str").val();
    //checkzkl();
    if (docheck()) {
        var patientinfo = [];
        var itemsinfo = [];
        var unit = $("#unit_R1").val();
        var examtype;
        var str = $("#str").val();
        if (str != "dblclick") {
            if (unit == "个人体检") {
                var examtype = combo_examType.getComboText();//体检类型
            } else {
                examtype = $("#examtype_R1").val();
            }
        } else {
            examtype = $("#examType_u").val();//grid中没传体检类型加载时自动赋值examType_u，examtype_R1中没赋值
        }

        var examid = $("#examid_R1").val();
        var pexamid = $("#pexamid").val();
        var idtype = combo_idtype.getComboText();//卡类型
        var idnum = $("#idnum").val();//卡号
        idnum = idnum.toUpperCase();
        var inscardno = $("#inscardno").val();//医疗卡号
        var patname = $("#patname").val();//体检病人姓名
        var sex = combo_sex.getComboText();//病人性别
        var dateofbirth = $("#dateofbirth").val();//出生日期
        var professional = combo_professional.getComboText();//职业
        var maritalstatus = combo_maritalstatus.getComboText();//婚姻状态
        var phonecall = $("#phoneNum").val();
        var Province_R = combo_Province_R.getComboText();//省
        var City_R = combo_City_R.getComboText();//县
        var County_R = combo_County_R.getComboText();//市
        var township_R = combo_township_R.getComboText();//镇
        var village_R = combo_village_R.getComboText();//乡
        var add_R = $("#add_R").val();//路牌
        var homeaddress = $("#homeaddress").val();//家庭住址
        //以下新加的9个字段
        var minzu = combo_minzu.getComboText();  //民族
        var guoji = combo_guoji.getComboText();  //国籍
        var whcd = combo_whcd.getComboText();  //文化程度
        var zjxy = combo_zjxy.getComboText();  //宗教信仰
        var kwxd = combo_kwxd.getComboText(); //口味咸淡
        var wordincomputer = $("#wordincomputer").val();  //电脑前工作
        var shuimian = $("#shuimian").val(); // 睡眠
        var yeniao = $("#yeniao").val(); //夜尿
        var tsys = $("#tsys").val(); //特殊饮食习惯

        patientinfo.push({
            unit: unit,
            phonecall: phonecall,
            examid: examid,
            pexamid: pexamid,
            idtype: idtype,
            idnum: idnum,
            inscardno: inscardno,
            patname: patname,
            sex: sex,
            dateofbirth: dateofbirth,
            professional: professional,
            maritalstatus: maritalstatus,
            Province_R: Province_R,
            City_R: City_R,
            County_R: County_R,
            township_R: township_R,
            village_R: village_R,
            add_R: add_R,
            examtype: examtype,
            homeaddress: homeaddress,
            //新增的9个字段
            minzu: minzu,
            guoji: guoji,
            whcd: whcd,
            zjxy: zjxy,
            kwxd: kwxd,
            wordincomputer: wordincomputer,
            shuimian: shuimian,
            yeniao: yeniao,
            tsys: tsys,
            zkl: $('#zkl').val(),
            wordaddress: $('#wordaddress').val()
        });
        var trs = document.getElementById('examitems').rows;
        //增加2个参数： 套餐 字符串和 组合字符串
        var group_str = "", items_str = "";
        for (var i = 0; i < trs.length; i++) {
            var itname = $(trs[i].cells[2]).text();
            var isperson = $(trs[i].cells[6]).text();
            if ($(trs[i].cells[5]).text() == 'y') {
                group_str += $(trs[i].cells[4]).text() + ",";
            } else {
                items_str += $(trs[i].cells[4]).text() + ",";
            }
            //if(isperson!=""){//如果不为空位个人套餐
            itemsinfo.push({
                isperson: isperson,//pexamid
                itemname: itname,//体检项目名称
                cost: $(trs[i].cells[3]).text(),//价格
                itemid: $(trs[i].cells[4]).text(),//项目id
                isgroup: $(trs[i].cells[5]).text()//是否是套餐
            });
            //}
        }
        if (group_str != "") {
            group_str = group_str.substring(0, group_str.length - 1);
        }
        if (items_str != "") {
            items_str = items_str.substring(0, items_str.length - 1);
        }

        $.ajax({
            url: "phyexam/savepatient.htm",
            type: "post",
            data: "method=add&json1=" + toJSON(patientinfo) + "&time=" + (new Date()).valueOf() + "&itemsinfo=" + toJSON(itemsinfo) + "&str=" + str + "&items_str=" + items_str + "&group_str=" + group_str,
            error: function () {
                alert("保存失败");
            },
            success: function (data) {
                if (data.indexOf('失败：') != -1) {
                    alert(data);
                } else {
                    alert("保存成功！");
                    if (str == "jiejian") {
                        parent.document.getElementById("search_value").value = idnum;
                        window.parent.topSearch();
                        doclose();
                    } else {
                        window.parent.setMainValue(examid);
                        doclose();
                    }

                    issavetag = 'y';//是否保存标志
                    //lastsavepexamid=pexamid;//最后一次保存的人员体检编号
                }
            }
        });

    }
}
//关闭按钮
function doclose() {
    parent.doClose();
}


//保存前验证
function docheck() {
    var unit = $("#unit_R1").val();
    var examtype;
    var str = $("#str").val();
    if (str != "dblclick") {
        if (unit == "个人体检") {
            examtype = combo_examType.getComboText();//体检类型
        } else {
            examtype = $("#examtype_R1").val();
        }
    } else {
        examtype = $("#examType_u").val();
    }
    var homeaddress = $("#homeaddress").val();
    var examid = $("#examid_R1").val();
    var pexamid = $("#pexamid").val();//个人体检号
    var idtype = combo_idtype.getComboText();//卡类型
    var idnum = $("#idnum").val();//卡号
    var inscardno = $("#inscardno").val();//医疗卡号
    var patname = $("#patname").val();//体检病人姓名
    var sex = combo_sex.getComboText();//病人性别
    var dateofbirth = $("#dateofbirth").val();//出生日期
    //以下新加的9个字段
    var minzu = combo_minzu.getComboText();  //民族
    var guoji = combo_guoji.getComboText();  //国籍
    var whcd = combo_whcd.getComboText();  //文化程度
    var zjxy = combo_zjxy.getComboText();  //宗教信仰
    var kwxd = combo_kwxd.getComboText(); //口味咸淡
    var wordincomputer = $("#wordincomputer").val();  //电脑前工作
    var shuimian = $("#shuimian").val(); // 睡眠
    var yeniao = $("#yeniao").val(); //夜尿
    var tsys = $("#tsys").val(); //特殊饮食习惯
    var lxdh = $("#phoneNum").val();//联系电话
    /**
     if(minzu=="" || minzu== null ){
		alert("请填写体检人员民族");
		return false;
	}
     if(guoji=="" || guoji== null ){
		alert("请填写体检人员国籍");
		return false;
	}
     if(whcd=="" || whcd== null ){
		alert("请填写体检人员文化程度");
		return false;
	}
     */

    /**
     if(kwxd=="" || kwxd== null ){
		alert("请填写体检人员口味咸淡");
		return false;
	}
     if(wordincomputer=="" || wordincomputer== null ){
		alert("请填写体检人员电脑前工作");
		return false;
	}
     if(shuimian=="" || shuimian== null ){
		alert("请填写体检人员睡眠");
		return false;
	}
     if(yeniao=="" || yeniao== null ){
		alert("请填写体检人员夜尿");
		return false;
	}
     if(tsys=="" || tsys== null ){
		alert("请填写体检人员特殊饮食习惯");
		return false;
	}
     */
    //===============================
    if (patname == "" || patname == null) {
        alert("请填写体检人员姓名！");
        return false;
    }
    if (sex == "" || sex == null) {
        alert("请填写体检人员性别");
        return false;
    }
    if (zjxy == "" || zjxy == null) {
        alert("请填写体检人员宗教信仰");
        return false;
    }
    if (lxdh == "" || lxdh == null) {
        alert("请填写体检人员联系电话");
        return false;
    }
    if (str == "dblclick") { //修改
        if (idnum == "" || idnum == null) {
            alert("请填写体检人员证件号");
            return false;
        }
        if (idtype == "" || idtype == null) {
            alert("请填写体检人员证件类型");
            return false;
        }

    } else {  //新增   证件和证件号必须一起保存 或一起不保存
        /**
         if(((idnum=="" || idnum==null) && (idtype=="" || idtype==null)) || ((idnum!="" || idnum!=null) && (idtype!="" || idtype!=null))){//ok

		}else{
			alert("请填写体检人证件类型和证件号码");
			return false;
		}
         */
        if (idnum != "" || idtype != "") {
            if (idnum != "" && idtype != "") {

            } else {
                alert("请填写体检人证件类型和证件号码");
                return false;
            }
        }
    }
    /**
     if(examtype=="" || examtype== null ){
		alert("请选择体检类别！");
		return false;
	}
     */
    /**
     if(examid=="" || examid== null ){
		alert("请选择体检单位！");
		return false;
	}
     if(idtype=="" || idtype== null ){
		alert("请选择证件类型！");
		return false;
	}
     if(idnum=="" || idnum== null ){
		alert("请填写证件号！");
		return false;
	}
     if(homeaddress==""){
		alert("请填写家庭住址！");
		return false;
	}
     var trs = document.getElementById('examitems').rows;
     if(trs.length==0){
		//alert("请选择体检套餐！");
		//return false;
	}
     if(dateofbirth=="" || dateofbirth== null ){
		alert("请填写体检人员生日");
		return false;
	}
     */
    var zkl = $('#zkl').val();
    var reg = /^(0\.(?!0+$)\d{1,2}|1(\.0{1,2})?)$/;
    var r = zkl.match(reg);
    if (zkl == '0') {

    } else {
        if (r == null) {
            alert('折扣率必须是[0-1]之间的两位小数!');
            $('#zkl').focus();
            return false;
        }
    }
    //判断是否存在套餐（有且只有一个套餐）
    var trs = document.getElementById('examitems').rows;
    var k = 0;
    for (var i = 0; i < trs.length; i++) {
        var flag = $(trs[i].cells[5]).text(); //是否套餐 n 或者 y
        if (flag == 'y') {
            k++;
        }
    }
    if (k == "0") {
        alert('必须选择一个套餐');
        return false;
    } else if (k == "2") {
        alert('不能选择多个套餐');
        return false;
    }

    return true;
}

//获取单位列表
function getUnitList() {
    $.ajax({
        url: 'phyexam/getUnitList.htm',
        type: 'post',
        data: 'time=' + (new Date()).valueOf(),
        error: function () {
            alert('获取数据失败！');
        },
        success: function (data) {
            if (data == 'fail') {
                alert('获取数据失败！');
            } else {
                var jsons = eval("(" + data + ")");
                combo_unit.clearAll();
                combo_unit.addOption("0000", "个人体检");
                for (var i = 0; i < jsons.length; i++) {
                    var json = jsons[i];
                    combo_unit.addOption(json.examid, json.examname);
                }
            }
            //默认选中第一项
            if (combo_unit.optionsArr.length > 0) {
                combo_unit.selectOption(0, true, true);
            }

        }
    });
}
//获取人员列表
function getPeopleList(combo) {
    var code = combo.getComboText();
    combo.unSelectOption();
    combo.clearAll();
    combo.closeAll();
    if (code.length < 2) {
        return;
    }
    $.ajax({
        url: 'pexam/getPeopleList.htm',
        type: 'post',
        data: 'code=' + code + '&time=' + (new Date()).valueOf(),
        error: function () {
            alert('获取数据失败！');
        },
        success: function (data) {
            if (data == 'fail') {
                alert('获取数据失败！');
            } else {
                var jsons = eval("(" + data + ")");
                for (var i = 0; i < jsons.length; i++) {
                    var json = jsons[i];
                    combo.addOption(json.sbbh, "<div><div style='float:left;width:12%'>" + json.xm + "</div><div style='float:left;width:42%'>身份证号码：" + json.sfzjh + "</div><div style='float:left;width:46%'>地址：" + json.dz + "</div></div><div>" + json.sfzjh + "</div><div>" + json.xb + "</div>");
                }
            }
            if (combo.optionsArr.length > 0) {
                var old = combo.getComboText();
                combo.selectOption(0, true, true);
                combo.setComboText(old);
                combo.openSelect();
            }
        }
    });
}

//搜索combo初始化
function initCombo(combo) {
    combo.DOMelem.onclick = function () {
        if (combo.DOMelem_input.disabled) {
            return;
        }
        if (combo.DOMlist.style.display == "block") {
            combo.closeAll();
        } else if (combo.optionsArr.length > 0) {
            window.setTimeout(function () {
                combo.openSelect();
            }, 0);
        }
    }

    combo.DOMelem_input.onfocus = function () {
        combo.DOMelem_input.select();
        if (comboTimeout != null) {
            window.clearTimeout(comboTimeout);
            comboTimeout = null;
        }
        comboTimeout = window.setTimeout(function () {
            if (combo.optionsArr.length > 0) {
                combo.openSelect();
            }
        }, 300);
    }

    combo.DOMelem_input.onkeyup = function (ev) {
        var event = ev || window.event;
        var keyCode = checkBrowser() == "FireFox" ? event.which : event.keyCode;
        if (keyCode == 13 && combo.getSelectedText() != "") {

        } else if (keyCode == 38) {

        } else if (keyCode == 40) {

        } else {

        }
    }

    combo.DOMelem_input.onkeydown = function (ev) {
        var event = ev || window.event;
        var keyCode = checkBrowser() == "FireFox" ? event.which : event.keyCode;
        if (keyCode == 13 && combo.getSelectedText() != "") {
            writePeopleInfo(combo);

        } else if (keyCode == 38) {
            var index = combo.getSelectedIndex();
            if (index == 0) {
                index = combo.optionsArr.length - 1;
                var old = combo.getComboText();
                combo.selectOption(index, true, true);
                combo.setComboText(old);
            } else {
                index--;
                var old = combo.getComboText();
                combo.selectOption(index, true, true);
                combo.setComboText(old);
            }
        } else if (keyCode == 40) {
            var index = combo.getSelectedIndex();
            if (index == combo.optionsArr.length - 1) {
                index = 0;
                var old = combo.getComboText();
                combo.selectOption(index, true, true);
                combo.setComboText(old);
            } else {
                index++;
                var old = combo.getComboText();
                combo.selectOption(index, true, true);
                combo.setComboText(old);
            }
        }
    }

    combo.DOMlist.onmouseover = function (ev) {
        var event = ev || window.event;
        var node = checkBrowser() == "FireFox" ? event.target : event.srcElement;
        while (!node._self) {
            node = node.parentNode;
            if (!node) {
                return;
            }
        }
        for (var i = 0; i < combo.DOMlist.childNodes.length; i++) {
            if (combo.DOMlist.childNodes[i] == node) {
                var old = combo.getComboText();
                combo.selectOption(i, true, true);
                combo.setComboText(old);
            }
        }
    }

    combo.DOMlist.onclick = function (ev) {
        if (combo.optionsArr.length == 0) {
            return;
        }
        writePeopleInfo(combo);
    }
}


function clearScreen() {
    $('#pexamid').val(getPexamid());
    $('#patname').val('');
    $('#dateofbirth').val('');
    combo_professional.setComboText('');
    combo_maritalstatus.setComboText('');
    $('#inscardno').val('');
    combo_idtype.setComboText('');
    $("#idnum").val('');
    combo_sex.setComboText('');
}
/*
 function getPexamid(){
 var pexamid = "" ;
 var datetime = new Date();
 var year = datetime.getFullYear();
 var month = datetime.getMonth()+1;//js从0开始取
 var date = datetime.getDate();
 var hour = datetime.getHours();
 var minutes = datetime.getMinutes();
 var second = datetime.getSeconds();
 var milliseconds = ""+datetime.getMilliseconds();
 if(month<10){
 month = "0" + month;
 }
 if(date<10){
 date = "0" + date;
 }
 if(hour <10){
 hour = "0" + hour;
 }
 if(minutes <10){
 minutes = "0" + minutes;
 }
 if(second <10){
 second = "0" + second ;
 }
 while(milliseconds.lenght<3){
 milliseconds = "0" + milliseconds;
 }
 pexamid = pexamid + year + month + date + hour + minutes + second + milliseconds ;
 return pexamid;
 }
 */
function continueEnter() {
    if ($('#idnum').val() == "") {
        alert("请输入证件号");
        return;
    }
    if ($('#patname').val() == "") {
        alert("请输入姓名");
        return;
    }
    var patientinfo = [];
    var examid = $('#examid').val();
    var sex = combo_sex.getComboText();
    var professional = combo_professional.getComboText();
    var maritalstatus = combo_maritalstatus.getComboText();
    var idtype = combo_idtype.getComboText();
    patientinfo.push({
        patname: $('#patname').val(),
        sex: sex,
        dateofbirth: $('#dateofbirth').val(),
        professional: professional,
        maritalstatus: maritalstatus,
        inscardno: $('#inscardno').val(),
        idtype: idtype,
        idnum: $('#idnum').val()
    });

    var name = $('#patname').val();
    var idnum = $('#idnum').val();
    var inscardno = $('#inscardno').val();
    var pexamid = $('#pexamid').val();
    var dateofbirth = $('#dateofbirth').val();
    $.ajax({
        url: "pexam/savepatient.htm",
        type: "post",
        data: "method=savePatient&json1=" + toJSON(patientinfo) + "&time=" + (new Date()).valueOf() + "&examid=" + examid + "&pexamid=" + pexamid,
        success: function (data) {
            if ("fail" == data) {
                alert("fail");
            } else {
                var index = window.parent.grid_patient_appoint.getRowsNum() + 1;
                if ("insert" == data) {
                    window.parent.grid_patient_appoint.addRow(index, [index, examid, name, sex, dateofbirth, idtype, idnum, inscardno, professional, maritalstatus, pexamid, '']);
                }
                alert("保存成功！");
                clearScreen();
            }
        }
    });

}


//光标定位
function doFocus(target) {
    if (target == "Province_R") {
        combo_City_R.DOMelem_input.select();
    } else if (target == "City_R") {
        combo_County_R.DOMelem_input.select();
    } else if (target == "County_R") {
        combo_township_R.DOMelem_input.select();
    } else if (target == "township_R") {

        //在载入后延迟指定时间，去执行表达式，仅执行一次
        window.setTimeout(function () {
            $("#add_r").select();
        }, 0);
    } else if (target == "Province_C") {
        combo_City_C.DOMelem_input.select();
    } else if (target == "City_C") {
        combo_County_C.DOMelem_input.select();
    } else if (target == "County_C") {
        combo_township_C.DOMelem_input.select();
    } else if (target == "township_C") {
        window.setTimeout(function () {
            $("#add_c").select();
        }, 0);
    }
}

function doClear(target) {
    if (target == "Province_R" && oldProvince != $("#Province_R").val()) {
        $("#City_R").val("");
        combo_City_R.setComboText("");
        combo_City_R.unSelectOption();
        combo_City_R.clearAll();
        $("#County_R").val("");
        combo_County_R.setComboText("");
        combo_County_R.unSelectOption();
        combo_County_R.clearAll();
        $("#township_R").val("");
        combo_township_R.setComboText("");
        combo_township_R.unSelectOption();
        combo_township_R.clearAll();
    } else if (target == "City_R" && oldCity != $("#City_R").val()) {
        $("#County_R").val("");
        combo_County_R.setComboText("");
        combo_County_R.unSelectOption();
        combo_County_R.clearAll();
        $("#township_R").val("");
        combo_township_R.setComboText("");
        combo_township_R.unSelectOption();
        combo_township_R.clearAll();
    } else if (target == "County_R" && oldCounty != $("#County_R").val()) {
        $("#township_R").val("");
        combo_township_R.setComboText("");
        combo_township_R.unSelectOption();
        combo_township_R.clearAll();
    } else if (target == "Province_C" && oldProvince != $("#Province_C").val()) {
        $("#City_C").val("");
        combo_City_C.setComboText("");
        combo_City_C.unSelectOption();
        combo_City_C.clearAll();
        $("#County_C").val("");
        combo_County_C.setComboText("");
        combo_County_C.unSelectOption();
        combo_County_C.clearAll();
        $("#township_C").val("");
        combo_township_C.setComboText("");
        combo_township_C.unSelectOption();
        combo_township_C.clearAll();
    } else if (target == "City_C" && oldCity != $("#City_C").val()) {
        $("#County_C").val("");
        combo_County_C.setComboText("");
        combo_County_C.unSelectOption();
        combo_County_C.clearAll();
        $("#township_C").val("");
        combo_township_C.setComboText("");
        combo_township_C.unSelectOption();
        combo_township_C.clearAll();
    } else if (target == "County_C" && oldCounty != $("#County_C").val()) {
        $("#township_C").val("");
        combo_township_C.setComboText("");
        combo_township_C.unSelectOption();
        combo_township_C.clearAll();
    }
}

function initCombo(combo, target) {
    //获得焦点时(鼠标点上去)调用
    combo.DOMelem_input.onfocus = function () {
        combo.DOMelem_input.select();
        if (target.indexOf("Province") != -1) {
            oldProvince = $("#" + target + "1").val();
        } else if (target.indexOf("City") != -1) {
            oldCity = $("#" + target + "1").val();
        } else if (target.indexOf("County") != -1) {
            oldCounty = $("#" + target + "1").val();
        } else if (target.indexOf("township") != -1) {
            oldTownship = $("#" + target + "1").val();
        }
        if (comboTimeOut != null) {
            window.clearTimeout(comboTimeOut);
            comboTimeOut = null;
        }

        comboTimeOut = window.setTimeout(function () {
            combo.openSelect();
            if ((target.indexOf("Province") != -1 || target.indexOf("City") != -1 ||
                target.indexOf("County") != -1 || target.indexOf("township") != -1) && combo.getComboText() == "") {
                getPlaceList(combo, target);
            }
        }, 300);
    }

    //失去焦点时发生
    combo.DOMelem_input.onblur = function () {
        doClear(target);
    }

    //按下键盘时调用
    combo.DOMelem_input.onkeydown = function (ev) {
        var event = ev || window.event;
        //keyCode是键盘键值
        var keyCode = checkBrowser() == "FireFox" ? event.which : event.keyCode;
        //13为Enter
        if (keyCode == 13) {
            if (combo.getSelectedText() != "") {
                $("#" + target).val(combo.getActualValue());
                combo.setComboText(combo.getSelectedText());
            }
            doFocus(target);
            doClear(target);
        }
        else if (keyCode == 38) {
            index = combo.getSelectedIndex();
            if (index == 0) {
                index = combo.optionsArr.length - 1;
                var old = combo.getComboText();
                combo.selectOption(index, true, true);
                combo.setComboText(old);
            } else {
                index--;
                var old = combo.getComboText();
                combo.selectOption(index, true, true);
                combo.setComboText(old);
            }
        }
        else if (keyCode == 40) {
            index = combo.getSelectedIndex();
            if (index == combo.optionsArr.length - 1) {
                index = 0;
                var old = combo.getComboText();
                combo.selectOption(index, true, true);
                combo.setComboText(old);
            }
            else {
                index++;
                var old = combo.getComboText();
                combo.selectOption(index, true, true);
                combo.setComboText(old);
            }
        }
        else if (keyCode == 8 || keyCode == 46 || (keyCode >= 48 && keyCode <= 57) ||
            (keyCode >= 65 && keyCode <= 90) || (keyCode >= 96 && keyCode <= 111)) {
            window.setTimeout(function () {
                if ((target.indexOf("Province") != -1 || target.indexOf("City") != -1 ||
                    target.indexOf("County") != -1 || target.indexOf("township") != -1)) {
                    getPlaceList(combo, target);
                }
            }, 10);
        }
    }
    //下拉列表鼠标滑过
    combo.DOMlist.onmouseover = function (ev) {
        var event = ev || window.event;
        var node = checkBrowser() == "FireFox" ? event.target : event.srcElement;
        while (!node._self) {
            node = node.parentNode;
            if (!node) {
                return;
            }
        }
        ;
        for (var i = 0; i < combo.DOMlist.childNodes.length; i++) {
            if (combo.DOMlist.childNodes[i] == node) {
                var old = combo.getComboText();
                combo.selectOption(i, true, true);
                combo.setComboText(old);
            }
        }
    }

    //下拉列表点击
    combo.DOMlist.onclick = function (ev) {
        if (combo.optionsArr.length == 0) {
            return;
        }
        $("#" + target).val(combo.getActualValue());
        combo.setComboText(combo.getSelectedText());
        doFocus(target);
        doClear(target);
    }

}

function getPlaceList(combo, target) {
    // /g全文搜索，把空格去掉
    var inputCpy = combo.getComboText().replace(/ /g, "");
    combo.unSelectOption();
    combo.clearAll();
    combo.openSelect();
    $("#" + target).val("");
    //type为要搜索的内容,如浙江省下的市，code为前一级的约束条件，如浙江省
    var type;
    var code;
    if (target.indexOf("Province") != -1) {
        type = "province";
    } else if (target.indexOf("City") != -1) {
        type = "city";
        if (target == "City_R") {
            if ($("#Province_R").val() == "") {
                return;
            }
            code = $("#Province_R").val();
        } else if (target == "City_C") {
            if ($("#Province_C").val() == "") {
                return;
            }
            code = $("#Province_C").val();
        }
    } else if (target.indexOf("County") != -1) {
        type = "county";
        if (target == "County_R") {
            if ($("#City_R").val() == "") {
                return;
            }
            code = $("#City_R").val();
        } else if (target == "County_C") {
            if ($("#City_C").val() == "") {
                return;
            }
            code = $("#City_C").val();
        }
    } else if (target.indexOf("township") != -1) {
        type = "township";
        if (target == "township_R") {
            if ($("#County_R").val() == "") {
                return;
            }
            code = $("#County_R").val();
        } else if (target == "township_C") {
            if ($("#County_C").val() == "") {
                return;
            }
            code = $("#County_C").val();
        }
    }
    $.ajax({
        async: false,
        cache: false,
        url: "phyexam/getPlaceList.htm",
        type: "post",
        data: "type=" + type + "&code=" + code + "&inputCpy=" + inputCpy + "&time=" + (new Date()).valueOf(),
        error: function () {
            alert("获取数据失败");
        },
        success: function (reply) {
            if (reply == "fail") {
                alert("获取数据失败");
            } else {
                var jsons = eval("(" + reply + ")");
                for (var i = 0; i < jsons.length; i++) {
                    combo.addOption(jsons[i].nevalue, jsons[i].contents);
                }
            }
        }
    });
}


//根据省份证号 判断性别
function setBirthdayAndSex() {
    var idnum = $('#idnum').val();
    var sex;
    if (idnum.length == 18) {
        var dateofbirth = idnum.substring(6, 10) + '-' + idnum.substring(10, 12) + '-' + idnum.substring(12, 14);
        $('#dateofbirth').val(dateofbirth);
        if (idnum.substring(16, 17) % 2 == 0) {
            sex = '女';
        } else {
            sex = '男';
        }
    } else if (idnum.length == 15) {
        var dateofbirth = "19" + idnum.substring(7, 9) + '-' + idnum.substring(9, 11) + '-' + idnum.substring(11, 13);
        $('#dateofbirth').val(dateofbirth);
        if (idnum.substring(15, 16) % 2 == 0) {
            sex = '女';
        } else {
            sex = '男';
        }
    }
    combo_sex.setComboText(sex);
}
function writePeopleInfo(combo) {
    $('#pexamid').val(getPexamid());
    combo.setComboText(combo.DOMlist.childNodes[combo.getSelectedIndex()].childNodes[0].childNodes[0].innerText);
    $("#patname").val(combo.getComboText());
    combo_idtype.setComboText("身份证");
    $("#idnum").val(combo.DOMlist.childNodes[combo.getSelectedIndex()].childNodes[1].innerText);
    setBirthdayAndSex();
    $("#continueEnter").focus();
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

//初始化普通combo
function initCombo2(combo, target) {
    //获得焦点时(鼠标点上去)调用
    combo.DOMelem_input.onfocus = function () {
        combo.DOMelem_input.select();
        /*
         if(target.indexOf("unit")!=-1){
         combo_unit.openSelect();
         }else if(target.indexOf("examtype")!=-1){
         combo_pexamType.DOMelem_input.focus();
         }else if(target.indexOf("idtype")!=-1){
         combo_idtype.openSelect();
         }else if(target.indexOf("sex")!=-1){
         combo_sex.openSelect();
         }else if(target.indexOf("maritalStatus")!=-1){
         combo_maritalstatus.openSelect();
         }else if(target.indexOf("professional")!=-1){
         combo_professional.openSelect();
         }
         */


        if (comboTimeOut != null) {
            window.clearTimeout(comboTimeOut);
            comboTimeOut = null;
        }
        comboTimeOut = window.setTimeout(function () {
            /*
             //获取数据
             $.get("phyexam/getdict.htm", function(json){
             var jsonarr = eval('('+json+')');
             var idtypeBds = jsonarr[0].idtypeBds;
             var temp = toJSON(idtypeBds);
             comboFilter(8,temp, "combo_idtype", "hp", true, false, true);

             temp = toJSON(jsonarr[0].sexBds);
             comboFilter(8,temp, "combo_sex", "hp", true, false, true);

             temp = toJSON(jsonarr[0].maritalstatusBds);
             comboFilter(8,temp, "combo_maritalstatus", "hp", true, false, true);

             temp = toJSON(jsonarr[0].professionalBds);
             comboFilter(8,temp, "combo_professional", "hp", true, false, true);

             });
             */

            // $("#idnum").val("准备弹出下拉框");
            combo.openSelect();
            /*
             if(target.indexOf("unit")!=-1 &&combo.getComboText()=="") {
             getUnitList();
             }
             */
        }, 300);
    }

    //失去焦点时发生
    combo.DOMelem_input.onblur = function () {
        /*
         if(target.indexOf("unit")!=-1 && combo.getComboText()!=""){
         combo_examType.openSelect();
         }else if(target.indexOf("examtype")!=-1 && combo.getComboText()!=""){
         $("#patname").focus();
         }else if(target.indexOf("idtype")!=-1 && combo.getComboText()!=""){
         $("#idnum").focus();
         }else if(target.indexOf("sex")!=-1 && combo.getComboText()!=""){
         $("#inscardno").focus();
         }
         */

        //doClear(target);
    }

    //按下键盘时调用
    combo.DOMelem_input.onkeydown = function (ev) {
        var event = ev || window.event;
        //keyCode是键盘键值
        var keyCode = checkBrowser() == "FireFox" ? event.which : event.keyCode;
        //13为Enter
        if (keyCode == 13) {
            if (combo.getSelectedText() != "") {
                combo.setComboText(combo.optionsArr[combo.getSelectedIndex()].text);
                doFocus2(target);
                doClear2(combo, target);
            }

        }
        else if (keyCode == 38) {
            index = combo.getSelectedIndex();
            if (index == 0) {
                index = combo.optionsArr.length - 1;
                var old = combo.getComboText();
                combo.selectOption(index, true, true);
                combo.setComboText(old);
            } else {
                index--;
                var old = combo.getComboText();
                combo.selectOption(index, true, true);
                combo.setComboText(old);
            }
        }
        else if (keyCode == 40) {
            index = combo.getSelectedIndex();
            if (index == combo.optionsArr.length - 1) {
                index = 0;
                var old = combo.getComboText();
                combo.selectOption(index, true, true);
                combo.setComboText(old);
            }
            else {
                index++;
                var old = combo.getComboText();
                combo.selectOption(index, true, true);
                combo.setComboText(old);
            }
        }
        /*else if(keyCode==8||keyCode==46||(keyCode>=48&&keyCode<=57)||
         (keyCode>=65&&keyCode<=90)||(keyCode>=96&&keyCode<=111))
         {
         window.setTimeout(function()
         {
         if((target.indexOf("Province")!=-1||target.indexOf("City")!=-1||
         target.indexOf("County")!=-1||target.indexOf("township") != -1))
         {
         getPlaceList(combo,target);
         }
         },10);
         }
         */
    }
    //下拉列表鼠标滑过
    combo.DOMlist.onmouseover = function (ev) {
        var event = ev || window.event;
        var node = checkBrowser() == "FireFox" ? event.target : event.srcElement;
        while (!node._self) {
            node = node.parentNode;
            if (!node) {
                return;
            }
        }
        ;
        for (var i = 0; i < combo.DOMlist.childNodes.length; i++) {
            if (combo.DOMlist.childNodes[i] == node) {
                var old = combo.getComboText();
                combo.selectOption(i, true, true);
                combo.setComboText(old);
            }
        }
    }

    //下拉列表点击
    combo.DOMlist.onclick = function (ev) {
        if (combo.optionsArr.length == 0) {
            return;
        }
        //$("#"+target).val(combo.getActualValue());
        combo.setComboText(combo.getSelectedText());
        doFocus2(target);
        doClear2(combo, target);
    }
}

//光标定位
function doFocus2(target) {
    if (target == "unit") {
        combo_examType.DOMelem_input.select();
    } else if (target == "examtype") {
        $("#patname").select();
    } else if (target == "idtype") {
        $("#idnum").select();//证件号
    } else if (target == "sex") {
        $("#inscardno").select();//医疗卡号
    } else if (target == "maritalstatus") {
        combo_professional.DOMelem_input.select();
    }
}
//回车 失去焦点后触发
function doClear2(combo, target) {
    combo.closeAll();
    /*
     if(target=="unit"){
     $("#City_R").val("");
     combo_City_R.setComboText("");
     combo_City_R.unSelectOption();
     combo_City_R.clearAll();
     $("#County_R").val("");
     combo_County_R.setComboText("");
     combo_County_R.unSelectOption();
     combo_County_R.clearAll();
     $("#township_R").val("");
     combo_township_R.setComboText("");
     combo_township_R.unSelectOption();
     combo_township_R.clearAll();
     }
     */
}
//------------------------------------------套餐部分------------------------------------------------


//添加项目验证方式改变了
function additems2(itemcode, itemname) {
    //验证已选项目是否存在
    var trs = document.getElementById('examitems').rows;
    var sex = combo_sex.getComboText();
    var groupids = "";
    for (var i = 0; i < trs.length; i++) {
        var name = $(trs[i].cells[2]).text();
        groupids = groupids + $(trs[i].cells[4]).text() + ",";
        if (itemname == name) {
            alert("此项目在存在");
            return false;
        }
    }
    if (!getsexinfo(itemcode, sex)) {
        alert("要添加的体检项目与当前性别不符合。");
        return;
    }
    groupids = groupids.substring(0, groupids.length - 1);
    var myDate = new Date();
    var stamp = myDate.getMilliseconds();
    //根据groupid查询，下面的id是否包含
    if (groupids == '') {
        alert('请先选择一个套餐！');
        return;
    }

    $.ajax({
        async: false,
        cache: false,
        type: 'get',
        url: "pexam/checkitemsingroup.htm?stamp=" + stamp + "&itemcode=" + itemcode + "&groupids=" + groupids,
        error: function () {
            alert('获取数据失败');
        },
        success: function (data) {
            if (data == 'exist') {
                alert('此项目在套餐中已存在');
            } else {

                $.ajax({
                    async: false,
                    cache: false,
                    type: 'get',
                    url: "pexam/getitembycode.htm?stamp=" + stamp + "&itemcode=" + itemcode,
                    error: function () {
                        alert('获取数据失败');
                    },
                    success: function (data) {
                        var trs = document.getElementById('examitems').rows;
                        var jsons = eval('(' + data + ')');
                        var price = $('#totalprice').text();
                        for (i = 0; i < jsons.length; i++) {
                            var index = i + 1 + trs.length;
                            var sprice = jsons[i].cost;
                            price = FloatAdd(price, sprice);
                            $('#examitems tbody').append("<tr onclick='remove_item(this)' style='cursor: pointer;'><td width='20' height='28'><img src='img/jiej4.jpg' /></td><td width='20'>" + index + "</td><td>" + jsons[i].itemname + "</td><td width='100' align='right'>" + jsons[i].cost + "</td><td width='100' align='right' style='display:none'>" + jsons[i].itemcode + "</td><td width='100' align='right' style='display:none'>n</td></tr>");
                        }
                        $('#totalprice').text(price);
                        var trs = document.getElementById('examitems').rows;
                        $('#exnum').text(trs.length);
                    }
                });
            }

        }
    });
}

//获取 组合的适用性别
function getsexinfo(itemcode, sex) {
    //性别的判断
    var ret = true;
    $.ajax({
        async: false, //同步
        url: "phyexam/getitemsex.htm",
        type: "post",
        data: "itemcode=" + itemcode + "&time=" + new Date().valueOf(),
        error: function () {
            alert('获取失败');
        },
        success: function (reply) {
            if (reply == ("fail")) {
                alert("查询出错，服务器内部错误");
            } else {
                if (reply.indexOf(sex) < 0) {
                    //alert("要添加的体检项目与当前性别不符合。");
                    ret = false;
                }
            }
        }
    });
    return ret;
}

var gprice = 0;
//不需拆分项目添加套餐
function addgroup2(groupid, groupname, cost) {
    //非隐藏元素个数
    var groupnum = $("#examitems tr:visible").size();
    var groupindex = groupnum + 1;
    var trs = document.getElementById('examitems').rows;
    for (var i = 0; i < trs.length; i++) {
        var name = $(trs[i].cells[2]).text();
        if (groupname == name) {
            alert("此套餐已存在");
            return false;
        }
    }

    groupinfo = groupid;
    var gpname = groupname;
    var pexamid = $("#pexamid").val();
    //$('#examitems').append("<tr onclick='remove_item(this)' style='cursor: pointer;'><td width='20' height='28'><img src='img/jiej4.jpg' /></td><td width='20'>"+groupindex+"</td><td>"+gpname+"</td><td width='100' align='right'>"+cost+"</td><td width='100' align='right' style='display:none'>"+groupid+"</td><td width='100' align='right' style='display:none'>y</td></tr>");
    var html = "<tr onclick='remove_item(this)' style='cursor: pointer;'>"
        + "<td width='20' height='28'><img src='img/jiej4.jpg' /></td>"
        + "<td width='20'>" + groupindex + "</td>"
        + "<td id='" + groupid + "'>" + gpname + "</td>"
        + "<td width='100' align='right'>" + cost + "</td>"
        + "<td width='100' align='right' style='display:none'>" + groupid + "</td>"
        + "<td width='100' align='right' style='display:none'>y</td>"
        + "<td width='100' align='right' style='display:none'>" + pexamid + "</td>"
        + "</tr>"
    $('#examitems').append(html);
    gprice = $('#totalprice').text();
    gprice = FloatAdd(gprice, cost);
    $('#totalprice').text(gprice);
    var trs = document.getElementById('examitems').rows;
    $('#exnum').text(trs.length);
}

//判断这个人是否开始体检----error

function ifintj(pexamid) {
    var ret = false;
    $.ajax({
        async: false, //同步
        url: "patSigns/load_grid1Data.htm",
        type: "post",
        data: "pexamid=" + pexamid + "&time=" + new Date().valueOf(),
        error: function () {
            alert('获取数据失败');
        },
        success: function (reply) {
            if (reply == ("fail")) {
                alert("查询出错，服务器内部错误");
            } else {

            }
        }
    });
}


function fSecondToTime(iIpt) {
    var dt1 = new Date(iIpt);
    return dateToStr(dt1);
}

function dateToStr(datetime) {
    var year = datetime.getFullYear();
    var month = datetime.getMonth() + 1;//js从0开始取
    var date = datetime.getDate();
    var hour = datetime.getHours();
    var minutes = datetime.getMinutes();
    var second = datetime.getSeconds();

    if (month < 10) {
        month = "0" + month;
    }
    if (date < 10) {
        date = "0" + date;
    }
    if (hour < 10) {
        hour = "0" + hour;
    }
    if (minutes < 10) {
        minutes = "0" + minutes;
    }
    if (second < 10) {
        second = "0" + second;
    }
    var time = year + "-" + month + "-" + date;
    return time;
}

//移去套餐
function remove_item(obj) {
    var isPerson = $(obj).children("td").eq(6).html();//不为空说明是个人附加项
    var startFlag = $(obj).children("td").eq(7).html();//为“Y”说一开始体检
    if (startFlag == 'Y') {
        alert('该套餐项目已开始体检，不能删除！');
        return;
    }
    if (isPerson == '') {
        alert("不是个人附加套餐不能删除!");
        return;
    }
    var totalprice = $('#totalprice').text();
    var pirce = $(obj).children("td").eq(3).html();
    totalprice = FloatSub(totalprice, pirce);
    $('#totalprice').text(totalprice);
    $(obj).remove();
    $("#exnum").text($("#exnum").text() - 1);
    var trs = document.getElementById('examitems').rows;
    for (var i = 0; i < trs.length; i++) {
        $(trs[i].cells[1]).text((i + 1));
    }
}

function changetab() {
    $('#groupitems').css("display", "block");
    $('#sigelitems').css("display", "none");
    $('#tcxm').addClass("tijian_hover");
    $('#dgxm').removeClass("tijian_hover");
    searchItems.clearAll(true);
    $("#searchItems .dhx_combo_input")[0].select();
}

function changetab2() {
    $('#groupitems').css("display", "none");
    $('#sigelitems').css("display", "block");
    $('#tcxm').removeClass("tijian_hover");
    $('#dgxm').addClass("tijian_hover");
    searchItems.clearAll(true);
    $("#searchItems .dhx_combo_input")[0].select();
    clikcstatus = '2';
}

//选卡切换至“体检信息”
function changeli() {
    $('#main_info').css("display", "block");
    $('#grid_patient_appoint').css("display", "none");
    $('#tjxxspan').addClass("xxhover");
    $('#rylbspan').removeClass("xxhover");
}

//选卡切换至“人员列表”
function changeli2() {
    $('#main_info').css("display", "none");
    $('#grid_patient_appoint').css("display", "block");
    $('#tjxxspan').removeClass("xxhover");
    $('#rylbspan').addClass("xxhover");
    //分页显示
    loadAppPatCount(clickexamid);
}

//浮点数加法运算
function FloatAdd(arg1, arg2) {
    var r1, r2, m;
    try {
        r1 = arg1.toString().split(".")[1].length
    } catch (e) {
        r1 = 0
    }
    try {
        r2 = arg2.toString().split(".")[1].length
    } catch (e) {
        r2 = 0
    }
    m = Math.pow(10, Math.max(r1, r2))
    return (arg1 * m + arg2 * m) / m
}

//浮点数减法运算
function FloatSub(arg1, arg2) {
    var r1, r2, m, n;
    try {
        r1 = arg1.toString().split(".")[1].length
    } catch (e) {
        r1 = 0
    }
    try {
        r2 = arg2.toString().split(".")[1].length
    } catch (e) {
        r2 = 0
    }
    m = Math.pow(10, Math.max(r1, r2));
    //动态控制精度长度
    n = (r1 >= r2) ? r1 : r2;
    return ((arg1 * m - arg2 * m) / m).toFixed(n);
}

function uploadPersonnel() {
    if ($('#examid').val() == "") {
        alert("请填写体检信息表并保存!");
        return
    }
    $.ajax({
        async: false,
        cache: false,
        type: 'get',
        url: "pexam/check.htm?examid=" + $('#examid').val(),
        error: function () {
            alert('请新建体检信息表或点击保存');
        },
        success: function (data) {
            if (data == 'no') {
                alert('请点击保存');
                return;
            } else {
                openWin('上传人员文档', '420', '200', 'pexam/importPersonne2.htm?examid=' + $('#examid').val() +
                    '&examname=' + $('#examname').val() + '&unitname=' + $('#unitname').val() + '&bookdate=' + $('#bookdate').val());
            }
        }
    });
}

//预约新建
function clearScreen() {
    //删除已选列表中的套餐
    $("#leftnav li").each(
        function () {
            $(this).removeClass("tijian_11");
        }
    );
    $('#examid').val(getExamid());
    $('#examname').val('');
    $('#unitname').val('');
    $('#salesman').val('');
    $('#bookdate').val('');
    $('#unitprice').val('');
    $('#examqty').val('');
    $('#discount').val('');
    $('#discamt').val('');
    $("#texamitems").empty();
    $('#exnum').text("0");
    $('#totalprice').text("0");
    $('#examname').focus();
}


//样式设置
function docolor(obj) {
    $("#leftnav li").each(function () {
        $(this).removeClass("tijian_11");
    });
    $(obj).addClass("tijian_11");
}

function showdetails(id) {
    var tag = "#" + id;
    var divid = id + "div";
//	var a="<div id='"+divid+"' class='cen2' onmouseover='test()'  name='dcd'><table width='191' border='0' cellspacing='0' cellpadding='0'  name='dcd'> <tr name='dcd'><td height='28' background='img/cen1.png' valign='bottom' name='dcd'><span class='cen3' name='dcd'>此套餐包含如下体检项目</span></td></tr><tr name='dcd'><td background='img/cen2.png' name='dcd'><table width='191' border='0' cellspacing='0' cellpadding='0'  name='dcd'>"+
//
//
//	"<tr valign='bottom' name='dcd'><td height='25' name='dcd'><span class='cen3' name='dcd'>项目2</span></td></tr>"+
//
//	"</table></td></tr><tr name='dcd'><td name='dcd'><img src='img/cen3.png' /></td></tr></table></div>";

    //得到套餐下面的项目

    $.ajax({
        url: "pexam/getgroupitemspop.htm",
        type: "post",
        data: "method=getGroupItemspop&groupids=" + id + "&time=" + (new Date()).valueOf(),
        success: function (data) {
            var strs = new Array();
            strs = data.split(",");
            var a = "<div id='" + divid + "' class='cen2' onmouseover='prvent()' onclick='prvent()'  name='dcd'><table width='191' border='0' cellspacing='0' cellpadding='0'  name='dcd'> <tr name='dcd'><td height='28' background='img/cen1.png' valign='bottom' name='dcd'><span class='cen3' name='dcd'>此套餐包含如下体检项目</span></td></tr><tr name='dcd'><td background='img/cen2.png' name='dcd'><table width='191' border='0' cellspacing='0' cellpadding='0'  name='dcd'>";
            for (i = 0; i < strs.length; i++) {
                var index = i + 1;
                a += "<tr valign='bottom' name='dcd'><td height='25' name='dcd'><span class='cen3' name='dcd'>&nbsp;&nbsp;" + strs[i] + "</span></td></tr>";
            }
            a += "</table></td></tr><tr name='dcd'><td name='dcd'><img src='img/cen3.png' /></td></tr></table></div>";
            // alert(a);
            $(tag).append(a);
        }

    });


}
//阻止冒泡
function prvent() {
    window.event.cancelBubble = true;
}


function removedetails(id) {
    var tag = "#" + id + "div";
    var id2 = window.event.toElement.id;
    var tname = window.event.toElement.name;
    var tagName = window.event.toElement.tagName;
    if (tname == 'dcd') {

    }
    else {

        $(tag).remove();
    }


}


function delteRow() {
    var selectedIds = grid_patient_appoint.getSelectedRowId();
    if (selectedIds == null) {
        alert('请先选中一行数据！');
    }
    else {
        if (window.confirm('你确定要删除这条信息？')) {
            $.ajax({
                url: "pexam/delnewpatient.htm",
                type: "post",
                data: "examid=" + clickexamid + "&pexamid=" + grid_patient_appoint.cells(selectedIds, 10).getValue() + "&time=" + (new Date()).valueOf(),
                success: function (data) {
                }
            });
            for (var i = 0; i < selectedIds.split(',').length; i++) {
                var selectedId = selectedIds.split(',')[i];
                grid_patient_appoint.deleteRow(selectedId);
            }
        }
    }
}

function getExamid() {
    var datetime = new Date();
    var year = datetime.getFullYear();
    var month = datetime.getMonth() + 1;//js从0开始取
    var date = datetime.getDate();
    var hour = datetime.getHours();
    var minutes = datetime.getMinutes();
    var second = datetime.getSeconds();
    var milliseconds = "" + datetime.getMilliseconds();
    if (month < 10) {
        month = "0" + month;
    }
    if (date < 10) {
        date = "0" + date;
    }
    if (hour < 10) {
        hour = "0" + hour;
    }
    if (minutes < 10) {
        minutes = "0" + minutes;
    }
    if (second < 10) {
        second = "0" + second;
    }
    while (milliseconds.lenght < 3) {
        milliseconds = "0" + milliseconds;
    }
    return parseInt("" + year + month + date + hour + minutes + second + milliseconds);
}

function setExamid() {
    if ($('#examid').val() == '') {
        var bbb = getExamid();
        $('#examid').val(bbb);
    }
    $('#examname').focus();
}

function adjustDisp() {//高度自适应，取相应的数据
    var avalibleHeight = $(window).height() - 220;
    if (avalibleHeight > 260) {
        pageSize = Math.floor((avalibleHeight - 27) / 26);
        $("#grid_patient_appoint2").css("height", avalibleHeight);
    }
}

function examPersonnelRegiste() {
    $.blockUI({
        message: "<iframe height='100%' width='100%' frameborder='0' src='pexamNew/examPersonnelRegister.htm'></iframe>",
        css: {width: "858px", height: "691px", border: "0px solid #b6cfd6", left: getLeftPos(858), top: getTopPos(691)}
    });
}
//身份证号码离开事件
function idnumcheck() {
    var date = new Date();
    var d = date.getFullYear();
    var year, month, day;
    var idnum = $("#idnum").val();//卡号
    var length = idnum.length;
    var sex;
    var idtype = combo_idtype.getComboText();//卡类型
    if (idtype == "身份证") {
        if (idnum == "" || idnum == null) {
            $("#dateofbirth").val("");
            combo_sex.setComboText("");
        } else {
            var str = checkIdcard(idnum);
            if (str != "验证通过！") {
                alert(str);
                $("#idnum").val("");
                $("#dateofbirth").val("");
                combo_sex.setComboText("");
                $("#idnum").focus();
            } else {
                if (length == 15) {
                    year = "19" + idnum.substring(6, 8);
                    month = idnum.substring(8, 10);
                    day = idnum.substring(10, 12);
                    if (idnum.substring(14, 15) % 2 == 0) {
                        sex = "女";
                    } else {
                        sex = "男";
                    }
                } else {
                    year = idnum.substring(6, 10);
                    month = idnum.substring(10, 12);
                    day = idnum.substring(12, 14);
                    if (idnum.substring(14, 17) % 2 == 0) {
                        sex = "女";
                    } else {
                        sex = "男";
                    }
                }
                if ((year - d > 0) || (month > 12) || (day > 31)) {
                    alert("身份证出生年份有误！！");
                    $("#idnum").val("");
                    $("#idnum").focus();
                }
                var birthday = year + "-" + month + "-" + day;
                $("#dateofbirth").val(birthday);
                combo_sex.setComboText(sex);

            }
        }
    }
}
//身份证验证
function checkIdcard(idcard) {
    var idcard = idcard.toUpperCase();
    var Errors = new Array("验证通过！", "身份证输入有误!")
    var length = idcard.length;
    if (length == 15 || length == 18) {
        return Errors[0];
    } else {
        return Errors[1];
    }
}

//读取身份证信息
function readCardInfo() {
    rdcard.readcard();
    var readcardno = "";
    if (rdcard.bHaveCard) {
        readcardno = rdcard.CardNo;
        rdcard.bHaveCard = false;
    }
    if (readcardno == "") {
        readcardno = $.trim($("#idnum").val());
        if (readcardno == "") {
            readcardno = $.trim($("#inscardno").val());
        }
        if (readcardno == "") {
            alert("请确认读卡器上是否有卡 或者 是否输入身份证或农保卡");
            return;
        }
        var url = "pexamNew/queryCardInfo.htm?cardno=" + readcardno + "&time=" + new Date().getMilliseconds();
        $.ajax({
            async: false,
            cache: false,
            ifModified: true,
            type: "GET",
            url: url,
            error: function (data) {
                alert(data);
            },
            success: function (data) {
                if (data.indexOf("错误：") == 0) {
                    alert(data);
                } else {
                    var json = eval("(" + data + ")");
                    if (combo_idtype.getActualValue() == "" || combo_idtype.getActualValue() == null) {
                        combo_idtype.setComboValue("身份证");
                    }
                    if ($("#idnum").val() == "") {
                        $("#idnum").val(json.personId);
                    }
                    $("#patname").val(json.personName);
                    combo_sex.setComboValue(json.personSex);
                    $("#dateofbirth").select();
                    var borndate = json.personBirth;
                    if (borndate.indexOf("-") == -1) {
                        borndate = borndate.substring(0, 4) + "-" + borndate.substring(4, 6) + "-" + borndate.substring(6, 8);
                    }
                    $("#dateofbirth").val(borndate);
                    $("#homeaddress").val(json.familyAddr);
                }
            }
        });
    } else {
        combo_idtype.setComboValue("身份证");
        $("#idnum").val(readcardno);
        $("#patname").val($.trim(rdcard.NameS));
        combo_sex.setComboValue(rdcard.SexL);
        $("#dateofbirth").select();
        var borndate = rdcard.Born;
        if (borndate.indexOf("-") == -1) {
            borndate = borndate.substring(0, 4) + "-" + borndate.substring(4, 6) + "-" + borndate.substring(6, 8);
        }
        $("#dateofbirth").val(borndate);
        $("#homeaddress").val(rdcard.Address);
    }
}