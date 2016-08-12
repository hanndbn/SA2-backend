define(['text!trang/sendmail/sendmail.html', 'trang/taglist/taglist', 'trang/editor/editor', 'trang/thai/thai'], function (layout, Taglist, Editor, Long)
{
	return function ()
	{
		var db = {};
		var long = new Long();
		var isCreated = false;
		var editor = new Editor();
		var tolist = new Taglist('whitesmoke');
		var dom = this.dom = $(layout);
		var trang = this;
		dom.find('.id_subject').after(long.dom);


		var mailtemdb = {};


		dom.find('.id_send').click(function ()
		{
			if (lll == undefined || lll.length == 0)
			{
				var maildb = [];

				var tos = tolist.getTags();

				for (var i in tos)
				{
					maildb.push(tos[i].value);
				}

				$.post('/apply/email/merge2', {to: maildb, subject: dom.find('.id_subject').val(), body: dom.find('.id_body').code() + "\n<\br>" + dom.find('.id_sign').code()});
				dom.modal('hide');
				return;

			}

			$.post('/apply/email/merge', {to: lll, subject: dom.find('.id_subject').val(), body: dom.find('.id_body').code() + "\n<\br>" + dom.find('.id_sign').code()});
			dom.modal('hide');

		});

		dom.find('.id_toanchor').after(tolist.dom);

		//dom.find('.id_bodyanchor').after(editor.dom)
		var createlink, updatelink, removelink, searchlink, readlink;

		var tol = [];
		var lll = [];
		this.setToList = function (maillist, ltolist)
		{
			lll = ltolist;
			tol = maillist;
			for (var i in maillist)
			{
				tolist.addTag({ label: maillist[i] });
			}
		};

		dom.find('#myModal').modal({
			keyboard: false
		});

		dom.find('.id_mailtemsub').change(function ()
		{
			var temp = mailtemdb[dom.find('.id_mailtemsub').find("option:selected").index()];
			dom.find('.id_subject').val(temp.subject);
		});
		dom.find('.id_mailtembody').change(function ()
		{
			var temp = mailtemdb[dom.find('.id_mailtembody').find("option:selected").index()];
			dom.find('.id_body').code(temp.body);
		});
		dom.find('.id_mailtemsign').change(function ()
		{
			var temp = mailtemdb[dom.find('.id_mailtemsign').find("option:selected").index()];
			dom.find('.id_sign').code(temp.sign);
		});


		dom.on('shown.bs.modal', function ()
		{
			if (!isCreated)
			{
				editor.make(dom.find('.id_body'));
				editor.make(dom.find('.id_sign', 230));
				isCreated = true;
			}
		});


		trang.clear = function ()
		{
			tolist.setTag([]);

			$.post('/apply/template/list', function (data)
			{
				data = JSON.parse(data);
				dom.find('.id_mailtemsub').empty();
				dom.find('.id_mailtembody').empty();
				dom.find('.id_mailtemsign').empty();
				for (var i in data)
				{
					mailtemdb[i] = mailtemdb["" + i] = data[i];

					dom.find('.id_mailtemsub').append('<option value="' + mailtemdb[i].id + '">' + mailtemdb[i].name + '</option>');

					dom.find('.id_mailtembody').append('<option value="' + mailtemdb[i].id + '">' + mailtemdb[i].name + '</option>');

					dom.find('.id_mailtemsign').append('<option value="' + mailtemdb[i].id + '">' + mailtemdb[i].name + '</option>');
				}


			});

			dom.find('.id_body').code("");
			dom.find('.id_subject').val("");
			dom.find('.id_sign').code("");

		};

		var _done;

		this.show = function (done)
		{
			_done = done;
			dom.modal('show');
		};

		trang.setSubject = function (subject)
		{
			dom.find('.id_subject').val(subject);
		};

		trang.setContent = function (content)
		{
			dom.find('.id_body').code(content);
		};

		this.wait = function ()
		{
			long.wait();
		};

		trang.ready = function ()
		{
			long.ready();
		};

	}

});
