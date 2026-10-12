package com.example.grafosjava.logic;

import com.example.grafosjava.model.Arista;
import com.example.grafosjava.model.GrafoModelo;
import com.example.grafosjava.model.MatrizAdyacencia;
import com.example.grafosjava.model.Vertice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para {@link GeneradorMatriz}.
 *
 * <p>Verifican que la conversión grafo → matriz produce los valores correctos
 * y que la operación matriz → grafo → matriz es inversa (round-trip).</p>
 */
class GeneradorMatrizTest {

    private final GeneradorMatriz generador = new GeneradorMatriz();

    @Test
    void generaMatrizSimetricaConPesos() {
        // Grafo: 0 --4-- 1, 0 --2-- 2.
        GrafoModelo grafo = new GrafoModelo();
        grafo.agregarVertice(new Vertice(0, "V0"));
        grafo.agregarVertice(new Vertice(1, "V1"));
        grafo.agregarVertice(new Vertice(2, "V2"));
        grafo.agregarArista(0, 1, 4);
        grafo.agregarArista(0, 2, 2);

        MatrizAdyacencia matriz = generador.generar(grafo);

        assertEquals(3, matriz.getCantidadVertices());
        assertEquals(4, matriz.getValor(0, 1));
        assertEquals(4, matriz.getValor(1, 0));
        assertEquals(2, matriz.getValor(0, 2));
        assertEquals(2, matriz.getValor(2, 0));
        assertEquals(0, matriz.getValor(1, 2));
        assertEquals(0, matriz.getValor(2, 1));
        assertEquals(0, matriz.getValor(0, 0));
    }

    @Test
    void roundTripMatrizAGrafoAMatriz() {
        // Matriz original.
        MatrizAdyacencia original = new MatrizAdyacencia(3);
        original.setValor(0, 1, 4); original.setValor(1, 0, 4);
        original.setValor(0, 2, 2); original.setValor(2, 0, 2);

        // Matriz → Grafo → Matriz.
        ConstructorGrafo constructor = new ConstructorGrafo();
        GrafoModelo grafo = constructor.construir(original);
        MatrizAdyacencia resultante = generador.generar(grafo);

        // La matriz resultante debe coincidir con la original.
        assertEquals(original.getCantidadVertices(), resultante.getCantidadVertices());
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(original.getValor(i, j), resultante.getValor(i, j),
                        "La celda (" + i + ", " + j + ") debe coincidir.");
            }
        }
    }

    @Test
    void roundTripConPesosDistintos() {
        // Matriz 4×4 con pesos variados.
        MatrizAdyacencia original = new MatrizAdyacencia(4);
        original.setValor(0, 1, 10); original.setValor(1, 0, 10);
        original.setValor(0, 2, 5);  original.setValor(2, 0, 5);
        original.setValor(1, 3, 3);  original.setValor(3, 1, 3);
        original.setValor(2, 3, 2);  original.setValor(3, 2, 2);

        ConstructorGrafo constructor = new ConstructorGrafo();
        GrafoModelo grafo = constructor.construir(original);
        MatrizAdyacencia resultante = generador.generar(grafo);

        assertEquals(4, resultante.getCantidadVertices());
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(original.getValor(i, j), resultante.getValor(i, j));
            }
        }
    }

    @Test
    void grafoVacioLanzaExcepcion() {
        GrafoModelo grafo = new GrafoModelo();

        assertThrows(IllegalArgumentException.class, () -> generador.generar(grafo));
    }

    @Test
    void grafoConVerticesNoContiguosMantieneTamanoCorrecto() {
        // IDs 0, 2, 4 (con huecos). La matriz debe ser 5×5.
        GrafoModelo grafo = new GrafoModelo();
        grafo.agregarVertice(new Vertice(0, "V0"));
        grafo.agregarVertice(new Vertice(2, "V2"));
        grafo.agregarVertice(new Vertice(4, "V4"));
        grafo.agregarArista(0, 2, 3);
        grafo.agregarArista(2, 4, 1);

        MatrizAdyacencia matriz = generador.generar(grafo);

        assertEquals(5, matriz.getCantidadVertices());
        assertEquals(3, matriz.getValor(0, 2));
        assertEquals(3, matriz.getValor(2, 0));
        assertEquals(1, matriz.getValor(2, 4));
        assertEquals(1, matriz.getValor(4, 2));
        assertEquals(0, matriz.getValor(0, 0));
        assertEquals(0, matriz.getValor(1, 1));
    }
}
