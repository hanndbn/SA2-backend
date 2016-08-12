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
				var aot = new SwitchBox.Plugin();
				var orj = new SwitchBox.Plugin();
				var n = new TextBox.Plugin();
				plus.bar([arc.onReady, aot.onReady, orj.onReady, n.onReady], function ()
				{
					thethis.dom.find('.id_autosendrc').append(arc.dom);
					thethis.dom.find('.id_autoopentest').append(aot.dom);
					thethis.dom.find('.id_autosendtr').append(orj.dom);
					thethis.dom.find('.id_n').append(n.dom);

					if (darc == "false")
						arc.uncheck();
					if (daot == "false")
						aot.uncheck();
					if (dorj == "false")
						orj.uncheck();
					n.displayText(dn + "," + dp);
					arc.onChecked.on(function ()
					{
						$.get('HRWeb/config/set', { ref: "auto send received mail", val: "true", unit: unit  })['fail'](function ()
						{
							alert("can't change config");
						});
					});

					arc.onUnchecked.on(function ()
					{
						$.get('HRWeb/config/set', { ref: "auto send received mail", val: "false",unit: unit  })['fail'](function ()
						{
							alert("can't change config");
						});
					});

					orj.onChecked.on(function ()
					{
						$.get('HRWeb/config/set', { ref: "send reject mail", val: "true", unit: unit })['fail'](function ()
						{
							alert("can't change config");
						});
					});

					orj.onUnchecked.on(function ()
					{
						$.get('HRWeb/config/set', { ref: "send reject mail", val: "false", unit: unit })['fail'](function ()
						{
							alert("can't change config");
						});
					});

					aot.onChecked.on(function ()
					{
						$.get('HRWeb/config/set', { ref: "auto open test", val: "true", unit: unit  })['fail'](function ()
						{
							alert("can't change config");
						});
					});

					aot.onUnchecked.on(function ()
					{
						$.get('HRWeb/config/set', { ref: "auto open test", val: "false", unit: unit })['fail'](function ()
						{
							alert("can't change config");
						});
					});

					n.onChanged.on(function ()
					{
						var text = n.text.split(",");
						var nval = text[0];
						var pval = text[1];
						nval = nval || "";
						pval = pval || "";
						nval.trim();
						pval.trim();

						$.get('HRWeb/config/set', { ref: "np", val: nval+","+ pval, unit: unit }, function ()
						{
							n.displayText(n.text);
						})['fail'](function ()
						{
							n.displayText(n.oldtext);
							alert("can't change config");
						});
					});

				
				});

				arc.init();
				aot.init();
				orj.init();
				n.init();
			});

			plus.req(['switchbox.js','textbox.js'], function ()
			{
				dependloaded.emit();
			});

			//lấy layout
			$.get('config.html', function (data)
			{
				thethis.dom = $(data);
				thethis.onReady.emit();
				layoutloaded.emit();
			});

			//lấy dữ liệu
			var darc;
			var daot;
			var dorj;
			var dn;
			var dp;
			$.get('HRWeb/config/get', { unit: unit, ref : "auto open test" }, function (data)
			{
				daot =  JSON.parse(data).result;
				$.get('HRWeb/config/get', { unit: unit, ref: "auto send received mail" }, function (data)
				{
					darc = JSON.parse(data).result;
					$.get('HRWeb/config/get', { unit: unit, ref: "send reject mail" }, function (data)
					{
						dorj = JSON.parse(data).result;
						$.get('HRWeb/config/get', { unit: unit, ref: "n" }, function (data)
						{
							dn = JSON.parse(data).result;
							$.get('HRWeb/config/get', { unit: unit, ref: "p" }, function (data)
							{
								dp = JSON.parse(data).result;
								dataloaded.emit();
							});
						});
					});
				});
			});


		}


	}
}  