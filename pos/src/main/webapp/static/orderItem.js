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
    if(status!= 'ACTIVE'){
        $("#print-invoice").text('Prince Invoice');
        $('#orderItem-form').hide();
    }

}

function getPdfUrl(){
    var url = "http://0.0.0.0:8081/invoice/api/generate-pdf";
    return url;
}
function getOrderUrl(){
   var baseUrl = $("meta[name=baseUrl]").attr("content")
   return baseUrl + "/api/order";
}
function getOrderItemUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/order/"+orderId+"/order-items";
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
    		editHtml += ((orderStatus != 'ACTIVE')?'disabled':'') + '>Edit</button>';
    		var deleteHtml ='<button class="btn btn-danger" onclick="deleteOrderItem('+e.id+')" ';
    		deleteHtml += ((orderStatus != 'ACTIVE')?'disabled':'') + '>Delete</button>';
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
            totalPrice+=(parseInt(e.sellingPrice)*parseInt(e.quantity));
    	}

    	var $totalPriceElement = $('#total-price');
    	$totalPriceElement.empty();
    	$totalPriceElement.append('<h5 class="fw-bold mb-0">Total : </h5>');
    	$totalPriceElement.append('<h5 class="fw-bold mb-0">'+totalPrice+'</h5>');

}

function addOrderItem(event){
	//Set the values to update
	var $form = $("#orderItem-form");
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

   },
	   error:handleAjaxError
	});

	return false;
}

function updateOrderItem(event){
	var id = $("#orderItem-edit-form input[name=id]").val();
	var url = getOrderItemUrl() + "/" + id;

	var $form = $("#orderItem-edit-form");
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
  if(data.status == "ACTIVE"){
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

function init(){
    $("#add-orderItem").click(addOrderItem);
    $("#update-orderItem").click(updateOrderItem);
    $("#print-invoice").click(printInvoice);
}
$(document).ready(init);
$(document).ready(getOrderDetails);
//$(document).ready(getOrderItemList());