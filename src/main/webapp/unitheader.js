///<reference path="ref.ts" />
var Unitheader;
(function (Unitheader) {
    var Plugin = (function () {
        function Plugin() {
            this.onRate = new plus.Emitter();
            this.onReady = new plus.Emitter();
        }
        Plugin.prototype.init = function (unitid, units) {
            var thethis = this;

            //tạo barrier để đồng bộ
            var scriptloaded = new plus.Emitter();
            var layoutloaded = new plus.Emitter();

            plus.bar([layoutloaded], function () {
                thethis.unitname = thethis.dom.find('.id_unitname');
                var menu = thethis.dom.find('.id_menu');
                for (var u in units) {
                    if (units[u].id == unitid) {
                        thethis.unitname.html(' ' + units[u].name);
                    }
                    var d = $('<li><a href="#requestmgr/' + units[u].id + '"><span class="icon-paperplane"></span><nbsp></nbsp> Quản lý đơn vị ' + units[u].name + '</a></li>');
                    menu.append(d);
                }

                thethis.onReady.emit();
            });

            //tải layout
            $.post('unitzone.html', function (data) {
                thethis.dom = $(data);
                layoutloaded.emit();
            })['fail'](function (error) {
                console.error(error);
            });
        };
        return Plugin;
    })();
    Unitheader.Plugin = Plugin;
})(Unitheader || (Unitheader = {}));
