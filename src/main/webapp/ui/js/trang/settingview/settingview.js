define(['text!trang/settingview/settingview.html', 'trang/long/long', 'trang/mgr/mgr', 'trang/template/template', 'trang/question/question'], function (layout, Long, Mgr, Template,Question)
{
	return function ()
	{
		//on: field, org, tem, gen
		var trang = this;
		var long = new Long();
		var dom = this.dom = $(layout);
		this.dom.append(long.dom);

		var question = new Question();
		var template = new Template();
		this.dom.append(template.dom);
		trang.dom.find('.id_template').click(function ()
		{
			$(trang).trigger('template');


			template.show();

		});

		trang.dom.find('.id_iq').click(function ()
		{
			$(trang).trigger('iq');
			question.show();
		});

		trang.dom.find('.id_eng').click(function ()
		{
			$(trang).trigger('eng');
			question.show();
		});


		trang.dom.find('.id_tem').click(function ()
		{
			$(trang).trigger('tem');
		});


		trang.dom.find('.id_gen').click(function ()
		{
			$(trang).trigger('gen');
		});

		//thểm task
		this.addTask = function ()
		{

		};

		//task = {name; progress; color}
		this.updateTask = function (task)
		{

		};

		//xóa task
		this.removeTask = function (id)
		{

		}

	}
});