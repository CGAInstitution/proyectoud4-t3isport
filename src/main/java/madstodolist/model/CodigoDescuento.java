package madstodolist.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "codigo_descuento")
public class CodigoDescuento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "codigo", nullable = false, length = 50)
    private String codigo;

    @Column(name = "descuento", nullable = false, precision = 5, scale = 2)
    private BigDecimal descuento;

    @JoinColumn(name = "usuario", nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private Usuario usuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}