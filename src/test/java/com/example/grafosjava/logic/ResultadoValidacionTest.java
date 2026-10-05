package com.example.grafosjava.logic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del Integrante A para {@link ResultadoValidacion}.
 */
class ResultadoValidacionTest {

    @Test
    void resultadoValidoNoTieneErrores() {
        ResultadoValidacion resultado = ResultadoValidacion.valida();

        assertTrue(resultado.esValida());
        assertTrue(resultado.getErrores().isEmpty());
    }

    @Test
    void resultadoConErroresNoEsValidoYLosConserva() {
        List<String> errores = List.of("La matriz no es cuadrada", "La diagonal debe ser 0");

        ResultadoValidacion resultado = ResultadoValidacion.conErrores(errores);

        assertFalse(resultado.esValida());
        assertEquals(errores, resultado.getErrores());
    }

    @Test
    void laListaDeErroresEsInmutable() {
        ResultadoValidacion resultado = ResultadoValidacion.conErrores(List.of("un error"));

        assertThrows(UnsupportedOperationException.class, () -> resultado.getErrores().add("otro"));
    }
}