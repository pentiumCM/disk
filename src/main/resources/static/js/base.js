/*用户自定义的js*/
$.ajaxSetup({
    global: false,
    contentType: "application/json; charset=utf-8",
    dataType: "json",
    error : function(XMLHttpRequest, textStatus, errorThrown) {
        console.log('XMLHttpRequest:'+XMLHttpRequest+';textStatus:'+textStatus+';errorThrown:'+errorThrown);
        bootbox.alert({
            size: "small",
            title: "提示",
            message: "提示：登录请求失败，请检查网络或服务器运行状态:"+textStatus,
            callback: function(){ /* your callback code */ }
        });
    }
});

bootbox.setLocale("zh_CN");

if($.ui!=undefined){

$.widget("ui.dialog", $.extend({}, $.ui.dialog.prototype, {
    _title: function(title) {
        var $title = this.options.title || '&nbsp;'
        if( ("title_html" in this.options) && this.options.title_html == true )
            title.html($title);
        else title.text($title);
    }
}));
}

var cc={
    setNavTitle:function (title) {

        $('#sidebar .nav-list li').each(function(){
            var active=true;
            $(this).find('li').each(function(){
                if($(this).text().trim()==document.title||$(this).text().trim()==title){
                    $(this).addClass('active');
                    active=false;
                    return false;
                }
            });
            if(!active){
                $(this).addClass('open')
            }
        });
    },
    getCurrentUser:function(){
        $.ajax({
            url: '/user/getCurrentUser',
            success: function (d) {
                if(d.picture==null){
                    $('#nav-user-photo').attr('src','/assets/images/avatars/profile-pic.jpg')
                }else{
                    $('#nav-user-photo').attr('src','/file/getCurrentProfile')
                }
                if(d.role=='Admin'){
                        $('#extend').append('<li id="admin-permission">\n' +
                            '                        <a href="/user/index">\n' +
                            '                            <i class="menu-icon fa fa-caret-right"></i>\n' +
                            '                            用户管理\n' +
                            '                        </a>\n' +
                            '                        <b class="arrow"></b>\n' +
                            '                    </li>')
                }

                cc.setNavTitle();
            }
        });
    },
    setCurrentUserId:function () {
        $('#p-currentUserId').html($('#hidden-currentUserId').val());
    },
    getUrlParam:function(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg);  //匹配目标参数
        if (r != null) return decodeURI(r[2]); return null; //返回参数值
    },
     bytesToSize:function(bytes) {
        if (bytes === 0) return '0 B';
        var k = 1024,
            sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
            i = Math.floor(Math.log(bytes) / Math.log(k));

        return (bytes / Math.pow(k, i)).toPrecision(4) + ' ' + sizes[i];
    },
    formatStr :function () {
        for (var i = 1; i < arguments.length; i++) {
            var exp = new RegExp('\\{' + (i - 1) + '\\}', 'gm');
            arguments[0] = arguments[0].replace(exp, arguments[i]);
        }
        return arguments[0];
    }
}


$.fn.serializeObject=function(){
    var o ={};
    $.each($(this).serializeArray(),function(index){
        if(o[this['name']]){
            o[this['name']] = o[this['name']] +","+this['value'];
        }else{
            o[this['name']] = this['value'];
        }
    });
    return o;
}

$.fn.loadJson = function(jsonValue) {
    var obj = this;
    $.each(jsonValue, function(name, ival) {
        var $oinput = obj.find(":input[name=" + name + "]");
        if ($oinput.attr("type") == "radio"
            || $oinput.attr("type") == "checkbox") {
            $oinput.each(function() {
                if (Object.prototype.toString.apply(ival) == '[object Array]') {//是复选框，并且是数组
                    for (var i = 0; i < ival.length; i++) {
                        if ($(this).val() == ival[i])
                            $(this).attr("checked", "checked");
                    }
                } else {
                    if ($(this).val() == ival)
                        $(this).attr("checked", "checked");
                }
            });
        } else if ($oinput.attr("type") == "textarea") {//多行文本框
            obj.find("[name=" + name + "]").html(ival);
        } else {
            obj.find("[name=" + name + "]").val(ival);
        }
    });
};

$.fn.jqvalidate=function (options) {
    var defaults={
        errorElement: 'div',
        errorClass: 'help-block',
        focusInvalid: false,
        ignore: "",
        highlight: function (e) {
            $(e).closest('.form-group').removeClass('has-info').addClass('has-error');
        },
        success: function (e) {
            $(e).closest('.form-group').removeClass('has-error');//.addClass('has-info');
            $(e).remove();
        },
        errorPlacement: function (error, element) {
            if(element.is('input[type=checkbox]') || element.is('input[type=radio]')) {
                var controls = element.closest('div[class*="col-"]');
                if(controls.find(':checkbox,:radio').length > 1) controls.append(error);
                else error.insertAfter(element.nextAll('.lbl:eq(0)').eq(0));
            }
            else if(element.is('.select2')) {
                error.insertAfter(element.siblings('[class*="select2-container"]:eq(0)'));
            }
            else if(element.is('.chosen-select')) {
                error.insertAfter(element.siblings('[class*="chosen-container"]:eq(0)'));
            }
            else error.insertAfter(element.parent());
        },

        submitHandler: function (form) {
        },
        invalidHandler: function (form) {
        }
    }

    $.extend(defaults,options);

    $(this).validate(defaults);
}