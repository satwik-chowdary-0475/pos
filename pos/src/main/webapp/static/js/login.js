const signUpButton = document.getElementById('signUp');
const signInButton = document.getElementById('signIn');
const container = document.getElementById('container');

signUpButton.addEventListener('click', () => {
	container.classList.add("right-panel-active");
});

signInButton.addEventListener('click', () => {
	container.classList.remove("right-panel-active");
});

function validateForm(event) {
        event.preventDefault(); // Prevent the form from submitting initially

        var email = document.getElementById("email").value;
        var password = document.getElementById("password").value;

        if (email.trim() === "" || password.trim() === "") {
            $.notify("Please fill in the details","error"); // Show an error message
        } else {
            document.getElementById("init-form").submit(); // Submit the form
        }
    }
