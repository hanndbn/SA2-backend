///<reference path="ref.ts" />
module Timer
{
	export class Plugin
	{
		onStoped = new plus.Emitter();
		onReady = new plus.Emitter();
		dom: JQuery;
		hop: number = 20; /*in second*/
		starttime: Date;
		endtime: Date;
		duration: number; /*in milisecond*/
		timeDiv: JQuery;
		onTick = new plus.Emitter();
		init()
		{
			var thethis = this;

			$.post('timer.html', function (data)
			{
				thethis.dom = $(data);
				thethis.timeDiv = thethis.dom.find('.id_time');

				thethis.onReady.emit({});
			})['fail'](function (error)
				{
					console.error(error);
					thethis.dom.css('background-color', '#ff000');
				});
		}

		runing :boolean =true;

		stop()
		{
			this.runing = false;
		}

		private run(thethis)
		{
			if(thethis.runing == false) return;
			var elapsed = new Date().getTime() - thethis.starttime.getTime(); /*in milisecond*/
			elapsed = Math.floor(elapsed / 1000);
			var delta = thethis.duration - elapsed;

			if (delta <= 0)
			{
				thethis.onStoped.emit();
				thethis.timeDiv.html(0 + ":" + 0);
				return;
			}

			thethis.onTick.emit({ tick: elapsed  });
			var min = Math.floor(delta / 60);
			var sec =  Math.floor((delta % 60));
			thethis.timeDiv.html(min + ":" + sec);
			setTimeout(thethis.run, 200, thethis);

		}

		countDown(sec: number)
		{
			this.runing = true;
			this.duration = sec;
			var thethis = this;
			this.starttime = new Date();
			this.endtime = new Date(this.starttime.getTime() + sec * 1000);

			this.run(this);
		}

	}
}  