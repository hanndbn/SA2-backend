///<reference path="ref.ts" />
var Mail;
(function (Mail) {
    var Plugin = (function () {
        function Plugin() {
            this.onReady = new plus.Emitter();
        }
        Plugin.prototype.init = function (unit) {
            var thethis = this;

            //tạo barrier để đồng bộ
            var layoutloaded = new plus.Emitter();
            var dataloaded = new plus.Emitter();
            var dependloaded = new plus.Emitter();

            plus.bar([layoutloaded, dataloaded, dependloaded], function () {
                var rcid;
                var trid;
                var rjid;
                var tid;

                var d1 = thethis.dom.find('.id_rctitle');
                var b1 = thethis.dom.find('.id_rjtitle');
                var a1 = thethis.dom.find('.id_trtitle');
                var c1 = thethis.dom.find('.id_ttitle');

                //var rctitle: JQuery = thethis.dom.find('.id_rctitle');
                //var rjtitle: JQuery = thethis.dom.find('.id_rjtitle');
                //var trtitle: JQuery = thethis.dom.find('.id_trtitle');
                //var ttitle: JQuery = thethis.dom.find('.id_ttitle');
                var rcbody = thethis.dom.find('.id_rcbody');
                var tbody = thethis.dom.find('.id_tbody');
                var rjbody = thethis.dom.find('.id_rjbody');
                var trbody = thethis.dom.find('.id_trbody');

                var tsignature = thethis.dom.find('.id_tsignature');
                var trsignature = thethis.dom.find('.id_trsignature');
                var rcsignature = thethis.dom.find('.id_rcsignature');
                var rjsignature = thethis.dom.find('.id_rjsignature');

                var savebt = thethis.dom.find('.id_save');

                plus.bar([a2.onReady, a3.onReady, b2.onReady, b3.onReady, c2.onReady, c3.onReady, d2.onReady, d3.onReady], function () {
                    trbody.append(a2.dom);

                    //trtitle.append(a1.dom);
                    trsignature.append(a3.dom);

                    rjsignature.append(b3.dom);

                    //rjtitle.append(b1.dom);
                    rjbody.append(b2.dom);

                    tsignature.append(c3.dom);

                    //ttitle.append(c1.dom);
                    tbody.append(c2.dom);

                    //					rctitle.append(d1.dom);
                    rcbody.append(d2.dom);
                    rcsignature.append(d3.dom);

                    for (var i in formlist) {
                        var form = formlist[i];

                        //receive mail
                        if (form.type == 1) {
                            rcid = form.id;
                            d1.val(form.title);
                            d2.setCode(form.body);
                            d3.setCode(form.signature);
                        } else if (form.type == 2) {
                            trid = form.id;
                            a1.val(form.title);
                            a2.setCode(form.body);
                            a3.setCode(form.signature);
                        } else if (form.type == 3) {
                            tid = form.id;
                            c1.val(form.title);
                            c2.setCode(form.body);
                            c3.setCode(form.signature);
                        } else if (form.type == 4) {
                            rjid = form.id;
                            b1.val(form.title);
                            b2.setCode(form.body);
                            b3.setCode(form.signature);
                        }
                    }
                });

                //	a1.init();
                //	b1.init();
                //	c1.init();
                //	d1.init();
                a2.init();
                b2.init();
                c2.init();
                d2.init();
                a3.init();
                b3.init();
                c3.init();
                d3.init();

                savebt.on('click', function () {
                    savebt.prop("disabled", true);
                    $.post('HRWeb/emailform/edit', { id: rcid, title: d1.val(), body: d2.getCode(), signature: d3.getCode() }, function () {
                        $.post('HRWeb/emailform/edit', { id: trid, title: a1.val(), body: a2.getCode(), signature: a3.getCode() }, function () {
                            $.post('HRWeb/emailform/edit', { id: rjid, title: b1.val(), body: b2.getCode(), signature: b3.getCode() }, function () {
                                $.post('HRWeb/emailform/edit', { id: tid, title: c1.val(), body: c2.getCode(), signature: c3.getCode() }, function () {
                                    savebt.prop("disabled", false);
                                })['fail'](function () {
                                    alert('can\'s save email form');
                                });
                            })['fail'](function () {
                                alert('can\'s save email form');
                            });
                        })['fail'](function () {
                            alert('can\'s save email form');
                        });
                    })['fail'](function () {
                        alert('can\'s save email form');
                    });
                });
            });

            //lấy layout
            $.get('mail.html', function (data) {
                thethis.dom = $(data);
                thethis.onReady.emit();
                layoutloaded.emit();
            });

            //lấy dữ liệu
            var n;
            var formlist;
            $.get('HRWeb/emailform/list', { unit: unit }, function (data) {
                formlist = JSON.parse(data);
                dataloaded.emit();
            });

            var a1, a2, a3, b1, b2, b3, c1, c2, c3;
            var d1, d2, d3;
            plus.req(['texteditor.js'], function () {
                //a1 = new TextEditor.Plugin();
                //b1 = new TextEditor.Plugin();
                //c1 = new TextEditor.Plugin();
                //d1 = new TextEditor.Plugin();
                a2 = new TextEditor.Plugin();
                b2 = new TextEditor.Plugin();
                c2 = new TextEditor.Plugin();
                d2 = new TextEditor.Plugin();
                a3 = new TextEditor.Plugin();
                b3 = new TextEditor.Plugin();
                c3 = new TextEditor.Plugin();
                d3 = new TextEditor.Plugin();
                dependloaded.emit();
            });
        };
        return Plugin;
    })();
    Mail.Plugin = Plugin;
})(Mail || (Mail = {}));
