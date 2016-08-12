///<reference path="ref.ts" />

module Password
{
	export class Plugin
	{
		onReady = new plus.Emitter();

		dom: JQuery;

		init()
		{
			var thethis = this;

			//tạo barrier để đồng bộ
			var layoutloaded = new plus.Emitter();

			plus.bar([ layoutloaded], function ()
			{
				var oldpass = thethis.dom.find('.id_oldpassword');
				var newpass = thethis.dom.find('.id_newpassword');
				var retype = thethis.dom.find('.id_retype');
				var changebt = thethis.dom.find('.id_changebt');
				changebt.prop('disabled',true);

				retype.on('keyup', function ()
				{
					if (retype.val() != newpass.val())
					{
						retype.addClass('btn-danger');
						changebt.prop('disabled', true);
					}
					else
					{
						retype.removeClass('btn-danger');
						changebt.prop('disabled', false);
					}
				});

				newpass.on('keyup', function ()
				{
					if (retype.val() != newpass.val())
					{
						retype.addClass('btn-danger');
						changebt.prop('disabled', true);
					}
					else
					{
						retype.removeClass('btn-danger');
						changebt.prop('disabled', false);
					}
				});

				changebt.on('click', function ()
				{
					changebt['button']('loading');
					$.get('HRWeb/user/editUP', { oldpassword : oldpass.val(), password : retype.val() }, function ()
					{
						newpass.val("");
						retype.val("");
						oldpass.val("");
						changebt['button']('reset');
					})['fail'](function ()
					{
						changebt['button']('reset');
						alert('wrong infomation, please try again');
						});
				});
				thethis.onReady.emit();
			});

				

			//lấy layout
			$.get('password.html', function (data)
			{
				thethis.dom = $(data);
				layoutloaded.emit();
			});

			


		}


	}
}  