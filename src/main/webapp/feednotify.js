///<reference path="ref.ts" />
var CVFeed;
(function (CVFeed) {
    var Plugin = (function () {
        function Plugin() {
            this.onRate = new plus.Emitter();
            this.onReady = new plus.Emitter();
            this.stars = [];
        }
        Plugin.prototype.init = function (htmlElement) {
            var thethis = this;
            this.element = $(htmlElement);

            /*Các bước sử dụng metisMenu:
            1. load metisMenu.js
            2. tạo DOM
            3. gọi $(DOM).metisMenu({toggle:true});
            */
            //tạo barrier để đồng bộ
            var scriptloaded = new plus.Emitter();
            var layoutloaded = new plus.Emitter();
            var dataloaded = new plus.Emitter();

            plus.bar([scriptloaded, layoutloaded, dataloaded], function () {
                var dom = $(content);
                thethis.element.append(dom);
                thethis.button = dom;
                if (n != 0) {
                    thethis.wake();
                    dom.find('id_message').html(n.toString());
                }

                thethis.onReady.emit({});
            });

            plus.req([''], function () {
                scriptloaded.emit();
            });

            var content;

            //lấy layout
            $.get('feednotify.html', function (data) {
                content = data;
                layoutloaded.emit();
            });

            //lấy dữ liệu
            var n = 0;
            var errorlist = [];

            //==
            dataloaded.emit();
            return;

            //===
            $.post('HRWeb/feed/getnunread', function (data) {
                var data = JSON.parse(data);
                n = data.return;
                dataloaded.emit();
            });
        };

        Plugin.prototype.dim = function () {
            this.button.addClass('btn-danger');
        };

        Plugin.prototype.wake = function () {
            this.button.removeClass('btn-danger');
        };
        return Plugin;
    })();
    CVFeed.Plugin = Plugin;
})(CVFeed || (CVFeed = {}));
