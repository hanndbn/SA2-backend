define(['text!trang/template/template.html', 'trang/mgr/mgr', 'trang/editor/editor'], function (layout, Mgr, Editor)
{
	return function ()
	{
		//on: field, org, tem, gen
		var trang = this;
		var editor = new Editor();
		layout = $(layout);
		var defitem = { id: 0, content: "", name: "", ctime: null };
		var tem = layout.find('.id_tem');
		var mgr = new Mgr(tem, layout.find('.id_detail'), defitem, "/apply/template/create", "/apply/template/list", "/apply/template/edit", toObject, toForm, toTemplate);
		var dom = this.dom = mgr.dom;

		mgr.setTitle('Mail template');
		tem.click(function ()
		{
			list.select
		});

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

		function toObject(det)
		{
			var obj = {};
			obj.name = det.find('.id_name').val();
			obj.id = det.find('.id_id').html();
			obj.subject = det.find('.id_subject').val();
			obj.body = det.find('.id_body').code();
			obj.sign = det.find('.id_sign').code();

			return obj;
		}

		function toForm(obj, det)
		{
			det.find('.id_name').val(obj.name);
			det.find('.id_id').html(obj.id);
			editor.make(det.find('.id_body'));
			editor.make(det.find('.id_sign'));

			det.find('.id_subject').val(obj.subject);
			det.find('.id_body').code(obj.body);
			det.find('.id_sign').code(obj.sign);


		}

		function toTemplate(obj, tem)
		{
			tem.find('.id_id').html(obj.id);
			tem.find('.id_name').html(obj.name);
			tem.find('.id_ctime').html(obj.ctime.dayOfMonth + "-" + (obj.ctime.month + 1) + "-" + obj.ctime.year);

		}

		trang.dom.find('.id_org').click(function ()
		{
			$(trang).trigger('org');
		});


		trang.dom.find('.id_tem').click(function ()
		{
			$(trang).trigger('tem');
		});


		trang.dom.find('.id_gen').click(function ()
		{
			$(trang).trigger('gen');
		});

		trang.show = function ()
		{
			mgr.show();
			mgr.search();
		}
	}
});