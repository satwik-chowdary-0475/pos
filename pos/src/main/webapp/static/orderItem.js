var orderId;
var orderStatus;
var totalPrice = 0;
var orderItems = [];
var customerName = "";
var orderCreationTime = "";

function getOrderCode(){
    orderCode = $("#order-item").attr("orderCode");
    return orderCode;
}

function handleButtons(status){
    if(status!= 'CREATED'){
        $("#print-invoice").html('<div class="d-flex gap-2 align-items-center"><i class="fas fa-print" style="font-size: 15px; margin-right: 10px;"></i>Print Invoice</div>');
        $('#orderItem-form').hide();
        $('#add-modal-orderItem').hide();
    }

}

function getPdfUrl(){
    var url = "http://0.0.0.0:8081/invoice/api/generate-pdf";
    return url;
}
function getOrderUrl(){
   var baseUrl = $("meta[name=baseUrl]").attr("content")
   return baseUrl + "/api/orders";
}
function getOrderItemUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/orders/"+orderId+"/order-items";
}

function getOrderDetails(){

    var url = getOrderUrl()+"/"+getOrderCode();
    $.ajax({
        url:url,
        type: 'GET',
        success: function(data){
            orderId = data.id;
            orderStatus = data.status;
            customerName = data.customerName;
            orderCreationTime = data.createdAt;
            handleButtons(orderStatus);
            displayOrderDetails(data);
            getOrderItemList();
        },
        error: function(e){
            handleAjaxError(e);
        }
    });
    return orderStatus;
}

function getOrderItemList(){
var url = getOrderItemUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	        orderItems = data;
	   		displayOrderItemList(data);
	   },
	   error: handleAjaxError
	});
}

function displayEditOrderItem(id){
	var url = getOrderItemUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayOrderItem(data);
	   },
	   error: handleAjaxError
	});
}

function displayOrderItem(data){
    $("#orderItem-edit-form").removeClass("was-validated");
	$("#orderItem-edit-form input[name=quantity]").val(data.quantity);
	$("#orderItem-edit-form input[name=barcode]").val(data.barcode);
	$("#orderItem-edit-form input[name=sellingPrice]").val(data.sellingPrice);
	$("#orderItem-edit-form input[name=id]").val(data.id);
	$('#edit-orderItem-modal').modal('toggle');
}

function deleteOrderItem(id){
    var url = getOrderItemUrl()+"/"+id;
    $.ajax({
    	   url: url,
    	   type: 'DELETE',
    	   success: function() {
    	   		getOrderItemList();
    	   },
    	   error: handleAjaxError
    	});
}

function displayOrderItemList(data){
    var $tbody = $('#orderItem-table').find('tbody');
    	$tbody.empty();
    	totalPrice = 0;
    	for(var i in data){
    		var e = data[i];
    		var editHtml = '<button class="btn btn-primary" onclick="displayEditOrderItem('+e.id+')" ';
    		editHtml += ((orderStatus != 'CREATED')?'disabled':'') + '><div class="d-flex gap-2 align-items-center"><i class="fas fa-pen" style="font-size: 15px; margin-right: 10px;"></i>Edit</div></button>';
    		var deleteHtml ='<button class="btn btn-danger" onclick="deleteOrderItem('+e.id+')" ';
    		deleteHtml += ((orderStatus != 'CREATED')?'disabled':'') + '><div class="d-flex gap-2 align-items-center"><i class="fas fa-trash" style="font-size: 15px; margin-right: 10px;"></i>Delete</div></button>';
    		var buttonHtml = editHtml + '&nbsp' + deleteHtml;
    		i = parseInt(i)+1;
    		var row = '<tr>'
    		+ '<td>' + i + '</td>'
    		+ '<td>' + e.barcode + '</td>'
    		+ '<td>' + e.productName + '</td>'
    		+ '<td>' + e.quantity + '</td>'
    		+ '<td>' + e.sellingPrice + '</td>'
    		+ '<td>' + e.totalPrice + '</td>'
    		+ '<td>' + buttonHtml + '</td>'
    		+ '</tr>';
            $tbody.append(row);
            totalPrice+=(parseFloat(e.sellingPrice)*parseInt(e.quantity));
    	}

        var totalPriceRow = '<tr class="no-border">'
    	+'<td colspan="5" style="font-weight:600">Total Price</td>'
    	+'<td colspan="1" style="font-weight: 600">'+totalPrice+'</td>'
    	+'<td>'
    	+'</tr>';
        $tbody.append(totalPriceRow);

}

function addOrderItem(event){
	//Set the values to update
	var $form = $("#orderItem-form");
	if($form[0].checkValidity()){
	    var json = toJson($form);
        var url = getOrderItemUrl();
        $.ajax({
           url: url,
           type: 'POST',
           data: json,
           headers: {
            'Content-Type': 'application/json'
           },
           success: function(response) {
                getOrderItemList();
                $("#orderItem-form input[name=barcode]").val('');
                $("#orderItem-form input[name=quantity]").val('');
                $("#orderItem-form input[name=sellingPrice]").val('');
                toggleOrderItemModal();
       },
           error:handleAjaxError
        });
        $form.addClass("was-validated");
	}
	else{
	    event.preventDefault();
        event.stopPropagation();
        $form.addClass('was-validated');
	}


	return false;
}

function updateOrderItem(event){
	var id = $("#orderItem-edit-form input[name=id]").val();
	var url = getOrderItemUrl() + "/" + id;

	var $form = $("#orderItem-edit-form");
	if($form[0].checkValidity()){
	    var json = toJson($form);
        $.ajax({
        	   url: url,
        	   type: 'PUT',
        	   data: json,
        	   headers: {
               	'Content-Type': 'application/json'
               },
        	   success: function(response) {
        	   		getOrderItemList();
        	   		$('#edit-orderItem-modal').modal('toggle');
        	   },
        	   error: handleAjaxError
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

function generatePdf(data){
    var url = getPdfUrl();
    var customerName = data.customerName;
    var json = {
        customerName: data.customerName,
        invoicedTime : data.createdAt,
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
            window.location=document.referrer;
            $.notify("PDF for "+customerName+" generated successfully","success");
        },
        error: handleAjaxError
    });
}

function printInvoice(){
    var url = getOrderUrl() + '/'+getOrderCode();
    $.ajax({
         url:url,
         type:'GET',
        headers: {
         'Content-Type': 'application/json'
        },
         success : function(data){
            if(data.orderItems.length == 0){
               $.notify("Add order items!!")
            }
            else{
             setOrderStatus(data,function(){
                    generatePdf(data);
             });
            }
         },
         error: handleAjaxError
     });
}

function setOrderStatus(data, callback) {
  if(data.status == "CREATED"){
    var url = getOrderUrl()+'/'+ getOrderCode()
    $.ajax({
        url:url,
        type:'PUT',
        headers: {
           'Content-Type': 'application/json'
        },
        success: function(data){
        },
        error: handleAjaxError
    });
  }
  callback();
}

function displayOrderDetails(data){
    var orderTitle = "Order #" + getOrderCode()
    var $title = $("#order-title").find("h3");
    $title.html(orderTitle);
    var $details = $("#order-details").find("div");
    var date = new Date(data.createdAt*1000);
    var customerName = '<span class="mr-3 pb-2">'+data.customerName+'</span>'
    var orderTime = '<span class="mr-3 pb-2">'+date.toLocaleDateString()+'</span>'
    $details.append(customerName);
    $details.append(orderTime);
}

function toggleOrderItemModal(){
    $('#add-orderItem-modal').modal('toggle');
}

function resetOrderItemModal(){
    $("#orderItem-form input[name=quantity]").val('');
    $("#orderItem-form input[name=barcode]").val('');
    $("#orderItem-form input[name=sellingPrice]").val('');
    $("#orderItem-form").removeClass("was-validated");
    toggleOrderItemModal();
}

function init(){
    $('#add-modal-orderItem').click(resetOrderItemModal);
    $("#add-orderItem").click(addOrderItem);
    $("#update-orderItem").click(updateOrderItem);
    $("#print-invoice").click(printInvoice);
}
$(document).ready(init);
$(document).ready(getOrderDetails);