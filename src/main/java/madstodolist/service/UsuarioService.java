package madstodolist.service;

import madstodolist.dto.UsuarioData;
import madstodolist.model.TipoPlan;
import madstodolist.model.Usuario;
import madstodolist.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UsuarioService {

    Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    public enum LoginStatus {LOGIN_OK, USER_NOT_FOUND, ERROR_PASSWORD}

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public LoginStatus login(String eMail, String password) {
        Usuario usuario = usuarioRepository.findByEmail(eMail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getPassword().equals(password)) {
            return LoginStatus.ERROR_PASSWORD;
        }
        return LoginStatus.LOGIN_OK;
    }

    @Transactional
    public UsuarioData registrar(UsuarioData usuarioData) {
        if (usuarioRepository.findByEmail(usuarioData.getEmail()).isPresent()) {
            throw new UsuarioServiceException("El usuario " + usuarioData.getEmail() + " ya está registrado");
        }
        if (usuarioData.getEmail() == null || usuarioData.getPassword() == null) {
            throw new UsuarioServiceException("El usuario debe tener email y password");
        }

        // Asignar 'user' como tipo de usuario por defecto si no se especifica
        if (usuarioData.getTipouser() == null) {
            usuarioData.setTipouser("user");
        }

        // Asignar plan por defecto si no se especifica en el formulario
        if (usuarioData.getPlan() == null) {
            usuarioData.setPlan(TipoPlan.GRATUITO);  // Se asigna "GRATUITO" como valor predeterminado
        }

        Usuario usuarioNuevo = modelMapper.map(usuarioData, Usuario.class);

        // Debugging para verificar el plan antes de guardar en la base de datos
        System.out.println("Plan asignado al usuario antes de guardar: " + usuarioNuevo.getPlan());

        usuarioNuevo = usuarioRepository.save(usuarioNuevo);

        return modelMapper.map(usuarioNuevo, UsuarioData.class);
    }


    @Transactional(readOnly = true)
    public UsuarioData findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(usuario -> modelMapper.map(usuario, UsuarioData.class))
                .orElse(null);  // Devolver null en lugar de lanzar una excepción
    }


    @Transactional(readOnly = true)
    public UsuarioData findById(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return modelMapper.map(usuario, UsuarioData.class);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long usuarioId) {
        return usuarioRepository.findById(usuarioId);
    }

    public List<UsuarioData> findAll() {
        return StreamSupport.stream(usuarioRepository.findAll().spliterator(), false)
                .map(usuario -> modelMapper.map(usuario, UsuarioData.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioData save(UsuarioData usuarioData) {
        Usuario usuario = modelMapper.map(usuarioData, Usuario.class);
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return modelMapper.map(usuarioGuardado, UsuarioData.class);
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public void actualizarUsuario(UsuarioData usuarioData) {
        Usuario usuarioExistente = usuarioRepository.findById(usuarioData.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuarioData.getEmail() != null) {
            usuarioExistente.setEmail(usuarioData.getEmail());
        }
        if (usuarioData.getNombre() != null) {
            usuarioExistente.setNombre(usuarioData.getNombre());
        }
        if (usuarioData.getApellidos() != null) {
            usuarioExistente.setApellidos(usuarioData.getApellidos());
        }
        if (usuarioData.getBio() != null) {
            usuarioExistente.setBio(usuarioData.getBio());
        }
        if (usuarioData.getTipouser() != null) {
            usuarioExistente.setTipouser(usuarioData.getTipouser());
        }

        usuarioRepository.save(usuarioExistente);
    }
}
