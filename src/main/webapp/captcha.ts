///<reference path="ref.ts" />

module Captcha
{
	export class Plugin
	{
		onReady = new plus.Emitter();

		dom: JQuery;
		private captchaimg: JQuery;
		private captchainput: JQuery;
		private captchaid: number;
		private base64: string;
		private regenBt: JQuery;
		private issumited = false;

		init()
		{
			var thethis = this;

			var layoutloaded = new plus.Emitter();
			var dataloaded = new plus.Emitter();

			plus.bar([layoutloaded], function ()
			{
				thethis.onReady.emit({});
			});

			plus.bar([dataloaded, layoutloaded], function ()
			{
				thethis.regenBt.on('click', function ()
				{
					thethis.regen();
				});

				thethis.captchaimg.on('click', function ()
				{
					thethis.regen();
				});

				thethis.captchaimg.attr("src", thethis.base64);
			});

			var content: string;
			$.get('captcha.html', function (data)
			{
				thethis.dom = $(data);
				thethis.regenBt = thethis.dom.find('.id_regen');
				thethis.captchainput = thethis.dom.find('.id_capchainput');
				thethis.captchaimg = thethis.dom.find('.id_captchaimg');

				layoutloaded.emit({});
			})['fail'](function (error)
				{
					console.error(error);
					thethis.dom.css('background-color', '#ff0000');
				});

			$.get('HRWeb/captcha/create', function (data)
			{
				data = JSON.parse(data);
				thethis.captchaid = data.id;
				thethis.base64 = data.base64;
				dataloaded.emit();
			});
		}

		regen()
		{
			this.issumited = false;
			var thethis = this;
			this.captchainput.val("");
			this.captchainput.prop('disabled',true);
			$.get('HRWeb/captcha/create', function (data)
			{
				thethis.captchainput.prop('disabled', false);
				setTimeout(function ()
				{
					if (thethis.issumited == false)
						thethis.regen();
				},400000);
				data = JSON.parse(data);
				thethis.captchaid = data.id;
				thethis.base64 = data.base64;
				thethis.captchaimg.attr("src", thethis.base64);
			});
		}

		getAnswer()
		{
			this.issumited = true;
			return {
				id: this.captchaid,
				answer: this.captchainput.val()
			}
		}
	}
}  