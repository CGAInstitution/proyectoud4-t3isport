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
public class UsuarioPlanId implements java.io.Serializable {
    private static final long serialVersionUID = -576194864708769860L;
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UsuarioPlanId entity = (UsuarioPlanId) o;
        return Objects.equals(this.planId, entity.planId) &&
                Objects.equals(this.usuarioId, entity.usuarioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planId, usuarioId);
    }

}