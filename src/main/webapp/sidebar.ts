///<reference path="ref.ts" />

module Sidebar
{
	export class Plugin {
		onReady = new plus.Emitter();
		table:JQuery;

		private stars:JQuery[] = [];
		private starlist:JQuery;

		dom:JQuery;

		init(unit:string, request:string, hash:string) {
			var thethis = this;
			//tạo barrier để đồng bộ
			var scriptloaded = new plus.Emitter();
			var layoutloaded = new plus.Emitter();
			plus.bar([scriptloaded, layoutloaded], function () {
				thethis.dom['metisMenu']({ toggle: true });

			});

			//lấy nội dung html
			var content:string;
			$.get('sidebarforhrteam.html', function (data) {
				thethis.dom = $(data);

				var menu = {};
				menu['#requestmgr'] = thethis.dom.find('.id_request');
				menu['#requestmgr'].prop('href', '#requestmgr/' + unit);
				menu['#requestmgr'].parent().removeClass('active');

				menu['#cvmgr'] = thethis.dom.find('.id_cv');
				menu['#cvmgr'].prop('href', '#cvmgr/' + unit + "/" + request);
				menu['#cvmgr'].parent().removeClass('active');

				menu['#engquestmgr'] = thethis.dom.find('.id_eng');
				menu['#engquestmgr'].prop('href', '#engquestmgr/' + unit);
				menu['#engquestmgr'].parent().removeClass('active');

				menu['#iqquestmgr'] = thethis.dom.find('.id_iq');
				menu['#iqquestmgr'].prop('href', '#iqquestmgr/' + unit);
				menu['#iqquestmgr'].parent().removeClass('active');

				menu['#proquestmgr'] = thethis.dom.find('.id_pro');
				menu['#proquestmgr'].prop('href', '#proquestmgr/' + unit);
				menu['#proquestmgr'].parent().removeClass('active');

				menu['#fieldmgr'] = thethis.dom.find('.id_field');
				menu['#fieldmgr'].prop('href', '#fieldmgr/' + unit);
				menu['#fieldmgr'].parent().removeClass('active');

				menu['#mailmgr'] = thethis.dom.find('.id_email');
				menu['#mailmgr'].prop('href', '#mailmgr/' + unit);
				menu['#mailmgr'].parent().removeClass('active');

				menu['#config'] = thethis.dom.find('.id_config');
				menu['#config'].prop('href', '#config/' + unit);
				menu['#config'].parent().removeClass('active');

				if (menu[hash] != undefined)
					menu[hash].parent().addClass('active');

				thethis.onReady.emit();
				layoutloaded.emit();
			})['fail'](function (error) {
				console.error(error);
				thethis.dom.css('background-color', '#ff0000');
			});

			//tải script
			plus.req(['res/jquery.metisMenu.js'], function () {
				scriptloaded.emit({});
			});
		}

		colapsein(loc:number) {
			$(this.dom.find('ul').children()[loc]).addClass('collapse in');

		}
	}
}  