package madstodolist.dto;

import java.util.Objects;

// Data Transfer Object para la clase Usuario
public class UsuarioData {

    private Long id;
    private String email;
    private String nombre;
    private String password;
    private String tipouser;
    private String foto;
    private String apellidos;
    private String bio;

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTipouser() {
        return tipouser;
    }

    public void setTipouser(String tipouser) {
        this.tipouser = tipouser;
    }

    // Sobreescribimos equals y hashCode para que dos usuarios sean iguales
    // si tienen el mismo ID (ignoramos el resto de atributos)

    public UsuarioData() {
    }

    public UsuarioData(Long id, String email, String nombre, String password, String tipouser, String foto, String apellidos, String bio) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.password = password;
        this.tipouser = tipouser;
        this.foto = foto;
        this.apellidos = apellidos;
        this.bio = bio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioData)) return false;
        UsuarioData that = (UsuarioData) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "UsuarioData{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                ", password='" + password + '\'' +
                ", tipouser='" + tipouser + '\'' +
                ", foto='" + foto + '\'' +
                '}';
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}