/////<reference path="jquery.d.ts" />
/////<reference path="framework.ts" />

//module NewField
//{
//	export class Plugin
//	{
//		onReady = new plus.Emitter();
//		onCreated = new plus.Emitter();
//		onEdited = new plus.Emitter();
//		modal: JQuery;
//		saveBt: JQuery;
//		dom : JQuery;
//		button: JQuery;
//		ctitle: JQuery;

//		init()
//		{
//			var thethis = this;
		
//			//tạo barrier để đồng bộ
//			var layoutloaded = new plus.Emitter();
//			//var dataloaded = new plus.Emitter();
//			//var dependloaded = new plus.Emitter();

//			plus.bar([layoutloaded], function ()
//			{
//				thethis.saveBt = thethis.dom.find('.id_save');
//				var ctitle = thethis.dom.find('.id_title');
//				var cpublic = thethis.dom.find('.id_public');
				
//				var clear = thethis.dom.find('.id_clear');
//				function clr()
//				{
//					ctitle.val("");
//					cpublic.val('false');
//				}
//				clear.on('click', clr);


//				thethis.saveBt.on('click', function ()
//				{
//					if (thethis.isnew)
//					{
//						$.post('HRWeb/question/create', {
//							unit: thethis.unit,
//							type: thethis.type,
//							content: thethis.contenteditor.getCode(),
//							answer: 1,
//							choose: cchoose.val(),
//							weight: cweight.val(),
//							isdraf: false
//						}, function (data)
//							{
//								var id = JSON.parse(data).result;
//								dom['modal']('hide');
//								clr();
//								thethis.onCreated.emit({ id: id });
//							})['fail'](function ()
//							{
//								alert('please try again');
//							});;
//					}
//					else
//					{
//						$.post('HRWeb/question/edit', {
//							id: thethis.q.qid,
//							type: thethis.type,
//							content: thethis.contenteditor.getCode(),
//							choose: cchoose.val(),
//							weight: cweight.val()
//						}, function ()
//							{
//								dom['modal']('hide');
//								thethis.onDoneEdit.emit({ id: thethis.q.qid, dom: thethis.q['dom'] });
//								clr();
//							})['fail'](function ()
//							{
//								alert('please try again');
//							});
//					}

//				thethis.saveBt.on('click', function ()
//				{
//					$.post('HRWeb/cvfield/create', {
//						cvform: 0,
//						title: ctitle.val(),
//						state: cpublic.val(),
//					});
//					thethis.dom['modal']('hide');
//					clr();
//				});
//				thethis.modal = thethis.dom;
//				thethis.onReady.emit();
//			});

//			//lấy layout
//			$.get('new_cvfield.html', function (data)
//			{
//				thethis.dom = $(data)
//				layoutloaded.emit();
//			});
//		}

//	}
//}  