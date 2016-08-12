///<reference path="jquery.d.ts" />
///<reference path="inlinewait.ts" />

module Login
{
	export class Plugin
	{
		onReady = new plus.Emitter();
		private usernameTb:JQuery;
		private passwordTb:JQuery;
		private loginBt:JQuery;
		dom:JQuery;
		private errorMd:ErrorDlg.Plugin;

		init()
		{
			var thethis = this;
			var layoutLoaded = new plus.Emitter();
			var dependLoaded = new plus.Emitter();

			plus.bar([dependLoaded, layoutLoaded], function ()
			{
				thethis.usernameTb = thethis.dom.find('.id_usernameTb');
				thethis.passwordTb = thethis.dom.find('.id_passwordTb');
				thethis.usernameTb.keyup(function (e)
				{
					if (e['keyCode'] == 13)
					{
						thethis.loginBt.trigger('click');
					}
				});
				thethis.passwordTb.keyup(function (e)
				{
					if (e['keyCode'] == 13)
					{
						thethis.loginBt.trigger('click');
					}
				});
				thethis.loginBt = thethis.dom.find('.id_loginBt');
				thethis.errorMd = new ErrorDlg.Plugin();
				thethis.errorMd.onReady.on(function ()
				{
					thethis.onReady.emit();
				});
				thethis.loginBt.on('click', function ()
				{
					$('.id_loginBt')['button']('loading');
					$.post('HRWeb/user/login',
						 { username: thethis.usernameTb.val(), password: thethis.passwordTb.val() },
						 function (data)
						 {

							 data = JSON.parse(data);
							 if (data.result === -1)
							 {
								 thethis.errorMd.show('Lỗi đăng nhập', "Sai tên đăng nhập hoặc mật khẩu");
								 $('.id_loginBt')['button']('reset');
								 return;
							 }
							 //if loggin successful, redirect to cvmgr

							 //DEC: nên đặt dòng này ở đây hay ở router.js
							 window.location.hash = "";
							 window.location.reload();
						 })['fail'](function (par)
					{
						var data = JSON.parse(par.responseText);
						$('.id_loginBt')['button']('reset');
						if (data.result === -1)
						{
							thethis.errorMd.show('Lỗi đăng nhập', "Sai tên đăng nhập hoặc mật khẩu");
						} else
						{
							thethis.errorMd.show('Lỗi kết nối', 'Không thể kết nối tới server. Xin vui lòng kiểm tra lại kết nối internet');
						}
					});
				});
				thethis.errorMd.init();
			});
			plus.req(['error.js'], function ()
			{
				dependLoaded.emit();
			});

			$.post('login.html', function (data)
			{
				thethis.dom = $(data);
				layoutLoaded.emit();
			});
		}
	}
}