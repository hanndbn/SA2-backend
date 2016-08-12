define(['trang/list/list', 'trang/thai/thai', 'text!trang/candidatelist/candidatelist.html',
	'trang/taglist/taglist', 'trang/sendmail/sendmail', 'trang/inputting/inputting', 'trang/cvhistory/cvhistory', 'trang/envelop/envelop'], function (List, Long, layout, TagList, Sendmail, Inputting, History, Envelop)
{
	return function ()
	{
		var envelop = new Envelop();
		var _joblist, _keyword, _status = -1, _p;
		var ITEMONPAGE = 15;
		var history = new History();
		var view = false;
		var inputting = new Inputting();
		var statemap = {
			'id_tc': -1,
			'id_cpv': 0,
			'id_dx': 1,
			'id_lcv': 2,
			'id_ltbp': 3,
			'id_tt': 4,
			'id_ltt': 5,
			'id_pv': 6,
			'id_dpvtt': 7,
			'id_dpvdp': 8,
			'id_dpvl': 9,
			'-1': 'id_tc',
			'0': 'id_cpv',
			'1': 'id_dx',
			'2': 'id_lcv',
			'3': 'id_ltbp',
			'4': 'id_tt',
			'5': 'id_ltt',
			'6': 'id_pv',
			'7': 'id_dpvtt',
			'8': 'id_dpvdp',
			'9': 'id_dpvl'
		};
		var viewenvelop = false;

		var list = new List();
		var long = new Long();
		var sendmail = new Sendmail();
		var dom = this.dom = list.dom;
		list.dom.find('.id_tablecon').css('top', '50px');
		list.dom.find('.id_sidecon').css('top', '50px');
		list.dom.find('.id_pagination').css('padding', '7px');

		dom.append(long.dom);
		var trang = this;
		var tbhead = $(layout).find('.id_tableheader');
		dom.append($(layout).find('.id_emptylist'));
		var filterdiv = $(layout).find('.id_ffilter');
		var jobtem = $(layout).find('.id_candidatetem');

		var header = $(layout).find('.id_candidateheader');
		var superheader = trang.head = $(layout).find('.id_candidatesuperheader');
		header.find('[data-toggle="tooltip"]').tooltip();
		superheader.find('[data-toggle="tooltip"]').tooltip();
		list.setHeader(header);
		list.setTableHeader(tbhead);
		superheader.find('.id_searchbox').change(function (e)
		{
			trang.list(_joblist, superheader.find('.id_searchbox').val(), _status, 0);

			e.preventDefault();
			return false;
		});

		trang.showEmail = function ()
		{
			viewenvelop = true;
			updateView();
		};


		$('#mybtn').click(function outterFunc(e)
		{
			function innerFunc()
			{
				console.log("innerfunction");
			}

			function anotherClosure()
			{
				var lbl = $('#mylbl');
				lbl.justaprop = innerFunc;
			}

			anotherClosure();
		});

		function filt()
		{
			_filteriq = $(filterdiv.find('.id_filteriq')).val().trim();
			_filtereng = $(filterdiv.find('.id_filtereng')).val().trim();
			_filterexp = $(filterdiv.find('.id_filterexp')).val();
			_filterspec = $(filterdiv.find('.id_filterspec')).val();
			_filtertech = $(filterdiv.find('.id_filtertech')).val();
			_filterlevel = $(filterdiv.find('.id_filterlevel')).val().trim();
			_filtersalary = $(filterdiv.find('.id_filtersalary')).val().trim();
			trang.list(_joblist, _keyword, _status, _p);
		}

		trang.hideEmail = function ()
		{
			viewenvelop = false;
			updateView();
		};

		trang.readEmail = function (id)
		{
			$.post('/apply/candidate/view', {cid: id}, function (data)
			{
				addCandidate(JSON.parse(data));
				list.selectItem(id);
				updateView();
			});
		};

		superheader.find('.id_reply').click(function ()
		{
			var maillist = [];
			sendmail.clear();
			var lll = [];
			var sI = list.getSelectedItem();
			for (var i in sI)
			{
				var can = candb[sI[i]];
				maillist.push(can.email);
				lll.push(can.id);
			}
			sendmail.setToList(maillist, lll);
			sendmail.show();
		});

		var filter = dom.find('.id_filter');

		superheader.find('.id_test').click(function ()
		{
			var ok = confirm("Mở test cho ứng viên ?");
			if (ok == false) return;

			var sC = list.getSelectedItem();
			if (sC.length == 0) return;
			sendmail.wait();
			sendmail.show();

			var count = 0;
			var maillist = [];
			var idlist = [];
			sendmail.clear();
			for (var i in sC)
			{
				if (sC[i] === undefined || candb[sC[i]] === undefined)
					continue;
				(function (ca)
				{

					idlist.push(ca.id);
					maillist.push(ca.email);
					$.post('/apply/test/open', {cid: ca.id, type: 2}, function (data)
					{
						ca.engtestlink = JSON.parse(data);
						$.post('/apply/test/open', {cid: ca.id, type: 1}, function (data)
						{
							ca.iqtestlink = JSON.parse(data);
							count++;
							ca.englishteststate = 0;
							ca.iqteststate = 0;
							ca.iq = -1;
							ca.eng = -1;
							addCandidate(ca);
							updateView();
							if (count == sC.length)
							{
								sendmail.setToList(maillist, idlist);
								$.post('/apply/job/get', {id: candb[sC[0]].jobid}, function (data)
								{
									var job = JSON.parse(data);
									sendmail.setContent(job.setting.testmail);
									sendmail.ready();
								});
							}
						});
					});
				})(candb[sC[i]]);
			}
		});

		function updateFilter(state)
		{
			state = statemap[state];
			filter.find('.id_status').empty();
			var xtem = filter.find("." + statemap["" + state]);
			filter.find('.id_status').append(xtem.html());
		}

		filter.find('.id_cpv').click(function ()
		{
			trang.list(_joblist, _keyword, 0, _p);
			updateFilter('id_cpv');
		});

		filter.find('.id_dx').click(function ()
		{
			trang.list(_joblist, _keyword, 1, _p);
			updateFilter('id_dx');
		});

		filter.find('.id_lcv').click(function ()
		{
			trang.list(_joblist, _keyword, 2, _p);
			updateFilter('id_lcv');
		});

		filter.find('.id_ltbp').click(function ()
		{
			trang.list(_joblist, _keyword, 3, _p);
			updateFilter('id_ltbp');
		});

		filter.find('.id_tt').click(function ()
		{
			trang.list(_joblist, _keyword, 4, _p);
			updateFilter('id_tt');
		});

		filter.find('.id_ltt').click(function ()
		{
			trang.list(_joblist, _keyword, 5, _p);
			updateFilter('id_ltt');
		});

		filter.find('.id_pv').click(function ()
		{
			trang.list(_joblist, _keyword, 6, _p);
			updateFilter('id_pv');
		});

		filter.find('.id_dpvtt').click(function ()
		{
			trang.list(_joblist, _keyword, 7, _p);
			updateFilter('id_dpvtt');
		});

		filter.find('.id_dpvdp').click(function ()
		{
			trang.list(_joblist, _keyword, 8, _p);
			updateFilter('id_dpvdp');
		});

		filter.find('.id_dpvl').click(function ()
		{
			trang.list(_joblist, _keyword, 9, _p);
			updateFilter('id_dpvl');
		});

		filter.find('.id_tc').click(function ()
		{
			trang.list(_joblist, _keyword, -1, _p);
			updateFilter('id_tc');
		});

		//filter
		var _filteriq = "", _filtereng = "", _filterexp = "", _filterspec = "", _filtertech = "", _filtersalary = "", _filterlevel = "";


		function setup(joblist, keyword, status, p)
		{
			if (keyword == undefined) keyword = _keyword;

			if (status == undefined) status = _status;

			if (p == undefined) p = 0;
			_joblist = joblist;
			_keyword = keyword;
			_status = status;
			_p = p;
		}

		function isup(joblist, keyword, status, p)
		{
			if (_joblist == joblist && _keyword == keyword && _status == status && _p == p)
				return true;
			return false;
		}

		$(list).on('next', function (e, p)
		{
			trang.list(_joblist, _keyword, _status, p - 1);

		});
		$(list).on('prev', function (e, p)
		{
			trang.list(_joblist, _keyword, _status, p - 1);
		});

		superheader.find('.id_switchview').click(function ()
		{
			view = !view;
			if (view == true)
			{
				superheader.find('.id_switchview').addClass('red');
				list.dom.find('.id_tag').removeClass('hidden');
			}
			else
			{
				superheader.find('.id_switchview').removeClass('red');
				list.dom.find('.id_tag').addClass('hidden');
			}
		});

		tbhead.find('.id_checkall').change(function ()
		{
			if (this.checked)
			{
				list.selectAll();
			}
			else
			{
				list.unselectAll();
			}
		});


		superheader.find('.jd_junkbtn').click(function ()
		{
			var ok = confirm('Chuyển ứng viên vào hòm Rác ?');
			if (ok == true)
			{
				var ids = list.getSelectedItem();
				for (var i in ids)
				{
					var can = candb[ids[i]];
					if(can !== undefined){
						$.post('/apply/candidate/edit', {id: can.id, jobid: 3});
						can.tem.fadeOut();
						var sectionjob = $('.id_jobtem.id_'+can.jobid).find('.id_ncan');
						sectionjob.html(parseInt(sectionjob.html()) - 1);
					}
				}
				list.unselectAll();
				setTimeout(trang.list(_joblist, _keyword, _status, _p),3000);
			}
		});

		superheader.find('.id_export').click(function ()
		{
			$.post('/apply/candidate/export', {can: list.getSelectedItem()}, function (data)
			{
				data = JSON.parse(data);
				var win = window.open("", '_parent');
				win.location.href = "/apply/" + data;
			})
		});

        superheader.find('.id_download').click(function ()
        {
			var id= list.getSelectedItem();
			if(id.length < 1){
				alert('Vui lòng chọn CV');
				return false;
			}
			downloadCV(id,0,id.length);
        });
		function downloadCV(id,count,max)
		{
			$.post('/apply/candidate/downloadcv', {id: id[count]}, function (data)
			{
				var win = undefined;
				data = JSON.parse(data);
				if(data.indexOf("file?path") !== -1){
					if(data.indexOf("pdf") !== -1 || data.indexOf("png") !== -1 || data.indexOf("jpg") !== -1){
						win = window.open("");
					} else win = window.open("","_parent");
					win.location.href = "/apply/" + data;
				}else {
					alert(data);
				}
				count++;
				if(count != max) setTimeout(downloadCV(id,count,max),500);
			})
		}

		function getValue(str)
		{
			//"<", ">", "<=", ">=", "=", "like"
			// 0   1     2     3     4    5
			if (str == undefined) str = "";
			if (str.length == 0) return {op: 4, va: ""};
			var fc = str.charAt(0);
			var o = 4, v;
			var starv = 0;
			if (fc == '>')
			{
				starv++;
				o = 1;
				if (str.length > 1 && str.charAt(1) == '=')
				{
					o = 3;
					starv++;
				}
			}
			else if (fc == '<')
			{
				starv++;
				o = 0;
				if (str.length > 1 && str.charAt(1) == '=')
				{
					o = 2;
					starv++;
				}
			}
			else if (fc == '=')
			{
				starv++;
				o = 4;
			}
			return {op: o, va: str.substr(starv)}
		}

		$(inputting).on('itemDeleted', function (e)
		{
			var id = e;
			$.post('/apply/candidate/edit', {id: id, jobid: 3});
			candb[id].tem.fadeIn();
		});

		superheader.find('.id_duyet').change(function ()
		{
			trang.list(_joblist, _keyword, _status, _p);
		});

		var jobdb = {};
		var candb = {};
		//METHOD
		trang.list = function (joblist, keyword, status, p)
		{
			list.transparentAll();
			list.unselectAll();
			tbhead.find('.id_checkall').prop('checked',false);
			long.wait();
			candb = {};

			var n = ITEMONPAGE;
			if (p == undefined)
			{
				p = 0;
				_p = 0;
			}
			if (status == undefined)
			{
				status = -1;
				_status = -1;
			}
			if (keyword == undefined) keyword = _keyword = "";
			if (joblist == undefined) joblist = _joblist;

			setup(joblist, keyword, status, p);

			$.post('/apply/job/list', {archived: false, keyword: "", p: 0, n: 100}, function (jobs)
			{
				//neu nguoi dung da gui query khac thi thoat luon
				if (!isup(joblist, keyword, status, p))
					return;
				jobs = JSON.parse(jobs);
				for (var i in jobs)
				{
					jobdb[jobs[i].id] = jobs[i];
				}

				var fils = [], opes = [], vals = [];
				//	_filteriq, _filtereng, _filterexp, _filterspec, _filtertech, _filtersalary, _filterlevel;
				//"tech", "level", "spec", "twe", "salary", "curemp", "cjt", "junk", "gender", "birth", "ctime", "state", "resumestatus", "iq", "eng"
				fils.push(13, 14, 5, 2, 0, 4, 1);


				opes.push(getValue(_filteriq).op);
				opes.push(getValue(_filtereng).op);
				opes.push(getValue(_filterexp).op);
				opes.push(getValue(_filterspec).op);
				opes.push(getValue(_filtertech).op);
				opes.push(getValue(_filtersalary).op);
				opes.push(getValue(_filterlevel).op);

				vals.push(getValue(_filteriq).va);
				vals.push(getValue(_filtereng).va);
				vals.push(getValue(_filterexp).va);
				vals.push(getValue(_filterspec).va);
				vals.push(getValue(_filtertech).va);
				vals.push(getValue(_filtersalary).va);
				vals.push(getValue(_filterlevel).va);


				//vals.push(0);//false
				//opes.push(4); // ==
				//fils.push(7); //junk


				$.post('/apply/candidate/count', {
					job: joblist,
					keyword: keyword,
					archived: true,
					n: n,
					p: p,
					status: status,
					filter: fils,
					value: vals,
					operator: opes
				}, function (number)
				{
					if (!isup(joblist, keyword, status, p))
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


					$.post('/apply/candidate/list', {
						job: joblist,
						keyword: keyword,
						archived: true,
						n: n,
						p: p,
						status: status,
						filter: fils,
						value: vals,
						operator: opes
					}, function (data)
					{
						if (!isup(joblist, keyword, status, p))
							return;
						//neu nguoi dung da gui query khac thi thoat luon
						if (!isup(joblist, keyword, status, p)) return;
						data = JSON.parse(data);
						length = data.length;
						listprogress = 0;

						for (var i in data)
						{
							(addCandidate(data[i]));
						}
						updateView();

					});
				});
			});
			//d();
		};
		var listprogress = 0;
		var length;

		function updateProgress()
		{
			listprogress++;
			if (listprogress == length)
			{
				listprogress = 0;
				long.ready();
			}
		}

		$(list).on('itemDeleted', function ()
		{
			var ids = list.getSelectedItem();
			for (var i in ids)
			{
				var can = candb[ids[i]];
				$.post('/apply/candidate/edit', {id: can.id, jobid: 3});
				can.tem.fadeOut();
			}
			list.unselectAll();
		});

		function updateView()
		{
			if (viewenvelop)
			{
				dom.css('height', '50%');
			}
			else
			{
				dom.css('height', '100%');
			}
			/*
			 if (viewenvelop)
			 {

			 dom.find('.candidate_showable').each(function ()
			 {
			 $(this).removeClass('hidden');
			 });
			 jobtem.find('.candidate_hiddenable').each(function ()
			 {
			 $(this).fadeOut({duration: 0})
			 });
			 dom.find('.candidate_hiddenable').each(function ()
			 {
			 $(this).fadeOut({duration: 0})
			 });
			 }
			 else
			 {
			 dom.find('.candidate_showable').each(function ()
			 {
			 $(this).addClass('hidden');
			 });

			 jobtem.find('.candidate_hiddenable').each(function ()
			 {
			 $(this).fadeIn({duration: 0})
			 });
			 dom.find('.candidate_hiddenable').each(function ()
			 {
			 $(this).fadeIn({duration: 0})
			 });
			 }
			 */
		}

		function addCandidate(can)
		{
			var tem = jobtem.clone();
			can.tem = tem;

			tem['huong'] = function ()
			{
				list.unselectAll();
				list.selectItem(can.id);
				$(trang).trigger('itemSelected', can);
				if (viewenvelop)
				{


					/*
					 function done(id)
					 {
					 $.post('/apply/email/markasread', {id: id}, function (data)
					 {
					 tem.currentmailid = id;
					 $.post('/apply/candidate/view', {cid: can.id}, function (data)
					 {
					 addCandidate(JSON.parse(data));
					 list.selectItem(can.id);
					 updateView();
					 });
					 });
					 }
					 enviframe[0].contentWindow.postMessage("wait", document.location.origin);

					 $.post('/apply/email/listnew', { email: can.email, n: 1, p: 0 }, function (data)
					 {
					 data = JSON.parse(data);
					 data = data[0];
					 if (data == undefined) data = {id: ''};
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
					 */
				}
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

			tem.find('.id_unread').click(function ()
			{

				$.post('/apply/email/markasunread', {id: tem.currentmailid}, function ()
				{

					//tem.find('.id_newlbl').removeClass('hidden');
					tem.css('font-weight', 'bold');
				});
			});
			tem.find('.id_actiondownloadcan').click(function(){
				$.post('/apply/candidate/downloadcv', {id: can.id}, function (data)
				{
					var win = undefined;
					data = JSON.parse(data);
					if(data.indexOf("file?path") !== -1){
						if(data.indexOf("pdf") !== -1 || data.indexOf("png") !== -1 || data.indexOf("jpg") !== -1){
							win = window.open("");
						} else win = window.open("","_parent");
						win.location.href = "/apply/" + data;
					}else {
						alert(data);
					}
				})
			});

			tem.find('.id_trash').click(function ()
			{
				$.post('/apply/candidate/edit', {id: can.id, jobid: 3});
				tem.fadeOut();

				var sib = findNext(can.id);
				if (sib == -1) sib = findPrev(can.id);

				if (sib != -1)
				{
					candb[sib].tem['huong']();
				}
				else
				{
					//pass null object to iframe
					enviframe[0].contentWindow.postMessage(JSON.stringify({id: ''}), document.location.origin);
				}

				delete candb[can.id];
			});


			tem.find('.n_unread').html('');

			if (can.unread > 0)
			{
				//tem.find('.id_newlbl').removeClass('hidden');
				tem.css('font-weight', 'bold');
				if (can.unread > 1)
				{
					tem.find('.n_unread').html(can.unread);
				}

			}

			if (can.junk == true)
			{
				tem.addClass('draf');
			}

			if (can.replied == true)
			{
				tem.find('.id_reply').removeClass('hidden');
			} else
			{
				tem.find('.id_reply').addClass('hidden');
			}

			tem.on('dragstart', function (evt)
			{
				var data = {};

				var sC = list.getSelectedItem();
				if (sC.indexOf(can.id) == -1) sC.push(can.id);
				data.ids = sC;
				data.callback = moved;
				data.param = data.ids;
				data.oldjobid = can.jobid;
				var nguyen = "nguyen";
				nguyen.constructor.data = data;

				evt.originalEvent.dataTransfer.setData("nguyen", nguyen);
                setTimeout(function () {
                    trang.list(_joblist, _keyword, _status, _p);
                },2000);

			});

			function moved(param, jobid)
			{
				for (var i in param)
				{
					list.transparent(param[i]);
					$.post('/apply/candidate/edit', {id: param[i], jobid: jobid});
				}
			}

			can.tem.css('cursor', 'pointer');


			candb[can.id] = can;
			//status
			function updatestate(state)
			{
				var sC = list.getSelectedItem();
				if (sC.indexOf(can.id) == -1) sC.push(can.id);
				for (var i in sC)
				{
					var canid = sC[i];
					(function (can)
					{
						can.tem.find('.id_jobstatus').empty();
						var xtem = can.tem.find("." + statemap["" + state]);
						can.tem.find('.id_jobstatus').append(xtem.find('i').clone());
						can.resumestatus = state;

						$.post('/apply/candidate/edit', {id: can.id, status: state});
					})(candb[canid]);
				}
			}

			tem.find('.id_cpv').click(function (e)
			{
				updatestate(statemap['id_cpv']);
			});

			tem.find('.id_dx').click(function (e)
			{
				updatestate(statemap['id_dx']);

			});

			tem.find('.id_lcv').click(function (e)
			{
				updatestate(statemap['id_lcv']);

			});

			tem.find('.id_ltbp').click(function (e)
			{
				updatestate(statemap['id_ltbp']);

			});

			tem.find('.id_tt').click(function ()
			{
				updatestate(statemap['id_tt']);
			});

			tem.find('.id_ltt').click(function ()
			{
				updatestate(statemap['id_ltt']);
			});

			tem.find('.id_pv').click(function ()
			{
				updatestate(statemap['id_pv']);
			});

			tem.find('.id_dpvtt').click(function ()
			{
				updatestate(statemap['id_dpvtt']);
			});

			tem.find('.id_dpvdp').click(function ()
			{
				updatestate(statemap['id_dpvdp']);
			});

			tem.find('.id_dpvl').click(function ()
			{
				updatestate(statemap['id_dpvl']);
			});

			tem.find('.id_jobstatus').empty();
			tem.find('.id_jobstatus').append(tem.find("." + statemap[can.resumestatus]).find('i').clone());

			tem.find('.id_name').html(" " + can.fullname + " ");

			function setTestDiv(point, state, link, div)
			{
				if (point == -2)
				{
					div.html("...");
					div.removeClass("label label-grey");
					div.removeClass("label label-success");
					div.addClass("label label-warning");
				} else if (point == -1 && link !== undefined)
				{
					div.html("ON");
					div.removeClass(" label-grey");
					div.addClass("label label-success");
					div.removeClass(" label-warning");
				}
				else if (link === undefined && point == 0)
				{
					div.html("OFF");
					div.addClass("label label-grey");
					div.removeClass(" label-success");
					div.removeClass(" label-warning");

				} else if (link === undefined && point != 0)
				{

					div.removeClass("label-grey");
					div.removeClass("label-success");
					div.removeClass("label-warning");
					div.html(point);

				}
			}

			//iq
			var iqdiv = tem.find('.id_iq');
			setTestDiv(can.iq, can.iqteststate, can.iqtestlink, iqdiv);

			//eng
			var engdiv = tem.find('.id_eng');
			setTestDiv(can.eng, can.englishteststate, can.engtestlink, engdiv);

			iqdiv.click(function ()
			{

				var ok = confirm("Mở IQ test cho ứng viên ?");
				if (ok == false) return;

				var sC = list.getSelectedItem();
				if (sC.indexOf(can.id) == -1) sC.push(can.id);

				sendmail.wait();
				sendmail.show();

				var count = 0;
				var maillist = [];
				var idlist = [];
				sendmail.clear();
				for (var i in sC)
				{
					(function (ca)
					{
						iqdiv.addClass('loading');
						idlist.push(ca.id);
						setTestDiv(-1, ca.tem.find('.id_iq'));
						maillist.push(ca.email);
						$.post('/apply/test/open', {cid: ca.id, type: 1}, function (data)
						{
							count++;
							iqdiv.removeClass('loading');
							ca.iqtestlink = JSON.parse(data);
							ca.iq = -1;

							if (count == sC.length)
							{
								sendmail.setToList(maillist, idlist);
								$.post('/apply/job/get', {id: candb[sC[0]].jobid}, function (data)
								{
									var job = JSON.parse(data);
									sendmail.setContent(job.setting.testmail);

									sendmail.ready(this);
								});
							}
						});
					})(candb[sC[i]]);
				}
			});

			engdiv.click(function ()
			{
				var ok = confirm("Mở test tiếng anh cho ứng viên ?");
				if (ok == false) return;

				var sC = list.getSelectedItem();
				if (sC.indexOf(can.id) == -1) sC.push(can.id);
				sendmail.clear();
				sendmail.wait();
				sendmail.show();

				var count = 0;
				var maillist = [];
				var idlist = [];
				for (var i in sC)
				{
					(function (ca)
					{
						engdiv.addClass("loading");
						setTestDiv(-1, ca.tem.find('.id_eng'));
						idlist.push(ca.id);
						maillist.push(ca.email);
						$.post('/apply/test/open', {cid: ca.id, type: 2}, function (data)
						{
							count++;
							engdiv.removeClass("loading");
							ca.engtestlink = JSON.parse(data);
							ca.eng = -1;
							if (count == sC.length)
							{
								sendmail.setToList(maillist, idlist);
								$.post('/apply/job/get', {id: candb[sC[0]].jobid}, function (data)
								{
									var job = JSON.parse(data);
									sendmail.setContent(job.setting.testmail);
									sendmail.ready(this);
								});
							}

						});
					})(candb[sC[i]]);
				}
			});

			//jobid
			tem.find('.id_job').html(to30Character(jobdb[can.jobid].tag));
			tem.find('.id_job').attr('data-original-title', jobdb[can.jobid].title);
			tem.find('[data-toggle="tooltip"]').tooltip();

			//	tem.find('.id_joblist').empty();

			tem.find('.id_viewtag').click(function ()
			{

				if (tem.find('.id_tag').hasClass('hidden'))
				{
					tem.find('.id_tag').removeClass('hidden')
				}
				else
				{
					tem.find('.id_tag').addClass('hidden')
				}
			});

			/*
			 for (var t in jobdb) {
			 (function (job) {

			 var jl = $('<li><a href="#">' + job.title + '</a></li>');
			 jl.click(function (e) {
			 e.stopPropagation();
			 var sC = list.getSelectedItem();

			 if (sC.indexOf(can.id) == -1) sC.push(can.id);
			 for (var i in sC) {
			 var canid = sC[i];

			 (function (can) {

			 $.post('/apply/candidate/edit', { cid: can.id, jobid: job.id }, function (data) {
			 can.jobid = job.id;
			 can.tem.find('.id_job').html(jobdb[can.jobid].title);
			 });

			 })(candb[canid]);
			 }
			 });
			 tem.find('.id_joblist').append(jl);
			 })(jobdb[t]);
			 }

			 */
			//	tem.find('.id_attcount').html(can.attachments.length);
			if (can.attachments.length == 0)
			{
				tem.find('.id_icon').addClass('trans');
			}
			else
			{
				for (var k in can.attachments)
				{
					//todo

					var li = $('<li><a >' + can.attachments[k] + ' </a></li>');
					tem.find('.id_attachment').append(li);
					li.click(function (e)
					{
						inputting.show(can, can.attachments[k], can.emailid[k], nextCandidate, prevCandidate, done);
					})

				}
			}

			tem.find('.id_email').html(to30Character(can.email));
			tem.find('.id_phone').html(can.phone);

			if (can.gender == true)
			{
				tem.find('.id_gender').removeClass('fa-female');
				tem.find('.id_gender').addClass('fa-male');
			} else
			{

				tem.find('.id_gender').addClass('fa-female');
				tem.find('.id_gender').removeClass('fa-male');
			}

			tem.find('.id_birth').html(can.birth.dayOfMonth + "-" + (can.birth.month + 1) + "-" + can.birth.year);
			tem.find('.id_ctime').html(can.ctime.dayOfMonth + "-" + (can.ctime.month + 1) + "-" + can.ctime.year);
			tem.find('.id_level').html(to30Character(can.level));
			tem.find('.id_salary').html(to30Character(can.salary));
			tem.find('.id_tech').html(to30Character(can.tech));
			tem.find('.id_spec').html(to30Character(can.spec));
			tem.find('.id_exp').html(to30Character(can.curemp));

			tem.find('.id_name').html(can.fullname);
			tem.find('.id_id').html(can.id);
			tem.find('.id_history').click(function ()
			{

				history.show(can.id);
			});
			tem.find('.id_edit').click(function ()
			{

				inputting.show(can, "", "", nextCandidate, prevCandidate, done);
			});


			tem.find('.id_star').click(function ()
			{
				can.star = !can.star;
				tem.find('.id_star').addClass('light-orange');

				$.post('/apply/candidate/edit', {id: can.id, star: can.star}, function ()
				{
					tem.find('.id_star').removeClass('light-orange');
					updateStar();
				});
			});

			function updateStar()
			{
				if (can.star == false)
				{
					tem.find('.id_star').addClass('light-grey');
					tem.find('.id_star').removeClass('orange');
				}
				else
				{
					tem.find('.id_star').addClass('orange');
					tem.find('.id_star').removeClass('light-grey');
				}
			}

			updateStar();

			if (!view)
			{
				tem.find('.id_tag').addClass('hidden');
			}
			else
			{
				tem.find('.id_tag').removeClass('hidden');
			}

			var commlist = new TagList('#d9534f');

			tem.find('.id_commentanchor').after(commlist.dom);

			$(commlist).on('tagcreated', function (e, tag)
			{
				if (tag.attrs.label.constructor.thanh !== undefined)
				{
					tag.relatedTarget.thanh = {};
					tag.relatedTarget.thanh.id = tag.attrs.label.constructor.thanh.id;
					tag.attrs.label.constructor.thanh = undefined;
				}
				else
				{
					tag.relatedTarget.thanh = {};
					$(tag.relatedTarget).addClass('loading');


					$.post('/apply/candidate/comment', {cid: can.id, comment: tag.attrs.value}, function (data)
					{
						data = JSON.parse(data);
						tag.relatedTarget.thanh.id = data.result;
						$(tag.relatedTarget).removeClass('loading');
					});
				}
			});

			$(commlist).on('tagedited', function (e, tag)
			{
				$(tag.relatedTarget).addClass('loading');
				$.post('/apply/comment/delete', {id: tag.relatedTarget.thanh.id}, function ()
				{
				});
			});

			$(commlist).on('tagdelete', function (e, tag)
			{
				//if delete by user then confirm first
				var r = confirm('Xác nhận xóa [' + tag.attrs.label + ']');
				if (r == false)
				{
					tag.preventDefault();
					return false;
				}

				$(tag.relatedTarget).addClass('loading');
				$.post('/apply/comment/delete', {id: tag.relatedTarget.thanh.id}, function ()
				{
					tag.relatedTarget.thanh.delete == true;
					$(tag.relatedTarget).fadeOut();
				});

			});

			for (var ci in can.comments)
			{
				var comment = can.comments[ci];
				var vl = comment.value + "";
				vl.constructor.thanh = comment;
				commlist.addTag({value: vl, label: vl});

			}


			//fieldlist.addTag({ value: "email", label: can.email });

			//								for (var ci in can.fields) {
			//								var field = can.fields[ci];
			//							fieldlist.addTag({ value: field, label: field.content });
			//					}


			can.tem.dblclick(function ()
			{
				inputting.show(can, "", "", nextCandidate, prevCandidate, done);
			});
			list.add(tem, undefined, can.id);

			updateProgress();
		}

		//EVENT


		function done(can)
		{

			$.post('/apply/candidate/view', {cid: can.id}, function (data)
			{
				data = JSON.parse(data);
				addCandidate(data);
				updateView();
			});

		}

		function nextCandidate()
		{

		}

		function prevCandidate()
		{

		}

		function to30Character(s)
		{
			if (s.length > 30)
				return s.substr(0, 30) + "...";
			else return s;
		}

		//PROPERTY

		function findNext(id)
		{
			var next = false;
			for (var i in candb)
			{
				if (next == true) return i;
				if (i == id) next = true;
			}
			return -1;
		}

		function findPrev(id)
		{
			var last = -1;// = false;
			for (var i in candb)
			{
				if (i == id) return last;
				last = i;
			}
			return -1;
		}


		this.wait = function ()
		{
			dom.find('.loading').removeClass('hidden');
		};

		this.ready = function ()
		{
			dom.find('.loading').addClass('hidden');
		};


		//initial list
		trang.list([0], "", -1, 0);

		this.reload = function (archived, keyword, p)
		{
			archived = archived === undefined ? false : archived;
			keyword = keyword === undefined ? "" : keyword;
			p = p === undefined ? 1 : p;
			$('/hr/job/count', {archived: archived, keyword: keyword, p: p, n: 20}, function (ret)
			{
				var n = Math.ceil(ret.result / 20);
				$('/hr/job/list', {archived: archived, keyword: keyword, p: p, n: 20}, function (data)
				{
					for (var i in data)
					{
						list.add(jobtem);
					}
				});
			});
		}
	}
});