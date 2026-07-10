package com.mercadoyuli.validator;

import com.mercadoyuli.model.Pedido;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validacion del checkout (datos del pedido) usando el Spring Validator
 * (org.springframework.validation.Validator).
 */
@Component
public class PedidoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Pedido.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Pedido p = (Pedido) target;

        // Nombre: minimo 3 caracteres
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nombreCliente", "nombre.vacio",
                "Ingresa tu nombre completo.");
        if (StringUtils.hasText(p.getNombreCliente()) && p.getNombreCliente().trim().length() < 3) {
            errors.rejectValue("nombreCliente", "nombre.corto", "Ingresa tu nombre completo.");
        }

        // DNI: 8 digitos
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dni", "dni.vacio",
                "El DNI debe tener exactamente 8 digitos.");
        if (StringUtils.hasText(p.getDni()) && !p.getDni().matches("\\d{8}")) {
            errors.rejectValue("dni", "dni.formato", "El DNI debe tener exactamente 8 digitos.");
        }

        // Email
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailCliente", "email.vacio",
                "Ingresa un correo electronico valido.");
        if (StringUtils.hasText(p.getEmailCliente())
                && !p.getEmailCliente().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            errors.rejectValue("emailCliente", "email.formato", "Ingresa un correo electronico valido.");
        }

        // Telefono: 9 digitos
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "telefonoCliente", "telefono.vacio",
                "El telefono debe tener 9 digitos.");
        if (StringUtils.hasText(p.getTelefonoCliente()) && !p.getTelefonoCliente().matches("\\d{9}")) {
            errors.rejectValue("telefonoCliente", "telefono.formato", "El telefono debe tener 9 digitos.");
        }

        // Direccion: obligatoria solo cuando el tipo de entrega es "envio"
        if ("envio".equals(p.getTipoEntrega()) && !StringUtils.hasText(p.getDireccion())) {
            errors.rejectValue("direccion", "direccion.requerida", "Ingresa tu direccion de envio.");
        }
    }
}
