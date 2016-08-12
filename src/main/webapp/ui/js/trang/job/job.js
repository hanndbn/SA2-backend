define(['text!trang/job/job.html', 'trang/mgr/mgr'], function (layout, Mgr) {
	return function ()
	{
		var trang = this;
		layout = $(layout);
		var defitem = { id: 0, content: "", choice: "", ctime: null };
		var tem = layout.find('.id_tem');
		var mgr = new Mgr(tem, layout.find('.id_detail'), defitem, "/apply/job/create", "/apply/job/list", "/apply/engquestion/edit", toObject, toForm, toTemplate);
		var dom = this.dom = mgr.dom;

		function toObject(det) {
		}

		//chuyển từ object sang form bên phải
		function toForm(obj, det) {

		}

		//chuyển từ object sang form bên trái
		function toTemplate(obj, tem) {
			tem.find('.id_title').html(obj.title);
			tem.find('.id_id').html(obj.id);
			tem.find('.id_ctime').html(obj.ctime.dayOfMonth + "-" + (obj.ctime.month + 1) + "-" + obj.ctime.year);
			tem.find('id_re').click(function(){
				tem.fadeOut();
				$.get('/apply/job/edit', { id: obj.id, state: 0 });
			});
		}

		trang.show = function () {
			mgr.show();
			mgr.search();
		}
	}
});