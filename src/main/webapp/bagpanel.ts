///<reference path="ref.ts" />

module BagPanel
{
	export class Plugin
	{
		private containerDiv: JQuery;
		onAdd = new plus.Emitter();
		onDeleted = new plus.Emitter();
		onMoved = new plus.Emitter();
		onSelected = new plus.Emitter();
		onReady = new plus.Emitter();
		onEdit = new plus.Emitter();

		dom: JQuery;
		private thedefaultBag: JQuery;
		private theaddnewBag: JQuery;
		focused: JQuery;

		disable()
		{
			this.dom.css('opacity', 0.2);
		}

		enable()
		{
			this.dom.css('opacity', 1);
		}

		init()
		{
			var thethis = this;
			var layoutloaded = new plus.Emitter();
			var dependloaded = new plus.Emitter();

			plus.bar([layoutloaded, dependloaded], function ()
			{
				thethis.theaddnewBag = thethis.dom.find('.id_addnew');
				thethis.thedefaultBag = thethis.dom.find('.id_hiddenbag');

				thethis.containerDiv = thethis.dom.find('.id_bagcontainer');

				thethis.theaddnewBag.on('click', function ()
				{
					thethis.onAdd.emit();
				});

				thethis.onReady.emit({});
			});

			$.get('bagpanel.html', function (data)
			{
				thethis.dom = $(data);
				layoutloaded.emit();
			})['fail'](function (error)
				{
					console.error(error);
					thethis.dom.css('background-color', '#ff0000');
				});

			plus.req(['textbox.js'], function ()
			{
				dependloaded.emit();

			});
		}

		displayText(id: number)
		{
			for (var i in this.bags)
			{
				if (this.bags[i]['__attach'].id == id)
				{
					this.bags[i]['__nametb'].displayText(); 
				}
			}
		}

		clear()
		{
			for(var i in this.bags)
			{
				this.bags[i].hide();
			}
			this.bags = [];
		}

		getFocusedBag(): any
		{
			return this.dom.find('.focusedBag')['__attach'];
		}

		focus(no: number = 0)
		{
			var child = this.containerDiv.children().eq(no);
			child.trigger('click');
		}

		changeN(bag: JQuery, n: number)
		{
			bag.find('.id_n').html(n.toString());
		}

		private bags = [];

		addBag(name: string, text: string, id: string, attach: any): JQuery
		{
			var thethis = this;
			var me = this;
			var bag = this.thedefaultBag.clone();
			bag['__attach'] = attach;
			bag.removeClass('id_hiddenbag');
			var nametbDiv = bag.find('.id_name');

			var nametb = new TextBox.Plugin();
			nametb.onChanged.on(function ()
			{
				thethis.onEdit.emit({ dom: bag, attach: attach, name: nametb.text });
			});

			nametb.onReady.on(function ()
			{
				nametb.displayText(name);
				nametbDiv.append(nametb.dom);
			});
			bag['__nametb'] = nametb;
			nametb.init();


			me.theaddnewBag.before(bag);
			bag.find('.close').on('click', function (e : Event)
			{
					me.onDeleted.emit({ dom: bag, attach: attach });
			});

			bag.on('click', function (e: Event)
			{
				if (me.focused == bag) return;
				if (me.focused !== undefined)
				{
					me.focused.removeClass('focusedBag');
				}
				bag.addClass('focusedBag');
				me.focused = bag;
				me.onSelected.emit({ dom: bag, attach: attach });
				e.stopPropagation();
			});

			bag.on('dragover', function (e:Event)
			{
				e.preventDefault();
				e.stopPropagation();
				bag.addClass('draggingBag');
			});

			bag.on('dragleave', function (e:Event)
			{
				e.preventDefault();
				e.stopPropagation();
				bag.removeClass('draggingBag');
			});

			bag[0].addEventListener('drop', function (event)
			{
				event.preventDefault();
				event.stopPropagation();
				me.onMoved.emit({ dom: bag, attach: attach, data: event.dataTransfer.getData('cv') });
			});
			this.bags.push(bag);
			return bag;
		}
	}
}  