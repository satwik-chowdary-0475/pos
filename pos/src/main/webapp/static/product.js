function getProductUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/product";
}
function addProduct(event){
    var $form = $("#product-form");
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
                $.notify("Added product successfully","success");

       },
    	   error: handleAjaxError
    	});

    	return false;
}

function processData(){
	var file = $('#productFile')[0].files[0];
	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results){
	fileData = results.data;
	uploadRows();
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
		var buttonHtml = '<button class="btn btn-primary" onclick="displayEditProduct('+e.id+')">Edit</button>'
        i = parseInt(i)+1;
		var row = '<tr>'
		+ '<td>' + i + '</td>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>'  + e.name + '</td>'
		+ '<td>'  + e.brand + '</td>'
		+ '<td>'  + e.category + '</td>'
        + '<td>'  + e.mrp + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

function uploadRows(){
//Update progress
	updateUploadDialog();
	//If everything processed then return
	if(processCount==fileData.length){
	    getProductList();
		return;
	}

	//Process next row
	var row = fileData[processCount];
	processCount++;

	var json = JSON.stringify(row);

	var url = getProductUrl();

	//Make ajax call
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		uploadRows();
	   },
	   error: function(response){
	   		row.error=response.responseText
	   		errorData.push(row);
	   		uploadRows();
	   }
	});


}


var fileData = [];
var errorData = [];
var processCount = 0;


function displayUploadData(){
 	resetUploadDialog();
	$('#upload-product-modal').modal('toggle');
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#productFile');
	$file.val('');
	$('#productFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	//Update counts
	updateUploadDialog();
}

function updateUploadDialog(){
	$('#rowCount').html("" + fileData.length);
	$('#processCount').html("" + processCount);
	$('#errorCount').html("" + errorData.length);
}

function updateFileName(){
	var $file = $('#productFile');
	var fileName = $file.val();
	$('#productFileName').html(fileName);
}
function downloadErrors(){
	writeFileData(errorData);
}

function updateProduct(event){
	$('#edit-product-modal').modal('toggle');
	//Get the ID
	var id = $("#product-edit-form input[name=id]").val();
	var url = getProductUrl() + "/" + id;

	//Set the values to update
	var $form = $("#product-edit-form");
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
	   },
	   error: handleAjaxError
	});

	return false;
}


function init(){
    $('#add-product').click(addProduct);
    $('#update-product').click(updateProduct);
    $('#upload-data').click(displayUploadData);
    $('#process-data').click(processData);
    $('#download-errors').click(downloadErrors);
    $('#productFile').on('change', updateFileName);
}

$(document).ready(init);
$(document).ready(getProductList)