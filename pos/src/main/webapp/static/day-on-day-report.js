function getDailySalesUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/report";
}

function downloadReports(reportData){
	writeFileData(reportData);
}

//function displayDailySales(data){
//
//    var $tbody = $('#daily-sales-table').find('tbody');
//    	$tbody.empty();
//    	for(var i in data){
//    		var e = data[i];
//            i = parseInt(i)+1;
//            var date = new Date(e.date*1000);
//    		var row = '<tr>'
//    		+ '<td>' + i + '</td>'
//    		+ '<td>' + date.toLocaleDateString() + '</td>'
//    		+ '<td>'  + e.invoiced_orders_count + '</td>'
//    		+ '<td>'  + e.invoiced_items_count + '</td>'
//    		+ '<td>'  + e.total_revenue + '</td>'
//    		+ '</tr>';
//            $tbody.append(row);
//    	}
//
//}

function getDailySalesReports(){

    var url = getDailySalesUrl() + '/day-on-day';
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

$('#download-report').click(getDailySalesReports);

}

$(document).ready(init);
