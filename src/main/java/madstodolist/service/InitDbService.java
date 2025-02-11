package madstodolist.service;

//import madstodolist.repository.TareaRepository;

import madstodolist.model.Usuario;
import madstodolist.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
// Se ejecuta solo si el perfil activo es 'dev'
@Profile("dev")
public class InitDbService {

    @Autowired
    private UsuarioRepository usuarioRepository;
//    @Autowired
//    private TareaRepository tareaRepository;

    // Se ejecuta tras crear el contexto de la aplicación
    // para inicializar la base de datos
    @PostConstruct
    public void initDatabase() {
        Usuario usuario = new Usuario("user@ua");
        usuario.setId(1L); // Manually assign an ID
        usuario.setNombre("Usuario Ejemplo");
        usuario.setApellidos("Ejemplo Apellidos"); // Set apellidos
        usuario.setPassword("123");
        usuario.setBio("Bio de ejemplo");
        usuario.setFoto("foto.jpg");
        usuario.setTipouser("admin");
        usuarioRepository.save(usuario);

        Usuario usuario2 = new Usuario("a@ua");
        usuario2.setId(2L); // Manually assign an ID
        usuario2.setNombre("Otro Usuario");
        usuario2.setApellidos("Otro Apellidos"); // Set apellidos
        usuario2.setPassword("123");
        usuario2.setBio("Otra bio de ejemplo");
        usuario2.setFoto("otra_foto.jpg");
        usuario2.setTipouser("user");
        usuarioRepository.save(usuario2);
//        Tarea tarea1 = new Tarea(usuario, "Lavar coche");
//        tareaRepository.save(tarea1);
//
//        Tarea tarea2 = new Tarea(usuario, "Renovar DNI");
//        tareaRepository.save(tarea2);
    }

}
