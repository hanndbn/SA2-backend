///<reference path="ref.ts" />

module ErrorDlg
{
	export class Plugin
	{
		onReady = new plus.Emitter();
		private title: JQuery;
		private message:JQuery;
		private dom: JQuery;

		init()
		{
			var thethis = this;
			var layoutLoaded = new plus.Emitter();
			layoutLoaded.on(function ()
			{
				thethis.title = thethis.dom.find('.id_errorTitle');
				thethis.message = thethis.dom.find('.id_errorMessage');
				thethis.onReady.emit();
			});

			$.post('error.html', function (data)
			{
				thethis.dom = $(data);
				layoutLoaded.emit();
			});
		}

		show(title: string, message: string)
		{
			this.title.html(title);
			this.message.html(message);
			this.dom['modal']('show');
		}

		hide()
		{
			this.dom['modal']('hide');
		}
	}
}