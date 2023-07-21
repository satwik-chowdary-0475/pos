var userRole;
var fileData = [];
var errorData = [];
var processCount = 0;
const rowsPerPage = 10;
var totalRows = 0;
var currentPage = 1;

function getProductUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/products";
}

function getRoleOfUser(callback){
    userRole = $("meta[name=role]").attr("content");
    callback(userRole);
}

function addProduct(event){
    var $form = $("#product-form");
    if($form[0].checkValidity()){
        var json = toJson($form);
        var url = getProductUrl();
        $.ajax({
           url: url,
           type: 'POST',
           data: json,
           headers: {
            'Content-Type': 'application/json'
           },
           success: function(response) {
                getProductList(currentPage);
                $("#product-form input[name=name]").val('');
                $("#product-form input[name=barcode]").val('');
                $("#product-form input[name=brand]").val('');
                $("#product-form input[name=category]").val('');
                $("#product-form input[name=mrp]").val('');
                toggleProductModal();
                showSuccessNotification("Added product successfully");

       },
           error: function(response){
                if(response.status == 403){
                    showErrorNotification("You cannot add product");
                }
                handleAjaxError(response);
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

function processData(){
    resetErrorCount();
	var file = $('#productFile')[0].files[0];
	readFileData(file, readFileDataCallback);
}

function validateFileData(fileData){
    const columnHeaders = Object.keys(fileData[0]);
    const expectedHeaders = ["name","barcode","brand", "category","mrp"];
    const headersMatched = expectedHeaders.every(header => columnHeaders.includes(header));
    return (headersMatched && columnHeaders.length === expectedHeaders.length);
}

function readFileDataCallback(results){
	fileData = results.data;
    if(validateFileData(fileData)){
        resetErrorCount();
        uploadRows();
    }
    else{
        resetErrorCount();
        updateUploadDialog();
        showErrorNotification("Uploaded file note supported. Header not matched");
    }
}

function getProductList(page){
	var url = getProductUrl()+"?page="+page+"&rowsPerPage="+rowsPerPage;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayProductList(data);
	   },
	   error: handleAjaxError
	});
}

function displayEditProduct(id){
	var url = getProductUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayProduct(data);
	   },
	   error: handleAjaxError
	});
}

function displayProduct(data){
    $("#product-edit-form").removeClass("was-validated");
	$("#product-edit-form input[name=name]").val(data.name);
	$("#product-edit-form input[name=brand]").val(data.brand);
	$("#product-edit-form input[name=category]").val(data.category);
	$("#product-edit-form input[name=mrp]").val(data.mrp);
    $("#product-edit-form input[name=barcode]").val(data.barcode);
    $("#product-edit-form input[name=id]").val(data.id);
	$('#edit-product-modal').modal('toggle');
}

function displayProductList(data){
	var $tbody = $('#product-table').find('tbody');
	$tbody.empty();
	totalRows = data.totalCount;
	for(var i in data.dataList){
		var e = data.dataList[i];
		var buttonHtml = '<button class="btn btn-primary" onclick="displayEditProduct('+e.id+')" ';
		buttonHtml += ((userRole == 'operator')?'disabled ':' ') +'>';
		buttonHtml += '<div class="d-flex gap-2 align-items-center"><i class="fas fa-pen" style="font-size: 15px; margin-right: 10px;"></i>Edit</div></button>';
        i = parseInt(i)+1;
        var sno = (currentPage - 1)*rowsPerPage + i;
		var row = '<tr>'
		+ '<td>' + sno + '</td>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>'  + e.name + '</td>'
		+ '<td>'  + e.brand + '</td>'
		+ '<td>'  + e.category + '</td>'
        + '<td>'  + (e.mrp).toFixed(2) + '</td>';
        row += (userRole!=null && userRole != 'operator')?( '<td>' + buttonHtml + '</td>'):'';
        row += '</tr>';
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

function processErrorData(errorDataList){
    if(errorDataList!=null && errorDataList.length > 0){
         showErrorNotification("Failed to upload the data");
         $.each(errorDataList, function(index) {
           var row = {"row":errorDataList[index].row,"error":errorDataList[index].errorMessage};
           errorData.push(row);
         })
     }
    updateUploadDialog();
}

function uploadRows(){
	var json = JSON.stringify(fileData);
	var url = getProductUrl()+'/bulk';
    if(JSON.parse(json).length <= 5000 && JSON.parse(json).length > 0){
        $("#process-data").prop("disabled", true).find(".fa-spinner").show();
        $.ajax({
        	   url: url,
        	   type: 'POST',
        	   data: json,
        	   headers: {
               	'Content-Type': 'application/json'
               },
        	   success: function(response) {
        	        getProductList(currentPage);
                    showSuccessNotification("Uploaded data successfully");
        	        updateUploadDialog();
        	   },
        	   error: function(response){
        	   		if(response.status == 403){
        	   		showErrorNotification("You cannot upload the data");
        	   		}
                    processErrorData(JSON.parse(response.responseText).errorDataList);
        	   },
        	   complete: function(){
        	       $("#process-data").prop("disabled", false).find(".fa-spinner").hide();
        	   }
        	});
    }
    else{
        (json.length>5000)?(showErrorNotification("Cannot upload more than 5000 rows")):(showErrorNotification("Empty file uploaded"));
    }
}

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-product-modal').modal('toggle');
}

function resetErrorCount(){
    errorData = [];
}
function resetUploadDialog(){
	var $file = $('#productFile');
	$file.val('');
	$('#productFileName').html("Choose File");
	processCount = 0;
	fileData = [];
    resetErrorCount();
    $('#download-errors').prop('disabled',true);
    $('#process-data').prop('disabled',true);
	updateUploadDialog();
}

function updateUploadDialog(){
	$('#errorCount').html("" + errorData.length);
	$('#download-errors').prop('disabled',(errorData.length == 0));
}

function updateFileName(){
	var $file = $('#productFile');
	var fileName = $file.val();
	fileName = removeFakePath(fileName);
	$('#productFileName').html(fileName);
	$("#process-data").prop('disabled',(fileName.length == 0));
}
function downloadErrors(){
	writeFileData(errorData,'tsv','product_errors');
}

function updateProduct(event){

    var $form = $("#product-edit-form");
    if($form[0].checkValidity()){
        var id = $("#product-edit-form input[name=id]").val();
        var url = getProductUrl() + "/" + id;
        var json = toJson($form);
        $.ajax({
           url: url,
           type: 'PUT',
           data: json,
           headers: {
            'Content-Type': 'application/json'
           },
           success: function(response) {
                getProductList(currentPage);
                $('#edit-product-modal').modal('toggle');
                showSuccessNotification("Product updated successfully");
           },
           error: function(response){
                if(response.status == 403){
                   showErrorNotification("You cannot update the data");
                }
                handleAjaxError(response);
           }
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

function toggleProductModal(){
    $('#add-product-modal').modal('toggle');
}

function resetProductModal(){
    $("#product-form input[name=name]").val('');
    $("#product-form input[name=brand]").val('');
    $("#product-form input[name=category]").val('');
    $("#product-form input[name=mrp]").val('');
    $("#product-form input[name=barcode]").val('');
    $("#product-form").removeClass("was-validated");
    toggleProductModal();
}

function init(){
    $('#add-modal-product').click(resetProductModal);
    $('#add-product').click(addProduct);
    $('#update-product').click(updateProduct);
    $('#upload-data').click(displayUploadData);
    $('#process-data').click(processData);
    $('#download-errors').click(downloadErrors);
    $('#productFile').on('change', updateFileName);
    $('.pagination').on('click', '.page-link', function(e) {
        e.preventDefault();
        const targetPage = parseInt($(this).data('page'));
        if (targetPage >= 1 && targetPage <= Math.ceil(totalRows / rowsPerPage)) {
          currentPage = targetPage;
          getProductList(currentPage);
        }
    });
}

$(document).ready(function() {
    getRoleOfUser(function(role) {
        init();
        getProductList(1);
    });
});