define(['trang/long/long','text!trang/profileeditor/profileeditor.html', 'trang/taglist/taglist'], function (Long, layout, Taglist) {
	return function () {
		var taglist = new Taglist();
		var long = new Long();
		var dom = this.dom = $(layout);
		dom.append(long);


		dom.find('.id_taglist_ancho').after(taglist.dom);
		taglist.setTag([{value:"sdf", label:"red"}]);
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