package madstodolist.service;

import madstodolist.model.CodigoDescuento;
import madstodolist.model.Usuario;
import madstodolist.repository.CodigoDescuentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CodigoDescuentoService {
    @Autowired
    private CodigoDescuentoRepository codigoDescuentoRepository;

    @Autowired
    private UsuarioService usuarioService;

    public List<CodigoDescuento> listarCodigos() {
        return codigoDescuentoRepository.findAll();
    }

    public CodigoDescuento buscarPorCodigo(String codigo) {
        System.out.println(codigo);
        System.out.println(codigoDescuentoRepository.findByCodigo(codigo).get().getCodigo());
        return codigoDescuentoRepository.findByCodigo(codigo).isPresent() ? codigoDescuentoRepository.findByCodigo(codigo).get() : null;
    }

    @Transactional
    public void nuevoCodigo(String codigo, BigDecimal descuento, Usuario usuario) {

        usuario = usuarioService.buscarPorId(usuario.getId()).get();
        CodigoDescuento codigoDescuento = new CodigoDescuento();
        codigoDescuento.setCodigo(codigo);
        codigoDescuento.setDescuento(descuento);
        codigoDescuento.setUsuario(usuario);
        codigoDescuentoRepository.save(codigoDescuento);
    }
}
