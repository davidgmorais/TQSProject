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
        "<br>Longitude: " + position.coords.latitude;
}

function initMap() {
    var mapProp= {
        center:new google.maps.LatLng(position.coords.latitude, position.coords.longitude),
        zoom:8,
    };
    var map = new google.maps.Map(document.getElementById("googleMap"),mapProp);
}