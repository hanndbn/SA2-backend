define(['text!trang/mtest/mtest.html', 'trang/ticketlist/ticketlist', 'trang/messagelist/messagelist', 'trang/long/long'],
	function (layout, Ticketlist, Messagelist, Long) {
		return function () {
			var long = new Long();
			var ticketlist = new Ticketlist();
				var messagelist = new Messagelist();

			var dom = this.dom = $(layout);
			dom.append(long.dom);
			
				dom.find('.ticketlist-anchor').after(ticketlist.dom);
				dom.find('.messagelist-anchor').after(messagelist.dom);

				
			this.wait = function () {
				dom.find('.loading').removeClass('hidden');
			}

			this.ready = function () {
				dom.find('.loading').addClass('hidden');
			}

			this.load = function (state) {

			}

			this.save = function () {

			}

			this.refresh = function (config) {


			}
		}

	});