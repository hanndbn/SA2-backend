///<reference path="ref.ts" />

module Mainlayout
{
	export class Layout
	{
		onReady = new plus.Emitter();

		header: Header.Plugin;
		sidebar: Sidebar.Plugin;
		contentDiv: JQuery;
		dom: JQuery;
		
		init(unit:string, request:string, hash:string)
		{
			var thethis = this;
		
			//this.dom = $('<div></div>');

			//tạo barrier để đồng bộ
			var layoutloaded = new plus.Emitter();
			//var dataloaded = new plus.Emitter();
			var dependloaded = new plus.Emitter();

			plus.bar([dependloaded, layoutloaded], function ()
			{
				thethis.header = new Header.Plugin();
				thethis.sidebar = new Sidebar.Plugin();

				plus.bar([thethis.header.onReady, thethis.sidebar.onReady], function ()
				{
					thethis.dom.find('.id_sidebar').append(thethis.sidebar.dom);
					thethis.dom.find('.id_header').append(thethis.header.dom);
				});

				thethis.header.init(unit);
				thethis.sidebar.init(unit, request,hash);
			});

			//Khởi tạo các control phụ thuộc
			plus.req(['header.js', 'sidebar.js'], function ()
			{
				dependloaded.emit();
			});

			//lấy layout
			$.get('mainlayout.html', function (data)
			{
				thethis.dom = $(data);
				
				thethis.contentDiv = thethis.dom.find('.id_content');
				thethis.onReady.emit();
				layoutloaded.emit();
			});


		}


	}
}  