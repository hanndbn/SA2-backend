///<reference path="ref.ts" />
module TextBox
{
	export class Plugin
	{
		onChanged = new plus.Emitter();
		onReady = new plus.Emitter();
		dom: JQuery;

		private stars: JQuery[] = [];
		 input: JQuery;
		private wait: JQuery;
		text: string;
		changed: boolean = false;
		oldtext : string;
		init()
		{
			var thethis = this;

			$.post('textbox.html', function (data)
			{
				thethis.dom = $(data);
				thethis.bound();
				thethis.onReady.emit({});
			})['fail'](function (error)
				{
					console.error(error);
					thethis.dom.css('background-color', '#ff0000');
				});
		}

		bound()
		{
			var thethis = this;

			thethis.input = this.dom.find('.id_inputex');
			thethis.wait = this.dom.find('.id_wait');

			thethis.input.dblclick( function ()
			{
				thethis.input.removeAttr('readonly');
			});

			thethis.input.click(function (e:Event)
			{
				if(thethis.input.attr('readonly') == undefined) e.stopPropagation();
			});
			thethis.input.on('keyup', function (e)
			{
				if(thethis.input.val() != thethis.oldtext) thethis.changed = true;
				if (e.keyCode == 13)
				{
					thethis.text = thethis.input.val();
					thethis.input.prop('readonly', true);
					thethis.onChanged.emit();
					thethis.displayLoading();
					thethis.changed = false;
				}
			});

			thethis.input.focusout(function ()
			{	
				thethis.input.prop('readonly', true);
				if (thethis.changed)
				{
					thethis.input.addClass('unsaved');		
				}
			});
		}

		displayLoading()
		{
			this.input.addClass('hidden');
			this.wait.removeClass('hidden');
		}

		displayText(text? :string)
		{
			if(text == undefined) text = this.text;
			this.input.removeClass('unsaved');
			this.text = text;
			this.oldtext = text;
			this.wait.addClass('hidden');
			this.input.removeClass('hidden');
			this.input.val(text);
		}
	}
}  