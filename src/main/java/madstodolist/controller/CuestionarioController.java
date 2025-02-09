package madstodolist.controller;

import madstodolist.model.Cuestionario;
import madstodolist.model.Pregunta;
import madstodolist.repository.CuestionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/cuestionario")
public class CuestionarioController {

    @Autowired
    private CuestionarioRepository cuestionarioRepository;


    @GetMapping("/{id}")
    public ResponseEntity<Cuestionario> mostrarCuestionario(@PathVariable Long id) {
        Optional<Cuestionario> cuestionarioOpt = cuestionarioRepository.findById(id);
        if (!cuestionarioOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Cuestionario cuestionario = cuestionarioOpt.get();
        return ResponseEntity.ok(cuestionario);
    }

    @GetMapping("/{id}/pregunta/{index}")
    public ResponseEntity<Pregunta> obtenerPregunta(@PathVariable Long id, @PathVariable int index) {
        Optional<Cuestionario> cuestionarioOpt = cuestionarioRepository.findById(id);
        if (!cuestionarioOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        List<Pregunta> preguntas = cuestionarioOpt.get().getPreguntas();
        if (index < 0 || index >= preguntas.size()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(preguntas.get(index));
    }

    @PostMapping("/guardar-cuestionario")
    public ResponseEntity<String> guardarCuestionario(@RequestParam Map<String, String> respuestas) {
        // Forma de guardar las respuestas (provisional , se puede usar la BD)
        respuestas.forEach((preguntaId, respuesta) ->
                System.out.println("Pregunta " + preguntaId + ": " + respuesta));

        return ResponseEntity.ok("¡Cuestionario enviado con éxito!");
    }
}


