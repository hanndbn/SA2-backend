///<reference path="ref.ts" />

module Table
{
	export class Plugin
	{
		onReady = new plus.Emitter();

		dom: JQuery;
		table : JQuery;
		rows : any[] =[];
		private stars: JQuery[] = [];
		private starlist: JQuery;
		count: number;
		tablehead: JQuery;

		init()
		{
			var thethis = this;
			$.post('table.html', function (data)
			{
				thethis.dom = $(data);
				thethis.table = thethis.dom.find('.id_table');
				
				thethis.onReady.emit({});
			})['fail'](function (error)
				{
					console.error(error);
					thethis.dom.css('background-color', '#ff0000');
				});
		}

		private disable()
		{
			this.dom.css('opacity', 0.2);
		}

		private enable()
		{
			this.dom.css('opacity', 1);
		}

		clear()
		{
			this.rows=[];
			this.count = 0;
			this.table.empty();
			this.table.append(this.tablehead);
		}

		setUpTable(header : string[])
		{
			this.count = 0;
			this.table.empty();
			var htmlheader= '<thead><tr>';
			for (var i in header)
			{
				htmlheader += '<th>' + header[i] + '</th>';
			}
			 htmlheader += '</tr></thead>';
			this.tablehead = $(htmlheader);
			this.table.append(this.tablehead);
		}

		getRow(no: number) : JQuery
		{
			for (var i in this.rows)
			{
				if (this.rows[i]['__no'] == no)
				{
					return  this.rows[i];
				}
			}
			return undefined;
		}

		deleteRow(no: number)
		{
			for (var i in this.rows)
			{
				if (this.rows[i]['__no'] == no)
				{
					this.count--;
					this.rows[i].remove();
					this.rows.splice(i,1);
				}
			}

		}

		editRow(no: number, rowdata) : JQuery
		{
			var htmlrow = this.getRow(no);

			//delete all children
			htmlrow.empty();
			//add new
			for (var i in rowdata)
			{
				htmlrow.append($('<td></td>').append(rowdata[i]));
			}

			return htmlrow;
		}

		addRow(no: number, rowdata) : JQuery
		{
			var htmlrow = $('<tr class="tablerow"></tr>');
			for (var i in rowdata)
			{
				htmlrow.append($('<td></td>').append(rowdata[i]));
			}
			this.table.append(htmlrow);
			setTimeout(function () { htmlrow.addClass('loaded') }, 100);
			htmlrow['__no'] = no;
			this.rows.push(htmlrow);
			this.count++;
			return htmlrow;
		}

		pushRow(no: number, rowdata): JQuery
		{
			var htmlrow = $('<tr class="tablerow"></tr>');
			for (var i in rowdata)
			{
				htmlrow.append($('<td></td>').append(rowdata[i]));
			}
			this.table.prepend(htmlrow);
			setTimeout(function () { htmlrow.addClass('loaded') }, 100);
			htmlrow['__no'] = no;
			this.rows.push(htmlrow);
			this.count++;
			return htmlrow;
		}
	}
}  