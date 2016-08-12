///<reference path="ref.ts" />

module Unitheader
{
	export class Plugin
	{
		onRate = new plus.Emitter();
		onReady = new plus.Emitter();
		unitname: JQuery;
		dom: JQuery;
		uniname: JQuery;

		init(unitid: string, units: any[])
		{
			var thethis = this;
			//tạo barrier để đồng bộ
			var scriptloaded = new plus.Emitter();
			var layoutloaded = new plus.Emitter();

			plus.bar([layoutloaded], function ()
			{
				thethis.unitname = thethis.dom.find('.id_unitname');
				var menu = thethis.dom.find('.id_menu');
				for (var u in units)
				{
					if (units[u].id == unitid)
					{
						thethis.unitname.html(' ' + units[u].name);
					}
					var d = $('<li><a href="#requestmgr/' + units[u].id + '"><span class="icon-paperplane"></span><nbsp></nbsp> Quản lý đơn vị ' + units[u].name + '</a></li>');
					menu.append(d);
				}

				thethis.onReady.emit();
			});

			
			//tải layout
			$.post('unitzone.html', function (data)
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