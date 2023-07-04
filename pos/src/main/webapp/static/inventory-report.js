function getInventoryUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/report";
}

function downloadReports(reportData){
	writeFileData(reportData);
}

//function displayInventoryDetails(data){
//
//    var $tbody = $('#inventory-table').find('tbody');
//    	$tbody.empty();
//    	for(var i in data){
//    		var e = data[i];
//            i = parseInt(i)+1;
//    		var row = '<tr>'
//    		+ '<td>' + i + '</td>'
//    		+ '<td>'  + e.brand + '</td>'
//    		+ '<td>'  + e.category + '</td>'
//    		+ '<td>'  + e.quantity + '</td>'
//    		+ '</tr>';
//            $tbody.append(row);
//    	}
//
//}

function getInventoryReports(){

    var url = getInventoryUrl() + '/inventory-report';
    $.ajax({
    	   url: url,
    	   type: 'GET',
    	   success: function(data) {
    	        downloadReports(data);
    	   },
    	   error: handleAjaxError
    	});
}
function init(){
    $("#download-report").click(getInventoryReports);
}
$(document).ready(init);
