package com.example.grafosjava.logic;

import com.example.grafosjava.model.Arista;
import com.example.grafosjava.model.GrafoModelo;
import com.example.grafosjava.model.ResultadoCaminoCorto;
import com.example.grafosjava.model.Vertice;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Implementa el algoritmo de Dijkstra para encontrar el camino más corto
 * entre dos vértices de un {@link GrafoModelo} ponderado.
 *
 * <p>Los vértices se procesan en orden de distancia creciente mediante una
 * cola de prioridad. Los vecinos se exploran en orden ascendente de ID
 * (gracias a {@link GrafoModelo#obtenerVecinos(int)} que ya ordena por ID).</p>
 *
 * <p>Todos los pesos de arista deben ser enteros positivos para que
 * Dijkstra funcione correctamente.</p>
 *
 * <p>Responsabilidad: integrante A. Pertenece a la capa de lógica y no
 * depende de JavaFX ni de SmartGraph.</p>
 */
public class RecorridoCaminoCorto {

    /**
     * Ejecuta Dijkstra desde {@code idOrigen} hacia {@code idDestino}.
     *
     * <p>Si el origen o el destino no existen en el grafo, el resultado
     * indica que no es alcanzable.</p>
     *
     * @param grafo     grafo ponderado (todos los pesos son enteros positivos)
     * @param idOrigen  ID del vértice origen
     * @param idDestino ID del vértice destino
     * @return resultado con el camino, distancia total y si es alcanzable
     */
    public ResultadoCaminoCorto dijkstra(GrafoModelo grafo, int idOrigen, int idDestino) {
        Vertice origen = grafo.obtenerVertice(idOrigen);
        Vertice destino = grafo.obtenerVertice(idDestino);

        if (origen == null || destino == null) {
            return new ResultadoCaminoCorto(idOrigen, idDestino, List.of(), 0, false);
        }

        if (idOrigen == idDestino) {
            return new ResultadoCaminoCorto(idOrigen, idDestino, List.of(idOrigen), 0, true);
        }

        // Distancias mínimas conocidas (inicialmente infinito, excepto el origen).
        Map<Integer, Integer> distancias = new HashMap<>();
        // Predecesores para reconstruir el camino.
        Map<Integer, Integer> predecesores = new HashMap<>();

        for (Vertice v : grafo.obtenerVertices()) {
            distancias.put(v.getId(), Integer.MAX_VALUE);
        }
        distancias.put(idOrigen, 0);

        // Cola de prioridad ordenada por distancia creciente.
        PriorityQueue<NodoDijkstra> pq = new PriorityQueue<>();
        pq.offer(new NodoDijkstra(idOrigen, 0));

        while (!pq.isEmpty()) {
            NodoDijkstra nodo = pq.poll();
            int idActual = nodo.id;

            // Si ya encontramos un camino mejor a este nodo, lo ignoramos.
            if (nodo.distancia > distancias.get(idActual)) {
                continue;
            }

            // Hemos alcanzado el destino: no hace falta seguir.
            if (idActual == idDestino) {
                break;
            }

            for (Vertice vecino : grafo.obtenerVecinos(idActual)) {
                int idVecino = vecino.getId();
                int peso = obtenerPesoArista(grafo, idActual, idVecino);
                int nuevaDistancia = distancias.get(idActual) + peso;

                if (nuevaDistancia < distancias.get(idVecino)) {
                    distancias.put(idVecino, nuevaDistancia);
                    predecesores.put(idVecino, idActual);
                    pq.offer(new NodoDijkstra(idVecino, nuevaDistancia));
                }
            }
        }

        // El destino no es alcanzable.
        if (!predecesores.containsKey(idDestino)) {
            return new ResultadoCaminoCorto(idOrigen, idDestino, List.of(), 0, false);
        }

        // Reconstruir el camino siguiendo los predecesores desde el destino.
        Deque<Integer> pila = new ArrayDeque<>();
        int actual = idDestino;
        while (actual != idOrigen) {
            pila.push(actual);
            actual = predecesores.get(actual);
        }
        pila.push(idOrigen);

        List<Integer> camino = new ArrayList<>(pila);
        int distanciaTotal = distancias.get(idDestino);

        return new ResultadoCaminoCorto(idOrigen, idDestino, camino, distanciaTotal, true);
    }

    /**
     * Obtiene el peso de la arista que conecta dos vértices.
     *
     * @param grafo grafo donde buscar
     * @param idA   ID de uno de los vértices
     * @param idB   ID del otro vértice
     * @return el peso de la arista, o {@code 0} si no existe conexión directa
     */
    private int obtenerPesoArista(GrafoModelo grafo, int idA, int idB) {
        for (Arista arista : grafo.obtenerAristas()) {
            if (arista.conecta(idA, idB)) {
                return arista.getPeso();
            }
        }
        return 0;
    }

    /**
     * Nodo interno para la cola de prioridad de Dijkstra.
     * Ordena por distancia creciente.
     */
    private static final class NodoDijkstra implements Comparable<NodoDijkstra> {
        final int id;
        final int distancia;

        NodoDijkstra(int id, int distancia) {
            this.id = id;
            this.distancia = distancia;
        }

        @Override
        public int compareTo(NodoDijkstra otro) {
            return Integer.compare(this.distancia, otro.distancia);
        }
    }
}
