/*用户自定义的js，负责文件的界面的逻辑*/
var webuploader = webuploader || {};        /*当webuploader有值时，则赋值给webuplaoder，否则={}*/
var abp = abp || {};
(function ($, window) {         /*$ 是把jQuery传进来，这样你可以在function中可以继续使用$作为jQuery的引用，②window 把当前的window（窗口）对象传进来*/
    var applicationPath = window.applicationPath === "" ? "" : window.applicationPath || "/js";

    var serviceUrl = "";
    var downloadUrl = "/file/download";
    webuploader.authorization = "";

    var userInfo = {
            md5: '',
            index: '',
            fileToken: ''
        },
        uploaders = {};

    // 大文件分片，断点续传，以及秒传功能
    // 在文件开始发送前做些异步操作。做md5验证
    // WebUploader会等待此异步操作完成后，开始发送文件。
    WebUploader.Uploader.register({
        "before-send-file": "beforeSendFile",
        "before-send": "beforeSend"
    }, {
        beforeSendFile: function (file) {
            var task = new $.Deferred();
            var $fileId = file.id;
            uploaders[userInfo.index].GUID = WebUploader.Base.guid();
            uploaders[userInfo.index].md5File(file, 0, 10 * 1024 * 1024).progress(function (percentage) {
                var uploadflieElement = $('#' + $fileId);
                if (uploaders[userInfo.index].options.uploadType === 'file') {
                    var value = Math.round(percentage * 100);
                    uploadflieElement.find('div.webuploadstate .webupload-text').html('正在验证文件' + value + '%');
                }
            }).then(function (val) {
                var $fileId = file.id;
                uploaders[userInfo.index].md5 = val;
                $.ajax({
                    headers: {
                        "Authorization": "Bearer " + webuploader.authorization
                    },
                    type: "POST",
                    url: serviceUrl + '/file/md5validate',
                    data: JSON.stringify({
                        md5Val: val,
                        fileName: file.name
                    }),
                    cache: false,
                    timeout: 3000,
                    dataType: "json"
                }).then(function (dd, textStatus, jqXHR) {
                    var response = {
                        success: dd.success,
                        error: {
                            message: ""
                        },
                        result: dd.errMsg
                    }
                    //若存在，这返回失败给WebUploader，(后台操作)表明该文件不需要上传，然后就可以把数据库查询出的文件信息的物理路径给新文件
                    if (response.success == true) {
                        //task.reject('秒传成功');  此处会执行uploadError
                        uploaders[userInfo.index].skipFile(file);
                        userInfo.fileToken = response.result;
                        //上传成功事件
                        if (uploaders[userInfo.index].options.uploadType === 'file') {
                            if ($('#' + $fileId + ' .webuploadstate .file-token').attr('data-filetoken') != '') {
                                return;
                            }
                            var limit = webuploader.bytesToSize(file.size);
                            var downUrl = serviceUrl + downloadUrl + '?fileToken=' + response.result + '&newName=' + file.name;

                            $('#' + $fileId).find('div.webuploadstate .webupload-text').html(limit + ' 秒传');
                            $('#' + $fileId).find('div.webuploadstate .file-token').attr('data-filetoken', response.result);

                            $('#' + $fileId)
                                .find('div.webuploadinfo .webupload-button a')
                                .after('<a href="' + downUrl + '" target="_blank"><span  class="webupload-download">下载</span></a>');

                        } else {
                            window.setTimeout(function () {
                                $('#' + $fileId + ' img').attr('src', serviceUrl + downloadUrl + '?fileToken=' + response.result);
                                $('#' + $fileId + ' .webupload-list-img-cover .img-upload-state span.file-token').attr('data-filetoken', response.result);
                            }, 500);
                        }
                    }
                    task.resolve();
                }, function (jqXHR, textStatus, errorThrown) { //任何形式的验证失败，都触发重新上传
                    task.resolve();
                });
            });

            return $.when(task);
        }
    });

    function initWebUpload(item, options) {

        if (!WebUploader.Uploader.support()) {
            var error = "上传控件不支持您的浏览器！请尝试升级flash版本或者使用Chrome引擎的浏览器。<a target='_blank' href='http://se.360.cn'>下载页面</a>";
            if (window.console) {
                window.console.log(error);
            }
            $(item).text(error);
            return;
        }

        //创建默认参数
        var defaults = {
            auto: true,                         //选完文件后，是否自动上传。
            onAllComplete: function (event) {
            }, // 当所有file都上传后执行的回调函数
            onComplete: function (event) {
            }, // 每上传一个file的回调函数
            innerOptions: {},
            fileNumLimit: 20, //验证文件总数量, 超出则不允许加入队列
            fileSizeLimit: 2000 * 1024 * 1024, //验证文件总大小是否超出限制, 超出则不允许加入队列。
            fileSingleSizeLimit: 200 * 1024 * 1024
            , uploadType: 'file' //file、img
            , downloadUrl: '/file/download'
        };

        var opts = $.extend(defaults, options);
        if (opts.uploadType === 'img') {
            opts.accept = {                         // 只允许选择图片文件。
                title: 'Images',
                extensions: 'gif,jpg,jpeg,bmp,png',
                mimeTypes: 'image/*'
            };
        }
        var target = $(item);
        var pickerid = webuploader.GUID();
        var uploaderStrdiv = '<div class="webuploader">';
        target.attr('data-uploadType', opts.uploadType);
        var text = opts.uploadType === 'img' ? "选择图片" : "选择文件";
        if (opts.auto) {                //选完文件后，是否自动上传。
            uploaderStrdiv =
                '<div class="btns">' +
                '<div id="' +
                pickerid +
                '">' + text + '</div>' +
                '</div>' +
                '<div  class="uploader-list"></div>';
        } else {
            uploaderStrdiv =
                '<div class="btns">' +
                '<div id="' +
                pickerid +
                '">' + text + '</div>' +
                '<button class="webuploadbtn" type="button">开始上传</button>' +
                '</div>' +
                '<div class="uploader-list"></div>';
        }

        target.append(uploaderStrdiv);

        var $list = target.find('.uploader-list'),
            $btn = target.find('.webuploadbtn'),        //手动上传按钮备用
            state = 'pending',
            uploader;

        var webuploaderoptions = $.extend({         /*jQuery.extend(object);为扩展jQuery类本身.为类添加新的方法。*/
            // swf文件路径
            swf: applicationPath + '/Uploader.swf',
            // 文件接收服务端。
            server: serviceUrl + '/file/bigFile',
            deleteServer: serviceUrl + '/file/delete',
            // 选择文件的按钮。可选。
            // 内部根据当前运行是创建，可能是input元素，也可能是flash.
            pick: '#' + pickerid,
            //不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
            resize: false,
            fileNumLimit: opts.fileNumLimit,
            fileSizeLimit: opts.fileSizeLimit,
            fileSingleSizeLimit: opts.fileSingleSizeLimit,
            accept: opts.accept,
            withCredentials: true,
            chunked: true, // 分片处理
            chunkSize: 5 * 1024 * 1024, // 每片32M,
            chunkRetry: false,// 如果失败，则不重试
            threads: 1,// 上传并发数。允许同时最大上传进程数。
        }, opts.innerOptions);

        uploader = WebUploader.create(webuploaderoptions);

        uploaders[pickerid] = uploader;
        $.extend(uploaders[pickerid].options, opts);

        // 当有文件添加进来的时候，监听fileQueued事件
        uploader.on('fileQueued',
            function (file) {
                var subName = file.name;
                //主要解决名字太长，上传文件名会超出换行，导致样式问题，让字数在20个字符以内即可
                if (subName.lastIndexOf('.') > 20) {
                    subName = subName.substring(0, 20);
                } else {
                    subName = subName.substring(0, subName.lastIndexOf('.'));
                }
                subName += '.' + file.ext;
                var fileId = file.id;
                var fileUpload = "";
                if (opts.uploadType === 'file') {
                    console.log("开始上传文件");
                    fileUpload = '<div id="' + fileId + '" class="item">\
                                         <div class="nui-ico nui-ico-file32 nui-ico-file32-' + file.ext.toLowerCase() + '"></div>\
                                            <div class="webuploadinfo"> <span title="' + file.name + '">' + subName + '</span>\
                                                <div class="webupload-button"><a><span class="webupload-delete" data-id="' + fileId + '">删除</span></a></div>\
                                            </div>\
                                            <div class="webuploadstate"><span class="file-token" data-filetoken=""></span><span class="webupload-text">等待上传</span>\
                                                 <div id="progress-' + fileId + '" class="progressbar-animation"></div>\
                                             </div>\
                                     </div>';
                } else {
                    /*文件类型为图片时*/
                    /*/Content/images/default-profile-picture.png*/
                    fileUpload = '<div  id="' + fileId + '" class="webupload-list-img">\
                            <img src = "" >\
                            <div class="webupload-list-img-cover">\
                            <i class="fa fa-eye img-show" title="预览"  data-id="' + fileId + '"></i>\
                            <i class="fa fa-remove img-delete" title="删除" data-id="' + fileId + '"></i>\
                            <div class="img-upload-state" style="display:none;" >' +
                        '<span class="file-name" data-filename="' + file.name + '"></span>' +
                        '<span class="file-token" data-filetoken=""></span>' +
                        '</div>\
                            </div>\
                          </div>';
                }

                $list.append(fileUpload);


                //自动上传
                if (opts.auto) {
                    uploader.upload();
                }
            });

        /*
         * 验证文件格式以及文件大小
         */
        uploader.on("error",
            function (type) {
                var limit = 0;
                if (type === "Q_TYPE_DENIED") {
                    bootbox.alert("不支持您上传的文件格式!");
                } else if (type === "F_EXCEED_SIZE") {
                    limit = opts.fileSingleSizeLimit / 1024 / 1024;
                    bootbox.alert("单个文件大小不能超过" + limit + "MB");
                } else if (type === "Q_EXCEED_SIZE_LIMIT") {
                    limit = opts.fileSingleSizeLimit / 1024 / 1024;
                    bootbox.alert("添加的文件总大小超出" + limit + "MB");
                } else if (type === "Q_EXCEED_NUM_LIMIT") {
                    bootbox.alert("添加的文件数量超出范围");
                } else if (type === "F_DUPLICATE") {
                    bootbox.alert("该文件已存在,请勿重复添加!");
                } else {
                    bootbox.alert("未知类型错误:" + type);
                }
            });

        /*当文件被加入队列之前触发，此事件的handler返回值为false，则此文件不会被添加进入队列。*/
        uploader.on("beforeFileQueued",
            function (file) {
                if (opts.fileNumLimit == undefined) {
                    return true;
                }
                var length;
                if (opts.uploadType === 'file') {
                    //当有文件限制，在编辑时验证文件数量
                    length = target.find('.item').length;
                } else {
                    length = target.find('.webupload-list-img').length;
                }
                if (length >= opts.fileNumLimit) {
                    bootbox.alert('添加的文件数量超出范围!');
                    return false;
                }
                userInfo.index = pickerid;
                return true;
            });

        uploader.on('uploadProgress',
            function (file, percentage) { //进度条事件
                var fileId = file.id;
                var uploadflieElement = target.find('#' + fileId);
                if (opts.uploadType === 'file') {
                    var value = Math.round(percentage * 100);
                    uploadflieElement.find('div.webuploadstate .webupload-text').html('正在上传' + value + '%');
                    if ($.fn.progressbar != undefined) {
                        uploadflieElement.find('div.webuploadstate #progress-' + fileId).progressbar({
                            value: value,
                            create: function (event, ui) {
                                $(this).addClass('progress progress-striped active').children(0).addClass('progress-bar progress-bar-success');
                            }
                        });
                    }
                } else {

                }
            });

        uploader.on('uploadSuccess',
            function (file, dd) {
                if (file.skipped == true) {
                    return;
                }
                var task = new $.Deferred();
                $.ajax({
                    url: '/file/merge',
                    type: 'POST',
                    contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
                    data: {
                        guid: uploaders[userInfo.index].GUID,
                        fileName: file.name,
                        md5Val: uploaders[userInfo.index].md5,
                        fileSize: file.size
                    }
                }).then(function (dd) {
                    var response = {
                        success: dd.success,
                        error: {
                            message: "未知异常"
                        },
                        result: dd.errMsg
                    }
                    //上传成功事件
                    //后台保存文件属性信息services 重构，后台上传文件的时候，就把数据存到后台
                    var $fileId = file.id;
                    if (opts.uploadType === 'file') {
                        if (response.success === false) {
                            bootbox.alert(response.error.message);
                            target.find('#' + $fileId).find('div.webuploadstate .webupload-text').html(response.error.message).css('color', 'red');
                        } else {
                            var limit = webuploader.bytesToSize(file.size);
                            var downUrl = serviceUrl + downloadUrl + '?fileToken=' + response.result + '&newName=' + file.name;

                            target.find('#' + $fileId).find('div.webuploadstate .webupload-text').html(limit + ' 上传成功');
                            target.find('#' + $fileId).find('div.webuploadstate .file-token').attr('data-filetoken', response.result);

                            target.find('#' + $fileId).find('div.webuploadinfo .webupload-button a')
                                .after('<a href="' + downUrl + '" target="_blank"><span  class="webupload-download">下载</span></a>');
                        }
                    } else {
                        if (response.success === false) {
                            target.find('#' + $fileId);
                            bootbox.alert(response.error.message);
                        } else {
                            window.setTimeout(function () {
                                target.find('#' + $fileId + ' img').attr('src', serviceUrl + downloadUrl + '?fileToken=' + response.result);
                                target.find('#' + $fileId + ' .webupload-list-img-cover .img-upload-state span.file-token').attr('data-filetoken', response.result);
                            }, 500);
                        }
                    }
                });
            });

        uploader.on('uploadError',
            function (file, reason) {
                target.find('#' + file.id).find('div.webuploadstate .webupload-text').html("上传失败!");
            });

        uploader.on('uploadComplete',
            function (file) { //全部完成事件
                var fileId = file.id;
                target.find('#' + fileId).find('#progress-' + fileId).fadeOut();
            });

        uploader.on('all',
            function (type) {
                if (type === 'startUpload') {
                    state = 'uploading';
                } else if (type === 'stopUpload') {
                    state = 'paused';
                } else if (type === 'uploadFinished') {
                    state = 'done';
                }

                if (state === 'uploading') {
                    $btn.text('暂停上传');
                } else {
                    $btn.text('开始上传');
                }
            });

        var deleteFile = function (fileToken, callback) {
            callback && callback();
            return
            // 增加秒传后，不再执行删除后台数据及文件操作
            //2018-10-17 后台接口调整，文件md5验证和token关联存储
            if (!webuploader.isNullOrEmpty(fileToken)) {
                $.ajax({
                    url: webuploaderoptions.deleteServer,
                    data: JSON.stringify({
                        fileToken: fileToken
                    }),
                    headers: {
                        "Authorization": "Bearer " + webuploader.authorization
                    },
                    success: function (data) {
                        if (data.Succeed == 0 && data.Message == "该文件不存在!") {
                            bootbox.alert(data.Message);
                            callback && callback();
                            return;
                        }
                        if (data.Succeed == 0) {
                            bootbox.alert(data.Message);
                        } else {
                            callback && callback();
                        }
                    }
                });
            } else {
                callback && callback();
            }
        }
        //删除时执行的方法
        uploader.on('fileDequeued',
            function (file) {
                var fileToken;
                var fileId = file.id;
                if (target.data('uploadtype') === 'file') {
                    fileToken = target.find("#" + fileId + ' .webuploadstate .file-token').data('filetoken');
                } else {
                    fileToken = target.find('#' + fileId + ' .webupload-list-img-cover .img-upload-state span.file-token').attr('data-filetoken');
                }
                deleteFile(fileToken, function () {
                    target.find("#" + fileId).hide(500,
                        function () {
                            $(this).remove();
                        });
                });

            });

        uploader.on('uploadBeforeSend', function (object, data, headers) {
            $.extend(data, {
                guid: uploaders[userInfo.index].GUID,
                md5: userInfo.md5,
            });
        });
        //多文件点击上传的方法
        $btn.on('click',
            function () {
                if (state === 'uploading') {
                    uploader.stop();
                } else {
                    uploader.upload();
                }
            });

        //删除
        if (opts.uploadType === 'file') {
            $list.on("click",
                ".webupload-delete",
                function () {
                    var $ele = $(this);
                    var fileId = $ele.attr("data-id");

                    var file = uploader.getFile(fileId);

                    if (file == undefined) {
                        //前台直接删除
                        $('#' + fileId).fadeOut(500,
                            function () {
                                $(this).remove();
                            });
                        var list = fileId.split('_');
                        var fileToken = $("#hiddenInput" + list[list.length - 1]).val();
                        if (list.length >= 1) {
                            $("#hiddenInput" + list[list.length - 1]).remove();
                        }
                    } else {
                        uploader.removeFile(file);
                    }
                });
        } else {
            $list.on("click",
                ".img-delete",
                function () {
                    var $ele = $(this);
                    var fileId = $ele.attr("data-id");
                    var file = uploader.getFile(fileId);

                    if (file == undefined) {
                        //前台直接删除
                        var filetoken = $ele.next().find('.file-token').data('filetoken');
                        deleteFile(filetoken, function () {
                            $('#' + fileId).hide(500,
                                function () {
                                    $(this).remove();
                                });
                        });
                    } else {
                        uploader.removeFile(file);
                    }
                });

            $list.on("click",
                ".img-show",
                function () {
                    var $ele = $(this);
                    webuploader.imagePreviewDialog($ele);
                });
        }
        return uploader;
    }

    $.extend(webuploader, {
        /**
         * webuploader的图片预览效果
         * @param {} $ele 当前点击的图片elem
         */
        imagePreviewDialog: function ($ele) {
            var id = '#' + $ele.attr("data-id");
            var imgSrc = $(id).find('img').attr('src');
            abp.imagePreviewDialog(imgSrc);
        },
        /**
         * 动态加载webuploader控件
         * @param {any} callback
         */
        getWebUpload: function (callback) {
            if (typeof WebUploader == 'undefined') {
                var casspath = applicationPath + "/webuploader.css";
                $("<link>").attr({
                    rel: "stylesheet",
                    type: "text/css",
                    href: casspath
                }).appendTo("head");
                var jspath = applicationPath + "/webuploader.min.js";
                $.getScript(jspath).done(function () {
                    callback && callback();
                }).fail(function () {
                    bootbox.alert("请检查webuploader的路径是否正确!");
                });
            } else {
                callback && callback();
            }
        },
        /**
         *  上传文件模板
         * @param {boolean} isCheck  是否是查看界面 true/false
         * @returns {} 参数为空时，默认不是查看界面，有删除按钮。参数为true时，无删除按钮
         */
        template: function (isCheck) {
            //如果是查看，没有删除按钮
            var template = '<div id="BinduploadflieWU_FILE_{0}" class="item">' +
                '<div class="nui-ico nui-ico-file32 nui-ico-file32-{1}"></div>' +
                '<div class="webuploadinfo">' +
                '<span title="{2}">{3}</span>' +
                '<div class="webupload-button">';
            if (!isCheck) {
                template += '<a><span class="webupload-delete" data-id="BinduploadflieWU_FILE_{0}">删除</span></a>';
            }
            template += '<a href="' + serviceUrl + downloadUrl + '?fileToken={4}&newName={2}" target="_blank"><span class="webupload-download">下载</span></a>' +
                '</div>' +
                '<div class="webuploadstate"><span class="file-token" data-filetoken="{4}"></span><span class="webupload-text">{5}</span></div>' +
                '</div></div>';
            return template;
        },
        /**
         * 图片上传模板
         * @param {} isCheck 是否是查看界面 true/false
         * @returns {} 参数为空时，默认不是查看界面，有删除功能。参数为true时，无删除功能
         */
        templateImg: function (isCheck) {
            var template = '<div id="BinduploadflieWU_FILE_{0}" class="webupload-list-img">' +
                '<img src="' + serviceUrl + downloadUrl + '?fileToken={2}">' +
                '<div class="webupload-list-img-cover">' +
                '<i class="fa fa-eye img-show" title="预览" data-id="BinduploadflieWU_FILE_{0}"></i>';
            if (!isCheck) {
                template += '<i class="fa fa-remove img-delete" title="删除" data-id="BinduploadflieWU_FILE_{0}"></i>';
            }
            template += '<div class="img-upload-state" style="display:none;"><span class="file-name" data-filename="{1}"></span><span class="file-token" data-filetoken="{2}"></span></div>' +
                '</div>' +
                '</div>';
            return template;

        },
        /**
         * 当图片没有时，会显示404图片
         * @param {} img
         * @returns {}
         */
        show404: function (img) {
            img.src = applicationPath + "/images/404.png";
            img.onerror = null;
        },
        /**
         *  编辑时，加载上传控件
         * @param {Object<>} options
         *  参数  | 默认值 | 说明
         * {
         *  elem '#upload-file'  上传控件id
         *  rows  []
         *  isCheck false 是否为查看   true/false
         *  uploadeFile  'file' 上传类型：'file' / 'img'
         * }参数:
         */
        loadFile: function (options) {
            var defaults = {
                elem: '#upload-files',
                rows: [],
                isCheck: false, //是否为查看
                uploadType: 'file' //绑定文件类型 file  或 img
            };
            options = $.extend(defaults, options);

            var rows = webuploader.getFiles(options.elem);

            if (rows && rows.length === 0) {
                rows = options.rows;
            }
            if (rows && rows.length > 0) {

                webuploader.getWebUpload(function () {
                    //未找到webuploader的控件加载成功后的回调事件，采用setTimeout()延迟执行
                    setTimeout(function () {
                        var html = '';
                        var uploadType = $(options.elem).data('uploadtype');
                        if (options.isCheck === true) {
                            html += '<div class="uploader-list">';
                            uploadType = options.uploadType;
                        }
                        $.each(rows, function (i, v) {
                            var subName = v.FileName;
                            if (subName.lastIndexOf('.') > 20) {
                                subName = subName.substring(0, 20);
                            } else {
                                subName = subName.substring(0, subName.lastIndexOf('.'));
                            }
                            var d = v.FileName;
                            var ext = d.substr(d.lastIndexOf('.') + 1, d.length);
                            subName += '.' + ext;

                            var limit = webuploader.bytesToSize(v.FileSize);
                            if (uploadType === 'file') {
                                //参数（id,文件后缀，文件名，截取后的文件名，fileToken,上传状态（文件大小+上传成功）)
                                html += webuploader.format(webuploader.template(options.isCheck), v.Id, ext.toLowerCase(), v.FileName, subName, v.FileToken, limit + "上传成功");
                            } else {
                                html += webuploader.format(webuploader.templateImg(options.isCheck), v.Id, v.FileName, v.FileToken);
                            }
                        });

                        if (options.isCheck === true) {
                            html += '</div>';
                            $(options.elem).append(html);
                            $(options.elem).find('.img-show').on('click',
                                function () {
                                    var $ele = $(this);
                                    webuploader.imagePreviewDialog($ele);
                                });

                        } else {
                            $(options.elem + ' div.uploader-list').append(html);
                        }
                    }, 400);
                });
            }
        },
        //<ul style="display:none;" id="abpfiles" >
        //@foreach(var item in Model.AbpFileOutput)
        //{
        //    <li data-filetoken="@item.FileToken" data-id="@item.Id" data-filesize="@item.FileSize">@item.FileName</li>
        //}
        //</ul>
        //上面的格式，会得到当前已上传的文件数组
        getFiles: function (elemId) {
            var abpfiles = [];
            $.each($(elemId + ' ul li'), function (i, v) {
                abpfiles.push({
                    Id: $(v).attr('data-id'),
                    FileToken: $(v).attr('data-filetoken'),
                    FileSize: $(v).attr('data-filesize'),
                    FileName: $(v).text()
                });
            });
            return abpfiles;
        },
        /**
         * 格式化字符串
         * @param {string} "{0}-{1}","a","b"
         * @returns a-b
         * @example  用法:
         webuploader.format("{0}-{1}","a","b");
         */
        format: function () {
            for (var i = 1; i < arguments.length; i++) {
                var exp = new RegExp('\\{' + (i - 1) + '\\}', 'gm');
                arguments[0] = arguments[0].replace(exp, arguments[i]);
            }
            return arguments[0];
        },
        /**
         * 判断变量是否为null或空
         * @example 用法:
         * webuploader.isNullOrEmpty(null)
         */
        isNullOrEmpty: function (str) {
            return str === undefined || str === null || str === "";
        },
        /**
         * 生成一个guid数据 (全局唯一标识)
         */
        GUID: function () {
            return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
        },
        /**
         * 1024MB转换成1GB
         * @param {number} bytes
         * @returns {} 返回最小不可转换的单位的空间大小
         */
        bytesToSize: function (bytes) {
            if (bytes === undefined || bytes === null || bytes === "") return "";
            if (bytes == 0) return '0B';
            var k = 1024;
            var sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
            var i = Math.floor(Math.log(bytes) / Math.log(k));
            return (bytes / Math.pow(k, i)).toFixed(2) + sizes[i];
        }
    });

    /**
     * 扩展方法：得到当前控件已经上传成功的文件地址，多文件，中间以*分割
     */
    $.fn.GetFilesAddress = function () {
        var filesAddress = [];
        if ($(this).data('uploadtype') === 'file') {
            $(this).find(".uploader-list  .webuploadstate .file-token").each(function () {
                filesAddress.push($(this).attr('data-filetoken'));
            });
        } else {
            $(this).find('.uploader-list .webupload-list-img .webupload-list-img-cover .img-upload-state span.file-token').each(function () {
                filesAddress.push($(this).attr('data-filetoken'));
            });
        }
        return filesAddress.join('*');
    }
    /**
     * 扩展方法：初始化上传文件
     * @param {Object<>} 参数为webuploader插件的所有参数
     * @param 新增参数:uploadType:'file'或'img' 上传类型：file为文件;img为图片，有上传预览效果
     */
    $.fn.powerWebUpload = function (options) {
        var ele = this;
        return initWebUpload(ele, options);
    }
})(jQuery, window);         /*函数内部有作用域,所以把代码包在一个即时函数中,防止对全局作用域造成污染*/