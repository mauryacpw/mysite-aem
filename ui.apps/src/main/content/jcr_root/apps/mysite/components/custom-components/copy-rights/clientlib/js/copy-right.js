
document.addEventListener("DOMContentLoaded", function () {

    const componentPath = document.getElementById("copy-rights");
    const path = componentPath.getAttribute("data-path");
         fetch(`/bin/copyRight?path=${path}`)
        .then(response => response.json())
        .then(data => {
            console.log(path);
            document.getElementById("componentText").innerText = data.componentText;
            document.getElementById("copyrightText").innerText = data.copyrightText;
        });

});
