package com.example.grafosjava.logic;

import com.example.grafosjava.model.GrafoModelo;
import com.example.grafosjava.model.MatrizAdyacencia;
import com.example.grafosjava.model.ResultadoCaminoCorto;
import com.example.grafosjava.model.ResultadoRecorrido;

/**
 * Servicio que coordina validación, conversiones, recorridos y cálculo de
 * caminos más cortos sobre el grafo.
 *
 * <p>Actúa como punto único de acceso para la lógica del grafo. Delega a las
 * clases especializadas ({@link ValidadorMatriz}, {@link ConstructorGrafo},
 * {@link GeneradorMatriz}, {@link RecorridoProfundidad}, {@link RecorridoAncho}
 * y {@link RecorridoCaminoCorto}) y expone un contrato común acordado por ambos
 * integrantes.</p>
 *
 * <p>Responsabilidad: integrante A (contrato acordado por ambos). Pertenece
 * a la capa de lógica y no depende de JavaFX ni de SmartGraph.</p>
 */
public class GrafoServicio {

    private final ValidadorMatriz validador = new ValidadorMatriz();
    private final ConstructorGrafo constructor = new ConstructorGrafo();
    private final GeneradorMatriz generador = new GeneradorMatriz();
    private final RecorridoProfundidad recorridoProfundidad = new RecorridoProfundidad();
    private final RecorridoAncho recorridoAncho = new RecorridoAncho();
    private final RecorridoCaminoCorto recorridoCaminoCorto = new RecorridoCaminoCorto();

    /**
     * Valida una matriz de adyacencia según las reglas acordadas.
     *
     * @param matriz matriz a validar
     * @return resultado con la lista de errores (vacía si es válida)
     */
    public ResultadoValidacion validarMatriz(MatrizAdyacencia matriz) {
        return validador.validar(matriz);
    }

    /**
     * Convierte una matriz de adyacencia en un {@link GrafoModelo}.
     *
     * <p>Valida la matriz antes de construir. Si la matriz es inválida, lanza
     * {@link IllegalArgumentException} con los errores encontrados; no produce
     * un grafo parcial.</p>
     *
     * @param matriz matriz a convertir (debe ser válida)
     * @return el grafo modelo construido
     * @throws IllegalArgumentException si la matriz no es válida
     */
    public GrafoModelo convertirMatrizAGrafo(MatrizAdyacencia matriz) {
        ResultadoValidacion resultado = validarMatriz(matriz);
        if (!resultado.esValida()) {
            throw new IllegalArgumentException(
                    "No se puede convertir la matriz a grafo porque es inválida:\n"
                            + resultado.getErroresComoTexto());
        }
        return constructor.construir(matriz);
    }

    /**
     * Genera la matriz de adyacencia correspondiente a un {@link GrafoModelo}.
     *
     * @param grafo grafo a convertir
     * @return la matriz de adyacencia resultante
     * @throws IllegalArgumentException si el grafo está vacío
     */
    public MatrizAdyacencia convertirGrafoAMatriz(GrafoModelo grafo) {
        return generador.generar(grafo);
    }

    /**
     * Ejecuta búsqueda en profundidad (DFS) desde un vértice.
     *
     * <p>Los vecinos se visitan en orden ascendente de ID para que los
     * resultados sean reproducibles.</p>
     *
     * @param grafo    grafo sobre el que recorrer
     * @param idInicio ID del vértice origen
     * @return resultado con el orden de visita (vacío si el vértice no existe)
     */
    public ResultadoRecorrido recorrerProfundidad(GrafoModelo grafo, int idInicio) {
        return recorridoProfundidad.recorrer(grafo, idInicio);
    }

    /**
     * Ejecuta búsqueda en anchura (BFS) desde un vértice.
     *
     * <p>Los vecinos se visitan en orden ascendente de ID para que los
     * resultados sean reproducibles.</p>
     *
     * @param grafo    grafo sobre el que recorrer
     * @param idInicio ID del vértice origen
     * @return resultado con el orden de visita (vacío si el vértice no existe)
     */
    public ResultadoRecorrido recorrerAnchura(GrafoModelo grafo, int idInicio) {
        return recorridoAncho.recorrer(grafo, idInicio);
    }

    /**
     * Ejecuta el algoritmo de Dijkstra para encontrar el camino más corto
     * entre dos vértices.
     *
     * <p>Todos los pesos de arista deben ser enteros positivos para que
     * el algoritmo funcione correctamente.</p>
     *
     * @param grafo     grafo ponderado
     * @param idOrigen  ID del vértice origen
     * @param idDestino ID del vértice destino
     * @return resultado con el camino, distancia total y si es alcanzable
     */
    public ResultadoCaminoCorto dijkstra(GrafoModelo grafo, int idOrigen, int idDestino) {
        return recorridoCaminoCorto.dijkstra(grafo, idOrigen, idDestino);
    }
}
