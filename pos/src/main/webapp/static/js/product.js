var userRole;
var fileData = [];
var errorData = [];
var processCount = 0;
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
                getProductList();
                $("#product-form input[name=name]").val('');
                $("#product-form input[name=barcode]").val('');
                $("#product-form input[name=brand]").val('');
                $("#product-form input[name=category]").val('');
                $("#product-form input[name=mrp]").val('');
                toggleProductModal();
                $.notify("Added product successfully","success");

       },
           error: function(response){
                if(response.status == 403){
                    $.notify("You cannot add product",{className:"error",autoHideDelay: 20000})
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
        $.notify("Uploaded file not supported. Headers not matched",{className:"error",autoHideDelay: 20000});
    }
}

function getProductList(){
	var url = getProductUrl();
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
	for(var i in data){
		var e = data[i];
		var buttonHtml = '<button class="btn btn-primary" onclick="displayEditProduct('+e.id+')" ';
		buttonHtml += ((userRole == 'operator')?'disabled ':' ') +'>';
		buttonHtml += '<div class="d-flex gap-2 align-items-center"><i class="fas fa-pen" style="font-size: 15px; margin-right: 10px;"></i>Edit</div></button>';
        i = parseInt(i)+1;
		var row = '<tr>'
		+ '<td>' + i + '</td>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>'  + e.name + '</td>'
		+ '<td>'  + e.brand + '</td>'
		+ '<td>'  + e.category + '</td>'
        + '<td>'  + (e.mrp).toFixed(2) + '</td>';
        row += (userRole!=null && userRole != 'operator')?( '<td>' + buttonHtml + '</td>'):'';
        row += '</tr>';
        $tbody.append(row);
	}
}

function processErrorData(errorDataList){
 if(errorDataList!=null && errorDataList.length > 0){
         $.notify("Failed to upload the data",{className:"error",autoHideDelay: 20000});
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
    if(json.length <= 5000 && json.length > 0){
        $.ajax({
        	   url: url,
        	   type: 'POST',
        	   data: json,
        	   headers: {
               	'Content-Type': 'application/json'
               },
        	   success: function(response) {
        	        getProductList();
        	        $.notify("Uploaded data successfully","success");
        	        updateUploadDialog();
        	   },
        	   error: function(response){
        	   		if(response.status == 403){
        	   		    $.notify("You cannot upload the data",{className:"error",autoHideDelay: 20000});
        	   		}
                    processErrorData(JSON.parse(response.responseText).errorDataList);
        	   }
        	});
    }
    else{
        (json.length>5000)?($.notify("Cannot upload more than 5000 rows",{className:"error",autoHideDelay: 20000})):($.notify("Empty file uploaded",{className:"error",autoHideDelay:20000}));
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
        	   		getProductList();
        	   		$('#edit-product-modal').modal('toggle');
        	   		$.notify("Product updated successfully","success")
        	   },
        	   error: function(response){
        	        if(response.status == 403){
                        $.notify("You cannot update the data",{className:"error",autoHideDelay: 20000});
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
}


$(document).ready(function() {
    getRoleOfUser(function(role) {
        init();
        getProductList();
    });
});