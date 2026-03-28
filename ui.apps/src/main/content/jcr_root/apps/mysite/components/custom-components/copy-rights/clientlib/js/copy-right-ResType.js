
document.addEventListener("DOMContentLoaded", function () {

    const componentPath = document.getElementById("copy-rights");
    const path = componentPath.getAttribute("data-path");
    fetch(`http://localhost:4502${path}`)
        .then(function(response) {
            return response.json();
        })
        .then(function(data) {

            if(data.componentText){
                document.getElementById("componentText").textContent = data.componentText;
            }

            if(data.copyrightText){
                document.getElementById("copyrightText").textContent = data.copyrightText;
            }

        })
        .catch(function(error){
            console.error("Error fetching copyright data:", error);
        });




});
