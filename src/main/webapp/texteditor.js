///<reference path="ref.ts" />
var TextEditor;
(function (TextEditor) {
    var Plugin = (function () {
        function Plugin() {
            this.onReady = new plus.Emitter();
        }
        Plugin.prototype.getCode = function () {
            return this.editor['code']();
        };

        Plugin.prototype.setCode = function (code) {
            this.editor['code'](code);
        };

        Plugin.prototype.init = function () {
            var thethis = this;
            plus.req(['summernote.min.js'], function () {
                thethis.dom = $('<div></div>');

                thethis.editor = $('<div></div>');
                thethis.dom.append(thethis.editor);
                thethis.editor['summernote']({
                    height: 300,
                    focus: true,
                    codemirror: {}
                });

                thethis.onReady.emit();
            });
        };
        return Plugin;
    })();
    TextEditor.Plugin = Plugin;
})(TextEditor || (TextEditor = {}));
