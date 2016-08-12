///<reference path="ref.ts" />
var SwitchBox;
(function (SwitchBox) {
    var Plugin = (function () {
        function Plugin() {
            this.onChecked = new plus.Emitter();
            this.onUnchecked = new plus.Emitter();
            this.onReady = new plus.Emitter();
        }
        Plugin.prototype.init = function () {
            var thethis = this;
            thethis.checked = true;
            $.post('switchbox.html', function (data) {
                thethis.dom = $(data);
                thethis.sw = thethis.dom.find('.onoffswitch-label');
                thethis.checkbox = thethis.dom.find('.onoffswitch-checkbox');

                thethis.sw.on('click', function () {
                    thethis.checked = !thethis.checked;
                    thethis.checkbox.prop('checked', thethis.checked);
                    if (thethis.checked)
                        thethis.onChecked.emit();
                    else
                        thethis.onUnchecked.emit();
                });

                thethis.onReady.emit();
            })['fail'](function (error) {
                console.error(error);
                thethis.dom.css('background-color', '#ff0000');
            });
        };

        Plugin.prototype.check = function () {
            if (!this.checked)
                this.sw.trigger('click');
        };

        Plugin.prototype.uncheck = function () {
            if (this.checked)
                this.sw.trigger('click');
        };
        return Plugin;
    })();
    SwitchBox.Plugin = Plugin;
})(SwitchBox || (SwitchBox = {}));
