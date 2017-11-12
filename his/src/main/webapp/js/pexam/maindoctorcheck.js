var grid_doctorstation;//候检名单
var pexamid;//体检者编号
var examid;//预约编号
var comclass = "";//项目类型
var pageIndex = 0;//页面索引初始值
var pageSize = 10;//每页显示条数初始化，修改显示条数，修改这里即可    //条数自适应
var loadType = "loadAll";
var combo_isTest;
var backinfo = {};
//var combo_village;
var myCalendar;
var starttime;
var endtime;
var village;
var isTest;
var comparSearch = "";
var setting = {
    isSimpleData: true,
    treeNodeKey: "id",
    treeNodeParentKey: "pId",
    showLine: true,
    expandSpeed: "fast",
    callback: {
        click: zTreeOnClick3
    }
};

var zTree;
var treeNodes;

var originalWidth = document.documentElement.clientWidth,
    originalHeight = document.documentElement.clientHeight;

//窗口大小改变事件
window.onresize = function () {
    var _originalWidth = document.documentElement.clientWidth;
    _originalHeight = document.documentElement.clientHeight
    if (_originalWidth != originalWidth || _originalHeight != originalHeight) {

        adjust();
    }
    originalWidth = _originalWidth;
    originalHeight = _originalHeight;
}

$(document).ready(function () {
    grid_doctorstation = new dhtmlXGridObject('grid_doctorstation');
    grid_doctorstation.setImagePath("dhtmlxGrid/codebase/imgs/");
    grid_doctorstation.setHeader("序号,姓名,状态,完成,pexamid,examid,体检日期");
    grid_doctorstation.setInitWidths("30,60,40,40,5,5,*");
    grid_doctorstation.setColAlign("center,center,center,center,center,center,center");
    grid_doctorstation.setColTypes("ro,ro,ro,ro,ro,ro,ro");
    grid_doctorstation.setSkin("dhx_custom");
    grid_doctorstation.setColumnHidden(4, true);
    grid_doctorstation.setColumnHidden(5, true);
    grid_doctorstation.init();
    grid_doctorstation.attachEvent("onRowSelect", dispalyPatientInfo);
    myCalendar = new dhtmlXCalendarObject("starttime");
    myCalendar = new dhtmlXCalendarObject("endtime");
    $('#starttime').val((new Date()).format('yyyy-MM-dd'));
    $('#endtime').val((new Date()).format('yyyy-MM-dd'));

    var imgs = [];
    for (var i = 0; i < 10; i++) {
        var z = document.createElement("img");
        z.className = "dhx_combo_img";
        z.src = "imgs/combo_select_dhx_blue.gif";
        imgs.push(z);
    }

    combo_isTest = new dhtmlXCombo("isTest", "alfa3", 55);
    combo_isTest.addOption("0", "全部");
    combo_isTest.addOption("1", "在检");
    combo_isTest.addOption("2", "完成");
    combo_isTest.addOption("3", "已打印");
    combo_isTest.setComboValue("0");
    combo_isTest.setComboText("全部");
    combo_isTest.readonly(true, false);
    combo_isTest.DOMelem_input.style.cssText = "height:20px;line-height:19px";
    combo_isTest.DOMelem.appendChild(imgs[1]);
    combo_isTest.attachEvent("onChange", timeSearch);
    //combo_isTest.enableOptionAutoPositioning(top);
    //combo_village= new dhtmlXCombo("town_village","alfa3", 100);
    //combo_village.DOMelem.appendChild(imgs[2]);
    //VillagecomboInit();
    //体检类型
    //combo_examtype= new dhtmlXCombo("town_examtype","alfa3", 80);
    //combo_examtype.addOption([["灵活就业体检","灵活就业体检"],["职工体检","职工体检"],["农民体检","农民体检"],["退休人员体检","退休人员体检"],["商业体检","商业体检"]]);
    //combo_examtype.attachEvent("onChange",timeSearch);
    //combo_examtype.DOMelem.appendChild(imgs[3]);

    loadCount();

    //showresu();//隐藏总检报告--让体检结果（处于空白）

    $("#save").click(function () {
        //var deptsum = $("#deptsum").text();//体检总结
        //var suggestion = $("#suggestiontext").text();//健康建议
        var deptsum = $(window.parent.frames["myIframe"].document.getElementById("deptsum")).text();
        var suggestion = $(window.parent.frames["myIframe"].document.getElementById("suggestiontext")).text();
        $.ajax({
            async: false,
            cache: false,
            type: "post",
            url: "pexamNew/modifyMainDoctorCheck.htm",
            data: {examid: examid, pexamid: pexamid, deptsum: deptsum, suggestion: suggestion},
            error: function () {
                alert("ajax请求失败")
            },
            success: function (data) {
                if (data == "fail") {
                    alert("保存失败！");
                } else {
                    alert("总检单保存成功");
                }
            }
        });
    });
    $("#search_value").focus();


    //窗口自适应
    adjust();
});

function saveZJandWXTS(vv) {
    var deptsum = $(window.parent.frames["myIframe"].document.getElementById("deptsum")).html();
    var suggestion = "";
    if (vv != '1') {
        suggestion = window.parent.frames["myIframe"].getSuggestHtml()
    }
    var radios = $(window.parent.frames["myIframe"].document.getElementsByName("radio"));
    var checkboxs = $(window.parent.frames["myIframe"].document.getElementsByName("checkbox"));
    var canji = $(window.parent.frames["myIframe"].document.getElementById("checkbox_sycj"));
    var doctorname = "";
    var doctorId = $("#doctorId").val();
    if ($("#ZJDoctor").val() == 'Y') {
        doctorname = $("#doctorname").val();
        doctorId = $("#doctorId").val();
    }
    var idNum = $('#inscardno').text();
    var radio;
    var textValues = "";
    var cjValue = "";
    if (vv != '1') {
        $.ajax({
            async: false,
            cache: false,
            type: "post",
            url: "pexamNew/modifyMainDoctorCheck1.htm",
            data: {
                examid: examid,
                pexamid: pexamid,
                deptsum: deptsum,
                suggestion: suggestion,
                radio: radio,
                textValues: textValues,
                idNum: idNum,
                cjValue: cjValue,
                doctorname: doctorname,
                doctorId: doctorId
            },
            error: function () {
                alert("ajax请求失败")
            },
            success: function (data) {
                if (data == "fail") {
                    alert("保存失败！");
                } else {
                    alert("总检单保存成功");
                }
            }
        });
    } else {
        $.ajax({
            async: false,
            cache: false,
            type: "post",
            url: "pexamNew/modifyMainDoctorCheck.htm",
            data: {
                examid: examid,
                pexamid: pexamid,
                deptsum: deptsum,
                suggestion: suggestion,
                radio: radio,
                textValues: textValues,
                idNum: idNum,
                cjValue: cjValue,
                doctorname: doctorname,
                doctorId: doctorId
            },
            error: function () {
                alert("ajax请求失败")
            },
            success: function (data) {
                if (data == "fail") {
                    alert("保存失败！");
                } else {
                    alert("总检单保存成功");
                }
            }
        });
    }
    //保存后自动刷新树
//	grid_doctorstation.selectRow(grid_doctorstation.row.rowIndex-1);
//	dispalyPatientInfo(grid_doctorstation.getRowId(grid_doctorstation.row.rowIndex-1),1);
}
/*
 function VillagecomboInit(){
 $.ajax({
 url:"pexamNew/getVillages.htm",
 type:"post",
 data:"time="+(new Date()).valueOf(),
 error:function(){
 alert("获取数据失败");
 combo_village.closeAll();
 },
 success:function(reply){
 if(reply=="fail"){
 alert("获取数据失败");
 combo_village.closeAll();
 }else if(reply=="[]"){
 alert("无符合条件数据");
 }else{
 var jsons=eval('('+reply+')');
 for(var i=0;i<jsons.length;i++){
 combo_village.addOption(i,jsons[i].village);
 }
 }
 }
 });
 }*/
//保存
function doSaveA() {
    var selectedNode = zTree.getSelectedNode();
    var name = ''
    if (selectedNode != null) {
        name = selectedNode.name;
        if (name == '体检总结') {
            //alert('只能保存温馨提示');
            saveZJandWXTS('1');
        } else if (name == '健康建议') {
            //alert('只能保存温馨提示');
            saveZJandWXTS('2');
        } else if (name == '温馨提示') {
            var wxts = window.parent.frames["myIframe1"].getSuggestHtml();
            setTimeout(function () {
                $.ajax({
                    async: false,
                    type: "post",
                    url: "pexamNew/SaveWxts.htm",
                    //data:"examid="+examid+"&pexamid="+pexamid+"&wxts_1="+encodeURI(encodeURI(wxts)),
                    data: {examid: examid, pexamid: pexamid, wxts_1: wxts},
                    error: function () {
                        alert("ajax请求失败");
                    },
                    success: function (data) {
                        alert(data);
                    }
                });
            }, 100);
        } else {
            alert('点击树节点再进行保存');
        }
    } else {
        alert('点击树节点再进行保存');
    }

    return;
    var deptsum = $(window.parent.frames["myIframe"].document.getElementById("deptsum")).html();
    var suggestion = $(window.parent.frames["myIframe"].document.getElementById("suggestiontext")).html(); //健康建议
    suggestion = window.parent.frames["myIframe"].getSuggestHtml();
    //var bb = window.parent.frames["myIframe"].getSuggestHtml();
    var radios = $(window.parent.frames["myIframe"].document.getElementsByName("radio"));
    var checkboxs = $(window.parent.frames["myIframe"].document.getElementsByName("checkbox"));
    var canji = $(window.parent.frames["myIframe"].document.getElementById("checkbox_sycj"));
    var doctorname = window.parent.frames["myIframe"].doctorname().split(",")[0];
    //var doctorId =  window.parent.frames["myIframe"].doctorname().split(",")[1];
    var doctorId = $("#doctorId").val();
    if ($("#ZJDoctor").val() == 'Y') {
        doctorname = $("#doctorname").val();
        doctorId = $("#doctorId").val();
    }
    //alert("doctorname:"+doctorname+",doctorId:"+doctorId);
    var idNum = $('#inscardno').text();
    var radio;
    var textValues = "";
    var cjValue = "";
    //alert(canji);
    if (canji[0].checked) {
        cjValue = '身有残疾';
    }
    //alert("cjValue:"+cjValue);
    for (var i = 0; i < checkboxs.length; i++) {
        if (checkboxs[i].checked) {
            if (i == checkboxs.length - 1) {
                textValues += checkboxs[i].value;
            } else {
                textValues += checkboxs[i].value + ";";
            }
        }
    }
    //alert("textValues:"+textValues);
    for (var i = 0; i < radios.length; i++) {
        if (radios[i].checked) {
            radio = radios[i].value;
            //alert(radios[i].value);
        }
    }
    if (radio == null || radio == "") {
        //alert("体检初印象未填写！")
        //return;
    }
    //弹出提示框
    if (!window.confirm("保存后无法将体检医生站修改的内容反馈到总检医生站，确定保存吗？")) {
        return;
    }
    $.ajax({
        async: false,
        cache: false,
        type: "post",
        url: "pexamNew/modifyMainDoctorCheck.htm",
        data: {
            examid: examid,
            pexamid: pexamid,
            deptsum: deptsum,
            suggestion: suggestion,
            radio: radio,
            textValues: textValues,
            idNum: idNum,
            cjValue: cjValue,
            doctorname: doctorname,
            doctorId: doctorId
        },
        error: function () {
            alert("ajax请求失败")
        },
        success: function (data) {
            if (data == "fail") {
                alert("保存失败！");
            } else {
                alert("总检单保存成功");
            }
        }
    });
    //保存后自动刷新树
    grid_doctorstation.cells(grid_doctorstation.getRowId(grid_doctorstation.row.rowIndex - 1), 2).setValue("完成");//并且把状态改成完成
    grid_doctorstation.selectRow(grid_doctorstation.row.rowIndex - 1);
    dispalyPatientInfo(grid_doctorstation.getRowId(grid_doctorstation.row.rowIndex - 1), 1);
}

//选中人员
function dispalyPatientInfo(rowId, colIndex) {
    $('#backButton').attr('disabled', true);
    disabledButton(true);//先处于不可编辑--防止数据加载时报错
    var loadFlag = true;//加载时是否有报错
    var stamp = new Date().getMilliseconds();
    pexamid = grid_doctorstation.cells(rowId, 4).getValue();
    examid = grid_doctorstation.cells(rowId, 5).getValue();
    //取得个人信息
    $.ajax({
        async: false,
        cache: false,
        type: 'get',
        url: "pexam/getpatientinfo.htm?stamp=" + stamp + "&pexamid=" + pexamid,
        error: function () {
            alert('fail');
            loadFlag = false;
        },
        success: function (data) {
            if (data == 'fail') {
                alert('加载数据失败！');
                loadFlag = false;
            } else {
                var jsons = eval('(' + data + ')');
                var Pexam_mans = jsons[0];
                if (Pexam_mans.isprint == 'Y') {
                    document.getElementById('patname').style.backgroundImage = 'url(img/drug_1.1.gif)';
                }
                if (Pexam_mans.isprint != 'Y') {
                    document.getElementById('patname').style.backgroundImage = 'url(img/drug_1.gif)';
                }
                $("#infobase").text("基本信息（" + Pexam_mans.pexamid + "）");
                $('#pexamid').val(Pexam_mans.pexamid);
                $('#patname').text(Pexam_mans.patname);
                $('#sex').text(Pexam_mans.sex);
                $('#age').text(Pexam_mans.age);
                $('#examtype').text(Pexam_mans.examtype);
                $('#inscardno').text(Pexam_mans.idnum);
                $("#farm_address").val(Pexam_mans.laddress);
                $("#farm_name").val(Pexam_mans.patname);
                $("#farm_gender").val(Pexam_mans.sex);
                var temp_birthday = new Date(Pexam_mans.dateofbirth.time);
                $("#farm_birthday").val(temp_birthday.getFullYear() + "-" + (temp_birthday.getMonth() + 1) + "-" + temp_birthday.getDate());
            }
        }
    });

    //加载体检项目树
    $.ajax({
        async: false,
        cache: false,
        type: 'get',
        dataType: "json",
        url: "pexamNew/createTreeNew.htm?method=mainDoctorCheck&pexamid=" + pexamid + "&examid=" + examid,
        error: function () {
            alert('fail');
            loadFlag = false;
        },
        success: function (data) {
            if (data == 'fail') {
                alert("服务器错误！");
                loadFlag = false;
            } else {
                treeNodes = data;
                zTree = $("#menuTree").zTree(setting, treeNodes);
            }
        }
    });
    var gblx = $('#examtype').text();//报告类型--
    /*if("职工体检"==gblx || gblx=="退休人员体检"){
     var url = "pexamNew/zjbg.htm?examid="+examid+"&pexamid="+pexamid;
     document.getElementById("myIframe").src = url;
     }else if("农民体检"==gblx){
     //var url = "pexamNew/lntjbgd.htm?examid="+examid+"&pexamid="+pexamid;
     //document.getElementById("myIframe").src = url;
     var url = "pexamNew/zjbg.htm?examid="+examid+"&pexamid="+pexamid;
     document.getElementById("myIframe").src = url;
     }else{
     document.getElementById("myIframe").src = "";
     }*/
    var url = "pexamNew/zjbg.htm?examid=" + examid + "&pexamid=" + pexamid;
    document.getElementById("myIframe").src = url;
    if (loadFlag) {
        disabledButton(false);
    }
    showdetail();
    $("#search_value").select();
    //alert(1)
    //zTree.selectNode(zTree.getNodesByParam("id", "zjbg",null)); //选中体检总结 树节点
    /*
     * 查询是否有体检医生上报过来的 危急值
     */
    queryTjDoctorWJZ();
}
function queryTjDoctorWJZ() {
    //alert(pexamid)
    //判断是否有未确认的 上报来的数据
    $.ajax({
        async: false,
        cache: false,
        type: 'post',
        url: "phyexamActionYH/confirmreasonBackReason.htm?stamp=" + new Date() + "&pexamid=" + pexamid + "&type=1",
        error: function () {
            alert('fail');
        },
        success: function (data) {
            if (data.indexOf('fail') > -1) {
                alert('加载数据失败！');
            } else {
                if (data == 'success') {
                    openWin('上报:', 632, 700, 'phyexamActionYH/report_tjysz.htm?time=' + new Date().getTime() + "&zjdoc=" + "Y" + "&pexamid=" + pexamid);
                }
            }
        }
    });


}

//传染病上报
// -lsp
function contagionrptPut() {
    $('#backButton').attr('disabled', true);
    $.ajax({
        async: false,
        cache: false,
        ifModified: true,
        type: "POST",
        url: "pexam/checkhandled.htm?pexamid=" + $('#pexamid').val(),
        dataType: "json",
        error: function () {
            alert("服务器内部错误！");
        },
        success: function (data) {
            if (!data.status) {
                alert('获取数据失败！');
            } else if (data.message == '修改上报') {
                showConfirm("已经提交的重大阳性，是否修改？");
                $("#close_iframe_img").click(function () {
                    $.unblockUI();
                });
                $("#confirmButton").click(function () {
                    openWin('重大阳性上报单', '604', '602', "pexam/showcontagionrpt.htm?pexamid=" + $('#pexamid').val() + "&patientId=" + "" + "&diagNo=" + "" + "&diagName=" + encodeURI(encodeURI("")) + "&type=doreadd" + "&id=" + data.value);
                });
                $("#abolishButton").click(function () {
                    $.unblockUI();
                });
            } else if (data.message == '新建上报') {
                showConfirm("是否确认上报重大阳性？");
                $("#close_iframe_img").click(function () {
                    $.unblockUI();
                });
                $("#confirmButton").click(function () {
                    openWin('重大阳性上报单', '604', '602', "pexam/showcontagionrpt.htm?pexamid=" + $('#pexamid').val() + "&patientId=" + "" + "&diagNo=" + "" + "&diagName=" + encodeURI(encodeURI("")) + "&type=doadd");
                });
                $("#abolishButton").click(function () {
                    $.unblockUI();
                });
            }
        }
    });

//	showConfirm("是否确认上报传染病？");
//	$("#close_iframe_img").click(function() {
//				$.unblockUI();
//			});
//	$("#confirmButton").click(function() {
//		openWin('法定传染病上报单', '604', '602',
//			"pexam/showcontagionrpt.htm?pexamid="+$('#pexamid').val());
//	});
//	$("#abolishButton").click(function() {
//				$.unblockUI();
//			});

}

function showdetail() {
    $('#backButton').attr('disabled', true);
    $("div.tjbg_1").show();//总检报告
    $("div.tjbg_2").hide();
    $("#resu").hide();//体检结果
}
function showresu() {
    $('#backButton').attr('disabled', true);
    $("div.tjbg_1").hide();
    $("div.tjbg_2").hide();
    $("#resu").show();
}

Array.prototype.uniq = function () {
    var temp = {}, len = this.length;
    for (var i = 0; i < len; i++) {
        if (typeof temp[this[i]] == "undefined") {
            temp[this[i]] = 1;
        }
    }
    this.length = 0;
    len = 0;
    for (var i in temp) {
        this[len++] = i;
    }
    return this;
}

function replayTag() {

}

/*
 function ss(s){
 var ss  = s;
 $("#suggestiontext").append(ss);
 $("#suggestiontext").focus();
 }
 */

//点击树
function zTreeOnClick3(event, treeId, treeNode) {
    $('#backButton').removeAttr('disabled');
    $("#LogC").text("");
    backinfo.itemcode = treeNode.id;
    backinfo.itemname = treeNode.name;
    backinfo.pexamid = $('#pexamid').val();
    var itemcode = treeNode.id;
    if (treeNode.isParent) {
        return false;
    }
    if (itemcode == "zjbg") {//点击的是"总检报告";
        $('#backButton').attr('disabled', true);
        var url = "pexamNew/zjbg.htm?examid=" + examid + "&pexamid=" + pexamid;
        document.getElementById("myIframe").src = url;
        $("div.tjbg_1").show();
        $("div.tjbg_2").hide();
        $("#resu").hide();
        return;
    }
    //点击的是"健康建议  ";
    if (itemcode == "jkjy") {
        $('#backButton').attr('disabled', true);
        var url = "pexamNew/zjbg_jkjy.htm?examid=" + examid + "&pexamid=" + pexamid;
        document.getElementById("myIframe").src = url;
        $("div.tjbg_1").show();
        $("div.tjbg_2").hide();
        $("#resu").hide();
        return;
    }

    if (itemcode == "wxts") {//点击的是"温馨提示 ";
        $('#backButton').attr('disabled', true);
        var url = "pexamNew/wxts.htm?examid=" + examid + "&pexamid=" + pexamid;
        document.getElementById("myIframe1").src = url;
        $("div.tjbg_2").show();
        $("div.tjbg_1").hide();
        $("#resu").hide();
        return;
    }
    showresu();//隐藏总检报告

    var itemcode = treeNode.id;//组合项id
    var itemuuid = treeNode.itemuuid;
    comclass = treeNode.comclass;

    $('#titems tr').remove();//删除table中的内容
    $('#jghead td').remove();
    $('#ep3_1').remove();   //删除2个表头
    $('#ep3_2').remove();
    $('#jghead').append("<td width='80' class='tj_ysz2' id='ep1'>体检项目</td>" +
        "<td  class='tj_ysz2' id='ep2'>体检结果</td>" +
        "<td width='75' class='tj_ysz2'	id='ep3'>单位</td>");


    $('#examtypep').text(treeNode.name);//组合项名称
    var deptSum = "";//科室小结
    var excdoctorname = "";//体检医生
    $.ajax({
        async: false,
        type: "post",
        url: "pexamNew/createExamNew2.htm",
        data: "itemcode=" + itemcode + "&pexamid=" + pexamid + "&sex=" + $('#sex').text() + "&itemuuid=" + itemuuid + "&comclass=" + comclass + "&examid=" + examid + "&time=" + (new Date()).valueOf(),
        error: function () {
            alert("ajax请求失败");
            $("#LogC").text(deptSum);
            $("#excdoctorname").text(excdoctorname);
        },
        success: function (data) {
            if (data == 'fail') {
                alert("加载数据失败");
            } else {
                var obj = eval('(' + data + ')');
                if (treeNode.name == '基础检查') {
                    $("#ep0").width(30);
                    $("#ep1").width(80);
                    $("#ep3").width(75);
                    //$('#dtitems').addClass("autohe");
                } else {
                    $("#ep0").width(30);
                    $("#ep1").width(80);
                    var rustwidth = $(window).width() - 270 - 210 - 300;
                    $("#ep2").width(rustwidth);
                    $("#ep3").width(75);
                }

                if (obj.isExamStart == '未体检') {
                    $("#titems").append("<tr><td>无该体检项目未进行体检</td></tr>");
                } else {
                    var jsons = obj.details;
                    for (var i = 0; i < jsons.length; i++) {
                        var result = jsons[i].result == "" ? "&nbsp;" : jsons[i].result;
                        if (comclass == '检验') {
                            //如果是检验
                            $('#ep3_1').remove();   //删除2个表头
                            $('#ep3_2').remove();
                            $('#jghead td').remove();
                            $('#jghead').append("<td width='150' class='tj_ysz2' align='center' id='ep1'>体检项目</td>" +
                                "<td width='200' class='tj_ysz2' align='right' id='ep2'>体检结果</td>" +
                                "<td width='200' class='tj_ysz2' align='right'	id='ep3'>单位</td>" +
                                "<td width='150' class='tj_ysz2' align='right'  id='ep3_1'>范围</td>" +
                                "<td width='100' class='tj_ysz2' align='right' style='margin-right: 30px;' id='ep3_2'>高低值</td>");
                            var unnormal = jsons[i].unnormal == "" ? "&nbsp;" : jsons[i].unnormal;
                            if (unnormal.indexOf('h') > -1 || unnormal.indexOf('hh') > -1) {
                                unnormal = "↑";
                            } else if (unnormal.indexOf('l') > -1 || unnormal.indexOf('ll') > -1) {
                                unnormal = "↓";
                            }
                            var resultunit = jsons[i].resultunit == "" ? "&nbsp;" : jsons[i].resultunit;
                            var range = jsons[i].range == "" ? "&nbsp;" : jsons[i].range;
                            $("#titems").append("<tr>" +
                                "<td style='border-right:1px solid #93afba;border-bottom:1px solid #93afba;' align='left' width='300' >"
                                + jsons[i].indname + "</td>" +
                                "<td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba;' " +
                                "  width='" + ($(window).width() - 270 - 210 - 300) + " '>" + result + "</td>" +
                                "<td  style='border-bottom:1px solid #93afba;border-right:1px solid #93afba; '  width='200' bgcolor='#F6FAFF'>" + resultunit + "</td>" +
                                "<td  style='border-bottom:1px solid #93afba; border-right:1px solid #93afba;'  width='200' bgcolor='#F6FAFF'>" + range + "</td>" +
                                "<td  style='border-bottom:1px solid #93afba; '  width='100' bgcolor='#F6FAFF'>" + unnormal + "</td>" +
                                "</tr>");
                        } else {
                            if (treeNode.name == '一般检查' || treeNode.name == '一般检查(全)' || treeNode.name == '一般检查*' || treeNode.name == '一般检查(全)*') {
                                $('#ep3_1').remove();   //删除2个表头
                                $('#ep3_2').remove();
                                $('#jghead td').remove();
                                var maxval_v = jsons[i].maxval; //项目的参考上限
                                var minval_v = jsons[i].minval; //项目的参考下限
                                if (maxval_v == '0' || minval_v == '0') {
                                    maxval_v = '&nbsp;';
                                    minval_v = '&nbsp;';
                                }
                                $('#jghead').append("<td width='150' class='tj_ysz2' align='left' id='ep1'>体检项目</td>" +
                                    "<td width='250' class='tj_ysz2' align='right' id='ep2'>体检结果</td>" +
                                    "<td width='200' class='tj_ysz2' align='right'	id='ep3'>单位</td>" +
                                    "<td width='100' class='tj_ysz2' align='right' style='margin-right: 30px;' id='ep3_1'>上限</td>" +
                                    "<td width='100' class='tj_ysz2' align='center' style='margin-right: 30px;' id='ep3_2'>下限</td>");
                                $("#titems").append("<tr>" +
                                    "<td style='border-right:1px solid #93afba; border-bottom:1px solid #93afba;' align='left' width='80' >" + jsons[i].indname + "</td>" +
                                    "<td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba;' width='" + ($(window).width() - 270 - 210 - 300) + "'>" + result + "</td>" +
                                    "<td  style='border-right:1px solid #93afba;border-bottom:1px solid #93afba; '  width='80' bgcolor='#F6FAFF'>" + (jsons[i].resultunit == "" ? "&nbsp;" : jsons[i].resultunit) + "</td>" +
                                    "<td  style='border-right:1px solid #93afba;border-bottom:1px solid #93afba; '  width='80' bgcolor='#F6FAFF'>" + maxval_v + "</td>" +
                                    "<td  style='border-bottom:1px solid #93afba; '  width='80' bgcolor='#F6FAFF'>" + minval_v + "</td>"
                                );
                            } else {
                                if (jsons[i].resultunit == "") {
                                    //$("#titems").append("<tr><td style='border-right:1px solid #93afba;border-bottom:1px solid #93afba;' align='left' width='150' >"+jsons[i].indname+"</td><td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba; ' width='380'>"+result+"</td><td style='border-bottom:1px solid #93afba; ' width='71' bgcolor='#F6FAFF'>&nbsp;</td></tr>");
                                    //$("#titems").append("<tr><td style='border-right:1px solid #93afba;border-bottom:1px solid #93afba;' align='left' width='150' >"+jsons[i].indname+"</td><td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba; ' width='"+($(window).width()-270-210-300)+"'>"+result+"</td><td style='border-bottom:1px solid #93afba; ' width='71' bgcolor='#F6FAFF'>&nbsp;</td></tr>");
                                    $("#titems").append("<tr><td style='border-right:1px solid #93afba;border-bottom:1px solid #93afba;' align='left' width='80' >" + jsons[i].indname + "</td><td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba;' width='" + ($(window).width() - 270 - 210 - 300) + " '>" + result + "</td><td style='border-bottom:1px solid #93afba; ' width='71' bgcolor='#F6FAFF'>&nbsp;</td></tr>");
                                } else {
                                    //$("#titems").append("<tr><td style='border-right:1px solid #93afba; border-bottom:1px solid #93afba;' align='left' width='150' >"+jsons[i].indname+"</td><td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba; ' width='380'>"+result+"</td><td  style='border-bottom:1px solid #93afba; '  width='71' bgcolor='#F6FAFF'>"+jsons[i].resultunit+"</td></tr>");
                                    // $("#titems").append("<tr><td style='border-right:1px solid #93afba; border-bottom:1px solid #93afba;' align='left' width='150' >"+jsons[i].indname+"</td><td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba; ' width='"+($(window).width()-270-210-300)+"'>"+result+"</td><td  style='border-bottom:1px solid #93afba; '  width='71' bgcolor='#F6FAFF'>"+jsons[i].resultunit+"</td></tr>");
                                    $("#titems").append("<tr><td style='border-right:1px solid #93afba; border-bottom:1px solid #93afba;' align='left' width='80' >" + jsons[i].indname + "</td><td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba;' width='" + ($(window).width() - 270 - 210 - 300) + "'>" + result + "</td><td  style='border-bottom:1px solid #93afba; '  width='71' bgcolor='#F6FAFF'>" + jsons[i].resultunit + "</td></tr>");
                                }
                            }
                        }
                    }
                    deptSum = obj.deptSum;
                    excdoctorname = obj.excdoctorname;
                }
            }
            $("#LogC").text(deptSum);
            $("#excdoctorname").text(excdoctorname);
        }
    });
    $('#backButton').removeAttr('disabled');
}

function topHigh() {
    var tr = document.getElementById("choose")
    if (tr.style.display == "none") {
        $("#choose").show();
    } else {
        $("#choose").hide();
    }
}
function timeSearch() {
    comparSearch = "loadByTime";
    topSearch1();
}


function topSearch() {
    comparSearch = "loadByTopSearch";
    topSearch1();
}
var searchvalue = "";

function topSearch1() {
    $("#choose").hide();
    var nameOrId = $("#search_value").val() == null ? "" : $("#search_value").val();//病人名字或者id
    starttime = $("#starttime").val();
    endtime = $("#endtime").val();
    //village = combo_village.getComboText();
    //var examtype=combo_examtype.getComboText();
    isTest = combo_isTest.getComboText();
    //alert("starttime:"+starttime+"   endtime:"+endtime+"  village:"+village+"  isTest:"+isTest);
    /*
     if(nameOrId==''){
     return false;
     }
     */

    //慈溪卡号后4位切割
    if (searchvalue.length == 20) {
        searchvalue = searchvalue.substr(0, 16);
        $("#search_value").val(searchvalue);
    }

    loadType = "loadBySearch";
    searchvalue = nameOrId;
    $('#pagination').css("display", "block");
    $.ajax({
        async: false,
        cache: false,
        ifModified: true,
        type: "post",
        url: "pexamNew/topSearchPatListCount.htm",
        data: "time=" + (new Date()).valueOf() + "&method=mainDoctorCheck" + "&starttime=" + starttime + "&endtime=" + endtime + "&isTest=" + encodeURI(encodeURI(isTest)) + "&searchvalue=" + nameOrId + "&comparSearch=" + comparSearch,
        dataType: "json",
        error: function () {
            alert("服务器内部错误！");
        },
        success: function (data) {
            if (data.status) {
                pageIndex = 0;
                var pageCount = data.value;
                createPagination(pageCount);
            } else {
                alert(data.message);
            }
        }
    });
}

//加载候检名单数据
function searchLoadPatList() {
    starttime = $("#starttime").val();
    endtime = $("#endtime").val();
    //village = combo_village.getComboText();
    //var examtype=combo_examtype.getComboText();
    isTest = combo_isTest.getComboText();
    //alert("starttime:"+starttime+"endtime:"+endtime+"village:"+village+"isTest:"+isTest);
    $.ajax({
        async: true,
        cache: false,
        type: "post",
        url: "pexamNew/topSearchPatListData.htm",
        data: "time=" + (new Date()).valueOf() + "&method=mainDoctorCheck" + "&starttime=" + starttime + "&endtime=" + endtime + "&isTest=" + encodeURI(encodeURI(isTest)) + "&index=" + pageIndex + "&size=" + pageSize + "&searchvalue=" + searchvalue + "&comparSearch=" + comparSearch,
        error: function () {
            alert("ajax请求失败！");
        },
        success: function (data) {
            if (data == 'fail') {
                alert("获取候检人名单失败！");
            } else {
                var json = eval("(" + data + ")");
                grid_doctorstation.clearAll();

                $('#infobase').text("基本信息");
                $('#patname').text("");//姓名
                $('#sex').text("");//性别
                $('#age').text("");//年龄
                $('#examtype').html("&nbsp;&nbsp;&nbsp;&nbsp;");//体检类型
                $('#inscardno').text("");//身份证号
                showresu();
                document.getElementById("myIframe").src = "";
                $("#titems").html("");//检查结果
                $("#LogC").text("");//科室小结
                $("#excdoctorname").text("");//医生姓名

                treeNodes = [];
                zTree = $("#menuTree").zTree(setting, treeNodes);

                if (json.length > 0) {
                    var status = "";
                    for (var i = 0; i < json.length; i++) {
                        if (json[i].edate == null) {
                            //status = json[i].total==json[i].fin?"完成":"在检";
                            status = json[i].printtime == null ? json[i].isover : "<span style=\"color: green\">已打印</span>";
                            //json[i].isover=="完成"?"完成":"在检",
                        } else {
                            status = "结束";
                        }
                        grid_doctorstation.addRow(getUUID().replace(/-/g, ""), [
                            i + 1,
                            json[i].patname,
                            status,
                            json[i].fin + "/" + json[i].total,
                            json[i].pexamid,
                            json[i].examid,
                            json[i].bdate1
                        ]);
                    }
                    grid_doctorstation.selectRow(0);
                    dispalyPatientInfo(grid_doctorstation.getRowId(0), 1);
                } else {
                    alert("查无此人");
                    disabledButton(true);
                    $("#search_value").select();
                }
            }
        }
    });
}

function doStyle(status) {
    if (status) {
        $("#save2").css("display", "none");
        $("#save").css("display", "block");
        $("#myprint2").css("display", "none");
        $("#myprint").css("display", "block");
    } else {
        $("#save").css("display", "none");
        $("#save2").css("display", "block");
        $("#myprint").css("display", "none");
        $("#myprint2").css("display", "block");
    }
}

function doSaveA1() {
    var deptsum = $(window.parent.frames["myIframe"].document.getElementById("deptsum")).text();
    var suggestion = $(window.parent.frames["myIframe"].document.getElementById("suggestiontext")).text();
    var radios = $(window.parent.frames["myIframe"].document.getElementsByName("radio"));
    var checkboxs = $(window.parent.frames["myIframe"].document.getElementsByName("checkbox"));
    var canji = $(window.parent.frames["myIframe"].document.getElementById("checkbox_sycj"));
    var doctorname = window.parent.frames["myIframe"].doctorname().split(",")[0];
    var doctorId = window.parent.frames["myIframe"].doctorname().split(",")[1];
    //alert("doctorname:"+doctorname+",doctorId:"+doctorId);
    var idNum = $('#inscardno').text();
    var radio;
    var textValues = "";
    var cjValue = "";
    //alert(canji);
    if (canji[0].checked) {
        cjValue = '身有残疾';
    }
    //alert("cjValue:"+cjValue);
    for (var i = 0; i < checkboxs.length; i++) {
        if (checkboxs[i].checked) {
            if (i == checkboxs.length - 1) {
                textValues += checkboxs[i].value;
            } else {
                textValues += checkboxs[i].value + ";";
            }
        }
    }
    //alert("textValues:"+textValues);
    for (var i = 0; i < radios.length; i++) {
        if (radios[i].checked) {
            radio = radios[i].value;
            //alert(radios[i].value);
        }
    }
    if (radio == null || radio == "") {
        //alert("体检初印象未填写！")
        //return false;
    }
    var flag = false;
    $.ajax({
        async: false,
        cache: false,
        type: "post",
        url: "pexamNew/modifyMainDoctorCheck.htm",
        data: {
            examid: examid,
            pexamid: pexamid,
            deptsum: deptsum,
            suggestion: suggestion,
            radio: radio,
            textValues: textValues,
            idNum: idNum,
            cjValue: cjValue,
            doctorname: doctorname,
            doctorId: doctorId
        },
        error: function () {
            alert("ajax请求失败")
        },
        success: function (data) {
            if (data == "fail") {
                flag = false;
            } else {
                flag = true;
            }
        }
    });
    //保存后自动刷新树
    //grid_doctorstation.cells(grid_doctorstation.getRowId(grid_doctorstation.row.rowIndex-1),2).setValue("完成");//并且把状态改成完成
    grid_doctorstation.selectRow(grid_doctorstation.row.rowIndex - 1);
    dispalyPatientInfo(grid_doctorstation.getRowId(grid_doctorstation.row.rowIndex - 1), 1);
    return flag;
}

function loadCount() {
    $('#pagination').css("display", "block");
    $.ajax({
        async: true,
        cache: false,
        ifModified: true,
        type: "post",
        url: "pexamNew/getDoctorStationPatientCountNew.htm",
        data: "time=" + (new Date()).valueOf() + "&method=mainDoctorCheck",
        dataType: "json",
        error: function () {
            alert("服务器内部错误！");
        },
        success: function (data) {
            if (data.status) {
                pageIndex = 0;
                var pageCount = data.value;
                createPagination(pageCount);
            } else {
                alert(data.message);
            }
        }
    });
}

function loadPatList() {
    //加载候检人名单
    $.ajax({
        async: true,
        cache: false,
        type: "post",
        url: "pexamNew/getDoctorStationPatientNew.htm",
        data: "time=" + (new Date()).valueOf() + "&method=mainDoctorCheck&index=" + pageIndex + "&size=" + pageSize,
        error: function () {
            alert("ajax请求失败！");
        },
        success: function (data) {
            if (data == 'fail') {
                alert("获取候检人名单失败！");
            } else {
                var json = eval("(" + data + ")");
                grid_doctorstation.clearAll();
                for (var i = 0; i < json.length; i++) {
                    grid_doctorstation.addRow(getUUID().replace(/-/g, ""), [
                        i + 1,
                        json[i].patname,
                        //json[i].total==json[i].fin?"完成":"在检",
                        //json[i].isover=="完成"?"完成":"在检",
                        json[i].printtime == null ? json[i].isover : "<span style=\"color: green\">已打印</span>",
                        //json[i].isover,    //如果打印时间不为空 状态是已打印
                        json[i].fin + "/" + json[i].total,
                        json[i].pexamid,
                        json[i].examid,
                        json[i].bdate1
                    ]);
                }
            }
            if (json.length > 0) {
                grid_doctorstation.selectRow(0);
                dispalyPatientInfo(grid_doctorstation.getRowId(0), 1);
            }
        }
    });
}

function createPagination(pageCount) {//创建分页标签
    if (pageCount == 0) {
        pageCount = 0;
    }
    //分页，pageCount是总条目数，这是必选参数，其它参数都是可选
    $("#pagination").pagination(pageCount, {
        callback: pageCallback,
        prev_text: '上一页',       //上一页按钮里text
        next_text: '下一页',       //下一页按钮里text
        items_per_page: pageSize,  //显示条数
        num_display_entries: 2,    //连续分页主体部分分页条目数
        current_page: pageIndex,   //当前页索引
        num_edge_entries: 1        //两侧首尾分页条目数
    });
}
function pageCallback(index, jq) {//翻页回调
    pageIndex = index;
    if ("loadBySearch" == loadType) {
        searchLoadPatList();
    } else {
        loadPatList();
    }
    return false;
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

function disabledButton(flag) {
    if (flag) {
        $("#suggestButton").attr("disabled", "true");
        $("#examResultButton").attr("disabled", "true");
        $("#saveButton").attr("disabled", "true");
        $("#completeButton").attr("disabled", "true");
        $("#printButton").attr("disabled", "true");
        $("#printSomeButton").attr("disabled", "true");
        $("#oneKeyButton").attr("disabled", "true");
    } else {
        $("#suggestButton").removeAttr("disabled");
        $("#examResultButton").removeAttr("disabled");
        $("#saveButton").removeAttr("disabled");
        $("#completeButton").removeAttr("disabled");
        $("#printButton").removeAttr("disabled");
        $("#printSomeButton").removeAttr("disabled");
        $("#oneKeyButton").removeAttr("disabled");
    }
}

function doPrint(obj) {
    $('#backButton').attr('disabled', true);
    var gblx = $('#examtype').text();//报告类型--
    var sug = obj.name;
    if (gblx == "学生体检") {
        doPrint_stu(sug, gblx);
    } else {
        doPrintA(sug);
    }

}

function doSaveFarmer() {
    var examsum = $(window.parent.frames["myIframe"].document.getElementById("examSum")).val();//体检结果
    $.ajax({
        async: false,
        cache: false,
        url: "pexamNew/doSaveFarmExamResult.htm",
        type: "post",
        data: "pexamid=" + pexamid + "&examid=" + examid + "&examsum=" + examsum + "&time=" + (new Date()).valueOf(),
        error: function () {
            alert("ajax请求失败!")
        },
        success: function (reply) {
            if (reply == "fail") {
                alert("保存失败！");
            } else {
                alert("总检单保存成功");
            }
        }
    });
}
//保存  按钮
function doSave() {
    $('#backButton').attr('disabled', true);
    var gblx = $('#examtype').text();//报告类型--
    //if("农民体检"==gblx){
    //doSaveFarmer();
    //}else if("职工体检"==gblx){
    doSaveA();
    //}
    $("#search_value").focus();
}

//完成
function click_completeButton() {
    var selectedId = grid_doctorstation.getSelectedRowId();
    if (selectedId == null || selectedId == '' || selectedId == "null") {
        alert('请在体检病人列表中选择一列');
        return;
    }
    var pexam_status = grid_doctorstation.cells(selectedId, "2").getValue();
    if (pexam_status != '完成') {
        //alert('请先点击保存');
        //return　;
    }
    if (window.confirm("点击完成之后体检医生将不能录入体检结果，确定完成吗？")) {
        $.ajax({
            async: false,
            type: "post",
            url: "pexamNew/completePexam.htm",
            data: {
                examid: grid_doctorstation.cells(selectedId, "5").getValue(),
                pexamid: grid_doctorstation.cells(selectedId, "4").getValue()
            },
            error: function () {
                alert("ajax请求失败")
            },
            success: function (data) {
                alert(data);
                grid_doctorstation.deleteRow(grid_doctorstation.cells(selectedId, "4").getValue());
                //grid_doctorstation.deleteSelectedRows();
                grid_doctorstation.cells(grid_doctorstation.getRowId(grid_doctorstation.row.rowIndex - 1), 2).setValue("完成");//并且把状态改成完成
            }
        });
    }
}


//窗口自适应
function adjust() {
    var autoheight = ($(window).height() - 310);
    var autowidth = ($(window).width());
    /*
     $("#dleft").css("height", autoheight+10);//候检人列表高度
     $("#grid_doctorstation").css("height",autoheight-10);//-10//候检人列表中grid的高度
     $("#trc").css("height", autoheight+116);//总检项目(树的高度)
     */
    var widths = document.documentElement.clientWidth - 525;
    if (widths > (92 * 6)) {
        $("#but1").show();
        $("#but2").hide();
        $("#dleft").css("height", autoheight - 1);//候检人列表高度
        pageSize = Math.round((autoheight + 10) / 24) - 5;
        $("#grid_doctorstation").css("height", autoheight - 10 - 20);//-10//候检人列表中grid的高度
        $("#trc").css("height", autoheight + 116);//总检项目(树的高度)
    } else {
        $("#but1").hide();
        $("#but2").show();
        $("#dleft").css("height", autoheight + 10 + 30);//候检人列表高度
        pageSize = Math.round((autoheight + 10 + 30) / 24) - 4;
        $("#grid_doctorstation").css("height", autoheight - 10 + 30);//-10//候检人列表中grid的高度
        $("#trc").css("height", autoheight + 116 + 30);//总检项目(树的高度)
    }
    $("#zjbgbk").css("width", autowidth - 520);
    $("#dtitems").css("height", autoheight - 34);
    $("#leftg").css("height", autoheight + 10);
    $("#ijkjy").css("height", $(window).height() - 273);
    $(".tjbg_1").css("height", autoheight + 110);
    $(".tjbg_2").css("height", autoheight + 110);
    $("#resu").css("height", autoheight + 118);
    $("#dleft1").css("height", autoheight + 18);
    /*
     //设置体检结果的宽度
     var rightdiv=$("#resu").width();//右侧iframe的宽度
     var kbwidth=rightdiv-30;
     $("#dtitems").width(kbwidth);//空白窗口的宽度
     */

}
function disabledButton(flag) {
    if (flag) {
        $("#contagionrptButton").attr("disabled", "true");
        $("#previewButton").attr("disabled", "true");
        $("#suggestButton").attr("disabled", "true");
        $("#examResultButton").attr("disabled", "true");
        $("#saveButton").attr("disabled", "true");
        $("#completeButton").attr("disabled", "true");
        $("#printButton").attr("disabled", "true");
        $("#printSomeButton").attr("disabled", "true");
        $("#oneKeyButton").attr("disabled", "true");
    } else {
        $("#contagionrptButton").removeAttr("disabled");
        $("#previewButton").removeAttr("disabled");
        $("#suggestButton").removeAttr("disabled");
        $("#examResultButton").removeAttr("disabled");
        $("#saveButton").removeAttr("disabled");
        $("#completeButton").removeAttr("disabled");
        $("#printButton").removeAttr("disabled");
        $("#printSomeButton").removeAttr("disabled");
        $("#oneKeyButton").removeAttr("disabled");
    }
}
function showOneKeySuggest() {
    openWin1('一键总检', '1000', '540', 'pexamNew/showSuggestMoreData.htm?time=' + new Date().getTime());
}
//体检项目回退
function backButtonClick() {
    var title = '';
    if (backinfo.itemname.indexOf('*') > -1) {
        title = backinfo.itemname.replace('*', '');
    }
    if (backinfo.itemcode != '0' && backinfo.itemcode != 'zjbg') {
        openWin('回退:' + title, 632, 221, 'pexamNew/toHuiTui.htm?time=' + new Date().getTime() + "&itemcode=" + backinfo.itemcode + "&pexamid=" + backinfo.pexamid);
    }

}

