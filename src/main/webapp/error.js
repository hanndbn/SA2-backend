///<reference path="ref.ts" />
var ErrorDlg;
(function (ErrorDlg) {
    var Plugin = (function () {
        function Plugin() {
            this.onReady = new plus.Emitter();
        }
        Plugin.prototype.init = function () {
            var thethis = this;
            var layoutLoaded = new plus.Emitter();
            layoutLoaded.on(function () {
                thethis.title = thethis.dom.find('.id_errorTitle');
                thethis.message = thethis.dom.find('.id_errorMessage');
                thethis.onReady.emit();
            });

            $.post('error.html', function (data) {
                thethis.dom = $(data);
                layoutLoaded.emit();
            });
        };

        Plugin.prototype.show = function (title, message) {
            this.title.html(title);
            this.message.html(message);
            this.dom['modal']('show');
        };

        Plugin.prototype.hide = function () {
            this.dom['modal']('hide');
        };
        return Plugin;
    })();
    ErrorDlg.Plugin = Plugin;
})(ErrorDlg || (ErrorDlg = {}));
