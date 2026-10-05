package com.example.grafosjava.logic;

/**
 * Error de formato del Integrante A producido por {@link ParserMatriz}.
 *
 * <p>Se lanza cuando el texto ingresado no se puede convertir en una
 * {@link com.example.grafosjava.model.MatrizAdyacencia}: caracteres no
 * numéricos, filas vacías, filas con distinta cantidad de valores o texto
 * vacío.</p>
 */
public class FormatoMatrizInvalidoException extends RuntimeException {

    public FormatoMatrizInvalidoException(String mensaje) {
        super(mensaje);
    }
}