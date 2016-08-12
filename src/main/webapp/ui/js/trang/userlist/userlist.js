define(['trang/list/list', 'text!trang/userlist/userlist.html'], function (List, layout) {
	return function () {
		var list = new List();
		var dom = this.dom = list.dom;


		var header = $(layout).find('.id_header');

		list.setHeader(header);

		for (var k = 0; k < 100; k++) list.add('h1',1,k);

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