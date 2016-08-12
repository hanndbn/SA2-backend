requirejs.config({
	//By default load any module IDs from js/lib
	baseUrl: '/ui',
	//except, if the module ID starts with "app",
	//load it from the js/app directory. paths
	//config is relative to the baseUrl, and
	//never includes a ".js" extension since
	//the paths config could be for a directory.
	paths: {
		trang: 'js/trang',
		text: 'js/text',
		jquery: 'js/jquery-2.0.3.min'
	}
});

$.get('/apply/user/isloggedin', function (data) {
	data = JSON.parse(data);
	if (data.result == false) {
		$('body').append("<code>ERR 325: access denied</code><h1><a href='/login'>Click vào đây để đăng nhập</a></h1>");
	}
	else {
		$.get('/apply/user/get', function (data) {
			$.user = JSON.parse(data);

			// Start the main app logic.
			require(['trang/router', 'text!trang/script.html'/*'/bl/user/get'*/], function (Router, sc, User) {
				router = new Router();
				$(document).ready(function () {
					$('.ngoc').after(router.dom);
					$('.id_script').append(sc);
					router.route(document.location.hash);
				});
				return {};
			});
		});
	}
});

$.___progresslist = {};
$.___progressontherun = false;
$.updateLoadProgress = function (name, progress) {
	$.___progressontherun = true;
	var container = $('.id_progress-container');
	container.removeClass('hidden');
	var bar = $('.id_progress');

	$.___progresslist[name] = progress;
	var p = 0, s = 0;
	for (var i in $.___progresslist) {
		p += $.___progresslist[i];
		s += 100;
	}
	var percent = 100 * p / s;

	if (percent <= bar.css('width').replace("%", ""))
		return;
	container.removeClass("")
	bar.css('width', percent + "%");

	//clear things out
	if (p == s) {
		$.___progressontherun = false;
		setTimeout(function () {
			if ($.___progressontherun == false) {
				container.addClass('hidden');
				bar.css('width', 0 + "%");
			}
		}, 1000);
		$.___progresslist = {};
	}
};

//như hàm require nhưng sẽ trả về một đối tượng mới thay vì trả về module
// ví dụ nếu require trả về module Long thì include trả về: new Long()
function reject_include(depends, callback) {
	var called = false;
	var name = ["df", "df"].toString() + new Date() + Math.random();
	$.updateLoadProgress(name, 10);

	define(depends, function (a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22, a23, a24, a25, a26, a27, a28, a29, a30, a31, a32, a33, a34, a35, a36, a37, a38, a39, a40, a41, a42, a43, a44, a45, a46, a47, a48, a49, a50, a51, a52, a53, a54, a55, a56, a57, a58, a59, a60, a61, a62, a63, a64, a65, a66, a67, a68, a69, a70, a71, a72, a73, a74, a75, a76, a77, a78, a79, a80, a81, a82, a83, a84, a85, a86, a87, a88, a89) {
		var param = "";
		var j = 0;
		for (var i in depends) {

			//if (depends[i].indexOf("!") == -1){
			//	console.log(eval("a" + j));
			//	eval("a" + j + " = new a" + j + "();");
			//}
			param += ",a" + j;
			j++;

		}
		param = param.substr(1);
		$.updateLoadProgress(name, 100);
		return eval("callback(" + param + ")");
	});
}

function requireEx(depends, callback) {
	var name = depends.toString() + new Date() + Math.random();
	$.updateLoadProgress(name, 10);
	require(depends, function (a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22, a23, a24, a25, a26, a27, a28, a29, a30, a31, a32, a33, a34, a35, a36, a37, a38, a39, a40, a41, a42, a43, a44, a45, a46, a47, a48, a49, a50, a51, a52, a53, a54, a55, a56, a57, a58, a59, a60, a61, a62, a63, a64, a65, a66, a67, a68, a69, a70, a71, a72, a73, a74, a75, a76, a77, a78, a79, a80, a81, a82, a83, a84, a85, a86, a87, a88, a89) {
		var param = "";
		var j = 0;
		for (var i in depends) {

			//không tạo luôn đối tượng
			//if (depends[i].indexOf("!") == -1) {
			//	eval("a" + j + " = new a" + j + "();");
			//}
			param += ",a" + j;
			j++;

		}
		param = param.substr(1);
		$.updateLoadProgress(name, 100);
		return eval("callback(" + param + ")");
	});

}

$.plugIn = plugIn = function (de, context, callback) {
	if (de.length >= 89)
		throw 'need to increase parameter size';
	//build dependencies array, variablen array
	var prop = [];
	var depends = [];

	for (var i in de) {
		if (de[i][1] === undefined) {
			de[i][1] = de[i][0];
		}
		if (context[de[i][1]] == undefined) {
			depends.push(de[i][0]);
			prop.push(de[i][1]);
		}
	}

	requireEx(depends, function (a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22, a23, a24, a25, a26, a27, a28, a29, a30, a31, a32, a33, a34, a35, a36, a37, a38, a39, a40, a41, a42, a43, a44, a45, a46, a47, a48, a49, a50, a51, a52, a53, a54, a55, a56, a57, a58, a59, a60, a61, a62, a63, a64, a65, a66, a67, a68, a69, a70, a71, a72, a73, a74, a75, a76, a77, a78, a79, a80, a81, a82, a83, a84, a85, a86, a87, a88, a89) {
		for (var i in depends) {
			if (context[prop[i]] == undefined) {
				context[prop[i]] = eval("a" + i);
			}
		}
		callback();
	});
};

$.setHash = function (hash) {
	$.___raisehash = true;
	window.location.hash = hash;
};

var router;

$(window).on('hashchange', function () {
	if ($.___raisehash == true)
		$.___raisehash = false;
	else router.route(window.location.hash);
});

