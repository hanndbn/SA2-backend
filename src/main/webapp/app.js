///<reference path="ref.ts" />
var AppLayout;
(function (AppLayout) {
    var Layout = (function () {
        function Layout() {
            this.onReady = new plus.Emitter();
        }
        Layout.prototype.init = function () {
            var thethis = this;

            //this.dom = $('<div></div>');
            //tạo barrier để đồng bộ
            var layoutloaded = new plus.Emitter();

            plus.bar([layoutloaded], function () {
                thethis.contentDiv = thethis.dom.find('.id_content');
                thethis.onReady.emit({});
            });

            //lấy layout
            $.get('applayout.html', function (data) {
                thethis.dom = $(data);
                layoutloaded.emit();
            });
        };
        return Layout;
    })();
    AppLayout.Layout = Layout;
})(AppLayout || (AppLayout = {}));
