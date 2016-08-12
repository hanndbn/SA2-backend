define(['text!trang/mjob/mjob.html'], function (layout) {
	return function () {
		this.dom = $(layout);
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

		this.refresh = function (config) {

		}
	}

});