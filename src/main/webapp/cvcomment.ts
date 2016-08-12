///<reference path="ref.ts" />

module CVComment
{
	export class Plugin
	{
		onReady = new plus.Emitter();
		menulist: JQuery;
		dom: JQuery;
		div: JQuery;

		init(cvid: number)
		{
			var thethis = this;
			var dataloaded = new plus.Emitter();
			var layoutloaded = new plus.Emitter();

			plus.bar([layoutloaded, dataloaded], function ()
			{
				var newcomment: JQuery = thethis.dom.find('.id_newcomment');
				newcomment.on('click', function (e)
				{
					e.stopPropagation();
					e.preventDefault();
				});
				newcomment.on('keyup', function (e)
				{
					if (e.keyCode == 13)
					{
						$.post('HRWeb/cv/comment', { id: cvid, comment: newcomment.val() }, function ()
						{
						})['fail'](function ()
							{
								alert('can not save comment');
							});
						thethis.addItem(newcomment.val(), "me"); newcomment.val("");
					}
				});

				for (var i in comments)
				{
					thethis.addItem(comments[i].comment, comments[i].author);
				}
			});

			$.get('cvcomment.html', function (data)
			{
				thethis.dom = $(data);
				thethis.div = thethis.dom.find('.id_div');
				thethis.menulist = thethis.dom.find('.dropdown-menu');
				thethis.onReady.emit();
				layoutloaded.emit();
			})['fail'](function (error)
				{
					console.error(error)
					});

			var comments;
			$.get('HRWeb/cv/listcomment', { id: cvid }, function (data)
			{
				comments = JSON.parse(data);
				dataloaded.emit();
			});
		}

		addItem(content: string, author: string)
		{
			var item = $('<li role = "presentation" ><span role="menuitem" tabindex="-1">-' + author + ': ' + content + '</span></li>');
			item.on('click', function (e)
			{
				e.stopPropagation();
				e.preventDefault();
			});
			this.div.before(item);
		}
	}
}  