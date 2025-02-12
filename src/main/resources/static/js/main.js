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
        btnNewTicket.addEventListener('click', function () {
            ticketUsuario.style.top = '15%';
        });
    }

    if (btnEnviarTicket) {
        btnEnviarTicket.addEventListener('click', function () {
            ticketUsuario.style.transition = 'top 0.5s';
            ticketUsuario.style.top = '-150%';
        });
    }
    if (document.getElementById("ticketForm")) {
        document.getElementById("ticketForm").addEventListener("submit", function (event) {
            event.preventDefault();

            const formData = new FormData();
            formData.append("horaContacto", document.getElementById("horaContacto").value);
            formData.append("tema", document.getElementById("tema").value);
            formData.append("asunto", document.getElementById("asunto").value);
            formData.append("descripcion", document.getElementById("descripcion").value);
            formData.append("archivo", document.getElementById("archivo").files[0]);

            const userIdElement = document.getElementById("userId");
            if (!userIdElement) {
                alert("Error: No se pudo obtener el ID del usuario.");
                return;
            }
            const userId = userIdElement.innerText.trim();

            fetch(`/tickets/${userId}`, {
                method: "POST",
                body: formData
            }).then(response => {
                if (response.ok) {
                    alert("Ticket enviado con éxito.");
                    window.location.reload();
                } else {
                    alert("Error al enviar el ticket.");
                }
            }).catch(error => {
                console.error("Error:", error);
                alert("Hubo un problema con el envío del ticket.");
            });
        });
    }
};

// FUNCION PARA EL FOOTER
document.addEventListener("DOMContentLoaded", function () {
    const footer = document.querySelector("footer");
    if (!window.location.pathname.includes("/login")) {
        footer.style.position = window.innerHeight > document.body.clientHeight ? "absolute" : "relative";
        footer.style.setProperty("margin-top", "50px", "important");
    } else {
        footer.style.position = "absolute";
    }
});