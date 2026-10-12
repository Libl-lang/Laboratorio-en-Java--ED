package com.example.grafosjava.model;

import java.util.List;

/**
 * Resultado de un recorrido (DFS o BFS) sobre un grafo.
 *
 * <p>Contiene el ID del vértice de inicio y el orden en que se visitaron
 * los vértices alcanzables desde él. La lista es inmutable.</p>
 *
 * <p>Responsabilidad: integrante A. Pertenece al modelo y no depende de
 * JavaFX ni de SmartGraph.</p>
 */
public class ResultadoRecorrido {

    /** ID del vértice desde el cual se inició el recorrido. */
    private final int idInicio;

    /** Orden de visita de los vértices (inmutable; vacío si idInicio no existe). */
    private final List<Integer> ordenVisita;

    /**
     * Crea un resultado de recorrido.
     *
     * @param idInicio    ID del vértice origen
     * @param ordenVisita orden de visita de los IDs; se hace una copia inmutable
     */
    public ResultadoRecorrido(int idInicio, List<Integer> ordenVisita) {
        this.idInicio = idInicio;
        this.ordenVisita = ordenVisita == null
                ? List.of()
                : List.copyOf(ordenVisita);
    }

    /**
     * @return el ID del vértice de inicio del recorrido
     */
    public int getIdInicio() {
        return idInicio;
    }

    /**
     * @return la lista inmutable de IDs en el orden de visita
     */
    public List<Integer> getOrdenVisita() {
        return ordenVisita;
    }

    /**
     * @return la cantidad de vértices visitados
     */
    public int getCantidadVisitados() {
        return ordenVisita.size();
    }
}
