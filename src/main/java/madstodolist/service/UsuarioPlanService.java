package madstodolist.service;

import madstodolist.model.UsuarioPlan;
import madstodolist.repository.UsuarioPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioPlanService {

    @Autowired
    private UsuarioPlanRepository usuarioPlanRepository;

    public List<UsuarioPlan> obtenerPlanesUsuario(Long usuarioId) {
        return usuarioPlanRepository.findByUsuarioId(usuarioId);
    }
}

