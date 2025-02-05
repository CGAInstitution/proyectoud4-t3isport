package madstodolist.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "usuario_cuestionario")
public class UsuarioCuestionario {
    @EmbeddedId
    private UsuarioCuestionarioId id;

    @MapsId("usuarioId")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @MapsId("cuestionarioId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "cuestionario_id", nullable = false)
    private Cuestionario cuestionario;

    @ColumnDefault("CURRENT_TIMESTAMP")
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