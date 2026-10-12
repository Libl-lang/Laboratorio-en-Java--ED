package com.example.grafosjava.logic;

import com.example.grafosjava.model.GrafoModelo;
import com.example.grafosjava.model.ResultadoRecorrido;
import com.example.grafosjava.model.Vertice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementa el recorrido en profundidad (DFS) sobre un {@link GrafoModelo}.
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
public class RecorridoProfundidad {

    /**
     * Ejecuta DFS desde el vértice con el ID dado.
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
        dfs(grafo, idInicio, visitados, ordenVisita);

        return new ResultadoRecorrido(idInicio, ordenVisita);
    }

    /**
     * Método recursivo interno que realiza el DFS.
     *
     * <p>Marca el vértice actual como visitado, lo agrega al orden de visita y
     * recursivamente visita cada vecino no visitado.</p>
     *
     * @param grafo       grafo sobre el que recorrer
     * @param idVertice   ID del vértice actual
     * @param visitados   conjunto de IDs ya visitados
     * @param ordenVisita lista donde se acumula el orden de visita
     */
    private void dfs(GrafoModelo grafo, int idVertice,
                     Set<Integer> visitados, List<Integer> ordenVisita) {
        visitados.add(idVertice);
        ordenVisita.add(idVertice);

        for (Vertice vecino : grafo.obtenerVecinos(idVertice)) {
            if (!visitados.contains(vecino.getId())) {
                dfs(grafo, vecino.getId(), visitados, ordenVisita);
            }
        }
    }
}
