package madstodolist.controller;

import madstodolist.model.Cuestionario;
import madstodolist.repository.CuestionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/cuestionario")
public class CuestionarioController {

    @Autowired
    private CuestionarioRepository cuestionarioRepository;

    @GetMapping("/{id}")
    public String mostrarCuestionario(@PathVariable Long id, Model model) {
        Optional<Cuestionario> cuestionarioOpt = cuestionarioRepository.findById(id);

        if (!cuestionarioOpt.isPresent()) {
            return "error"; // Puedes crear una página de error
        }

        Cuestionario cuestionario = cuestionarioOpt.get();

        // **Solución: Forzar carga de preguntas antes de enviarlas a la vista**
        cuestionario.getPreguntas().size(); // Esto evitará el LazyInitializationException

        model.addAttribute("cuestionario", cuestionario);
        model.addAttribute("preguntas", cuestionario.getPreguntas()); // Asegurar que Thymeleaf tenga acceso

        return "cuestionario";
    }
}

