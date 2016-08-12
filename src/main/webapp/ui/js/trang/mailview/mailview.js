define([], function ()
{
	return function ()
	{
		var trang = this;
		var enviframe = trang.dom = $('<iframe style="width: 75%; height: 50%; border: none; float:right" />');
		var dom = trang.dom;

		enviframe.prop('src', '/ui/html/email.html#');

		//list.setSide(enviframe);
		enviframe.parent().css('overflow-y', 'hidden');

		trang.hide = function ()
		{
			dom.css('display', 'none');
		};

		trang.show = function ()
		{
			dom.css('display', 'block');
		};

		trang.view = function (emailid)
		{
			enviframe[0].contentWindow.postMessage("wait", document.location.origin);

			function done(id)
			{
				$.post('/apply/email/markasread', {id: id}, function (data)
				{
					$(trang).trigger('emailReaded', id);
				});
			}

			$.post('/apply/email/view', { id: emailid }, function (data)
			{
				data = JSON.parse(data);
				if( data !== undefined && data[0] !== undefined)
					data = data[0];
				if (data === undefined) data = {id: ''};
				else
				{
					done(data.id);

					if (enviframe[0].contentDocument.readyState != 'complete')
					{
						enviframe.load(function ()
						{
							enviframe[0].contentWindow.postMessage(JSON.stringify(data), document.location.origin);
						});
					}
				}
				enviframe[0].contentWindow.postMessage(JSON.stringify(data), document.location.origin);
			});
		};
	}

});