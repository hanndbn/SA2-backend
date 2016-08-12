define(['text!trang/undonetask/undonetask.html', 'trang/long/long'], function (layout, Long) {
	return function () {
		var _i = 0;
		var _count = 0;
		var long = new Long();
		this.dom = $(layout);
		this.dom.append(long.dom);

		var tem = $('<li><a><div class="clearfix"><span class="pull-left id_message">???</span><span class="pull-right">99%</span></div><div class="progress progress-mini  progress-striped active"><div style="width:35%" class="progress-bar progress-bar-danger"></div></div></a></li>')

		function updateIcon() {
			dom.find('.id_n').html(_count);
			if (_count > 0) {
				dom.find('.id_icon').removeClass('light-grey');
				dom.find('.id_icon').addClass('red');
				dom.find('.id_n').removeClass('badge-light');
				dom.find('.id_n').addClass('badge-danger');
			}
			else {
				dom.find('.id_icon').removeClass('red');
				dom.find('.id_icon').addClass('light-grey');
				dom.find('.id_n').removeClass('badge-danger');
				dom.find('.id_n').addClass('badge-light');
			}
		}

		//task = {name; progress; color}
		this.addTask = function (task) {
			_i++;
			tem.addClass(".id_" + _i);
			dom.find('.id_list').append(tem);
			tem.find('.id_message').html(task);
			_count++;
			updateIcon();
			return _i++;
		}

		//xóa task
		this.removeTask = function (id) {
			dom.find(".id_" + id).fadeOut();
			_count--;

		}

		this.refresh = function () {
			long.wait();

			var cookie = JSON.parse(document.cookie);
			cookie.user;



			long.unwait();
		}
	}
});