package madstodolist.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Lob
    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "horaContacto", nullable = false)
    private String horaContacto;

    @Column(name = "tema", nullable = false)
    private String tema;

    @Column(name = "asunto", nullable = false)
    private String asunto;

    @Column(name = "estado", nullable = false)
    private Boolean estado = false;

    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }


    public String getHoraContacto() {
        return horaContacto;
    }

    public void setHoraContacto(String horaContacto) {
        this.horaContacto = horaContacto;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }



    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", usuario=" + usuario +
                ", descripcion='" + descripcion + '\'' +
                ", horaContacto='" + horaContacto + '\'' +
                ", tema='" + tema + '\'' +
                ", asunto='" + asunto + '\'' +
                ", estado=" + estado +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }


    public Ticket() {
        this.fechaCreacion = Date.from(Instant.now());
    }

    public Ticket(Usuario usuario, Long id, Date fechaCreacion, Boolean estado, String descripcion) {
        this.usuario = usuario;
        this.id = id;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
        this.descripcion = descripcion;

    }
}