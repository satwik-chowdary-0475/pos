
function toJson($form){
    var serialized = $form.serializeArray();
    var s = '';
    var data = {};
    for(s in serialized){
        data[serialized[s]['name']] = serialized[s]['value']
    }
    var json = JSON.stringify(data);
    return json;
}

function removeExistingNotification() {
  $('.notifyjs-wrapper').fadeOut(function() {
      $(this).remove();
  });
}

function showSuccessNotification(message) {
    removeExistingNotification();
    $.notify(message,"success");
}

function showErrorNotification(message) {
    removeExistingNotification();
    $.notify(message,{className:"error",autoHideDelay: 20000});
}

function showError(message){
    removeExistingNotification();
    $.notify(message,{className:"error",autoHideDelay: 20000});
}

function handleAjaxError(response){
	var response = JSON.parse(response.responseText);
	showError(response.message);
}

function readFileData(file, callback){
	var config = {
		header: true,
		delimiter: "\t",
		skipEmptyLines: "greedy",
		complete: function(results) {
			callback(results);
	  	}
	}
	Papa.parse(file, config);
}

function writeFileData(arr, format, fileName) {
  var config = {
    quoteChar: '"',
    escapeChar: '"',
    delimiter: format == 'csv' ? "," : "\t"
  };

  var data = Papa.unparse(arr, config);
  var mimeType = format === 'csv' ? 'text/csv' : 'text/tsv';
  var fileExtension = format === 'csv' ? 'csv' : 'tsv';
  var blob = new Blob([data], { type: mimeType + ';charset=utf-8;' });
  var fileUrl = null;

  if (navigator.msSaveBlob) {
    fileUrl = navigator.msSaveBlob(blob, fileName + '.' + fileExtension);
  } else {
    fileUrl = window.URL.createObjectURL(blob);
  }

  var tempLink = document.createElement('a');
  tempLink.href = fileUrl;
  tempLink.setAttribute('download', fileName + '.' + fileExtension);
  tempLink.click();
}


function convertBase64ToPDF(base64String,name) {
  const dataUrlPrefix = 'data:application/pdf;base64,';
  const base64Data = base64String.replace(dataUrlPrefix, '');

  const byteCharacters = atob(base64Data);
  const byteNumbers = new Array(byteCharacters.length);
  for (let i = 0; i < byteCharacters.length; i++) {
    byteNumbers[i] = byteCharacters.charCodeAt(i);
  }
  const byteArray = new Uint8Array(byteNumbers);

  const blob = new Blob([byteArray], { type: 'application/pdf' });

  const link = document.createElement('a');
  link.href = URL.createObjectURL(blob);
  link.download = 'invoice_'+name+'.pdf';

  link.click();

  URL.revokeObjectURL(link.href);
}

function removeFakePath(path){
    return path.substring(path.lastIndexOf("\\") + 1, path.length);
}

function scientificNumberReviver(value) {
  if (typeof value === 'string' && /^[-+]?(\d+(\.\d*)?|\.\d+)(e[-+]?\d+)$/i.test(value)) {
    return parseFloat(value);
  }
  value = parseInt(value);
  return value;
}