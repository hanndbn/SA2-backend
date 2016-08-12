///<reference path="jquery.d.ts" />

declare var require;

module plus
{
	export function get(url:string, data:any, success:any, fail:any, progress:any)
	{
		return ajax(url, 'GET', data, success, fail, progress, undefined);
	}

	export function upload(url:string, data:any, success:any, fail:any, upload:any)
	{
		return ajax(url, 'POST', data, success, fail, undefined, upload);
	}

	export function post(url:string, data:any, success:any, fail:any, progress:any)
	{
		return ajax(url, 'POST', data, success, fail, progress, undefined);
	}

	export function ajax(url:string, type:string, data:any, success:any, fail:any, download:any, upload:any)
	{
		if (upload == undefined || upload == null)
			upload = function ()
			{
			}

		if (download == undefined || download == null)
			download = function ()
			{
			}

		if (fail == undefined || fail == null)
			fail = function ()
			{
			}

		if (success == undefined || success == null)
			success = function ()
			{
			}


		return $['ajax']({
			url: url,
			type: type,
			data: data,
			async: true,
			xhr: function ()
			{
				// get the native XmlHttpRequest object
				var xhr = $['ajaxSettings'].xhr();

				//UPLOAD PROGRESS
				xhr.upload.addEventListener("progress", function (evt)
				{
					upload(evt.loaded / evt.total * 100);
				}, false);

				//DOWNLOAD PROGRESS
				xhr.addEventListener("progress", function (evt)
				{
					download(evt.loaded / evt.total * 100);
				}, false);

				return xhr;
			},
			success: function (data)
			{
				success(data);
			},
			cache: false,
			contentType: false,
			processData: false
		})['fail'](function (e)
		{
			fail(e);
		});
	}

	export function req(urls:string[], callback:any)
	{
		require(urls, function (util)
		{
			callback();
		});
	}

	export function bar(evs:Emitter[], callback:any, timeout:any = 10000)
	{
		// if callback hadn't been called after a specificed of time
		// then it could be error -> have to warn programmer
		var called = false;
		if (timeout == undefined || timeout == null)
			timeout = 10000;

		setTimeout(function ()
		{
			if (called == false)
				console.warn("Wait too long", evs, callback);
		}, timeout);

		var em = []; //list of state of emitter( true = ready, false = unready)
		for (var i in evs)
		{
			em[i] = false;
			(function ()
			{
				var e = i;
				evs[i].once(function ()
				{
					em[e] = true;
					for (var j in em)
						if (em[j] == false) return;
					called = true;
					callback();
				});
			})();
		}
	}

	export class DelayedEmitter
	{
		private i:number = 0;
		private isopen = false;
		private handlers = {};

		private calls = [];

		public open()
		{
			this.isopen = true;
			var calls = this.calls;
			for (var i in calls)
			{
				this.emit(calls[i].param, calls[i].callback, calls[i].context);
			}
		}

		public once(func:any, context?:any):number
		{
			var id = this.on(func, context);
			this.handlers[id]['-.-.-once-.-.-'] = true;
			return id;
		}

		public on(func:any, context?:any):number
		{
			this.i++;
			if (context === undefined)
				context = null;

			//JAT:
			if (this.handlers[this.i] !== undefined)
				throw "should not run this";
			this.handlers[this.i] = func.bind(context);
			return this.i;
		}

		public de(handle:number):void
		{
			delete this.handlers[handle];
		}

		public clear():void
		{
			delete this.handlers;
			this.handlers = [];
		}

		public emit(param?:any, callback?:any, context?:any):void
		{
			//delay call
			if (this.isopen == false)
			{
				this.calls.push({ param: param, callback: callback, context: context });
				return;
			}

			for (var i in this.handlers)
			{
				var stop = this.handlers[i](param);
				if (this.handlers[i]['-.-.-once-.-.-'] === true)
					this.de(i);
				if (stop == true) break;
			}

			if (callback !== undefined)
			{
				if (context !== undefined)
					callback.bind(context)(param);
				else
					callback(param);
			}
		}
	}

	export class Button
	{
		layout = "button.html";

	}

	export class Plugin
	{
		public layouturl:string;
		public required:string[];
		public onLayoutLoaded = new Emitter();
		public onDependLoaded = new Emitter();

		public dom:JQuery = $('<div>');

		// 0 : normal
		// -1 : unload
		// 1 : waitting
		// 2 : error
		private state : number;

		public getState() {}

		Plugin()
		{
			var self = this;
			bar([this.onLayoutLoaded, this.onDependLoaded], function ()
			{
				self.display();
			});

			plus.get(this.layouturl, {}, function ()
			{
				self.onLayoutLoaded.emit();
			}, function ()
			{
				self.displayError();
				self.onLayoutLoaded.emit();
			}, function (p)
			{
				self.displayWait(p);
			});

			req(self.required, function ()
			{
				self.onDependLoaded.emit();
			});
		}

		public displayWait(progress:number = 0)
		{
		}

		public displayError()
		{
		}

		public displayReady()
		{
		}

		public display()
		{

		}
	}

	export class Emitter
	{
		private i:number = 0;
		private handlers = {};

		public once(func:any, context?:any):number
		{
			var id = this.on(func, context);
			this.handlers[id]['-.-.-once-.-.-'] = true;
			return id;
		}

		public on(func:any, context?:any):number
		{
			this.i++;
			if (context === undefined)
				context = null;

			//JAT:
			if (this.handlers[this.i] !== undefined)
				throw "should not run this";
			this.handlers[this.i] = func.bind(context);
			return this.i;
		}

		public de(handle:number):void
		{
			delete this.handlers[handle];
		}

		public clear():void
		{
			delete this.handlers;
			this.handlers = [];
		}

		public emit(param?:any, callback?:any, context?:any):void
		{
			for (var i in this.handlers)
			{
				var stop = this.handlers[i](param);
				if (this.handlers[i]['-.-.-once-.-.-'] === true)
					this.de(i);
				if (stop == true) break;
			}

			if (callback !== undefined)
			{
				if (context !== undefined)
					callback.bind(context)(param);
				else
					callback(param);
			}
		}
	}

}
