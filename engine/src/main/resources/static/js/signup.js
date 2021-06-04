function signupStoreForm() {
    var signup = document.getElementById("signupStoreForm");
    var select = document.getElementById("select");
        select.style.display = "none";
        signup.style.display = "block";

}

function signupRiderForm() {
    var image = document.getElementById("image");
    image.classList.remove("bg-register-image");
    image.classList.add("bg-register-image-2");
    var signup = document.getElementById("signupRiderForm");
    var select = document.getElementById("select");
    select.style.display = "none";
    signup.style.display = "block";

}