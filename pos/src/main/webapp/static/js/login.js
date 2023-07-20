const signUpButton = document.getElementById('signUp');
const signInButton = document.getElementById('signIn');
const container = document.getElementById('container');

signUpButton.addEventListener('click', () => {
	container.classList.add("right-panel-active");
});

signInButton.addEventListener('click', () => {
	container.classList.remove("right-panel-active");
});

function validateEmail(email){
    var pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return pattern.test(email);
}

function validatePassword(password){
    var pattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
    return pattern.test(password);
}

function showError(message,info){
    if(info!=null && info.length > 0)
    message = message + ' : ' + info;
    showErrorNotification(message);

}

function validateForm(event) {
        event.preventDefault();

        var email = document.getElementById("email").value;
        var password = document.getElementById("password").value;

        if (email.trim() === "" || password.trim() === "") {
            showError("Please fill in the details");
        }
        else if(!validateEmail(email.trim())){
            showError("Please enter a valid email","Email must contain @ and domain name.");
        }
        else if(!validatePassword(password.trim())) {
            showError("Please enter a valid password","Password must be at least 8 characters long and include at least one letter, one number, and one special character");
        }
        else{
            signup(event);
        }
}
function getBaseUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content");
    return baseUrl;
}

function getLoginUrl(){
    return getBaseUrl() + '/session/login';
}

function getSignupUrl(){
    return getBaseUrl() + '/signup';
}

function login(){
    var url = getLoginUrl();
    var $form = $("#login-form");
    var json = toJson($form);
    $.ajax({
           url: url,
           type: 'POST',
           data: json,
           headers: {
               'Content-Type': 'application/json'
           },
           success: function(response) {
                window.location.href = getBaseUrl() + "/home";
       },
           error:function(error){
                handleAjaxError(error);
           }
        })
}

function resetFormValues(){
    container.classList.remove("right-panel-active");
    $("#init-form input[name=email]").val('');
    $("#init-form input[name=password]").val('');
}

function signup(event){

    var url = getSignupUrl();
    var $form = $("#init-form");
    var json = toJson($form);
    $.ajax({
               url: url,
               type: 'POST',
               data: json,
               headers: {
                   'Content-Type': 'application/json'
               },
               success: function(response) {
                    resetFormValues();
                    showSuccessNotification("User added successfully");
           },
               error:function(error){
                    handleAjaxError(error);
               }
            })
}

function init(){
    $("#login").click(login);
    $("#signup").click(validateForm);
}

$(document).ready(init);



