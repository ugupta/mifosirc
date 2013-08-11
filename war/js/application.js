var latestLog = "#latestLog";
var moreLogPrev = "#moreLogPrev";
var moreLogNext = "#moreLogNext";
var logOfDate = "#logOfDate";
var header = $("#header");

var nextLogDate;
var prevLogDate;

$(window).keydown(function(e) {
	if(e.keyCode == 37) {
		getMoreLog(prevLogDate);
	}
	if(e.keyCode == 39) {
		getMoreLog(nextLogDate);
	}
});

function getMoreLog(sDate) {
	var params = { date : sDate };
	$(latestLog).html("<div class='loader'></div>");
	$.getJSON("/logs.json", params, successHandler, failureHandler);
}

function successHandler(data) {
	var content = "";
	for ( var i = 0; i < data.logs.length - 1; i++) {
		content += "<span class='logText'>" + data.logs[i] + "</span></br>";
	}
	
	if(data.logs.length == 1){
		content += "<span class='logText'>No Logs</span></br>";
	}
	
	$(latestLog).html(content);
	
	$(moreLogPrev).attr("value", data.prevDate);
	$(moreLogPrev).html(" << " + data.prevDate);
	$(moreLogPrev).unbind("click");
	$(moreLogPrev).click(function() {
		getMoreLog(data.prevDate);
	});
	
	$(moreLogNext).attr("value", data.nextDate);
	$(moreLogNext).html(data.nextDate + " >> ");
	$(moreLogNext).unbind("click");
	$(moreLogNext).click(function() {
		getMoreLog(data.nextDate);
	});
	
	nextLogDate = data.nextDate;
	prevLogDate = data.prevDate;

	$(logOfDate).html("Log for "+ data.currDate);		
}

function failureHandler() {
	alert('server error!!!');
}