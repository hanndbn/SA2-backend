define(['text!trang/navbar/navbar.html', 'trang/long/long'], function (layout, Long) {
	return  function()
	{
		var long = new Long();
		var trang = this;
		var dom = this.dom = $(layout);
		this.dom.append(long.dom);

		this.addLeft = function(plugin)
		{
			trang.dom.find('.id_leftnav').append(plugin.dom);
		}

		this.showMenuButton= function()
		{
			dom.find('.id_btnmenu').fadeIn();
			dom.find('.id_jobselect').fadeOut();

		}

		dom.find('.id_btnmenu').click(function () {
			$(trang).trigger('menu');
		});

		this.hideMenuButton = function () {
			
			dom.find('.id_btnmenu').fadeOut();
			dom.find('.id_jobselect').fadeOut();

		}
		
		this.addRight = function(plugin)
		{
			trang.dom.find('.id_rightnav').append(plugin.dom);
		}

		this.clear = function()
		{

		}
	}

});