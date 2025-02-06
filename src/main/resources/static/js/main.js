window.onload = function () {
    console.log("JS cargado...")
    var mensajeDiv = document.getElementById('mensaje');
    if (mensajeDiv) { // Solo ejecutar si el div existe
        setTimeout(function () {
            mensajeDiv.style.top = '20%';
            setTimeout(function () {
                mensajeDiv.style.top = '-150%';
            }, 5000);
        }, 100);
    }
};