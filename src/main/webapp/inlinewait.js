///<reference path="jquery.d.ts" />
var Wait;
(function (Wait) {
    var InlinePlugin = (function () {
        function InlinePlugin() {
        }
        InlinePlugin.prototype.init = function (element) {
            this.element = $(element);
        };

        InlinePlugin.prototype.show = function () {
            this.element.removeClass('hidden');
        };

        InlinePlugin.prototype.hide = function () {
            this.element.addClass('hidden');
        };
        return InlinePlugin;
    })();
    Wait.InlinePlugin = InlinePlugin;
})(Wait || (Wait = {}));
