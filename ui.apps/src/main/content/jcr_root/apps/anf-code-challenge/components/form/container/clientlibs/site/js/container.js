$(document).ready(function() {
    $(document).on('submit','form#anf-user-form',function(e) {
       e.preventDefault();

       var userData = {
        'firstName': $('[name="first-name"]').val(),
        'lastName': $('[name="last-name"]').val(),
        'age': $('[name="age"]').val(),
        'country': $('[name="country"]').val()
       }
       $.ajax({
          url: "/etc/age.json",
          success: function(data) {
            if( (Number(userData.age) >= Number(data.minAge)) && 
                (Number(userData.age) <= Number(data.maxAge)) )
                postUserDetails(userData);
            else
                alert("You are not eligible");
          },
          error: function() {
             alert('Something went wrong, Please try later');
          }
        });
    });

    function postUserDetails(userData) {
        var url = location.pathname.replace(".html", "") + ".submitUserDetails.json";
        $.ajax({
          url: url,
          method: 'POST',
          data: userData,
          success: function(data) {
			if(data.result == 'success')
                alert("Thank you for submitting the details");
            else
                alert('Something went wrong, Please try later');
          },
          error: function() {
             alert('Something went wrong, Please try later');
          }
        });

    }

});
