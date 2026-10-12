package com.example.grafosjava.logic;

import com.example.grafosjava.model.GrafoModelo;
import com.example.grafosjava.model.MatrizAdyacencia;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para {@link ConstructorGrafo}.
 *
 * <p>Verifican que la conversión matriz → grafo produce los vértices y
 * aristas esperados, usando el caso de la sección 11 del documento:
 * peso 4 entre V0–V1 y peso 2 entre V0–V2.</p>
 */
class ConstructorGrafoTest {

    private final ConstructorGrafo constructor = new ConstructorGrafo();

    private MatrizAdyacencia matriz3x3ConPesos() {
        MatrizAdyacencia m = new MatrizAdyacencia(3);
        m.setValor(0, 1, 4);
        m.setValor(1, 0, 4);
        m.setValor(0, 2, 2);
        m.setValor(2, 0, 2);
        return m;
    }

    @Test
    void construyeVerticesConIdsDesdeCero() {
        GrafoModelo grafo = constructor.construir(matriz3x3ConPesos());

        assertEquals(3, grafo.contarVertices());
        assertNotNull(grafo.obtenerVertice(0));
        assertNotNull(grafo.obtenerVertice(1));
        assertNotNull(grafo.obtenerVertice(2));
    }

    @Test
    void construyeAristasConPesosCorrectos() {
        GrafoModelo grafo = constructor.construir(matriz3x3ConPesos());

        assertEquals(2, grafo.contarAristas());

        assertTrue(grafo.existeArista(0, 1));
        assertTrue(grafo.existeArista(0, 2));
        assertFalse(grafo.existeArista(1, 2));
    }

    @Test
    void noCreaAristasParaCeldasCero() {
        GrafoModelo grafo = constructor.construir(matriz3x3ConPesos());

        assertEquals(2, grafo.contarAristas(),
                "Solo debe haber 2 aristas (las de peso > 0).");
    }

    @Test
    void reconstruyeMatrizCorrectamente() {
        GrafoModelo grafo = constructor.construir(matriz3x3ConPesos());
        GeneradorMatriz generador = new GeneradorMatriz();
        MatrizAdyacencia matrizResultante = generador.generar(grafo);

        assertEquals(3, matrizResultante.getCantidadVertices());
        assertEquals(4, matrizResultante.getValor(0, 1));
        assertEquals(4, matrizResultante.getValor(1, 0));
        assertEquals(2, matrizResultante.getValor(0, 2));
        assertEquals(2, matrizResultante.getValor(2, 0));
        assertEquals(0, matrizResultante.getValor(1, 2));
        assertEquals(0, matrizResultante.getValor(2, 1));
    }

    @Test
    void matrizNxNConstruyeNGrafoConAristasCorrectas() {
        MatrizAdyacencia m = new MatrizAdyacencia(4);
        // Gráfo en forma de estrella: 0 conectado a 1, 2, 3.
        m.setValor(0, 1, 3); m.setValor(1, 0, 3);
        m.setValor(0, 2, 5); m.setValor(2, 0, 5);
        m.setValor(0, 3, 7); m.setValor(3, 0, 7);

        GrafoModelo grafo = constructor.construir(m);

        assertEquals(4, grafo.contarVertices());
        assertEquals(3, grafo.contarAristas());
    }
}
