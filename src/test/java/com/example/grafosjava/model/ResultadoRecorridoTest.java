package com.example.grafosjava.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para {@link ResultadoRecorrido}.
 */
class ResultadoRecorridoTest {

    @Test
    void almacenaIdInicioYOrdenDeVisita() {
        ResultadoRecorrido r = new ResultadoRecorrido(0, List.of(0, 1, 2, 3));

        assertEquals(0, r.getIdInicio());
        assertEquals(List.of(0, 1, 2, 3), r.getOrdenVisita());
        assertEquals(4, r.getCantidadVisitados());
    }

    @Test
    void ordenDeVisitaEsInmutable() {
        List<Integer> original = List.of(0, 1, 2);
        ResultadoRecorrido r = new ResultadoRecorrido(0, original);

        assertThrows(UnsupportedOperationException.class,
                () -> r.getOrdenVisita().add(3));
    }

    @Test
    void listaNulaProduceListaVacia() {
        ResultadoRecorrido r = new ResultadoRecorrido(0, null);

        assertTrue(r.getOrdenVisita().isEmpty());
        assertEquals(0, r.getCantidadVisitados());
    }

    @Test
    void listaModificadaExternamenteNoAfectaResultado() {
        List<Integer> original = new java.util.ArrayList<>(List.of(0, 1, 2));
        ResultadoRecorrido r = new ResultadoRecorrido(0, original);

        original.add(3);
        assertEquals(3, r.getCantidadVisitados(), "La copia debe ser independiente.");
    }
}
