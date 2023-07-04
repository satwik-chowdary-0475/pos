
//HELPER METHOD
function toJson($form){
    var serialized = $form.serializeArray();
//    console.log(serialized);
    var s = '';
    var data = {};
    for(s in serialized){
        data[serialized[s]['name']] = serialized[s]['value']
    }
    var json = JSON.stringify(data);
    return json;
}


function showError(message){
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


function writeFileData(arr){
	var config = {
		quoteChar: '',
		escapeChar: '',
		delimiter: "\t"
	};

	var data = Papa.unparse(arr, config);
    var blob = new Blob([data], {type: 'text/tsv;charset=utf-8;'});
    var fileUrl =  null;

    if (navigator.msSaveBlob) {
        fileUrl = navigator.msSaveBlob(blob, 'download.tsv');
    } else {
        fileUrl = window.URL.createObjectURL(blob);
    }
    var tempLink = document.createElement('a');
    tempLink.href = fileUrl;
    tempLink.setAttribute('download', 'download.tsv');
    tempLink.click();
}

function convertBase64ToPDF(base64String,name) {
  // Remove data URL prefix
  const dataUrlPrefix = 'data:application/pdf;base64,';
  const base64Data = base64String.replace(dataUrlPrefix, '');

  // Convert Base64 to Uint8Array
  const byteCharacters = atob(base64Data);
  const byteNumbers = new Array(byteCharacters.length);
  for (let i = 0; i < byteCharacters.length; i++) {
    byteNumbers[i] = byteCharacters.charCodeAt(i);
  }
  const byteArray = new Uint8Array(byteNumbers);

  // Create Blob from Uint8Array
  const blob = new Blob([byteArray], { type: 'application/pdf' });

  // Create a temporary link element
  const link = document.createElement('a');
  link.href = URL.createObjectURL(blob);
  link.download = 'invoice_'+name+'.pdf';
  // Trigger the download
  link.click();

  // Clean up
  URL.revokeObjectURL(link.href);
}
