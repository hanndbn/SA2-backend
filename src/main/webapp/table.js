///<reference path="ref.ts" />
var Table;
(function (Table) {
    var Plugin = (function () {
        function Plugin() {
            this.onReady = new plus.Emitter();
            this.rows = [];
            this.stars = [];
        }
        Plugin.prototype.init = function () {
            var thethis = this;
            $.post('table.html', function (data) {
                thethis.dom = $(data);
                thethis.table = thethis.dom.find('.id_table');

                thethis.onReady.emit({});
            })['fail'](function (error) {
                console.error(error);
                thethis.dom.css('background-color', '#ff0000');
            });
        };

        Plugin.prototype.disable = function () {
            this.dom.css('opacity', 0.2);
        };

        Plugin.prototype.enable = function () {
            this.dom.css('opacity', 1);
        };

        Plugin.prototype.clear = function () {
            this.rows = [];
            this.count = 0;
            this.table.empty();
            this.table.append(this.tablehead);
        };

        Plugin.prototype.setUpTable = function (header) {
            this.count = 0;
            this.table.empty();
            var htmlheader = '<thead><tr>';
            for (var i in header) {
                htmlheader += '<th>' + header[i] + '</th>';
            }
            htmlheader += '</tr></thead>';
            this.tablehead = $(htmlheader);
            this.table.append(this.tablehead);
        };

        Plugin.prototype.getRow = function (no) {
            for (var i in this.rows) {
                if (this.rows[i]['__no'] == no) {
                    return this.rows[i];
                }
            }
            return undefined;
        };

        Plugin.prototype.deleteRow = function (no) {
            for (var i in this.rows) {
                if (this.rows[i]['__no'] == no) {
                    this.count--;
                    this.rows[i].remove();
                    this.rows.splice(i, 1);
                }
            }
        };

        Plugin.prototype.editRow = function (no, rowdata) {
            var htmlrow = this.getRow(no);

            //delete all children
            htmlrow.empty();

            for (var i in rowdata) {
                htmlrow.append($('<td></td>').append(rowdata[i]));
            }

            return htmlrow;
        };

        Plugin.prototype.addRow = function (no, rowdata) {
            var htmlrow = $('<tr class="tablerow"></tr>');
            for (var i in rowdata) {
                htmlrow.append($('<td></td>').append(rowdata[i]));
            }
            this.table.append(htmlrow);
            setTimeout(function () {
                htmlrow.addClass('loaded');
            }, 100);
            htmlrow['__no'] = no;
            this.rows.push(htmlrow);
            this.count++;
            return htmlrow;
        };

        Plugin.prototype.pushRow = function (no, rowdata) {
            var htmlrow = $('<tr class="tablerow"></tr>');
            for (var i in rowdata) {
                htmlrow.append($('<td></td>').append(rowdata[i]));
            }
            this.table.prepend(htmlrow);
            setTimeout(function () {
                htmlrow.addClass('loaded');
            }, 100);
            htmlrow['__no'] = no;
            this.rows.push(htmlrow);
            this.count++;
            return htmlrow;
        };
        return Plugin;
    })();
    Table.Plugin = Plugin;
})(Table || (Table = {}));
