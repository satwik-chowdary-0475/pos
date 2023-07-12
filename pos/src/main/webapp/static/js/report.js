var userRole;
function getRoleOfUser(callback){
    var role = $("meta[name=role]").attr("content");
    userRole = role;
    callback(role);
}

function getBrandUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/reports";
}

function getDailySalesUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/reports";
}

function getInventoryUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/reports";
}

function getSalesUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/reports";
}


function getDailySalesReports(){
    var url = getDailySalesUrl() + '/day-on-day';
    $.ajax({
    	   url: url,
    	   type: 'GET',
    	   success: function(data) {
    	   		downloadReports(data);
    	   		$.notify("Downloaded Daily Sales Reports successfully","success");
    	   },
    	   error: handleAjaxError
    	});
}

function getBrandReports(){
    var url = getBrandUrl() + '/brand-report';
    $.ajax({
    	   url: url,
    	   type: 'GET',
    	   success: function(data) {
    	   		downloadReports(data);
    	   		$.notify("Downloaded Brand Reports successfully","success");
    	   },
    	   error: handleAjaxError
    	});
}

function getInventoryReports(){

    var url = getInventoryUrl() + '/inventory-report';
    $.ajax({
    	   url: url,
    	   type: 'GET',
    	   success: function(data) {
    	        downloadReports(data);
    	        $.notify("Downloaded Inventory Reports successfully","success");
    	   },
    	   error: handleAjaxError
    	});
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
    	   		toggleSalesModal();
    	   		$.notify("Downloaded Sales Reports successfully","success");
       },
    	   error:handleAjaxError
    	});
    	return false;
}

function downloadReports(reportData){
	writeFileData(reportData,'csv');
}

function toggleSalesModal(){
    $('#sales-report-modal').modal('toggle');
}

function init(){
    $("#download-brand-report").click(getBrandReports);
    $('#download-daily-sales-report').click(getDailySalesReports);
    $("#download-inventory-report").click(getInventoryReports);
    $("#download-sales-report").click(getSalesReport)
    $("#show-filters").click(toggleSalesModal)

}
$(document).ready(function() {
    getRoleOfUser(function(role) {
        init();
    });
});