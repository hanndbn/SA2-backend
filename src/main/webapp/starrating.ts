///<reference path="ref.ts" />
module StarRating
{
	export class Plugin
	{
		onRate = new plus.Emitter();
		onReady = new plus.Emitter();
		 dom: JQuery;
		nstar: number;
		private stars: JQuery[] = [];
		private starlist: JQuery;
		public wait: JQuery;
		public error: JQuery;
		private loading = false;
		init()
		{
			var thethis = this;

			$.post('starrating.html', function (data)
			{
				thethis.dom=$(data);
				thethis.bound();
				thethis.onReady.emit({});
			})['fail'](function (error)
				{
					console.error(error);
					thethis.dom.css('background-color', '#ff0000');
				});
		}

		bound()
		{
			var thethis = this;

			var ele = thethis.starlist = this.dom.find('.id_starlist');
			thethis.stars[0] = ele.find('.id_0');
			thethis.stars[1] = ele.find('.id_1');
			thethis.stars[2] = ele.find('.id_2');
			thethis.stars[3] = ele.find('.id_3');
			thethis.stars[4] = ele.find('.id_4');
			
			thethis.wait = this.dom.find('.id_wait');
			thethis.error = this.dom.find('.id_error');
			
			for (var i = 0; i < 5; i++)
			{
				(function ()
				{
					var e = i;
					thethis.stars[e].on('click', function ()
					{
						thethis.displayLoading();
						thethis.onRate.emit({ star: e + 1 });
					});

					thethis.stars[e].on('mousemove', function ()
					{
						thethis.stars[e].addClass('starrating_superhiglight');
						for (var j = 0; j <= e; j++)
							thethis.stars[j].addClass('starrating_higlight');
					});

					thethis.stars[e].on('mouseleave', function ()
					{
						thethis.refreshStar();
					});
				})();
			}

		}


		refreshStar(nstar?: number)
		{
			if(this.loading == true) return;
			this.starlist.removeClass('hidden');
			this.wait.addClass('hidden');
			this.error.addClass('hidden');

			if (nstar === undefined)
			{

				for (var i = 0; i < 5; i++)
					this.stars[i].removeClass('starrating_higlight starrating_superhiglight');


				for (var i = 0; i < nstar; i++)
					this.stars[i].addClass('starrating_higlight starrating_superhiglight');

			}
			else
			{
				this.nstar = nstar;
				this.refreshStar();
			}

		}

		displayLoading()
		{
			this.loading = true;
			this.starlist.addClass('hidden');
			this.wait.removeClass('hidden');
			this.error.addClass('hidden');
		}

		displayError()
		{
			this.loading=true;
			this.starlist.addClass('hidden');
			this.wait.addClass('hidden');
			this.error.removeClass('hidden');
		}

		displayStar()
		{
			this.loading = false;
			this.refreshStar();
		}

	}
}  