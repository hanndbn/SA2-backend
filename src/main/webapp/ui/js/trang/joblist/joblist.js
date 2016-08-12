define(['trang/list/list', 'text!trang/joblist/joblist.html', 'trang/long/long', 'trang/jobedit/jobedit'], function (List, layout, Long, Jobedit)
{
	return function ()
	{
		var trang = this;
		var long = new Long();
		var list = new List();
		var dom = this.dom = list.dom;

		var jobedit = new Jobedit();
		dom.append(jobedit);

		trang.jobsearch = undefined;
		dom.append(long.dom);

		var jobtem = $(layout).find('.id_jobtem');

		var header = $(layout).find('.jobmgrheader');
		list.setHeader(header);

		header.find('.id_createjob').click(function ()
		{
			jobedit.createnew(created);
		});

		function created(job)
		{

			var item = jobtem.clone();
			item.css('background-color', 'lavenderblush');
			createJob(item, job);
			list.add(item, 2, job.id);
		}

		list.hidePage();

		long.wait();


		header.find('.id_search').click(function ()
		{
			$.plugIn([['trang/job/job', 'jobsearch']],trang,function(){
				trang.jobsearch.show();
			});
		});

		header.find('.id_hideshow').click(function ()
		{
			$(trang).trigger('hide');
		});
		//archived = archived === undefined ? false : archived;
		//keyword = keyword === undefined ? "" : keyword;
		//p = p === undefined ? 1 : p;
		//$('/hr/job/count', { archived: archived, keyword: keyword, p: p, n: 20 }, function (ret) {
		//	var n = Math.ceil(ret.result / 20);
		$.get('/apply/job/list', { archived: false, keyword: "", p: 0, n: 100 }, function (data)
		{
			data = JSON.parse(data);
			for (var i in data)
			{
				var item = jobtem.clone();



				createJob(item, data[i]);
				if (data[i].id == 0)
				{
					if (data[i].ccount >= 30)
					{

						setInterval(function(){
							item.toggleClass("backgroundRed");
						},2000);



					}
					list.add(item, 0, data[i].id);
					item.find('.id_c').prop('checked', true);
				} else if (data[i].id == 1)
				{
					list.add(item, 1, data[i].id);
				}
				else
				{
					list.add(item, 3, data[i].id);
				}
			}
			long.ready();
		});
		//});

		function updateJobstatus(status, item)
		{

			if (status == 1)
			{
				item.find('.id_statuscolor').prop('class', 'fa fa-circle red id_statuscolor');
			} else if (status == 2)
			{
				item.find('.id_statuscolor').prop('class', 'fa fa-circle green id_statuscolor');
			} else if (status == 3)
			{
				item.find('.id_statuscolor').prop('class', 'fa fa-circle grey id_statuscolor');
			} else if (status == 4)
			{
				item.find('.id_statuscolor').prop('class', 'fa fa-circle orange id_statuscolor');
			} else
			{
				item.find('.id_statuscolor').prop('class', 'fa fa-circle pink id_statuscolor');
			}
		}

		function createJob(item, job)
		{
			item.find('[data-toggle="tooltip"]').tooltip();

			item.on('dragover', function (ev)
			{
				if (item.hasClass('active')) return;
				ev.preventDefault();
			});


			item.find('.id_jobtemclickable').click(function()
			{
				list.unselectAll();
				list.selectItem(job.id);
				$(trang).trigger('itemSelected');
			});

			item.on('drop', function (evt)
			{
				if (item.hasClass('active')) return;
				evt.preventDefault();
				var data = evt.originalEvent.dataTransfer.getData("nguyen");
				data = data.constructor.data;

				item.find('.id_ncan').html(parseInt(item.find('.id_ncan').html()) + data.ids.length);
				var oldjob = $('.id_jobtem.id_'+data.oldjobid).find('.id_ncan');
				oldjob.html(parseInt(oldjob.html()) - data.ids.length);
				data.callback(data.param,job.id);
			});


			item.find('.id_code').html(job.tag);

			item.find('.id_planning').click(function ()
			{
				job.status = 1;
				$.get('/apply/job/edit', { id: job.id, jobstatus: job.status });
				updateJobstatus(job.status, item);
			});

			item.find('.id_openning').click(function ()
			{
				job.status = 2;
				$.get('/apply/job/edit', { id: job.id, jobstatus: job.status });
				updateJobstatus(job.status, item);
			});

			item.find('.id_closed').click(function ()
			{
				job.status = 3;
				$.get('/apply/job/edit', { id: job.id, jobstatus: job.status });
				updateJobstatus(job.status, item);
			});

			item.find('.id_onhold').click(function ()
			{
				job.status = 4;
				$.get('/apply/job/edit', { id: job.id, jobstatus: job.status });
				updateJobstatus(job.status, item);
			});

			updateJobstatus(job.jobstatus, item);

			var jobtit = job.title;
			if (jobtit.length >= 30) jobtit = jobtit.substr(0, 150) + "...";


			item.find('.id_title').html(jobtit);
			item.find('.id_ncan').html(job.ccount);

			item.find('.id_star').click(function ()
			{

				job.star = !job.star;
				$.get('/apply/job/edit', { id: job.id, star: job.star });
				updateStar();
			});

			function updateStar()
			{
				if (job.star == true)
				{
					item.find('.id_star').removeClass('light-grey');
					item.find('.id_star').addClass('orange');
				}
				else
				{
					item.find('.id_star').addClass('light-grey');
					item.find('.id_star').removeClass('orange');

				}
			}

			updateStar();
			item.on('mouseover', function ()
			{
				item.find('.id_view').removeClass('hidden');
				item.find('.id_trash').removeClass('hidden');
				item.find('.id_edit').removeClass('hidden');

			});

			item.find('.id_view').click(function (e)
			{
				window.open('/vitri/' + job.id, '_blank');
			});

			item.find('.id_trash').click(function ()
			{
				if (true == confirm("Chắc chắn lưu trữ tin tuyển dụng này ?"))
				{
					$.get('/apply/job/edit', { id: job.id, state: -1 });
					list.remove(job.id);
				}
			});




			item.find('.id_edit').click(function ()
			{
				jobedit.editold(job, edited);
			});

			function edited(gjob)
			{
				job['title'] = gjob['title'];
				item.find('.id_title').html(gjob['title']);
			}

			item.on('mouseleave', function ()
			{
				item.find('.id_view').addClass('hidden');
				item.find('.id_trash').addClass('hidden');
				item.find('.id_edit').addClass('hidden');
			});

		}

		$(list).on('itemSelected', function (data)
		{
			$(trang).trigger('itemSelected', data);

		});

		$(list).on('itemUnselected', function (data)
		{

			$(trang).trigger('itemUnselected', data);
		});


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

	}

});