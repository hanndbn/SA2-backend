///<reference path="ref.ts" />


module ActionMenu
{
	export class Plugin
	{
		onReady = new plus.Emitter();

		menulist: JQuery;
		dom: JQuery;

		init()
		{
			var thethis = this;

			var thethis = this;
			$.get('actionmenu.html', function (data)
			{

				thethis.dom = $(data);
				thethis.menulist = thethis.dom.find('.dropdown-menu');
				thethis.onReady.emit({});

			})['fail'](function (error)
				{
					console.error(error)
					});
		}

		addItem2(title: string, cl: string, href: string)
		{
			
			var item = $('<li role="presentation"><a role="menuitem" tabindex = "-1" href="'+ href +'" target="_blank"><span class="' + cl + '"> </span>  ' + title + '</a></li>');
			this.menulist.append(item);
			return item;
		}

		addItem(title: string, cl: string, action: any,context?:any)
		{
			if (context !== undefined)
				action = action.bind(context);
			var item = $('<li role = "presentation" ><a role="menuitem" tabindex = "-1"><span class="' + cl + '"> </span>  ' + title + '</a></li>').on('click', action);
			this.menulist.append(item);
			return item;
		}

	}
}  