define(['text!trang/question/question.html', 'trang/mgr/mgr'], function (layout, Mgr) {
	return function ()
	{
		var trang = this;
		layout = $(layout);
		var defitem = { id: 0, content: "", choice: "", ctime: null };
		var tem = layout.find('.id_tem');
		var mgr = new Mgr(tem, layout.find('.id_detail'), defitem, "/apply/engquestion/create", "/apply/engquestion/list", "/apply/engquestion/edit", toObject, toForm, toTemplate);
		var dom = this.dom = mgr.dom;

		/*
		fieldMgr.ctor(dom.find('.id_fieldPreview'), dom.find('.id_fieldDetails'),
			'/apply/field/create',
			'/apply/field/update',
			'/apply/field/delete',
			'/apply/field/list',
			'/apply/field/get');
		*/

		trang.dom.find('.id_field').click(function ()
		{
			$(trang).trigger('field');
			fieldMgr.show();

		});

		function toObject(det) {
			var obj = {};
			obj.name = det.find('.id_name').val();
			obj.id = det.find('.id_id').html();
			obj.weight = det.find('.id_weight').val();
			return obj;
		}

		//chuyển từ object sang form bên phải
		function toForm(obj, det) {
			det.find('.id_id').html(obj.id);
			det.find('.id_content').val(obj.title);
			det.find('.id_choose').html(obj.choose.replace('\\n', '\n'));
			det.find('.id_weight').val(obj.weight);
		}

		//chuyển từ object sang form bên trái
		function toTemplate(obj, tem) {
			tem.find('.id_content').html(obj.title);
			tem.find('.id_id').html(obj.id);
			tem.find('.id_ctime').html(obj.ctime.dayOfMonth + "-" + (obj.ctime.month + 1) + "-" + obj.ctime.year);

		}

		trang.dom.find('.id_org').click(function () {
			$(trang).trigger('org');
		});


		trang.dom.find('.id_tem').click(function () {
			$(trang).trigger('tem');
		});


		trang.dom.find('.id_gen').click(function () {
			$(trang).trigger('gen');
		});

		trang.show = function () {
			mgr.show();
			mgr.search();
		}
	}
});