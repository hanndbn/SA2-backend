///<reference path="ref.ts" />

module Test
{
	export class Plugin
	{
		onReady = new plus.Emitter();
		captcha: Captcha.Plugin;
		dom: JQuery;
		questions: JQuery[] = [];
		phase1: JQuery;
		phase2: JQuery;
		phase3: JQuery;
		markSpan: JQuery;
		timeDiv: JQuery;
		timeprogress: JQuery;
		timer: Timer.Plugin;
		private data: any;
		init(testcode: string)
		{
			var thethis = this;
			//tạo barrier để đồng bộ
			var layoutloaded = new plus.Emitter();
			var dependloaded = new plus.Emitter();

			plus.bar([dependloaded, layoutloaded], function ()
			{
				var capDiv: JQuery;
				var questionsDiv: JQuery;
				var phase1: JQuery;
				var phase2: JQuery;
				var testsubmitBt: JQuery;
				//Phase 1: get test
				plus.bar([thethis.captcha.onReady, thethis.timer.onReady], function ()
				{
					capDiv = thethis.dom.find('.id_captcha');
					capDiv.append(thethis.captcha.dom);
					thethis.timeprogress = thethis.dom.find('.id_timeprogress');
					thethis.phase1 = thethis.dom.find('.id_phase1');
					thethis.phase2 = thethis.dom.find('.id_phase2');
					thethis.phase3 = thethis.dom.find('.id_phase3');
					questionsDiv = thethis.dom.find('.id_questions');
					testsubmitBt = thethis.dom.find('.id_testsubmit');
					thethis.markSpan = thethis.dom.find('.id_mark');
					thethis.timeDiv = thethis.dom.find('.id_timer');
					thethis.timeDiv.append(thethis.timer.dom);
					thethis.onReady.emit();
				});
				var stoped = false;
				thethis.dom.find('.id_gettest').on('click', function ()
				{
                    var btn = $(this);
                    btn['button']('loading');
					var cap = thethis.captcha.getAnswer();
					$.get('HRWeb/test/start', { code: testcode, capid: cap.id, cap: cap.answer }, function (data)
					{
                        btn['button']('reset');
						//---------------------------------------PHASE 2----------------------------//
						thethis.phase1.addClass('hidden');
						thethis.phase2.removeClass('hidden');
						data = JSON.parse(data);

						$(window).on('beforeunload', function ()
						{
							return "Nếu bạn đã làm xong, nhấn Cancel, sau đó nhấn nút Gửi ở cuối trang\nNếu thực sự muốn bỏ bài thi, gõ \"quit\", sau đó nhấn OK";

						});

						thethis.timer.onTick.on(function(param)
						{
							thethis.timeprogress.css('width', (param.tick / data.sec)*100 + "%");
						});

						thethis.timer.onStoped.on(function ()
						{
							if (stoped == true) return;
							stoped = true;
							//---------------------------------------PHASE 3----------------------------//
							thethis.timeprogress.css('width','100%');
							thethis.submitTest();
						});
						thethis.timer.countDown(data.sec);
						thethis.data = data;
						thethis.dom.find('.id_appname').html(data.fullname);
						thethis.dom.find('.id_apptime').html(data.time);
						thethis.dom.find('.id_apprequest').html('<a href="#uploadcv/' + data.requestid + '">' + data.requesttitle + "</a>"); 
						for (var i in data.question)
						{
							thethis.questions[i] = $('<div></div>');
							thethis.questions[i].css('background-color', 'rgb(255, 255, 220)');
							thethis.questions[i].css('padding-top', '15px');
							thethis.questions[i].css('padding-bottom', '15px');
							thethis.questions[i].css('border-top', '1px dashed gray');
							thethis.questions[i].append($('<div class="id_questiontitle"><p>Câu ' + (parseInt(i) + 1)  + '</p>' + data.question[i].title + '</div>'));
							var cs = data.question[i].choose.split("\\n");
							var j = 0;
							var choose = thethis.questions[i]['choose'] = $("<select></select>");
							for (var c in cs)
							{
								j++;
								var pick = $('<option></option>');
								pick.html(cs[c]);
								pick.prop('value', j);
								choose.append(pick);
							}
							(function (i)
							{
								choose.on('click', function ()
								{
									thethis.questions[i].css('background-color', 'transparent');
								});
							})(i);
							thethis.questions[i].append(choose);
							questionsDiv.append(thethis.questions);
						}

						testsubmitBt.on('click', function ()
						{
							testsubmitBt['button']('loading');
							if (stoped == true) return;
							stoped = true;
							thethis.timer.stop();
							thethis.submitTest();
						});
					})['fail'](function ()
					{
                        btn['button']('reset');
                        testsubmitBt['button']('reset');
							alert('wrong captcha or the test is expired');
                            thethis.captcha.regen();
						});
				});

				thethis.captcha.init();
				thethis.timer.init();
			});

			//Khởi tạo các control phụ thuộc
			plus.req(['captcha.js', 'timer.js'], function ()
			{
				thethis.captcha = new Captcha.Plugin();
				thethis.timer = new Timer.Plugin();
				dependloaded.emit();
			});

			//lấy layout
			$.get('test.html', function (data)
			{
				thethis.dom = $(data);
				layoutloaded.emit();
			});
		}

		submitTest()
		{
			var thethis = this;
			var testanswer = [];
			var question = [];
			var choose = [];
			for (var i in this.questions)
			{
				choose[i] = thethis.data.question[i].choose;
				question[i] = thethis.data.question[i].id;
				testanswer[i] = parseInt(this.questions[i]['choose'].val());
			}
			thethis.phase2.hide(0.5);
			plus['wait'].emit();

			$(window).off('beforeunload');

			$.post("HRWeb/test/submit", {
				code: thethis.data.answercode,
				answer: testanswer, question: question, choose: choose
			}, function (data)
				{
					plus['ready'].emit();
					//thethis.phase3.removeClass('hidden');
					thethis.phase3.removeClass('hidden');

				})['fail'](function ()
				{
					alert('this is so f*cking ashamed');
				});
		}

	}
}  