var F1_KEYCODE = 112;
var F2_KEYCODE = 113;
var F3_KEYCODE = 114;
var F4_KEYCODE = 115;
var F5_KEYCODE = 116;
var F6_KEYCODE = 117;
var F7_KEYCODE = 118;
var F8_KEYCODE = 119;
var F9_KEYCODE = 120;
var F10_KEYCODE = 121;
var F11_KEYCODE = 122;
var F12_KEYCODE = 123;
//是否按住ctrl键
var isCtrl = false;

var F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, ENTER, SHIFT, CTRL, CTRL_S, CTRL_Z, CTRL_X, CTRL_N, CTRL_P, KEY_37,
    KEY_39, CTRL_D;
var mywin;
function openMyWin(win, title, width, height, url) {
    mywin = win;
    openWin(title, width, height, url);
}
function forward(url, menuid) {
    if (url.indexOf('?') > -1) {
        document.location.href = '<%=basePath%>' + url + '&menuid=' + menuid;
    } else {
        document.location.href = '<%=basePath%>' + url + '?menuid=' + menuid;

    }
}

document.onkeydown = function (evt) {
    var event = evt ? evt : window.event;
    var node = event.srcElement ? event.srcElement : event.target;
    if (event.keyCode == F1_KEYCODE) {
        event.keyCode = 0;
        event.returnValue = false;
        if (evt) {
            event.preventDefault();
        }
        eval(F1);
    } else if (event.keyCode == F2_KEYCODE) {
        event.keyCode = 0;
        event.returnValue = false;
        if (evt) {
            event.preventDefault();
        }
        eval(F2);
    } else if (event.keyCode == F3_KEYCODE) {
        event.keyCode = 0;
        event.returnValue = false;
        if (evt) {
            event.preventDefault();
        }
        eval(F3);
    } else if (event.keyCode == F4_KEYCODE) {
        event.keyCode = 0;
        event.returnValue = false;
        if (evt) {
            event.preventDefault();
        }
        eval(F4);
    } else if (event.keyCode == F5_KEYCODE) {
        event.keyCode = 0;
        event.returnValue = false;
        if (evt) {
            event.preventDefault();
        }
        eval(F5);
    } else if (event.keyCode == F6_KEYCODE) {
        event.keyCode = 0;
        event.returnValue = false;
        if (evt) {
            event.preventDefault();
        }
        eval(F6);
    } else if (event.keyCode == F7_KEYCODE) {
        event.keyCode = 0;
        event.returnValue = false;
        if (evt) {
            event.preventDefault();
        }
        eval(F7);
    } else if (event.keyCode == F8_KEYCODE) {
        event.keyCode = 0;
        event.returnValue = false;
        if (evt) {
            event.preventDefault();
        }
        eval(F8);
    } else if (event.keyCode == F9_KEYCODE) {
        event.keyCode = 0;
        event.returnValue = false;
        if (evt) {
            event.preventDefault();
        }
        eval(F9);
    } else if (event.keyCode == F10_KEYCODE) {
        event.keyCode = 0;
        event.returnValue = false;
        if (evt) {
            event.preventDefault();
        }
        eval(F10);
    } else if (event.keyCode == F11_KEYCODE) {
        event.keyCode = 0;
        event.returnValue = false;
        if (evt) {
            event.preventDefault();
        }
        eval(F11);
    } else if (event.keyCode == 8) {//backspace
        if ((node.tagName.toLowerCase() != "input" && node.tagName.toLowerCase() != "textarea" && node.tagName.toLowerCase() != "text") || node.readOnly) {
            event.keyCode = 0;
            event.returnValue = false;
            if (evt) {
                event.preventDefault();
            }
        }
    } else if (event.keyCode == 13) {//enter
        eval(ENTER);
    } else if (event.keyCode == 16) {//shift
        eval(SHIFT);
    } else if (event.keyCode == 17) {//ctrl
        eval(CTRL);
    } else if (isCtrl && event.keyCode == 83) {//ctrl_s
        eval(CTRL_S);
    } else if (isCtrl && event.keyCode == 90) {//ctrl_z
        eval(CTRL_Z);
    } else if (event.ctrlKey && event.keyCode == 78) {//n
        eval(CTRL_N);
    } else if (event.ctrlKey && event.keyCode == 80) {//p
        eval(CTRL_P);
    } else if (event.ctrlKey && event.keyCode == 83) {//s
        eval(CTRL_S);
    } else if (event.keyCode == 37) {
        eval(KEY_37);
    } else if (event.keyCode == 39) {
        eval(KEY_39);
    } else if (event.ctrlKey && event.keyCode == 88) {//ctrl_x
        eval(CTRL_X);
    } else if (event.ctrlKey && event.keyCode == 68) {//ctrl_d
        eval(CTRL_D);
    }
}