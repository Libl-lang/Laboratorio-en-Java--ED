package com.example.grafosjava.logic;

import com.example.grafosjava.model.Arista;
import com.example.grafosjava.model.GrafoModelo;
import com.example.grafosjava.model.MatrizAdyacencia;
import com.example.grafosjava.model.Vertice;

/**
 * Genera una {@link MatrizAdyacencia} a partir de un {@link GrafoModelo}.
 *
 * <p>El tamaño de la matriz se determina a partir del ID máximo de los vértices
 * más uno, preservando la correspondencia entre índices de filas/columnas y
 * IDs de vértices incluso si algunos IDs intermedios fueron eliminados.</p>
 *
 * <p>El peso de cada arista se escribe en las posiciones simétricas
 * {@code (origen.id, destino.id)} y {@code (destino.id, origen.id)}.</p>
 *
 * <p>Responsabilidad: integrante A. Pertenece a la capa de lógica y no
 * depende de JavaFX ni de SmartGraph.</p>
 */
public class GeneradorMatriz {

    /**
     * Crea una matriz de adyacencia que representa el grafo dado.
     *
     * @param grafo grafo a convertir
     * @return la matriz de adyacencia resultante
     * @throws IllegalArgumentException si el grafo está vacío
     */
    public MatrizAdyacencia generar(GrafoModelo grafo) {
        int maxId = -1;
        for (Vertice vertice : grafo.obtenerVertices()) {
            if (vertice.getId() > maxId) {
                maxId = vertice.getId();
            }
        }

        if (maxId == -1) {
            throw new IllegalArgumentException(
                    "No se puede generar una matriz para un grafo vacío.");
        }

        MatrizAdyacencia matriz = new MatrizAdyacencia(maxId + 1);

        for (Arista arista : grafo.obtenerAristas()) {
            int origen = arista.getOrigen().getId();
            int destino = arista.getDestino().getId();
            int peso = arista.getPeso();
            matriz.setValor(origen, destino, peso);
            matriz.setValor(destino, origen, peso);
        }

        return matriz;
    }
}
