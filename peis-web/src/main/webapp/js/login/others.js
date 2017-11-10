var F5_KEYCODE = 116;
var F6_KEYCODE = 117;
var F7_KEYCODE = 118;
var tabbar;
var zTreeObj;
var setting = {
    showLine: true,
    expandSpeed: "fast",
    nameCol: "name",
    treeNodeKey: "id",
    treeNodeParentKey: "pid",
    isSimpleData: true,
    callback: {
        click: zTreeOnClick
    }
};

$(document).ready(function () {
    reloadTree();

    tabbar = new dhtmlXTabBar("tab_bar", "top");
    tabbar.setSkin("dhx_skyblue");
    tabbar.setImagePath("libs/dhtmlx_std_full/imgs/");
    tabbar.enableTabCloseButton(true);
    tabbar.enableAutoReSize(true);

    // addNewTab('380202', '集团部门维护', 'dept/showGlobal.htm', null);
    addNewTab("380401", '菜单管理', 'menu/system_menu.htm', null);

    $(document).bind("keydown", function (event) {
        if (event.keyCode == F5_KEYCODE) {
            $("#reg_li")[0].click();
            event.preventDefault();
        } else if (event.keyCode == F6_KEYCODE) {
            $("#charge_li")[0].click();
            event.preventDefault();
        } else if (event.keyCode == F7_KEYCODE) {
            $("#other_li")[0].click();
            event.preventDefault();
        }
    });

    $("ul.hsp_xx > li").click(function () {
        if (!$(this).hasClass("hsp_xxhover")) {
            $(this).addClass("hsp_xxhover");
            $(this).siblings().removeClass("hsp_xxhover");
        }
        if ($(this).attr("id") == "reg_li") {
            window.location = "register.htm";
        } else if ($(this).attr("id") == "charge_li") {
            //window.location = "";
        } else if ($(this).attr("id") == "other_li") {
            //window.location = "others.htm";
        }
    }).hover(function () {
            $(this).css("cursor", "pointer");
        }, function () {
            $(this).css("cursor", "default");
        }
    );

});


function zTreeOnClick(event, treeId, treeNode) {
    var model = treeNode.model;
    Log(treeNode);
    // event.preventDefault();
    // return ;
    if (!treeNode.isParent) {
        addNewTab(treeNode.id, treeNode.name, treeNode.model, treeNode.type);
    }
}
function addNewTab(menuId, menuName, url, opentype) {
    if (url == null) {
        //alert('待实现!');
        return;
    }
    if (opentype == 1) {
        if (url.indexOf('?') > -1) {
            document.location.href = url + "&menuid=" + menuId;
        } else {
            document.location.href = url + "?menuid=" + menuId;
        }
        return;
    }
    var $ma = $("#mainArea_" + menuId);
    if ($ma[0] == undefined || $ma[0] == null) {
        tabbar.addTab("tab_" + menuId, "<font size='2' face='微软雅黑'>" + menuName + "</font>", (menuName.length + 3) * 16 + "px");
        var mainArea = $("#mainArea").clone(true);
        var mid = "mainArea_" + menuId;
        mainArea.attr("id", mid);
        mainArea.find("iframe")[0].src = url;
        mainArea.find("iframe")[0].height = $("#dhxMainCont").height();
        mainArea.show();
        var area = mainArea[0].outerHTML;
        //alert(12)
        $("#tab_bar").append(mainArea);
        tabbar.setContent("tab_" + menuId, mid);
        tabbar.setContentHTML("tab_" + menuId, area);//直接把网页加载进tab
    }
    tabbar.setTabActive("tab_" + menuId);
}

function reloadTree() {
    var zNodes1 = clone(simpleNodes);//clone(zNodes)
    zTreeObj = $("#menuTree").zTree(setting, zNodes1);

}