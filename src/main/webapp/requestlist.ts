///<reference path="ref.ts" />

module RequestList
{
	export class Plugin
	{
		onReady = new plus.Emitter();
		pagi:Pagination.Plugin;
		dom:JQuery;
		defjob:JQuery;
		content:JQuery;
		private p:number;
		private k:string;

		loadJobs(p:number, k:string)
		{
			var self = this;
			var thethis = this;
			this.content.empty();

			plus['wait'].emit();
			$.get('HRWeb/request/count2', { k: k  }, function (data)
			{
				var n = JSON.parse(data).result;
				thethis.pagi.setN(Math.ceil(n / 20));
				thethis.pagi.setI(p + 1);
				if (n == 0)
				{
					thethis.content.append("<div class='col-md-12'><p class='alert alert-warning'><span class='fa fa-meh-o' style='font-size:xx-large'></span>  &nbsp;Hiện tại hệ thống không tìm được tin tuyển dụng nào phù hợp với từ khóa bạn tìm kiếm. Vui lòng thử lại sau.</p></div>");
					plus['ready'].emit();
				}
				else
				{
					$.get('HRWeb/request/list2', { p: p, ps: 20, k: k  }, function (data)
					{
						thethis.p = p;
						thethis.k = k;

						data = JSON.parse(data);
						for (var i in data)
						{
							var req = data[i];
							var job = self.defjob.clone();
							job.removeClass("hidden id_defjob");


							var reqtime = new Date(req.ctime);
							var curtime = new Date();

							var deltat :number = (curtime.getTime() - reqtime.getTime()) / 1000;

							var u:string = "giây";
							if (deltat < 60)
							{
							} else if (deltat < 3600)
							{
								u = "phút";
								deltat /= 60;
							}
							else if (deltat < 86400)
							{
								u= "giờ";
								deltat /= 3600;
							}
							else {
								u= "ngày";
								deltat /= 86400;
							}deltat = Math.ceil(deltat);

							job.find('.id_date').html("Đăng "+ deltat +" " + u+" trước");


							job.find('.id_title').html(req.title);
							job.find('.id_jobdesc').html(req.jobdesc);
							job.find('.id_unit').html(req.unit.name);
							job.find('.id_apply').html(req.totalcv);
							job.find('.id_details').attr('href', '#uploadcv/' + req.id);

							self.content.append(job);
						}
						plus['ready'].emit();
					}) ['fail'](()=>
					{
						alert('cant connect to the server');
						plus['ready'].emit();
					});
				}

			})['fail'](()=>
			{
				alert('cant connect to the server');
				plus['ready'].emit();
			});

		}

		init(p:number, k:string)
		{

			if (isNaN(p))
				p = 0;
			this.p = p;
			this.k = k;

			var thethis = this;

			var layoutloaded = new plus.Emitter();
			var dependloaded = new plus.Emitter();

			plus.bar([dependloaded, layoutloaded], function ()
			{
				//Sau khi các plugin đã load xong
				plus.bar([ thethis.pagi.onReady], function ()
				{
					thethis.dom.find('.id_pagi').append(thethis.pagi.dom);

					thethis.pagi.onNavigate.on(function (data)
					{
						thethis.p = data.index;
						thethis.loadJobs(thethis.p, thethis.k);
					});

					thethis.loadJobs(thethis.p, thethis.k);
					thethis.onReady.emit();
				});

				thethis.pagi.init();

			});

			plus.req([ 'pagination.js'], function ()
			{
				thethis.pagi = new Pagination.Plugin();

				dependloaded.emit();
			});

			$.get('requestlist.html', function (data)
			{
				thethis.dom = $(data);
				/*thethis.dom.find('.id_applybtn').on('click',function()
				{
					-;
				});*/

				thethis.dom.find('.id_search').on('change', function ()
				{
					thethis.k = $(this).val();
					thethis.loadJobs(thethis.p, thethis.k);
				});

				thethis.content = thethis.dom.find('.id_content');
				thethis.defjob = thethis.dom.find('.id_defjob');
				layoutloaded.emit();
			});

		}

	}
}