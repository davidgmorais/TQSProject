function getLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition);
    } else {
        var s = "Geolocation is not supported by this browser.";
    }
}


function showPosition(position) {
    $("#myModal").modal("toggle");
    var x = "Latitude: " + position.coords.latitude +
        "<br>Longitude: " + position.coords.longitude;
}