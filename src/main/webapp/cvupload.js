///<reference path="ref.ts" />
var CVUpload;
(function (CVUpload) {
    var Plugin = (function () {
        function Plugin() {
            this.onReady = new plus.Emitter();
        }
        Plugin.prototype.htmlEscape = function (str) {
            return String(str).replace(/&/g, '&amp;').replace(/"/g, '&quot;').replace(/'/g, '&#39;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
        };

        Plugin.prototype.serialize = function (obj, prefix) {
            if (typeof prefix === "undefined") { prefix = undefined; }
            var thethis = this;
            var str = [];
            for (var p in obj) {
                var k = prefix ? prefix : p, v = obj[p];
                str.push(typeof v == "object" ? thethis.serialize(v, k) : encodeURIComponent(k) + "=" + encodeURIComponent(v));
            }
            return str.join("&");
        };

        Plugin.prototype.init = function (requestid) {
            var thethis = this;
            if (requestid == "bk")
                requestid = 7 + "";
            thethis.requestid = parseInt(requestid);

            //tạo barrier để đồng bộ
            var layoutloaded = new plus.Emitter();
            var dataloaded = new plus.Emitter();
            var dependloaded = new plus.Emitter();

            plus.bar([dependloaded, layoutloaded, dataloaded], function () {
                var capDiv;
                var requiredFieldsDiv;
                var testsubmitBt;

                //Phase 1: get test
                thethis.captcha.onReady.on(function () {
                    thethis.dom.find('.id_requestid').val(requestid.toString());

                    capDiv = thethis.dom.find('.id_captcha');
                    capDiv.append(thethis.captcha.dom);

                    thethis.phase0 = thethis.dom.find('.id_phase0');
                    thethis.phase1 = thethis.dom.find('.id_phase1');
                    thethis.phase2 = thethis.dom.find('.id_phase2');

                    thethis.dom.find('.id_req').html(req.requirement);
                    thethis.dom.find('.id_int').html(req.interest);
                    thethis.dom.find('.id_title').html(req.title);
                    thethis.dom.find('.id_jobdesc').html(req.jobdesc);
                    thethis.dom.find('.id_quan').html(req.totalcv);

                    document.getElementsByClassName('id_applybtn')[0]['onclick'] = function () {
                        thethis.dom.find('.id_apply')[0].scrollIntoView();
                        thethis.dom.find('.id_apply').trigger('click');
                    };

                    var reqtime = new Date(req.ctime);
                    var curtime = new Date();

                    var deltat = (curtime.getTime() - reqtime.getTime()) / 1000;

                    var u = "giây";
                    if (deltat < 60) {
                    } else if (deltat < 3600) {
                        u = "phút";
                        deltat /= 60;
                    } else if (deltat < 86400) {
                        u = "giờ";
                        deltat /= 3600;
                    } else {
                        u = "ngày";
                        deltat /= 86400;
                    }
                    deltat = Math.ceil(deltat);
                    thethis.dom.find('.id_date').html(deltat + " " + u);
                    thethis.dom.find('.id_apply').on('click', function () {
                        thethis.dom.find('.id_apply').hide();
                        thethis.phase1.removeClass('hidden');
                    });

                    requiredFieldsDiv = thethis.dom.find('.id_requiredFields');
                    testsubmitBt = thethis.dom.find('.id_cvsubmit');
                    thethis.nameTb = thethis.dom.find('.id_name');
                    thethis.emailTb = thethis.dom.find('.id_email');
                    testsubmitBt.on('click', function () {
                        t['submitcv']();
                    });

                    for (var i in thethis.requiredFieldsNames) {
                        requiredFieldsDiv.append($('<br/><p>' + thethis.htmlEscape(thethis.requiredFieldsNames[i].title) + '</p>'));
                        requiredFieldsDiv.append(thethis.requiredFields[thethis.requiredFieldsNames[i].id]);
                    }
                });
                thethis.captcha.init();
            });

            //Khởi tạo các control phụ thuộc
            plus.req(['captcha.js'], function () {
                thethis.captcha = new Captcha.Plugin();
                dependloaded.emit();
            });
            var t = {};

            //lấy layout
            $.get('cvupload.html', function (data) {
                thethis.dom = $(data);
                thethis.onReady.emit();

                t['submitcv'] = function () {
                    var cap = thethis.captcha.getAnswer();

                    var fields = [];
                    var infos = [];
                    for (var i in thethis.requiredFieldsNames) {
                        fields.push(thethis.requiredFieldsNames[i].id);
                        infos.push(thethis.requiredFields[thethis.requiredFieldsNames[i].id].val());
                    }

                    var data = {
                        cap: cap.answer,
                        capid: cap.id,
                        field: fields,
                        info: infos,
                        email: thethis.emailTb.val(),
                        name: thethis.nameTb.val(),
                        request: thethis.requestid
                    };

                    var missing = false;
                    for (var t in infos) {
                        if (infos[i] == "")
                            missing = true;
                    }

                    if (thethis.emailTb.val() == "" || thethis.nameTb.val() == "")
                        missing = true;

                    if (missing == true) {
                        alert("bạn chưa nhập đầy đủ thông tin");
                        return false;
                    }
                    var formData;
                    if (thethis.dom.find('.id_optiondoccv').is(":checked")) {
                        var form = thethis.dom.find('.id_docform')[0];
                        if (thethis.dom.find('.id_filecv').val() == false) {
                            alert("bạn chưa nhập đầy đủ thông tin");
                            return false;
                        }
                        formData = new FormData(form);
                        data['type'] = "file";
                    } else {
                        data['type'] = "link";
                        var link = thethis.dom.find('.id_linkcv').val();
                        if (link == undefined || link == "") {
                            alert("bạn chưa nhập đầy đủ thông tin");
                            return false;
                        }
                        formData = null; //new FormData(null);
                        data['link'] = link;
                    }

                    plus['wait'].emit();

                    plus.upload('HRWeb/cv/upload?' + thethis.serialize(data), formData, function (data) {
                        //------------PHASE 2---------------
                        plus['ready'].emit();
                        thethis.phase0.addClass('hidden');
                        thethis.phase1.addClass('hidden');
                        thethis.phase2.removeClass('hidden');
                    }, function (e) {
                        thethis.captcha.regen();
                        plus['ready'].emit();
                        alert('Không thể gửi CV');
                    }, function (p) {
                        plus['change'].emit({ progress: p });
                    });

                    return false;
                };

                var filecv = thethis.dom.find('.id_filecv');
                var linkcv = thethis.dom.find('.id_linkcv');

                thethis.dom.find('.id_optiondoccv').click(function () {
                    res.removeClass('fa-exclamation');
                    res.removeClass('fa-check');
                    linkcv.attr('disabled', true);
                    filecv.attr('disabled', false);
                });

                thethis.dom.find('.id_optionlinkcv').click(function () {
                    linkcv.trigger('keyup');
                    linkcv.attr('disabled', false);
                    filecv.attr('disabled', true);
                });

                var res = thethis.dom.find('.id_linkcvres');

                linkcv.keyup(function () {
                    if (thethis.isvalidurl(linkcv.val())) {
                        res.removeClass('fa-exclamation');
                        res.addClass('fa-check');
                        res.css('color', 'green');
                    } else {
                        res.css('color', 'red');
                        res.addClass('fa-exclamation');
                        res.removeClass('fa-check');
                    }
                });
                layoutloaded.emit();
            });

            var req;
            $.get('HRWeb/request/get2', { id: requestid }, function (data) {
                req = JSON.parse(data);
                $.get('HRWeb/requiredfield/list2', { request: requestid }, function (data) {
                    thethis.requiredFieldsNames = JSON.parse(data);
                    thethis.requiredFields = {};
                    for (var i in thethis.requiredFieldsNames) {
                        thethis.requiredFields[thethis.requiredFieldsNames[i].id] = $('<textarea class="form-control" name="fields" rows="3" type="text"/>');
                    }
                    dataloaded.emit();
                });
            })['fail'](function () {
                alert('the request is not exist or locked');
            });
        };

        Plugin.prototype.isvalidurl = function (url) {
            return url.match(/^(ht|f)tps?:\/\/[a-z0-9-\.]+\.[a-z]{2,4}\/?([^\s<>\#%"\,\{\}\\|\\\^\[\]`]+)?$/);
        };
        return Plugin;
    })();
    CVUpload.Plugin = Plugin;
})(CVUpload || (CVUpload = {}));
