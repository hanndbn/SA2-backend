define(['text!trang/messagelist/messagelist.html'], function (layout) {
	return function () {
		this.dom = $(layout);

		this.addItem = function()
		{

		}

		this.addMilestone = function()
		{

		}

		

		this.wait = function () {
			dom.find('.loading').removeClass('hidden');
		}

		this.ready = function () {
			dom.find('.loading').addClass('hidden');
		}

		this.load = function (state) {

		}

		this.save = function () {

		}

		this.refresh = function () {


		}
	}

});