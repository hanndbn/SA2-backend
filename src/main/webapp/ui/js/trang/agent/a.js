var http = require('http');
var mysql = require('mysql');

var poolthanhthai = mysql.createPool({
	connectionLimit: 10,
	host: 'thanhthai.vuonuom.tv',
	user: 'facebook',
	password: 'facebook',
	database: 'facebook_apps'
});

var poollocal = mysql.createPool({
	connectionLimit: 10,
	host: '127.0.0.1',
	user: 'root',
	password: '',
	database: 'facebook_apps'
});

var count = 0;
var n = 20;
var success = 0;

function update_progress() {
	console.log('synchonizing tvhn: ' + 100*(success/count) + " %" );
}

function importchunk() {
	poolthanhthai.query('select * from tuvihangngay_users where status=1 and count = 0 limit ' + n, function (err, rows, fields) {

		if (err) throw err;
		var insertquery = "";
		var idset = "";
		for (var i in rows) {
			var row = rows[i];
			idset += "," + row['ID'];
			insertquery += ",(" + row['ID'] + row['userID'] + row['username'] + row['accessToken'] + row['count'] + row['status'] + row['updated'] + row['cron_log'] + row['description'] + ")";
		}
		idset = idset.substr(1);
		inserquery = inserquery.substr(1);
		//update thanhthai
		poolthanhthai.query('update tuvihangngay_users set status=2 where ID in (' + idset + ")", function (err, rows, fields) {
			if (err) throw err;
			//insert to local
			poollocal.query('insert into tuvihangngay_users(ID,userID,username,accessToken,count,status,updated,cron_log,description) values' + insertquery, function (err, rows, fields) {
				if (err) throw err;
				success += n;
				setTimeout(importchunk, 1);
				update_progress();
			});
		});
	});
}

function doimport() {
	poolthanhthai.query('select count(*) from tuvihangngay_users where status=2', function (err, rows, fields) {
		if (err) throw err;
		count = rows[0]['count'];
		importchunk();
	});
}


