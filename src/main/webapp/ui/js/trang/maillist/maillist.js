define(['trang/list/list', 'text!trang/maillist/maillist.html', 'trang/long/long'], function (List, layout, Long)
{
	return function ()
	{
		var trang = this;
		var long = new Long();
		var list = new List();
		var dom = this.dom = list.dom;
		var listprogress = 0;
		var ITEMONPAGE = 20;

		layout = $(layout);
		dom.append(long.dom);

		var header = layout.find('.id_mailheader');
		list.setHeader(header);

		var mailtem = $(layout).find('.id_mailtem');

		list.hidePage();

		var _candidateid = -1;
		var _p = 0;

		function setup(candidateid, p)
		{
			trang.canid = candidateid;
			_candidateid = candidateid;
			_p = p;
		}

		function isup(candidateid, p)
		{
			return !!(_candidateid == candidateid && _p == p);
		}

		trang.list = function (candidateid, p)
		{
			list.transparentAll();
			list.unselectAll();
			long.wait();
			maildb = {};

			var n = ITEMONPAGE;

			if (p == undefined)
			{
				p = 0;
				_p = 0;
			}
			setup(candidateid, p);

			$.post('/apply/email/countbycandidate', { cid: candidateid }, function (number)
			{
				if (!isup(candidateid, p))
					return;

				var co = JSON.parse(number).result;
				if (co == 0)
				{
					dom.find('.id_emptylist').fadeIn();
					long.ready();
					return;
				}
				else
				{
					dom.find('.id_emptylist').fadeOut();
				}

				list.setPage(p + 1, 1 + parseInt((co / ITEMONPAGE)));

				$.post('/apply/email/listbycandidate', {  cid: candidateid, p: p, n: n }, function (data)
				{
					//neu nguoi dung da gui query khac thi thoat luon
					if (!isup(candidateid, p))
						return;
					data = JSON.parse(data);
					for (var i in data)
					{
						addMail(data[i]);
					}
					updateView();
				});
			});

		};

		function updateView()
		{
			//if (viewenvelop)
			//{
			//	dom.css('height','50%');
			//}
			//else
			//{
			//	dom.css('height','100%');
			//}
		}

		function addMail(mai)
		{
			var tem = mailtem.clone();
			mai.tem = tem;

			tem['huong'] = function ()
			{
				list.unselectAll();
				list.selectItem(mai.id);
				$(trang).trigger('itemSelected', mai);
			};

			tem.click(function (e)
			{
				if (e.shiftKey)
				{
					return;
				}
				if ($(e.target).prop('class') == '' || $(e.target).hasClass('selectable'))
				{
					tem['huong']();
				}
			});

			if(mai.unread)
			{
				tem.find('.id_newlbl').fadeIn();
			}
			else
			{
				tem.find('.id_newlbl').fadeOut();
			}

			tem.css('cursor', 'pointer');
			maildb[mai.id] = mai;


			tem.find('.id_l').click(function ()
			{
				updatestate(statemap['id_l']);
			});

			tem.find('.id_name').html(" " + mai.subject + " ");

			tem.find('[data-toggle="tooltip"]').tooltip();
			tem.find('.id_name').html(mai.subject);
			list.add(tem, undefined, mai.id);

			updateProgress();
		}

		function updateProgress()
		{
			listprogress++;
			if (listprogress == length)
			{
				listprogress = 0;
				long.ready();
			}
		}

		trang.readEmail = function(id)
		{
			$.post('/apply/email/view', {id: id}, function (data)
			{
				addMail(JSON.parse(data));
				list.selectItem(id);
				updateView();
			});

		};

		trang.unreadEmail = function(id)
		{

		};


		trang.getSelectedItems = function ()
		{
			return list.getSelectedItem();
		};

		trang.getItems = function ()
		{
			return list.getItems();
		};

		trang.unselectItem = function (i)
		{
			list.unselectItem(i);
		};

		trang.selectItem = function (i)
		{
			list.selectItem(i);

		}

		trang.show = function()
		{
			trang.dom.fadeIn(0);

		};

		trang.hide = function()
		{
			trang.dom.fadeOut(0);

		};
	}
});