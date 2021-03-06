﻿///<reference path="ref.ts" />

module Header
{
	export class Plugin
	{
		onRate = new plus.Emitter();
		onReady = new plus.Emitter();

		dom: JQuery;

		unitheader : Unitheader.Plugin;
		userheader: Userheader.Plugin;

		init(unitid:string)
		{
			var thethis = this;

			var layoutloaded = new plus.Emitter();
			var scriptloaded = new plus.Emitter();
			var dataloaded = new plus.Emitter();
			plus.bar([layoutloaded, scriptloaded, dataloaded], function ()
			{
				thethis.userheader = new Userheader.Plugin();
				//thethis.feednotify = new CVFeed.Plugin();
				//thethis.errornotify = new ErrorNotify.Plugin();
				thethis.unitheader = new Unitheader.Plugin();
				plus.bar([thethis.userheader.onReady, thethis.unitheader.onReady], function ()
				{
					thethis.dom.find('.id_nav').append(thethis.userheader.dom);
					thethis.dom.find('.id_nav').append(thethis.unitheader.dom);

				});

				thethis.userheader.init(fullname, units);
				thethis.unitheader.init(unitid, units);
			//	thethis.feednotify.init(thethis.dom.find('.id_cv')[0]);
			//	thethis.errornotify.init(thethis.dom.find('.id_error')[0]);

			});

			$.get('header.html', function (data)
			{
				thethis.dom = $(data);
				thethis.onReady.emit();
				layoutloaded.emit();
			});

			var fullname: string;
			var units: any[];

			$.post('HRWeb/user/get', function (data)
			{
				data = JSON.parse(data);
				units = data.units;

				fullname = data.fullname;
				dataloaded.emit();
			})['fail'](function (error)
				{
					alert('cant get user');
				});


			plus.req(['userheader.js', 'unitheader.js'], function ()
			{
				scriptloaded.emit();
			});


		}
	}
}  