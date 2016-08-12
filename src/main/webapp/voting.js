///<reference path="ref.ts" />
var Voting;
(function (Voting) {
    var Plugin = (function () {
        function Plugin() {
            this.onVoteUp = new plus.Emitter();
            this.onVoteDown = new plus.Emitter();
            this.onReady = new plus.Emitter();
            this.stars = [];
        }
        Plugin.prototype.init = function () {
            var thethis = this;
            this.vote = 0;
            $.post('voting.html', function (data) {
                thethis.dom = $(data);
                thethis.bound();
                thethis.onReady.emit();
            })['fail'](function (error) {
                console.error(error);
                thethis.dom.css('background-color', '#ff0000');
            });
        };

        Plugin.prototype.bound = function () {
            var thethis = this;

            this.thumpUp = this.dom.find('.id_up');
            this.thumpDown = this.dom.find('.id_down');
            this.level = this.dom.find('.id_level');
            this.wait = this.dom.find('.id_wait');
            this.error = this.dom.find('.id_error');
            this.content = this.dom.find('.id_content');

            this.thumpDown.on('click', function () {
                thethis.onVoteDown.emit();
            });

            this.thumpUp.on('click', function () {
                thethis.onVoteUp.emit();
            });

            this.refresh(0);
        };

        Plugin.prototype.refresh = function (level) {
            this.vote = level;
            this.content.removeClass('hidden');
            this.wait.addClass('hidden');
            this.error.addClass('hidden');
            this.level.html(level.toString());
        };

        Plugin.prototype.displayLoading = function () {
            this.content.addClass('hidden');
            this.wait.removeClass('hidden');
            this.error.addClass('hidden');
        };

        Plugin.prototype.displayError = function () {
            this.content.addClass('hidden');
            this.wait.addClass('hidden');
            this.error.removeClass('hidden');
        };

        Plugin.prototype.displayVote = function (level) {
            this.refresh(level);
        };
        return Plugin;
    })();
    Voting.Plugin = Plugin;
})(Voting || (Voting = {}));
