///<reference path="ref.ts" />
var BagPanel;
(function (BagPanel) {
    var Plugin = (function () {
        function Plugin() {
            this.onDeleted = new plus.Emitter();
            this.onMoved = new plus.Emitter();
            this.onSelected = new plus.Emitter();
            this.onReady = new plus.Emitter();
        }
        Plugin.prototype.init = function () {
            var thethis = this;

            $.get('bagpannel.html', function (data) {
                thethis.dom = $(data);

                thethis.theaddnewBag = thethis.dom.find('.id_addnew');
                thethis.thedefaultBag = thethis.dom.find('.id_hiddenbag');

                thethis.containerDiv = thethis.dom.find('.id_bagcontainer');

                thethis.onReady.emit({});
            })['fail'](function (error) {
                console.error(error);
                thethis.dom.css('background-color', '#ff0000');
            });
        };

        Plugin.prototype.focus = function (no) {
            if (typeof no === "undefined") { no = 0; }
            var child = this.containerDiv.children().eq(no);
            child.trigger('click');
        };

        Plugin.prototype.addBag = function (name, text, id, attach) {
            var me = this;
            var bag = this.thedefaultBag.clone();
            bag.removeClass('id_hiddenbag');
            bag.find('.id_name').html(text);
            me.theaddnewBag.before(bag);
            bag.find('.close').on('click', function () {
                me.onDeleted.emit({ dom: bag, attach: attach });
            });

            bag.on('click', function () {
                me.focused.removeClass('focusedBag');
                me.focused = bag;
                bag.addClass('focusedBag');

                me.onSelected.emit({ dom: bag, attach: attach });
            });

            bag.on('dragover', function () {
                event.preventDefault();
                event.stopPropagation();
                bag.addClass('draggingBag');
            });

            bag.on('dragleave', function () {
                event.preventDefault();
                event.stopPropagation();
                bag.removeClass('draggingBag');
            });

            bag.on('drop', function (event) {
                event.preventDefault();
                event.stopPropagation();

                me.onMoved.emit({ dom: bag, attach: attach, data: event.dataTransfer.getData('cv') });
            });

            return bag;
        };
        return Plugin;
    })();
    BagPanel.Plugin = Plugin;
})(BagPanel || (BagPanel = {}));
