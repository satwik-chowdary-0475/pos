var userRole;
var fileData = [];
var errorData = [];

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
                getBrandList();
                $("#brand-form input[name=brand]").val('');
                $("#brand-form input[name=category]").val('');
                toggleBrandModal();
                $.notify("Added brand successfully","success");

       },
           error:function(error){
                 if(error.status == 403){
                     $.notify("You cannot add brand",{className:"error",autoHideDelay: 20000});
                 }
                handleAjaxError(error);
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

function uploadRows(){
    var url = getBrandUrl() + '/bulk';
    var json = JSON.stringify(fileData);
    if(json.length <= 5000 && json.length > 0){
        $.ajax({
                url:url,
                type:'POST',
                data:json,
                headers: {
                    'Content-Type': 'application/json'
                },
                success: function(response){
                    getBrandList();
                    $.notify("Uploaded data successfully","success");
                    updateUploadDialog();
                },
                error: function(response){
                    if(response.status == 403){
                         $.notify("You cannot upload brand",{className:"error",autoHideDelay: 20000});
                    }
                    processErrorData(JSON.parse(response.responseText).errorDataList);
                }

            })
    }
    else{
        (json.length>5000)?($.notify("Cannot upload more than 5000 rows",{className:"error",autoHideDelay: 20000})):($.notify("Empty file uploaded",{className:"error",autoHideDelay:20000}));
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
                getBrandList();
                $('#edit-brand-modal').modal('toggle');
                $.notify("Updated brand successfully","success");
           },
           error: function(response){
                if(response.status == 403){
                 $.notify("You cannot update brand",{className:"error",autoHideDelay: 20000});
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
	for(var i in data){
		var e = data[i];
		var buttonHtml = '<button class="btn btn-primary" onclick="displayEditBrand('+e.id+')" ';
		 buttonHtml += ((userRole == 'operator')?'disabled ' : ' ') + '>';
		 buttonHtml += '<div class="d-flex gap-2 align-items-center"><i class="fas fa-pen" style="font-size: 15px; margin-right: 10px;"></i>Edit</div></button>';
		i = parseInt(i)+1;
		var row = '<tr>'
       	+ '<td>' + i + '</td>'
		+ '<td>' + e.brand + '</td>'
		+ '<td>'  + e.category + '</td>';
		row += (userRole!=null && userRole != 'operator')?('<td>' + buttonHtml + '</td>'):'';
		row+= '</tr>';
        $tbody.append(row);
	}
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
	    $.notify("Uploaded file not supported. Headers not matched",{className:"error",autoHideDelay: 20000});
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
$('#brandFile').on('change', updateFileName)
}

$(document).ready(function() {
    getRoleOfUser(function(role) {
        init();
        getBrandList();
    });
});