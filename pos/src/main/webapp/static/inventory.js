var userRole;
var fileData = [];
var errorData = [];
var processCount = 0;

function getRoleOfUser(callback){
    userRole = $("meta[name=role]").attr("content");
    callback(userRole);
}

function addInventory(event){
	//Set the values to update
	var $form = $("#inventory-form");
	if($form[0].checkValidity()){
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
                toggleInventoryModal();
                $.notify("Added product in inventory successfully","success");
       },
           error: function(response){
                if(response.status == 403){
                    $.notify("You cannot add the data",{className:"error",autoHideDelay: 20000});
                }
                handleAjaxError(response);
           }
        });
        $form.addClass("was-validated")
	}
	else{
        event.preventDefault();
        event.stopPropagation();
        $form.addClass('was-validated');
	}
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
    $("#inventory-edit-form").removeClass("was-validated");
	$("#inventory-edit-form input[name=id]").val(data.productId);
	$("#inventory-edit-form input[name=barcode]").val(data.barcode);
	$("#inventory-edit-form input[name=quantity]").val(data.quantity);
	$('#edit-inventory-modal').modal('toggle');
}

function displayInventoryList(data){
	var $tbody = $('#inventory-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var buttonHtml = '<button class="btn btn-primary" onclick="displayEditInventory('+e.productId+') "';
		buttonHtml += ((userRole=='operator')?' disabled':'')+'>';
		buttonHtml += '<div class="d-flex gap-2 align-items-center"><i class="fas fa-pen" style="font-size: 15px; margin-right: 10px;"></i>Edit</div></button>';
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

function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

function resetErrorCount(){
    errorData = [];
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#inventoryFile');
	$file.val('');
	$('#inventoryFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	resetErrorCount();
	$('#download-errors').prop('disabled',true);
    $('#process-data').prop('disabled',true);
	//Update counts
	updateUploadDialog();
}



function updateUploadDialog(){
	$('#errorCount').html("" + errorData.length);
	$('#download-errors').prop('disabled',(errorData.length == 0));
}

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-inventory-modal').modal('toggle');
}

function processData(){
    resetErrorCount();
	var file = $('#inventoryFile')[0].files[0];
	readFileData(file, readFileDataCallback);
}

function validateFileData(fileData){
    const columnHeaders = Object.keys(fileData[0]);
    const expectedHeaders = ["barcode", "quantity"];
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
        $.notify("Uploaded file not supported. Columns not matched",{className:"error",autoHideDelay: 20000});
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
//Update progress
//	updateUploadDialog();

	var json = JSON.stringify(fileData);
	var url = getInventoryUrl()+'/bulk';
    if(json.length <= 5000 && json.length > 0){
        //Make ajax call
        $.ajax({
        	   url: url,
        	   type: 'POST',
        	   data: json,
        	   headers: {
               	'Content-Type': 'application/json'
               },
        	   success: function(response) {
        	   		getInventoryList();
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

function updateInventory(event){

    var $form = $("#inventory-edit-form");
	//Get the ID
	if($form[0].checkValidity()){
	    var id = $("#inventory-edit-form input[name=id]").val();
        var url = getInventoryUrl() + "/" + id;
        //Set the values to update
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
                $.notify("Product updated successfully","success")

           },
           error: function(response){
                if(response.status == 403){
                      $.notify("You cannot update the data",{className:"error",autoHideDelay: 20000});
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

function updateFileName(){
	var $file = $('#inventoryFile');
	var fileName = $file.val();
	$('#inventoryFileName').html(fileName);
	$("#process-data").prop('disabled',(fileName.length == 0));
}
function downloadErrors(){
	writeFileData(errorData);
}

function toggleInventoryModal(){
    $('#add-inventory-modal').modal('toggle');
}

function resetInventoryModal(){
    $("#inventory-form input[name=barcode]").val('');
    $("#inventory-form input[name=quantity]").val('');
    $("#inventory-form").removeClass("was-validated");
    toggleInventoryModal();
}

function init(){
$('#add-modal-inventory').click(resetInventoryModal);
$('#add-inventory').click(addInventory);
$('#update-inventory').click(updateInventory);
$('#upload-data').click(displayUploadData);
$('#process-data').click(processData);
$('#download-errors').click(downloadErrors);
$('#inventoryFile').on('change', updateFileName)
}

$(document).ready(function() {
    getRoleOfUser(function(role) {
        init();
        getInventoryList();
    });
});
