package com.example.grafosjava.logic;

import com.example.grafosjava.model.GrafoModelo;
import com.example.grafosjava.model.MatrizAdyacencia;
import com.example.grafosjava.model.ResultadoCaminoCorto;
import com.example.grafosjava.model.Vertice;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para {@link RecorridoCaminoCorto} (algoritmo de Dijkstra).
 */
class RecorridoCaminoCortoTest {

    private final RecorridoCaminoCorto dijkstra = new RecorridoCaminoCorto();

    private GrafoModelo grafoConPesos() {
        //   0 --4-- 1
        //   |      |
        //   2 --  --
        //   0 --2-- 2  (0-1: peso 4, 0-2: peso 2, 1-2: peso 1)
        GrafoModelo grafo = new GrafoModelo();
        grafo.agregarVertice(new Vertice(0, "V0"));
        grafo.agregarVertice(new Vertice(1, "V1"));
        grafo.agregarVertice(new Vertice(2, "V2"));
        grafo.agregarArista(0, 1, 4);
        grafo.agregarArista(0, 2, 2);
        grafo.agregarArista(1, 2, 1);
        return grafo;
    }

    @Test
    void caminoMasCortoUsaCaminoIndirecto() {
        GrafoModelo grafo = grafoConPesos();

        ResultadoCaminoCorto resultado = dijkstra.dijkstra(grafo, 0, 1);

        assertTrue(resultado.esAlcanzable());
        assertEquals(3, resultado.getDistanciaTotal(),
                "Camino más corto 0→2→1: peso 2 + 1 = 3 (no 0→1 que pesa 4).");
        assertEquals(List.of(0, 2, 1), resultado.getCamino());
    }

    @Test
    void caminoDirectoEsElMasCortoCuandoEsUnico() {
        GrafoModelo grafo = grafoConPesos();

        ResultadoCaminoCorto resultado = dijkstra.dijkstra(grafo, 0, 2);

        assertTrue(resultado.esAlcanzable());
        assertEquals(2, resultado.getDistanciaTotal());
        assertEquals(List.of(0, 2), resultado.getCamino());
    }

    @Test
    void origenIgualADestino() {
        GrafoModelo grafo = grafoConPesos();

        ResultadoCaminoCorto resultado = dijkstra.dijkstra(grafo, 0, 0);

        assertTrue(resultado.esAlcanzable());
        assertEquals(0, resultado.getDistanciaTotal());
        assertEquals(List.of(0), resultado.getCamino());
    }

    @Test
    void destinoInalcanzable() {
        // Dos componentes separadas: {0,1} y {2}.
        GrafoModelo grafo = new GrafoModelo();
        grafo.agregarVertice(new Vertice(0, "V0"));
        grafo.agregarVertice(new Vertice(1, "V1"));
        grafo.agregarVertice(new Vertice(2, "V2"));
        grafo.agregarArista(0, 1, 5);

        ResultadoCaminoCorto resultado = dijkstra.dijkstra(grafo, 0, 2);

        assertFalse(resultado.esAlcanzable());
        assertTrue(resultado.getCamino().isEmpty());
    }

    @Test
    void idOrigenInexistente() {
        GrafoModelo grafo = grafoConPesos();

        ResultadoCaminoCorto resultado = dijkstra.dijkstra(grafo, 99, 1);

        assertFalse(resultado.esAlcanzable());
    }

    @Test
    void idDestinoInexistente() {
        GrafoModelo grafo = grafoConPesos();

        ResultadoCaminoCorto resultado = dijkstra.dijkstra(grafo, 0, 99);

        assertFalse(resultado.esAlcanzable());
    }

    @Test
    void grafoMayorConMultiplesRutas() {
        //   0 --2-- 1 --3-- 3
        //   |      |      |
        //   6      3      1
        //   |      |      |
        //   2 --1-- 4 --+
        GrafoModelo grafo = new GrafoModelo();
        for (int i = 0; i < 5; i++) {
            grafo.agregarVertice(new Vertice(i, "V" + i));
        }
        // 0→1 (2), 0→2 (6), 1→2 (3), 1→3 (3), 2→4 (1), 4→3 (1)
        grafo.agregarArista(0, 1, 2);
        grafo.agregarArista(0, 2, 6);
        grafo.agregarArista(1, 2, 3);
        grafo.agregarArista(1, 3, 3);
        grafo.agregarArista(2, 4, 1);
        grafo.agregarArista(4, 3, 1);

        // Camino más corto 0→1→2→4→3: 2+3+1+1 = 7
        // Alternativa 0→1→3: 2+3 = 5
        ResultadoCaminoCorto resultado = dijkstra.dijkstra(grafo, 0, 3);

        assertTrue(resultado.esAlcanzable());
        assertEquals(5, resultado.getDistanciaTotal());
        assertEquals(List.of(0, 1, 3), resultado.getCamino());
    }

    @Test
    void matrizConPesosDelDocumento() {
        // Caso de la sección 11: peso 4 entre V0-V1, peso 2 entre V0-V2.
        MatrizAdyacencia m = new MatrizAdyacencia(3);
        m.setValor(0, 1, 4); m.setValor(1, 0, 4);
        m.setValor(0, 2, 2); m.setValor(2, 0, 2);

        ConstructorGrafo constructor = new ConstructorGrafo();
        GrafoModelo grafo = constructor.construir(m);

        ResultadoCaminoCorto resultado = dijkstra.dijkstra(grafo, 0, 1);

        assertEquals(4, resultado.getDistanciaTotal());
        assertEquals(List.of(0, 1), resultado.getCamino());
    }
}
