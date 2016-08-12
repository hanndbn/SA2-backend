define(['text!trang/jobedit/jobedit.html', 'trang/taglist/taglist', 'trang/editor/editor', 'trang/thai/thai'], function (layout, Taglist, Editor, Long)
{
	return function ()
	{
		var long = new Long();
		var db = {};
		var taglist = new Taglist('whitesmoke');
		var dom = this.dom = $(layout);
		var trang = this;
		var fieldlist = [];
		dom.find('.id_fieldanchor').after(taglist.dom);


		dom.find('.id_settingform').append(long.dom);
		dom.find('#myModal').modal({
			keyboard: false,
			backdrop: 'static'
		});

		this.wait = function ()
		{
			dom.find('.loading').removeClass('hidden');
		};

		this.ready = function ()
		{
			dom.find('.loading').addClass('hidden');
		};

		dom.find('.id_infotab').click(function ()
		{
			dom.find('.id_infoform').fadeIn("fast");
			dom.find('.id_settingform').fadeOut("fast");
			dom.find('.id_infotab').addClass("active");
			dom.find('.id_settingtab').removeClass("active");

		});

		dom.find('.id_settingtab').click(function ()
		{
			dom.find('.id_infoform').fadeOut("fast");
			dom.find('.id_settingform').fadeIn("fast");
			dom.find('.id_infotab').removeClass("active");
			dom.find('.id_settingtab').addClass("active");
		});

		$(taglist).on('tagcreated', function (e, tag)
		{
			//if created by code
			if (tag.attrs.label.constructor.thanh !== undefined)
			{
				tag.relatedTarget.thanh = {};
				tag.relatedTarget.thanh.id = tag.attrs.label.constructor.thanh.id;
				tag.attrs.label.constructor.thanh = undefined;
			}
			else
			{//created by hand
				tag.relatedTarget.thanh = {};
				$(tag.relatedTarget).addClass('loading');

				$.post('/apply/field/create', { name: tag.attrs.label }, function (data)
				{
					data = JSON.parse(data);
					tag.relatedTarget.thanh.id = data.result;
					$(tag.relatedTarget).removeClass('loading');
					fieldlist.push(data.result);
				});
			}
		});

		//$(tag).on('tagedited', function (e, tag) {
		//	$(tag.relatedTarget).addClass('loading');
		//	$.get('/apply/comment/delete', { id: tag.relatedTarget.thanh.id }, function () {
		//	});
		//});

		//$(commlist).on('tagdelete', function (e, tag) {
		//	//if delete by user then confirm first
		//	var r = confirm('Xác nhận xóa [' + tag.attrs.label + ']');
		//	if (r == false) {
		//		tag.preventDefault();
		//		return false;
		//	}
//
		//		$(tag.relatedTarget).addClass('loading');
//			$.get('/apply/comment/delete', { id: tag.relatedTarget.thanh.id }, function () {
		//		tag.relatedTarget.thanh.delete == true;
		//		$(tag.relatedTarget).fadeOut();
		//	});

		//});


		var isCreated = false;
		dom.on('shown.bs.modal', function ()
		{
			if (!isCreated)
			{
				isCreated = true;
			}
		});

		new Editor().make(dom.find('.id_resmail'));
		new Editor().make(dom.find('.id_rejectmail'));
		new Editor().make(dom.find('.id_testmail'));
		new Editor().make(dom.find('.id_rcvmail'));
		new Editor().make(dom.find('.id_attachment'));
		new Editor().make(dom.find('.id_jobint'));
		new Editor().make(dom.find('.id_jobdesc'));

		dom.find('.id_picture').keyup(function ()
		{
			dom.find('.if_propic').prop('src', dom.find('.id_picture').val());
		});

		dom.find('.id_picture').change(function ()
		{
			dom.find('.if_propic').prop('src', dom.find('.id_picture').val());
		});

		var map = {
			"1": "/ui/img/tv/tvg.png",
			"2": "/ui/img/tv/tvo.png",
			"3": "/ui/img/tv/tvc.png",
			"4": "/ui/img/tv/tvb.jpg",
			"5": "/ui/img/tv/tvi.png",
			"6": "/ui/img/tv/tvs.jpg",
			"7": "/ui/img/tv/mcc.jpg",
			"8": "/ui/img/tv/tvt.jpg"
		}

		dom.find('.id_org').change(function ()
		{
			dom.find('.id_picture').val(map[$(this).val()]);
			dom.find('.id_picture').change();
		});


		dom.find('.if_propic').error(function ()
		{
			$(this).prop('src', '/ui/img/tv/err.png');
		});

		this.toForm = function (job)
		{
			long.wait();
			$.post('/apply/job/get', {id: job.id}, function (data)
			{
				data = JSON.parse(data);

				dom.find('.id_title').val(data.title);
				dom.find('.id_picture').val(data.picture);
				dom.find('.id_cvform').val(data.cvform);
				dom.find('.id_org').val(data.orgid);
				dom.find('.id_enddate').val(data.endtime.year + "-" + ("0" + (parseInt(data.endtime.month) + 1)).slice(-2) + "-" + ("0" + data.endtime.dayOfMonth).slice(-2));
				dom.find('.id_opendate').val(data.opentime.year + "-" + ("0" + (parseInt(data.opentime.month) + 1)).slice(-2) + "-" + ("0" + data.opentime.dayOfMonth).slice(-2));
				dom.find('.id_salary').val(data.salary);
				dom.find('.id_quantity').val(data.quantity);
				dom.find('.id_jobdesc').code(data.description);
				dom.find('.id_jobcontact').val(data.contact);
				dom.find('.id_jobint').code(data.interest);
				dom.find('.id_category').val(data.category);
				dom.find('.id_state').val(data.jobstatus);
				dom.find('.id_attachment').code(data.attachment);
				var rvcmail = data.setting.rvcmail.split("\n");
				var resmail = data.setting.resmail.split("\n");
				var rejectmail = data.setting.rejectmail.split("\n");
				var testmail = data.setting.testmail.split("\n");

				dom.find('.id_rcvmail').code(rvcmail[1]);
				dom.find('.id_resmail').code(resmail[1]);
				dom.find('.id_rejectmail').code(rejectmail[1]);
				dom.find('.id_testmail').code(testmail[1]);

				dom.find('.id_rcvmailt').val(rvcmail[0]);
				dom.find('.id_resmailt').val(resmail[0]);
				dom.find('.id_rejectmailt').val(rejectmail[0]);
				dom.find('.id_testmailt').val(testmail[0]);

				taglist.setTag([]);

				for (var f in data.fields)
				{
					var field = data.fields[f];
					var vl = field.name + "";
					vl.constructor.thanh = field;
					taglist.addTag({ value: field, label: vl });
				}

				dom.find('.id_picture').change();
				long.ready();
			});
		};

		var jobid;

		function clear()
		{
			dom.find('.id_title').val("");
			dom.find('.id_picture').val("");
			dom.find('.id_cvform').val("");
			dom.find('.id_attachment').code("");
			dom.find('.id_enddate').val("");
			dom.find('.id_opendate').val("");
			dom.find('.id_salary').val("");
			dom.find('.id_quantity').val("");
			dom.find('.id_jobdesc').code("");
			dom.find('.id_jobcontact').val("Bất kỳ");
			dom.find('.id_jobint').code("");
			dom.find('.id_category').val("");
			dom.find('.id_state').val("");
			dom.find('.id_org').val("");
			taglist.setTag([]);
			dom.find('.id_rcvmail').code("");
			dom.find('.id_resmail').code("");
			dom.find('.id_rejectmail').code("");
			dom.find('.id_testmail').code("");
			fieldlist = [];
			dom.find('.id_rcvmailt').val("");
			dom.find('.id_resmailt').val("");
			dom.find('.id_rejectmailt').val("");
			dom.find('.id_testmailt').val("");
		}

		dom.find('.id_save').click(function ()
		{
			var job = {};
			job.id = jobid;
			job.title = dom.find('.id_title').val();
			job.picture = dom.find('.id_picture').val();
			job.cvform = dom.find('.id_cvform').val();
			job.attachment = dom.find('.id_attachment').code();
			job.endtime = new Date(dom.find('.id_enddate').val()).toUTCString();
			job.opentime = new Date(dom.find('.id_opendate').val()).toUTCString();
			job.salary = dom.find('.id_salary').val();
			job.quantity = dom.find('.id_quantity').val();
			job.description = dom.find('.id_jobdesc').code();
			job.contact = dom.find('.id_jobcontact').val();
			job.interest = dom.find('.id_jobint').code();
			job.category = dom.find('.id_category').val();
			job.jobstatus = dom.find('.id_state').val();
			job.orgid = dom.find('.id_org').val();
			job.rcvmail = dom.find('.id_rcvmailt').val() + "\n" + dom.find('.id_rcvmail').code();
			job.resmail = dom.find('.id_resmailt').val() + "\n" + dom.find('.id_resmail').code();
			job.rejectmail = dom.find('.id_rejectmailt').val() + "\n" + dom.find('.id_rejectmail').code();
			job.testmail = dom.find('.id_testmailt').val() + "\n" + dom.find('.id_testmail').code();
			job.field = fieldlist;
			$.post('/apply/job/edit', job, function (data)
			{
			});

			dom.modal('hide');
			_callback(job);

		});

		$(taglist).on('tagedited', function (e, tag)
		{
			delete fieldlist[tag.relatedTarget.thanh.id];
		});

		$(taglist).on('tagdelete', function (e, tag)
		{
			delete fieldlist[tag.relatedTarget.thanh.id];
		});

		dom.find('.id_submit').click(function ()
		{
			var job = {};
			job.title = dom.find('.id_title').val();
			job.picture = dom.find('.id_picture').val();
			job.cvform = dom.find('.id_cvform').val();
			job.attachment = dom.find('.id_attachment').code();
			job.endtime = new Date(dom.find('.id_enddate').val()).toUTCString();
			job.opentime = new Date(dom.find('.id_opendate').val()).toUTCString();
			job.salary = dom.find('.id_salary').val();
			job.quantity = dom.find('.id_quantity').val();
			job.description = dom.find('.id_jobdesc').code();
			job.contact = dom.find('.id_jobcontact').val();
			job.interest = dom.find('.id_jobint').code();
			job.category = dom.find('.id_category').val();
			job.state = dom.find('.id_state').val();
			job.orgid = dom.find('.id_org').val();
			job.rcvmail = dom.find('.id_rcvmailt').val() + "\n" + dom.find('.id_rcvmail').code();
			job.resmail = dom.find('.id_resmailt').val() + "\n" + dom.find('.id_resmail').code();
			job.rejectmail = dom.find('.id_rejectmailt').val() + "\n" + dom.find('.id_rejectmail').code();
			job.testmail = dom.find('.id_testmailt').val() + "\n" + dom.find('.id_testmail').code();

			job.ccount = 0;
			job.field = fieldlist;
			$.post('/apply/job/create', job, function (data)
			{
				data = JSON.parse(data);
				fieldlist[data.result] = true;
				job.id = data.result;

				_callback(job);
			});

			dom.modal('hide');
			clear();
		});

		var _callback;

		this.createnew = function (callback)
		{
			clear();
			dom.find('.id_submit').removeClass('hidden');

			dom.find('.id_save').addClass('hidden');
			_callback = callback;
			dom.modal('show');
		};

		this.editold = function (job, callback)
		{
			clear();
			jobid = job.id;
			this.toForm(job);
			dom.find('.id_save').removeClass('hidden');

			dom.find('.id_submit').addClass('hidden');
			_callback = callback;
			dom.modal('show');
		}
	}
});
