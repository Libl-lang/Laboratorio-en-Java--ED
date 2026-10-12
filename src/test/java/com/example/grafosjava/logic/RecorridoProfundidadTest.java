package com.example.grafosjava.logic;

import com.example.grafosjava.model.GrafoModelo;
import com.example.grafosjava.model.Vertice;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para {@link RecorridoProfundidad}.
 *
 * <p>Los vecinos se visitan en orden ascendente de ID. DFS y BFS producen
 * órdenes diferentes en grafos con suficiente estructura.</p>
 */
class RecorridoProfundidadTest {

    private final RecorridoProfundidad dfs = new RecorridoProfundidad();

    private GrafoModelo grafoCuadradoConCruz() {
        // 0 -- 1
        // |    |
        // 2 -- 3
        GrafoModelo grafo = new GrafoModelo();
        grafo.agregarVertice(new Vertice(0, "V0"));
        grafo.agregarVertice(new Vertice(1, "V1"));
        grafo.agregarVertice(new Vertice(2, "V2"));
        grafo.agregarVertice(new Vertice(3, "V3"));
        grafo.agregarArista(0, 1, 1);
        grafo.agregarArista(0, 2, 1);
        grafo.agregarArista(1, 3, 1);
        grafo.agregarArista(2, 3, 1);
        return grafo;
    }

    @Test
    void dfsVisitaEnOrdenAscendenteDeVecinos() {
        GrafoModelo grafo = grafoCuadradoConCruz();

        // DFS desde 0: visita 0, luego vecino 1 (ID más pequeño),
        // desde 1 visita 3 (único no visitado), desde 3 visita 2.
        List<Integer> orden = dfs.recorrer(grafo, 0).getOrdenVisita();

        assertEquals(List.of(0, 1, 3, 2), orden);
    }

    @Test
    void dfsDesdeOtroVertice() {
        GrafoModelo grafo = grafoCuadradoConCruz();

        List<Integer> orden = dfs.recorrer(grafo, 3).getOrdenVisita();

        assertEquals(List.of(3, 1, 0, 2), orden,
                "Los vecinos se visitan por ID ascendente.");
    }

    @Test
    void dfsEnGrafoDesconectadoSoloAlcanzaComponenteConectada() {
        // Componente 1: 0-1 ; Componente 2: 2-3
        GrafoModelo grafo = new GrafoModelo();
        grafo.agregarVertice(new Vertice(0, "V0"));
        grafo.agregarVertice(new Vertice(1, "V1"));
        grafo.agregarVertice(new Vertice(2, "V2"));
        grafo.agregarVertice(new Vertice(3, "V3"));
        grafo.agregarArista(0, 1, 1);
        grafo.agregarArista(2, 3, 1);

        List<Integer> orden = dfs.recorrer(grafo, 0).getOrdenVisita();

        assertEquals(List.of(0, 1), orden,
                "Solo se deben visitar los alcanzables desde el inicio.");
    }

    @Test
    void dfsConIdInexistenteDevuelveListaVacia() {
        GrafoModelo grafo = grafoCuadradoConCruz();

        List<Integer> orden = dfs.recorrer(grafo, 99).getOrdenVisita();

        assertTrue(orden.isEmpty());
    }

    @Test
    void dfsEnGrafoConUnSoloVertice() {
        GrafoModelo grafo = new GrafoModelo();
        grafo.agregarVertice(new Vertice(0, "V0"));

        List<Integer> orden = dfs.recorrer(grafo, 0).getOrdenVisita();

        assertEquals(List.of(0), orden);
    }

    @Test
    void dfsRespetaIdInicio() {
        GrafoModelo grafo = grafoCuadradoConCruz();

        assertEquals(0, dfs.recorrer(grafo, 0).getIdInicio());
    }
}
