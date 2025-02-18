package madstodolist.controller;

import madstodolist.dto.LoginData;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;  // Simulación del servicio

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testMostrarFormularioLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())  // Espera un 200 OK
                .andExpect(view().name("formLogin"));  // Verifica que carga la vista de login
    }

    @Test
    public void testLoginUsuarioCorrecto() throws Exception {
        // Simulamos un usuario válido
        when(usuarioService.login("testuser@mail.com", "password123"))
                .thenReturn(UsuarioService.LoginStatus.LOGIN_OK);

        LoginData loginData = new LoginData();
        loginData.seteMail("testuser@mail.com");
        loginData.setPassword("password123");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("eMail", loginData.geteMail())
                        .param("password", loginData.getPassword()))
                .andExpect(status().is3xxRedirection())  // Redirige después de login exitoso
                .andExpect(redirectedUrlPattern("/usuarios/*/userhub")); // Verifica que la redirección es correcta
    }

    @Test
    public void testLoginUsuarioIncorrecto() throws Exception {
        when(usuarioService.login("wronguser@mail.com", "wrongpassword"))
                .thenReturn(UsuarioService.LoginStatus.ERROR_PASSWORD);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("eMail", "wronguser@mail.com")
                        .param("password", "wrongpassword"))
                .andExpect(status().isOk())  // Mantiene en la misma página
                .andExpect(view().name("formLogin"))  // Carga nuevamente el formulario
                .andExpect(model().attributeExists("error")); // Verifica que el mensaje de error existe
    }
}
