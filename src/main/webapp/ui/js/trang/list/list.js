define(['text!trang/list/list.html', 'trang/long/long'], function (layout, Long)
{
	//event itemAdded: trigger when a new item is added
	//event itemRemoved: trigger when a new item is removed
	//event itemSelected;
	//event itemStartDrag
	//event itemDrop
	return function ()
	{

		var trang = this;
		var count = 0;

		var long = new Long();
		var dom = this.dom = $(layout);
		//	var defaultrow = dom.find('.default-row');
		var eledb = {};

		//number of page and current page
		var n, p;
		this.setPage = function (_p, _n)
		{
			p = _p;
			n = _n === undefined ? n : _n;
			renderPagination();
		};

		this.hidePage = function ()
		{
			dom.find('.id_pagination').addClass('hidden');
			dom.find('.id_tablecon').removeClass('w-f-sm');
		};

		this.getPage = function ()
		{
			return p;
		};

		function renderPagination()
		{
			dom.find('.id_p').html(p);
			dom.find('.id_n').html(n);
		}

		dom.find('.id_tab').keyup(function (e)
		{

			if (e.keyCode == 46)
			{
				$(trang).trigger('itemDeleted');
			}
		});


		dom.find('.id_b').click(function ()
		{
			p -= 1;
			if (p < 1)
			{
				p = 1;
				return;
			}
			renderPagination();
			$(trang).trigger('prev', p);
		});

		dom.find('.id_f').click(function ()
		{
			p++;
			if (p > n)
			{
				p = n;
				return;
			}
			renderPagination();
			$(trang).trigger('next', p);
		});

		var nElement = dom.find('.id_n');
		var pElement = dom.find('.id_p');

		dom.append(long.dom);
		var header;
		this.setHeader = function (_header)
		{
			header = _header;
			dom.find('.header-anchor').after(header);

		};

		var rowdb = [];
		var activedb = [];
		this.add = function (element, index, id)
		{

			function selectId(id)
			{
				if (activedb.indexOf(id) !== -1) return;
				activedb.push(id);
				rowdb[id].addClass('active');
				$(rowdb[id]['linh']).prop('checked',true);

			}

			if (index == undefined)
				index = count;
			$(element).prop('data-id',id);
			if (eledb[id] !== undefined)
			{

				eledb[id].after(element);
				eledb[id].hide();
				eledb[id].removeClass('id_' + id);
				eledb[id] = element;
				var row = element;

				var checkbox = row.find('input[type=checkbox].ace');
				row['linh'] = checkbox;
				//row.find('.checkboxcontainer').click(function () {
				//	if (checkbox.prop('checked') == true)
				//		checkbox.prop('checked', false);
				//	else checkbox.prop('checked', true);
				//	checkbox.trigger('change');
				//});


				checkbox.change(function ()
				{
					if (this.checked)
					{
						if (activedb.indexOf(id) !== -1) return;
						activedb.push(id);
						rowdb[id].addClass('active');

						$(trang).trigger('itemSelected', id);
					}
					else
					{
						activedb.splice(activedb.indexOf(id), 1);
						rowdb[id].removeClass('active');
						$(trang).trigger('itemUnselected', id);
					}
				});

				rowdb[id] = row;
				row.removeClass('hidden');
				if (index == 0)
				{
					dom.find('.veryfirstrow').after(row);
				} else if (index == 1)
				{
					dom.find('.verysecondrow').after(row);
				}
				else if (index == 2)
				{
					dom.find('.verythirdrow').after(row);
				}


				row.fadeOut(0);
				row.addClass("id_" + id);
				row.fadeIn();
				setTimeout(function ()
				{
					$(trang).trigger('itemAdded')
				});
				return "old";
			}

			eledb[id] = element;
			var row = element;

			var checkbox = row.find('input[type=checkbox].ace');
			row['linh'] = checkbox;
			//row.find('.checkboxcontainer').click(function () {
			//	if (checkbox.prop('checked') == true)
			//		checkbox.prop('checked', false);
			//	else checkbox.prop('checked', true);
			//	checkbox.trigger('change');
			//});
			checkbox.change(function ()
			{
				if (this.checked)
				{
					if (activedb.indexOf(id) !== -1) return;
					activedb.push(id);
					rowdb[id].addClass('active');

					$(trang).trigger('itemSelected', id);
				}
				else
				{
					activedb.splice(activedb.indexOf(id), 1);
					rowdb[id].removeClass('active');
					$(trang).trigger('itemUnselected', id);
				}
			});

			rowdb[id] = row;
			row.removeClass('hidden');
			row.fadeOut(0);
			row.addClass("id_" + id);

			if (index == 0)
			{
				dom.find('.veryfirstrow').after(row);
			} else if (index == 1)
			{
				dom.find('.verysecondrow').after(row);
			}
			else if (index == 2)
			{
				dom.find('.verythirdrow').after(row);
			}
			else
			{
				dom.find('.firstrow').before(row);
			}

			element.click(function (e)
			{

				if (e.shiftKey)
				{

					var ei = $(element).prev();
					selectId(id);
					while (!ei.hasClass('active') && !ei.hasClass('veryfirstrow'))
					{

						if (ei.hasClass('hidden') || ei.css('display') == 'none')
						{
							ei = ei.prev();
							continue;
						}
						selectId(ei.prop('data-id'));
						ei = ei.prev();
					}
					$(trang).trigger('itemSelected', activedb);

				}

			});

			row.fadeIn();
			count++;
			setTimeout(function ()
			{
				$(trang).trigger('itemAdded')
			});
			return "new"
		};

		this.transparent = function (id)
		{
			if (rowdb[id] !== undefined)
			{
				rowdb[id].fadeOut();
				setTimeout(function ()
				{
					$(trang).trigger('itemRemoved')
				});

			}
			else throw "row not found";
		};

		var con = {
			dragable: true
		};

		//
		//dragable
		//
		this.config = function (c)
		{
			if (c.dragable !== undefined)
			{
				con.dragable = c.dragable;
			}

		};

		this.remove = function (id)
		{
			var row = dom.find('.id_' + id);
			if (row[0] == undefined) throw "wrong id";
			row.fadeOut();
			count--;
		};

		this.getNP = function ()
		{
			return {
				n: nElement.html(),
				p: pElement.html()
			}
		};

		this.setNP = function (n, p)
		{
			nElement.html(n);
			pElement.html(p)
		};


		this.load = function (state)
		{

		};

		this.save = function ()
		{

		};

		this.transparentAll = function ()
		{
			for (var id in rowdb)
				rowdb[id].fadeOut();
		};

		trang.selectItem = function (id)
		{
			activedb.push(id);
			rowdb[id]['linh'].prop('checked', true);
			rowdb[id].addClass('active');
			rowdb[id].focus();
			$(trang).trigger('itemSelected', id);
		};

		this.getSelectedItem = function ()
		{
			return activedb.slice(0); //return a clone
		};

		this.getItem = function (i)
		{
			return rowdb[i];
		};


		trang.unselectItem = function (i)
		{
			var index = activedb.indexOf(i);
			activedb.splice(index, 1);
			rowdb[i].removeClass('active');
			rowdb[i]['linh'].prop('checked', false);
			$(trang).trigger('itemUnselected', i);
		};


		trang.unselectAll = function ()
		{
			for (var i in activedb)
			{
				rowdb[activedb[i]].removeClass('active');
				rowdb[activedb[i]]['linh'].prop('checked', false);
			}
			activedb = [];
		};

		this.selectAll = function ()
		{
			activedb = [];
			for (var i in rowdb)
			{
				activedb.push(i);
			}

			for (var i in rowdb)
			{
				rowdb[i]['linh'].prop('checked', true);
			}
		};

		this.refresh = function (config)
		{
		};

		this.wait = function ()
		{
			long.wait();
		};

		this.unwait = function ()
		{
			long.ready();
		};

		this.setTableHeader = function (div)
		{
			dom.find('.id_tbheader').before(div);

		};

		this.getItems = function ()
		{
			return rowdb;
		};

		this.setSide = function (thedom)
		{
			dom.find('.id_sidecon').append(thedom);
		};

		/*
		this.showSide = function ()
		{
			dom.find('.id_tablecon').css('width', '25%');
			dom.find('.id_sidecon').css('width', '75%');
			dom.find('.id_sidecon').fadeIn();
		};

		this.hideSide = function ()
		{
			dom.find('.id_tablecon').css('width', '100%');
			dom.find('.id_sidecon').fadeOut();
		};
		*/

	}

});