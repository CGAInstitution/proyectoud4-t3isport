package madstodolist.controller;

import madstodolist.dto.RegistroData;
import madstodolist.model.Contenido;
import madstodolist.model.PlanesEntrenamiento;
import madstodolist.service.ContenidoService;
import madstodolist.service.PlanesEntrenamientoService;
import madstodolist.service.UsuarioPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class PlanEntrenamientoController {

    @Autowired
    private PlanesEntrenamientoService planesEntrenamientoService;

    @Autowired
    private ContenidoService contenidoService;

    @GetMapping("{id}/plan_entrenamiento")
    public String registroForm(@PathVariable Long id, Model model) {
        PlanesEntrenamiento planEntrenamiento = planesEntrenamientoService.obtenerPlan(id);
        model.addAttribute("planEntrenamiento", planEntrenamiento);

        List<Contenido> contenidos = contenidoService.obtenerContenidosPlan(planEntrenamiento);
        model.addAttribute("contenidos", contenidos);
        for (Contenido contenido : contenidos) {
            if (contenido.getUrl().endsWith(".txt")) {
                try {
                    // Leer el archivo de texto y pasarlo al modelo
                    String fileContent = new String(Files.readAllBytes(Paths.get("/src/main/resources/static/docs/" + contenido.getUrl())));

                    contenido.setContent(fileContent); // Establece el contenido en el objeto Contenido
                } catch (IOException e) {
                    e.printStackTrace();  // Manejar errores de lectura de archivos
                }
            }
        }
        return "planEntrenamiento";
    }

}
