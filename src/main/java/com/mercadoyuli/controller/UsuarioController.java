package com.mercadoyuli.controller;

import com.mercadoyuli.model.PedidoEntity;
import com.mercadoyuli.model.Usuario;
import com.mercadoyuli.service.PedidoService;
import com.mercadoyuli.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PedidoService pedidoService;

    public UsuarioController(UsuarioService usuarioService, PedidoService pedidoService) {
        this.usuarioService = usuarioService;
        this.pedidoService = pedidoService;
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute Usuario usuario,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            String primerError = bindingResult.getAllErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("mensajeRegistro", "ERROR:" + primerError);
            return "redirect:/";
        }

        String resultado = usuarioService.registrar(usuario);
        switch (resultado) {
            case "OK" -> redirectAttributes.addFlashAttribute("mensajeRegistro",
                    "OK:Cuenta creada exitosamente. Ya puedes iniciar sesion con tu correo y contrasena.");
            case "EMAIL_EXISTE" -> redirectAttributes.addFlashAttribute("mensajeRegistro",
                    "ERROR:El correo electronico ya esta registrado. Usa otro correo o inicia sesion.");
            case "DNI_EXISTE" -> redirectAttributes.addFlashAttribute("mensajeRegistro",
                    "ERROR:El DNI ingresado ya esta registrado en otra cuenta.");
        }
        return "redirect:/";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        if (email == null || email.isBlank()) {
            redirectAttributes.addFlashAttribute("mensajeLogin", "ERROR:Ingresa tu correo electronico.");
            return "redirect:/";
        }
        if (!usuarioService.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("mensajeLogin",
                    "ERROR:No existe ninguna cuenta con ese correo. Registrate primero.");
            return "redirect:/";
        }
        var usuario = usuarioService.login(email, password);
        if (usuario.isPresent()) {
            session.setAttribute("usuarioLogueado", usuario.get());
            String primerNombre = usuario.get().getNombre().split(" ")[0];
            redirectAttributes.addFlashAttribute("mensajeLogin",
                    "OK:Bienvenido de vuelta, " + primerNombre + "!");
        } else {
            redirectAttributes.addFlashAttribute("mensajeLogin",
                    "ERROR:Contrasena incorrecta. Intentalo de nuevo.");
        }
        return "redirect:/";
    }

    @GetMapping("/mi-cuenta")
    public String miCuenta(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/";
        }
        List<PedidoEntity> pedidos = pedidoService.obtenerPorEmail(usuario.getEmail());
        double totalGastado = pedidos.stream().mapToDouble(PedidoEntity::getTotal).sum();
        model.addAttribute("usuario", usuario);
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("totalGastado", totalGastado);
        return "mi-cuenta/index";
    }
}
