function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brand";
}

function addBrand(event){
	//Set the values to update
	var $form = $("#brand-form");
	var json = toJson($form);
	var url = getBrandUrl();
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getBrandList();
	   		$("#brand-form input[name=brand]").val('');
	   		$("#brand-form input[name=category]").val('');
	   		$.notify("Added brand successfully","success");

   },
	   error:handleAjaxError
	});

	return false;
}

function getBrandList(){
	var url = getBrandUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBrandList(data);
	   },
	   error: handleAjaxError
	});
}

function displayEditBrand(id){
	var url = getBrandUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBrand(data);
	   },
	   error: handleAjaxError
	});
}

function displayBrand(data){
	$("#brand-edit-form input[name=brand]").val(data.brand);
	$("#brand-edit-form input[name=category]").val(data.category);
    $("#brand-edit-form input[name=id]").val(data.id);
	$('#edit-brand-modal').modal('toggle');
}

function displayBrandList(data){
	var $tbody = $('#brand-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];

		var buttonHtml = '<button class="btn btn-primary" onclick="displayEditBrand('+e.id+')">Edit</button>';
		i = parseInt(i)+1;
		var row = '<tr>'
       	+ '<td>' + i + '</td>'
		+ '<td>' + e.brand + '</td>'
		+ '<td>'  + e.category + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}
var fileData = [];
var errorData = [];
var processCount = 0;




function resetUploadDialog(){
	//Reset file name
	var $file = $('#brandFile');
	$file.val('');
	$('#brandFileName').html("Choose File");
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
	$('#upload-brand-modal').modal('toggle');
}

function processData(){
	var file = $('#brandFile')[0].files[0];
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
	    getBrandList();
		return;
	}

	//Process next row
	var row = fileData[processCount];
	processCount++;

	var json = JSON.stringify(row);
	var url = getBrandUrl();

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

function updateBrand(event){

	//Get the ID
	var id = $("#brand-edit-form input[name=id]").val();
	var url = getBrandUrl() + "/" + id;

	//Set the values to update
	var $form = $("#brand-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getBrandList();
	   		$('#edit-brand-modal').modal('toggle');
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateFileName(){
	var $file = $('#brandFile');
	var fileName = $file.val();
	$('#brandFileName').html(fileName);
}
function downloadErrors(){
	writeFileData(errorData);
}

function init(){

$('#add-brand').click(addBrand);
$('#update-brand').click(updateBrand);
$('#upload-data').click(displayUploadData);
$('#process-data').click(processData);
$('#download-errors').click(downloadErrors);
$('#brandFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(getBrandList);

