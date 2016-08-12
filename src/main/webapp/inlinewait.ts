///<reference path="jquery.d.ts" />

module Wait
{
	export class InlinePlugin
	{
		private element: JQuery;

		init(element: HTMLElement)
		{
			this.element = $(element);
		}

		show()
		{
			this.element.removeClass('hidden');
		}

		hide()
		{
			this.element.addClass('hidden');
		}

	}
}