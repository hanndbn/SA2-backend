///<reference path="ref.ts" />
var Question;
(function (Question) {
    var Plugin = (function () {
        function Plugin() {
            this.onReady = new plus.Emitter();
            this.no = 0;
        }
        Plugin.prototype.init = function (unit, type) {
            var thethis = this;
            this.unit = unit;
            this.type = type;

            //tạo barrier để đồng bộ
            var layoutloaded = new plus.Emitter();

            //var dataloaded = new plus.Emitter();
            var dependloaded = new plus.Emitter();

            plus.bar([dependloaded, layoutloaded], function () {
                //Sau khi các plugin đã load xong
                plus.bar([thethis.table.onReady, thethis.pagi.onReady, thethis.new_question.onReady], function () {
                    thethis.dom.find('.id_pagi').append(thethis.pagi.dom);
                    thethis.dom.find('.id_table').append(thethis.table.dom);
                    thethis.pagi.setI(0);
                    thethis.pagi.setN(Math.ceil(numberofquestion / 20));
                    thethis.pagi.onNavigate.on(function (data) {
                        thethis.loadData(data.index);
                    });

                    thethis.table.setUpTable(['#', 'Nội dung câu hỏi', 'Độ khó', 'Sử dụng', 'Khác']);
                    thethis.loadData(1);
                    thethis.onReady.emit();
                });
                thethis.pagi.init();
                thethis.table.init();
                thethis.new_question.onCreated.on(function (param) {
                    $.get('HRWeb/question/get', { id: param.id }, function (data) {
                        var q = JSON.parse(data);
                        thethis.addQuestion(q);
                    });
                });
                thethis.new_question.init(thethis.dom.find('.id_newmodal')[0], unit, type);
            });

            var numberofquestion;

            //Khởi tạo các control phụ thuộc
            plus.req(['table.js', 'pagination.js', 'actionmenu.js', 'new_question.js', 'switchbox.js'], function () {
                thethis.table = new Table.Plugin();
                thethis.pagi = new Pagination.Plugin();
                thethis.new_question = new NewQuestion.Plugin();
                $.get('HRWeb/question/count', { unit: unit, type: type }, function (data) {
                    numberofquestion = JSON.parse(data).result;
                    dependloaded.emit();
                });
            });

            //lấy layout
            $.get('question.html', function (data) {
                thethis.dom = $(data);
                thethis.dom.find('.id_new').on('click', function () {
                    thethis.new_question.show();
                });

                var importmodal = thethis.dom.find('.id_importmodal');
                thethis.dom.find('.id_import').click(function () {
                    importmodal['modal']('show');
                });

                var querystring = {
                    type: thethis.type,
                    unit: thethis.unit
                };

                importmodal.find('.id_save').click(function () {
                    importmodal['modal']('hide');
                    plus['wait'].emit();
                    var form = importmodal.find('.id_importform')[0];
                    var formData = new FormData(form);

                    $.ajax({
                        url: 'HRWeb/question/import?' + thethis.serialize(querystring),
                        type: 'POST',
                        data: formData,
                        async: true,
                        success: function (data) {
                            //------------PHASE 2---------------
                            plus['ready'].emit();
                            window.location.reload();
                        },
                        cache: false,
                        contentType: false,
                        xhr: function () {
                            // get the native XmlHttpRequest object
                            var xhr = $['ajaxSettings'].xhr();

                            // set the onprogress event handler
                            xhr.upload.onprogress = function (evt) {
                                plus['change'].emit({ progress: evt.loaded / evt.total * 100 });
                            };

                            //	xhr.upload.onload = function(){ console.log('DONE!') } ;
                            return xhr;
                        },
                        processData: false
                    })['fail'](function (e) {
                        plus['ready'].emit();
                        alert('Không thể import cau hoi');
                    });
                });

                thethis.dom.find('.id_export').attr('href', 'HRWeb/question/export?' + thethis.serialize(querystring));

                layoutloaded.emit();
            });
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

        Plugin.prototype.addQuestion = function (q) {
            var thethis = this;
            var acm = new ActionMenu.Plugin();
            var stateswitch = new SwitchBox.Plugin();

            thethis.no++;
            q['no'] = thethis.no;
            thethis.table.addRow(q['no'], {});

            plus.bar([acm.onReady, stateswitch.onReady], function () {
                if (q.isdraf == true)
                    stateswitch.uncheck();
                else
                    stateswitch.check();
                stateswitch.onChecked.on(function () {
                    $.post('HRWeb/question/edit', { id: q.qid, isdraf: false })['fail'](function () {
                        alert("can't change question's state");
                    });
                });

                stateswitch.onUnchecked.on(function () {
                    $.post('HRWeb/question/edit', { id: q.qid, isdraf: true })['fail'](function () {
                        alert("can't change question's state");
                    });
                });

                acm.addItem('Chi tiết / Sửa', 'icon-pencil2', function () {
                    thethis.new_question.show(false, q);
                });

                var hand = thethis.new_question.onDoneEdit.on(function (param) {
                    if (param.id != q.qid)
                        return;
                    var e = param.dom;
                    thethis.new_question.onDoneEdit.de(hand);
                    $.get('HRWeb/question/get', { id: param.id }, function (data) {
                        thethis.table.deleteRow(q['no']);
                        q = JSON.parse(data);
                        thethis.addQuestion(q);
                    });
                });

                acm.addItem('Xóa', 'icon-x', function () {
                    if (!confirm("Click <OK> to delete.")) {
                        return;
                    }

                    $.post('HRWeb/question/delete', { id: q.qid }, function () {
                        row.hide(200);
                        row.remove();
                    })['fail'](function () {
                        alert("can't delete question");
                    });
                });

                var row = thethis.table.editRow(q['no'], {
                    0: q['no'],
                    10: q.content,
                    50: q.weight,
                    60: stateswitch.dom,
                    80: acm.dom
                });

                row.hide();
                q['dom'] = row;
                row.fadeIn();
            });
            acm.init();
            stateswitch.init();
        };

        Plugin.prototype.loadData = function (ind) {
            this.pagi.setI(ind);
            var thethis = this;
            thethis.no = 0;
            plus['wait'].emit();

            //this.table.dom.prop('disabled', true);
            $.get('HRWeb/question/list', { unit: this.unit, p: ind - 1, ps: 20, type: this.type }, function (data) {
                thethis.table.clear();
                var questionlist = JSON.parse(data);
                for (var i in questionlist) {
                    thethis.addQuestion(questionlist[i]);
                }
                plus['ready'].emit();
                //thethis.table.dom.prop('disabled', false);
            });
        };
        return Plugin;
    })();
    Question.Plugin = Plugin;
})(Question || (Question = {}));
