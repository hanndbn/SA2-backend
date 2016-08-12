///<reference path="ref.ts" />
var BagPanel;
(function (BagPanel) {
    var Plugin = (function () {
        function Plugin() {
            this.onAdd = new plus.Emitter();
            this.onDeleted = new plus.Emitter();
            this.onMoved = new plus.Emitter();
            this.onSelected = new plus.Emitter();
            this.onReady = new plus.Emitter();
            this.onEdit = new plus.Emitter();
            this.bags = [];
        }
        Plugin.prototype.disable = function () {
            this.dom.css('opacity', 0.2);
        };

        Plugin.prototype.enable = function () {
            this.dom.css('opacity', 1);
        };

        Plugin.prototype.init = function () {
            var thethis = this;
            var layoutloaded = new plus.Emitter();
            var dependloaded = new plus.Emitter();

            plus.bar([layoutloaded, dependloaded], function () {
                thethis.theaddnewBag = thethis.dom.find('.id_addnew');
                thethis.thedefaultBag = thethis.dom.find('.id_hiddenbag');

                thethis.containerDiv = thethis.dom.find('.id_bagcontainer');

                thethis.theaddnewBag.on('click', function () {
                    thethis.onAdd.emit();
                });

                thethis.onReady.emit({});
            });

            $.get('bagpanel.html', function (data) {
                thethis.dom = $(data);
                layoutloaded.emit();
            })['fail'](function (error) {
                console.error(error);
                thethis.dom.css('background-color', '#ff0000');
            });

            plus.req(['textbox.js'], function () {
                dependloaded.emit();
            });
        };

        Plugin.prototype.displayText = function (id) {
            for (var i in this.bags) {
                if (this.bags[i]['__attach'].id == id) {
                    this.bags[i]['__nametb'].displayText();
                }
            }
        };

        Plugin.prototype.clear = function () {
            for (var i in this.bags) {
                this.bags[i].hide();
            }
            this.bags = [];
        };

        Plugin.prototype.getFocusedBag = function () {
            return this.dom.find('.focusedBag')['__attach'];
        };

        Plugin.prototype.focus = function (no) {
            if (typeof no === "undefined") { no = 0; }
            var child = this.containerDiv.children().eq(no);
            child.trigger('click');
        };

        Plugin.prototype.changeN = function (bag, n) {
            bag.find('.id_n').html(n.toString());
        };

        Plugin.prototype.addBag = function (name, text, id, attach) {
            var thethis = this;
            var me = this;
            var bag = this.thedefaultBag.clone();
            bag['__attach'] = attach;
            bag.removeClass('id_hiddenbag');
            var nametbDiv = bag.find('.id_name');

            var nametb = new TextBox.Plugin();
            nametb.onChanged.on(function () {
                thethis.onEdit.emit({ dom: bag, attach: attach, name: nametb.text });
            });

            nametb.onReady.on(function () {
                nametb.displayText(name);
                nametbDiv.append(nametb.dom);
            });
            bag['__nametb'] = nametb;
            nametb.init();

            me.theaddnewBag.before(bag);
            bag.find('.close').on('click', function (e) {
                me.onDeleted.emit({ dom: bag, attach: attach });
            });

            bag.on('click', function (e) {
                if (me.focused == bag)
                    return;
                if (me.focused !== undefined) {
                    me.focused.removeClass('focusedBag');
                }
                bag.addClass('focusedBag');
                me.focused = bag;
                me.onSelected.emit({ dom: bag, attach: attach });
                e.stopPropagation();
            });

            bag.on('dragover', function (e) {
                e.preventDefault();
                e.stopPropagation();
                bag.addClass('draggingBag');
            });

            bag.on('dragleave', function (e) {
                e.preventDefault();
                e.stopPropagation();
                bag.removeClass('draggingBag');
            });

            bag[0].addEventListener('drop', function (event) {
                event.preventDefault();
                event.stopPropagation();
                me.onMoved.emit({ dom: bag, attach: attach, data: event.dataTransfer.getData('cv') });
            });
            this.bags.push(bag);
            return bag;
        };
        return Plugin;
    })();
    BagPanel.Plugin = Plugin;
})(BagPanel || (BagPanel = {}));
