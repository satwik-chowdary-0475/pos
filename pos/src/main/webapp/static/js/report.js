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
    $("#download-daily-sales-report").prop("disabled", true).find(".fa-spinner").show();
    var url = getDailySalesUrl() + '/daily_sales';
    $.ajax({
       url: url,
       type: 'GET',
       success: function(data) {
            downloadReports(data,'daily_sales_report');
            showSuccessNotification("Downloaded Daily Sales Reports successfully");
       },
       error: handleAjaxError,
       complete: function(){
            $("#download-daily-sales-report").prop("disabled", false).find(".fa-spinner").hide();
       }
    });
}

function getBrandReports(){
    $("#download-brand-report").prop("disabled", true).find(".fa-spinner").show();
    var url = getBrandUrl() + '/brands';
    $.ajax({
       url: url,
       type: 'GET',
       success: function(data) {
            downloadReports(data,'brand_report');
            showSuccessNotification("Downloaded Brand Reports successfully");
       },
       error: handleAjaxError,
       complete: function(){
            $("#download-brand-report").prop("disabled", false).find(".fa-spinner").hide();
       }
    });
}

function getInventoryReports(){
     $("#download-inventory-report").prop("disabled", true).find(".fa-spinner").show();
    var url = getInventoryUrl() + '/inventory';
    $.ajax({
       url: url,
       type: 'GET',
       success: function(data) {
            downloadReports(data,'inventory_report');
            showSuccessNotification("Downloaded Inventory Reports successfully");
       },
       error: handleAjaxError,
       complete: function(){
            $("#download-inventory-report").prop("disabled", false).find(".fa-spinner").hide();
       }
    });
}

function validateDate(){
    const startDate = new Date(inputStartTime.value);
    const endDate = new Date(inputEndTime.value);
    if (startDate > endDate) {
        inputStartTime.setCustomValidity('Start Date must be before End Date.');
        return false;
    }
   inputStartTime.setCustomValidity('');
   return true;
}

function getSalesReport(){
    var $form = $("#sales-form");
    if($form[0].checkValidity()){
        var json = toJson($form);
        var url = getSalesUrl() + '/sales';
         $("#download-sales-report").prop("disabled", true).find(".fa-spinner").show();
        $.ajax({
           url: url,
           type: 'POST',
           data: json,
           headers: {
            'Content-Type': 'application/json'
           },
           success: function(data) {
                downloadReports(data,'sales_report');
                $("#sales-form input[name=startTime]").val('');
                $("#sales-form input[name=endTime]").val('');
                $("#sales-form input[name=brand]").val('');
                $("#sales-form input[name=category]").val('');
                toggleSalesModal();
                showSuccessNotification("Downloaded Sales Reports successfully");
       },
           error:handleAjaxError,
           complete: function(){
                $("#download-sales-report").prop("disabled", false).find(".fa-spinner").hide();
           }
        });
        $form.addClass('was-validated');
    }
    else{
        event.preventDefault();
        event.stopPropagation();
        $form.addClass('was-validated');
    }

    return false;
}

function downloadReports(reportData,fileName){
	writeFileData(reportData,'csv',fileName);
}

function toggleSalesModal(){
    $('#sales-report-modal').modal('toggle');
}

function resetSalesModal(){
     $("#sales-form input[name=startTime]").val('');
    $("#sales-form input[name=endTime]").val('');
    $("#sales-form").removeClass("was-validated");
    toggleSalesModal();
}

function init(){
    $("#download-brand-report").click(getBrandReports);
    $('#download-daily-sales-report').click(getDailySalesReports);
    $("#download-inventory-report").click(getInventoryReports);
    $("#download-sales-report").click(getSalesReport);
    $("#show-filters").click(resetSalesModal);
    $("#inputStartTime").change(validateDate);
    $("#inputEndTime").change(validateDate);
}

$(document).ready(function() {
    getRoleOfUser(function(role) {
        init();
    });
});