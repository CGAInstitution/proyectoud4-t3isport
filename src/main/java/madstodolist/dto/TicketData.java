package madstodolist.dto;

import madstodolist.model.MensajeTicket;
import madstodolist.model.Ticket;

import java.util.List;
import java.util.stream.Collectors;

public class TicketData {
    private Long id;
    private String descripcion;
    private String horaContacto;
    private String tema;
    private String asunto;
    private Boolean estado;
    private String fechaCreacion;
    private List<MensajeTicketData> mensajes;

    public TicketData(Ticket ticket) {
        this.id = ticket.getId();
        this.descripcion = ticket.getDescripcion();
        this.horaContacto = ticket.getHoraContacto();
        this.tema = ticket.getTema();
        this.asunto = ticket.getAsunto();
        this.estado = ticket.getEstado();
        this.fechaCreacion = ticket.getFechaCreacion().toString();
        this.mensajes = ticket.getMensajes().stream()
                              .map(MensajeTicketData::new)
                              .collect(Collectors.toList());
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getHoraContacto() {
        return horaContacto;
    }

    public String getTema() {
        return tema;
    }

    public String getAsunto() {
        return asunto;
    }

    public Boolean getEstado() {
        return estado;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public List<MensajeTicketData> getMensajes() {
        return mensajes;
    }

    @Override
    public String toString() {
        return "TicketData{" +
                "id=" + id +
                ", descripcion='" + descripcion + '\'' +
                ", horaContacto='" + horaContacto + '\'' +
                ", tema='" + tema + '\'' +
                ", asunto='" + asunto + '\'' +
                ", estado=" + estado +
                ", fechaCreacion='" + fechaCreacion + '\'' +
                ", mensajes=" + mensajes +
                '}';
    }
}