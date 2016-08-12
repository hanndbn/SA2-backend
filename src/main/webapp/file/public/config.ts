///<reference path="ref.ts" />

module Config
{
	export class Plugin
	{
		onReady = new plus.Emitter();

		dom: JQuery;

		init(unit: string)
		{
			var thethis = this;

			//tạo barrier để đồng bộ
			var layoutloaded = new plus.Emitter();
			var dataloaded = new plus.Emitter();
			var dependloaded = new plus.Emitter();

			plus.bar([dependloaded,layoutloaded, dataloaded], function ()
			{
				var arc = new SwitchBox.Plugin();
				var atr = new SwitchBox.Plugin();


				var neng = new TextBox.Plugin();
				var niq = new TextBox.Plugin();
				var peng = new TextBox.Plugin();
				var piq = new TextBox.Plugin();

				plus.bar([neng.onReady, niq.onReady, peng.onReady, piq.onReady, arc.onReady, atr.onReady], function ()
				{

					thethis.dom.find('.id_autosendrc').append(arc.dom);
					thethis.dom.find('.id_autosendtr').append(atr.dom);
					thethis.dom.find('.id_neng').append(neng.dom);
					thethis.dom.find('.id_peng').append(peng.dom);
					thethis.dom.find('.id_niq').append(niq.dom);
					thethis.dom.find('.id_piq').append(piq.dom);

					arc.onChecked.on(function ()
					{
						$.get('/HRWeb/HRWeb/config', { autosendreceived: 1, unit: unit  })['fail'](function ()
						{
							alert("can't change config");
						});
					});

					arc.onUnchecked.on(function ()
					{
						$.get('/HRWeb/HRWeb/config', { autosendreceived: 0, unit: unit  })['fail'](function ()
						{
							alert("can't change config");
						});
					});

					atr.onChecked.on(function ()
					{
						$.get('/HRWeb/HRWeb/config', { autosendtestresult: 1, unit: unit  })['fail'](function ()
						{
							alert("can't change config");
						});
					});

					atr.onUnchecked.on(function ()
					{
						$.get('/HRWeb/HRWeb/config', { autosendtestresult: 0, unit: unit })['fail'](function ()
						{
							alert("can't change config");
						});
					});

					neng.onChanged.on(function ()
					{
						$.get('/HRWeb/HRWeb/config', { neng: neng.text, unit: unit })['fail'](function ()
						{
							alert("can't change config");
						});
					});

					niq.onChanged.on(function ()
					{
						$.get('/HRWeb/HRWeb/config', { niq: niq.text, unit: unit  })['fail'](function ()
						{
							alert("can't change config");
						});
					});

					peng.onChanged.on(function ()
					{
						$.get('/HRWeb/HRWeb/config', { peng: peng.text, unit: unit  })['fail'](function ()
						{
							alert("can't change config");
						});
					});

					piq.onChanged.on(function ()
					{
						$.get('/HRWeb/HRWeb/config', { piq: piq.text, unit: unit })['fail'](function ()
						{
							alert("can't change config");
						});
					});

					thethis.onReady.emit();
				});

				arc.init();
				atr.init();
				neng.init();
				niq.init();
				peng.init();
				piq.init();

			});

			plus.req(['switchbox.js','textbox.js'], function ()
			{
				dependloaded.emit();
			});

			//lấy layout
			$.get('config.html', function (data)
			{
				thethis.dom = $(data);
				layoutloaded.emit();
			});

			//lấy dữ liệu
			var n;
			var formlist;
			$.get('/HRWeb/HRWeb/mail/listform', { unit: unit }, function (data)
			{
				formlist = JSON.parse(data);
			});


		}


	}
}  