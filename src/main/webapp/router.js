///<reference path="ref.ts" />
var router;
(function (router) {
    function init() {
        $(document).ready(router.route);
        $(window).on('hashchange', router.route);
    }
    router.init = init;

    function setHash(hash) {
        $(window).off('hashchange', router.route);
        window.location.hash = hash;
        ga();
        $(window).on('hashchange', router.route);
    }
    router.setHash = setHash;

    var Hrteam = (function () {
        function Hrteam() {
            this.onReady = new plus.Emitter();
        }
        Hrteam.prototype.init = function () {
            if (!plus['wait'])
                plus['wait'] = new plus.DelayedEmitter();
            if (!plus['ready'])
                plus['ready'] = new plus.DelayedEmitter();
            if (!plus['change'])
                plus['change'] = new plus.DelayedEmitter();
            var thethis = this;
            var dependloaded = new plus.Emitter();
            dependloaded.on(function () {
                thethis.layout = new Mainlayout.Layout();
                plus.bar([thethis.layout.onReady], function () {
                    thethis.dom = thethis.layout.dom;
                    thethis.contentDiv = thethis.layout.contentDiv;

                    var waiting = thethis.dom.find('.id_waiting');
                    var wr = 0;

                    plus['wait'].on(function (data) {
                        wr++;
                        console.log(wr);
                        waiting.removeClass('hidden');
                        if (wr <= 0) {
                            waiting.addClass('hidden');
                        }
                    });
                    var progress = thethis.dom.find('.id_progress');
                    plus['change'].on(function (param) {
                        progress.css('width', param.progress + "%");
                    });
                    plus['change'].open();
                    plus['wait'].open();

                    plus['ready'].on(function (data) {
                        wr--;
                        console.log(wr);
                        if (wr <= 0) {
                            waiting.addClass('hidden');
                        }
                    });
                    plus['ready'].open();

                    thethis.onReady.emit({});
                });

                if (router.params[1] == undefined || router.params[1] == "") {
                    $.get('HRWeb/user/get', function (data) {
                        data = JSON.parse(data);
                        var unit = data.units[0].id;
                        $.get('HRWeb/defaultparam', { unit: unit }, function (data) {
                            data = JSON.parse(data);
                            thethis.layout.init(unit, data.result, router.params[0]);
                        });
                    });
                } else {
                    $.get('HRWeb/defaultparam', { unit: router.params[1] }, function (data) {
                        data = JSON.parse(data);
                        thethis.layout.init(router.params[1], data.result, router.params[0]);
                    });
                }
            });

            plus.req(['mainlayout.js', 'request.js'], function () {
                dependloaded.emit();
            });
        };
        return Hrteam;
    })();

    var App = (function () {
        function App() {
            this.onReady = new plus.Emitter();
        }
        App.prototype.init = function () {
            var thethis = this;
            var dependloaded = new plus.Emitter();
            if (!plus['wait'])
                plus['wait'] = new plus.DelayedEmitter();
            if (!plus['ready'])
                plus['ready'] = new plus.DelayedEmitter();
            if (!plus['change'])
                plus['change'] = new plus.DelayedEmitter();

            dependloaded.on(function () {
                thethis.layout = new AppLayout.Layout();
                plus.bar([thethis.layout.onReady], function () {
                    thethis.dom = thethis.layout.dom;
                    thethis.contentDiv = thethis.layout.contentDiv;

                    var waiting = thethis.dom.find('.id_waiting');
                    var wr = 0;

                    var progress = thethis.dom.find('.id_progress');
                    plus['change'].on(function (param) {
                        progress.css('width', param.progress + "%");
                    });

                    plus['wait'].on(function (data) {
                        wr++;
                        console.log(wr);
                        waiting.removeClass('hidden');
                        if (wr <= 0) {
                            waiting.addClass('hidden');
                        }
                    });

                    plus['ready'].on(function (data) {
                        wr--;
                        console.log(wr);
                        if (wr <= 0) {
                            waiting.addClass('hidden');
                        }
                    });
                    plus['wait'].open();
                    plus['ready'].open();
                    plus['change'].open();

                    thethis.onReady.emit();
                });
                thethis.layout.init();
            });

            plus.req(['app.js'], function () {
                dependloaded.emit();
            });
        };
        return App;
    })();

    function isLoggedin(callback) {
        callback(false);
        //$.get('HRWeb/user/isloggedin', function (data)
        //{
        //	data = JSON.parse(data);
        //	if (data.result == true)
        //	{
        //		callback(true);
        //	}
        //	else
        //	{
        //		callback(false);
        //	}
        //});
    }

    router.params;

    function ga() {
        window['ga']('send', 'pageview', { 'page': location.pathname + location.search + location.hash });
    }

    function route() {
        ga();
        delete plus['wait'];
        delete plus['ready'];
        delete plus['change'];

        //$('body').empty();
        var hash = document.location.hash;
        var params = hash.split('/');
        router.params = params;
        isLoggedin(function (il) {
            if (il == true) {
                if (params[0] == '' || params[0] == '#') {
                    $.get('HRWeb/user/get', function (data) {
                        data = JSON.parse(data);
                        var unit = data.units[0].id;
                        window.location.hash = 'requestmgr/' + unit;
                    });
                    return;
                } else if (params[0] == '#requestmgr') {
                    var hrteam = new Hrteam();
                    var reqcontent;
                    var contentLoaded = new plus.Emitter();

                    plus.bar([contentLoaded, hrteam.onReady], function () {
                        hrteam.contentDiv.append(reqcontent.dom);
                        $('body').empty();
                        $('body').append(hrteam.dom);
                    });
                    plus.req(['request.js'], function () {
                        reqcontent = new Request.Plugin();
                        reqcontent.onReady.on(function () {
                            contentLoaded.emit();
                        });
                        reqcontent.init('cvmgr/' + params[1], params[1]);
                    });
                    hrteam.init();
                    return;
                } else if (params[0] == '#cvmgr') {
                    var hrteam = new Hrteam();
                    var cvcontent;

                    var contentLoaded = new plus.Emitter();
                    plus.bar([contentLoaded, hrteam.onReady], function () {
                        hrteam.contentDiv.append(cvcontent.dom);
                        $('body').empty();
                        $('body').append(hrteam.dom);
                    });
                    plus.req(['cv.js'], function () {
                        cvcontent = new CV.Plugin();
                        cvcontent.onReady.on(function () {
                            contentLoaded.emit();
                        });
                        cvcontent.init(params[1], params[2]);
                    });
                    hrteam.init();
                    return;
                } else if (params[0] == '#mailmgr') {
                    var hrteam = new Hrteam();
                    var mailmgr;

                    var contentLoaded = new plus.Emitter();
                    plus.bar([contentLoaded, hrteam.onReady], function () {
                        hrteam.contentDiv.append(mailmgr.dom);
                        $('body').empty();
                        $('body').append(hrteam.dom);
                    });

                    plus.req(['mail.js'], function () {
                        mailmgr = new Mail.Plugin();
                        mailmgr.onReady.on(function () {
                            contentLoaded.emit();
                        });
                        mailmgr.init(params[1]);
                    });

                    hrteam.init();
                    return;
                } else if (params[0] == '#config') {
                    var hrteam = new Hrteam();
                    var config;
                    var contentLoaded = new plus.Emitter();
                    plus.bar([contentLoaded, hrteam.onReady], function () {
                        hrteam.contentDiv.append(config.dom);
                        $('body').empty();
                        $('body').append(hrteam.dom);
                    });

                    plus.req(['config.js'], function () {
                        config = new Config.Plugin();
                        config.onReady.on(function () {
                            contentLoaded.emit();
                        });
                        config.init(params[1]);
                    });

                    hrteam.init();
                    return;
                } else if (params[0] == '#fieldmgr') {
                    var hrteam = new Hrteam();
                    var fcontent;
                    var contentLoaded = new plus.Emitter();
                    plus.bar([contentLoaded, hrteam.onReady], function () {
                        hrteam.contentDiv.append(fcontent.dom);
                        $('body').empty();
                        $('body').append(hrteam.dom);
                    });
                    plus.req(['cvfield.js'], function () {
                        fcontent = new CVField.Plugin();
                        fcontent.onReady.on(function () {
                            contentLoaded.emit();
                        });
                        fcontent.init(params[1]);
                    });
                    hrteam.init();
                    return;
                } else if (params[0] == '#engquestmgr') {
                    var hrteam = new Hrteam();
                    var qcontent;

                    var contentLoaded = new plus.Emitter();
                    plus.bar([contentLoaded, hrteam.onReady], function () {
                        hrteam.contentDiv.append(qcontent.dom);
                        $('body').empty();
                        $('body').append(hrteam.dom);
                    });
                    plus.req(['question.js'], function () {
                        qcontent = new Question.Plugin();
                        qcontent.onReady.on(function () {
                            contentLoaded.emit();
                        });
                        qcontent.init(params[1], 2 + "");
                    });
                    hrteam.init();
                    return;
                } else if (params[0] == '#iqquestmgr') {
                    var hrteam = new Hrteam();
                    var qcontent;

                    var contentLoaded = new plus.Emitter();
                    plus.bar([contentLoaded, hrteam.onReady], function () {
                        hrteam.contentDiv.append(qcontent.dom);
                        $('body').empty();
                        $('body').append(hrteam.dom);
                    });
                    plus.req(['question.js'], function () {
                        qcontent = new Question.Plugin();
                        qcontent.onReady.on(function () {
                            contentLoaded.emit();
                        });
                        qcontent.init(params[1], 1 + "");
                    });
                    hrteam.init();
                    return;
                    //} else if (params[0] == '#proquestmgr')
                    //{
                    //	var hrteam = new Hrteam();
                    //	var qcontent: Question.Plugin;
                    //	var contentLoaded = new plus.Emitter();
                    //	plus.bar([contentLoaded, hrteam.onReady], function ()
                    //	{
                    //		hrteam.contentDiv.append(qcontent.dom);
                    //		$('body').empty();
                    //		$('body').append(hrteam.dom);
                    //	});
                    //	plus.req(['question.js'], function ()
                    //	{
                    //		qcontent = new Question.Plugin();
                    //		qcontent.onReady.on(function ()
                    //		{
                    //			contentLoaded.emit();
                    //		});
                    //		qcontent.init(params[1], 3 + "");
                    //	});
                    //	hrteam.init();
                    //	return;
                } else if (params[0] == '#password') {
                    var hrteam = new Hrteam();
                    var password;

                    var contentLoaded = new plus.Emitter();
                    plus.bar([contentLoaded, hrteam.onReady], function () {
                        hrteam.contentDiv.append(password.dom);
                        $('body').empty();
                        $('body').append(hrteam.dom);
                    });
                    plus.req(['password.js'], function () {
                        password = new Password.Plugin();
                        password.onReady.on(function () {
                            contentLoaded.emit();
                        });
                        password.init();
                    });
                    hrteam.init();
                    return;
                }
            }

            if (params[0] == '#uploadcv') {
                var app = new App();
                var cvupload;

                var contentLoaded = new plus.Emitter();

                plus.bar([contentLoaded, app.onReady], function () {
                    app.contentDiv.append(cvupload.dom);
                    $('body').empty();
                    $('body').append(app.dom);
                });
                plus.req(['cvupload.js'], function () {
                    cvupload = new CVUpload.Plugin();
                    cvupload.onReady.on(function () {
                        contentLoaded.emit();
                    });
                    cvupload.init((params[1]));
                });

                app.init();
                return;
            } else if (params[0] == '' || params[0] == '#') {
                var app = new App();
                var requestlist;
                var contentLoaded = new plus.Emitter();
                plus.bar([contentLoaded, app.onReady], function () {
                    app.contentDiv.append(requestlist.dom);
                    $('body').empty();
                    $('body').append(app.dom);
                });

                plus.req(['requestlist.js'], function () {
                    requestlist = new RequestList.Plugin();
                    requestlist.onReady.on(function () {
                        contentLoaded.emit();
                    });
                    requestlist.init(parseInt(params[1]), params[2]);
                });

                app.init();
                return;
            } else if (params[0] == '#login') {
                if (il)
                    window.location.hash = "";
                plus.req(['login.js'], function () {
                    var login = new Login.Plugin();

                    login.onReady.on(function () {
                        $('body').empty();
                        $('body').append(login.dom);
                    });

                    login.init();
                });
                return;
            } else if (params[0] == '#test') {
                var app = new App();
                var testcontent;
                var contentLoaded = new plus.Emitter();
                plus.bar([contentLoaded, app.onReady], function () {
                    app.contentDiv.append(testcontent.dom);
                    $('body').empty();
                    $('body').append(app.dom);
                });
                plus.req(['test.js'], function () {
                    testcontent = new Test.Plugin();
                    testcontent.onReady.on(function () {
                        contentLoaded.emit();
                    });
                    testcontent.init(params[1]);
                });
                app.init();
                return;
            }

            document.body.innerHTML = '<p>404 NOT FOUND + 401 ACCESS DENY</p><p>Click vào <a href="#login">đây để đăng nhập</a></p>';
        });
    }
    router.route = route;
})(router || (router = {}));
