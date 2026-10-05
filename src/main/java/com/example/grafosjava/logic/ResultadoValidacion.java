package com.example.grafosjava.logic;

import java.util.List;

/**
 * Resultado de la validación de la matriz (Integrante A).
 *
 * <p>Contiene dos cosas: si la matriz es válida (sí/no) y la lista de mensajes
 * de error. La validez se deriva de la lista: si no hay errores, la matriz es
 * válida.</p>
 */
public final class ResultadoValidacion {

    private final List<String> errores;

    private ResultadoValidacion(List<String> errores) {
        this.errores = List.copyOf(errores);
    }

    /**
     * Resultado válido, sin errores.
     *
     * @return resultado con la lista de errores vacía
     */
    public static ResultadoValidacion valida() {
        return new ResultadoValidacion(List.of());
    }

    /**
     * Resultado con los mensajes de error recibidos.
     *
     * @param errores mensajes producidos por la validación
     * @return resultado no válido (o válido si la lista está vacía)
     */
    public static ResultadoValidacion conErrores(List<String> errores) {
        return new ResultadoValidacion(errores);
    }

    /**
     * Indica si la matriz pasó todas las reglas de validación.
     *
     * @return {@code true} si no hay errores
     */
    public boolean esValida() {
        return errores.isEmpty();
    }

    /**
     * Mensajes de error encontrados (lista inmutable; vacía si es válida).
     *
     * @return lista de mensajes legibles para el usuario
     */
    public List<String> getErrores() {
        return errores;
    }
}