///<reference path="ref.ts" />
var Pagination;
(function (Pagination) {
    var Plugin = (function () {
        function Plugin() {
            this.onReady = new plus.Emitter();
            this.onNext = new plus.Emitter();
            this.onPrev = new plus.Emitter();
            this.onNavigate = new plus.Emitter();
            this.n = 0;
        }
        Plugin.prototype.setI = function (i) {
            if (i > this.n)
                i = this.n;
            this.i = i;
            this.index.val(i.toString());
        };

        Plugin.prototype.navigate = function (i) {
            if (i > this.n || i < 1)
                return;

            this.i = i;
            this.index.val(i.toString());
            this.prevbt.prop('disabled', false);
            if (i == 1) {
                this.prevbt.prop('disabled', true);
            }

            this.prevbt.prop('disabled', false);
            if (i == this.n) {
                this.nextbt.prop('disabled', true);
            } else {
                this.nextbt.prop('disabled', false);
                this.prevbt.prop('disabled', false);
            }
            this.onNavigate.emit({ index: this.i });
        };

        Plugin.prototype.setN = function (n) {
            this.n = n;
            this.total.html(n.toString());
        };

        Plugin.prototype.init = function (n) {
            if (typeof n === "undefined") { n = 1; }
            this.n = n;
            var thethis = this;
            var layoutloaded = new plus.Emitter();
            plus.bar([layoutloaded], function () {
                thethis.index = thethis.dom.find('.id_index');
                thethis.nextbt = thethis.dom.find('.id_next');
                thethis.prevbt = thethis.dom.find('.id_prev');
                thethis.total = thethis.dom.find('.id_total');

                thethis.nextbt.on('click', function () {
                    thethis.onNext.emit();
                    thethis.navigate(thethis.i + 1);
                });

                thethis.prevbt.on('click', function () {
                    thethis.onPrev.emit();

                    thethis.navigate(thethis.i - 1);
                });

                thethis.index.on('keyup', function (e) {
                    if (e.keyCode == 13)
                        thethis.navigate(thethis.index.val());
                });
                thethis.onReady.emit();
            });

            //lấy layout
            $.get('pagination.html', function (data) {
                thethis.dom = $(data);
                layoutloaded.emit();
            });
        };
        return Plugin;
    })();
    Pagination.Plugin = Plugin;
})(Pagination || (Pagination = {}));
