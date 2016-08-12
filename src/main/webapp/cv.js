///<reference path="ref.ts" />
var CV;
(function (CV) {
    var Plugin = (function () {
        function Plugin() {
            this.onReady = new plus.Emitter();
            this.no = 0;
            this.bags = {};
            this.databaglist = {};
        }
        Plugin.prototype.loadDatatable = function (bagid, p, n) {
            this.currentbag = this.databaglist[bagid];
            this.no = 0;
            var thethis = this;
            thethis.pagi.setN(Math.ceil(n / 20));
            thethis.pagi.setI(p);

            //thethis.bagpanel.disable();
            console.log('wait fire');
            plus['wait'].emit();
            $.get('HRWeb/cv/list', { bagid: bagid, p: p - 1, ps: 20, s: ["level", "total", "ctime"] }, function (data) {
                thethis.table.clear();
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

                        thethis.no++;
                        cv['_no'] = thethis.no;
                        thethis.table.addRow(cv['_no'], {});

                        plus.bar([acm.onReady, rate.onReady, vote.onReady, eng.onReady, iq.onReady, prestb.onReady, archtb.onReady, potetb.onReady], function () {
                            vote.onVoteUp.on(function () {
                                vote.displayLoading();

                                $.get('HRWeb/cv/edit', { id: cv.id, level: vote.vote + 1 }, function () {
                                    vote.refresh(vote.vote + 1);
                                })['fail'](function () {
                                    alert("Không sửa được trạng thái của cv");
                                    vote.refresh(vote.vote);
                                });
                            });

                            vote.onVoteDown.on(function () {
                                vote.displayLoading();
                                $.get('HRWeb/cv/edit', { id: cv.id, level: vote.vote - 1 }, function () {
                                    vote.refresh(vote.vote - 1);
                                })['fail'](function () {
                                    alert("Không sửa được trạng thái của cv");
                                    vote.refresh(vote.vote);
                                });
                            });

                            prestb.onChanged.on(function () {
                                prestb.displayLoading();
                                $.get('HRWeb/cv/edit', { id: cv.id, pres: parseInt(prestb.text) }, function () {
                                    prestb.displayText(prestb.text);
                                })['fail'](function () {
                                    alert("Không sửa được trạng thái của cv");
                                    prestb.displayText(prestb.oldtext);
                                });
                            });

                            archtb.onChanged.on(function () {
                                archtb.displayLoading();
                                $.get('HRWeb/cv/edit', { id: cv.id, arch: parseInt(archtb.text) }, function () {
                                    archtb.displayText(archtb.text);
                                })['fail'](function () {
                                    alert("Không sửa được trạng thái của cv");
                                    archtb.displayText(archtb.oldtext);
                                });
                            });

                            potetb.onChanged.on(function () {
                                potetb.displayLoading();
                                $.get('HRWeb/cv/edit', { id: cv.id, pote: parseInt(potetb.text) }, function () {
                                    potetb.displayText(potetb.text);
                                })['fail'](function () {
                                    alert("Không sửa được trạng thái của cv");
                                    potetb.displayText(potetb.oldtext);
                                });
                            });

                            acm.addItem2('Xuất / In', 'icon-export', 'HRWeb/cv/export?id=' + cv.id);

                            acm.addItem('Từ chối', 'icon-thumbsdown', function () {
                                console.log('wait fire');
                                plus['wait'].emit();
                                $.get('HRWeb/cv/delete', { id: cv.id }, function () {
                                    thethis.table.deleteRow(cv['_no']);
                                    console.log('ready fire');
                                    plus['ready'].emit();
                                })['fail'](function () {
                                    console.log('ready fire');
                                    plus['ready'].emit();
                                    alert("Không thể xóa CV");
                                    potetb.displayText(potetb.oldtext);
                                });
                            });

                            var mix = $('<div></div>');
                            mix.append(cm.dom);
                            mix.append(acm.dom);

                            if (cv.cantestiq) {
                                iq['!html'] = iq.dom;
                            } else {
                                acm.addItem('Thi lại IQ', 'icon-refresh', function () {
                                    iq.check();
                                    iq.onChecked.emit();
                                });
                                if (cv.iq !== -1) {
                                    iq['!html'] = cv.iq;
                                } else {
                                    iq['!html'] = iq.dom;
                                    iq.uncheck();
                                }
                            }

                            iq.onUnchecked.on(function () {
                                plus['wait'].emit();
                                $.get('HRWeb/cv/edit', { id: cv.id, cantestiq: false }, function () {
                                    plus['ready'].emit();
                                })['fail'](function () {
                                    plus['ready'].emit();
                                    alert('cannot close the IQ test');
                                });
                            });

                            iq.onChecked.on(function () {
                                plus['wait'].emit();
                                $.get('HRWeb/cv/edit', { id: cv.id, cantestiq: true }, function () {
                                    plus['ready'].emit();
                                })['fail'](function () {
                                    plus['ready'].emit();
                                    alert('cannot open the IQ test');
                                });
                            });

                            if (cv.cantesteng) {
                                eng['!html'] = eng.dom;
                            } else {
                                acm.addItem('Thi lại ENG', 'icon-refresh', function () {
                                    eng.check();
                                    eng.onChecked.emit();
                                });
                                if (cv.eng !== -1 && cv.eng !== undefined) {
                                    eng['!html'] = cv.eng;
                                } else {
                                    eng['!html'] = eng.dom;
                                    eng.uncheck();
                                }
                            }

                            eng.onChecked.on(function () {
                                plus['wait'].emit();
                                $.get('HRWeb/cv/edit', { id: cv.id, cantesteng: true }, function () {
                                    plus['ready'].emit();
                                })['fail'](function () {
                                    plus['ready'].emit();
                                    alert('cannot open the IQ test');
                                });
                            });

                            eng.onUnchecked.on(function () {
                                plus['wait'].emit();
                                $.get('HRWeb/cv/edit', { id: cv.id, cantesteng: false }, function () {
                                    plus['ready'].emit();
                                })['fail'](function () {
                                    plus['ready'].emit();
                                    alert('cannot close the IQ test');
                                });
                            });

                            vote.refresh(cv.level);
                            prestb.displayText(cv.pres);
                            archtb.displayText(cv.arch);
                            potetb.displayText(cv.pote);
                            prestb.input.addClass('numberinput');
                            archtb.input.addClass('numberinput');
                            potetb.input.addClass('numberinput');

                            var total;
                            if (cv.total == -1) {
                                total = $('<span class="label label-warning" >pending</span>');
                            } else {
                                total = cv.total;
                            }

                            var cvname = $('<a></a>');
                            cvname.html(cv.name + "<br/>" + new Date(cv.ctime).toDateString());
                            cvname.on('click', function () {
                                thethis.quickview.show(cv.id);
                            });

                            var row = thethis.table.editRow(cv['_no'], {
                                0: cv['_no'],
                                1: cvname,
                                3: iq['!html'],
                                4: eng['!html'],
                                5: prestb.dom,
                                6: archtb.dom,
                                7: potetb.dom,
                                80: total,
                                90: vote.dom,
                                100: mix[0]
                            });
                            cv['_dom'] = row;
                            row.attr('draggable', true);
                            row[0].addEventListener('dragstart', function (ev) {
                                ev.dataTransfer.setData("cv", JSON.stringify({ cvid: cv.id, cvno: cv['_no'] }));
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
                plus['ready'].emit();
                //thethis.bagpanel.enable();
                //thethis.table.enable();
            });
        };

        Plugin.prototype.pageload = function (bagid, unit, requestid) {
            var thethis = this;
            var dataloaded = new plus.Emitter();
            thethis.bagpanel.clear();
            dataloaded.on(function () {
                thethis.table.clear();
                thethis.no = 0;

                for (var i in thethis.baglist) {
                    var bag = thethis.baglist[i];
                    var bdom = thethis.bagpanel.addBag(bag.name, '<strong>' + bag.name + '</strong>', bag.id, { id: bag.id, name: bag.name });
                    thethis.bags[bag.id] = bdom;
                    thethis.databaglist[bag.id] = bag;
                    thethis.bagpanel.changeN(bdom, bag.count);
                }
                thethis.loadDatatable(thethis.currentbag.id, 1, thethis.currentbag['count']);
            });

            $.get('HRWeb/bag/list', { request: requestid }, function (data) {
                thethis.baglist = JSON.parse(data);
                if (bagid == null) {
                    thethis.currentbag = thethis.baglist[0];
                }

                dataloaded.emit();
            });
        };

        Plugin.prototype.init = function (unit, requestid) {
            if (requestid == "undefined" || requestid == "-1") {
                this.dom = $("<p>CV BAG IS EMPTY</p><p>CAN'T FOUND ANY REQUEST</p>");
                this.onReady.emit();
                return;
            }

            var thethis = this;

            //tạo barrier để đồng bộ
            var layoutloaded = new plus.Emitter();

            var dependloaded = new plus.Emitter();

            plus.bar([dependloaded, layoutloaded], function () {
                //Sau khi các plugin đã load xong
                plus.bar([thethis.table.onReady, thethis.pagi.onReady, thethis.picker.onReady, thethis.bagpanel.onReady, thethis.quickview.onReady], function () {
                    thethis.dom.find('.id_table').append(thethis.table.dom);
                    thethis.dom.find('.id_pagi').append(thethis.pagi.dom);
                    thethis.dom.find('.id_requestpicker').append(thethis.picker.dom);
                    thethis.bagpanelDiv = thethis.dom.find('.id_bagpanel');
                    thethis.dom.find('.id_quickview').append(thethis.quickview.dom);
                    thethis.bagpanelDiv.append(thethis.bagpanel.dom);

                    thethis.bagpanel.onAdd.on(function () {
                        $.get('HRWeb/bag/create', { name: "New bag", request: requestid }, function (data) {
                            data = JSON.parse(data);
                            var bdom = thethis.bagpanel.addBag("New bag", '<strong>' + name + '</strong>', data.result, { id: data.result, name: "New bag" });
                            thethis.bags[data.result] = bdom;
                            thethis.databaglist[data.result] = { id: data.result, count: 0 };
                            thethis.bagpanel.changeN(bdom, 0);
                        })['fail'](function () {
                            alert('Không thể tạo thùng mới');
                        });
                    });

                    thethis.bagpanel.onEdit.on(function (param) {
                        var bid = param.attach.id;
                        var newname = param.name;

                        $.get('HRWeb/bag/edit', { id: bid, name: newname }, function () {
                            thethis.bagpanel.displayText(bid);
                        })['fail'](function () {
                            alert('Không thể thay đổi thùng');
                        });
                    });

                    thethis.bagpanel.onDeleted.on(function (param) {
                        var rbname = param.attach.name;
                        var rbid = param.attach.id;
                        if (rbid == thethis.baglist[0].id || rbid == thethis.baglist[1].id) {
                            alert("can not remove the default bag");
                            return;
                        } else {
                            var bagname = prompt("Please type name of the bag to remove it", "bag name");
                            if (bagname == rbname) {
                                thethis.bagpanel.disable();
                                $.get('HRWeb/bag/delete', { id: rbid }, function () {
                                    thethis.bags[rbid].hide();

                                    //chuyển select
                                    if (param.dom === thethis.bagpanel.focused) {
                                        thethis.bagpanel.focus(0);
                                    }

                                    thethis.bagpanel.enable();
                                })['fail'](function () {
                                    thethis.bagpanel.enable();
                                });
                            } else {
                                if (bagname !== null && bagname !== undefined && bagname !== "") {
                                    alert('Wrong bag name');
                                }
                            }
                        }
                    });

                    thethis.bagpanel.onMoved.on(function (param) {
                        param.data = JSON.parse(param.data);

                        //Nếu kéo vào chính gói của mình thì thoát
                        if (param.attach.id == thethis.currentbag.id)
                            return;
                        console.log('wait fire');
                        plus['wait'].emit();

                        //thethis.bagpanel.disable();
                        //thethis.table.disable();
                        $.get('HRWeb/cv/move', { id: param.data.cvid, bagid: param.attach.id }, function () {
                            thethis.databaglist[thethis.currentbag.id]['count'] -= 1;
                            thethis.databaglist[param.attach.id]['count'] += 1;
                            thethis.bagpanel.changeN(thethis.bags[thethis.currentbag.id], thethis.databaglist[thethis.currentbag.id]['count']);
                            thethis.bagpanel.changeN(thethis.bags[param.attach.id], thethis.databaglist[param.attach.id]['count']);
                            thethis.table.deleteRow(param.data.cvno);
                            console.log('ready fire');
                            plus['ready'].emit();
                            //thethis.table.enable();
                            //thethis.bagpanel.enable();
                        })['fail'](function () {
                            alert('cannot move cv');
                            console.log('ready fire');
                            plus['ready'].emit();
                            //thethis.table.enable();
                            //thethis.bagpanel.enable();
                        });
                    });

                    thethis.pagi.onNavigate.on(function (data) {
                        var bag = thethis.bagpanel.getFocusedBag();
                        thethis.loadDatatable(bag.id, data.index, bag.count);
                    });
                    thethis.bagpanel.onSelected.on(function (param) {
                        thethis.loadDatatable(param.attach.id, 1, thethis.databaglist[param.attach.id]['count']);
                    });

                    thethis.table.setUpTable(['#', 'Tên', 'IQ', 'ENG', 'PRES', 'ARCH', 'POTE', 'Tổng', 'Xếp hạng', 'Khác']);
                    thethis.pageload(null, unit, requestid);
                });

                thethis.quickview.init();
                thethis.pagi.init(1);
                thethis.table.init();
                thethis.picker.init(unit, requestid);
                thethis.bagpanel.init();
            });

            //Khởi tạo các control phụ thuộc
            plus.req([
                'table.js', 'pagination.js',
                'actionmenu.js', 'cvcomment.js',
                'requestpicker.js', 'voting.js', 'textbox.js',
                'starrating.js', 'bagpanel.js', 'switchbox.js', 'cvquickview.js'], function () {
                thethis.table = new Table.Plugin();
                thethis.pagi = new Pagination.Plugin();
                thethis.picker = new RequestPicker.Plugin();
                thethis.bagpanel = new BagPanel.Plugin();
                thethis.quickview = new CVQuickview.Plugin();
                dependloaded.emit();
            });

            //lấy layout
            $.get('cvmgr.html', function (data) {
                thethis.dom = $(data);
                thethis.onReady.emit();
                layoutloaded.emit();
            });

            var baglist;
        };
        return Plugin;
    })();
    CV.Plugin = Plugin;
})(CV || (CV = {}));
