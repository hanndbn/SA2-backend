///<reference path="ref.ts" />
var Captcha;
(function (Captcha) {
    var Plugin = (function () {
        function Plugin() {
            this.onReady = new plus.Emitter();
            this.issumited = false;
        }
        Plugin.prototype.init = function () {
            var thethis = this;

            var layoutloaded = new plus.Emitter();
            var dataloaded = new plus.Emitter();

            plus.bar([layoutloaded], function () {
                thethis.onReady.emit({});
            });

            plus.bar([dataloaded, layoutloaded], function () {
                thethis.regenBt.on('click', function () {
                    thethis.regen();
                });

                thethis.captchaimg.on('click', function () {
                    thethis.regen();
                });

                thethis.captchaimg.attr("src", thethis.base64);
            });

            var content;
            $.get('captcha.html', function (data) {
                thethis.dom = $(data);
                thethis.regenBt = thethis.dom.find('.id_regen');
                thethis.captchainput = thethis.dom.find('.id_capchainput');
                thethis.captchaimg = thethis.dom.find('.id_captchaimg');

                layoutloaded.emit({});
            })['fail'](function (error) {
                console.error(error);
                thethis.dom.css('background-color', '#ff0000');
            });

            $.get('/apply/captcha/create', function (data) {
                data = JSON.parse(data);
                thethis.captchaid = data.id;
                thethis.base64 = data.base64;
                dataloaded.emit();
            });
        };

        Plugin.prototype.regen = function () {
            this.issumited = false;
            var thethis = this;
            this.captchainput.val("");
            this.captchainput.prop('disabled', true);
            $.get('/apply/captcha/create', function (data) {
                thethis.captchainput.prop('disabled', false);
                setTimeout(function () {
                    if (thethis.issumited == false)
                        thethis.regen();
                }, 400000);
                data = JSON.parse(data);
                thethis.captchaid = data.id;
                thethis.base64 = data.base64;
                thethis.captchaimg.attr("src", thethis.base64);
            });
        };

        Plugin.prototype.getAnswer = function () {
            this.issumited = true;
            return {
                id: this.captchaid,
                answer: this.captchainput.val()
            };
        };
        return Plugin;
    })();
    Captcha.Plugin = Plugin;
})(Captcha || (Captcha = {}));
