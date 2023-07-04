
function addInventory(event){
	//Set the values to update
	var $form = $("#inventory-form");
	var json = toJson($form);
	var url = getInventoryUrl();
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getInventoryList();
	   		$('#inventory-form input[name=barcode]').val('');
	   		$('#inventory-form input[name=quantity]').val('');
	   		$.notify("Added product in inventory successfully","success");
   },
	   error:handleAjaxError
	});

	return false;
}

function getInventoryList(){
	var url = getInventoryUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayInventoryList(data);
	   },
	   error: handleAjaxError
	});
}

function displayEditInventory(id){
	var url = getInventoryUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayInventory(data);
	   },
	   error: handleAjaxError
	});
}

function displayInventory(data){
	$("#inventory-edit-form input[name=id]").val(data.id);
	$("#inventory-edit-form input[name=barcode]").val(data.barcode);
	$("#inventory-edit-form input[name=quantity]").val(data.quantity);
	$('#edit-inventory-modal').modal('toggle');
}

function displayInventoryList(data){
	var $tbody = $('#inventory-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var buttonHtml = '<button class="btn btn-primary" onclick="displayEditInventory('+e.id+')">Edit</button>';
		i = parseInt(i) + 1;
		var row = '<tr>'
		+ '<td>' + i + '</td>'
		+ '<td>' + e.productName + '</td>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>' + e.quantity + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}
var fileData = [];
var errorData = [];
var processCount = 0;


function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#inventoryFile');
	$file.val('');
	$('#inventoryFileName').html("Choose File");
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

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-inventory-modal').modal('toggle');
}

function processData(){
	var file = $('#inventoryFile')[0].files[0];
	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results){
	fileData = results.data;
	uploadRows();
}

function uploadRows(){
//Update progress
	updateUploadDialog();
	//If everything processed then return
	if(processCount==fileData.length){
	    getInventoryList();
		return;
	}

	//Process next row
	var row = fileData[processCount];
	processCount++;

	var json = JSON.stringify(row);
	var url = getInventoryUrl();

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

function updateInventory(event){

	//Get the ID
	var id = $("#inventory-edit-form input[name=id]").val();
	var url = getInventoryUrl() + "/" + id;

	//Set the values to update
	var $form = $("#inventory-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getInventoryList();
	   		$('#edit-inventory-modal').modal('toggle');
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateFileName(){
	var $file = $('#inventoryFile');
	var fileName = $file.val();
	$('#inventoryFileName').html(fileName);
}
function downloadErrors(){
	writeFileData(errorData);
}

function init(){

$('#add-inventory').click(addInventory);
$('#update-inventory').click(updateInventory);
$('#upload-data').click(displayUploadData);
$('#process-data').click(processData);
$('#download-errors').click(downloadErrors);
$('#inventoryFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(getInventoryList);

