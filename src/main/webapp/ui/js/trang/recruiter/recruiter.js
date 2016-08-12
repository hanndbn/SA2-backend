define(['text!trang/recruiter/recruiter.html', 'trang/long/long', 'trang/navbar/navbar',
	'trang/userview/userview', 'trang/settingview/settingview', 'trang/joblist/joblist',
	'trang/candidatelist/candidatelist', 'trang/recruiter/multiselect', 'trang/mailview/mailview', 'trang/maillist/maillist'], function (layout, Long, Navbar, Userview, Settingview, JobList, CandidateList, Multiselect, Mailview, Maillist)
{
	return function ()
	{
		var long = new Long();
		var joblist = new JobList();
		var candidatelist = new CandidateList();
		candidatelist.dom.css('float', 'left');
		var settingview = new Settingview();
		var navbar = new Navbar();
		var userview = new Userview();
		var trang = this;
		var mailview = new Mailview();
		var maillist = new Maillist();
		var dom = this.dom = $(layout);
		trang.dom.append(long.dom);
		var viewenvelop = false;

		dom.find('.canlist-anchor').after(candidatelist.dom);
		maillist.dom.addClass('maillist');
		dom.find('.mailist-anchor').after(maillist.dom);
		dom.find('.joblist-anchor').after(joblist.dom);
		dom.find('.email-anchor').after(mailview.dom);


		$(candidatelist).on('itemSelected', function (e1, can)
		{
			maillist.list(can.id, 0);
		});

		$(maillist).on('itemSelected', function (e, mai)
		{
			mailview.view(mai.id);
		});


		var emailmode = {
			dom: dom.find('.id_emailmode')
		};

		var candidatehead = {
			dom: candidatelist.head
		};

		navbar['addRight'](emailmode);
		navbar['addRight'](candidatehead);
		navbar['addRight'](settingview);
		navbar['addRight'](userview);

		$(mailview).on('emailReaded', function (data, id)
		{
			candidatelist.readEmail(maillist.canid);
			maillist.readEmail(id);
		});

		$(mailview).on('emailUnreaded', function (data, id)
		{
			candidatelist.unreadEmail(maillist.canid);
			maillist.unreadEmail(id);
		});

		$(candidatelist).on('displayEmail', function (data)
		{
			mailview.update(data);
		});

		emailmode.dom.find('.id_emailmodebtn').click(function ()
		{
			viewenvelop = !viewenvelop;
			if (viewenvelop)
			{
				candidatelist.showEmail();
				mailview.show();
				maillist.show();
			}
			else
			{
				candidatelist.hideEmail();
				mailview.hide();
				maillist.hide();
			}
		});


		this.dom.find('.id_navbar').append(navbar.dom);

		function updateCandidate()
		{
			candidatelist.list(joblist.getSelectedItems());
		}

		var select = navbar.dom.find('.id_select');
		select.empty();


		$(joblist).on('hide', function ()
		{
			dom.find('.id_jobcontainer').addClass('hidden');
			dom.find('.id_linh').css('width', '100%');
			navbar['showMenuButton']();

			var items = joblist.getItems();

			var selectedItems = joblist.getSelectedItems();

			for (var i in items) if (items.hasOwnProperty(i))
			{
				var job = items[i];
				var id = i;
				var title = job.find('.id_title').html();
				if (selectedItems.indexOf(parseInt(id)) != -1)
				{
					navbar.dom.find('.id_select').append('<option value="' + id + '" selected>' + title + '</option>');
				}

				navbar.dom.find('.id_select').append('<option value="' + id + '">' + title + '</option>');
			}

			select.multiselect({
				onChange: function (element, checked)
				{
					if (checked)
					{
						joblist.selectItem(parseInt(element.val()));
					}
					else
					{
						joblist.unselectItem(parseInt(element.val()));
					}

				}
			});
			//
		});

		$(joblist).on('itemUnselected', function ()
		{
			updateCandidate();
		});


		$(joblist).on('itemSelected', function ()
		{
			updateCandidate();
		});

		this.refresh = function (path)
		{

		};

		$(navbar).on('menu', function ()
		{
			dom.find('.id_linh').css('width', '80%');
			navbar['hideMenuButton']();
			dom.find('.id_jobcontainer').removeClass('hidden');
		});

		navbar.hideMenuButton();
		mailview.hide();
		maillist.hide();
	}
});