define(['text!trang/psidebar/psidebar.html', 'trang/long/long'], function (layout, Long) {
	return function () {
		var long = new Long();
		var trang = this;
		var dom = this.dom = $(layout);
		dom.find('.id_job').on('click', function () {
			$(trang).trigger('job');
		});

		dom.find('.id_candidate').on('click', function () {
			$(trang).trigger('candidate');
		});

		dom.find('.id_test').on('click', function () {
			$(trang).trigger('test');
		});

		dom.find('.id_user').on('click', function () {
			$(trang).trigger('user');
		});

		dom.find('.id_setting').on('click', function () {
			$(trang).trigger('setting');
		});

		this.dom.append(long.dom);

		this.load = function (state) {
		};

		this.save = function () {
		};

		this.refresh = function () {
			long.wait();

			var cookie = JSON.parse(document.cookie);
			cookie.user;

			long.ready();
		};
	};
});