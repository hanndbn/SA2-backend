define(['text!trang/userview/userview.html', 'trang/long/long'], function (layout, Long)
{
	return function ()
	{
		var long = new Long();

		var dom = this.dom = $(layout);
		this.dom.append(long.dom);
		var _user;

		dom.find('.id_username').html($.user.fullname);

		dom.find('.id_logout').click(function ()
		{
			$.get('/apply/user/logout');
			window.location.href = "/login";
			return false;
		});

		this.refresh = function ()
		{
			long.wait();
			var cookie = JSON.parse(document.cookie);
			long.ready();
		}
	}
});