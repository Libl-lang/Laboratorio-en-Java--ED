package com.example.grafosjava.logic;

import com.example.grafosjava.model.GrafoModelo;
import com.example.grafosjava.model.MatrizAdyacencia;
import com.example.grafosjava.model.Vertice;

/**
 * Convierte una {@link MatrizAdyacencia} válida en un {@link GrafoModelo}.
 *
 * <p>Recorre solo la mitad superior de la matriz ({@code i < j}) para no
 * duplicar aristas. Los vértices se crean con IDs desde {@code 0} hasta
 * {@code cantidadVertices - 1}. El peso de cada arista proviene del valor
 * de la celda correspondiente.</p>
 *
 * <p>Responsabilidad: integrante A. Pertenece a la capa de lógica y no
 * depende de JavaFX ni de SmartGraph.</p>
 */
public class ConstructorGrafo {

    /**
     * Construye un {@link GrafoModelo} a partir de una matriz de adyacencia.
     *
     * <p>Se asume que la matriz ya fue validada por {@link ValidadorMatriz}.
     * Si la matriz no es válida, el comportamiento no está garantizado.</p>
     *
     * @param matriz matriz de adyacencia (asumida válida)
     * @return el grafo modelo construido
     */
    public GrafoModelo construir(MatrizAdyacencia matriz) {
        int n = matriz.getCantidadVertices();
        GrafoModelo grafo = new GrafoModelo();

        // Crear vértices con IDs 0..n-1.
        for (int i = 0; i < n; i++) {
            Vertice vertice = new Vertice(i, "V" + i);
            grafo.agregarVertice(vertice);
        }

        // Crear aristas desde la mitad superior (i < j) para no duplicar.
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int peso = matriz.getValor(i, j);
                if (peso > 0) {
                    grafo.agregarArista(i, j, peso);
                }
            }
        }

        return grafo;
    }
}
