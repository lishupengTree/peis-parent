var grid_doctorstation;//候检名单
var grid_Results;
var Result_indid;
var dname;
var pexamid;//选中的体检者编号
var examid;//选择者所属的预约id
var treeNodeId;//组合项id
var treeNodeName;//组合项名称
var itemuuid;//pexam_items_title主键
var comclass = "";//组合项类别
var operationType = "";//操作状态--add和modify
var pageIndex = 0;//页面索引初始值
var pageSize = 16;//每页显示条数初始化，修改显示条数，修改这里即可
var pageIndex1 = 0;
var pageSize1 = 1;
var loadType = "loadAll";
var showInterval;
var hiddenInterval;
var zTree;
var treeNodes;
var treeNode;
var combo_doctorName;
var iszh;
var typeid;//大项id
var typename;//大项名称
var lisType;//lis系统厂商类型
var mouseLocation; //鼠标位置
var app = {};
var searchvalue = "";
var comparSearch;
var ind_diagno = [];
var ind_numObj = {}; //全局变量  存放小项或者科室小结的最大序号。（点击小项框的时候初始化值。）
var buweiStr; // 全局变量  存放部位
var setting = {
    isSimpleData: true,
    treeNodeKey: "id",
    treeNodeParentKey: "pId",
    showLine: true,
    expandSpeed: "fast",
    callback: {
        click: zTreeOnClick
    }
};

function ff() {
    $('#LogC').attr('id', 'LogC' + $('#deptcode').val());
    $("#addcommonresult").attr("disabled", "true");
    document.onmousedown = function () {
        mouseLocation = mouseMove();
    }
    app.faqdivpos = getPosition(document.getElementById('faqdiv'));
    //alert(app.faqdivpos.x);
    insertimg();
}

$(document).ready(function () {
    //体检医生 危急值上报
    $('#report_tjysz').click(function () {
        var selectedNode = zTree.getSelectedNode();
        var itemcode = selectedNode.id;
        var pexamid = $('#pexamid').val();
        openWin('上报:', 632, 700, 'phyexamActionYH/report_tjysz.htm?time=' + new Date().getTime() + "&zjdoc=" + "N" + "&pexamid=" + pexamid);
    });
    toSelfWH();//页面自适应
    /*
     var winheight=$(window).height();
     var topheight=$(".top").height();
     $(".tj_ysz1").css("height",winheight-topheight);
     $("#trc").css("height",winheight-topheight-110);
     $("#grid_doctorstation").css("height", winheight-320);
     $("#dtitems").css("height", winheight-440);
     $("#grid_Results").css("height", winheight-240);
     $("#calculator").css("height",winheight-245);
     var winwidth=$(window).width();
     $("#right_all").css("width",winwidth-195-30);
     $("#dtitems").css("width", winwidth-660);
     $("#ep2").css("width",winwidth-660-75-150);
     var aa=$("#dtitems").width()/8.3;
     $("#LogC"+$('#deptcode').val()).attr("cols",Math.round(aa));
     //alert("科室小结宽度："+Math.round(aa));
     $("#addcommonresulttd").css("width",$("#dtitems").width()-135-90-90);
     */
    grid_doctorstation = new dhtmlXGridObject('grid_doctorstation');
    grid_doctorstation.setImagePath("dhtmlxGrid/codebase/imgs/");
    grid_doctorstation.setHeader("序号,姓名,状态,完成,pexamid,examid,体检时间");
    grid_doctorstation.setInitWidths("30,60,35,30,5,5,*");
    grid_doctorstation.setColAlign("center,center,center,center,center,center,center");
    grid_doctorstation.setColTypes("ro,ro,ro,ro,ro,ro,ro");
    grid_doctorstation.setSkin("dhx_custom");
    grid_doctorstation.setColumnHidden(4, true);
    grid_doctorstation.setColumnHidden(5, true);
    grid_doctorstation.init();
    grid_doctorstation.attachEvent("onRowSelect", dispalyPatientInfo);
    hiddenPatientList(50, 80);


    //常见结果
    grid_Results = new dhtmlXGridObject('grid_Results');
    grid_Results.setImagePath("dhtmlxGrid/codebase/imgs/");
    grid_Results.setSkin("dhx_custom");
    //grid_Results.setHeader("<img id='all_img' src='dhtmlxGrid/codebase/imgs/item_chk0.gif' onclick='checkAll()'/>,常见结果,状态,sugestid");
    //------------------------0---1----2-----3------4---------------
    grid_Results.setHeader("序号,诊断,常见结果,状态,sugestid");
    grid_Results.setColAlign("center,left,left,center,center");
    grid_Results.setInitWidths("30,80,*,30,60");
    grid_Results.setColTypes("ro,ro,ro,ro,ro");
    grid_Results.setColumnHidden(4, true);
    grid_Results.attachEvent("onRowSelect", function (rId, cInd) {
        var info = grid_Results.cells(rId, 2).getValue();
        $('#result_details_text').text(info);
    });    //常用结果的加载（点击小项的框的函数  loadCommonResults）
    grid_Results.attachEvent("onRowDblClicked", doAddResults);

    //grid_Results.attachEvent("onCheck",doAddResults1);
    //心电科室不需要正常和异常，并且正常和异常能一起存在
    if ($("#deptcode").val() == "12334") {
        grid_Results.setColumnHidden(3, true);
    }
    grid_Results.init();


    var isDishDept = $("#isDishDept").val();//是否区分科室
    if (isDishDept == 'Y') {
        //区分科室
    } else {
        reBulidCombo_doctorName();//combo_doctorname初始化
    }
    $("#search_value").focus();
    lisType = $("#lisType").val();//--获取lis系统厂商类型---
    var myCalendar = new dhtmlXCalendarObject(["starttime", "endtime"]);
    myCalendar.setDateFormat("%Y-%m-%d");
    var date = myCalendar.getDate() //回传Calendar中目前选定的日期
    var endtime = myCalendar.getFormatedDate("%Y-%m-%d", date);
    var year = date.getYear();
    var month = date.getMonth();
    var day = date.getDate();
    if (month.toString().length == 1 && day.toString().length == 1) {
        document.getElementById("starttime").value = year + "-0" + month + "-0" + day;
    } else if (month.toString().length == 1 && day.toString().length == 2) {
        document.getElementById("starttime").value = year + "-0" + month + "-" + day;
    } else if (month.toString().length == 2 && day.toString().length == 1) {
        document.getElementById("starttime").value = year + "-" + month + "-0" + day;
    } else {
        document.getElementById("starttime").value = year + "-" + month + "-" + day;
    }
    document.getElementById("starttime").value = endtime;
    $("#endtime").val(endtime);

    //加载候检人名单
    loadCount();
});

//ctrl+s保存快捷
$(document).bind("keydown", function (event) {
    if (event.ctrlKey && (event.keyCode == 83)) {
        event.preventDefault();
        event.keyCode = 0;
        event.returnValue = false;
        saveitemdetils();
    }
});

//体检病人列表搜索
function timeSearch1111() {
    comparSearch = "loadByTime";
    topSearch1();
}

//页面头部的搜索
function topSearch() {
    comparSearch = "loadByTopSearch";
    topSearch1();
}
function topSearch1() {
    var nameOrId = $("#search_value").val();//病人名字或者id
    if ((nameOrId == '' || nameOrId.length < 2) && comparSearch != "loadByTime") {
        $("#search_value").select();
        return false;
    }

    //慈溪卡号后4位切割
    if (nameOrId.length == 20) {
        nameOrId = nameOrId.substr(0, 16);
        $("#search_value").val(nameOrId);
    }

    loadType = "loadBySearch";
    searchvalue = nameOrId;
    $('#pagination').css("display", "block");
    var urldata;
    var starttime = $("#starttime").val();
    var endtime = $("#endtime").val();
    if ("loadByTopSearch" == comparSearch) {
        urldata = "time=" + (new Date()).valueOf() + "&method=doctorStation&searchvalue=" + nameOrId + "&comparSearch=" + comparSearch;
    } else if (comparSearch == "loadByTime") {
        searchvalue = "";
        urldata = "time=" + (new Date()).valueOf() + "&method=doctorStation&searchvalue=" + nameOrId + "&starttime=" + starttime + "&endtime=" + endtime + "&comparSearch=" + comparSearch;
    }
    $.ajax({
        async: false,
        cache: false,
        ifModified: true,
        type: "post",
        url: "pexamNew/topSearchPatListCount.htm",
        data: urldata,
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
    var urldata;
    var starttime = $("#starttime").val();
    var endtime = $("#endtime").val();
    if ("loadByTopSearch" == comparSearch) {
        urldata = "time=" + (new Date()).valueOf() + "&method=doctorStation&index=" + pageIndex + "&size=" + pageSize + "&searchvalue=" + searchvalue + "&comparSearch=" + comparSearch;
    } else if (comparSearch == "loadByTime") {
        searchvalue = "";
        urldata = "time=" + (new Date()).valueOf() + "&method=doctorStation&index=" + pageIndex + "&size=" + pageSize + "&searchvalue=" + searchvalue + "&comparSearch=" + comparSearch + "&starttime=" + starttime + "&endtime=" + endtime;
    }
    $.ajax({
        async: true,
        cache: false,
        type: "post",
        url: "pexamNew/topSearchPatListData.htm",
        data: urldata,
        error: function () {
            alert("ajax请求失败！");
        },
        success: function (data) {
            if (data == 'fail') {
                alert("获取候检人名单失败！");
            } else {
                var json = eval("(" + data + ")");
                grid_doctorstation.clearAll();

                disabledButton(true);
                $("#LogC" + $('#deptcode').val()).text("");//清空科室小结
                $("#titems ").html("");//内科体检项目
                $("#basinfo").text("基本信息");
                $('#patname').text("");//体检人员姓名
                $('#sex').text("");//性别
                $('#age').text("");//年龄
                $('#examtype').html("&nbsp;&nbsp;&nbsp;&nbsp;");//体检类型
                $('#inscardno').text("");//身份证号
                if ($("#isDishDept").val() != "Y") {
                    combo_doctorName.setComboText("");
                }
                treeNodes = [];
                zTree = $("#menuTree").zTree(setting, treeNodes);

                if (json.length > 0) {
                    for (var i = 0; i < json.length; i++) {
                        grid_doctorstation.addRow(getUUID().replace(/-/g, ""), [
                            i + 1,
                            json[i].patname,
                            json[i].total == json[i].fin ? "<font color='red'>完成</font>" : "在检",
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
                    $("#search_value").select();
                    $("#search_value").focus();
                }
            }
        }
    });
}

//加载候检名单总条数
//页面加载的 时候执行
//查找当天的体检病人
function loadCount() {
    $('#pagination').css("display", "block");
    $.ajax({
        async: true,
        cache: false,
        ifModified: true,
        type: "post",
        url: "pexamNew/getDoctorStationPatientCountNew.htm",
        data: "time=" + (new Date()).valueOf() + "&method=doctorStation" + "&starttime=" + $('#starttime').val() + "&endtime=" + $('#endtime').val(),
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
function loadPatList() {
    $.ajax({
        async: true,
        cache: false,
        type: "post",
        url: "pexamNew/getDoctorStationPatientNew.htm",
        data: "time=" + (new Date()).valueOf() + "&method=doctorStation&index=" + pageIndex + "&size=" + pageSize + "&starttime=" + $('#starttime').val() + "&endtime=" + $('#endtime').val(),
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
                        json[i].total == json[i].fin ? "<font color='red'>完成</font>" : "在检",
                        json[i].fin + "/" + json[i].total,
                        json[i].pexamid,
                        json[i].examid,
                        json[i].bdate1
                    ]);
                }
            }
        }
    });
}

//点击树节点
function zTreeOnClick(event, treeId, treeNode) {
    var xmstatus = treeNode.xmstatus;
    $.post('pexamNew/seeIfBackReason.htm', 'itemcode=' + treeNode.id + '&pexamid=' + $('#pexamid').val(),
        function (data) {
            if (data.indexOf('fail') > -1 || data == 'none') {
                //alert(data);
            } else if (data == 'ok') {
                //有回退记录的话
                openWin('回退提醒', 500, 300, 'pexamNew/toSeeBackPage.htm?itemcode=' + treeNode.id + '&pexamid=' + $('#pexamid').val());
            }
        });
    var ggxm = treeNode.ggxm; //公共项目
    var deptcode = treeNode.deptcode; //组合的执行科室Id
    var _deptcode = $('#deptcode').val(); //登录的科室Id
    //alert(treeNode.ggxm+'--  '+treeNode.deptcode);
    $("#LogC" + $('#deptcode').val()).text("");
    if (treeNode.isParent) {//是父节点
        return false;
    }
    grid_Results.clearAll();
    $("#dname").text("常见结果");
    $("#all_img").attr("src", "dhtmlxGrid/codebase/imgs/item_chk0.gif");
    $("#calculator").css("display", "none");
    /****自动保存***/
    //if($("#titems").html()!=''&&combo_doctorName.setComboText()!=''){
    //alert('aa');
    //saveitemdetils();
    //}
    /****自动保存***/

    var itemcode = treeNode.id;//体检组合项id
    treeNodeId = itemcode;//体检组合项id
    treeNodeName = treeNode.name;//体检组合项名称
    itemuuid = treeNode.itemuuid;//pexam_items_title主键
    iszh = treeNode.iszh;//是否组合项
    var sex = $("#sex").text();
    comclass = treeNode.comclass;//组合类型
    typeid = treeNode.typeid;//大项id
    typename = treeNode.typename;//大项名称
    var age = $("#age").text();

    // alert("typeid:"+typeid+'--'+comclass);
    if (iszh == "是") {
        // alert("重置四列表头!");
        tableColToFour();
    } else {
        // alert("重置三列表头!");
        tableColToThree();
    }
    disabledButton(false);//让按钮处于可编辑状态
    //$("#afrSugBut").attr("disabled",true);
    $.ajax({
        async: false,
        type: "post",
        url: "pexamNew2/createExamNew2.htm",
        data: "itemcode=" + itemcode + "&examid=" + examid + "&pexamid=" + pexamid + "&itemuuid=" + itemuuid + "&sex=" + sex + "&comclass=" + comclass + "&time=" + (new Date()).valueOf() + "&iszh=" + iszh + "&typeid=" + typeid + "&age=" + age + "&lisType=" + lisType,
        error: function () {
            alert("ajax请求失败");
            disabledButton(true);//获取数据失败--让按钮处于不可编辑状态
        },
        success: function (data) {
            $('#titems tr').remove();//删除table中的内容
            $('#examtypep').text(treeNode.name);//组合项名称
            var deptSum = "";
            var excdoctorname = "";
            var typeflag = "";
            if (data == 'fail') {
                alert("加载数据失败！");
            } else {
                var obj = eval('(' + data + ')');
                buweiStr = obj.buweiStr;
                obj.lisflag = "F";
                if (iszh == "是") {
                    /*
                     $("#ep1").width(149);
                     $("#ep2").width(($(window).width()-660-75-150));
                     $("#ep3").width(72);
                     $("#ep4").width(72);
                     */
                    $("#ep4").width(90);
                    $("#ep1").width(90);
                    $("#ep2").width(($(window).width() - 660 - 50 - 180));
                    $("#ep3").width(50);

                } else {
                    //---常规项目td宽度调整--
                    $("#ep1").width(90);
                    $("#ep2").width(($(window).width() - 660 - 50 - 180));
                    $("#ep3").width(50);
                }
                if ("其他" == comclass || "检查" == comclass) {
                    var jsons = obj.details;
                    operationType = obj.operationType;//操作类型
                    if (obj.excdoctorname != '') {//体检医生不为空
                        $("#saveResultButton").val("保存");
                        excdoctorname = obj.excdoctorname;
                    } else {
                        $("#saveResultButton").val("保存");
                    }
                    //如果只有1个小项
                    if (jsons.length == 1) {
                        //删除原来绘制的表头  重新绘制
                        tableColToThree1();
                        disabledButton(false);//让按钮处于可编辑状态
                        //$("#afrSugBut").attr("disabled",true);
                        $('#titems tr').remove();//删除table中的内容
                        $('#examtypep').text(treeNode.name);//组合项名称
                        if (obj.excdoctorname != '') {//体检医生不为空
                            $("#saveResultButton").val("保存");
                            excdoctorname = obj.excdoctorname;
                        } else {
                            $("#saveResultButton").val("保存");
                        }
                        $("#ep1").width(90);
                        $("#ep2").width(($(window).width() - 660 - 50 - 180));
                        $("#ep3").width(50);
                        for (var i = 0; i < jsons.length; i++) {
                            var itemhtml = "";//组套节点
                            var indiszh = jsons[i].iszh;//是否组合
                            var jsonIndSon = jsons[i].sonIndList;//子节点
                            var indid = jsons[i].indid//节点id
                            var indname = jsons[i].indname;//节点名称
                            itemhtml += " <tr width='100%' style='border:1px solid red' ><td style='border-right:1px solid #93afba;border-bottom:1px solid #93afba;'  align='left' width='95px' >" + indname + "</td><td colspan='2'><table id=" + indid + " name=" + indname + " iszh=" + indiszh + "></table></td></tr> ";
                            if (indiszh == '是') {
                                $("#titems tbody").eq(0).append(itemhtml);
                                normorSonIndsShow(indid, indname, jsonIndSon);
                            } else {
                                var jsonInd = jsons[i];
                                if (indname == "住院史" || indname == "家庭病床史") {
                                    normorIndShowZy(jsonInd, jsonIndSon);//住院常规项目显示
                                } else if (indname == "主要用药情况") {
                                    normorIndShowYy(jsonInd, jsonIndSon);//主要用药情况项目显示
                                } else if (indname == "非免疫规划预防接种史") {
                                    normorIndShowJzs(jsonInd, jsonIndSon);//非免疫预防接种史项目显示
                                } else {
                                    normorIndShow1(indid, indname, jsonInd);//常规项目显示
                                }
                            }
                        }
                    } else {
                        for (var i = 0; i < jsons.length; i++) {//遍历加载体检项目
                            //	alert(jsons[i].minval+"-"+jsons[i].maxval);
                            var itemhtml = "";//组套节点
                            //var ck = "";
                            //var inpname =jsons[i].resulttype=="数值"?"num":"str";
                            //--1.遍历体检组合项目--
                            //--2.循环遍历组合项目下小项--
                            var indiszh = jsons[i].iszh;//是否组合
                            var jsonIndSon = jsons[i].sonIndList;//子节点
                            var indid = jsons[i].indid//节点id
                            var indname = jsons[i].indname;//节点名称
                            itemhtml += " <tr width='100%' style='border:1px solid red' ><td style='border-right:1px solid #93afba;border-bottom:1px solid #93afba;'  align='left' width='95px' >" + indname + "</td><td colspan='2'><table id=" + indid + " name=" + indname + " iszh=" + indiszh + "></table></td></tr> ";
                            if (indiszh == '是') {
                                $("#titems tbody").eq(0).append(itemhtml);
                                //----组合项目显示----
                                //----二级项目遍历节点显示-----
                                normorSonIndsShow(indid, indname, jsonIndSon);
                            } else {
                                //----常规项目遍历节点显示-----
                                // alert("indname:"+indname);
                                var jsonInd = jsons[i];
                                if (indname == "住院史" || indname == "家庭病床史") {
                                    normorIndShowZy(jsonInd, jsonIndSon);//住院常规项目显示
                                } else if (indname == "主要用药情况") {
                                    normorIndShowYy(jsonInd, jsonIndSon);//主要用药情况项目显示
                                } else if (indname == "非免疫规划预防接种史") {
                                    normorIndShowJzs(jsonInd, jsonIndSon);//非免疫预防接种史项目显示
                                } else {
                                    normorIndShow(indid, indname, jsonInd);//常规项目显示
                                }
                            }
                        }
                    }
                    //------中医体质辨识 弹出报表窗口-------
                    //alert("typeid:"+typeid);
                    if (typeid == '00031') {
                        //openZytj();//弹出问卷界面
                        $("#zytjbsb").removeAttr("disabled");
                    } else {
                        $("#zytjbsb").remove();//如果检验数据已获取显示回滚按钮
                    }

                    $("#" + jsons[0].indid).focus();
                    $("#" + jsons[0].indid).select();
                    deptSum = obj.deptSum;
                } else if ("检验" == comclass || "外送" == comclass) {

                    //$("#errorMsgButton").removeAttr("disabled");
                    $("#errorMsg").val(obj.errorMsg);		//检验项目匹配日志
                    var lisflag = obj.lisflag;//LIS提供的数据是否有检验数据了
                    if (lisflag == "F") {//有数据,所有项目都匹配上了
                        var newName = treeNode.name.replace("*", "");
                        treeNode.name = newName;
                        zTree.updateNode(treeNode, true);
                    } else if (lisflag == "Y") {//有数据,部分匹配
                        var unMatchDate = obj.unMatchDate;//未匹配上的项目
                        alert("【" + treeNodeName + "】:以下体检项目未匹配成功,请核对好后重新匹配：\r" + unMatchDate);
                    } else if (lisflag == "N") { //无检验数据
                        alert("【" + treeNodeName + "】:未查询到该项检验结果,结果尚未生成或未审核!")
                    }
                    typeflag = obj.typeflag;//检验项目手输N 还是 自动获取Y
                    typeflag = 'Y';
                    var hosnum = $("#hosnum").val();
                    typeflag = "Y";
                    if (typeflag == "Y") {
                        // $("#rollBackButton").attr("disabled","disabled");//如果检验数据已获取显示回滚按钮
                        //$("#rollBackButton").removeAttr("disabled");
                        $("#saveResultButton").attr("disabled", "true");//如果检验数据已获取显示回滚按钮
                        $("#confirmsaveResultButton").attr("disabled", "true");
                        $("#typeflag").val(typeflag);
                        operationType = "";
                        var jsons = obj.details;
                        if (jsons.length > 0) {//存在
                            for (var i = 0; i < jsons.length; i++) {
                                var ck = jsons[i].result;
                                ck = ck == "null" ? "" : ck;
                                var exceptionResult = "";
                                if (jsons[i].unnormal == '异常') {
                                    exceptionResult = jsons[i].indname + ":" + ck + jsons[i].resultunit + (jsons[i].stringvalue == "" ? "" : (" " + jsons[i].stringvalue) + " ") + (jsons[i].range == "" ? "" : ("(参考值：" + jsons[i].range + ")"));
                                }
                                if (jsons[i].resultunit == "" || jsons[i].resultunit == "null") {
                                    $("#titems").append("<tr><td style='border-right:1px solid #93afba;border-bottom:1px solid #93afba;' align='left' width='110' unnormal='" + jsons[i].unnormal + "' exceptionResult='" + exceptionResult + "'>" + jsons[i].indname + "</td><td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba; ' width='" + ($(window).width() - 660 - 110 - 63) + "'>" + ck + "</td><td style='border-bottom:1px solid #93afba; ' width='62' bgcolor='#F6FAFF' resultunit='" + jsons[i].resultunit + "'>&nbsp;</td></tr>");
                                } else {
                                    $("#titems").append("<tr><td style='border-right:1px solid #93afba;border-bottom:1px solid #93afba;' align='left' width='145' unnormal='" + jsons[i].unnormal + "' exceptionResult='" + exceptionResult + "'>" + jsons[i].indname + "</td><td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba; ' width='" + ($(window).width() - 660 - 110 - 63) + "'>" + ck + "</td><td  style='border-bottom:1px solid #93afba; '  width='62' bgcolor='#F6FAFF' resultunit='" + jsons[i].resultunit + "'>" + jsons[i].resultunit + "</td></tr>");
                                }
                            }
                            $("#saveResultButton").val("保存");
                            //$("#saveResultButton").append("<span class='font2'>(ctrl+s)</span>");
                            excdoctorname = obj.excdoctorname;
                            deptSum = obj.deptSum;
                        } else {
                            //一般不可能出现此情况
                        }
                        // }
                    } else {
                        var jsons = obj.details;
                        operationType = obj.operationType;//操作类型
                        if (obj.excdoctorname != '') {//体检医生不为空
                            $("#saveResultButton").val("保存");
                            //$("#saveResultButton").append("<span class='font2'>(ctrl+s)</span>");
                            excdoctorname = obj.excdoctorname;
                        } else {
                            $("#saveResultButton").val("保存");
                            //$("#saveResultButton").append("<span class='font2'>(ctrl+s)</span>");
                        }
                        for (var i = 0; i < jsons.length; i++) {
                            var ck = "";
                            var inpname = jsons[i].resulttype == "数值" ? "num" : "str";
                            //ck += "<input type='text' id='" + jsons[i].indid + "' onfocus='this.select()' onBlur='autosuggest(this)' inputv='"+jsons[i].indid+"' value='"+(jsons[i].result==""?jsons[i].defaultv:jsons[i].result)+"'  name='"+inpname+"'  style='border-left:1px;border-top:1px;border-right:1px;border-bottom:1px; border-bottom-color:Black;width:100%'/> ";
                            if (jsons[i].resulttype == "数值") {
                                //alert(jsons[i].maxval);
                                //alert(jsons[i].indid);
                                ck += "<input type='text' id='" + jsons[i].indid + "' onfocus='loadWrite(this)' onclick='loadWrite(this)' onkeydown = 'enter(this)'  inputv='" + jsons[i].indid + "' value='" + (jsons[i].result == "" ? jsons[i].defaultv : jsons[i].result) + "'  name='" + inpname + "' dtype='" + inpname + "' dname='" + jsons[i].indname + "'  style='border-left:1px;border-top:1px;border-right:1px;border-bottom:1px; border-bottom-color:Black;width:99%'/> ";
                                ck += "<input type='hidden' value='" + jsons[i].maxval + "'>"//上限值
                                    + "<input type='hidden' value='" + jsons[i].minval + "'>"//下限值
                                    + "<input type='hidden' value='" + jsons[i].maxpromp + "'>"//上线提示
                                    + "<input type='hidden' value='" + jsons[i].minpromp + "'>"//下限提示
                                    + "<input type='hidden' value='" + (jsons[i].result == "" ? jsons[i].defaultv : jsons[i].result) + "'>";//是否改过结果值对照值
                            } else if (jsons[i].resulttype == "文字") {
                                ck += "<input type='text' id='" + jsons[i].indid + "' onfocus='loadCommonResults(this)' onclick='loadCommonResults(this)'  onkeydown = 'enter(this)'  inputv='" + jsons[i].indid + "' value='" + (jsons[i].result == "" ? jsons[i].defaultv : jsons[i].result) + "'  dtype='" + inpname + "' name='" + inpname + "' dname='" + jsons[i].indname + "' style='border-left:1px;border-top:1px;border-right:1px;border-bottom:1px; border-bottom-color:Black;width:99%'/> ";
                                ck += "<input type='hidden' value='" + jsons[i].defaultv + "'>"//默认值--后台设置的必须是正常的
                                    + "<input type='hidden'  value='" + (jsons[i].unnormal == "" ? "正常" : jsons[i].unnormal) + "'>"//是否正常标志--不是绝对的
                                    + "<input type='hidden'  value='" + jsons[i].result + "'>"//通过弹出层选择的结果保存列
                                    + "<input type='hidden' value='" + (jsons[i].result == "" ? jsons[i].defaultv : jsons[i].result) + "'>";//是否改过结果值对照值
                            }
                            if (jsons[i].resultunit == "") {
                                $("#titems").append("<tr><td style='border-right:1px solid #93afba;border-bottom:1px solid #93afba;' align='left' width='110' sn='" + jsons[i].sn + "'>" + jsons[i].indname + "</td><td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba; '  width='" + ($(window).width() - 660 - 63 - 110) + "' >" + ck + "</td><td style='border-bottom:1px solid #93afba;' width='62' bgcolor='#F6FAFF' resultunit='" + jsons[i].resultunit + "'>&nbsp;</td></tr>");
                            } else {
                                $("#titems").append("<tr><td style='border-right:1px solid #93afba; border-bottom:1px solid #93afba;' align='left' width='110' sn='" + jsons[i].sn + "'>" + jsons[i].indname + "</td><td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba; '  width='" + ($(window).width() - 660 - 63 - 110) + "'>" + ck + "</td><td  style='border-bottom:1px solid #93afba; '  width='62' bgcolor='#F6FAFF' resultunit='" + jsons[i].resultunit + "'>" + jsons[i].resultunit + "</td></tr>");
                                //$("#titems").append("<tr><td style='border-right:1px solid #93afba; border-bottom:1px solid #93afba;' align='left' width='110' sn='"+ jsons[i].sn +"'>"+jsons[i].indname+"</td><td>"+jsons[i].result+"</td><td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba; '  width='"+($(window).width()-660-63-110)+"'>"+ck+"</td><td  style='border-bottom:1px solid #93afba; '  width='62' bgcolor='#F6FAFF' resultunit='"+jsons[i].resultunit+"'>"+jsons[i].resultunit+"</td></tr>");
                            }
                        }
                        $("#" + jsons[0].indid).focus();
                        $("#" + jsons[0].indid).select();
                        deptSum = obj.deptSum;
                    }
                }
            }

            //不分科室是该项目的体检医生
            if ($("#isDishDept").val() != "Y") {
                //combo_doctorName.setComboText(excdoctorname);
                if (excdoctorname != "") {
                    combo_doctorName.setComboText(excdoctorname);
                } else if (comclass == "其他" || comclass == "检查") {
                    getDoctrname(treeNodeId, combo_doctorName);//---获取最近一次医生保存姓名
                } else if (comclass == "检验" || comclass == "外送") {
                    combo_doctorName.setComboText("");//如果检查项目为检验则将默认医生框清空
                }
            }
            //科室小结
            $("#LogC" + $('#deptcode').val()).text(deptSum);
        }
    });
    //设置 name='ind_content' 这个textarea为kinder编辑器
//	var editor;
//	KindEditor.ready(function(K) {
//		K.create('textarea[name="ind_content"]', {
//			cssPath : ['kindeditor-4.0.3/plugins/code/prettify.css', 'index.css'],
//			items : []
//		});
//	});
//	UE.getEditor('myEditor',{
//        //这里可以选择自己需要的工具按钮名称,此处仅选择如下五个
//        toolbars:[['FullScreen', 'Source', 'Undo', 'Redo','Bold','test']],
//        //focus时自动清空初始化时的内容
//        autoClearinitialContent:true,
//        //关闭字数统计
//        wordCount:false,
//        //关闭elementPath
//        elementPathEnabled:false,
//        //默认的编辑区域高度
//        initialFrameHeight:300,
//        //更多其他参数，请参考ueditor.config.js中的配置项
//        serverUrl: '/server/ueditor/controller.php'
//    })


    /*
     //----titems的html-----
     alert($("#titems").html());
     $("#LogC"+$('#deptcode').val()).text($("#titems").html());
     */
    //判断是否是公共项目  保存的按钮是否可以点击
    if (ggxm == 'Y') {
        if (deptcode != _deptcode) {
            $("#saveResultButton").attr("disabled", "true");
            $("#confirmsaveResultButton").attr("disabled", "true");
        } else {
            $("#saveResultButton").removeAttr("disabled");
            $("#confirmsaveResultButton").removeAttr("disabled");
        }
    } else {
        if (deptcode == _deptcode && _deptcode == '12334') {  //是心电科科室小结框加大。
            $('#titems').parent().css('height', 200);
            $('#LogC' + $('#deptcode').val()).attr('rows', '11');
            $('#titems').css('overflow', 'hidden');
            //$('#titems textarea').css('overflow-y','hidden');
            //$('#titems textarea').removeAttr('rows');
        }
    }
    if (xmstatus == '弃检') {
        $("#saveResultButton").attr("disabled", "true");
        $("#confirmsaveResultButton").attr("disabled", "true");
    }
}
//----其他 + 检查类型 获取最近一次保存医生的姓名------
function getDoctrname(treeNodeId, combo_doctorName) {
    $.ajax({
        async: false,
        cache: false,
        ifModified: true,
        type: "post",
        url: "pexamNew/SearchDoctorname.htm?treeNodeId=" + treeNodeId,
        dataType: "json",
        error: function () {
            alert("服务器内部错误！");
        },
        success: function (data) {
            combo_doctorName.setComboText(data.value);
        }
    });

}
//选中候检人名单
function dispalyPatientInfo(rowId, colIndex) {
    $("#LogC" + $('#deptcode').val()).text("");//清空科室小结
    $("#titems ").html("");//内科体检项目
    var stamp = new Date().getMilliseconds();
    pexamid = grid_doctorstation.cells(rowId, 4).getValue();
    examid = grid_doctorstation.cells(rowId, 5).getValue();//如果是“个人体检”则为“0000”
    //获取体检人个人信息
    $.ajax({
        async: false,
        cache: false,
        type: 'get',
        url: "pexam/getpatientinfo.htm?stamp=" + stamp + "&pexamid=" + pexamid,
        error: function () {
            alert('fail');
        },
        success: function (data) {
            if (data == 'fail') {
                alert('加载数据失败！');
            } else {
                var jsons = eval('(' + data + ')');
                var Pexam_mans = jsons[0];
                $('#dnqgz').text("");
                $('#patname').text(Pexam_mans.patname);
                $('#sex').text(Pexam_mans.sex);
                $('#age').text(Pexam_mans.age);
                $('#examtype').text(Pexam_mans.examtype);
                $('#inscardno').text(Pexam_mans.idnum);
                $('#bdate').text(Pexam_mans.isbdate);
                //$('#village').text(Pexam_mans.village);
                $('#kwxd').text(Pexam_mans.kwxd);
                $('#guoji').text(Pexam_mans.guoji);
                $('#minzu').text(Pexam_mans.minzu);
                $('#zjxy').text(Pexam_mans.zjxy);
                $('#shuimian').text(Pexam_mans.shuimian);
                $('#dnqgz').text(Pexam_mans.wordincomputer);
                $('#ysxg').text(Pexam_mans.tsys);
                $('#basinfo').text("基本信息（" + Pexam_mans.pexamid + "）");
                $('#pexamid').val(Pexam_mans.pexamid);//---选中的值给隐藏域赋值  点击的时候有用----
            }
        }
    });

    //加载体检项目树
    $.ajax({
        async: false,
        cache: false,
        type: 'get',
        dataType: "json",
        url: "pexamNew2/createTreeNew.htm?method=doctorStation&pexamid=" + pexamid + "&examid=" + examid + "&lisType=" + lisType,
        error: function () {
            alert('fail');
        },
        success: function (data) {
            if (data == 'fail') {
                alert("加载体检项目失败！");
            } else {
                treeNodes = data;
                $(eval(treeNodes)).each(function (index, elem) {
                    if (elem.xmstatus == '完成') {
                        elem.icon = "img/yhtj/yhtj_wc.gif";
                    }
                });
                zTree = $("#menuTree").zTree(setting, treeNodes);
                getFirstLeaf(0);
            }
        }
    });
}

//保存
function saveitemdetils() {
    //afreshSuggest();
    var flag;
    var v_result;
    var doctorid = "";//暂时不要医生id
    var doctorname = "";
    if ($("#isDishDept").val() != "Y") {//不区分科室
        doctorid = "";//暂时不要医生id
        doctorname = combo_doctorName.getComboText();
        if (doctorname == '') {
            alert('请填写医生姓名！');
            window.setTimeout(function () {
                combo_doctorName.openSelect();
            }, 100);
            return;
        }
    } else {
        doctorid = $("#doctorId").val();
        doctorname = $("#doctorName").val();
    }

    var data = "";
    var deptsum = $("#LogC" + $('#deptcode').val()).text();//科室小结
    deptsum = deptsum.replace(/\+/g, '@');//检验项目加号转换
    //deptsum=deptsum.replace(/\%/g,'$');
    var typeflag = $("#typeflag").val();
    //---检验项目且是自动获取----
    if ("检验" == comclass && typeflag == "Y") {
        data = "comclass=" + comclass + "&itemuuid=" + itemuuid + "&pexamid=" + pexamid + "&doctorid=" + doctorid + "&doctorname=" + doctorname + "&deptsum=" + encodeURI(encodeURI(deptsum)) + "&typeflag=" + typeflag + "&time=" + (new Date()).valueOf();
    } else {
        //------遍历项目结果值-----
        var resultArr = [];//----项目结果集
        var resultArr_son = [];//---自定义项目结果集---
        if (iszh == "是") {
            //--如果是二级组合项目--
            resultArr = TwoItemsResult();
        } else {

            //--一级组合项目--
            if (typeid == '00029' || typeid == '00030') {
                resultArr = OneItemsResult_zdy();//--自定义项的结果集---
                resultArr_son = OneItemsResult_jzs();//---非免疫常规接种史子项 结果集----
            } else if (typeid == '00028') {
                resultArr = OneItemsResult_zdy();//--自定义项的结果集---
                resultArr_son = OneItemsResult_zy();//---住院治疗情况 结果集----
            } else {
                var tempObj = OneItemsResult();//--一般项的结果集---
                resultArr = tempObj.resultArr;
                flag = tempObj.flag;
                v_result = tempObj.v_result;
            }

        }
        data = "comclass=" + comclass + "&resultArr=" + encodeURI(encodeURI(toJSON(resultArr))) + "&itemuuid=" + itemuuid + "&pexamid=" + pexamid + "&examid=" + examid + "&comid=" + treeNodeId + "&comname=" + treeNodeName + "&doctorid=" + doctorid + "&doctorname=" + doctorname + "&deptsum=" + encodeURI(encodeURI(deptsum)) + "&operationType=" + operationType + "&typeflag=" + typeflag + "&time=" + (new Date()).valueOf() + "&iszh=" + iszh + "&resultArr_son=" + encodeURI(encodeURI(toJSON(resultArr_son)))
            + "&typeid=" + typeid + "&flag=" + flag + "&v_result=" + encodeURI(encodeURI(v_result));
    }
    $.ajax({
        async: false,
        url: "pexamNew2/saveItemDetailsNew3.htm",
        type: "post",
        data: data,
        error: function () {
            alert("ajax请求失败");
        },
        success: function (reply) {
            if (reply.indexOf("fail") != -1) {
                //showMsg("数据保存失败!");
                alert(reply);
                $('#Log' + $('#deptcode').val()).focus();
            } else {
                var temp = grid_doctorstation.cells(grid_doctorstation.getRowId(grid_doctorstation.row.rowIndex - 1), 3).getValue();
                var tempArr = temp.split("/");
                temp = reply + "/" + tempArr[1];
                grid_doctorstation.cells(grid_doctorstation.getRowId(grid_doctorstation.row.rowIndex - 1), 3).setValue(temp);
                if (reply == tempArr[1]) {
                    grid_doctorstation.cells(grid_doctorstation.getRowId(grid_doctorstation.row.rowIndex - 1), 2).setValue("完成");
                }
                operationType = "modify"
                $("#saveResultButton").val("保存");
                //$("#saveResultButton").append("<span class='font2'>(ctrl+s)</span>");
                showMsg("数据保存成功!");
            }
        }
    });
    //刷新树
    //refleshTree();
    $("#search_value").select();
    ind_diagno.length = 0;
    $('#search_value').val(pexamid);
    //topSearch();
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


//结果类型为字符的--小结生成
function autoSuggestStr(obj, deptSumArr) {
    var temp = [];
    var id = obj.id;//结果文本框的id
    var result = obj.value;//结果值
    var tdNodeObj2 = $("#" + id).parent();//结果文本框的父节点
    var inputNodes = tdNodeObj2.children();//所有的input
    var result2 = inputNodes.eq(4).val();//对照用---看是否改过结果
    //alert(result+"----"+result2);
    if (result2 != result) {//对结果进行判断--如果相等，则为未改过不需要重新生成小结
        var indName = tdNodeObj2.prev().text();//获取指标名称
        var defaultv = inputNodes.eq(1).val();//默认值
        var result3 = inputNodes.eq(3).val();
        var unnormal = inputNodes.eq(2).val();
        var removeOrModify = "";//如果结果为正常，则删除小结中异常的显示“remove”
        if (result == "" || result == defaultv) {//规则--如果结果为空，则表示该体检结果无默认值，且是正常的结果
            removeOrModify = "remove";
        } else if (result == result3) {
            if (unnormal == "异常") {
                removeOrModify = "modify";
            } else {
                removeOrModify = "remove";
            }
        } else {//不正常
            removeOrModify = "modify";
        }
        if (removeOrModify == "remove") {
            for (var i = 0; i < deptSumArr.length; i++) {
                if (deptSumArr[i].indexOf(indName) < 0) {//指标不存
                    temp.push(deptSumArr[i]);
                }
            }
        } else if (removeOrModify == "modify") {
            var newResult = indName + ":" + result;
            //var newResult = result;
            if (deptSumArr.length > 0) {//小结中有值
                var flag = true;
                for (var i = 0; i < deptSumArr.length; i++) {
                    if (deptSumArr[i].indexOf(indName) < 0) {
                        temp.push(deptSumArr[i]);
                    } else {
                        temp.push(newResult);
                        flag = false;
                    }
                }
                if (flag) {
                    temp.push(newResult);
                }
            } else {
                temp.push(newResult);
            }
        }
        inputNodes.eq(4).val(result);
    } else {//结果为改变不需要重新生成小结
        temp = deptSumArr
    }
    return temp;
}

//结果类型为数值的--小结生成
function autoSuggestNum(obj, deptSumArr) {
    var temp = [];
    var id = obj.id;//结果文本框的id
    var result = obj.value;
    var tdNodeObj2 = $("#" + id).parent();//结果文本框的父节点
    var inputNodes = tdNodeObj2.children();//所有的input
    var result2 = inputNodes.eq(5).val();//对照用---看是否改过结果
    if (result2 != result) {//对结果进行判断--如果相等，则为未改过
        var indName = tdNodeObj2.prev().text();//获取指标名称
        if (checknum(result)) {//先判断是否是数字
            var maxval = inputNodes.eq(1).val();//上限
            var minval = inputNodes.eq(2).val();//下限
            var resultUnit = tdNodeObj2.next().text();//单位
            if (maxval == '0' && maxval == minval) {//没有设置上下限
                for (var i = 0; i < deptSumArr.length; i++) {//如果存在该指标的小结，则进行删除
                    if (deptSumArr[i].indexOf(indName) < 0) {//指标不存
                        temp.push(deptSumArr[i]);
                    }
                }
            } else {
                $("#" + obj.id).parent().parent().contents().removeClass("font67");
                $("#" + obj.id).parent().parent().contents().removeClass("font66");
                $("#" + obj.id).removeClass("font66");//正常，取消变红效果
                $("#" + obj.id).removeClass("font67");//正常，取消变蓝效果
                var promp = "";
                var jyflag = "";//检验箭头标志
                var mmrang = "";//参考范围
                var unnormal = true;//检验项目正常否
                maxval = parseFloat(maxval);
                minval = parseFloat(minval);
                result = parseFloat(result);
                mmrang = minval + "~" + maxval;
                //alert("-----上下结果------"+maxval+";"+minval+";"+result);
                if (result > maxval) {
                    $("#" + obj.id).parent().parent().contents().addClass("font66");//超出最高上限，字体变红
                    $("#" + obj.id).addClass("font66");
                    promp = inputNodes.eq(3).val();
                    if (comclass == "检验") {
                        jyflag = "↑";
                    }
                    unnormal = false;
                } else if (result < minval) {
                    $("#" + obj.id).parent().parent().contents().addClass("font67");
                    $("#" + obj.id).addClass("font67");//低于最低下限，字体变蓝
                    promp = inputNodes.eq(4).val();
                    if (comclass == "检验") {
                        jyflag = "↓";
                    }
                    unnormal = false;

                }
                //alert("----promp值为："+promp);
                if (comclass == "检验") {
                    if (unnormal) {//结果正常--如果存在该指标的小结，则进行删除
                        for (var i = 0; i < deptSumArr.length; i++) {
                            if (deptSumArr[i].indexOf(indName) < 0) {//指标不存
                                temp.push(deptSumArr[i]);
                            }
                        }
                    } else {//结果不正常
                        //alert("--------结果不正常："+promp);
                        var newResult = indName + ":" + result + resultUnit + " " + jyflag + "(参考值:" + mmrang + ")";
                        if (deptSumArr.length > 0) {//小结不为空
                            var flag = true;
                            for (var i = 0; i < deptSumArr.length; i++) {
                                if (deptSumArr[i].indexOf(indName) < 0) {//指标不存
                                    temp.push(deptSumArr[i]);
                                } else {//存在进行替换
                                    temp.push(newResult);
                                    flag = false;
                                }
                            }
                            if (flag) {//原有数组中不存在
                                temp.push(newResult);
                            }
                        } else {
                            temp.push(newResult);
                        }
                    }

                } else {
                    if (promp == "") {//结果正常--如果存在该指标的小结，则进行删除
                        for (var i = 0; i < deptSumArr.length; i++) {
                            if (deptSumArr[i].indexOf(indName) < 0) {//指标不存
                                temp.push(deptSumArr[i]);
                            }
                        }
                    } else {//结果不正常
                        // alert("--------结果不正常："+promp);
                        var newResult = indName + ":" + result + resultUnit + "(" + promp + ")";
                        if (deptSumArr.length > 0) {//小结不为空
                            var flag = true;
                            for (var i = 0; i < deptSumArr.length; i++) {
                                if (deptSumArr[i].indexOf(indName) < 0) {//指标不存
                                    temp.push(deptSumArr[i]);
                                } else {//存在进行替换
                                    temp.push(newResult);
                                    flag = false;
                                }
                            }
                            if (flag) {//原有数组中不存在
                                temp.push(newResult);
                            }
                        } else {
                            temp.push(newResult);
                        }
                    }

                }

            }
            inputNodes.eq(5).val(result);
        } else {//不是数字---如果存在该指标的小结，则进行删除
            for (var i = 0; i < deptSumArr.length; i++) {
                if (deptSumArr[i].indexOf(indName) < 0) {//指标不存
                    temp.push(deptSumArr[i]);
                }
            }
        }
    } else {
        temp = deptSumArr;
    }
    return temp;
}
//回车事件
function enter(obj) {
    var id = obj.id;
    var name = obj.dname;
    if (event.keyCode == 13) {
        //$(obj).append("\r");
        insertAtCursor(obj, '\r');
    }
}
//回车事件
function enter1(obj) {
    var id = obj.id;
    var name = obj.dname;
    var rows = $('#' + id).attr('rows');
    if (event.keyCode == 13) {
        $('#' + id).attr('rows', rows + 1);
        insertAtCursor(obj, '\r');
    }
}

// 在光标处插入字符串
// myField 文本框对象
// 要插入的值
function insertAtCursor(myField, myValue) {
    // IE support
    if (document.selection) {
        myField.focus();
        sel = document.selection.createRange();
        sel.text = myValue;
        sel.select();
    }
    // MOZILLA/NETSCAPE support
    else if (myField.selectionStart || myField.selectionStart == '0') {
        var startPos = myField.selectionStart;
        var endPos = myField.selectionEnd;
        // save scrollTop before insert
        var restoreTop = myField.scrollTop;
        myField.value = myField.value.substring(0, startPos) + myValue
            + myField.value.substring(endPos, myField.value.length);
        if (restoreTop > 0) {
            // restore previous scrollTop
            myField.scrollTop = restoreTop;
        }
        myField.focus();
        myField.selectionStart = startPos + myValue.length;
        myField.selectionEnd = startPos + myValue.length;
    } else {
        myField.value += myValue;
        myField.focus();
    }
}

//小结的生成
function autosuggest(obj) {
    var temp = '';
    var s = '';
    var deptSum = $("#LogC" + $('#deptcode').val()).text();   // 获取小结的文本值
    //var name = obj.dtype;//数据类型
    var name = $('#' + obj.id).attr("dtype"); //类型
    var ind_info = $('#' + obj.id).val();
    var deptSumArr = strToArr(deptSum, ";");

    var tdNodeObj2 = $("#" + obj.id).parent();//结果文本框的父节点
    var indName = tdNodeObj2.prev().text();//获取指标名称

    if (name == "num") {//结果为数值型
        deptSumArr = autoSuggestNum(obj, deptSumArr);
    } else if (name == "str") {
        deptSumArr = autoSuggestStr(obj, deptSumArr);
        //这里进行分割
        var cc = 1;
        $("#titems tr").each(function (ind, ele) {
            var tdNodes = $(this).children();
            var tdNode1 = tdNodes.eq(0);
            var tdNode2 = tdNodes.eq(1);
            var tdNode3 = tdNodes.eq(2);
            var inpNodes = tdNode2.children();
            var indname = tdNode1.text(); //小项的名字
            var name1 = inpNodes.eq(0).attr("name");//结果类型
            var dtype = inpNodes.eq(0).attr("dtype"); //num 或者str
            var result = inpNodes.eq(0).val();//结果
            var unnormal = inpNodes.eq(2).val();  //正常或者异常标志
            if (dtype == 'str') {
                if (result != '' && unnormal != '' && unnormal != '正常' && unnormal != 'undefined') {
                    //有结果的话  加到temp变量里
                    var s = strToArrBy(result, ',', 'num');
                    s = cc + '、' + name1 + ":" + s + ';\r';
                    temp += s;
                    cc++;
                } else {
                    temp += '';
                }
            }
        });
    }
    if (name == "num") {//结果为数值型
        setDeptSum(deptSumArr);
        //$("#LogC"+$('#deptcode').val()).text(temp);
    } else if (name == "str") {
        $("#LogC" + $('#deptcode').val()).text(temp);
    }
    //var dname = obj.dname;//属性名
    var dname = obj.name;//属性名
    if ("身高" == dname && $("input[name='身高']").val() != "" && $("input[name='体重']").val() != "") {
        var h = $("input[name='身高']").val() / 100;//身高值
        var w = $("input[name='体重']").val();//体重值
        var bmi = w / (h * h);//bmi值
        bmi = bmi.toFixed(1);
        //$("#903").val(bmi);
        $("input[name='BMI']").val(bmi);
    }
    if ("体重" == dname && $("input[name='身高']").val() != "" && $("input[name='体重']").val() != "") {
        var h = $("input[name='身高']").val() / 100;//身高值
        var w = $("input[name='体重']").val();//体重值
        var bmi = w / (h * h);//bmi值
        bmi = bmi.toFixed(1);
        $("input[name='BMI']").val(bmi);
    }
}

//重新生成小结
function afreshSuggest() {
    return;
    var deptSumStr = "";
    var temp = "";
    if ("检验" == comclass) {
//		$("#titems tr").each(function(){
//			var tdNodes = $(this).children();
//			var tdNode1 = tdNodes.eq(0);
//			var unnormal = tdNode1.attr("unnormal");
//			if("异常"==unnormal){
//				deptSumStr += tdNode1.attr("exceptionResult")+";";
//			}
//		});
    } else {
        var cc = 1;
        $("#titems tr").each(function (ind, ele) {
            var tdNodes = $(this).children();
            var tdNode1 = tdNodes.eq(0);
            var tdNode2 = tdNodes.eq(1);
            var tdNode3 = tdNodes.eq(2);
            var inpNodes = tdNode2.children();
            var indname = tdNode1.text(); //小项的名字
            var name1 = inpNodes.eq(0).attr("name");//结果类型
            var dtype = inpNodes.eq(0).attr("dtype"); //num 或者str
            var result = inpNodes.eq(0).val();//结果
            var unnormal = inpNodes.eq(2).val();  //正常或者异常标志
            var indid = inpNodes.eq(0).attr("id");
            if (dtype == 'str') {
                //显示出小结里面的 诊断.
                for (var i = 0; i < ind_diagno.length; i++) {
                    if (ind_diagno[i].indid == indid) {
                        result = ind_diagno[i].sugestname;
                    }
                }
                if (result != '' && unnormal != '' && unnormal != '正常' && unnormal != 'undefined') {
                    temp += cc + '.' + result + ';\r';
                    cc++;
                }
//				if(result!='' && unnormal!='' && unnormal!='正常' && unnormal!='undefined'){
//					//有结果的话  加到temp变量里
//					var s = strToArrBy(result,',','num');
//					s = cc+'.'+'' +""+ s +';\r';
//					temp +=s;
//					cc++;
//				}else{
//					temp +='';
//				}
            } else if (dtype == 'num') {//数值
                //alert(result);
                if (checknum(result)) {//先判断是否是数字
                    var maxval = inpNodes.eq(1).val();//上限
                    var minval = inpNodes.eq(2).val();//下限
                    var resultUnit = tdNode3.text();//
                    if (maxval == '0' && maxval == minval) {//没有设置上下限

                    } else {
                        var promp = "";
                        maxval = parseFloat(maxval);
                        minval = parseFloat(minval);
                        result = parseFloat(result);
                        if (result > maxval) {
                            promp = inpNodes.eq(3).val();
                        } else if (result < minval) {
                            promp = inpNodes.eq(4).val();
                        }
                        //alert(promp)
                        if (promp == "") {//结果正常--如果存在该指标的小结，则进行删除

                        } else {//结果不正常
                            temp += cc + '.' + indname + ":" + result + resultUnit + "(" + promp + ");" + '\r';
                            cc++;
                        }
                    }
                }
            }
        });
    }
    $("#LogC" + $('#deptcode').val()).text(temp);
}

//字符串切割转换为数组
function strToArr(str, reg) {
    var arr = [];
    if (str != "") {
        var temp = str.split(reg);
        for (var i = 0; i < temp.length; i++) {
            if (i != (temp.length - 1)) {
                arr[i] = temp[i];
            }
        }
    }
    return arr;
}

//将获取的科室小结返回页面
function setDeptSum(deptSumArr) {
    var temp = "";
    for (var i = 0; i < deptSumArr.length; i++) {
        //alert(deptSumArr[i])
        temp += deptSumArr[i] + ";";
    }
    $("#LogC" + $('#deptcode').val()).text(temp);
}


//数字的判断
function checknum(num) {
    var re = /^-?[1-9]+(\.\d+)?$|^-?0(\.\d+)?$|^-?[1-9]+[0-9]*(\.\d+)?$/;
    if (!re.test(num)) {
        return false;
    } else {
        return true;
    }
}

//自动获取小结--已废除
function autosuggest2(obj) {
    var detailid = obj.id;
    var result = obj.value;
    $.ajax({
        url: "pexam/autosuggest.htm",
        type: "post",
        data: "pexamid=" + pexamid + "&examid=" + examid + "&method=autoSuggest&detailid=" + detailid + "&time=" + (new Date()).valueOf() + "&result=" + result,
        success: function (reply) {
            if (reply == "fail") {

            } else {
                var temp = $("#LogC" + $('#deptcode').val()).text();
                $("#LogC" + $('#deptcode').val()).text(temp + "" + reply);
            }
        }
    });
}

function replayTag() {

}

//jisheng
/*
 function clickdj(){
 $("#jsdj").css("color","red");
 $("#jsjy").css("color","#a5c3cc");
 $("#jsjc").css("color","#a5c3cc");
 $("#jszd").css("color","#a5c3cc");
 $("#jssf").css("color","#a5c3cc");

 $("#idj").css("display","block");
 $("#ijkjy").css("display","none");
 $("#ijkjc").css("display","none");
 $("#izxzd").css("display","none");
 $("#izzsf").css("display","none");

 var autoheight=($(window).height()-270);
 $("#idj").css("height",autoheight);
 $("#ifidj").css("height",autoheight-5);
 }

 function clickjy(){
 $("#jsdj").css("color","#a5c3cc");
 $("#jsjy").css("color","red");
 $("#jsjc").css("color","#a5c3cc");
 $("#jszd").css("color","#a5c3cc");
 $("#jssf").css("color","#a5c3cc");

 $("#idj").css("display","none");
 $("#ijkjy").css("display","block");
 $("#ijkjc").css("display","none");
 $("#izxzd").css("display","none");
 $("#izzsf").css("display","none");

 var autoheight=($(window).height()-270);
 $("#ijkjy").css("height",autoheight);
 }



 function clickjc() {
 $("#jsdj").css("color","a5c3cc");
 $("#jsjy").css("color","#a5c3cc");
 $("#jsjc").css("color","red");
 $("#jszd").css("color","#a5c3cc");
 $("#jssf").css("color","#a5c3cc");


 $("#idj").css("display","none");
 $("#ijkjy").css("display","none");
 $("#ijkjc").css("display","block");
 $("#izxzd").css("display","none");
 $("#izzsf").css("display","none");




 var autoheight=($(window).height()-270);
 $("#ijkjc").css("height",autoheight);
 $("#ijc").css("height",autoheight-5);

 }

 function clickzd() {
 $("#jsdj").css("color","a5c3cc");
 $("#jsjy").css("color","#a5c3cc");
 $("#jsjc").css("color","#a5c3cc");
 $("#jszd").css("color","red");
 $("#jssf").css("color","#a5c3cc");

 $("#idj").css("display","none");
 $("#ijkjy").css("display","none");
 $("#ijkjc").css("display","none");
 $("#izxzd").css("display","block");
 $("#izzsf").css("display","none");

 var autoheight=($(window).height()-270);
 $("#izxzd").css("height",autoheight);
 $("#izd").css("height",autoheight-5);
 }

 function clicksf() {
 $("#jsdj").css("color","#a5c3cc");
 $("#jsjy").css("color","#a5c3cc");
 $("#jsjc").css("color","#a5c3cc");
 $("#jszd").css("color","#a5c3cc");
 $("#jssf").css("color","red");

 $("#idj").css("display","none");
 $("#ijkjy").css("display","none");
 $("#ijkjc").css("display","none");
 $("#izxzd").css("display","none");
 $("#izzsf").css("display","block");

 var autoheight=($(window).height()-270);
 $("#izzsf").css("height",autoheight);
 $("#isf").css("height",autoheight-5);
 }
 */

function setinput(aaa, bbb) {
    var aa = '#' + bbb;
    $(aa).val(aaa);
}

function nextone() {
    if (window.confirm("是否确认下一位体检人开始体检？")) {
        var selectedIds = grid_doctorstation.getSelectedRowId();
        var rowsnum = grid_doctorstation.getRowsNum();
        if (selectedIds >= rowsnum) {
            alert("已经是最后一位");
            return;
        }
        grid_doctorstation.selectRowById(parseInt(selectedIds) + 1, false, true, true);//选中下一行
        setTimeout('getFirstLeaf(0)', 300);//延迟执行，等树渲染完成后执行
    }
}

//得到树下的第一个叶子节点
function getFirstLeaf(i) {
    var levelNodes = zTree.getNodesByParam("level", i);
    var treeNode = levelNodes[0];
    if (treeNode.isParent) {
        getFirstLeaf(++i);
    } else {
        zTree.selectNode(treeNode);
        zTreeOnClick("", "", treeNode);
    }
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

//flag=true 不可操作
function disabledButton(flag) {
    if (flag) {
        $("#saveResultButton").attr("disabled", "true");
        $("#confirmsaveResultButton").attr("disabled", "true");
        $("#afrSugBut").attr("disabled", "true");

        //$("#rollBackButton").attr("disabled","true");
    } else {
        $("#saveResultButton").removeAttr("disabled");
        $("#confirmsaveResultButton").removeAttr("disabled");
        $("#afrSugBut").removeAttr("disabled");
        $("#rollBackButton").attr("disabled", "true");
        $("#getLisButton").attr("disabled", "true");
    }
}

//已废除
function isNormal(obj) {
    var detailId = obj.detailid;//指标项id
    if (obj.checked) {//选中--有异常
        $.ajax({
            type: "post",
            url: "pexamNew/isNormal.htm",
            data: "pexamId=" + pexamid + "&examId=" + examid + "&detailId=" + detailId + "&time=" + (new Date()).valueOf() + "&isNormal=Y",
            error: function () {
                alert("ajax请求失败");
            },
            success: function (data) {

            }
        });
    } else {
        $.ajax({
            type: "post",
            url: "pexamNew/isNormal.htm",
            data: "pexamId=" + pexamid + "&examId=" + examid + "&detailId=" + detailId + "&time=" + (new Date()).valueOf() + "&isNormal=N",
            error: function () {
                alert("ajax请求失败");
            },
            success: function (data) {

            }
        });
    }
}

function showPatientList(size, time) {
    if (hiddenInterval != null) {
        window.clearInterval(hiddenInterval);
        hiddenInterval = null;
    }
    if (showInterval == null && parseInt($("#patientList").css("left")) != 10) {
        showInterval = window.setInterval(function () {
            $("#patientList").css("left", function (obj, value) {
                var nowLeft = parseInt(value) + size;
                if (nowLeft >= 10) {
                    nowLeft = 10;
                    window.clearInterval(showInterval);
                    showInterval = null;
                }
                return nowLeft;
            });
        }, time);
    }

}
function hiddenPatientList(size, time) {
    if (showInterval != null) {
        window.clearInterval(showInterval);
        showInterval = null;
    }
    if (hiddenInterval == null && parseInt($("#patientList").css("left")) != -225) {
        hiddenInterval = window.setInterval(function () {
            $("#patientList").css("left", function (obj, value) {
                var nowLeft = parseInt(value) - size;
                if (nowLeft <= -225) {
                    nowLeft = -225;
                    window.clearInterval(hiddenInterval);
                    hiddenInterval = null;
                }
                return nowLeft;
            });
        }, time);
    }
}
/**
 * 科室小结的聚焦 和点击事件
 * @param {} obj
 */
function loadCommonResults1(obj) {
    Result_indid = obj.id; //光标放在输出框的时候把id赋值给全局变量
    var dtype = $('#' + obj.id).attr('dtype');
    //----如果项目为文字类型  显示常见结果属性----
    if ((dtype) == 'str') {
        $("#addcommonresult").removeAttr("disabled");
    }
    dname = obj.name;
    dname = dname + "常见结果";
    $("#dname").text(dname);
    $("#comResult").css("display", "block");
    $("#calculator").css("display", "none");
    loadCommonResultsCount();
    //loadCommonGridResults();
}

/**
 * 小项 的聚焦 和点击事件
 * @param {} obj
 */
function loadCommonResults(obj) {
    $('#result_details_text').text('');
    Result_indid = obj.id; //光标放在输出框的时候把id赋值给全局变量
    if ($('#Result_indid').val() == '') {
        //alert(1)
    }
    var dtype = $('#' + obj.id).attr('dtype');
    var tsxm = $('#' + obj.id).attr('tsxm');  // 特殊项目
    //----如果项目为文字类型  显示常见结果属性----
    if ((dtype) == 'str') {
        $("#addcommonresult").removeAttr("disabled");
    }

    dname = obj.name;
    dname = dname + "常见结果";
    $("#dname").text(dname);
    $("#comResult").css("display", "block");
    $("#calculator").css("display", "none");
    loadCommonResultsCount();
    //loadCommonGridResults();
}

// 常见结果分页
function loadCommonResultsCount() {
    $("#choose").hide();
    var resultname = $("#resultname").val() == null ? "" : $("#resultname").val();//常见结果
    //alert(resultname);
    $.ajax({
        async: false,
        cache: false,
        type: "post",
        url: "pexamNew/getCommonResultsCount.htm",
        data: "indid=" + Result_indid + "&comparSearch=" + comparSearch + "&resultname=" + resultname,
        dataType: "json",
        error: function () {
            alert("服务器内部错误！");
        },
        success: function (data) {
            //if(data!=0){
            pageIndex1 = 0;
            var pageCount = data;
            createPagination1(pageCount);
            //}
        }
    });

}

function createPagination1(pageCount) {//创建分页标签
//alert("pageCount----"+pageCount);
    if (pageCount == 0) {
        pageCount = 0;
    }
    //分页，pageCount是总条目数，这是必选参数，其它参数都是可选
    $("#resultsPagination").pagination(pageCount, {
        callback: pageCallback1,
        prev_text: '上一页',       //上一页按钮里text
        next_text: '下一页',       //下一页按钮里text
        //items_per_page: pageSize1,  //显示条数
        num_display_entries: 2,    //连续分页主体部分分页条目数
        current_page: pageIndex1,   //当前页索引
        num_edge_entries: 1        //两侧首尾分页条目数
    });
}

//分页回调函数
function pageCallback1(index, jq) {//翻页回调
    pageIndex1 = index;
    loadCommonGridResults();
    return false;
}

//----加载指标对应结果----
function loadCommonGridResults() {
    var resultname = $("#resultname").val() == null ? "" : $("#resultname").val();//常见结果
    //alert(resultname);
    $.ajax({
        cache: false,
        type: "post",
        url: "pexamNew/loadCommonResults.htm",
        data: "curPage=" + pageIndex1 + "&pageSize=" + "11" + "&indid=" + Result_indid + "&comparSearch=" + comparSearch + "&resultname=" + resultname + "&type=" + 1,
        dataType: "json",
        error: function () {
            alert("ajax请求失败！");
        },
        success: function (json) {
            grid_Results.clearAll();
            for (var i = 0; i < json.length; i++) {
                grid_Results.addRow(i, [
                    i + 1,
                    json[i].classname == null ? "" : json[i].classname,
                    json[i].result == null ? "" : json[i].result,
                    json[i].unnormal == null ? "" : json[i].unnormal,
                    json[i].sugestid == null ? "" : json[i].sugestid
                ]);

            }
            var results = $("#" + Result_indid).val();
            var num = grid_Results.getRowsNum();
            var commonresults = "";
            //不用去设置勾选  --lsp
            if (results != "") {
                var result_strs = results.split(",");
                for (var i = 0; i < result_strs.length; i++) {
                    var oneOfResult = result_strs[i];
                    for (var j = 0; j < num; j++) {
                        commonresults = grid_Results.cells2(j, 1).getValue();
                        if (oneOfResult == commonresults) {
                            //grid_Results.cells2(j,0).setValue(1);
                            break;
                        }
                    }

                }
            }
        }
    });
    $("#resultname").val("");
    return false;
}

//原来的全选 的功能。
function checkAll() {
    var chk0 = "dhtmlxGrid/codebase/imgs/item_chk0.gif";
    var chk1 = "dhtmlxGrid/codebase/imgs/item_chk1.gif";
    if ($("#all_img").attr("src") == chk0) {
        $("#all_img").attr("src", chk1);
        for (var i = 0; i < grid_Results.getRowsNum(); i++) {
            grid_Results.cells2(i, 0).setValue(1);
        }
        doAddResults1();
    } else {
        $("#all_img").attr("src", chk0);
        for (var i = 0; i < grid_Results.getRowsNum(); i++) {
            grid_Results.cells2(i, 0).setValue(0);
        }
        doAddResults1();
    }
}

//单击 常用结果 的函数
function doAddResults(rId, cInd) {
    //单击一行获取的变量值
    var v_id = grid_Results.cells(rId, 0).getValue(); //序号
    var v_sugestname = grid_Results.cells(rId, 1).getValue(); //诊断名称
    var v_result = grid_Results.cells(rId, 2).getValue(); //常用结果内容
    var v_status = grid_Results.cells(rId, 3).getValue(); //状态
    var v_sugestid = grid_Results.cells(rId, 4).getValue(); //诊断id

    var tdNodeObj2 = $("#" + Result_indid).parent();
    var inputNodes = tdNodeObj2.children();//所有的input

    var result = v_result;
    var status = "";
    //结果文本框里含有 未检异常  就去掉
    if ($("#" + Result_indid).val().indexOf('未见异常') > -1) {
        $("#" + Result_indid).val($("#" + Result_indid).val().replace('未见异常', ''));
    }
    var sp = spDeptSum('LogC' + $('#deptcode').val()); //分割科室小结 得到最大的数字
    var ins = 1;
    if (sp.length > 0) {
        ins = parseInt(sp.charAt(sp.length - 1)) + 1;
    }
    var flag = false; //科室小结 还是小项的标志（false为小项）
    if (Result_indid.indexOf('LogC') != -1) {
        flag = true;
    }
    var v_deptsum = $('#LogC' + $('#deptcode').val()).val();  //科室小结的值
    if (flag) {
        if (v_sugestid != '') {
            if ($("#" + Result_indid).val().indexOf(v_sugestname) > -1) {
                alert('已经存在诊断：' + v_sugestname);
                $("#" + Result_indid).focus();
                return;
            }
            if (result != '') {//如果常见结果的内容不为空
                //生成序号   ---这里遇到了取最大序号的困难，采用了最笨的办法，到后台请求 去获取最大值。
                $.ajax({
                    async: false,
                    url: 'phyexam/getItemFisrtNum.htm',
                    type: 'post',
                    data: 'now=' + new Date() + '&input=' + encodeURI(encodeURI(v_deptsum)),
                    error: function () {
                        alert('获取数据失败');
                    },
                    success: function (data) {
                        result = data + '.' + v_sugestname + '\r';
                    }
                });
            }
            $("#" + Result_indid).append(result);
        } else {
            //$("#"+Result_indid).focus();
            return;
        }
    } else {
        if (inputNodes.eq(2).val() != 'undefined' && $("#" + Result_indid).val() != '' && inputNodes.eq(2).val() != v_status) {
            //alert('不能同时选择正常和异常，请重新选择！');
            //return ;
        } else {

        }
        if (v_sugestid != '') {
            ind_diagno.push({indid: Result_indid, sugestname: v_sugestname});  //放到数组里
        }
        inputNodes.eq(2).val(v_status); //设置小项的状态
        //加上序号
        sp = spDeptSum(Result_indid);
        ins = 1;
        if (sp.length > 0) {
            ins = parseInt(sp.charAt(sp.length - 1)) + 1;
        }
        if ($("#" + Result_indid).val() == '') {
            $("#" + Result_indid).val($("#" + Result_indid).val() + '' + result);
        } else {
            $("#" + Result_indid).val($("#" + Result_indid).val() + ',' + result);
        }
        //把诊断放到科室小结里面
        if (v_sugestid != '') {
            if ($('#LogC' + $('#deptcode').val()).val().indexOf(v_sugestname) > -1) {
                alert('已经存在诊断：' + v_sugestname);
                //$("#"+Result_indid).focus();
                return;
            }
            ins = 1;
            sp = spDeptSum('LogC' + $('#deptcode').val());
            if (sp.length > 0) {
                ins = parseInt(sp.charAt(sp.length - 1)) + 1;  //获取到序号
            }
            var deptsumresult = $('#LogC' + $('#deptcode').val()).val(); //原来的科室小结的内容
            $.ajax({
                async: false,
                url: 'phyexam/getItemFisrtNum.htm',
                type: 'post',
                data: 'now=' + new Date() + '&input=' + encodeURI(encodeURI(v_deptsum)),
                error: function () {
                    alert('获取数据失败');
                },
                success: function (data) {
                    ins = data;
                }
            });
            $('#LogC' + $('#deptcode').val()).val(deptsumresult + ins + "." + v_sugestname + "\r");
        }
    }

    //$("#"+Result_indid).focus();
}
//原来的勾选常用结果的函数  --现在不用了。
function doAddResults1() {
    var sugestid = grid_Results.cells(rId, 3).getValue();
    var sp = spDeptSum();
    var ins = 1;
    if (sp.length > 0) {
        ins = parseInt(sp.charAt(sp.length - 1)) + 1;
    }
    var flag = false;
    if (Result_indid.indexOf('LogC') != -1) {  //判断点击的是否是科室小结
        flag = true;
    }
    var tdNodeObj2 = $("#" + Result_indid).parent();
    var inputNodes = tdNodeObj2.children();//所有的input
    var checkIds = grid_Results.getCheckedRows(0);
    var result = "";
    var status = "";

    if (checkIds != "") {
        var strs = new Array(); //定义一数组
        strs = checkIds.split(",");
        for (var i = 0; i < strs.length; i++) {
            result += grid_Results.cells(strs[i], 1).getValue() + ',';
            status += grid_Results.cells(strs[i], 2).getValue() + ',';
        }
        result = result.substring(0, result.length - 1);
        status = status.substring(0, status.length - 1);
        var flag1 = status.indexOf('正常');
        var flag2 = status.indexOf('异常');
        if (status.split(',').length >= 2 && flag1 > -1 && flag2 > -1) {
            if ($("#deptcode").val() != "12334") {
                alert('不能同时选择正常和异常，请重新选择！');
                return false;
            }
        }
        status = (status.split(','))[0];
    }
    ind_diagno.push({indid: Result_indid, sugestid: sugestid});  //放到数组里
    if (flag) {
        if (result != '') {
            result = ins + '.' + result + ';\r';
        }
        $("#" + Result_indid).append(result);
    } else {
        if ($("#" + Result_indid).val() != '') {
            var ind_v = $("#" + Result_indid).val();
            var last_str = ind_v.charAt(ind_v.length - 1);
            if (last_str != ';') { //如果最后一个字符不是  分号
                $("#" + Result_indid).val($("#" + Result_indid).val() + '' + result);
            } else {
                $("#" + Result_indid).val($("#" + Result_indid).val() + '' + result);
            }
        } else {
            $("#" + Result_indid).val($("#" + Result_indid).val() + '' + result);
        }

    }
    inputNodes.eq(2).val(status);
    if (flag) {
        $("#" + Result_indid).focus();
    } else {
        $("#" + Result_indid).focus();
    }
}
//分割科室小结 得到最大的数字

function spDeptSum(id) {
    var text = $("#" + id).val();
    var value = text.replace(/[^0-9]/ig, "");
    return (value);
}

//获取常见结果
function commonResult(obj) {
    var indid = obj.id;
    var tdNodeObj2 = $("#" + indid).parent();//文本框的父节点
    var indname = tdNodeObj2.prev().text();
    var url = path + "/pexamNew/commonResult.htm?indid=" + indid + "&indname=" + encodeURI(encodeURI(indname));
    var resultObj = window.showModalDialog(url, "", "dialogHeight: 320px; dialogWidth: 610px; dialogHide: yes; help: no; resizable: yes; status: no; scroll: no");
    if (resultObj) {//修改结果，弹出层设值列，是否正常
        var inputNodes = tdNodeObj2.children();//所有的input
        obj.value = resultObj.result;//回置结果
        inputNodes.eq(2).val(resultObj.status);//回置结果是否正常
        inputNodes.eq(3).val(resultObj.result);//
    }
}


function loadWrite(obj) {
    $("#comResult").css("display", "none");
    $("#calculator").css("display", "block");
    $("#addcommonresult").attr("disabled", "true");
    Result_indid = obj.id;
    if (Result_indid == "13073") {  //对小项 心率的特殊处理 。。
        var index_cmf = $("#" + Result_indid).val().indexOf('次/分')
        if (index_cmf == -1) {
            var ind_v = $("#" + Result_indid).val();
            ind_v = ind_v.replace(/次/g, '');
            ind_v = ind_v.replace(/\//g, '');
            ind_v = ind_v.replace(/分/g, '');
            $("#" + Result_indid).val(ind_v + '次/分');
        }
    }
    //dname=obj.dname;
    dname = obj.name;
    dname = "编辑" + dname;
    $("#dname").text(dname);
    $("#calculatordisplay").val($("#" + Result_indid).val());
}

function add(obj) {
    var old_value = $("#" + Result_indid).val();
    var id = obj.id;
    var add_value = "";
    for (var i = 0; i < 10; i++) {
        if (id == ("value" + i)) {
            add_value = i;
        }
    }
    if (id == "point") {
        add_value = ".";
    } else if (id == "deleteone") {
        old_value = old_value.substr(0, old_value.length - 1);
    } else if (id == "deleteall") {
        old_value = "";
    }
    $("#" + Result_indid).val(old_value + add_value);
    $("#" + Result_indid).focus();
}

function addcommonresult() {
    //openWin($("#"+Result_indid).attr("dname")+'常见结果添加','632','395','pexamNew/itemsCombo.htm?indid='+Result_indid);//632,395
    openWin($('#dname').text() + '添加', '632', '395', 'pexamNew/itemsCombo.htm?indid=' + Result_indid);//632,395
}

//----------刷新树-------------
function refleshTree() {
    var selectedNode = zTree.getSelectedNode();
    var stamp = new Date().getMilliseconds();
    //pexamid = grid_doctorstation.cells(rowId,4).getValue();
    //examid = grid_doctorstation.cells(rowId,5).getValue();//如果是“个人体检”则为“0000”
    //alert("999999:"+examid);
    //加载体检项目树
    $.ajax({
        async: false,
        cache: false,
        type: 'get',
        dataType: "json",
        url: "pexamNew2/createTreeNew.htm?method=doctorStation&pexamid=" + pexamid + "&examid=" + examid + "&lisType=" + lisType,
        error: function () {
            alert('fail');
        },
        success: function (data) {
            if (data == 'fail') {
                alert("加载体检项目失败！");
            } else {

                treeNodes = data;
                zTree = $("#menuTree").zTree(setting, treeNodes);
                //alert(selectedNode);
                zTree.selectNode(selectedNode);
                zTreeOnClick("", "", selectedNode);
            }
        }
    });
}
//-------------自动获取检验结果的lis数据回滚操作------------------
function rollbackitemdetils() {
    /*
     if(CheckDeptsum()){//总检是否保存过
     alert("总检医生已保存,已无法修改");
     return ;
     }
     */
    // alert(itemuuid);
    if (window.confirm("该项所有检验结果将清空,你确定要回滚吗?")) {
        //--------回滚检验信息------------
        $.ajax({
            async: false,
            cache: false,
            type: 'get',
            url: "pexamNew/RollbackItemdetils.htm?pexamid=" + pexamid + "&itemuuid=" + itemuuid,
            error: function () {
                alert('fail');
            },
            success: function (data) {
                //刷新树
                refleshTree();
            }
        });
    }


}
//-----验证某人总检是否保存过------------
function CheckDeptsum() {
    var flag = true;
    $.ajax({
        async: false,
        cache: false,
        type: 'get',
        url: "pexamNew/CheckDeptSum.htm?pexamid=" + pexamid,
        error: function () {
            alert('fail333');
        },
        success: function (data) {
            if (data == 'Y') {
                flag = true;
            } else {
                flag = false;
            }
        }
    });

    return flag;

}


function getAllLisData() {
    openWin('批量获取检验数据', '450', '310', 'pexamExtends/importLisData.htm?method=add');
}

//-----加载4列表头------
function tableColToFour() {
    var isDishDept = $("#isDishDept").val();//是否区分科室
    var doctorName = $("#doctorName").val();
    var htmlTable2 = "";
    if (isDishDept == 'Y') {
        //区分科室
        htmlTable2 += '<td align="right" width="100">医生：' + doctorName + '</td> ';
    } else {
        htmlTable2 += '	<td align="right" width="45">医生：</td> '
            + '	    <td width="90"> '
            + '	    <div id="comboDoctorName" style="width:85px;float:left;"  ></div> '
            + '		</td> ';
    }

    var htmlTable = ' <table id="indtable"  border="0" cellspacing="1" cellpadding="0" bgcolor="#93afba" class="mar3" width="99%"> '
        + '	<tr bgcolor="#e4edf9" align="center">  '
        + '		<td width="80" height="30" class="tj_ysz2">分类</td> '
        + '		<td width="75" class="tj_ysz2" id="ep4">组合项目</td> '
        + '		<td width="75" class="tj_ysz2" id="ep1">体检项目</td> '
        + '		<td  class="tj_ysz2" id="ep2">体检结果</td> '
        + '		<td width="75" class="tj_ysz2" 	id="ep3">单位</td> '
        + '	</tr> '
        + '	<tr align="center"> '
        + '		<td bgcolor="#f6faff" width="80px"><strong id="examtypep"> </strong></td> '
        + '		<td colspan="4" bgcolor="#ffffff"> '
        + '		  <div  style="OVERFLOW-y:auto;OVERFLOW-x:hidden;width:100% " id="dtitems"  > '
        + '		    <table width="100%" border="0" cellspacing="0" cellpadding="3" id="titems" > '
        + '							           '
        + '		    </table> '
        + '		  </div> '
        + '		</td> '
        + '	</tr> '
        + '	<tr align="center"> '
        + '		<td rowspan="2" bgcolor="#f6faff"><strong>科室小结</strong></td> '
        + '		<td colspan="4" bgcolor="#ffffff" height="80"> '
        + '		  <div> '
        + '			<textarea id="LogC' + $('#deptcode').val() + '" dtype="str"  rows="6"  onfocus="loadCommonResults(this)" onclick="loadCommonResults(this)"  style= "overflow-x:auto;overflow-y:auto" onkeydown= "enter(this);"> </textarea> '
        + '		  </div> '
        + '	    </td> '
        + '	</tr> '
        + '	<tr align="right"> '
        + '		<td colspan="4" bgcolor="#f6faff" height="28" align="right"> '
        + '			<table width="100%" height="48" border="0" cellspacing="0" cellpadding="0"> '
        + '				<tr> '
        + '				   <td align="center" width="90"><input id="saveResultButton" type="button" value="保存" onclick="saveitemdetils()" disabled="true" /></td> '
        + '				   <td align="center" width="90"><input id="errorMsgButton" type="button" value="匹配日志" onclick="getErrorMsg()" disabled="true" /></td> '
        + '				   <td align="center" width="90"><input id="rollBackButton" type="button" value="回滚" onclick="rollbackitemdetils()" disabled="true" /></td> '
        + '				   <td align="left" id="addcommonresulttd"> <input id="addcommonresult" type="button" style="margin-right:10px;" value="新增常见结果" onclick="addcommonresult()" disabled="true"/></td> ';

    var htmlTable3 = '				</tr> '
        + '			</table> '
        + '		</td> '
        + '	</tr> '
        + '</table> ';
    var htmlTable4 = htmlTable + htmlTable2 + htmlTable3;
    $("#indtable").remove();
    $("#inddiv").append(htmlTable4);
    toSelfWH();//宽高自适应
    if (isDishDept == 'Y') {
        //区分科室
    } else {
        reBulidCombo_doctorName();//重置医生combo
    }
}

//-----加载3列常规表头------
function tableColToThree() {
    var isDishDept = $("#isDishDept").val();//是否区分科室
    var doctorName = $("#doctorName").val();
    var htmlTable2 = "";
    if (isDishDept == 'Y') {
        //区分科室
        htmlTable2 += '<td align="right" width="100">医生：' + doctorName + '</td> ';
    } else {
        htmlTable2 += '	<td align="right" width="45">医生：</td> '
            + '	    <td width="90"> '
            + '	    <div id="comboDoctorName" style="width:85px;float:left"  ></div> '
            + '		</td> ';
    }
    var htmlTable = ' <table id="indtable"  border="0" cellspacing="1" cellpadding="0" bgcolor="#93afba" class="mar3" width="99%"> '
        + '	<tr bgcolor="#e4edf9" align="center">  '
        + '		<td width="80" height="30" class="tj_ysz2">分类</td> '
        + '		<td width="90" class="tj_ysz2" id="ep1">体检项目</td> '
        + '		<td  class="tj_ysz2" id="ep2">体检结果</td> '
        + '		<td width="50" class="tj_ysz2" 	id="ep3">单位</td> '
        + '	</tr> '
        + '	<tr align="center"> '
        + '		<td bgcolor="#f6faff" width="80px"><strong id="examtypep"> </strong></td> '
        + '		<td colspan="3" bgcolor="#ffffff" > '
        + '		<div  style="OVERFLOW-y:auto;OVERFLOW-x:hidden;" id="dtitems" class="autohe"> '
        + '		<table width="100%"  border="0" cellspacing="0" cellpadding="3" id="titems"> '
        + '							           '
        + '		</table> '
        + '		</div> '
        + '		</td> '
        + '	</tr> '
        + '	<tr align="center"> '
        + '		<td rowspan="2" bgcolor="#f6faff"><strong>科室小结</strong></td> '
        + '		<td colspan="3" bgcolor="#ffffff" height="80"> '
        + '		<div> '
        + '			<textarea   onkeydown= "enter(this);" id="LogC' + $('#deptcode').val() + '"  dtype="str" rows="6"  onfocus="loadCommonResults(this)" onclick="loadCommonResults(this)"  style= "overflow-x:auto;overflow-y:auto;BORDER-BOTTOM: 0px solid; BORDER-LEFT: 0px solid; BORDER-RIGHT: 0px solid; BORDER-TOP: 0px solid;"  > </textarea> '
        + '		</div> '
        + '	    </td> '
        + '	</tr> '
        + '	<tr align="right"> '
        + '		<td colspan="3" bgcolor="#f6faff" height="28" align="right"> '
        + '			<table width="100%" height="48" border="0" cellspacing="0" cellpadding="0"> '
        + '				<tr> '
        + '				   <td align="center" width="90"><input id="saveResultButton" type="button" value="保存" onclick="saveitemdetils()" disabled="true" /></td> '
        + '				   <td align="center" width="90"><input id="confirmsaveResultButton" type="button" value="确认" onclick="confirmsaveitemdetils()" disabled="true" /></td> '
//				+'				   <td align="center" width="90"><input id="errorMsgButton" type="button" value="匹配日志" onclick="getErrorMsg()" disabled="true" /></td> '
        + '				   <td align="center" width="90"><input id="rollBackButton111" type="button" value="清空小结" onclick="clearDeptSum()" /></td> '
//				+'				   <td align="center" width="90"><input id="rollBackButton" type="button" value="回滚" onclick="rollbackitemdetils()" disabled="true" /></td> '
        + '				   <td align="left" id="addcommonresulttd" width="90" > <input id="addcommonresult" type="button" style="margin-right:10px;" value="新增常见结果" onclick="addcommonresult()" disabled="true"/></td> '
        + '					<td align="left" ><input id="afrSugBut" type="button" value="自动生成小结" onclick="afreshSuggest()" disabled="true" onkeydown= "enter(this);"/></td>    '
        + '  			   <td align="left" id="zytjbsbtd"   > <input id="zytjbsb" type="button" style="margin-right:10px;" value="中医体质辨识表" onclick="openZytj()" disabled="true"/></td> '

    var htmlTable3 = '				</tr> '
        + '			</table> '
        + '		</td> '
        + '	</tr> '
        + '</table> ';
    var htmlTable4 = htmlTable + htmlTable2 + htmlTable3;

    $("#indtable").remove();
    $("#inddiv").append(htmlTable4);
    toSelfWH();//宽高自适应

    if (isDishDept == 'Y') {
        //区分科室
    } else {
        reBulidCombo_doctorName();//重置医生combo
    }

}
/**
 * 体检医生 完成 确认按钮
 */
function confirmsaveitemdetils() {
    $.ajax({
        url: "pexamNew2/confirmsaveitemdetils.htm",
        type: "post",
        async: false,
        data: "itemuuid=" + itemuuid + "&time=" + (new Date()).valueOf(),
        error: function () {
            alert("获取数据失败");
        },
        success: function (reply) {
            if (reply.indexOf('fail') > -1) {
                //showError("获取数据失败");
                alert(reply);
            } else {
                alert(reply);
                $('#search_value').val(pexamid);
                //topSearch();
                //更新体检项目树 （和病人grid）
                var selectNode = zTree.getSelectedNode();
                var name = selectNode.name;
                selectNode.name = name.replace("*", "");
                selectNode.icon = "img/yhtj/yhtj_wc.gif";
                zTree.updateNode(selectNode, true);
            }
        }
    });


}

//-----加载3列常规表头------   --重新绘制的 表头
function tableColToThree1() {
    var isDishDept = $("#isDishDept").val();//是否区分科室
    var doctorName = $("#doctorName").val();
    var htmlTable2 = "";
    if (isDishDept == 'Y') {
        //区分科室
        htmlTable2 += '<td align="right" width="100">医生：' + doctorName + '</td> ';
    } else {
        htmlTable2 += '	<td align="right" width="45">医生：</td> '
            + '	    <td width="	90"> '
            + '	    <div id="comboDoctorName" style="width:85px;float:left"  ></div> '
            + '		</td> ';
    }
    var htmlTable = ' <table id="indtable"  border="0" cellspacing="1" cellpadding="0" bgcolor="#93afba" class="mar3" width="99%"> '
        + '	<tr bgcolor="#e4edf9" align="center">  '
        + '		<td width="80" height="30" class="tj_ysz2">分类</td> '
        + '		<td width="*" class="tj_ysz2" id="ep1">体检结果</td> '
        //+'		<td  class="tj_ysz2" id="ep2">体检结果</td> '
        //+'		<td width="50" class="tj_ysz2" 	id="ep3">单位</td> '
        + '	</tr> '
        + '	<tr align="center"> '
        + '		<td bgcolor="#f6faff" width="80px"><strong id="examtypep"> </strong></td> '
        + '		<td colspan="1" bgcolor="#ffffff"> '
        + '		<div  style="OVERFLOW-y:auto;OVERFLOW-x:hidden;" id="dtitems" class="autohe"> '
        + '		<table width="100%" height="97.5%" border="0" cellspacing="0" cellpadding="3" id="titems"> '
        + '							           '
        + '		</table> '
        + '		</div> '
        + '		</td> '
        + '	</tr> '
        + '	<tr align="center"> '
        + '		<td rowspan="2" bgcolor="#f6faff"><strong>科室小结</strong></td> '
        + '		<td colspan="1" bgcolor="#ffffff" height="80"> '
        + '		<div> '
        + '			<textarea onkeydown= "enter(this);"  id="LogC' + $('#deptcode').val() + '" dtype="str" rows="6"  onfocus="loadCommonResults(this)" onclick="loadCommonResults(this)" style= "overflow-x:auto;overflow-y:auto;BORDER-BOTTOM: 0px solid; BORDER-LEFT: 0px solid; BORDER-RIGHT: 0px solid; BORDER-TOP: 0px solid;"  > </textarea> '
        + '		</div> '
        + '	    </td> '
        + '	</tr> '
        + '	<tr align="right"> '
        + '		<td colspan="2" bgcolor="#f6faff" height="28" align="right"> '
        + '			<table width="100%" height="48" border="0" cellspacing="0" cellpadding="0"> '
        + '				<tr> '
        + '				   <td align="center" width="90"><input id="saveResultButton" type="button" value="保存" onclick="saveitemdetils()" disabled="true" /></td> '
        + '				   <td align="center" width="90"><input id="confirmsaveResultButton" type="button" value="确认" onclick="confirmsaveitemdetils()" disabled="true" /></td> '
//				+'				   <td align="center" width="90"><input id="errorMsgButton" type="button" value="匹配日志" onclick="getErrorMsg()" disabled="true" /></td> '
        + '				   <td align="center" width="90"><input id="rollBackButton11222" type="button" value="清空小结" onclick="clearDeptSum()" /></td> '
//				+'				   <td align="center" width="90"><input id="rollBackButton" type="button" value="回滚" onclick="rollbackitemdetils()" disabled="true" /></td> '
        + '				   <td align="left" id="addcommonresulttd" width="90" > <input id="addcommonresult" type="button" style="margin-right:10px;" value="新增常见结果" onclick="addcommonresult()" disabled="true"/></td> '
        + '                 <td align="left" ><input id="afrSugBut" type="button" value="自动生成小结" onclick="afreshSuggest()" disabled="true" onkeydown= "enter(this);"/></td>     '
        + '  			   <td align="left" id="zytjbsbtd"   > <input id="zytjbsb" type="button" style="margin-right:10px;" value="中医体质辨识表" onclick="openZytj()" disabled="true"/></td> '

    var htmlTable3 = '				</tr> '
        + '			</table> '
        + '		</td> '
        + '	</tr> '
        + '</table> ';
    var htmlTable4 = htmlTable + htmlTable2 + htmlTable3;

    $("#indtable").remove();
    $("#inddiv").append(htmlTable4);
    toSelfWH();//宽高自适应

    if (isDishDept == 'Y') {
        //区分科室
    } else {
        reBulidCombo_doctorName();//重置医生combo
    }

}
function clearDeptSum() {
    var id = 'LogC' + $('#deptcode').val();
    $('#' + id).text('');
}
//----常规项目遍历显示-----
function normorIndShow(indid, indname, jsons) {
    var ck = "";
    var dtype = jsons.resulttype == "数值" ? "num" : "str";
    //alert(";"+jsons.result+";");
    //alert(jsons.result=="undefined" || jsons.result==undefined);
    // alert("jsons.defaultv:"+jsons.defaultv);
    //----常规项目遍历节点显示-----
    if (jsons.resulttype == "数值") {
        ck += "<input type='text' id='" + jsons.indid + "' tsxm='" + jsons.tsxm + "' onfocus='loadWrite(this)' onclick='loadWrite(this)' onkeydown = 'enter(this)'   inputv='" + jsons.indid + "' value='" + ((jsons.result == "" || jsons.result == undefined || jsons.result == null) ? jsons.defaultv : jsons.result) + "'  name='" + jsons.indname + "'  dtype='" + dtype + "'  style='border-left:1px;border-top:1px;border-right:1px;border-bottom:1px; border-bottom-color:Black;width:99%'/> ";
        ck += "<input type='hidden' value='" + jsons.maxval + "'>"//上限值
            + "<input type='hidden' value='" + jsons.minval + "'>"//下限值
            + "<input type='hidden' value='" + jsons.maxpromp + "'>"//上线提示
            + "<input type='hidden' value='" + jsons.minpromp + "'>"//下限提示
            + "<input type='hidden' value='" + ((jsons.result == "" || jsons.result == undefined || jsons.result == null) ? jsons.defaultv : jsons.result) + "'>";//是否改过结果值对照值
    } else if (jsons.resulttype == "文字") {
        //把 原来的input 改为 textarea
        //ck += "<input type='text' id='" + jsons.indid + "' tsxm='"+jsons.tsxm+"' onfocus='loadCommonResults(this)' onclick='loadCommonResults(this)'  onkeydown = 'enter(this)'  inputv='"+jsons.indid+"' value='"+((jsons.result==""||jsons.result==undefined||jsons.result==null)?jsons.defaultv:jsons.result)+"' name='"+jsons.indname+"'  dtype='"+dtype+"'  style='border-left:1px;border-top:1px;border-right:1px;border-bottom:1px; border-bottom-color:Black;width:99%'/> ";
        ck += "<textarea  id='" + jsons.indid + "'  onpropertychange='this.style.posHeight=this.scrollHeight'  onkeydown= 'enter1(this);' style='overflow-x:auto;overflow-y:auto;'  rows='1' tsxm='" + jsons.tsxm + "' onfocus='loadCommonResults(this)' onclick='loadCommonResults(this)'  onkeydown = 'enter(this)'  inputv='" + jsons.indid + "'  name='" + jsons.indname + "'  dtype='" + dtype + "'  style='border-left:1px;border-top:1px;border-right:1px;border-bottom:1px; border-bottom-color:Black;width:99%'>" + ((jsons.result == "" || jsons.result == undefined || jsons.result == null) ? jsons.defaultv : jsons.result) + "</textarea> ";
        ck += "<input type='hidden' value='" + jsons.defaultv + "'>"//默认值--后台设置的必须是正常的
            + "<input type='hidden'  value='" + (jsons.unnormal == "" ? "正常" : jsons.unnormal) + "'>"//是否正常标志--不是绝对的
            + "<input type='hidden'  value='" + jsons.result + "'>"//通过弹出层选择的结果保存列
            + "<input type='hidden' value='" + ((jsons.result == "" || jsons.result == undefined || jsons.result == null) ? jsons.defaultv : jsons.result) + "'>";//是否改过结果值对照值
    }
    if (jsons.resultunit == "") {
        $("#titems").append("<tr><td onclick='chooseItem(this);' style='cursor: pointer;border-right:1px solid #93afba;border-bottom:1px solid #93afba;' align='left' width='110' sn='" + jsons.sn + "'>" + jsons.indname + "</td><td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba; '  width='" + ($(window).width() - 660 - 53 - 94 - 16 - 10) + "' >" + ck + "</td><td style='border-bottom:1px solid #93afba;' width='62' bgcolor='#F6FAFF' resultunit='" + jsons.resultunit + "'>&nbsp;</td></tr>");
    } else {
        $("#titems").append("<tr><td onclick='chooseItem(this);' style='cursor: pointer;border-right:1px solid #93afba; border-bottom:1px solid #93afba;' align='left' width='110' sn='" + jsons.sn + "'>" + jsons.indname + "</td><td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba; '  width='" + ($(window).width() - 660 - 53 - 94 - 16 - 10) + "'>" + ck + "</td><td  style='border-bottom:1px solid #93afba; '  width='62' bgcolor='#F6FAFF' resultunit='" + jsons.resultunit + "'>" + jsons.resultunit + "</td></tr>");
    }

}
/**
 * 牙科 ---点击小项名字的时候
 * @param {} tdObj
 */
function chooseItem(tdObj) {
    var $v = $(tdObj); //jQuery对象
    //var trObj = $v.parent();  //tr对象
    //var td2 = trObj.children.eq(1);
    var tdNode2 = $v.next(); //td对象
    var inpNodes = tdNode2.children();
    var indid = inpNodes.eq(0).attr("id");//id
    var dtype = inpNodes.eq(0).attr("dtype"); //num 或者 str
    var name = inpNodes.eq(0).attr("name"); //小项的名字
    var tsxm = inpNodes.eq(0).attr("tsxm"); //特殊项目
    var result = inpNodes.eq(0).val();//结果
    if (tsxm == '') {//如果为空  则退出。
        return;
    }
    if (tsxm == '牙') {
        $("#faqbg").css({display: "block", height: $(document).height()});
        var yscroll = document.documentElement.scrollTop;
        $("#faqdiv").css("display", "block");
        $("#faqdiv h2").html('' + name);
        document.documentElement.scrollTop = 0;
        app.conInd = indid; //点击的小项的id
        //把刚才选的值显示成牙齿被勾选的样子
        if (result != '') {
            $("body >img").each(function () {
                $(this).addClass('hiddenimg');
            });
//			var tt = result.split(';');
//			var arr_result = tt[0].split(',');
//			for(var i=0;i<arr_result.length;i++){ //分割
//				var arrstr = arr_result[i];
//				var posstr = arrstr.replace(/\d+/g,'');   //返回区域字符串
//				var numstr = arrstr.replace(/[\u4e00-\u9fa5]+/g,''); //返回数字字符串
//				for(var j=0;j<numstr.length;j++){
//					var s = posstr+numstr.charAt(j);
//					$("body >img[info^='右上']").each(function(){
//						if(this.info==s){$(this).removeClass('hiddenimg');}
//					});
//					$("body >img[info^='左上']").each(function(){
//						if(this.info==s){$(this).removeClass('hiddenimg');}
//					});
//					$("body >img[info^='右下']").each(function(){
//						if(this.info==s){$(this).removeClass('hiddenimg');}
//					});
//					$("body >img[info^='左下']").each(function(){
//						if(this.info==s){$(this).removeClass('hiddenimg');}
//					});
//				}
//			}
        }
    } else {
        app.conInd = indid; //点击的小项的id
        //alert(name);
        if (buweiStr.indexOf(name) > -1) {
            openWin('部位选择', 800, 700, 'pexamNew/toBuWeiPage.htm?indid='
                + app.conInd + '&result=' + encodeURI(encodeURI(result)) + '&tsxm=' + encodeURI(encodeURI(tsxm)));
        }
    }
}
//确定所选的牙齿
function ycBtnClick(btnObj) {
    //取得所有显示出来的img
    var arr1 = [];
    var arr2 = [];
    var arr3 = [];
    var arr4 = [];
    var sum = '';
    var info = '';
    $("body > img[class!='hiddenimg']").each(function (i) {
        info = this.info;
        var temp = '';
        if (info.indexOf('右上') != -1) {
            temp = info.replace(/[\u4e00-\u9fa5]+/g, '');
            arr1.push(temp);
        } else if (info.indexOf('左上') != -1) {
            temp = info.replace(/[\u4e00-\u9fa5]+/g, '');
            arr2.push(temp);
        } else if (info.indexOf('右下') != -1) {
            temp = info.replace(/[\u4e00-\u9fa5]+/g, '');
            arr3.push(temp);
        } else if (info.indexOf('左下') != -1) {
            temp = info.replace(/[\u4e00-\u9fa5]+/g, '');
            arr4.push(temp);
        }
    });
    if (arr1.length > 0) {
        sum += '右上' + arr1.join('') + '';
    }
    if (arr2.length > 0) {
        sum += '左上' + arr2.join('') + '';
    }
    if (arr3.length > 0) {
        sum += '右下' + arr3.join('') + '';
    }
    if (arr4.length > 0) {
        sum += '左下' + arr4.join('') + '';
    }
    if (sum != '') {
        sum = sum.substring(0, sum.length - 1) + '';
    }
    $("#faqbg").css("display", "none");
    $("#faqdiv").css("display", "none");
    $('body > img').addClass('hiddenimg');
    if (sum != '') {
        $('#' + app.conInd).val($('#' + app.conInd).val() + '' + sum + '');
    }
}
//取消选择牙齿
function ycBtnClickCalcle() {
    $("#faqbg").css("display", "none");
    $("#faqdiv").css("display", "none");
    $('body > img').addClass('hiddenimg');
}
//点击图片 改变class
function changeIMGClass(obj) {
    var $v = $(obj);
    $v.addClass('hiddenimg');
}
//点击牙齿
function getyachi(areaObj) {
    $('#red' + $(areaObj).attr('id')).toggleClass('hiddenimg');
}
//得到鼠标的位置
function mouseMove(ev) {
    ev = ev || window.event;
    if (ev.pageX || ev.pageY) {
        return {
            x: ev.pageX,
            y: ev.pageY
        };
    }
    return {
        x: ev.clientX + document.body.scrollLeft - document.body.clientLeft,
        y: ev.clientY + document.body.scrollTop - document.body.clientTop
    };
}
//----常规项目遍历显示-----
function normorIndShow1(indid, indname, jsons) {
    var ck = "";
    var dtype = jsons.resulttype == "数值" ? "num" : "str";
    //alert(";"+jsons.result+";");
    //alert(jsons.result=="undefined" || jsons.result==undefined);
    // alert("jsons.defaultv:"+jsons.defaultv);
    //----常规项目遍历节点显示-----
    //单个项目的显示：把input改成textarea
    if (jsons.resulttype == "数值") {
        ck += "<textarea rows='30' valign='top' tsxm='" + jsons.tsxm + "'   name='ind_content'  id='" + jsons.indid + "'   onfocus='loadWrite(this)' onclick='loadWrite(this)' onkeydown = 'enter(this)'   inputv='" + jsons.indid + "'   style='overflow-x:auto;overflow-y:auto;border-left:1px;border-top:1px;border-right:0px;border-bottom:1px; border-bottom-color:Black;width:99%;height:98%;'  name='" + jsons.indname + "'  dtype='" + dtype + "' >" +
            " " + ((jsons.result == "" || jsons.result == undefined || jsons.result == null) ? jsons.defaultv : jsons.result) + "  </textarea>";
        //ck += "<input type='text' id='" + jsons.indid + "' onfocus='loadWrite(this)' onclick='loadWrite(this)' onkeydown = 'enter(this)'  onBlur='autosuggest(this)' inputv='"+jsons.indid+"' value='"+((jsons.result==""||jsons.result==undefined||jsons.result==null)?jsons.defaultv:jsons.result)+"'  name='"+jsons.indname+"'  dtype='"+dtype+"'  style='border-left:1px;border-top:1px;border-right:1px;border-bottom:1px; border-bottom-color:Black;width:99%'/> ";
        ck += "<input type='hidden' value='" + jsons.maxval + "'>"//上限值
            + "<input type='hidden' value='" + jsons.minval + "'>"//下限值
            + "<input type='hidden' value='" + jsons.maxpromp + "'>"//上线提示
            + "<input type='hidden' value='" + jsons.minpromp + "'>"//下限提示
            + "<input type='hidden' value='" + ((jsons.result == "" || jsons.result == undefined || jsons.result == null) ? jsons.defaultv : jsons.result) + "'>";//是否改过结果值对照值
    } else if (jsons.resulttype == "文字") {
        ck += "<textarea rows='30' valign='top' tsxm='" + jsons.tsxm + "'   name='ind_content' id='" + jsons.indid + "'   onfocus='loadCommonResults(this)' onclick='loadCommonResults(this)'  onkeydown = 'enter(this)'  inputv='" + jsons.indid + "' name='" + jsons.indname + "'  dtype='" + dtype + "'  style='overflow-x:auto;overflow-y:auto;border-left:1px;border-top:1px;border-right:0px;border-bottom:0px; border-bottom-color:Black;width:99%;height:99%;'  >" + ((jsons.result == "" || jsons.result == undefined || jsons.result == null) ? jsons.defaultv : jsons.result) + "</textarea>";
        //ck += "<input type='text' id='" + jsons.indid + "' onfocus='loadCommonResults(this)' onclick='loadCommonResults(this)'  onkeydown = 'enter(this)' onBlur='autosuggest(this)' inputv='"+jsons.indid+"' value='"+((jsons.result==""||jsons.result==undefined||jsons.result==null)?jsons.defaultv:jsons.result)+"' name='"+jsons.indname+"'  dtype='"+dtype+"'  style='border-left:1px;border-top:1px;border-right:1px;border-bottom:1px; border-bottom-color:Black;width:99%'/> ";
        ck += "<input type='hidden' value='" + jsons.defaultv + "'>"//默认值--后台设置的必须是正常的
            + "<input type='hidden'  value='" + (jsons.unnormal == "" ? "正常" : jsons.unnormal) + "'>"//是否正常标志--不是绝对的
            + "<input type='hidden'  value='" + jsons.result + "'>"//通过弹出层选择的结果保存列
            + "<input type='hidden' value='" + ((jsons.result == "" || jsons.result == undefined || jsons.result == null) ? jsons.defaultv : jsons.result) + "'>";//是否改过结果值对照值
    }
    if (jsons.resultunit == "") {
        //$("#titems").append("<tr><td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba; '  width='"+($(window).width()-660-53-94-16-10)+"' >"+ck+"</td></tr>");
        $("#titems").append("<tr height='100%'><td style='cursor: pointer;border-right:1px solid #93afba;border-bottom:1px solid #93afba;display: none;' align='left' width='110' sn='" + jsons.sn + "'>" + jsons.indname + "</td><td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba; '  width='100%' height='100%'>" + ck + "</td><td style='border-bottom:1px solid #93afba;display: none;' width='62' bgcolor='#F6FAFF' resultunit='" + jsons.resultunit + "'>&nbsp;</td></tr>");
    } else {
        //$("#titems").append("<tr><td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba; '  width='"+($(window).width()-660-53-94-16-10)+"' >"+ck+"</td></tr>");
        $("#titems").append("<tr height='100%'><td style='cursor: pointer;border-right:1px solid #93afba; border-bottom:1px solid #93afba;display: none;' align='left' width='110' sn='" + jsons.sn + "'>" + jsons.indname + "</td><td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba; '  width='100%' height='100%'>" + ck + "</td><td  style='border-bottom:1px solid #93afba;display: none; '  width='62' bgcolor='#F6FAFF' resultunit='" + jsons.resultunit + "'>" + jsons.resultunit + "</td></tr>");
    }
}

//----二级节点项目遍历显示-----
function normorSonIndsShow(indid, indname, indjsons) {
    //----二级项目遍历节点显示-----
    for (var j = 0; j < indjsons.length; j++) {
        var sonck = "";
        var dtype = indjsons[j].resulttype == "数值" ? "num" : "str";
        if (indjsons[j].resulttype == "数值") {
            sonck += "<input type='text' id='" + indjsons[j].indid + "' onfocus='loadWrite(this)' onclick='loadWrite(this)' onkeydown = 'enter(this)'   inputv='" + indjsons[j].indid + "' value='" + (indjsons[j].result == "" ? indjsons[j].defaultv : indjsons[j].result) + "'  name='" + indjsons[j].indname + "'  dtype='" + dtype + "'  style='border-left:1px;border-top:1px;border-right:1px;border-bottom:1px; border-bottom-color:Black;width:99%'/> ";
            sonck += "<input type='hidden' value='" + indjsons[j].maxval + "'>"//上限值
                + "<input type='hidden' value='" + indjsons[j].minval + "'>"//下限值
                + "<input type='hidden' value='" + indjsons[j].maxpromp + "'>"//上线提示
                + "<input type='hidden' value='" + indjsons[j].minpromp + "'>"//下限提示
                + "<input type='hidden' value='" + (indjsons[j].result == "" ? indjsons[j].defaultv : indjsons[j].result) + "'>";//是否改过结果值对照值
        } else {
            sonck += "<input type='text' id='" + indjsons[j].indid + "' onfocus='loadCommonResults(this)' onclick='loadCommonResults(this)'  onkeydown = 'enter(this)'  inputv='" + indjsons[j].indid + "' value='" + (indjsons[j].result == "" ? indjsons[j].defaultv : indjsons[j].result) + "'  name='" + indjsons[j].indname + "' dtype='" + dtype + "' style='border-left:1px;border-top:1px;border-right:1px;border-bottom:1px; border-bottom-color:Black;width:99%'/> ";
            sonck += "<input type='hidden' value='" + indjsons[j].defaultv + "'>"//默认值--后台设置的必须是正常的
                + "<input type='hidden'  value='" + (indjsons[j].unnormal == "" ? "正常" : indjsons[j].unnormal) + "'>"//是否正常标志--不是绝对的
                + "<input type='hidden'  value='" + indjsons[j].result + "'>"//通过弹出层选择的结果保存列
                + "<input type='hidden' value='" + (indjsons[j].result == "" ? indjsons[j].defaultv : indjsons[j].result) + "'>";//是否改过结果值对照值
        }
        if (indjsons[j].resultunit == "") {
            $("#" + indid + "").append("<tr><td style='border-right:1px solid #93afba;border-bottom:1px solid #93afba;' align='left' width='92' sn='" + indjsons[j].sn + "'>" + indjsons[j].indname + "</td><td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba; '  width='" + ($(window).width() - 660 - 50 - 180) + "' >" + sonck + "</td><td style='border-bottom:1px solid #93afba;' width='50' bgcolor='#F6FAFF' resultunit='" + indjsons[j].resultunit + "'>&nbsp;</td></tr>");
        } else {
            $("#" + indid + "").append("<tr><td style='border-right:1px solid #93afba; border-bottom:1px solid #93afba;' align='left' width='92' sn='" + indjsons[j].sn + "'>" + indjsons[j].indname + "</td><td align='left' style='border-right:1px solid #93afba;border-bottom:1px solid #93afba; '  width='" + ($(window).width() - 660 - 50 - 180) + "'>" + sonck + "</td><td  style='border-bottom:1px solid #93afba; '  width='50' bgcolor='#F6FAFF' resultunit='" + indjsons[j].resultunit + "'>" + indjsons[j].resultunit + "</td></tr>");
        }
    }

}


//----住院治疗情况常规项目遍历显示-----
function normorIndShowZy(jsons, jsonsson) {

    var ck = "";
    var dtype = jsons.resulttype == "数值" ? "num" : "str";

    if (jsons.resulttype == "数值") {
        ck += "<input type='hidden' id='" + jsons.indid + "' name='" + jsons.indname + "'  dtype='" + dtype + "'  /> ";
        ck += "<input type='hidden' value='" + jsons.maxval + "'>"//上限值
            + "<input type='hidden' value='" + jsons.minval + "'>"//下限值
            + "<input type='hidden' value='" + jsons.maxpromp + "'>"//上线提示
            + "<input type='hidden' value='" + jsons.minpromp + "'>"//下限提示
    } else if (jsons.resulttype == "文字") {
        ck += "<input type='hidden' id='" + jsons.indid + "' name='" + jsons.indname + "'  dtype='" + dtype + "' /> ";
        ck += "<input type='hidden' value='" + jsons.defaultv + "'>"//默认值--后台设置的必须是正常的
            + "<input type='hidden'  value='" + (jsons.unnormal == "" ? "正常" : jsons.unnormal) + "'>"//是否正常标志--不是绝对的
            + "<input type='hidden'  value='" + jsons.result + "'>"//通过弹出层选择的结果保存列
    }
    var indid = jsons.indid;//一级节点id
    var indname = jsons.indname;//一级节点名称
    //----住院治疗情况遍历节点显示-----
    var indHtml = "";
    if (indname == "住院史") {
        indHtml = " <tr class='zyclass'><td class='indtd_bt'>入/出院日期</td><td class='indtd_bt'>原 因</td><td class='indtd_bt'>医疗机构名称</td><td class='indtd_bt'>病案号</td></tr> ";
        if (jsonsson.length > 0) {
            for (var i = 0; i < jsonsson.length; i++) {
                indHtml += " <tr class='zyclass' ><td class='indtd'><table border='0' cellspacing='0' cellpadding='0' width='100%'><tr><td align='left' class='indtd'><input type='text' class='indinputhalf' value='" + (jsonsson[i].option01 == null ? '' : jsonsson[i].option01) + "' ></input>/<input type='text' class='indinputhalf' value='" + (jsonsson[i].option02 == null ? '' : jsonsson[i].option02) + "'></input></td></tr></table></td><td class='indtd' ><input type='text' class='indinput' value='" + (jsonsson[i].option03 == null ? '' : jsonsson[i].option03) + "' ></input></td><td class='indtd'><input type='text' class='indinput' value='" + (jsonsson[i].option04 == null ? '' : jsonsson[i].option04) + "'></input></td><td class='indtd'><input type='text' class='indinput' value='" + (jsonsson[i].option05 == null ? '' : jsonsson[i].option05) + "' ></input></td></tr> ";
            }
        } else {
            indHtml += " <tr class='zyclass' ><td class='indtd'><table border='0' cellspacing='0' cellpadding='0' width='100%'><tr><td align='left' class='indtd'><input type='text' class='indinputhalf' ></input>/<input type='text' class='indinputhalf' ></input></td></tr></table></td><td class='indtd' ><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput'  ></input></td></tr> "
                + " <tr class='zyclass' ><td class='indtd'><table border='0' cellspacing='0' cellpadding='0' width='100%'><tr><td align='left' class='indtd'><input type='text' class='indinputhalf' ></input>/<input type='text' class='indinputhalf' ></input></td></tr></table></td><td class='indtd' ><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput'  ></input></td></tr> ";
        }
    } else if (indname == "家庭病床史") {
        indHtml += " <tr class='zyclass' ><td class='indtd_bt'>建/撤床日期</td><td class='indtd_bt'>原 因</td><td class='indtd_bt'>医疗机构名称</td><td class='indtd_bt'>病案号</td></tr> ";
        if (jsonsson.length > 0) {
            for (var i = 0; i < jsonsson.length; i++) {
                indHtml += " <tr class='zyclass' ><td class='indtd'><table border='0' cellspacing='0' cellpadding='0' width='100%'><tr><td align='left' class='indtd'><input type='text' class='indinputhalf' value='" + (jsonsson[i].option01 == null ? '' : jsonsson[i].option01) + "' ></input>/<input type='text' class='indinputhalf' value='" + (jsonsson[i].option02 == null ? '' : jsonsson[i].option02) + "'></input></td></tr></table></td><td class='indtd' ><input type='text' class='indinput' value='" + (jsonsson[i].option03 == null ? '' : jsonsson[i].option03) + "' ></input></td><td class='indtd'><input type='text' class='indinput' value='" + (jsonsson[i].option04 == null ? '' : jsonsson[i].option04) + "'></input></td><td class='indtd'><input type='text' class='indinput' value='" + (jsonsson[i].option05 == null ? '' : jsonsson[i].option05) + "' ></input></td></tr> ";
            }
        } else {
            indHtml += " <tr class='zyclass' ><td class='indtd'><table border='0' cellspacing='0' cellpadding='0' width='100%'><tr><td align='left' class='indtd'><input type='text' class='indinputhalf' ></input>/<input type='text' class='indinputhalf' ></input></td></tr></table></td><td class='indtd' ><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput'  ></input></td></tr> "
                + " <tr class='zyclass'><td class='indtd'><table border='0' cellspacing='0' cellpadding='0' width='100%'><tr><td align='left' class='indtd'><input type='text' class='indinputhalf' ></input>/<input type='text' class='indinputhalf' ></input></td></tr></table></td><td class='indtd' ><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput'  ></input></td></tr> ";
        }
    }

    var indHeadRow = "<tr class='zdyclass' ><td class='indtd' align='left' width='110' sn='" + jsons.sn + "'>" + jsons.indname + ck + "</td><td align='left' class='indtd'  width='" + ($(window).width() - 660 - 63 - 110) + "' ><table border='0' cellspacing='0' cellpadding='0' width='100%' id=zy" + indid + ">" + indHtml + "</table></td><td class='indtd' width='63' bgcolor='#F6FAFF' >&nbsp;</td></tr>"

    $("#titems").append(indHeadRow);

    /*
     var indname=jsons.indname;//节点名称
     //----住院治疗情况项目遍历节点显示-----
     var indHtml="";
     if(indname=="住院史"){
     indHtml=" <tr><td class='indtd_bt'>入/出院日期</td><td class='indtd_bt'>原 因</td><td class='indtd_bt'>医疗机构名称</td><td class='indtd_bt'>病案号</td></tr> "
     +" <tr><td class='indtd'><table border='0' cellspacing='0' cellpadding='0' width='100%'><tr><td align='left' class='indtd'><input type='text' class='indinputhalf' ></input>/<input type='text' class='indinputhalf' ></input></td></tr></table></td><td class='indtd' ><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput'  ></input></td></tr> "
     +" <tr><td class='indtd'><table border='0' cellspacing='0' cellpadding='0' width='100%'><tr><td align='left' class='indtd'><input type='text' class='indinputhalf' ></input>/<input type='text' class='indinputhalf' ></input></td></tr></table></td><td class='indtd' ><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput'  ></input></td></tr> ";
     }else if(indname=="家庭病床史"){
     indHtml+=" <tr><td class='indtd_bt'>建/撤床日期</td><td class='indtd_bt'>原 因</td><td class='indtd_bt'>医疗机构名称</td><td class='indtd_bt'>病案号</td></tr> "
     +" <tr><td class='indtd'><table border='0' cellspacing='0' cellpadding='0' width='100%'><tr><td align='left' class='indtd'><input type='text' class='indinputhalf' ></input>/<input type='text' class='indinputhalf' ></input></td></tr></table></td><td class='indtd' ><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput'  ></input></td></tr> "
     +" <tr><td class='indtd'><table border='0' cellspacing='0' cellpadding='0' width='100%'><tr><td align='left' class='indtd'><input type='text' class='indinputhalf' ></input>/<input type='text' class='indinputhalf' ></input></td></tr></table></td><td class='indtd' ><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput'  ></input></td></tr> ";
     }

     var indHeadRow="<tr><td class='indtd' align='left' width='94' sn='"+ jsons.sn +"'>"+jsons.indname+"</td><td align='left' class='indtd'  width='"+($(window).width()-660-53-94)+"' ><table border='0' cellspacing='0' cellpadding='0' width='100%'>"+indHtml+"</table></td><td class='indtd' width='53' bgcolor='#F6FAFF' resultunit='"+jsons.resultunit+"'>&nbsp;</td></tr>"
     $("#titems").append(indHeadRow);
     */
}


//----主要用药情况项目遍历显示-----
function normorIndShowYy(jsons, jsonsson) {
    //---jsons 为父节点结果集 jsonsson 为子节点结果集------
    var ck = "";
    var dtype = jsons.resulttype == "数值" ? "num" : "str";

    if (jsons.resulttype == "数值") {
        ck += "<input type='hidden' id='" + jsons.indid + "' name='" + jsons.indname + "'  dtype='" + dtype + "'  /> ";
        ck += "<input type='hidden' value='" + jsons.maxval + "'>"//上限值
            + "<input type='hidden' value='" + jsons.minval + "'>"//下限值
            + "<input type='hidden' value='" + jsons.maxpromp + "'>"//上线提示
            + "<input type='hidden' value='" + jsons.minpromp + "'>"//下限提示
    } else if (jsons.resulttype == "文字") {
        ck += "<input type='hidden' id='" + jsons.indid + "' name='" + jsons.indname + "'  dtype='" + dtype + "' /> ";
        ck += "<input type='hidden' value='" + jsons.defaultv + "'>"//默认值--后台设置的必须是正常的
            + "<input type='hidden'  value='" + (jsons.unnormal == "" ? "正常" : jsons.unnormal) + "'>"//是否正常标志--不是绝对的
            + "<input type='hidden'  value='" + jsons.result + "'>"//通过弹出层选择的结果保存列
    }

    var indname = jsons.indname;//节点名称
    //----主要用药情况项目遍历节点显示-----
    var indHtml = "";
    indHtml = " <tr><td class='indtd_bt'>药物名称</td><td class='indtd_bt'>用法</td><td class='indtd_bt'>用量</td><td class='indtd_bt'>用药时间</td><td class='indtd_bt'>服药依从性</br>1规律 2间断 3不服药</td></tr> ";

    if (jsonsson.length > 0) {
        for (var i = 0; i < jsonsson.length; i++) {
            indHtml += " <tr><td class='indtd' ><input type='text' class='indinput' value='" + (jsonsson[i].option01 == null ? '' : jsonsson[i].option01) + "' ></input></td><td class='indtd'><input type='text' class='indinput' value='" + (jsonsson[i].option02 == null ? '' : jsonsson[i].option02) + "' ></input></td><td class='indtd'><input type='text' class='indinput'  value='" + (jsonsson[i].option03 == null ? '' : jsonsson[i].option03) + "'  ></input></td><td class='indtd'><input type='text' class='indinput' value='" + (jsonsson[i].option04 == null ? '' : jsonsson[i].option04) + "'   ></input></td><td class='indtd'><input type='text' class='indinput' value='" + (jsonsson[i].option05 == null ? '' : jsonsson[i].option05) + "'  ></input></td></tr> ";
        }
    } else {
        indHtml += " <tr><td class='indtd' ><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td></tr> "
            + " <tr><td class='indtd' ><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td></tr> "
            + " <tr><td class='indtd' ><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td></tr> "
            + " <tr><td class='indtd' ><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td></tr> "
            + " <tr><td class='indtd' ><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td></tr> ";
    }
    var indHeadRow = "<tr class='zdyclass' ><td class='indtd' align='left' width='110' sn='" + jsons.sn + "'>" + jsons.indname + ck + "</td><td align='left' class='indtd'  width='" + ($(window).width() - 660 - 63 - 110) + "' ><table border='0' cellspacing='0' cellpadding='0' width='100%' id=" + typeid + ">" + indHtml + "</table></td><td class='indtd' width='63' bgcolor='#F6FAFF' >&nbsp;</td></tr>"

    $("#titems").append(indHeadRow);
}


//----非免疫规划预防接种史项目遍历显示-----
function normorIndShowJzs(jsons, jsonsson) {
    //---jsons 为父节点结果集 jsonsson 为子节点结果集------
    var ck = "";
    var dtype = jsons.resulttype == "数值" ? "num" : "str";
    if (jsons.resulttype == "数值") {
        ck += "<input type='hidden' id='" + jsons.indid + "' name='" + jsons.indname + "'  dtype='" + dtype + "'  /> ";
        ck += "<input type='hidden' value='" + jsons.maxval + "'>"//上限值
            + "<input type='hidden' value='" + jsons.minval + "'>"//下限值
            + "<input type='hidden' value='" + jsons.maxpromp + "'>"//上线提示
            + "<input type='hidden' value='" + jsons.minpromp + "'>"//下限提示
    } else if (jsons.resulttype == "文字") {
        ck += "<input type='hidden' id='" + jsons.indid + "' name='" + jsons.indname + "'  dtype='" + dtype + "' /> ";
        ck += "<input type='hidden' value='" + jsons.defaultv + "'>"//默认值--后台设置的必须是正常的
            + "<input type='hidden'  value='" + (jsons.unnormal == "" ? "正常" : jsons.unnormal) + "'>"//是否正常标志--不是绝对的
            + "<input type='hidden'  value='" + jsons.result + "'>"//通过弹出层选择的结果保存列
    }


    var indname = jsons.indname;//节点名称
    //----非免疫规划预防接种史项目遍历节点显示-----
    var indHtml = "";
    indHtml = " <tr><td class='indtd_bt'>名称</td><td class='indtd_bt'>接种日期</td><td class='indtd_bt'>接种机构</td></tr> "
    if (jsonsson.length > 0) {
        for (var i = 0; i < jsonsson.length; i++) {
            indHtml += " <tr><td class='indtd' ><input type='text' class='indinput' value='" + (jsonsson[i].option01 == null ? '' : jsonsson[i].option01) + "' ></input></td><td class='indtd'><input type='text' class='indinput' value='" + (jsonsson[i].option02 == null ? '' : jsonsson[i].option02) + "'  ></input></td><td class='indtd'><input type='text' class='indinput' value='" + (jsonsson[i].option03 == null ? '' : jsonsson[i].option03) + "'  ></input></td></tr> ";
        }
    } else {
        indHtml += " <tr><td class='indtd' ><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td></tr> "
            + " <tr><td class='indtd' ><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td></tr> "
            + " <tr><td class='indtd' ><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td><td class='indtd'><input type='text' class='indinput' ></input></td></tr> ";
    }

    var indHeadRow = "<tr class='zdyclass'><td class='indtd' align='left' width='110' sn='" + jsons.sn + "'>" + jsons.indname + ck + "</td><td align='left' class='indtd'  width='" + ($(window).width() - 660 - 63 - 110) + "' ><table border='0' cellspacing='0' cellpadding='0' width='100%' id=" + typeid + ">" + indHtml + "</table></td><td class='indtd' width='63' bgcolor='#F6FAFF' >&nbsp;</td></tr>"
    $("#titems").append(indHeadRow);

}

//页面自适应
function toSelfWH() {
    var winheight = $(window).height();
    var topheight = $(".top").height();
    $(".tj_ysz1").css("height", winheight - topheight);
    $("#trc").css("height", winheight - topheight - 110);
    $("#grid_doctorstation").css("height", winheight - 420);
    $("#dtitems").css("height", winheight - 440);
    $("#grid_Results").css("height", winheight - 380);
    $("#comResult").css("height", winheight - 242);
    $("#calculator").css("height", winheight - 245);
    var winwidth = $(window).width();
    $("#right_all").css("width", winwidth - 195 - 30);
    //$("#dtitems").css("width", winwidth-660);
    //$("#ep2").css("width",winwidth-660-75-150);
    var aa = $("#dtitems").width() / 8.3;
    $("#LogC" + $('#deptcode').val()).attr("cols", Math.round(aa));
    //alert("科室小结宽度："+Math.round(aa));
    //$("#addcommonresulttd").css("width",$("#dtitems").width()-135-90-90-90-90);
    $("#zytjbsbtd").css("width", $("#dtitems").width() - 135 - 90 - 90 - 90 - 90);
    pageSize1 = (window.parent.document.documentElement.clientHeight - 227) / 27 - 2;
    pageSize1 = parseInt(pageSize1);
}

//重新初始化医生姓名combo
function reBulidCombo_doctorName() {
    combo_doctorName = new dhtmlXCombo("comboDoctorName", "alfa3", 85);
    if ($("#isDishDept").val() != "Y") {//是否分科室“Y”是
        var obj = document.createElement("img");
        obj.className = "dhx_combo_img";
        obj.src = "dhtmlxCombo/codebase/imgs/combo_select_dhx_blue.gif";
        combo_doctorName.DOMelem.appendChild(obj);
        combo_doctorName.attachEvent("onKeyPressed", function (keyCode) {
            $.get("operation.htm?method=getusers", function (json) {
                if (comboFilter(keyCode, json, "combo_doctorName", "hp", true, false, true) == 0) {
                }
            });
        });

        $.get("operation.htm?method=getusers", function (json) {
            comboFilter(8, json, "combo_doctorName", "hp", true, false, true);
        });
    }
}

//----打开中医体质辨识表-----
function openZytj() {
    var pexamid = $("#pexamid").val();
    openWin('中医体质辨识表', '700', '420', 'pexamNew2/openZytj.htm?pexamid=' + pexamid);//632,395
}

//----保存后中医体质辨识赋值-----
function zytjValue(json) {
    for (var i = 0; i < json.length; i++) {
        var key = json[i].key;
        var value = json[i].value;
        $("input[name='" + key + "']").val(value);
    }
}

//-----返回一级项目结果集合----
function OneItemsResult() {
    var resultArr = [];
    var indid = "";//指标id
    var result = "";//结果
    var isnormal = "";//是否异常
    var resultunit = "";//单位
    var flag = 2;//=2 代表多个tr
    var v_result = '';
    if ($("#titems tr").length == 1) {
        flag = 1;// =1 代表1个tr
    }
    $("#titems tr").each(function () {//文字：指标id，结果，是否正常；数值：指标id，结果
        var tdNodes = $(this).children();
        var tdNode2 = tdNodes.eq(1);
        var inpNodes = tdNode2.children();
        var dtype = inpNodes.eq(0).attr("dtype") == undefined ? "" : inpNodes.eq(0).attr("dtype");//结果类型
        var result = inpNodes.eq(0).val() == undefined ? "" : inpNodes.eq(0).val();//结果
        var indid = inpNodes.eq(0).attr("id");
        var indname = tdNodes.eq(0).text();
        var sn = tdNodes.eq(0).attr("sn") == undefined ? "" : tdNodes.eq(0).attr("sn");//序号
        var maxvalue = "";
        var minvalue = "";

        if (dtype == "num") {
            maxvalue = inpNodes.eq(1).val();
            minvalue = inpNodes.eq(2).val();
        }
        resultunit = tdNodes.eq(2).attr("resultunit") == undefined ? "" : tdNodes.eq(2).attr("resultunit");//单位

        if (dtype == 'str') {//文字：指标id，结果，是否正常；
            var defaultv = inpNodes.eq(1).val();//默认值
            var result3 = inpNodes.eq(3).val();//弹出层设置的对照
            var unnormal = inpNodes.eq(2).val();
            if (result == "" || result == defaultv) {//此处规则--结果为空、等于系统设置的默认值则为正常
                isnormal = "正常";
            } else if (result3 == result) {//相等这正常否看"正常否列"值
                isnormal = unnormal;
            } else {//异常
                isnormal = "异常";
            }
        } else if (dtype == 'num') {
            //---如果是手工输入的检验项目 与上下限比较后 转化为 ↑ ↓---
            if ("检验" == comclass) {
                //alert(result+";"+maxvalue+";"+minvalue);
                if (result != '' && result != null) {
                    if (result > maxvalue) {
                        isnormal = "↑";
                    } else if (result < minvalue) {
                        isnormal = "↓";
                    } else {
                        isnormal = "";
                    }
                } else {
                    isnormal = "";
                }
            } else {
                isnormal = "";//这个字段暂时对数值无用
            }

        }
        //----文字型与数值型 分类存值----
        if (dtype == "num") {
            resultArr.push({
                indid: indid,
                indname: indname,
                result: result,
                isnormal: isnormal,
                sn: sn,
                resultunit: resultunit,
                dtype: dtype,
                maxvalue: maxvalue,
                minvalue: minvalue,
                parentid: ''
            });
        } else {
            if (flag == 1) {
                v_result = result;
                resultArr.push({
                    flag: '1',
                    indid: indid,
                    indname: indname,
                    result: '',
                    isnormal: isnormal,
                    sn: sn,
                    resultunit: resultunit,
                    dtype: dtype,
                    parentid: ''
                });
            } else {
                resultArr.push({
                    flag: '2',
                    indid: indid,
                    indname: indname,
                    result: result,
                    isnormal: isnormal,
                    sn: sn,
                    resultunit: resultunit,
                    dtype: dtype,
                    parentid: ''
                });
            }
        }

    });
    return {resultArr: resultArr, flag: flag, v_result: v_result};
}

//-----返回一级自定义项目结果集合----
function OneItemsResult_zdy() {
    var resultArr = [];
    var indid = "";//指标id
    var result = "";//结果
    var isnormal = "";//是否异常
    var resultunit = "";//单位

    $("#titems tr[class='zdyclass']").each(function () {//文字：指标id，结果，是否正常；数值：指标id，结果
        var tdNodes = $(this).children();
        var tdNode2 = tdNodes.eq(0);
        var inpNodes = tdNode2.children();
        var dtype = inpNodes.eq(0).attr("dtype");//结果类型
        var result = "";//结果
        var indid = inpNodes.eq(0).attr("id");
        var indname = inpNodes.eq(0).attr("name");
        var sn = tdNodes.eq(0).attr("sn");//序号
        var maxvalue = "";
        var minvalue = "";

        if (dtype == "num") {
            maxvalue = inpNodes.eq(1).val();
            minvalue = inpNodes.eq(2).val();
        }

        if (dtype == 'str') {//文字：指标id，结果，是否正常；
            var defaultv = inpNodes.eq(1).val();//默认值
            //var result3 = inpNodes.eq(3).val();//弹出层设置的对照
            var unnormal = inpNodes.eq(2).val();
        } else if (dtype == 'num') {
            isnormal = "";//这个字段暂时对数值无用
        }
        //----文字型与数值型 分类存值----
        if (dtype == "num") {
            resultArr.push({
                indid: indid,
                indname: indname,
                result: result,
                isnormal: isnormal,
                sn: sn,
                resultunit: resultunit,
                dtype: dtype,
                maxvalue: maxvalue,
                minvalue: minvalue,
                parentid: ''
            });
        } else {
            resultArr.push({
                indid: indid,
                indname: indname,
                result: result,
                isnormal: isnormal,
                sn: sn,
                resultunit: resultunit,
                dtype: dtype,
                parentid: ''
            });
        }

    });
    return resultArr;
}


//-----返回一级项目子项结果集合(非免疫规划预防接种史)----
function OneItemsResult_jzs() {
    var resultArr_jzs = [];

    $("#" + typeid + " tr").each(function (j) {//文字：指标id，结果，是否正常；数值：指标id，结果
        if (j > 0) {
            var tdNodes = $(this).children();
            var option01 = tdNodes.eq(0).children().eq(0).val() == undefined ? "" : tdNodes.eq(0).children().eq(0).val();
            var option02 = tdNodes.eq(1).children().eq(0).val() == undefined ? "" : tdNodes.eq(1).children().eq(0).val();
            var option03 = tdNodes.eq(2).children().eq(0).val() == undefined ? "" : tdNodes.eq(2).children().eq(0).val();
            var option04 = tdNodes.eq(3).children().eq(0).val() == undefined ? "" : tdNodes.eq(3).children().eq(0).val();
            var option05 = tdNodes.eq(4).children().eq(0).val() == undefined ? "" : tdNodes.eq(4).children().eq(0).val();

            var sn = j;
            resultArr_jzs.push({
                option01: option01,
                option02: option02,
                option03: option03,
                option04: option04,
                option05: option05,
                sn: sn
            });
        }

    });
    return resultArr_jzs;
}

//-----返回两层结构子项结果集合(住院治疗情况)----
function OneItemsResult_zy() {
    var resultArr_zy = [];

    $("#titems tr[class='zdyclass']").each(function () {//文字：指标id，结果，是否正常；数值：指标id，结果
        var tdNodes = $(this).children();
        var tdNode2 = tdNodes.eq(0);
        var inpNodes = tdNode2.children();
        var indid = inpNodes.eq(0).attr("id");
        // alert("indid:"+indid);
        $("#zy" + indid + " tr[class='zyclass']").each(function (j) {//文字：指标id，结果，是否正常；数值：指标id，结果
            if (j > 0) {
                var tdNodes = $(this).children();
                var firstTableNode = tdNodes.eq(0).children();
                var firstTbodyNode = firstTableNode.eq(0).children();
                var firstTrNode = firstTbodyNode.eq(0).children();
                var firstTdNode = firstTrNode.eq(0).children();
                var inputNodes = firstTdNode.children();
                var option01 = inputNodes.eq(0).val();
                var option02 = inputNodes.eq(2).val();
                var option03 = tdNodes.eq(1).children().eq(0).val();
                var option04 = tdNodes.eq(2).children().eq(0).val();
                var option05 = tdNodes.eq(3).children().eq(0).val();

                //alert("option01+option02+option03+option04+option05:"+option01+";"+option02+";"+option03+";"+option04+";"+option05);
                var sn = j;
                resultArr_zy.push({
                    indid: indid,
                    option01: option01,
                    option02: option02,
                    option03: option03,
                    option04: option04,
                    option05: option05,
                    sn: sn
                });
            }

        });

    });
    return resultArr_zy;
}
function getErrorMsg() {
    alert($("#errorMsg").val());
}
//-----返回二级项目结果集合----
function TwoItemsResult() {
    var resultArr = [];
    var indid = "";//指标id
    var result = "";//结果
    var isnormal = "";//是否异常
    var resultunit = "";//单位

    $("#titems tr").each(function () {//文字：指标id，结果，是否正常；数值：指标id，结果
        var tdNodes = $(this).children();
        var tdNode2 = tdNodes.eq(1);//---获取第二个TD节点---
        var tdTable = tdNode2.children();
        ;//---获得的Table---
        var tableId = tdTable.eq(0).attr("id");//---获取Table id 父节点id---
        var tableName = tdTable.eq(0).attr("name");//---获取Table name 父节点name---
        var tableIszh = tdTable.eq(0).attr("iszh");//---获取Table iszh 父节点是否组合---
        //var inpNodes = tdNode2.children();//---获得的Table---
        $("#" + tableId + " tr").each(function () {

            var tdNodes = $(this).children();
            var tdNode2 = tdNodes.eq(1);
            var inpNodes = tdNode2.children();
            var dtype = inpNodes.eq(0).attr("dtype") == undefined ? "" : inpNodes.eq(0).attr("dtype");//结果类型
            var result = inpNodes.eq(0).val() == undefined ? "" : inpNodes.eq(0).val();//结果
            var sonindid = inpNodes.eq(0).attr("id") == undefined ? "" : inpNodes.eq(0).attr("id");
            var sonindname = tdNodes.eq(0).text() == undefined ? "" : tdNodes.eq(0).text();
            var sn = tdNodes.eq(0).attr("sn") == undefined ? "" : tdNodes.eq(0).attr("sn");//序号
            var maxvalue = "";
            var minvalue = "";


            if (dtype == "num") {
                maxvalue = inpNodes.eq(1).val();
                minvalue = inpNodes.eq(2).val();
            }
            resultunit = tdNodes.eq(2).attr("resultunit") == undefined ? "" : tdNodes.eq(2).attr("resultunit");//单位

            if (dtype == 'str') {//文字：指标id，结果，是否正常；
                var defaultv = inpNodes.eq(1).val();//默认值
                var result3 = inpNodes.eq(3).val();//弹出层设置的对照
                var unnormal = inpNodes.eq(2).val();
                if (result == "" || result == defaultv) {//此处规则--结果为空、等于系统设置的默认值则为正常
                    isnormal = "正常";
                } else if (result3 == result) {//相等这正常否看"正常否列"值
                    isnormal = unnormal;
                } else {//异常
                    isnormal = "异常";
                }
            } else if (dtype == 'num') {
                isnormal = "";//这个字段暂时对数值无用
            }
            //----文字型与数值型 分类存值----
            if (dtype == "num") {
                resultArr.push({
                    indid: sonindid,
                    indname: sonindname,
                    result: result,
                    isnormal: isnormal,
                    sn: sn,
                    resultunit: resultunit,
                    dtype: dtype,
                    maxvalue: maxvalue,
                    minvalue: minvalue,
                    //sonindid:sonindid,
                    //sonindname:sonindname,
                    parentid: tableId
                    //iszh:tableIszh
                });
            } else {
                resultArr.push({
                    indid: sonindid,
                    indname: sonindname,
                    result: result,
                    isnormal: isnormal,
                    sn: sn,
                    resultunit: resultunit,
                    dtype: dtype,
                    //sonindid:sonindid,
                    //sonindname:sonindname,
                    parentid: tableId
                    //iszh:tableIszh
                });
            }
        });

    });
    return resultArr;

}

//体检常见接口筛选
function topHigh() {
    var tr = document.getElementById("choose")
    if (tr.style.display == "none") {
        $("#choose").show();
    } else {
        $("#choose").hide();
    }
    $("#resultname").val("");
}
function timeSearch() {
    comparSearch = "loadByTime";
    loadCommonResultsCount();
}


function insertimg() {
    var top = (260 + 105);  //上面一行的top距离
    var top1 = (260 + 105 + 101); //下面一行的top距离
    var left = (document.body.clientWidth / 2 - 100) + 34;
    var imgstr = '<img class="hiddenimg" id="reda' + (0 + 1) + '" style="position:absolute;z-index:200;top:' + top + 'px;left:' + (left + (0) * 29.7) + 'px;" info="右上8" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="reda' + (1 + 1) + '" style="position:absolute;z-index:200;top:' + top + 'px;left:' + (left + (1) * 29.7) + 'px;" info="右上7" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="reda' + (2 + 1) + '" style="position:absolute;z-index:200;top:' + top + 'px;left:' + (left + (2) * 29.7 + 2) + 'px;" info="右上6" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="reda' + (3 + 1) + '" style="position:absolute;z-index:200;top:' + top + 'px;left:' + (left + (3) * 29.7 + 2) + 'px;" info="右上5" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="reda' + (4 + 1) + '" style="position:absolute;z-index:200;top:' + top + 'px;left:' + (left + (4) * 29.7) + 'px;" info="右上4" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="reda' + (5 + 1) + '" style="position:absolute;z-index:200;top:' + top + 'px;left:' + (left + (5) * 29.7) + 'px;" info="右上3" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="reda' + (6 + 1) + '" style="position:absolute;z-index:200;top:' + top + 'px;left:' + (left + (6) * 29.7) + 'px;" info="右上2" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="reda' + (7 + 1) + '" style="position:absolute;z-index:200;top:' + top + 'px;left:' + (left + (7) * 29.7) + 'px;" info="右上1" onclick="changeIMGClass(this);" src="images/red1.png" />';

    imgstr += '<img class="hiddenimg" id="reda' + (8 + 1) + '" style="position:absolute;z-index:200;top:' + top + 'px;left:' + (left + (8) * 29.7 + 2) + 'px;" info="左上1" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="reda' + (9 + 1) + '" style="position:absolute;z-index:200;top:' + top + 'px;left:' + (left + (9) * 29.7 + 4) + 'px;" info="左上2" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="reda' + (10 + 1) + '" style="position:absolute;z-index:200;top:' + top + 'px;left:' + (left + (10) * 29.7) + 'px;" info="左上3" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="reda' + (11 + 1) + '" style="position:absolute;z-index:200;top:' + top + 'px;left:' + (left + (11) * 29.7) + 'px;" info="左上4" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="reda' + (12 + 1) + '" style="position:absolute;z-index:200;top:' + top + 'px;left:' + (left + (12) * 29.7 - 2) + 'px;" info="左上5" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="reda' + (13 + 1) + '" style="position:absolute;z-index:200;top:' + top + 'px;left:' + (left + (13) * 29.7 - 1) + 'px;" info="左上6" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="reda' + (14 + 1) + '" style="position:absolute;z-index:200;top:' + top + 'px;left:' + (left + (14) * 29.7) + 'px;" info="左上7" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="reda' + (15 + 1) + '" style="position:absolute;z-index:200;top:' + top + 'px;left:' + (left + (15) * 29.7) + 'px;" info="左上8" onclick="changeIMGClass(this);" src="images/red1.png" />';

    imgstr += '<img class="hiddenimg" id="redaa' + (0 + 1) + '" style="position:absolute;z-index:200;top:' + top1 + 'px;left:' + (left + (0) * 29.7) + 'px;" info="右下8" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="redaa' + (1 + 1) + '" style="position:absolute;z-index:200;top:' + top1 + 'px;left:' + (left + (1) * 29.7 + 3) + 'px;" info="右下7" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="redaa' + (2 + 1) + '" style="position:absolute;z-index:200;top:' + top1 + 'px;left:' + (left + (2) * 29.7 + 10) + 'px;" info="右下6" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="redaa' + (3 + 1) + '" style="position:absolute;z-index:200;top:' + top1 + 'px;left:' + (left + (3) * 29.7 + 10) + 'px;" info="右下5" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="redaa' + (4 + 1) + '" style="position:absolute;z-index:200;top:' + top1 + 'px;left:' + (left + (4) * 29.7 + 10) + 'px;" info="右下4" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="redaa' + (5 + 1) + '" style="position:absolute;z-index:200;top:' + top1 + 'px;left:' + (left + (5) * 29.7 + 8) + 'px;" info="右下3" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="redaa' + (6 + 1) + '" style="position:absolute;z-index:200;top:' + top1 + 'px;left:' + (left + (6) * 29.7 + 3) + 'px;" info="右下2" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="redaa' + (7 + 1) + '" style="position:absolute;z-index:200;top:' + top1 + 'px;left:' + (left + (7) * 29.7) + 'px;" info="右下1" onclick="changeIMGClass(this);" src="images/red1.png" />';

    imgstr += '<img class="hiddenimg" id="redaa' + (8 + 1) + '" style="position:absolute;z-index:200;top:' + top1 + 'px;left:' + (left + (8) * 29.7) + 'px;" info="左下1" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="redaa' + (9 + 1) + '" style="position:absolute;z-index:200;top:' + top1 + 'px;left:' + (left + (9) * 29.7 - 3) + 'px;" info="左下2" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="redaa' + (10 + 1) + '" style="position:absolute;z-index:200;top:' + top1 + 'px;left:' + (left + (10) * 29.7 - 8) + 'px;" info="左下3" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="redaa' + (11 + 1) + '" style="position:absolute;z-index:200;top:' + top1 + 'px;left:' + (left + (11) * 29.7 - 10) + 'px;" info="左下4" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="redaa' + (12 + 1) + '" style="position:absolute;z-index:200;top:' + top1 + 'px;left:' + (left + (12) * 29.7 - 16) + 'px;" info="左下5" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="redaa' + (13 + 1) + '" style="position:absolute;z-index:200;top:' + top1 + 'px;left:' + (left + (13) * 29.7 - 10) + 'px;" info="左下6" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="redaa' + (14 + 1) + '" style="position:absolute;z-index:200;top:' + top1 + 'px;left:' + (left + (14) * 29.7 - 3) + 'px;" info="左下7" onclick="changeIMGClass(this);" src="images/red1.png" />';
    imgstr += '<img class="hiddenimg" id="redaa' + (15 + 1) + '" style="position:absolute;z-index:200;top:' + top1 + 'px;left:' + (left + (15) * 29.7) + 'px;" info="左下8" onclick="changeIMGClass(this);" src="images/red1.png" />';

    $("body").append(imgstr);
}
function getPosition(obj) {
    var top = obj.style.top;
    var left = obj.style.left;
    return {x: left, y: top};
}