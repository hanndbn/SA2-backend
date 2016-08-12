///<reference path="jquery.d.ts" />

var plus;
(function (plus) {
    function get(url, data, success, fail, progress) {
        return ajax(url, 'GET', data, success, fail, progress, undefined);
    }
    plus.get = get;

    function upload(url, data, success, fail, upload) {
        return ajax(url, 'POST', data, success, fail, undefined, upload);
    }
    plus.upload = upload;

    function post(url, data, success, fail, progress) {
        return ajax(url, 'POST', data, success, fail, progress, undefined);
    }
    plus.post = post;

    function ajax(url, type, data, success, fail, download, upload) {
        if (upload == undefined || upload == null)
            upload = function () {
            };

        if (download == undefined || download == null)
            download = function () {
            };

        if (fail == undefined || fail == null)
            fail = function () {
            };

        if (success == undefined || success == null)
            success = function () {
            };

        return $['ajax']({
            url: url,
            type: type,
            data: data,
            async: true,
            xhr: function () {
                // get the native XmlHttpRequest object
                var xhr = $['ajaxSettings'].xhr();

                //UPLOAD PROGRESS
                xhr.upload.addEventListener("progress", function (evt) {
                    upload(evt.loaded / evt.total * 100);
                }, false);

                //DOWNLOAD PROGRESS
                xhr.addEventListener("progress", function (evt) {
                    download(evt.loaded / evt.total * 100);
                }, false);

                return xhr;
            },
            success: function (data) {
                success(data);
            },
            cache: false,
            contentType: false,
            processData: false
        })['fail'](function (e) {
            fail(e);
        });
    }
    plus.ajax = ajax;

    function req(urls, callback) {
        require(urls, function (util) {
            callback();
        });
    }
    plus.req = req;

    function bar(evs, callback, timeout) {
        if (typeof timeout === "undefined") { timeout = 10000; }
        // if callback hadn't been called after a specificed of time
        // then it could be error -> have to warn programmer
        var called = false;
        if (timeout == undefined || timeout == null)
            timeout = 10000;

        setTimeout(function () {
            if (called == false)
                console.warn("Wait too long", evs, callback);
        }, timeout);

        var em = [];
        for (var i in evs) {
            em[i] = false;
            (function () {
                var e = i;
                evs[i].once(function () {
                    em[e] = true;
                    for (var j in em)
                        if (em[j] == false)
                            return;
                    called = true;
                    callback();
                });
            })();
        }
    }
    plus.bar = bar;

    var DelayedEmitter = (function () {
        function DelayedEmitter() {
            this.i = 0;
            this.isopen = false;
            this.handlers = {};
            this.calls = [];
        }
        DelayedEmitter.prototype.open = function () {
            this.isopen = true;
            var calls = this.calls;
            for (var i in calls) {
                this.emit(calls[i].param, calls[i].callback, calls[i].context);
            }
        };

        DelayedEmitter.prototype.once = function (func, context) {
            var id = this.on(func, context);
            this.handlers[id]['-.-.-once-.-.-'] = true;
            return id;
        };

        DelayedEmitter.prototype.on = function (func, context) {
            this.i++;
            if (context === undefined)
                context = null;

            //JAT:
            if (this.handlers[this.i] !== undefined)
                throw "should not run this";
            this.handlers[this.i] = func.bind(context);
            return this.i;
        };

        DelayedEmitter.prototype.de = function (handle) {
            delete this.handlers[handle];
        };

        DelayedEmitter.prototype.clear = function () {
            delete this.handlers;
            this.handlers = [];
        };

        DelayedEmitter.prototype.emit = function (param, callback, context) {
            //delay call
            if (this.isopen == false) {
                this.calls.push({ param: param, callback: callback, context: context });
                return;
            }

            for (var i in this.handlers) {
                var stop = this.handlers[i](param);
                if (this.handlers[i]['-.-.-once-.-.-'] === true)
                    this.de(i);
                if (stop == true)
                    break;
            }

            if (callback !== undefined) {
                if (context !== undefined)
                    callback.bind(context)(param);
                else
                    callback(param);
            }
        };
        return DelayedEmitter;
    })();
    plus.DelayedEmitter = DelayedEmitter;

    var Button = (function () {
        function Button() {
            this.layout = "button.html";
        }
        return Button;
    })();
    plus.Button = Button;

    var Plugin = (function () {
        function Plugin() {
            this.onLayoutLoaded = new Emitter();
            this.onDependLoaded = new Emitter();
            this.dom = $('<div>');
        }
        Plugin.prototype.getState = function () {
        };

        Plugin.prototype.Plugin = function () {
            var self = this;
            bar([this.onLayoutLoaded, this.onDependLoaded], function () {
                self.display();
            });

            plus.get(this.layouturl, {}, function () {
                self.onLayoutLoaded.emit();
            }, function () {
                self.displayError();
                self.onLayoutLoaded.emit();
            }, function (p) {
                self.displayWait(p);
            });

            req(self.required, function () {
                self.onDependLoaded.emit();
            });
        };

        Plugin.prototype.displayWait = function (progress) {
            if (typeof progress === "undefined") { progress = 0; }
        };

        Plugin.prototype.displayError = function () {
        };

        Plugin.prototype.displayReady = function () {
        };

        Plugin.prototype.display = function () {
        };
        return Plugin;
    })();
    plus.Plugin = Plugin;

    var Emitter = (function () {
        function Emitter() {
            this.i = 0;
            this.handlers = {};
        }
        Emitter.prototype.once = function (func, context) {
            var id = this.on(func, context);
            this.handlers[id]['-.-.-once-.-.-'] = true;
            return id;
        };

        Emitter.prototype.on = function (func, context) {
            this.i++;
            if (context === undefined)
                context = null;

            //JAT:
            if (this.handlers[this.i] !== undefined)
                throw "should not run this";
            this.handlers[this.i] = func.bind(context);
            return this.i;
        };

        Emitter.prototype.de = function (handle) {
            delete this.handlers[handle];
        };

        Emitter.prototype.clear = function () {
            delete this.handlers;
            this.handlers = [];
        };

        Emitter.prototype.emit = function (param, callback, context) {
            for (var i in this.handlers) {
                var stop = this.handlers[i](param);
                if (this.handlers[i]['-.-.-once-.-.-'] === true)
                    this.de(i);
                if (stop == true)
                    break;
            }

            if (callback !== undefined) {
                if (context !== undefined)
                    callback.bind(context)(param);
                else
                    callback(param);
            }
        };
        return Emitter;
    })();
    plus.Emitter = Emitter;
})(plus || (plus = {}));
