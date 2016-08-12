///<reference path="ref.ts" />
var NewRequest;
(function (NewRequest) {
    var Plugin = (function () {
        function Plugin() {
            this.onEdit = new plus.Emitter();
            this.onReady = new plus.Emitter();
            this.onNew = new plus.Emitter();
            this.isnew = false;
        }
        Plugin.prototype.init = function (htmlElement, unit) {
            this.unit = unit;
            var thethis = this;
            this.element = $(htmlElement);

            //tạo barrier để đồng bộ
            var layoutloaded = new plus.Emitter();

            //var dataloaded = new plus.Emitter();
            var dependloaded = new plus.Emitter();

            var dom;
            plus.bar([layoutloaded, dependloaded], function () {
                thethis.element.append(dom);
                thethis.saveBt = dom.find('.id_save');
                thethis.ctitle = dom.find('.id_titleTb');
                thethis.cquantity = dom.find('.id_quantity');
                var cjobdesc = dom.find('.id_jobdesc');
                var cinterest = dom.find('.id_inte');
                var crequirement = dom.find('.id_req');

                thethis.jobdescE = new TextEditor.Plugin();
                thethis.inteE = new TextEditor.Plugin();
                thethis.reqE = new TextEditor.Plugin();

                plus.bar([thethis.jobdescE.onReady, thethis.inteE.onReady, thethis.reqE.onReady], function () {
                    cjobdesc.append(thethis.jobdescE.dom);
                    cinterest.append(thethis.inteE.dom);
                    crequirement.append(thethis.reqE.dom);

                    var clear = dom.find('.id_clear');
                    function clr() {
                        thethis.ctitle.val("");
                        thethis.cquantity.val("");
                        thethis.inteE.setCode("");
                        thethis.jobdescE.setCode("");
                        thethis.reqE.setCode("");
                    }
                    clear.on('click', clr);

                    thethis.saveBt.on('click', function () {
                        if (thethis.ctitle.val() == "" || thethis.jobdescE.getCode() == "") {
                            alert("Dữ liệu không hợp lệ, bạn đã nhập thiếu một trường nào đó");
                            return;
                        }

                        if (thethis.isnew) {
                            $.post('HRWeb/request/create', {
                                unit: thethis.unit,
                                title: thethis.ctitle.val(),
                                quantity: thethis.cquantity.val(),
                                jobdesc: thethis.jobdescE.getCode(),
                                endtime: "",
                                position: "",
                                interest: thethis.inteE.getCode(),
                                requirement: thethis.reqE.getCode(),
                                starttime: new Date().toString()
                            }, function (data) {
                                var id = JSON.parse(data).result;
                                dom['modal']('hide');
                                clr();
                                thethis.onNew.emit({ id: id });
                            })['fail'](function () {
                                alert('please try again');
                            });
                        } else {
                            $.post('HRWeb/request/edit', {
                                id: thethis.r.id,
                                title: thethis.ctitle.val(),
                                quantity: thethis.cquantity.val(),
                                jobdesc: thethis.jobdescE.getCode(),
                                endtime: "",
                                position: "",
                                interest: thethis.inteE.getCode(),
                                requirement: thethis.reqE.getCode(),
                                starttime: new Date().toString()
                            }, function (data) {
                                dom['modal']('hide');
                                thethis.onEdit.emit({ id: thethis.r.id, dom: thethis.r['_dom'] });
                                clr();
                            })['fail'](function () {
                                alert('please try again');
                            });
                        }
                    });
                    thethis.modal = dom;
                    thethis.onReady.emit();
                });
                thethis.jobdescE.init();
                thethis.inteE.init();
                thethis.reqE.init();
            });

            //lấy layout
            $.get('new_request.html', function (data) {
                dom = $(data);
                layoutloaded.emit();
            });

            plus.req(['texteditor.js'], function () {
                dependloaded.emit();
            });
        };

        Plugin.prototype.show = function (isnew, r) {
            if (typeof isnew === "undefined") { isnew = true; }
            this.r = r;
            this.isnew = isnew;
            if (r !== undefined) {
                this.ctitle.val(r.title);
                this.cquantity.val(r.quantity);
                this.jobdescE.setCode(r.jobdesc);
                this.reqE.setCode(r.requirement);
                this.inteE.setCode(r.interest);
            }
            this.modal['modal']('show');
        };
        return Plugin;
    })();
    NewRequest.Plugin = Plugin;
})(NewRequest || (NewRequest = {}));
