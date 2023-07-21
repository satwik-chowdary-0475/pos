var userRole;
var fileData = [];
var errorData = [];
const rowsPerPage = 10;
var totalRows = 0;
var currentPage = 1;

var activeNotification = null;

function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brands";
}

function getRoleOfUser(callback){
    userRole = $("meta[name=role]").attr("content");
    callback(userRole);
}

function addBrand(event){
	var $form = $("#brand-form");
	if($form[0].checkValidity()){
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
                getBrandList(currentPage);
                $("#brand-form input[name=brand]").val('');
                $("#brand-form input[name=category]").val('');
                toggleBrandModal();
                showSuccessNotification("Added brand successfully");
       },
           error:function(error){
                 if(error.status == 403){
                        activeNotification = showErrorNotification("You cannot add brand");
                 }
                 else{
                    activeNotification = handleAjaxError(error);
                 }

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

function getBrandList(page){
	var url = getBrandUrl()+"?page="+page+"&rowsPerPage="+rowsPerPage;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBrandList(data);
	   },
	   error: handleAjaxError
	});
}

function uploadRows(){
    var url = getBrandUrl() + '/bulk';
    var json = JSON.stringify(fileData);
    if(JSON.parse(json).length <= 5000 && JSON.parse(json).length > 0){
        $("#process-data").prop("disabled", true).find(".fa-spinner").show();
        $.ajax({
                url:url,
                type:'POST',
                data:json,
                headers: {
                    'Content-Type': 'application/json'
                },
                success: function(response){
                    getBrandList(currentPage);
                    showSuccessNotification("Uploaded data successfully")
                    updateUploadDialog();
                },
                error: function(response){
                    if(response.status == 403){
                        showErrorNotification("You cannot upload brand");
                    }
                    processErrorData(JSON.parse(response.responseText).errorDataList);
                },
                complete: function(){
                    $("#process-data").prop("disabled", false).find(".fa-spinner").hide();
                }
            })
    }
    else{
        (json.length>5000)?( showErrorNotification("Cannot upload more than 5000 rows")):(showErrorNotification("Empty file uploaded"));
    }
}

function updateBrand(event){
    var $form =  $("#brand-edit-form");
    if($form[0].checkValidity()){
        var id = $("#brand-edit-form input[name=id]").val();
        var url = getBrandUrl() + "/" + id;
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
                getBrandList(currentPage);
                $('#edit-brand-modal').modal('toggle');
                showSuccessNotification("Updated brand successfully");
           },
           error: function(response){
                if(response.status == 403){
                   showErrorNotification("You cannot update brand");
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
    $("#brand-edit-form").removeClass("was-validated");
	$("#brand-edit-form input[name=brand]").val(data.brand);
	$("#brand-edit-form input[name=category]").val(data.category);
    $("#brand-edit-form input[name=id]").val(data.id);
	$('#edit-brand-modal').modal('toggle');
}

function displayBrandList(data){
	var $tbody = $('#brand-table').find('tbody');
	$tbody.empty();
	totalRows = data.totalCount;
	for(var i in data.dataList){
		var e = data.dataList[i];
		var buttonHtml = '<button class="btn btn-primary" onclick="displayEditBrand('+e.id+')" ';
		 buttonHtml += ((userRole == 'operator')?'disabled ' : ' ') + '>';
		 buttonHtml += '<div class="d-flex gap-2 align-items-center"><i class="fas fa-pen" style="font-size: 15px; margin-right: 10px;"></i>Edit</div></button>';
		i = parseInt(i)+1;
		var sno = (currentPage - 1)*rowsPerPage + i;
		var row = '<tr>'
       	+ '<td>' + sno + '</td>'
		+ '<td>' + e.brand + '</td>'
		+ '<td>'  + e.category + '</td>';
		row += (userRole!=null && userRole != 'operator')?('<td>' + buttonHtml + '</td>'):'';
		row+= '</tr>';
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

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-brand-modal').modal('toggle');
}

function resetUploadDialog(){
	var $file = $('#brandFile');
	$file.val('');
	$('#brandFileName').html("Choose File");
	fileData = [];
	resetErrorCount();
    $('#download-errors').prop('disabled',true);
    $('#process-data').prop('disabled',true);
	updateUploadDialog();
}

function resetErrorCount(){
    errorData = [];
}

function updateUploadDialog(){
	$('#errorCount').html("" + errorData.length);
	$('#download-errors').prop('disabled',(errorData.length == 0));
}

function updateFileName(){
	var $file = $('#brandFile');
	var fileName = $file.val();
	fileName = removeFakePath(fileName);
	$('#brandFileName').html(fileName);
	$("#process-data").prop('disabled',(fileName.length == 0));
}

function processData(){
    resetErrorCount();
	var file = $('#brandFile')[0].files[0];
	readFileData(file, readFileDataCallback);
}

function validateFileData(fileData){
    const columnHeaders = Object.keys(fileData[0]);
    const expectedHeaders = ["brand", "category"];
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

function downloadErrors(){
	writeFileData(errorData,'tsv','brand_errors');
}

function toggleBrandModal(){
    $('#add-brand-modal').modal('toggle');
}

function resetBrandModal(){
    $("#brand-form input[name=brand]").val('');
    $("#brand-form input[name=category]").val('');
    $("#brand-form").removeClass("was-validated");
    toggleBrandModal();
}

function init(){
$('#modal-add-brand').click(resetBrandModal);
$('#add-brand').click(addBrand);
$('#update-brand').click(updateBrand);
$('#upload-data').click(displayUploadData);
$('#process-data').click(processData);
$('#download-errors').click(downloadErrors);
$('#brandFile').on('change', updateFileName);
$('.pagination').on('click', '.page-link', function(e) {
    e.preventDefault();
    const targetPage = parseInt($(this).data('page'));
    if (targetPage >= 1 && targetPage <= Math.ceil(totalRows / rowsPerPage)) {
      currentPage = targetPage;
      getBrandList(currentPage);
    }
});
}

$(document).ready(function() {
    getRoleOfUser(function(role) {
        init();
        getBrandList(1);
    });
});