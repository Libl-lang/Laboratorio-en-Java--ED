package com.example.grafosjava.logic;

import java.util.List;

/**
 * Resultado de la validación de una matriz de adyacencia.
 *
 * <p>Indica si la matriz es válida (sí/no) y conserva todos los mensajes de
 * error encontrados. La validez se deriva de la lista de errores: si no
 * hay errores, la matriz es válida.</p>
 *
 * <p>Responsabilidad: integrante A. Pertenece a la capa de lógica y no
 * depende de JavaFX ni de SmartGraph.</p>
 */
public final class ResultadoValidacion {

    /** Lista inmutable de mensajes de error (vacía si la matriz es válida). */
    private final List<String> errores;

    /**
     * Crea un resultado de validación con la lista de errores dada.
     *
     * @param errores mensajes de error producidos por la validación
     */
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
     * Mensajes de error encontrados.
     *
     * @return lista inmutable de mensajes legibles para el usuario (vacía si es válida)
     */
    public List<String> getErrores() {
        return errores;
    }

    /**
     * Devuelve todos los errores como un único mensaje unido por saltos de línea.
     *
     * @return cadena con todos los errores, o cadena vacía si la matriz es válida
     */
    public String getErroresComoTexto() {
        return String.join("\n", errores);
    }
}
