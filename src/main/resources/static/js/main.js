window.onload = function () {
    console.log("JS cargado...");
    var mensajeDiv = document.getElementById('mensaje');
    var ticketUsuario = document.getElementById('ticketUsuario');
    var btnNewTicket = document.getElementById('btnNewTicket');
    var btnEnviarTicket = document.getElementById('btnEnviarTicket');

    if (mensajeDiv) { // Solo ejecutar si el div existe
        setTimeout(function () {
            mensajeDiv.style.top = '20%';
            setTimeout(function () {
                mensajeDiv.style.top = '-150%';
            }, 5000);
        }, 100);
    }

    if (btnNewTicket) {
        btnNewTicket.addEventListener('click', function() {
            ticketUsuario.style.top = '15%';
        });
    }

    if (btnEnviarTicket) {
        btnEnviarTicket.addEventListener('click', function() {
            ticketUsuario.style.transition = 'top 0.5s';
            ticketUsuario.style.top = '-150%';
        });
    }

    // Cerrar el mensaje

};