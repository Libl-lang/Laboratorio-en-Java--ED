package com.example.grafosjava.logic;

import com.example.grafosjava.model.GrafoModelo;
import com.example.grafosjava.model.Vertice;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para {@link RecorridoAncho}.
 *
 * <p>Los vecinos se visitan en orden ascendente de ID. BFS explora por
 * niveles, produciendo un orden distinto al de DFS.</p>
 */
class RecorridoAnchoTest {

    private final RecorridoAncho bfs = new RecorridoAncho();

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
    void bfsVisitaPorNiveles() {
        GrafoModelo grafo = grafoCuadradoConCruz();

        // BFS desde 0: nivel 0=[0], nivel 1=[1,2], nivel 2=[3].
        List<Integer> orden = bfs.recorrer(grafo, 0).getOrdenVisita();

        assertEquals(List.of(0, 1, 2, 3), orden);
    }

    @Test
    void bfsDesdeVerticeIntermedio() {
        GrafoModelo grafo = grafoCuadradoConCruz();

        List<Integer> orden = bfs.recorrer(grafo, 1).getOrdenVisita();

        assertEquals(List.of(1, 0, 3, 2), orden,
                "Vecinos en orden ascendente: 0 y 3 del nivel 1, luego 2.");
    }

    @Test
    void bfsEnGrafoDesconectadoSoloAlcanzaComponenteConectada() {
        GrafoModelo grafo = new GrafoModelo();
        grafo.agregarVertice(new Vertice(0, "V0"));
        grafo.agregarVertice(new Vertice(1, "V1"));
        grafo.agregarVertice(new Vertice(2, "V2"));
        grafo.agregarVertice(new Vertice(3, "V3"));
        grafo.agregarArista(0, 1, 1);
        grafo.agregarArista(2, 3, 1);

        List<Integer> orden = bfs.recorrer(grafo, 0).getOrdenVisita();

        assertEquals(List.of(0, 1), orden,
                "Solo se deben visitar los alcanzables desde el inicio.");
    }

    @Test
    void bfsConIdInexistenteDevuelveListaVacia() {
        GrafoModelo grafo = grafoCuadradoConCruz();

        List<Integer> orden = bfs.recorrer(grafo, 99).getOrdenVisita();

        assertTrue(orden.isEmpty());
    }

    @Test
    void bfsEnGrafoConUnSoloVertice() {
        GrafoModelo grafo = new GrafoModelo();
        grafo.agregarVertice(new Vertice(0, "V0"));

        List<Integer> orden = bfs.recorrer(grafo, 0).getOrdenVisita();

        assertEquals(List.of(0), orden);
    }

    @Test
    void bfsYdfsProducenOrdenesDiferentes() {
        GrafoModelo grafo = grafoCuadradoConCruz();

        List<Integer> ordenDfs = new RecorridoProfundidad().recorrer(grafo, 0).getOrdenVisita();
        List<Integer> ordenBfs = bfs.recorrer(grafo, 0).getOrdenVisita();

        assertNotEquals(ordenDfs, ordenBfs,
                "DFS y BFS deben producir órdenes diferentes en este grafo.");
    }
}
