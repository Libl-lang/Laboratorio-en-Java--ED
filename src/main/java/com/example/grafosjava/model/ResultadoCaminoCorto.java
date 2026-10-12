package com.example.grafosjava.model;

import java.util.List;

/**
 * Resultado del algoritmo de Dijkstra para encontrar el camino más corto
 * entre dos vértices de un grafo ponderado.
 *
 * <p>Contiene el camino (lista de IDs de vértices desde el origen hasta el
 * destino), la distancia total acumulada y un indicador de si el destino es
 * alcanzable desde el origen.</p>
 *
 * <p>Responsabilidad: integrante A. Pertenece al modelo y no depende de
 * JavaFX ni de SmartGraph.</p>
 */
public class ResultadoCaminoCorto {

    /** ID del vértice origen. */
    private final int idOrigen;

    /** ID del vértice destino. */
    private final int idDestino;

    /** Lista de IDs que forman el camino (inmutable; vacía si no es alcanzable). */
    private final List<Integer> camino;

    /** Peso total del camino (suma de pesos de las aristas). */
    private final int distanciaTotal;

    /** Indica si el destino es alcanzable desde el origen. */
    private final boolean alcanzable;

    /**
     * Crea un resultado de camino más corto.
     *
     * @param idOrigen      ID del vértice origen
     * @param idDestino     ID del vértice destino
     * @param camino        lista de IDs que forman el camino
     * @param distanciaTotal peso total del camino
     * @param alcanzable    {@code true} si el destino es alcanzable
     */
    public ResultadoCaminoCorto(int idOrigen, int idDestino, List<Integer> camino,
                                int distanciaTotal, boolean alcanzable) {
        this.idOrigen = idOrigen;
        this.idDestino = idDestino;
        this.camino = camino == null
                ? List.of()
                : List.copyOf(camino);
        this.distanciaTotal = distanciaTotal;
        this.alcanzable = alcanzable;
    }

    /** @return el ID del vértice origen */
    public int getIdOrigen() {
        return idOrigen;
    }

    /** @return el ID del vértice destino */
    public int getIdDestino() {
        return idDestino;
    }

    /** @return la lista inmutable de IDs que forman el camino (vacía si no es alcanzable) */
    public List<Integer> getCamino() {
        return camino;
    }

    /** @return el peso total del camino */
    public int getDistanciaTotal() {
        return distanciaTotal;
    }

    /** @return {@code true} si el destino es alcanzable desde el origen */
    public boolean esAlcanzable() {
        return alcanzable;
    }
}
