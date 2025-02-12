package madstodolist.controller;

import madstodolist.model.Cuestionario;
import madstodolist.repository.CuestionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
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

    @PostMapping("/guardar-cuestionario")
    public String guardarCuestionario(@RequestParam Map<String, String> respuestas, Model model) {
        //Depuracion respuestas
        respuestas.forEach((preguntaId, respuestaId) ->
                System.out.println("Pregunta: " + preguntaId + ": Respuesta " + respuestaId));


        model.addAttribute("mensaje", "¡Cuestionario enviado con éxito!");

        // Se pueden coger los valores de preguntaID y Respuesta ID para hacer un algoritmo sencillo y asignar
        //con el valor en la tabla usuario_plan la ID del usuario que hace el cuestionario y un plan

        // el redirect es provisional para que no de error , hay que cambiarlo a /hubplanes

        return "redirect:/userHub";
    }

}

