define(['text!trang/envelop/envelop.html', 'trang/thai/thai'], function (layout, Thai)
{
	return function ()
	{
		var trang = this;
		var thai = new Thai();

		var dom = this.dom = $(layout);
		dom.append(thai.dom);
		var anchor = dom.find('.id_mes');
		var n, p;
		var id;
		var emailitem = dom.find('.id_emailitem');
		var tagitem = dom.find('.id_tagitem');
		var otheritem = dom.find('.id_otheritem');

		var db = [];
		var out;

		function clear()
		{
			trang.dom.empty();
			trang.dom.append ($(layout));
			dom.append(thai.dom);
			anchor = dom.find('.id_mes');
		}

		trang.show = function (email,done)
		{

				thai.wait();
				$.post('/apply/email/listnew', { email: email, n: 1, p: 0 }, function (data)
				{
					thai.ready();
					clear();
					data = JSON.parse(data);
					for (var i in data)
					{
						done(data[i].id);
						add(data[i]);
					}
				});


		};


		function add(item)
		{
			var tem = emailitem.clone();

			dom.find('.id_showjunk').click(function ()
			{
				dom.find('.id_normal').fadeIn();
			});

			if (item.junk == false)
			{
				dom.find('.id_junkdiv').fadeOut();
				dom.find('.id_normal').fadeIn();
			}
			else
			{
				dom.find('.id_junkdiv').fadeIn();
				dom.find('.id_normal').fadeOut();
			}

			tem.find('.id_notjunk').click(function ()
			{
				$.get('/email/unjunk', { id: item.id });
				dom.find('.id_junkdiv').fadeOut();
				dom.find('.id_normal').fadeIn();
			});

			tem.find('.id_junk').click(function ()
			{
				$.get('/email/junk', { id: item.id });
				dom.find('.id_junkdiv').fadeIn();
				dom.find('.id_normal').fadeOut();
			});
			tem.find('.id_time').html(item.sendtime.hourOfDay + ":" + item.sendtime.minute + ":" + item.sendtime.second + "   " + item.sendtime.dayOfMonth + "/" + item.sendtime.month + "/" + item.sendtime.year);
			tem.find('.id_from').html(item.from);
			tem.find('.id_to').html(item.to);
			tem.find('.id_body').html(item.body);
			tem.find('.id_subject').html(item.subject);
			if (tem.readed == false)
			{
				tem.addClass('unreaded');
				tem.find('.id_read').click(function ()
				{
					$.get('/email/markasread', { id: item.id });
					tem.removeClass('unreaded');
					tem.find('.id_read').fadeOut();

				});

			}
			else
			{
				tem.removeClass('readed');
				tem.find('.id_read').fadeIn();
			}

			tem.find('.id_sendtocv').click(function ()
			{
				var newmail = tem.find('.id_sendtocvtextbox').val();
				$.get('/apply/candidate/send', { eid: item.id, email: newmail  });
				tem.fadeOut();
			});
			anchor.append(tem);
		}


	}

});