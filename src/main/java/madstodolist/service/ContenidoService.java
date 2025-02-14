package madstodolist.service;

import madstodolist.model.Contenido;
import madstodolist.model.PlanesEntrenamiento;
import madstodolist.repository.ContenidoRepository;
import madstodolist.repository.UsuarioPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContenidoService {

    @Autowired
    private ContenidoRepository contenidoRepository;

    public List<Contenido> obtenerContenidosPlan(PlanesEntrenamiento plan) {
        return contenidoRepository.findByPlan(plan);
    }
}