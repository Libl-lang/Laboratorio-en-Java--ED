package com.example.grafosjava.logic;

import com.example.grafosjava.model.GrafoModelo;
import com.example.grafosjava.model.ResultadoRecorrido;
import com.example.grafosjava.model.Vertice;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementa el recorrido en anchura (BFS) sobre un {@link GrafoModelo}.
 *
 * <p>Los vecinos se visitan en orden ascendente de ID (gracias a
 * {@link GrafoModelo#obtenerVecinos(int)} que ya ordena por ID), lo que
 * hace que los resultados sean reproducibles.</p>
 *
 * <p>Un recorrido desde un vértice visita solo los vértices alcanzables
 * desde ese inicio.</p>
 *
 * <p>Responsabilidad: integrante A. Pertenece a la capa de lógica y no
 * depende de JavaFX ni de SmartGraph.</p>
 */
public class RecorridoAncho {

    /**
     * Ejecuta BFS desde el vértice con el ID dado.
     *
     * @param grafo    grafo sobre el que recorrer
     * @param idInicio ID del vértice origen
     * @return resultado con el orden de visita (vacío si el vértice no existe)
     */
    public ResultadoRecorrido recorrer(GrafoModelo grafo, int idInicio) {
        if (grafo.obtenerVertice(idInicio) == null) {
            return new ResultadoRecorrido(idInicio, List.of());
        }

        List<Integer> ordenVisita = new ArrayList<>();
        Set<Integer> visitados = new HashSet<>();
        Deque<Integer> cola = new ArrayDeque<>();

        cola.offer(idInicio);
        visitados.add(idInicio);

        while (!cola.isEmpty()) {
            int idActual = cola.poll();
            ordenVisita.add(idActual);

            for (Vertice vecino : grafo.obtenerVecinos(idActual)) {
                if (!visitados.contains(vecino.getId())) {
                    visitados.add(vecino.getId());
                    cola.offer(vecino.getId());
                }
            }
        }

        return new ResultadoRecorrido(idInicio, ordenVisita);
    }
}
