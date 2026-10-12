package com.example.grafosjava.logic;

import com.example.grafosjava.model.GrafoModelo;
import com.example.grafosjava.model.MatrizAdyacencia;
import com.example.grafosjava.model.ResultadoCaminoCorto;
import com.example.grafosjava.model.ResultadoRecorrido;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integracion para {@link GrafoServicio}.
 *
 * Verifican el flujo completo: validar, convertir, recorrer y dijkstra,
 * incluyendo el caso de la seccion 11 del documento y el round-trip
 * matriz a grafo a matriz.
 */
class GrafoServicioTest {

    private final GrafoServicio servicio = new GrafoServicio();

    private MatrizAdyacencia matrizDelDocumento() {
        MatrizAdyacencia m = new MatrizAdyacencia(3);
        m.setValor(0, 1, 4); m.setValor(1, 0, 4);
        m.setValor(0, 2, 2); m.setValor(2, 0, 2);
        return m;
    }

    @Test
    void validarMatrizValidaDevuelveResultadoValido() {
        assertTrue(servicio.validarMatriz(matrizDelDocumento()).esValida());
        assertTrue(servicio.validarMatriz(matrizDelDocumento()).getErrores().isEmpty());
    }

    @Test
    void validarMatrizInvalidaDevuelveErrores() {
        MatrizAdyacencia m = new MatrizAdyacencia(2);
        m.setValor(0, 0, 5);
        m.setValor(0, 1, 3);
        m.setValor(1, 0, 7);

        assertFalse(servicio.validarMatriz(m).esValida());
        assertFalse(servicio.validarMatriz(m).getErrores().isEmpty());
    }

    @Test
    void convertirMatrizAGrafoProduceGrafoCorrecto() {
        GrafoModelo grafo = servicio.convertirMatrizAGrafo(matrizDelDocumento());

        assertEquals(3, grafo.contarVertices());
        assertEquals(2, grafo.contarAristas());
        assertTrue(grafo.existeArista(0, 1));
        assertTrue(grafo.existeArista(0, 2));
        assertFalse(grafo.existeArista(1, 2));
    }

    @Test
    void convertirMatrizAGrafoRechazaMatrizInvalida() {
        MatrizAdyacencia m = new MatrizAdyacencia(2);
        m.setValor(0, 1, 3);
        m.setValor(1, 0, 5);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.convertirMatrizAGrafo(m));

        assertFalse(ex.getMessage().isEmpty());
        assertTrue(ex.getMessage().contains("La celda"));
    }

    @Test
    void convertirGrafoAMatrizProduceMatrizSimetrica() {
        GrafoModelo grafo = servicio.convertirMatrizAGrafo(matrizDelDocumento());
        MatrizAdyacencia matriz = servicio.convertirGrafoAMatriz(grafo);

        assertEquals(3, matriz.getCantidadVertices());
        assertEquals(4, matriz.getValor(0, 1));
        assertEquals(2, matriz.getValor(0, 2));
        assertEquals(0, matriz.getValor(1, 2));
        assertEquals(0, matriz.getValor(0, 0));
    }

    @Test
    void roundTripMatrizAGrafoAMatrizCoincide() {
        MatrizAdyacencia original = matrizDelDocumento();

        GrafoModelo grafo = servicio.convertirMatrizAGrafo(original);
        MatrizAdyacencia resultante = servicio.convertirGrafoAMatriz(grafo);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(original.getValor(i, j), resultante.getValor(i, j));
            }
        }
    }

    @Test
    void recorrerProfundidadDesdeDocumento() {
        GrafoModelo grafo = servicio.convertirMatrizAGrafo(matrizDelDocumento());

        ResultadoRecorrido resultado = servicio.recorrerProfundidad(grafo, 0);

        assertEquals(List.of(0, 1, 2), resultado.getOrdenVisita());
    }

    @Test
    void recorrerAnchuraDesdeDocumento() {
        GrafoModelo grafo = servicio.convertirMatrizAGrafo(matrizDelDocumento());

        ResultadoRecorrido resultado = servicio.recorrerAnchura(grafo, 0);

        assertEquals(List.of(0, 1, 2), resultado.getOrdenVisita());
    }

    @Test
    void dijkstraDesdeDocumento() {
        GrafoModelo grafo = servicio.convertirMatrizAGrafo(matrizDelDocumento());

        ResultadoCaminoCorto resultado = servicio.dijkstra(grafo, 0, 1);

        assertTrue(resultado.esAlcanzable());
        assertEquals(4, resultado.getDistanciaTotal());
        assertEquals(List.of(0, 1), resultado.getCamino());
    }

    @Test
    void dijkstraCaminoIndirecto() {
        MatrizAdyacencia m = new MatrizAdyacencia(3);
        m.setValor(0, 1, 4); m.setValor(1, 0, 4);
        m.setValor(0, 2, 2); m.setValor(2, 0, 2);
        m.setValor(1, 2, 1); m.setValor(2, 1, 1);

        GrafoModelo grafo = servicio.convertirMatrizAGrafo(m);
        ResultadoCaminoCorto resultado = servicio.dijkstra(grafo, 0, 1);

        assertEquals(3, resultado.getDistanciaTotal());
        assertEquals(List.of(0, 2, 1), resultado.getCamino());
    }

    @Test
    void dijkstraIddInexistenteNoEsAlcanzable() {
        GrafoModelo grafo = servicio.convertirMatrizAGrafo(matrizDelDocumento());

        ResultadoCaminoCorto resultado = servicio.dijkstra(grafo, 0, 99);

        assertFalse(resultado.esAlcanzable());
    }

    @Test
    void recorrerConIdInexistenteDevuelveListaVacia() {
        GrafoModelo grafo = servicio.convertirMatrizAGrafo(matrizDelDocumento());

        assertTrue(servicio.recorrerProfundidad(grafo, 99).getOrdenVisita().isEmpty());
        assertTrue(servicio.recorrerAnchura(grafo, 99).getOrdenVisita().isEmpty());
    }

    @Test
    void convertirGrafoVacioLanzaExcepcion() {
        GrafoModelo grafo = new GrafoModelo();

        assertThrows(IllegalArgumentException.class,
                () -> servicio.convertirGrafoAMatriz(grafo));
    }
}
