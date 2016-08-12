define(['trang/long/long'], function (Long, Boot_wysihtml5)
{
	return function ()
	{
		var trang = this;
		trang.Note = undefined;
		var id = "id_tedit" + new Date().getTime();
		//var dom = this.dom = $('<div><textarea id="' + id + '" placeholder="Enter text ..."></textarea></div>');

		this.make = function (ele, h)
		{
			if (h !== undefined)
				ele.summernote({height: h});
			else
				ele.summernote({height: 300});
		};
	}
});