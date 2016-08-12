define(['text!trang/router.html'], function (layout) {
	return  function () {
		this.dom = $(layout);
		this.recruiter = undefined;
		this.Recruiter = undefined;
		this.consumer = undefined;
		var trang = this;
		this.route =  function (hash) {
			if ( (hash == undefined || hash == "")) {
				hash = { recruiter: { child: {} } };
				hash = encodeURI(JSON.stringify(hash));
				$.setHash(hash);
				hash = '#' + hash;
			}
			var path = JSON.parse(decodeURI(hash.substr(1)));
			if (path['recruiter'] != undefined) {
				plugIn([["trang/recruiter/recruiter", "Recruiter"]], trang, function () {
					if(trang.recruiter == undefined) trang.recruiter = new trang.Recruiter();
					trang.recruiter.refresh(path['recruiter'].child);

					trang.dom.find('.id_recruiter').append(trang.recruiter.dom);
				});
			}
			else if (path.name == "candidate") {

			}


		}
	}
});