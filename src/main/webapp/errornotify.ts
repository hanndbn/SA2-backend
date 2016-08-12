///<reference path="ref.ts" />

module ErrorNotify
{
	export class Plugin
	{
		onRate = new plus.Emitter();
		onReady = new plus.Emitter();

		element: JQuery;
		table: JQuery;

		private stars: JQuery[] = [];
		private starlist: JQuery;
		public wait: JQuery;
		public error: JQuery;
		button: JQuery;

		init(htmlElement: HTMLElement)
		{
			var thethis = this;
			this.element = $(htmlElement);

			//tạo barrier để đồng bộ
			var layoutloaded = new plus.Emitter();
			var dataloaded = new plus.Emitter();

			plus.bar([ layoutloaded, dataloaded], function ()
			{
				
				var dom = $(content);

				var menu = $('<ul></ul>');
				menu.addClass('dropdown-menu');

				thethis.button = dom.find('.id_button');

				if (n != 0)
				{
					thethis.wake();
					dom.find('.id_message').html(n.toString());
				}

				for (var i in errorlist)
				{
					var item = $('<li><a href="errorlist.html">'+ errorlist[i].time + ': '+ errorlist[i].message + '</a></li>');
					menu.append(item);
				}
				
				menu.append($('<li class="divider"></li><li><a href="errorlist.html">See all error</a></li>'));
				thethis.element.append(dom);
				thethis.element.append(menu);
				
				thethis.onReady.emit({});
			});



			var content: string;
			//lấy layout
			$.get('errornotify.html', function (data)
			{
				content = data;
				layoutloaded.emit();
			});

			//lấy dữ liệu
			var n = 0;
			var errorlist = [];

			//
			dataloaded.emit();
			return;
			//

			$.post('HRWeb/error/getnunread', function (data)
			{
				var data = JSON.parse(data);
				n = data.return;
				$.post('HRWeb/error/listunread', { p: 0, ps: 6, field: ['message', 'time'] }, function (data)
				{
					errorlist = JSON.parse(data);
					dataloaded.emit();
				})
			});


		}

		dim()
		{
			this.button.addClass('btn-danger');
		}

		wake()
		{
			this.button.removeClass('btn-danger');
		}
	}
}  