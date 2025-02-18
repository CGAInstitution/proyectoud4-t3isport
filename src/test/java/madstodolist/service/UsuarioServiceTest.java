package madstodolist.service;

import madstodolist.dto.UsuarioData;
import madstodolist.model.TipoPlan;
import madstodolist.repository.UsuarioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Método auxiliar para generar emails aleatorios
    private String generarEmailAleatorio() {
        return "testuser" + System.currentTimeMillis() + "@mail.com";
    }

    @Test
    public void servicioLoginUsuario() {
        // GIVEN: Creamos un usuario nuevo aleatorio
        UsuarioData usuario = new UsuarioData();
        String emailAleatorio = generarEmailAleatorio();
        usuario.setEmail(emailAleatorio);
        usuario.setNombre("Usuario Test");
        usuario.setPassword("123456");
        usuario.setPlan(TipoPlan.PREMIUM);
        usuarioService.registrar(usuario);

        // WHEN: Intentamos login con credenciales correctas
        UsuarioService.LoginStatus loginStatus1 = usuarioService.login(emailAleatorio, "123456");

        // Intentamos login con credenciales incorrectas
        UsuarioService.LoginStatus loginStatus2 = usuarioService.login(emailAleatorio, "wrongpassword");

        // THEN: Verificar los resultados esperados
        assertThat(loginStatus1).isEqualTo(UsuarioService.LoginStatus.LOGIN_OK);
        assertThat(loginStatus2).isEqualTo(UsuarioService.LoginStatus.ERROR_PASSWORD);
    }

    @Test
    public void servicioRegistroUsuario() {
        // GIVEN: Datos de usuario aleatorio
        UsuarioData usuario = new UsuarioData();
        String emailAleatorio = generarEmailAleatorio();
        usuario.setEmail(emailAleatorio);
        usuario.setPassword("password123");
        usuario.setPlan(TipoPlan.GOLD);

        // WHEN: Registramos el usuario
        usuarioService.registrar(usuario);

        // THEN: Verificamos que se ha guardado correctamente
        UsuarioData usuarioBaseDatos = usuarioService.findByEmail(emailAleatorio);
        assertThat(usuarioBaseDatos).isNotNull();
        assertThat(usuarioBaseDatos.getEmail()).isEqualTo(emailAleatorio);
        assertThat(usuarioBaseDatos.getPlan()).isEqualTo(TipoPlan.GOLD);
    }

    @Test
    public void servicioRegistroUsuarioExcepcionConNullPassword() {
        // WHEN, THEN: Intentamos registrar un usuario con un password null, debe lanzar excepción
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail(generarEmailAleatorio());

        Assertions.assertThrows(UsuarioServiceException.class, () -> {
            usuarioService.registrar(usuario);
        });
    }

    @Test
    public void servicioRegistroUsuarioExcepcionConEmailRepetido() {
        // GIVEN: Creamos un usuario nuevo
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@test.com");
        usuario.setPassword("password123");
        usuario.setPlan(TipoPlan.GRATUITO);
        usuarioService.registrar(usuario);

        // THEN: Intentamos registrar otro usuario con el mismo email, debe lanzar excepción
        UsuarioData usuarioDuplicado = new UsuarioData();
        usuarioDuplicado.setEmail("user@test.com");
        usuarioDuplicado.setPassword("password123");

        Assertions.assertThrows(UsuarioServiceException.class, () -> {
            usuarioService.registrar(usuarioDuplicado);
        });
    }

    @Test
    public void servicioRegistroUsuarioDevuelveUsuarioConId() {
        // WHEN: Registramos un usuario aleatorio
        UsuarioData usuario = new UsuarioData();
        String emailAleatorio = generarEmailAleatorio();
        usuario.setEmail(emailAleatorio);
        usuario.setPassword("12345678");
        usuario.setPlan(TipoPlan.GOLD);

        UsuarioData usuarioNuevo = usuarioService.registrar(usuario);

        // THEN: Se debe generar un ID válido
        assertThat(usuarioNuevo.getId()).isNotNull();

        // Verificamos que el usuario guardado en BD es el mismo que el registrado
        UsuarioData usuarioBD = usuarioService.findById(usuarioNuevo.getId());
        assertThat(usuarioBD).isEqualTo(usuarioNuevo);
        assertThat(usuarioBD.getPlan()).isEqualTo(TipoPlan.GOLD);
    }

    @Test
    public void servicioConsultaUsuarioDevuelveUsuario() {
        // GIVEN: Registramos un usuario nuevo
        UsuarioData usuario = new UsuarioData();
        String emailAleatorio = generarEmailAleatorio();
        usuario.setEmail(emailAleatorio);
        usuario.setPassword("securepassword");
        usuario.setNombre("Test Usuario");
        usuarioService.registrar(usuario);

        // WHEN: Recuperamos el usuario por email
        UsuarioData usuarioRecuperado = usuarioService.findByEmail(emailAleatorio);

        // THEN: Verificamos que los datos coincidan
        assertThat(usuarioRecuperado).isNotNull();
        assertThat(usuarioRecuperado.getEmail()).isEqualTo(emailAleatorio);
        assertThat(usuarioRecuperado.getNombre()).isEqualTo("Test Usuario");
    }
}
