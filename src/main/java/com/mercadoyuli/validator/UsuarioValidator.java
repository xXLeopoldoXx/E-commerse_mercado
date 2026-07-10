package com.mercadoyuli.validator;

import com.mercadoyuli.model.Usuario;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validacion del registro de usuarios usando el Spring Validator
 * (org.springframework.validation.Validator). Es la unica autoridad
 * de validacion del formulario de registro.
 */
@Component
public class UsuarioValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Usuario.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Usuario u = (Usuario) target;

        // DNI: 8 digitos
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dni", "dni.vacio",
                "El DNI debe tener exactamente 8 digitos.");
        if (StringUtils.hasText(u.getDni()) && !u.getDni().matches("\\d{8}")) {
            errors.rejectValue("dni", "dni.formato", "El DNI debe tener exactamente 8 digitos.");
        }

        // Nombre: minimo 3 caracteres
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nombre", "nombre.vacio",
                "Ingresa tu nombre completo (minimo 3 caracteres).");
        if (StringUtils.hasText(u.getNombre()) && u.getNombre().trim().length() < 3) {
            errors.rejectValue("nombre", "nombre.corto",
                    "Ingresa tu nombre completo (minimo 3 caracteres).");
        }

        // Telefono: 9 digitos
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "telefono", "telefono.vacio",
                "El telefono debe tener 9 digitos.");
        if (StringUtils.hasText(u.getTelefono()) && !u.getTelefono().matches("\\d{9}")) {
            errors.rejectValue("telefono", "telefono.formato", "El telefono debe tener 9 digitos.");
        }

        // Email
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "email.vacio",
                "Ingresa un correo electronico valido.");
        if (StringUtils.hasText(u.getEmail()) && !u.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            errors.rejectValue("email", "email.formato", "Ingresa un correo electronico valido.");
        }

        // Contrasena: min 8, una mayuscula, una minuscula y un numero
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password.vacio",
                "Minimo 8 caracteres, una mayuscula, una minuscula y un numero.");
        if (StringUtils.hasText(u.getPassword())
                && !u.getPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$")) {
            errors.rejectValue("password", "password.formato",
                    "Minimo 8 caracteres, una mayuscula, una minuscula y un numero.");
        }
    }
}
