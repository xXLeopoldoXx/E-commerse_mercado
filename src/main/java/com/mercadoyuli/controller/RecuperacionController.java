package com.mercadoyuli.controller;

import com.mercadoyuli.service.EmailService;
import com.mercadoyuli.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RecuperacionController {

    // Misma regla de contrasena que en el registro
    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$";

    private final UsuarioService usuarioService;
    private final EmailService emailService;

    @Value("${app.url-base}")
    private String urlBase;

    public RecuperacionController(UsuarioService usuarioService, EmailService emailService) {
        this.usuarioService = usuarioService;
        this.emailService = emailService;
    }

    // Paso 1: formulario para pedir el correo
    @GetMapping("/recuperar")
    public String solicitarForm() {
        return "recuperar/solicitar";
    }

    // Paso 2: genera el token y envia el correo con el enlace
    @PostMapping("/recuperar")
    public String solicitar(@RequestParam String email, RedirectAttributes ra) {
        usuarioService.generarTokenRecuperacion(email).ifPresent(token -> {
            String enlace = urlBase + "/recuperar/" + token;
            emailService.enviarRecuperacion(email, enlace);
        });
        // Mensaje generico (no revela si el correo existe o no)
        ra.addFlashAttribute("mensaje",
                "Si el correo esta registrado, te enviamos un enlace para restablecer tu contrasena.");
        return "redirect:/recuperar";
    }

    // Paso 3: valida el token y muestra el formulario de nueva contrasena
    @GetMapping("/recuperar/{token}")
    public String nuevaForm(@PathVariable String token, Model model, RedirectAttributes ra) {
        if (usuarioService.buscarPorTokenValido(token).isEmpty()) {
            ra.addFlashAttribute("error", "El enlace no es valido o ya expiro. Solicita uno nuevo.");
            return "redirect:/recuperar";
        }
        model.addAttribute("token", token);
        return "recuperar/nueva";
    }

    // Paso 4: valida la nueva contrasena (Spring, servidor) y la restablece
    @PostMapping("/recuperar/restablecer")
    public String restablecer(@RequestParam String token,
                              @RequestParam String password,
                              @RequestParam String password2,
                              Model model,
                              RedirectAttributes ra) {
        if (usuarioService.buscarPorTokenValido(token).isEmpty()) {
            ra.addFlashAttribute("error", "El enlace no es valido o ya expiro. Solicita uno nuevo.");
            return "redirect:/recuperar";
        }

        String errorPass = null;
        if (password == null || !password.matches(PASSWORD_REGEX)) {
            errorPass = "Minimo 8 caracteres, una mayuscula, una minuscula y un numero.";
        } else if (!password.equals(password2)) {
            errorPass = "Las contrasenas no coinciden.";
        }

        if (errorPass != null) {
            model.addAttribute("token", token);
            model.addAttribute("errorPassword", errorPass);
            return "recuperar/nueva";
        }

        usuarioService.restablecerPassword(token, password);
        ra.addFlashAttribute("mensajeRecuperacion",
                "Tu contrasena se actualizo correctamente. Ya puedes iniciar sesion.");
        return "redirect:/";
    }
}
