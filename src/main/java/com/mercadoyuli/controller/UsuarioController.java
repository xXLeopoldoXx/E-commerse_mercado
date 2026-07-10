package com.mercadoyuli.controller;

import com.mercadoyuli.model.PedidoEntity;
import com.mercadoyuli.model.Usuario;
import com.mercadoyuli.security.JwtService;
import com.mercadoyuli.service.PedidoService;
import com.mercadoyuli.service.UsuarioService;
import com.mercadoyuli.validator.UsuarioValidator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioValidator usuarioValidator;
    private final PedidoService pedidoService;
    private final JwtService jwtService;

    public UsuarioController(UsuarioService usuarioService, UsuarioValidator usuarioValidator,
                             PedidoService pedidoService, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.usuarioValidator = usuarioValidator;
        this.pedidoService = pedidoService;
        this.jwtService = jwtService;
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario,
                            RedirectAttributes redirectAttributes) {
        // Validacion del lado del servidor con el Spring Validator.
        BindingResult binding = new BeanPropertyBindingResult(usuario, "usuario");
        usuarioValidator.validate(usuario, binding);

        // Cada error se envia con la clave del campo para mostrarlo inline en el formulario.
        Map<String, String> errores = new HashMap<>();
        if (binding.hasErrors()) {
            binding.getFieldErrors().forEach(fe ->
                    errores.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
        } else {
            String resultado = usuarioService.registrar(usuario);
            switch (resultado) {
                case "EMAIL_EXISTE" -> errores.put("email", "El correo electronico ya esta registrado.");
                case "DNI_EXISTE"   -> errores.put("dni", "El DNI ya esta registrado.");
            }
        }

        if (!errores.isEmpty()) {
            redirectAttributes.addFlashAttribute("erroresRegistro", errores);
            redirectAttributes.addFlashAttribute("usuarioForm", usuario);
            return "redirect:/";
        }

        redirectAttributes.addFlashAttribute("mensajeRegistro",
                "Cuenta creada exitosamente. Ya puedes iniciar sesion.");
        return "redirect:/";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpServletResponse response,
                        RedirectAttributes redirectAttributes) {
        // Validacion de credenciales en el servidor, con mensajes por campo
        Map<String, String> errores = new HashMap<>();
        if (email == null || email.isBlank()) {
            errores.put("email", "Ingresa tu correo electronico.");
        }
        if (password == null || password.isBlank()) {
            errores.put("password", "Ingresa tu contrasena.");
        }

        if (errores.isEmpty()) {
            var usuario = usuarioService.login(email, password);
            if (usuario.isPresent()) {
                // Autenticacion con JWT: se emite el token en una cookie HttpOnly
                String token = jwtService.generar(usuario.get().getEmail(), usuario.get().getRol());
                jwtService.agregarCookie(response, token);

                if ("ADMIN".equals(usuario.get().getRol())) {
                    return "redirect:/admin/dashboard";
                }
                redirectAttributes.addFlashAttribute("mensajeLogin",
                        "Bienvenido, " + usuario.get().getNombre().split(" ")[0] + "!");
                return "redirect:/";
            }
            errores.put("password", "Correo o contrasena incorrectos.");
        }

        redirectAttributes.addFlashAttribute("erroresLogin", errores);
        redirectAttributes.addFlashAttribute("loginEmailValue", email);
        return "redirect:/";
    }

    @GetMapping("/mi-cuenta")
    public String miCuenta(Model model) {
        Usuario usuario = usuarioService.usuarioActual().orElse(null);
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

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        jwtService.limpiarCookie(response);
        return "redirect:/";
    }
}
