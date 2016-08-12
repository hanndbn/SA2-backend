///<reference path="ref.ts" />
module Voting
{
	export class Plugin
	{
		onVoteUp = new plus.Emitter();
		onVoteDown = new plus.Emitter();
		onReady = new plus.Emitter();
		public dom: JQuery;
		vote : number;
		private stars: JQuery[] = [];
		private thumpUp: JQuery;
		public thumpDown: JQuery;
		public level: JQuery;

		public content : JQuery;
		public error: JQuery;
		public wait: JQuery;

		init()
		{
			var thethis = this;
			this.vote = 0;
			$.post('voting.html', function (data)
			{
				thethis.dom = $(data);
				thethis.bound();
				thethis.onReady.emit();
			})['fail'](function (error)
				{
					console.error(error);
					thethis.dom.css('background-color', '#ff0000');
				});
		}

		bound()
		{
			var thethis = this;
			
			this.thumpUp = this.dom.find('.id_up');
			this.thumpDown = this.dom.find('.id_down');
			this.level = this.dom.find('.id_level');
			this.wait = this.dom.find('.id_wait');
			this.error = this.dom.find('.id_error');
			this.content = this.dom.find('.id_content');
			
			this.thumpDown.on('click', function ()
			{
				thethis.onVoteDown.emit();
				
			});

			this.thumpUp.on('click', function ()
			{
				thethis.onVoteUp.emit();
			});

			this.refresh(0);
		}

		refresh(level : number)
		{
			this.vote = level;
			this.content.removeClass('hidden');
			this.wait.addClass('hidden');
			this.error.addClass('hidden');
			this.level.html(level.toString());
		}


		displayLoading()
		{
			this.content.addClass('hidden');
			this.wait.removeClass('hidden');
			this.error.addClass('hidden');
		}

		displayError()
		{
			this.content.addClass('hidden');
			this.wait.addClass('hidden');
			this.error.removeClass('hidden');
		}

		displayVote(level: number)
		{
			this.refresh(level);
		}


	}
}  