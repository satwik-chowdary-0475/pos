const rowsPerPage = 10;
var totalRows = 0;
var currentPage = 1;

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
            getOrderList(currentPage);
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
        orderCode : data.orderCode,
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
            showSuccessNotification("PDF for "+customerName+" generated successfully");
        },
        error: function(e){
            showErrorNotification("PDF generation failed");
        }
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
            getOrderList(currentPage);
        },
        error: handleAjaxError
    });
  }
  callback();
}

function displayOrderList(data){
    var $tbody = $('#order-table').find('tbody');
    $tbody.empty();
    totalRows = data.totalCount;
    for(var i in data.dataList){
        var e = data.dataList[i];
        var status = e.status;
        var deleteHtml = (e.status != 'CREATED')?'':'<button class="btn btn-danger" onclick="deleteOrder(\''+e.orderCode+'\')" ';
        deleteHtml += (e.status != 'CREATED')?'':'<div class="d-flex gap-2 align-items-center"><i class="fas fa-trash" style="font-size: 15px; margin-right: 10px;"></i>Delete</div></button>';
        var buttonHtml = '<a class="btn btn-info" href="/pos/orders/' + e.orderCode + '">';
        buttonHtml += (status == 'CREATED' ? '<div class="d-flex gap-2 align-items-center"><i class="fas fa-cart-plus" style="font-size: 15px; margin-right: 10px;"></i>Add Items</div>' : '<div class="d-flex gap-2 align-items-center"><i class="fas fa-eye" style="font-size: 15px; margin-right: 10px;"></i>View</div>') + '</a> &nbsp;';
        buttonHtml += (status == 'INVOICED') ? '<button class="btn btn-success" onclick="printInvoice(\'' + e.orderCode + '\')"><div class="d-flex gap-2 align-items-center"><i class="fas fa-print" style="font-size: 15px; margin-right: 10px;"></i> Print Invoice</div></button> &nbsp;' : '';
        buttonHtml += deleteHtml;
        i = parseInt(i)+1;
        var sno = (currentPage - 1)*rowsPerPage + i;
        var row = '<tr rowId='+e.id+' >'
        + '<td>' + sno + '</td>'
        + '<td>' + e.customerName + '</td>'
        + '<td>' + e.status + '</td>'
        + '<td>' + buttonHtml + '</td>'
        + '</tr>';
        $tbody.append(row);
    }
    updatePagination();
}

function updatePagination() {
    const pagination = $('.pagination');
    pagination.empty();

    const totalPages = Math.ceil(totalRows / rowsPerPage);
    const prevDisabled = currentPage === 1 ? 'disabled' : '';
    const nextDisabled = currentPage === totalPages ? 'disabled' : '';

    let paginationHTML = `
      <li class="page-item ${prevDisabled}">
        <a class="page-link" href="#" tabindex="-1" data-page="${currentPage - 1}"><i class="fas fa-angle-double-left" style="font-size: 15px;"></i></a>
      </li>
    `;

    let startPage = Math.max(1, currentPage - 1);
    let endPage = Math.min(totalPages, startPage + 2);

    if (endPage - startPage < 2) {
    if (startPage === 1) {
      endPage = Math.min(totalPages, startPage + 2);
    } else {
      startPage = Math.max(1, endPage - 2);
    }
    }
    if (startPage > 1) {
    paginationHTML += `
      <li class="page-item">
        <a class="page-link" href="#" data-page="1">1</a>
      </li>
    `;

    if (startPage > 2) {
      paginationHTML += `
        <li class="page-item disabled">
          <a class="page-link" href="#" tabindex="-1">...</a>
        </li>
      `;
    }
    }

    for (let i = startPage; i <= endPage; i++) {
    const active = currentPage === i ? 'active' : '';
    paginationHTML += `
      <li class="page-item ${active}">
        <a class="page-link" href="#" data-page="${i}">${i}</a>
      </li>
    `;
    }

    if (endPage < totalPages) {
    if (endPage < totalPages - 1) {
      paginationHTML += `
        <li class="page-item disabled">
          <a class="page-link" href="#" tabindex="-1">...</a>
        </li>
      `;
    }
    paginationHTML += `
          <li class="page-item">
            <a class="page-link" href="#" data-page="${totalPages}">${totalPages}</a>
          </li>
        `;
      }

    paginationHTML += `
      <li class="page-item ${nextDisabled}">
        <a class="page-link" href="#" data-page="${currentPage + 1}"><i class="fas fa-angle-double-right" style="font-size: 15px;"></i></a>
      </li>
    `;

    pagination.html(paginationHTML);
}


function getOrderList(page){
    var url = getOrderUrl()+"?page="+page+"&rowsPerPage="+rowsPerPage;
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
    window.location.href = "/pos/orders/"+orderCode;
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

function removeHash() {
    window.location.hash = '';
}

function init(){
    $('#create-modal-order').click(resetOrderModal);
    $('#create-order').click(createOrder);
    $('.pagination').on('click', '.page-link', function(e) {
        e.preventDefault();
        const targetPage = parseInt($(this).data('page'));
        if (targetPage >= 1 && targetPage <= Math.ceil(totalRows / rowsPerPage)) {
          currentPage = targetPage;
          getOrderList(currentPage);
        }
    });
    if(window.location.hash.includes("#success")){
        const encodedData = window.location.hash.split('?')[1];
        const decodedData = decodeURIComponent(encodedData);
        const data = JSON.parse(decodedData);
        showSuccessNotification("PDF for "+data.customerName+" generated successfully");
        removeHash();
    }
    else if(window.location.hash.includes("#error")){
         showErrorNotification("PDF generation failed");
         removeHash();
    }
}

$(document).ready(init);
$(document).ready(getOrderList(1));