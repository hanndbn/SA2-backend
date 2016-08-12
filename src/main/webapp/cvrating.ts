///<reference path="ref.ts" />

module CVRating
{
	export class Plugin
	{
		//onReady = new plus.Emitter();

		//table: Table.Plugin;
		//pagi: Pagination.Plugin;
		//picker: RequestPicker.Plugin;


		//button: JQuery;
		//dom: JQuery;

		//init(requestid: number)
		//{
		//	var thethis = this;

		//	//tạo barrier để đồng bộ
		//	var layoutloaded = new plus.Emitter();
		//	var dataloaded = new plus.Emitter();
		//	var dependloaded = new plus.Emitter();

		//	plus.bar([dependloaded, layoutloaded, dataloaded], function ()
		//	{
		//		//Sau khi các plugin đã load xong
		//		plus.bar([thethis.table.onReady, thethis.pagi.onReady, thethis.picker.onReady], function ()
		//		{
		//			thethis.dom.find('.id_table').append(thethis.table.dom);
		//			thethis.dom.find('.id_pagi').append(thethis.pagi.dom);
		//			thethis.dom.find('.id_requestpicker').append(thethis.picker.dom);

		//			thethis.pagi.setN(n);

		//			thethis.table.setUpTable(['#', 'Name', 'Submit date', 'IQ', 'PRES', 'ARCH', 'POTE', 'SUM', 'Rating', 'Group', 'Comment' , 'Action']);

		//			for (var i in cvlist)
		//			{
		//				(function ()
		//				{
		//					var e = i;
		//					var cv = cvlist[i];
		//					var acm = new ActionMenu.Plugin();

		//					var rate = new StarRating.Plugin();
		//					var vote = new Voting.Plugin();

		//					plus.bar([acm.onReady, rate.onReady, vote.onReady], function ()
		//					{
		//						acm.addItem('Details', 'glyphicon glyphicon-eye-open', function ()
		//						{
		//							window.location.href = "HRWeb/tvg.html#cv_filtering?requestid=" + cv.id;
		//						});
		//						acm.addItem('Reject', 'glyphicon glyphicon-eye-open', function ()
		//						{
		//							console.debug(cv);
		//						});
		//						acm.addItem('Edit', 'glyphicon glyphicon-eye-open', function ()
		//						{
		//							console.debug(cv);
		//						});


		//						thethis.table.addRow({
		//							0: (1 + parseInt(e)),
		//							1: cv.name,
		//							2: cv.submitdate,
		//							3: cv.iq,
		//							4: cv.pres,
		//							5: cv.arch,
		//							6: cv.pote,
		//							7: cv.sum,
		//							8: rate.dom,
		//							9: vote.dom,
		//							10: acm.dom
		//						});
		//					});

		//					acm.init();
		//					rate.init();
		//					vote.init();
		//				})();
		//			}

		//			thethis.onReady.emit();
		//		});

		//		thethis.pagi.init();
		//		thethis.table.init();
		//	thethis.picker.init("0",requestid.toString() );

		//	});

		//	//Khởi tạo các control phụ thuộc
		//	plus.req(['table.js', 'pagination.js',
		//		'actionmenu.js',
		//		'requestpicker.js', 'voting.js',
		//		'starrating.js'], function ()
		//		{
		//			thethis.table = new Table.Plugin();
		//			thethis.pagi = new Pagination.Plugin();
		//			thethis.picker = new RequestPicker.Plugin();

		//			dependloaded.emit();
		//		});

		//	//lấy layout
		//	$.get('cvmgr.html', function (data)
		//	{
		//		thethis.dom = $(data);
		//		layoutloaded.emit();
		//	});

		//	//lấy dữ liệu
		//	var n;
		//	var cvlist;
		//	$.get('HRWeb/cv/list', { requestid: requestid, p: 0, ps: 20, field: ['title', 'time'], sortby: 'title' }, function (data)
		//	{
		//		cvlist = JSON.parse(data);
		//		$.get('HRWeb/cv/count', { requestid: requestid }, function (data)
		//		{
		//			n = JSON.parse(data).return;
		//			dataloaded.emit();
		//		});
		//	});


		//}

		//dim()
		//{
		//	this.button.addClass('btn-danger');
		//}

		//wake()
		//{
		//	this.button.removeClass('btn-danger');
		//}
	}
}  