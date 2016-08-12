///<reference path="ref.ts" />
var TextBox;
(function (TextBox) {
    var Plugin = (function () {
        function Plugin() {
            this.onChanged = new plus.Emitter();
            this.onReady = new plus.Emitter();
            this.stars = [];
            this.changed = false;
        }
        Plugin.prototype.init = function () {
            var thethis = this;

            $.post('textbox.html', function (data) {
                thethis.dom = $(data);
                thethis.bound();
                thethis.onReady.emit({});
            })['fail'](function (error) {
                console.error(error);
                thethis.dom.css('background-color', '#ff0000');
            });
        };

        Plugin.prototype.bound = function () {
            var thethis = this;

            thethis.input = this.dom.find('.id_inputex');
            thethis.wait = this.dom.find('.id_wait');

            thethis.input.dblclick(function () {
                thethis.input.removeAttr('readonly');
            });

            thethis.input.click(function (e) {
                if (thethis.input.attr('readonly') == undefined)
                    e.stopPropagation();
            });
            thethis.input.on('keyup', function (e) {
                if (thethis.input.val() != thethis.oldtext)
                    thethis.changed = true;
                if (e.keyCode == 13) {
                    thethis.text = thethis.input.val();
                    thethis.input.prop('readonly', true);
                    thethis.onChanged.emit();
                    thethis.displayLoading();
                    thethis.changed = false;
                }
            });

            thethis.input.focusout(function () {
                thethis.input.prop('readonly', true);
                if (thethis.changed) {
                    thethis.input.addClass('unsaved');
                }
            });
        };

        Plugin.prototype.displayLoading = function () {
            this.input.addClass('hidden');
            this.wait.removeClass('hidden');
        };

        Plugin.prototype.displayText = function (text) {
            if (text == undefined)
                text = this.text;
            this.input.removeClass('unsaved');
            this.text = text;
            this.oldtext = text;
            this.wait.addClass('hidden');
            this.input.removeClass('hidden');
            this.input.val(text);
        };
        return Plugin;
    })();
    TextBox.Plugin = Plugin;
})(TextBox || (TextBox = {}));
