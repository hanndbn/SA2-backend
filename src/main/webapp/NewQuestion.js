///<reference path="ref.ts" />
var NewQuestion;
(function (NewQuestion) {
    var Plugin = (function () {
        function Plugin() {
            this.onReady = new plus.Emitter();
        }
        Plugin.prototype.init = function (htmlElement) {
            var thethis = this;
            this.element = $(htmlElement);

            //tạo barrier để đồng bộ
            var layoutloaded = new plus.Emitter();

            //var dataloaded = new plus.Emitter();
            //var dependloaded = new plus.Emitter();
            var dom;
            plus.bar([layoutloaded], function () {
                thethis.element.append(dom);

                thethis.saveBt = dom.find('.id_save');
                var ccontent = dom.find('.id_content');
                var canswer = dom.find('.id_answer');
                var cchoose = dom.find('.id_choose');
                var cweight = dom.find('.id_weight');
                var cisdraf = dom.find('.id_isdraf');

                var clear = dom.find('.id_clear');
                function clr() {
                    ccontent.val("");
                    canswer.val("");
                    cchoose.val("");
                    cweight.val("");
                    cisdraf.val("");
                }
                clear.on('click', clr);

                thethis.saveBt.on('click', function () {
                    $.post('/HRWeb/HRWeb/question/create', {
                        content: ccontent.val(),
                        answer: canswer.val(),
                        cchoose: cchoose.val(),
                        cweight: cweight.val(),
                        cisdraf: cisdraf.val()
                    });
                    dom['modal']('hide');
                    clr();
                });
                thethis.modal = dom;
                thethis.onReady.emit();
            });

            //lấy layout
            $.get('new_question.html', function (data) {
                dom = $(data);
                layoutloaded.emit();
            });
        };

        Plugin.prototype.show = function () {
            this.modal['modal']('show');
        };
        return Plugin;
    })();
    NewQuestion.Plugin = Plugin;
})(NewQuestion || (NewQuestion = {}));
