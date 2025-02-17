package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.model.CodigoDescuento;
import madstodolist.model.Usuario;
import madstodolist.repository.UsuarioRepository;
import madstodolist.service.CodigoDescuentoService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class CodigosController {

    @Autowired
    CodigoDescuentoService codigoDescuentoService;

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/panelAdmin/{id}/listaCodigos")
    public String listaCodigos(@PathVariable Long id, Model model, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");

        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        List<CodigoDescuento> codigos = codigoDescuentoService.listarCodigos();
        model.addAttribute("codigos", codigos);
        model.addAttribute("userId", id);
        return "listaCodigos";
    }

    @PostMapping("/panelAdmin/{id}/addCodigo")
    public String addUser(@RequestParam("codigo") String codigo,
                          @RequestParam("descuento") BigDecimal descuento,
                          @RequestParam("usuario") Long usuario,
                          @PathVariable Long id,
                          RedirectAttributes redirectAttributes, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }
        try {
            codigoDescuentoService.buscarPorCodigo(codigo);
            redirectAttributes.addFlashAttribute("error", "El código ya existe.");
            return "redirect:/panelAdmin/" + id + "/listaCodigos";
        } catch (RuntimeException e) {
            // Código no encontrado, podemos crearlo
        }

        Usuario usuarionuevo = usuarioService.buscarPorId(usuario).get();
        codigoDescuentoService.nuevoCodigo(codigo , descuento, usuarionuevo);
        redirectAttributes.addFlashAttribute("success", "Código creado correctamente.");
        return "redirect:/panelAdmin/" + id + "/listaCodigos";
    }
}
