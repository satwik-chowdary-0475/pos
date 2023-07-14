$(document).ready(function() {
  var $cardProfile = $(".card-profile");
  var $displayPicture = $(".display-picture");

  $displayPicture.on("click", function() {
    $cardProfile.toggleClass("hidden");
  });
});