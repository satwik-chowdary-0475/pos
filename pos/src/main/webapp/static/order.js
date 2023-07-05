function getOrderUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/order";
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
    console.log("DATAn 25  ",data)
    var url = getPdfUrl();
    var customerName = data.customerName;
    var json = {
        customerName: data.customerName,
        invoicedTime : data.invoicedTime,
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
//            console.log("DATA ",data);
            convertBase64ToPDF(data,customerName);
            $.notify("PDF for "+customerName+" generated successfully","success");
        },
        error: handleAjaxError
    });
}

function printInvoice(orderCode){
    console.log("Ordercode  ",orderCode);
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
  if(data.status == "ACTIVE"){
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
    		console.log('e   ', e);
    		var status = e.status;
    		var deleteHtml = (e.status != 'ACTIVE')?'':'<button class="btn btn-danger" onclick="deleteOrder('+e.orderCode+')">Delete</button>'
    		var buttonHtml = '<a class="btn btn-info" href="/pos/ui/order/'+e.orderCode+'/order-items">'
    		buttonHtml+= ((status == 'ACTIVE')?'Add Items</a>':'View</a>') + '&nbsp';
    		buttonHtml += (status == 'INVOICED')?('<button class="btn btn-success" onclick="printInvoice(\'' + e.orderCode + '\')">Print Invoice</button>' + '&nbsp'):'';
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
    window.location.href = "/pos/ui/order/"+orderCode+"/order-items"
}

function createOrder(event){
	var $form = $("#order-form");
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
	   		redirectToOrderItem(response);
   },
	   error:handleAjaxError
	});
	return false;
}

function init(){
    $('#create-order').click(createOrder);
}
$(document).ready(init);
$(document).ready(getOrderList);