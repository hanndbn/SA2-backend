define(['text!trang/long/long.html'], function (layout) {
	return function ()
	{
		var dom = this.dom = $(layout);
		this.wait = function()
		{
			dom.find('.loading').removeClass('hidden');
		}

		this.ready = function()
		{
			dom.find('.loading').addClass('hidden');
		}

		this.load = function(state)
		{

		}

		this.save  = function()
		{

		}

		this.refresh = function()
		{
		

		}
	}

});