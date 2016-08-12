///<reference path="ref.ts" />

module SwitchBox
{
	export class Plugin
	{
		onChecked = new plus.Emitter();
		onUnchecked = new plus.Emitter();
		onReady = new plus.Emitter();
		dom: JQuery;

		checked: boolean;
		checkbox: JQuery;
		sw : JQuery;
		init()
		{
			var thethis = this;
			thethis.checked = true;
			$.post('switchbox.html', function (data)
			{
				thethis.dom = $(data);
				thethis.sw = thethis.dom.find('.onoffswitch-label');
				thethis.checkbox = thethis.dom.find('.onoffswitch-checkbox');

				thethis.sw.on('click', function ()
				{
					thethis.checked = !thethis.checked;
					thethis.checkbox.prop('checked', thethis.checked);
					if (thethis.checked) thethis.onChecked.emit();
					else thethis.onUnchecked.emit();
				});

				thethis.onReady.emit();
			})['fail'](function (error)
				{
					console.error(error);
					thethis.dom.css('background-color', '#ff0000');
				});
		}

		check()
		{
			if (!this.checked)
				this.sw.trigger('click');
				
		}

		uncheck()
		{
			if (this.checked)
				this.sw.trigger('click');
		}
	}
}  