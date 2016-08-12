define(['text!trang/inputting/inputting.html', 'trang/taglist/taglist'], function (layout, Taglist)
{
	return function ()
	{
		var taglist = new Taglist('whitesmoke');
		var dom = this.dom = $(layout);
		var trang = this;

		dom.find('.id_fieldanchor').after(taglist.dom);

		dom.find('.id_phone').click(function ()
		{
			this.select();
		});

		dom.find('.id_name').click(function ()
		{
			this.select();
		});

		dom.find('.id_tech').click(function ()
		{
			this.select();
		});

		dom.find('.id_exp').click(function ()
		{
			this.select();
		});

		dom.find('.id_next').click(function ()
		{
			_next(_candidate, function (u)
			{
				trang.show(u, next, prev)
			});
		});

		dom.find('.id_prev').click(function ()
		{
			_prev(_candidate, function (u)
			{
				trang.show(u, next, prev)
			});
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

				$.post('/apply/candidate/comment', { cid: _candidate.id, comment: tag.attrs.label }, function (data)
				{
					data = JSON.parse(data);
					tag.relatedTarget.thanh.id = data.result;
					$(tag.relatedTarget).removeClass('loading');
				});
			}
		});

		dom.find('.id_save').click(function ()
		{
			var candidate = toCandidate();

			if (candidate.fullname == "" || candidate.fullname == "chưa nhập")
			{
				alert('Chưa nhập họ tên');
				dom.find('.id_name').focus();
				dom.find('.id_name').select();
				return;
			}

			if (candidate.phone == "" || candidate.phone == "chưa nhập")
			{
				alert('Chưa nhập số điện thoại');
				dom.find('.id_phone').focus();
				dom.find('.id_phone').select();
				return;
			}

			if (candidate.curemp == "" || candidate.curemp == "chưa nhập")
			{
				alert('Chưa nhập kinh nghiệm');
				dom.find('.id_exp').focus();
				dom.find('.id_exp').select();
				return;
			}

			if (candidate.color == "" || candidate.color == "chưa nhập")
			{
				alert('Chưa nhập số năm kinh nghiệm');
				dom.find('.id_color').focus();
				dom.find('.id_color').select();
				return;
			}

			if (candidate.tech == "" || candidate.tech == "chưa nhập")
			{
				alert('Chưa nhập công nghệ');
				dom.find('.id_tech').focus();
				dom.find('.id_tech').select();
				return;
			}

			if (candidate.spec == "" || candidate.spec == "chưa nhập")
			{
				alert('Chưa nhập chuyên nghành');
				dom.find('.id_spec').focus();
				dom.find('.id_spec').select();
				return;
			}

			if (candidate.birth == "")
			{
				alert('Chưa nhập ngày sinh');
				dom.find('.id_birth').focus();
				dom.find('.id_birth').select();
				return;
			}

			candidate.junk = false;
			dom.find('.id_save').button('loading');
			$.post('/apply/candidate/edit', candidate, function ()
			{
				dom.find('.id_save').button('reset');
				_done(candidate);
			});

		});

		dom.find('id_tranfer').click(function ()
		{
			if (eid == "")
			{
				alert("Không thể chuyển");
				return;
			}
			dom.modal('hide');

			var em = dom.find('.id_cemail').val();

			$.post('/apply/candidate/send', {eid: -123, email: em }, function ()
			{
				alert('googd');
			});

		});

		dom.find('.id_trash').click(function ()
		{
			dom.modal('hide');
			$(trang).trigger('itemDeleted', index);
		});

		dom.find('.id_nexxt').click(function ()
		{
			var candidate = toCandidate();

			dom.find('.id_nexxt').button('loading');
			$.post('/apply/candidate/edit', candidate, function ()
			{
				dom.find('.id_nexxt').button('reset');
				_done(candidate);
			});

		});

		var _done;

		function setupSelect(links)
		{
			var select = $('<select></select>');
			for (var i in links)
			{
				if ((links[i]).substr(0, 1) != "_")
				{ //LINK
					select.append($('<option value="' + links[i] + ' ">' + links[i] + '</option>'));
					continue;
				}
				//FILE
				select.append($('<option value="' + links[i] + '">' + links[i].substring(links[i].lastIndexOf('/') + 1, links[i].length) + '</option>'));
			}

			return select;
		}


		var _next;
		var _prev;
		var _candidate;


		function toCandidate()
		{
			var candidate = {};
			candidate.fullname = dom.find('.id_name').val();
			candidate.star = !!dom.find('.id_star').prop('checked');
			candidate.gender = !!dom.find('.id_gender')[0]['checked'];
			candidate.id = _candidate.id;
			candidate.phone = dom.find('.id_phone').val();
			candidate.curemp = dom.find('.id_exp').val();
			candidate.color = dom.find('.id_color').val();
			candidate.level = dom.find('.id_level').val();
			candidate.jobid = parseInt(dom.find('.id_job').val());
			candidate.tech = dom.find('.id_tech').val();
			candidate.spec = dom.find('.id_spec').val();
			candidate.salary = dom.find('.id_salary').val();
			candidate.birth = new Date(dom.find('.id_birth').val()).toUTCString();
			candidate.ctime = new Date(dom.find('.id_date').val()).toUTCString();
			candidate.junk = !dom.find('.id_quanlified').is(':checked');
			return candidate;
		}


		function clearForm()
		{
			dom.find('.id_name').val("");

			dom.find('.id_star').prop('checked', false);

			dom.find('.id_gender')[0].checked = true;

			dom.find('.id_phone').val("");
			dom.find('.id_exp').val("");
			dom.find('.id_level').val(0);
			dom.find('.id_color').val(0);

			dom.find('.id_quanlified').prop('checked', false);
			dom.find('.id_tech').val("");
			dom.find('.id_spec').val("");
			dom.find('.id_salary').val("");
			dom.find('.id_birth').val("");
			dom.find('.id_date').val("");


			taglist.setTag([]);
		}

		function toForm(candidate)
		{
			dom.find('.id_name').val(candidate.fullname);
			if (candidate.star == 1)
				dom.find('.id_star').prop('checked', true);

			if (candidate.gender == 1)
				dom.find('.id_gender')[0].checked = true;
			else
				dom.find('.id_gender')[1].checked = true;
			dom.find('.id_quanlified').prop('checked', !candidate.junk);
			dom.find('.id_phone').val(candidate.phone);
			dom.find('.id_exp').val(candidate.curemp);
			dom.find('.id_color').val(candidate.color);
			dom.find('.id_level').val(candidate.level);
			dom.find('.id_cemail').val(candidate.email);
			var jobselect = dom.find('.id_job');
			$.get('/apply/job/list', { archived: false, keyword: "", p: 0, n: 100 }, function (jobs)
			{

				jobs = JSON.parse(jobs);
				for (var i in jobs)
				{

					jobselect.append($('<option value="' + jobs[i].id + '">' + jobs[i].tag + '</option>'));
				}

				jobselect.val(candidate.jobid);
			});

			dom.find('.id_tech').val(candidate.tech);
			dom.find('.id_spec').val(candidate.spec);
			dom.find('.id_salary').val(candidate.salary);
			dom.find('.id_cemail').val(candidate.email);

			dom.find('.id_cengtest').prop('href', "/#test/" + candidate.engtestlink);
			dom.find('.id_ciqtest').prop('href', "/#test/" + candidate.iqtestlink);
			dom.find('.id_birth').val(candidate.birth.year + "-" + ("0" + (candidate.birth.month + 1)).slice(-2) + "-" + ("0" + candidate.birth.dayOfMonth).slice(-2));
			dom.find('.id_date').val(candidate.ctime.year + "-" + ("0" + (candidate.ctime.month + 1)).slice(-2) + "-" + ("0" + candidate.ctime.dayOfMonth).slice(-2));

			taglist.setTag([]);

			for (var f in candidate.comment)
			{
				var comment = candidate.comment[f];
				var vl = comment.value + "";
				vl.constructor.thanh = comment;
				taglist.addTag({ value: vl, label: vl });
			}
		}

		var index;

		function resetFile()
		{
			dom.find('.id_cv').fadeOut("fast", function ()
			{
				dom.find('.id_cv').empty();
				dom.find('.id_cv').append("Không có tệp đính kèm nào");
				dom.find('.id_cv').fadeIn();
			});
		}

		function selectFile(cvs, select)
		{
			dom.find('.id_cv').fadeOut("fast", function ()
			{
				dom.find('.id_cv').empty();
				dom.find('.id_cv').append(cvs[select.find('option:selected').index()]);
				dom.find('.id_cv').fadeIn();
			});
		}

		var eid;
		this.show = function (candidate, index_, eid_, next, prev, done)
		{
				resetFile();
				if (index_ == undefined || index_ == "")
				{
					index_ = candidate.attachments[0];
				}
				if (eid_ == undefined || eid_ == "")
				{
					eid_ = candidate.emailid[0];
				}
				eid = eid_;

				index = index_;
				_candidate = candidate;
				_next = next;
				_prev = prev;
				_done = done;

				clearForm();
				toForm(candidate);
				dom.modal('show');
				var links = candidate.attachments;
				var select = setupSelect(links);

				dom.find('.id_fileselect').empty();
				dom.find('.id_fileselect').append(select);
				var cvs = [];
				select.change(function ()
				{
					selectFile(cvs, select);
				});

				for (var i in links)
				{
					var cvdiv;
					var link = links[i];
					//if (link.substr(0, 1) != "_") { //LINK
					//	cvdiv = $('<a target="_blank" href="' + link + '"><span class="fa fa-external-link"></span>&nbsp; ' + $("<div>").text(link).val() + '</a>');

					//}
					//else {
					//FILE
					if (link[0] == "_")
						link = link.substr(1);

					if (link.substr(0, 9) != "file?path")
					{
						cvdiv = $('<a target="_blank" href="' + link + '"><span class="fa fa-external-link"></span>&nbsp; ' + $("<div>").text(link).val() + '</a>');
					}
					else
					{
						var filetype = link.split('.').pop();
						if (["JPEG", "JPG", "PNG", "GIF", "TIFF", "BMP"].indexOf(filetype.toUpperCase()) != -1)
						{
							cvdiv = $('<img/>');
							cvdiv.css('height', 'auto');
							cvdiv.prop('src', "/apply/" + link);
							cvdiv.addClass('form-control');
						}
						else if (["ODT", "ODF", "DOC", "DOCX", "PPT"].indexOf(filetype.toUpperCase()) != -1)
						{
							var url = "http://docs.google.com/viewer?embedded=true&url=" + encodeURIComponent(document.domain + "/apply/" + link);
							cvdiv = $('<iframe>');
							cvdiv.prop('src', url);
							cvdiv.css('height', '800px');
							cvdiv.addClass('form-control');
						}
						else if (["PDF"].indexOf(filetype.toUpperCase()) != -1)
						{
							var url = "/ui/js/trang/pdfjs/web/viewer.html#" + encodeURIComponent("//" + document.domain + "/apply/" + link);
							cvdiv = $('<iframe>');
							cvdiv.prop('src', url);
							cvdiv.css('height', '100%');
							cvdiv.addClass('form-control');
						}
						else
						{
							cvdiv = $('<p>Khong the xem duoc file online, click vao <a href="' + "/apply/" + link + '">day</a> de download</p>');
						}
					}
					cvs[i] = cvdiv;
				}

				selectFile(cvs, select);
		}
	}

});
