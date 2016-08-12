///<reference path="ref.ts" />

module CVFeed
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
		button : JQuery;
		init(htmlElement: HTMLElement)
		{
			var thethis = this;
			this.element = $(htmlElement);


			/*Các bước sử dụng metisMenu:
			1. load metisMenu.js
			2. tạo DOM
			3. gọi $(DOM).metisMenu({toggle:true});
			*/

			//tạo barrier để đồng bộ
			var scriptloaded = new plus.Emitter();
			var layoutloaded = new plus.Emitter();
			var dataloaded = new plus.Emitter();

			plus.bar([scriptloaded, layoutloaded, dataloaded], function ()
			{
				var dom = $(content);
				thethis.element.append(dom);
				thethis.button = dom;
				if (n != 0)
				{
					thethis.wake();
					dom.find('id_message').html(n.toString());
				}

				thethis.onReady.emit({});
			});


			plus.req([''], function ()
			{
				scriptloaded.emit();
			});

			var content: string;
			//lấy layout
			$.get('feednotify.html', function (data)
			{
				content = data;
				layoutloaded.emit();
			});

			//lấy dữ liệu
			var n = 0;
			var errorlist = [];
			//==
			dataloaded.emit();
			return;
			//===
			$.post('HRWeb/feed/getnunread', function (data)
			{
				var data = JSON.parse(data);
				n = data.return;
				dataloaded.emit();
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