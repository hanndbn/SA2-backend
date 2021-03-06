﻿///<reference path="ref.ts" />

module CVQuickview
{
	export class Plugin {
		onEdit = new plus.Emitter();
		onReady = new plus.Emitter();
		onNew = new plus.Emitter();
		modal:JQuery;
		cvid:string;
		field:JQuery;
		cv:JQuery;
		wait:JQuery;
		w = 0;
		dom:JQuery;
		fieldloaded = new plus.Emitter();
		title:JQuery;
		cvloaded = new plus.Emitter();

		init() {
			var thethis = this;

			//tạo barrier để đồng bộ
			var layoutloaded = new plus.Emitter();
			//var dataloaded = new plus.Emitter();

			plus.bar([layoutloaded], function () {
				thethis.cv = thethis.dom.find('.id_cv');
				thethis.field = thethis.dom.find('.id_field');
				thethis.title = thethis.dom.find('.id_title');
				thethis.modal = thethis.dom;
				thethis.wait = thethis.dom.find('.id_waiting');
				thethis.onReady.emit();
			});
			//lấy layout
			$.get('cvquickview.html', function (data) {
				thethis.dom = $(data)
				layoutloaded.emit();
				thethis.onReady.emit;
			});
		}

		private cvdata:string;
		private fielddata:string;

		private doJob() {
			var me = this;
			if (me.w == 0) {
				me.wait.addClass('hidden');
				if ((<string>me.cvdata).substr(0, 1) != "_") //LINK
				{
					var cvdiv = $('<a target="_blank" href="' + me.cvdata + '"><span class="fa fa-external-link"></span>&nbsp; ' + $("<div>").text(me.cvdata).html() + '</a>');
				} else {
					me.cvdata = me.cvdata.substr(1);

					var filetype = me.cvdata.split('.').pop();
					if (["JPEG", "JPG" , "PNG", "GIF", "TIFF", "BMP"].indexOf(filetype.toUpperCase()) != -1) {
						var cvdiv = $('<img/>');
						cvdiv.css('height', 'auto')
						cvdiv.attr('src', me.cvdata);
						cvdiv.addClass('form-control');
					}
					else if (["ODT", "ODF", "PDF", "DOC", "DOCX", "PPT"].indexOf(filetype.toUpperCase()) != -1) {
						var url = "http://docs.google.com/viewer?embedded=true&url=" + encodeURIComponent(me.cvdata);
						var cvdiv = $('<iframe></iframe>');
						cvdiv.attr('src', url);
						cvdiv.css('height', '800px');
						cvdiv.addClass('form-control');
					}
					else {
						var cvdiv = $('<p>Khong the xem duoc file online, click vao <a href="' + me.cvdata + '">day</a> de download</p>');
					}
				}
				me.cv.append(cvdiv);
				me.field.append(me.fielddata);
			}
		}

		private loadfield(cvid:string) {
			var me = this;
			$.get('HRWeb/cv/listfield', { id: cvid }, function (data) {
				if (cvid != me.cvid) return;

				me.w--;
				data = JSON.parse(data);
				me.fielddata = "";
				for (var i in data) {
					me.fielddata += '<p><b>' + data[i].name + '</b></p>' + '<p>' + data[i].data + '</p><p></p>';

				}
				me.doJob();
			});
		}

		private loadcv(cvid:string) {
			var me = this;
			$.get('HRWeb/cv/quickview', { id: cvid }, function (data) {
				if (me.cvid != cvid) return;

				me.w--;
				me.cvdata = data;
				me.doJob();
			});
		}

		show(cvid:string) {
			this.w = 2;
			this.cvid = cvid;
			this.wait.removeClass('hidden');
			this.cv.empty();
			this.field.empty();
			this.loadfield(cvid);
			this.loadcv(cvid);

			this.modal['modal']('show');
		}
	}
}  