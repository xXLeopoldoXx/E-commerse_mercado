package com.mercadoyuli.validator;

import com.mercadoyuli.model.Pedido;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.time.YearMonth;

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

        // Datos de tarjeta: obligatorios solo cuando el metodo de pago es "tarjeta"
        if ("tarjeta".equals(p.getMetodoPago())) {
            // Numero de tarjeta: 16 digitos
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "numeroTarjeta", "tarjeta.vacio",
                    "Ingresa el numero de tarjeta.");
            if (StringUtils.hasText(p.getNumeroTarjeta())
                    && !p.getNumeroTarjeta().replaceAll("\\s", "").matches("\\d{16}")) {
                errors.rejectValue("numeroTarjeta", "tarjeta.formato", "La tarjeta debe tener 16 digitos.");
            }

            // CVV: 3 digitos
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cvv", "cvv.vacio",
                    "Ingresa el CVV.");
            if (StringUtils.hasText(p.getCvv()) && !p.getCvv().matches("\\d{3}")) {
                errors.rejectValue("cvv", "cvv.formato", "El CVV debe tener 3 digitos.");
            }

            // Vencimiento: formato MM/AA y que la tarjeta no este vencida
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "vencimiento", "vencimiento.vacio",
                    "Ingresa el vencimiento.");
            if (StringUtils.hasText(p.getVencimiento())) {
                if (!p.getVencimiento().matches("(0[1-9]|1[0-2])/\\d{2}")) {
                    errors.rejectValue("vencimiento", "vencimiento.formato", "Formato valido: MM/AA.");
                } else {
                    int mes = Integer.parseInt(p.getVencimiento().substring(0, 2));
                    int anio = 2000 + Integer.parseInt(p.getVencimiento().substring(3, 5));
                    YearMonth exp = YearMonth.of(anio, mes);
                    if (exp.isBefore(YearMonth.now())) {
                        errors.rejectValue("vencimiento", "vencimiento.vencida", "La tarjeta esta vencida.");
                    }
                }
            }

            // Titular: obligatorio
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "titular", "titular.vacio",
                    "Ingresa el nombre del titular.");
        }
    }
}
