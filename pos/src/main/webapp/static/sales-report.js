function getSalesUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/report";
}

function downloadReports(reportData){
	writeFileData(reportData);
}


function getSalesReport(){

    var $form = $("#sales-form");
    	var json = toJson($form);
    	var url = getSalesUrl() + '/sales';
    	$.ajax({
    	   url: url,
    	   type: 'POST',
    	   data: json,
    	   headers: {
           	'Content-Type': 'application/json'
           },
    	   success: function(data) {
    	   		downloadReports(data);
    	   		$("#sales-form input[name=startTime]").val('');
                $("#sales-form input[name=endTime]").val('');
    	   		$("#sales-form input[name=brand]").val('');
    	   		$("#sales-form input[name=category]").val('');
       },
    	   error:handleAjaxError
    	});

    	return false;

}

function init(){
    $("#download-report").click(getSalesReport)
}

$(document).ready(init);
