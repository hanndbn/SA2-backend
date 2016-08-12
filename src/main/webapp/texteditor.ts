///<reference path="ref.ts" />

module TextEditor
{
	export class Plugin
	{
		onReady = new plus.Emitter();

		dom: JQuery;
		private editor;

		getCode(): string
		{
			return this.editor['code']();
		}

		setCode(code: string)
		{
			this.editor['code'](code);
		}

		init()
		{
			var thethis = this;
			plus.req(['summernote.min.js'], function ()
			{
				thethis.dom = $('<div></div>');

				thethis.editor = $('<div></div>');
				thethis.dom.append(thethis.editor);
				thethis.editor['summernote']({
					height: 300,   //set editable area's height
					focus: true,    //set focus editable area after Initialize summernote
						codemirror: { // codemirror options
						//	theme: 'monokai'
						}
				});
				
				thethis.onReady.emit();
			});
		}


	}
}  