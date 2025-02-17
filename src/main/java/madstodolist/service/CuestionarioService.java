package madstodolist.service;

import madstodolist.model.Cuestionario;
import madstodolist.repository.CuestionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class CuestionarioService {
    @Autowired
    private CuestionarioRepository cuestionarioRepository;


    public Optional<Cuestionario> findById(Long id) {
        return cuestionarioRepository.findById(id);
    }
}

