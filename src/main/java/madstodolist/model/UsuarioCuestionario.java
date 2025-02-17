package madstodolist.model;

import javax.persistence.*;

import madstodolist.dto.UsuarioData;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "usuario_cuestionario")
public class UsuarioCuestionario {
    @EmbeddedId
    private UsuarioCuestionarioId id;

    @MapsId("usuarioId")
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @MapsId("cuestionarioId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "cuestionario_id", nullable = false)
    private Cuestionario cuestionario;

    @Column(name = "fecha_completado")
    private Instant fechaCompletado;

    public UsuarioCuestionarioId getId() {
        return id;
    }

    public void setId(UsuarioCuestionarioId id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Cuestionario getCuestionario() {
        return cuestionario;
    }

    public void setCuestionario(Cuestionario cuestionario) {
        this.cuestionario = cuestionario;
    }

    public Instant getFechaCompletado() {
        return fechaCompletado;
    }

    public void setFechaCompletado(Instant fechaCompletado) {
        this.fechaCompletado = fechaCompletado;
    }

}