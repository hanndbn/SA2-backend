define(['text!trang/taglist/taglist.html', 'trang/long/long', 'trang/taglist/tokenfield.min', 'trang/taglist/typeahead.bundle.min'], function (layout, Long, Selectize) {
	return function (color) {


		var long = new Long();
		var trang = this;
		var dom = this.dom = $(layout);

		var field = this.field = dom.find('.id_tokenfield');

		this.dom.append(long.dom);

		this.load = function (state) {
		};

		this.save = function () {
		};

		var typeaheadsource;
		field.tokenfield();
		field.parent().css('border-left', '4px solid ' + color);
		this.setTypeahead = function (helptext) {
			var local = [];
			for (var i in helptext) {
				local.push({ value: helptext[i] });
			}

			var engine = new Bloodhound({
				local: local,
				datumTokenizer: function (d) {
					return Bloodhound.tokenizers.whitespace(d.value);
				},
				queryTokenizer: Bloodhound.tokenizers.whitespace
			});

			engine.initialize();
			field.tokenfield({
				typeahead: [null, { source: engine.ttAdapter() }]
			});
		}

		this.addTag = function (tag) {
			field.tokenfield('createToken', tag.label);
		};


		this.getTags = function()
		{
			return field.tokenfield('getTokens',false);
		};


		field.on('tokenfield:createdtoken', function (e) {
			$(trang).trigger('tagcreated', e);
		}).on('tokenfield:editedtoken', function (e) {
			$(trang).trigger('tagedited', e);
		}).on('tokenfield:removetoken', function (e) {
			$(trang).trigger('tagdelete', e);
		});

		this.setTag = function (tags) {

			field.tokenfield('setTokens', tags);
		}

		this.refresh = function () {
			long.wait();

			var cookie = JSON.parse(document.cookie);
			cookie.user;

			long.ready();
		};
	};
});