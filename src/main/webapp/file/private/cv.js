///<reference path="ref.ts" />
var CV;
(function (CV) {
    var Plugin = (function () {
        function Plugin() {
            this.onReady = new plus.Emitter();
        }
        Plugin.prototype.loadDatatable = function (bagid) {
            var thethis = this;
            this.table.dom.prop('disabled', true);
            $.get('/HRWeb/HRWeb/cv/list', { bagid: bagid }, function (data) {
                var cvlist = JSON.parse(data);
                for (var i in cvlist) {
                    (function () {
                        var e = i;
                        var cv = cvlist[i];
                        var acm = new ActionMenu.Plugin();
                        var cm = new CVComment.Plugin();
                        var rate = new StarRating.Plugin();
                        var vote = new Voting.Plugin();

                        var iq = new SwitchBox.Plugin();
                        var eng = new SwitchBox.Plugin();

                        var prestb = new TextBox.Plugin();
                        var archtb = new TextBox.Plugin();
                        var potetb = new TextBox.Plugin();

                        plus.bar([acm.onReady, rate.onReady, vote.onReady, eng.onReady, iq.onReady, prestb.onReady, archtb.onReady, potetb.onReady], function () {
                            acm.addItem('Details', 'glyphicon glyphicon-eye-open', function () {
                                window.location.href = "/HRWeb/tvg.html#cv_filtering?requestid=" + cv.id;
                            });
                            acm.addItem('Reject', 'glyphicon glyphicon-eye-open', function () {
                                console.debug(cv);
                            });
                            acm.addItem('Open ENG test', 'glyphicon glyphicon-eye-open', function () {
                                console.debug(cv);
                            });

                            acm.addItem('Open IQ test', 'glyphicon glyphicon-eye-open', function () {
                                console.debug(cv);
                            });

                            var mix = $('<div></div>');
                            mix.append(cm.dom);
                            mix.append(acm.dom);

                            if (cv.cantestiq) {
                                iq['!html'] = iq.dom;
                                iq.onUnchecked.on(function () {
                                    $.get('/HRWeb/HRWeb/test/closeiq', { id: cv.id })['fail'](function () {
                                        alert('cannot close the IQ test');
                                    });
                                });
                            } else {
                                if (cv.iq !== -1) {
                                    iq['!html'] = cv.iq;
                                } else {
                                    iq['!html'] = iq.dom;
                                    iq.uncheck();
                                    iq.onChecked.on(function () {
                                        $.get('/HRWeb/HRWeb/test/openiq', { id: cv.id })['fail'](function () {
                                            alert('cannot open the IQ test');
                                        });
                                    });
                                }
                            }

                            if (cv.cantesteng) {
                                eng['!html'] = eng.dom;
                                eng.onUnchecked.on(function () {
                                    $.get('/HRWeb/HRWeb/test/closeeng', { id: cv.id })['fail'](function () {
                                        alert('cannot close the IQ test');
                                    });
                                });
                            } else {
                                if (cv.eng !== -1 && cv.eng !== undefined) {
                                    eng['!html'] = cv.eng;
                                } else {
                                    eng['!html'] = eng.dom;
                                    eng.uncheck();
                                    eng.onChecked.on(function () {
                                        $.get('/HRWeb/HRWeb/test/openiq', { id: cv.eng })['fail'](function () {
                                            alert('cannot open the IQ test');
                                        });
                                    });
                                }
                            }

                            prestb.displayText(cv.pres);
                            archtb.displayText(cv.pote);
                            potetb.displayText(cv.sum);

                            prestb.input.addClass('numberinput');
                            archtb.input.addClass('numberinput');
                            potetb.input.addClass('numberinput');

                            var row = thethis.table.addRow({
                                0: (1 + parseInt(e)),
                                1: cv.name,
                                2: cv.submitdate,
                                3: iq['!html'],
                                4: eng['!html'],
                                9: vote.dom,
                                11: mix[0]
                            });

                            row.attr('draggable', true);
                            row.on('dragstart', function (ev) {
                                ev.dataTransfer.setData("cv", { id: cv.id, row: row });
                            });
                        });
                        cm.init(cvlist[i].id);
                        acm.init();
                        rate.init();
                        vote.init();

                        eng.init();
                        iq.init();
                        prestb.init();
                        archtb.init();
                        potetb.init();
                    })();
                }
                thethis.table.dom.prop('disabled', false);
            });
        };

        Plugin.prototype.init = function (requestid) {
            var thethis = this;

            //tạo barrier để đồng bộ
            var layoutloaded = new plus.Emitter();
            var dataloaded = new plus.Emitter();
            var dependloaded = new plus.Emitter();

            plus.bar([dependloaded, layoutloaded, dataloaded], function () {
                //Sau khi các plugin đã load xong
                plus.bar([thethis.table.onReady, thethis.pagi.onReady, thethis.picker.onReady, thethis.bagpanel.onReady], function () {
                    thethis.dom.find('.id_table').append(thethis.table.dom);
                    thethis.dom.find('.id_pagi').append(thethis.pagi.dom);
                    thethis.dom.find('.id_requestpicker').append(thethis.picker.dom);
                    thethis.bagpanelDiv = thethis.dom.find('.id_bagpanel');
                    thethis.bagpanelDiv.append(thethis.bagpanel.dom);
                    thethis.pagi.setN(n);

                    thethis.table.setUpTable(['#', 'Name', 'Submit', 'IQ', 'ENG', 'Like', 'Action']);

                    for (var i in baglist) {
                        var bag = baglist[i];
                        thethis.bagpanel.addBag(bag.name, '<strong>' + bag.name + '</strong>', bag.id, { id: bag.id, name: bag.name });
                    }

                    thethis.bagpanel.onDeleted.on(function (param) {
                        var rbname = param.attach.name;
                        var rbid = param.attach.id;
                        if (rbid == baglist[0].id) {
                            alert("can not remove the default bag");
                            return;
                        } else {
                            var bagname = prompt("Please type name of the bag to remove it", "bag name");
                            if (bagname == rbname) {
                                thethis.bagpanel.dom.prop('disabled', true);
                                $.get('/HRWeb/HRWeb/bag/delete', { id: rbid }, function () {
                                    //xóa bag
                                    bag.hide(200);
                                    bag.remove();

                                    //chuyển select
                                    if (param.dom === thethis.bagpanel.focused) {
                                        thethis.bagpanel.focus(0);
                                    }

                                    thethis.bagpanel.dom.prop('disabled', false);
                                })['fail'](function () {
                                    thethis.bagpanel.dom.prop('disabled', false);
                                });
                            } else {
                                if (bagname !== null && bagname !== undefined && bagname !== "") {
                                    alert('Wrong bag name');
                                }
                            }
                        }
                    });

                    thethis.bagpanel.onMoved.on(function (param) {
                        thethis.bagpanel.dom.prop('disabled', true);
                        thethis.table.dom.prop('disabled', true);
                        $.get('/HRWeb/HRWeb/cv/move', { id: param.data.id, bagid: param.attach.id }, function () {
                            var row = param.data.row;
                            row.hide(200);
                            row.remove();
                            thethis.table.dom.prop('disabled', true);
                            thethis.bagpanel.dom.prop('disabled', true);
                        });
                    });

                    thethis.bagpanel.onSelected.on(function (param) {
                        console.log(param);
                        thethis.loadDatatable(param.attach.id);
                    });

                    thethis.loadDatatable(baglist[0].id);
                    thethis.onReady.emit();
                });

                thethis.pagi.init();
                thethis.table.init();
                thethis.picker.init(requestid);
                thethis.bagpanel.init();
            });

            //Khởi tạo các control phụ thuộc
            plus.req([
                'table.js', 'pagination.js',
                'actionmenu.js', 'cvcomment.js',
                'requestpicker.js', 'voting.js', 'textbox.js',
                'starrating.js', 'bagpanel.js', 'switchbox.js'], function () {
                thethis.table = new Table.Plugin();
                thethis.pagi = new Pagination.Plugin();
                thethis.picker = new RequestPicker.Plugin();
                thethis.bagpanel = new BagPanel.Plugin();
                dependloaded.emit();
            });

            //lấy layout
            $.get('cvmgr.html', function (data) {
                thethis.dom = $(data);
                layoutloaded.emit();
            });

            //lấy dữ liệu
            var n;

            var baglist;

            $.get('/HRWeb/HRWeb/cv/count', { requestid: requestid }, function (data) {
                n = JSON.parse(data).return;

                $.get('/HRWeb/HRWeb/bag/list', { requestid: requestid }, function (dataa) {
                    baglist = JSON.parse(dataa);
                    dataloaded.emit();
                });
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
    CV.Plugin = Plugin;
})(CV || (CV = {}));
