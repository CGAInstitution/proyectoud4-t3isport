package madstodolist.service;

import madstodolist.model.UsuarioCuestionario;
import madstodolist.repository.UsuarioCuestionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UsuarioCuestionarioService {
    @Autowired
    private UsuarioCuestionarioRepository usuarioCuestionarioRepository;

    @Transactional
    public void guardarUsuarioCuestionario(UsuarioCuestionario usuarioCuestionario) {
        usuarioCuestionarioRepository.save(usuarioCuestionario);
    }
}

