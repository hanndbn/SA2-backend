///<reference path="ref.ts" />
var Request;
(function (Request) {
    var Plugin = (function () {
        function Plugin() {
            this.onReady = new plus.Emitter();
            this.no = 0;
        }
        Plugin.prototype.addRequest = function (rq) {
            var thethis = this;
            var acm = new ActionMenu.Plugin();
            var stateswitcher = new SwitchBox.Plugin();

            thethis.no++;
            rq['_no'] = thethis.no;
            thethis.table.addRow(rq['_no'], {});

            plus.bar([acm.onReady, stateswitcher.onReady], function () {
                if (rq.state == 0)
                    stateswitcher.check();
                else
                    stateswitcher.uncheck();
                stateswitcher.onChecked.on(function () {
                    $.get('HRWeb/request/edit', { id: rq.id, state: 0 }, function () {
                    })['fail'](function () {
                        stateswitcher.uncheck();
                        alert('cannot change request state');
                    });
                });

                stateswitcher.onUnchecked.on(function () {
                    $.get('HRWeb/request/edit', { id: rq.id, state: 1 }, function () {
                    })['fail'](function () {
                        stateswitcher.check();
                        alert('cannot change request state');
                    });
                });

                var hand = thethis.new_request.onEdit.on(function (param) {
                    if (param.id != rq.id)
                        return;
                    var e = param.dom;
                    thethis.new_request.onEdit.de(hand);
                    $.get('HRWeb/request/get', { id: param.id }, function (data) {
                        thethis.table.deleteRow(rq['_no']);
                        rq = JSON.parse(data);
                        thethis.addRequest(rq);
                    });
                });

                //acm.addItem('Tới trang quản lý CV', 'icon-reply-all', function ()
                //{
                //	window.location.hash = thethis.openhash + "/" + rq.id;
                //});
                //acm.addItem('Đóng / Mở tin', 'icon-open', function ()
                //{
                //	if (stateswitcher.checked)
                //		stateswitcher.uncheck();
                //	else
                //		stateswitcher.check();
                //});
                var mix = $('<div></div>');
                var edit = $('<span></span>');
                edit.addClass('icon-pencil but');
                edit.click(function () {
                    thethis.new_request.show(false, rq);
                });
                var leave = $('<a href="#uploadcv/' + rq.id + '" target="_blank"></a>');
                leave.addClass('fa-share-square-o fa but');
                mix.append(edit);
                mix.append(leave);

                //	acm.addItem('Chi tiết / Sửa tin', 'icon-pencil2', function ()
                //	{
                //
                //	});
                //	acm.addItem2('Tới trang tuyển dụng', 'icon-export',  );
                var row = thethis.table.editRow(rq['_no'], {
                    0: rq['_no'],
                    1: '<a href="#' + thethis.openhash + "/" + rq.id + '">' + rq.title + '</a>',
                    2: rq.ctime,
                    4: stateswitcher.dom,
                    5: rq.position,
                    7: "<code>" + rq.newcv + "</code>" + "/" + "<code>" + rq.totalcv + "</code>",
                    8: mix
                });
                rq['_dom'] = row;
            });
            stateswitcher.init();
            acm.init();
        };

        Plugin.prototype.loadTable = function (p) {
            this.no = 0;
            this.pagi.setI(p);
            var thethis = this;
            plus['wait'].emit();

            //thethis.table.dom.prop('disabled', true);
            $.get('HRWeb/request/list', { p: p - 1, ps: 20, unit: thethis.unit }, function (data) {
                thethis.table.clear();

                //thethis.table.dom.prop('disabled', true);
                thethis.requestlist = JSON.parse(data);
                thethis.no = 0;

                for (var i in thethis.requestlist) {
                    thethis.addRequest(thethis.requestlist[i]);
                }
                plus['ready'].emit();
            })['fail'](function () {
                plus['ready'].emit();
                alert('Không lấy được danh sách tin từ server');
            });
        };

        Plugin.prototype.init = function (openhash, unit) {
            this.openhash = openhash;
            this.unit = unit;
            var thethis = this;

            //tạo barrier để đồng bộ
            var layoutloaded = new plus.Emitter();
            var dataloaded = new plus.Emitter();
            var dependloaded = new plus.Emitter();

            plus.bar([dependloaded, layoutloaded, dataloaded], function () {
                //Sau khi các plugin đã load xong
                plus.bar([thethis.table.onReady, thethis.pagi.onReady, thethis.new_request.onReady], function () {
                    thethis.dom.find('.id_pagi').append(thethis.pagi.dom);
                    thethis.dom.find('.id_table').append(thethis.table.dom);
                    thethis.pagi.setN(Math.ceil(n / 20));
                    thethis.pagi.setI(0);

                    thethis.pagi.onNavigate.on(function (data) {
                        thethis.loadTable(data.index);
                    });

                    thethis.table.setUpTable(['#', 'Tiêu đề', 'Ngày tạo', 'Trạng thái', 'Vị trí', 'Tiến độ', 'Khác']);
                    thethis.loadTable(1);
                    thethis.onReady.emit();
                });

                thethis.pagi.init();
                thethis.table.init();
                thethis.new_request.init(thethis.dom.find('.id_newmodal')[0], unit);

                thethis.new_request.onNew.on(function (param) {
                    $.get('HRWeb/request/get', { id: param.id }, function (data) {
                        var r = JSON.parse(data);
                        thethis.addRequest(r);
                    });
                });
            });

            //Khởi tạo các control phụ thuộc
            plus.req(['table.js', 'pagination.js', 'actionmenu.js', 'new_request.js', 'switchbox.js'], function () {
                thethis.table = new Table.Plugin();
                thethis.pagi = new Pagination.Plugin();

                thethis.new_request = new NewRequest.Plugin();
                dependloaded.emit();
            });

            //lấy layout
            $.get('requestmgr.html', function (data) {
                thethis.dom = $(data);
                thethis.dom.find('.id_new').on('click', function () {
                    thethis.new_request.show();
                });
                layoutloaded.emit();
            });

            //lấy dữ liệu
            var n;
            $.get('HRWeb/request/count', { unit: unit }, function (data) {
                n = JSON.parse(data).result;
                dataloaded.emit();
            });
        };
        return Plugin;
    })();
    Request.Plugin = Plugin;
})(Request || (Request = {}));
