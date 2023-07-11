function getOrderUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/orders";
}

function getPdfUrl(){
    var url = "http://0.0.0.0:8081/invoice/api/generate-pdf";
    return url;
}

function deleteOrder(orderCode){

    var url = getOrderUrl()+"/"+orderCode;
    $.ajax({
    	   url: url,
    	   type: 'DELETE',
    	   success: function() {
    	   		getOrderList();
    	   },
    	   error: handleAjaxError
    	});

}
function generatePdf(data){
    var url = getPdfUrl();
    var customerName = data.customerName;
    var json = {
        customerName: data.customerName,
        invoicedTime : data.invoicedAt,
        orderId : data.id,
        orderItems : data.orderItems
    }
    var jsonString = JSON.stringify(json);
    $.ajax({
        url:url,
        type:'POST',
        data: jsonString,
       headers: {
        'Content-Type': 'application/json'
       },
        success : function(data){
            convertBase64ToPDF(data,customerName);
            $.notify("PDF for "+customerName+" generated successfully","success");
        },
        error: handleAjaxError
    });
}

function printInvoice(orderCode){
    var url = getOrderUrl() + '/' + orderCode;
     $.ajax({
             url:url,
             type:'GET',
            headers: {
             'Content-Type': 'application/json'
            },
             success : function(data){
                 setOrderStatus(data,function(){
                    generatePdf(data);
                 });
             },
             error: handleAjaxError
         });
}

function setOrderStatus(data, callback) {
  if(data.status == "CREATED"){
    var url = getOrderUrl()+'/'+data.orderCode;
    $.ajax({
        url:url,
        type:'PUT',
        headers: {
           'Content-Type': 'application/json'
        },
        success: function(data){
            getOrderList();
        },
        error: handleAjaxError
    });
  }
  callback();
}

function displayOrderList(data){
    var $tbody = $('#order-table').find('tbody');
    	$tbody.empty();
    	for(var i in data){
    		var e = data[i];
    		var status = e.status;
    		var deleteHtml = (e.status != 'CREATED')?'':'<button class="btn btn-danger" onclick="deleteOrder(\''+e.orderCode+'\')" ';
    		deleteHtml += (e.status != 'CREATED')?'':'<div class="d-flex gap-2 align-items-center"><i class="fas fa-trash" style="font-size: 15px; margin-right: 10px;"></i>Delete</div></button>';
    		var buttonHtml = '<a class="btn btn-info" href="/pos/ui/orders/' + e.orderCode + '/order-items">';
            buttonHtml += (status == 'CREATED' ? '<div class="d-flex gap-2 align-items-center"><i class="fas fa-cart-plus" style="font-size: 15px; margin-right: 10px;"></i>Add Items</div>' : '<div class="d-flex gap-2 align-items-center"><i class="fas fa-eye" style="font-size: 15px; margin-right: 10px;"></i>View</div>') + '</a> &nbsp;';
            buttonHtml += (status == 'INVOICED') ? '<button class="btn btn-success" onclick="printInvoice(\'' + e.orderCode + '\')"><div class="d-flex gap-2 align-items-center"><i class="fas fa-print" style="font-size: 15px; margin-right: 10px;"></i> Print Invoice</div></button> &nbsp;' : '';
    		buttonHtml += deleteHtml;
    		i = parseInt(i)+1;
    		var row = '<tr rowId='+e.id+' >'
    		+ '<td>' + i + '</td>'
    		+ '<td>' + e.customerName + '</td>'
    		+ '<td>' + e.status + '</td>'
    		+ '<td>' + buttonHtml + '</td>'
    		+ '</tr>';

            $tbody.append(row);
    	}
}

function getOrderList(){
    var url = getOrderUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayOrderList(data);
	   },
	   error: handleAjaxError
	});
}

function redirectToOrderItem(orderCode){
    window.location.href = "/pos/ui/orders/"+orderCode+"/order-items"
}

function createOrder(event){
	var $form = $("#order-form");
	if($form[0].checkValidity()){
	    var json = toJson($form);
        var url = getOrderUrl();
        $.ajax({
           url: url,
           type: 'POST',
           data: json,
           headers: {
            'Content-Type': 'application/json'
           },
           success: function(response) {
                getOrderList();
                $("#order-form input[name=customerName]").val('');
                toggleOrderModal();
                redirectToOrderItem(response);
       },
           error:handleAjaxError
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

function toggleOrderModal(){
    $('#create-order-modal').modal('toggle');
}

function resetOrderModal(){
    $('#order-form input[name=customerName]').val('');
    $('#order-form').removeClass('was-validated');
    toggleOrderModal();
}

function init(){
    $('#create-modal-order').click(resetOrderModal);
    $('#create-order').click(createOrder);
}
$(document).ready(init);
$(document).ready(getOrderList);