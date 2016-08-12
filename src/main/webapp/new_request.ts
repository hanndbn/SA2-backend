﻿///<reference path="ref.ts" />

module NewRequest
{
	export class Plugin
	{
		onEdit = new plus.Emitter();
		onReady = new plus.Emitter();
		onNew = new plus.Emitter();
		modal: JQuery;
		element: JQuery;
		table: Table.Plugin;
		pagi: Pagination.Plugin;
		saveBt: JQuery;
		unit: string;
		requestid: string;
		private jobdescE: TextEditor.Plugin;
		private inteE: TextEditor.Plugin;
		private reqE: TextEditor.Plugin;
		private ctitle: JQuery;
		private cquantity: JQuery;

		init(htmlElement: HTMLElement, unit: string)
		{
			this.unit = unit;
			var thethis = this;
			this.element = $(htmlElement);

			//tạo barrier để đồng bộ
			var layoutloaded = new plus.Emitter();
			//var dataloaded = new plus.Emitter();
			var dependloaded = new plus.Emitter();

			var dom: JQuery;
			plus.bar([layoutloaded, dependloaded], function ()
			{
				thethis.element.append(dom);
				thethis.saveBt = dom.find('.id_save');
				thethis.ctitle = dom.find('.id_titleTb');
				thethis.cquantity = dom.find('.id_quantity');
				var cjobdesc = dom.find('.id_jobdesc');
				var cinterest = dom.find('.id_inte');
				var crequirement = dom.find('.id_req');

				thethis.jobdescE = new TextEditor.Plugin();
				thethis.inteE = new TextEditor.Plugin();
				thethis.reqE = new TextEditor.Plugin();

				plus.bar([thethis.jobdescE.onReady, thethis.inteE.onReady, thethis.reqE.onReady], function ()
				{
					cjobdesc.append(thethis.jobdescE.dom);
					cinterest.append(thethis.inteE.dom);
					crequirement.append(thethis.reqE.dom);

					var clear = dom.find('.id_clear');
					function clr()
					{
						thethis.ctitle.val("");
						thethis.cquantity.val("");
						thethis.inteE.setCode("");
						thethis.jobdescE.setCode("");
						thethis.reqE.setCode("");
					}
					clear.on('click', clr);

					thethis.saveBt.on('click', function ()
					{
						if (thethis.ctitle.val() == "" || thethis.jobdescE.getCode() == "")
						{
							alert("Dữ liệu không hợp lệ, bạn đã nhập thiếu một trường nào đó");
							return;
						}

						if (thethis.isnew)
						{
							$.post('HRWeb/request/create', {
								unit: thethis.unit,
								title: thethis.ctitle.val(),
								quantity: thethis.cquantity.val(),
								jobdesc: thethis.jobdescE.getCode(),
								endtime: "",
								position: "",
								interest: thethis.inteE.getCode(),
								requirement: thethis.reqE.getCode(),
								starttime: new Date().toString()
							}, function (data)
								{
									var id = JSON.parse(data).result;
									dom['modal']('hide');
									clr();
									thethis.onNew.emit({ id: id });
								})['fail'](function ()
								{
									alert('please try again');
								});

						} else
						{
							$.post('HRWeb/request/edit', {
								id: thethis.r.id,
								title: thethis.ctitle.val(),
								quantity: thethis.cquantity.val(),
								jobdesc: thethis.jobdescE.getCode(),
								endtime: "",
								position: "",
								interest: thethis.inteE.getCode(),
								requirement: thethis.reqE.getCode(),
								starttime: new Date().toString()
							}, function (data)
								{
									dom['modal']('hide');
									thethis.onEdit.emit({ id: thethis.r.id, dom: thethis.r['_dom'] });
									clr();
								})['fail'](function ()
								{
									alert('please try again');
								});
						}
					});
					thethis.modal = dom;
					thethis.onReady.emit();
				});
				thethis.jobdescE.init();
				thethis.inteE.init();
				thethis.reqE.init();
			});

			//lấy layout
			$.get('new_request.html', function (data)
			{
				dom = $(data)
				layoutloaded.emit();
			});

			plus.req(['texteditor.js'], function ()
			{
				dependloaded.emit();
			});
		}

		private isnew = false;
		private r: any;
		show(isnew: boolean = true, r?: any)
		{
			this.r = r;
			this.isnew = isnew;
			if (r !== undefined)
			{
				this.ctitle.val(r.title);
				this.cquantity.val(r.quantity);
				this.jobdescE.setCode(r.jobdesc);
				this.reqE.setCode(r.requirement);
				this.inteE.setCode(r.interest);
			}
			this.modal['modal']('show');
		}



	}
}  