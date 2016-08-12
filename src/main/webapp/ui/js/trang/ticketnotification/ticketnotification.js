define(['text!trang/ticketnotification/ticketnotification.html', 'trang/long/long'], function (layout, Long) {
	return function () {
		var long = new Long();
		this.dom = $(layout);
		this.dom.append(long.dom);

		this.load = function (state) {


		}

		this.save = function () {

		}

		this.refresh = function () {
			long.wait();

			var cookie = JSON.parse(document.cookie);
			cookie.user;



			long.unwait();
		}
	}
});