///<reference path="ref.ts" />

module CVField
{
	export class Plugin
	{
		onReady = new plus.Emitter();

		table: Table.Plugin;
		//new_field: NewField.Plugin;

		button: JQuery;
		dom: JQuery;
		unit: string;
		no = 0;

		init(unit: string)
		{
			var thethis = this;

			thethis.unit = unit;
			//tạo barrier để đồng bộ
			var layoutloaded = new plus.Emitter();
			var dataloaded = new plus.Emitter();
			var dependloaded = new plus.Emitter();

			plus['wait'].emit();

			plus.bar([dependloaded, layoutloaded, dataloaded], function ()
			{
				//Sau khi các plugin đã load xong
				plus.bar([thethis.table.onReady, /*thethis.new_field.onReady*/], function ()
				{
					//thethis.dom.find('.id_newmodal').append(thethis.new_field.dom);
					thethis.dom.find('.id_table').append(thethis.table.dom);

					thethis.table.setUpTable(['#', 'Tiêu đề', 'Sử dụng', 'Khác ']);
					for (var i in fieldlist)
					{
						thethis.addField(fieldlist[i]);
					}

					plus['ready'].emit();
				});

				thethis.table.init();
			});

			//Khởi tạo các control phụ thuộc
			plus.req(['table.js', 'actionmenu.js', 'textbox.js', 'switchbox.js'], function ()
			{
				thethis.table = new Table.Plugin();

				dependloaded.emit();
			});

			//lấy layout
			$.get('requiredfield.html', function (data)
			{
				thethis.dom = $(data);
				thethis.onReady.emit();
				thethis.dom.find('.id_new').on('click', function ()
				{
					$.get('HRWeb/requiredfield/create', { unit: unit, title: "New Field", state: 0 }, function (data)
					{
						var id = JSON.parse(data).result;
						$.get('HRWeb/requiredfield/get', { id: id }, function (data)
						{
							var f = JSON.parse(data);
							thethis.addField(f);
						})['fail'](function ()
							{
								alert("cannot get new field");
							});

					})['fail'](function ()
						{
							alert("cannot create new field");
						});
				});

				layoutloaded.emit();
			});

			//lấy dữ liệu

			var fieldlist;
			$.get('HRWeb/requiredfield/list', { unit: unit }, function (data)
			{
				fieldlist = JSON.parse(data);
				dataloaded.emit();
			});
		}

		addField(f: any) 
		{
			var thethis = this;
			var acm = new ActionMenu.Plugin();
			var titletb = new TextBox.Plugin();
			var stateswitch = new SwitchBox.Plugin();

			thethis.no++;
			f['_no'] = thethis.no;
			thethis.table.addRow(f['_no'], {});

			plus.bar([acm.onReady, titletb.onReady, stateswitch.onReady], function ()
			{

				titletb.onChanged.on(function ()
				{
					$.get('HRWeb/requiredfield/edit', { id: f.id, title: titletb.text }, function ()
					{
						titletb.displayText(titletb.text);
					})['fail'](function ()
						{
							alert('can not edit field');
							titletb.displayText(titletb.oldtext);
						});
				});

				//if is public
				if (f.state == 0)
					stateswitch.check();
				else stateswitch.uncheck();

				stateswitch.onChecked.on(function ()
				{
					$.post('HRWeb/requiredfield/edit', { id: f.id, state: 0, unit: thethis.unit })['fail'](function ()
					{
						alert("can't change field's state");
					});
				});

				stateswitch.onUnchecked.on(function ()
				{
					$.post('HRWeb/requiredfield/edit', { id: f.id, state: 1 })['fail'](function ()
					{
						stateswitch.check
						alert("can't change field's state");
					});
				});

				//acm.addItem('Công cộng / Nội bộ', 'icon-lock', function ()
				//{
				//	if (f.state == 0)
				//	{
				//		f.state = 1;
				//		stateswitch.uncheck();
				//	} else
				//	{
				//		f.state = 0
				//		stateswitch.check();
				//	}
				//});

				acm.addItem('Xóa', 'icon-x', function ()
				{
					if (!confirm("Click <OK> to delete."))
					{
						return;
					}
					$.post('HRWeb/requiredfield/delete', { id: f.id }, function ()
					{
						row['fadeOut']();
						thethis.table.deleteRow(f['_no']);
					})['fail'](function ()
						{
							alert("can't delete field");
						});
				});

				titletb.displayText(f.title);
			
				var row = thethis.table.editRow(f['_no'], {
					0: f['_no'],
					10: titletb.dom,
					40: stateswitch.dom,
					80: acm.dom
				});
				f['_dom'] = row;
			});
			stateswitch.init();
			acm.init();
			titletb.init();
		}
	}

}  