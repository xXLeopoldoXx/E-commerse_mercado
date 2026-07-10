package com.mercadoyuli.validator;

import com.mercadoyuli.model.Producto;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validacion de productos del panel admin usando el Spring Validator
 * (org.springframework.validation.Validator). Los mensajes se muestran
 * en el formulario con th:errors / #fields.
 */
@Component
public class ProductoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Producto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Producto p = (Producto) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nombre", "nombre.vacio",
                "El nombre del producto es obligatorio");
        if (StringUtils.hasText(p.getNombre())
                && (p.getNombre().trim().length() < 2 || p.getNombre().trim().length() > 100)) {
            errors.rejectValue("nombre", "nombre.tamano",
                    "El nombre debe tener entre 2 y 100 caracteres");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "descripcion", "descripcion.vacio",
                "La descripcion es obligatoria");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "proveedor", "proveedor.vacio",
                "El proveedor es obligatorio");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "unidad", "unidad.vacio",
                "La unidad es obligatoria");

        if (p.getStock() < 0) {
            errors.rejectValue("stock", "stock.negativo", "El stock no puede ser negativo");
        }
    }
}
