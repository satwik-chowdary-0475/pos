var userRole;
var fileData = [];
var errorData = [];
var processCount = 0;
const rowsPerPage = 10;
var totalRows = 0;
var currentPage = 1;

function getRoleOfUser(callback){
    userRole = $("meta[name=role]").attr("content");
    callback(userRole);
}

function addInventory(event){
	var $form = $("#inventory-form");
	if($form[0].checkValidity()){
	    var json = toJson($form);
	    var jsonObj = JSON.parse(json);
	    jsonObj.quantity = scientificNumberReviver(jsonObj.quantity);
	    json = JSON.stringify(jsonObj);
        var url = getInventoryUrl();
        $.ajax({
           url: url,
           type: 'POST',
           data: json,
           headers: {
            'Content-Type': 'application/json'
           },
           success: function(response) {
                getInventoryList(currentPage);
                $('#inventory-form input[name=barcode]').val('');
                $('#inventory-form input[name=quantity]').val('');
                toggleInventoryModal();
                showSuccessNotification("Added product in inventory successfully");
       },
           error: function(response){
                if(response.status == 403){
                showErrorNotification("You cannot add the data");
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

function getInventoryList(page){
	var url = getInventoryUrl()+"?page="+page+"&rowsPerPage="+rowsPerPage;
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
	totalRows = data.totalCount;
	for(var i in data.dataList){
		var e = data.dataList[i];
		var buttonHtml = '<button class="btn btn-primary" onclick="displayEditInventory('+e.productId+') "';
		buttonHtml += ((userRole=='operator')?' disabled':'')+'>';
		buttonHtml += '<div class="d-flex gap-2 align-items-center"><i class="fas fa-pen" style="font-size: 15px; margin-right: 10px;"></i>Edit</div></button>';
		i = parseInt(i) + 1;
		var sno = (currentPage - 1)*rowsPerPage + i;
		var row = '<tr>'
		+ '<td>' + sno + '</td>'
		+ '<td>' + e.productName + '</td>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>' + e.quantity + '</td>';
		row += (userRole!=null && userRole != 'operator')?('<td>' + buttonHtml + '</td>'):'';
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

function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

function resetErrorCount(){
    errorData = [];
}

function resetUploadDialog(){
	var $file = $('#inventoryFile');
	$file.val('');
	$('#inventoryFileName').html("Choose File");
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
        showErrorNotification("Uploaded file not supported. Headers not matched");
    }
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
	var url = getInventoryUrl()+'/bulk';
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
        	   		getInventoryList(currentPage);
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

function updateInventory(event){
    var $form = $("#inventory-edit-form");
	if($form[0].checkValidity()){
	    var id = $("#inventory-edit-form input[name=id]").val();
        var url = getInventoryUrl() + "/" + id;
        var json = toJson($form);
        var jsonObj = JSON.parse(json);
        jsonObj.quantity = scientificNumberReviver(jsonObj.quantity);
        json = JSON.stringify(jsonObj);
        $.ajax({
           url: url,
           type: 'PUT',
           data: json,
           headers: {
            'Content-Type': 'application/json'
           },
           success: function(response) {
                getInventoryList(currentPage);
                $('#edit-inventory-modal').modal('toggle');
                showSuccessNotification("Product inventory updated successfully");
           },
           error: function(response){
                if(response.status == 403){
                    showErrorNotification("You cannot update the data");
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
	fileName = removeFakePath(fileName);
	$('#inventoryFileName').html(fileName);
	$("#process-data").prop('disabled',(fileName.length == 0));
}

function downloadErrors(){
	writeFileData(errorData,'tsv','inventory_errors');
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
$('.pagination').on('click', '.page-link', function(e) {
    e.preventDefault();
    const targetPage = parseInt($(this).data('page'));
    if (targetPage >= 1 && targetPage <= Math.ceil(totalRows / rowsPerPage)) {
      currentPage = targetPage;
      getInventoryList(currentPage);
    }
});
}

$(document).ready(function() {
    getRoleOfUser(function(role) {
        init();
        getInventoryList(1);
    });
});
