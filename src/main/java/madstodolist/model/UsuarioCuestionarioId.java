package madstodolist.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.hibernate.Hibernate;

import java.util.Objects;

@Embeddable
public class UsuarioCuestionarioId implements java.io.Serializable {
    private static final long serialVersionUID = 3164701459535613465L;
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "cuestionario_id", nullable = false)
    private Long cuestionarioId;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getCuestionarioId() {
        return cuestionarioId;
    }

    public void setCuestionarioId(Long cuestionarioId) {
        this.cuestionarioId = cuestionarioId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UsuarioCuestionarioId entity = (UsuarioCuestionarioId) o;
        return Objects.equals(this.cuestionarioId, entity.cuestionarioId) &&
                Objects.equals(this.usuarioId, entity.usuarioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cuestionarioId, usuarioId);
    }

}