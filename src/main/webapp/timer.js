///<reference path="ref.ts" />
var Timer;
(function (Timer) {
    var Plugin = (function () {
        function Plugin() {
            this.onStoped = new plus.Emitter();
            this.onReady = new plus.Emitter();
            this.hop = 20;
            this.onTick = new plus.Emitter();
            this.runing = true;
        }
        Plugin.prototype.init = function () {
            var thethis = this;

            $.post('timer.html', function (data) {
                thethis.dom = $(data);
                thethis.timeDiv = thethis.dom.find('.id_time');

                thethis.onReady.emit({});
            })['fail'](function (error) {
                console.error(error);
                thethis.dom.css('background-color', '#ff000');
            });
        };

        Plugin.prototype.stop = function () {
            this.runing = false;
        };

        Plugin.prototype.run = function (thethis) {
            if (thethis.runing == false)
                return;
            var elapsed = new Date().getTime() - thethis.starttime.getTime();
            elapsed = Math.floor(elapsed / 1000);
            var delta = thethis.duration - elapsed;

            if (delta <= 0) {
                thethis.onStoped.emit();
                thethis.timeDiv.html(0 + ":" + 0);
                return;
            }

            thethis.onTick.emit({ tick: elapsed });
            var min = Math.floor(delta / 60);
            var sec = Math.floor((delta % 60));
            thethis.timeDiv.html(min + ":" + sec);
            setTimeout(thethis.run, 200, thethis);
        };

        Plugin.prototype.countDown = function (sec) {
            this.runing = true;
            this.duration = sec;
            var thethis = this;
            this.starttime = new Date();
            this.endtime = new Date(this.starttime.getTime() + sec * 1000);

            this.run(this);
        };
        return Plugin;
    })();
    Timer.Plugin = Plugin;
})(Timer || (Timer = {}));
