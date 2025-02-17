package madstodolist.dto;

import madstodolist.model.MensajeTicket;

public class MensajeTicketData {
    private Long id;
    private String contenido;
    private Long usuarioId;
    private String usuarioNombre;
    private String fechaCreacion;

    public MensajeTicketData(MensajeTicket mensajeTicket) {
        this.id = mensajeTicket.getId();
        this.contenido = mensajeTicket.getMensaje();
        this.usuarioId = mensajeTicket.getUsuario().getId();
        this.usuarioNombre = mensajeTicket.getUsuario().getNombre();
        this.fechaCreacion = mensajeTicket.getFechaEnvio().toString();
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public String getContenido() {
        return contenido;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }
}
