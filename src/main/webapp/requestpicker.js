///<reference path="ref.ts" />
var RequestPicker;
(function (RequestPicker) {
    var Plugin = (function () {
        function Plugin() {
            this.onReady = new plus.Emitter();
        }
        Plugin.prototype.setCurrentRequestId = function (text) {
            this.currentrequest.html(text);
        };

        Plugin.prototype.init = function (unit, requestid) {
            var thanh = this;

            //tạo barrier để đồng bộ
            var layoutloaded = new plus.Emitter();
            var dataloaded = new plus.Emitter();

            //var dependloaded = new plus.Emitter();
            var requesttitle;
            plus.bar([layoutloaded, dataloaded], function () {
                for (var i in requestlist) {
                    var item = $('<li><a href="#cvmgr/' + unit + "/" + requestlist[i].id + '">' + requestlist[i].title + ' [ ' + requestlist[i].newcv + '/' + requestlist[i].totalcv + ' ]</a></li>');
                    thanh.menu.append(item);
                }
                thanh.menu.append($('<li class="divider"></li><li><a href="#requestmgr/' + unit + '">Nhiều hơn ...</a></li>'));
                thanh.currentrequest.html(requesttitle);
                thanh.onReady.emit();
            });

            //lấy layout
            $.get('requestpicker.html', function (data) {
                thanh.dom = $(data);
                thanh.menu = thanh.dom.find('.id_menu');
                thanh.currentrequest = thanh.dom.find('.id_requestid');
                layoutloaded.emit();
            });

            var requestlist;

            //lấy dữ liệu
            $.get('HRWeb/request/list', { unit: unit, p: 0, ps: 10 }, function (data) {
                requestlist = JSON.parse(data);
                $.get('HRWeb/request/get', { id: requestid }, function (data) {
                    data = JSON.parse(data);
                    requesttitle = data.title;
                    dataloaded.emit();
                });
            });
        };
        return Plugin;
    })();
    RequestPicker.Plugin = Plugin;
})(RequestPicker || (RequestPicker = {}));
