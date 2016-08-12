define(['text!trang/muser/muser.html','trang/profileeditor/profileeditor', 'trang/joblist/joblist', 'trang/candidatelist/candidatelist'], function (layout, ProfileEditor, JobList, CandidateList) {
	return function () {
		var profileeditor = new ProfileEditor();
		var joblist = new JobList();
		var candidatelist = new CandidateList();

		var dom = this.dom = $(layout);
		dom.find('.profileeditor-anchor').after(candidatelist.dom);
		dom.find('.messagelist-anchor').after(joblist.dom);

		

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