﻿///<reference path="ref.ts" />
module Userheader
{
	export class Plugin
	{
		onRate = new plus.Emitter();
		onReady = new plus.Emitter();
		dom : JQuery;
		table: JQuery;
		username: JQuery;
		logout: JQuery;
		private unit:JQuery;

		init(fullname:string, units: any[])
		{
			var thethis = this;

			//tạo barrier để đồng bộ
			var scriptloaded = new plus.Emitter();
			var layoutloaded = new plus.Emitter();

			plus.bar([layoutloaded], function ()
			{
				thethis.username = thethis.dom.find('.id_username');
				
				
				thethis.username.html(' ' + fullname);

				thethis.logout = thethis.dom.find('.id_logout');
				thethis.unit = thethis.dom.find('.id_unit');

				for (var u in units)
				{
					var d = $('</li><li><a href="#requestmgr/' + units[u].id + '"><span class="glyphicon glyphicon - transfer"></span><nbsp></nbsp>Go to unit ' + units[u].name + '</a></li>');
					thethis.unit.after(d);
				}

				thethis.logout.on('click', function ()
				{
					$.get('HRWeb/user/logout', function ()
					{
						window.location.href = '#login';
					})['fail'](function ()
						{
							alert('cannot loggout');
						});
				});
				thethis.onReady.emit({});
			});

			//tải layout
			$.post('userzone.html', function (data)
			{
				thethis.dom = $(data);
				layoutloaded.emit();

			})['fail'](function (error)
				{
					console.error(error);
				});
		}
	}
}  