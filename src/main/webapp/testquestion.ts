///<reference path="ref.ts" />

module TestQuestion
{
	export class Plugin
	{

		onReady = new plus.Emitter();

		dom: JQuery;
		private captchaimg: JQuery;
		private captchainput: JQuery;
		private captchaid: number;
		private base64: string;
		init()
		{
			var thethis = this;

			//tạo barrier để đồng bộ
			//var scriptloaded = new plus.Emitter();
			var layoutloaded = new plus.Emitter();
			var dataloaded = new plus.Emitter();

			plus.bar([dataloaded, layoutloaded], function ()
			{
				thethis.captchaimg.attr("src", thethis.base64);
				thethis.onReady.emit({});
			});


			//lấy nội dung html
			var content: string;
			$.get('captcha.html', function (data)
			{
				thethis.dom = $(data);
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
			var thethis = this;
			$.get('HRWeb/captcha/create', function (data)
			{
				data = JSON.parse(data);
				thethis.captchaid = data.id;
				thethis.base64 = data.base64;
				thethis.captchaimg.attr("src", thethis.base64);
			});
		}

		getAnswer()
		{
			return {
				id: this.captchaid,
				answer: this.captchainput.val()
			}
		}
	}
}  