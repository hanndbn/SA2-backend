define(['text!trang/thai/thai.html'], function (layout) {
	return function ()
	{
		var dom = this.dom = $(layout);
		this.wait = function()
		{
			dom.find('.loading').removeClass('hidden');
		};

		this.ready = function()
		{
			dom.find('.loading').addClass('hidden');
		};
	}

});