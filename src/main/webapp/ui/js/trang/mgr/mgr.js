define(['text!trang/mgr/mgr.html', 'trang/list/list', 'trang/thai/thai'], function (layout, List, Long)
{
	return function (tem, detail, defitem, createlnk, listlnk, updatelnk, toObject, toForm, toTemplate)
	{
		var list = new List();
		var long = new Long();
		var temdb = {};
		var detaildb = {};
		var db = {};
		var dom = this.dom = $(layout);
		dom.append(long);
		var trang = this;

		var Jele;
		var Jdetail;

		dom.find('.mgr-left').append(list.dom);
		var con = list.dom.find('section.m-t-sm');
		con.removeClass('m-t-sm');
		con.addClass('m-t-md');

		list.setPage(1, 20);

		var header = dom.find('.id_header');
		list.setHeader(header);

		dom.find('.id_detail').append(Jdetail);

		var Jarchived = dom.find('.id_archived');
		var Jsearch = dom.find('.id_search');
		var Jinsert = dom.find('.id_insert');

		var _n = 20;
		var _p = 0;
		var _keyword = "";

		this.search = function (keyword)
		{
			if (keyword === undefined)
			{
				keyword = _keyword
			}
			else
			{
				_keyword = keyword
			}

			list.wait();
			list.transparentAll();
			$.post(listlnk, {
				keyword: _keyword,
				archived: true,
				n: _n,
				p: _p
			}, function (data)
			{
				data = JSON.parse(data);
				for (var i in data)
				{
					var it = data[i];
					temdb[it.id] = tem.clone();
					toTemplate(it, temdb[it.id]);
					detaildb[it.id] = detail.clone();
					toForm(it, detaildb[it.id]);
					db[it.id] = it;
					list.add(temdb[it.id], 2, it.id);
				}
				list.unwait();
			});
		};

		trang.setTitle = function(t)
		{
			dom.find('.id_title').html(t);
		};

		Jinsert.click(function ()
		{
			long.wait();
			$.post(createlnk, defitem, function (data)
			{
				var id = JSON.parse(data);

				detaildb[id] = detail.clone();
				temdb[id] = tem.clone();
				list.add(temdb[id], 1, id);
				long.ready();
			});
		});

		$(list).on('itemSelected', function (id)
		{
			dom.find('.id_detail').empty();
			dom.find('.id_detail').append(detail[id]);
		});

		dom.find('.id_save').click(function ()
		{

			var thethis = this;
		$(	this).button('loading');
			var sc = list.getSelectedItem();
			for (var i in sc)
			{
				var a = toObject(detaildb[sc[i]]);
				$.post(updatelnk, a, function (data)
				{

					$(thethis).button('reset');
				});
			}

		});

		dom.find('.id_saveandclose').click(function ()
		{
			$('.id_save').trigger('click');
			dom.modal('close');
		});

		$(list).on('itemSelected', function (q, id)
		{
			dom.find('.id_detail').empty();
			dom.find('.id_detail').append(detaildb[id]);
		});

		Jsearch.change(function ()
		{
			search();
		});

		dom.find('#myModal').modal({
			keyboard: false
		});

		this.show = function ()
		{
			dom.modal('show');

		};

		this.wait = function ()
		{
			dom.find('.loading').removeClass('hidden');
		};

		this.ready = function ()
		{
			dom.find('.loading').addClass('hidden');
		};


	}

});
