package madstodolist.service;

import madstodolist.model.UsuarioPlan;
import madstodolist.repository.UsuarioPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UsuarioPlanService {

    @Autowired
    private UsuarioPlanRepository usuarioPlanRepository;

    @Transactional
    public void guardarUsuarioPlan(UsuarioPlan usuarioPlan) {
        usuarioPlanRepository.save(usuarioPlan);
    }

    public List<UsuarioPlan> obtenerPlanesUsuario(Long usuarioId) {
        return usuarioPlanRepository.findByUsuarioId(usuarioId);
    }

    public List<UsuarioPlan> findAllUsuarioPlanes() {
        return usuarioPlanRepository.findAll();
    }
}


