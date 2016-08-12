///<reference path="ref.ts" />
var NewQuestion;
(function (NewQuestion) {
    var Plugin = (function () {
        function Plugin() {
            this.onReady = new plus.Emitter();
            this.onCreated = new plus.Emitter();
            this.onDoneEdit = new plus.Emitter();
        }
        Plugin.prototype.init = function (htmlElement, unit, type) {
            this.type = type;
            this.unit = unit;
            var thethis = this;
            this.element = $(htmlElement);

            //tạo barrier để đồng bộ
            var layoutloaded = new plus.Emitter();

            //var dataloaded = new plus.Emitter();
            var dependloaded = new plus.Emitter();

            var dom;
            plus.bar([layoutloaded, dependloaded], function () {
                thethis.contenteditor = new TextEditor.Plugin();

                plus.bar([thethis.contenteditor.onReady], function () {
                    thethis.element.append(dom);
                    thethis.saveBt = dom.find('.id_save');
                    var ccontent = thethis.ccontent = dom.find('.id_content');
                    ccontent.append(thethis.contenteditor.dom);
                    var cchoose = thethis.cchoose = dom.find('.id_choose');
                    var cweight = thethis.cweight = dom.find('.id_weight');
                    var clear = dom.find('.id_clear');
                    function clr() {
                        thethis.contenteditor.setCode("");
                        cchoose.val("");
                        cweight.val("");
                    }
                    clear.on('click', clr);

                    thethis.saveBt.on('click', function () {
                        var choose = cchoose.val().trim().split('\n').join('\\n');

                        if (thethis.contenteditor.getCode() == "" || choose == "") {
                            alert("Dữ liệu không hợp lệ, bạn đã nhập thiếu một trường nào đó");
                            return;
                        }

                        if (thethis.isnew) {
                            $.post('HRWeb/question/create', {
                                unit: thethis.unit,
                                type: thethis.type,
                                content: thethis.contenteditor.getCode(),
                                answer: 1,
                                choose: choose,
                                weight: cweight.val(),
                                isdraf: false
                            }, function (data) {
                                var id = JSON.parse(data).result;
                                dom['modal']('hide');
                                clr();
                                thethis.onCreated.emit({ id: id });
                            })['fail'](function () {
                                alert('Dữ liệu sai hoặc không thể kết nối được với server');
                            });
                            ;
                        } else {
                            $.post('HRWeb/question/edit', {
                                id: thethis.q.qid,
                                type: thethis.type,
                                content: thethis.contenteditor.getCode(),
                                choose: choose,
                                weight: cweight.val()
                            }, function () {
                                dom['modal']('hide');
                                thethis.onDoneEdit.emit({ id: thethis.q.qid, dom: thethis.q['dom'] });
                                clr();
                            })['fail'](function () {
                                alert('Dữ liệu sai hoặc không thể kết nối được với server');
                            });
                        }
                    });
                    thethis.modal = dom;
                    thethis.onReady.emit();
                });
                thethis.contenteditor.init();
            });

            plus.req(['texteditor.js'], function () {
                dependloaded.emit();
            });

            //lấy layout
            $.get('new_question.html', function (data) {
                dom = $(data);
                layoutloaded.emit();
            });
        };

        Plugin.prototype.show = function (isnew, q) {
            if (typeof isnew === "undefined") { isnew = true; }
            this.q = q;
            this.isnew = isnew;
            if (q !== undefined) {
                this.contenteditor.setCode(q.content);
                if (q.choose == undefined || q.choose == null)
                    q.choose = "";
                var choose = q.choose.trim().split('\\n').join('\n');
                this.cchoose.val(choose);
                this.cweight.val(q.weight);
            }
            this.modal['modal']('show');
        };
        return Plugin;
    })();
    NewQuestion.Plugin = Plugin;
})(NewQuestion || (NewQuestion = {}));
