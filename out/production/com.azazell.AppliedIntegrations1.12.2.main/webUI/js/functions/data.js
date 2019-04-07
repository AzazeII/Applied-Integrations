// Create ajax request
$.ajax({
    url: "/json",
    type: 'GET',
    success: function(response) {
        console.log(response);
        //map your data here -  to  series parameter in your highchart method.
    },
    error: function(error) {
        errorFunction(error);
    }
});

function errorFunction(error){
    console.log(error);
}