///<reference path="ref.ts" />

module AppLayout
{
	export class Layout
	{
		onReady = new plus.Emitter();

		contentDiv: JQuery;
		dom: JQuery;

		init()
		{
			var thethis = this;

			//this.dom = $('<div></div>');

			//tạo barrier để đồng bộ
			var layoutloaded = new plus.Emitter();


			plus.bar([ layoutloaded], function ()
			{
			
					thethis.contentDiv = thethis.dom.find('.id_content');
					thethis.onReady.emit({});
			});

			//lấy layout
			$.get('applayout.html', function (data)
			{
				thethis.dom = $(data)
				layoutloaded.emit();
			});
		}

	}
}  