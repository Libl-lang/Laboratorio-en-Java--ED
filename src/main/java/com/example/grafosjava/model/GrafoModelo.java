package com.example.grafosjava.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mantiene los datos principales del grafo en memoria y administra las operaciones
 * sobre sus vértices y aristas.
 *
 * <p>El modelo no dibuja el grafo, no muestra ventanas y no depende de SmartGraph ni de
 * JavaFX. Sirve como fuente común de datos para el resto de componentes del laboratorio
 * (dibujo, lista de pesos, matriz de adyacencia y recorridos).</p>
 *
 * <p>Responsabilidad: integrante B.</p>
 */
public class GrafoModelo {

    /** Vértices indexados por su identificador (orden de inserción, identificadores estables). */
    private final Map<Integer, Vertice> vertices;

    /** Aristas no dirigidas del grafo (una única arista lógica por conexión). */
    private final List<Arista> aristas;

    /**
     * Crea un grafo vacío.
     */
    public GrafoModelo() {
        this.vertices = new LinkedHashMap<>();
        this.aristas = new ArrayList<>();
    }

    /**
     * Agrega un vértice al grafo.
     *
     * @param vertice vértice a agregar; no puede ser {@code null}
     * @return {@code true} si se agregó; {@code false} si es {@code null} o si ya existe
     *         un vértice con el mismo identificador
     */
    public boolean agregarVertice(Vertice vertice) {
        if (vertice == null) {
            return false;
        }
        if (vertices.containsKey(vertice.getId())) {
            return false;
        }
        vertices.put(vertice.getId(), vertice);
        return true;
    }

    /**
     * Elimina un vértice por su identificador y todas las aristas conectadas a él.
     *
     * @param id identificador del vértice a eliminar
     * @return {@code true} si existía y fue eliminado; {@code false} si no existe
     */
    public boolean eliminarVertice(int id) {
        if (!vertices.containsKey(id)) {
            return false;
        }
        vertices.remove(id);
        aristas.removeIf(a -> a.getOrigen().getId() == id || a.getDestino().getId() == id);
        return true;
    }

    /**
     * @param id identificador del vértice
     * @return el vértice con ese identificador, o {@code null} si no existe
     */
    public Vertice obtenerVertice(int id) {
        return vertices.get(id);
    }

    /**
     * @return una vista no modificable de los vértices del grafo
     */
    public Collection<Vertice> obtenerVertices() {
        return Collections.unmodifiableCollection(vertices.values());
    }

    /**
     * @return una vista no modificable de las aristas del grafo
     */
    public List<Arista> obtenerAristas() {
        return Collections.unmodifiableList(aristas);
    }

    /**
     * Agrega una arista no dirigida entre dos vértices existentes.
     *
     * <p>No se permiten auto-conexiones, aristas hacia vértices inexistentes ni aristas
     * duplicadas para una misma conexión (independientemente del orden de los identificadores).</p>
     *
     * @param idOrigen  identificador del vértice de origen
     * @param idDestino identificador del vértice de destino
     * @param peso      peso de la arista; debe ser un entero positivo
     * @return {@code true} si se agregó la arista; {@code false} si los datos son inválidos,
     *         los vértices no existen, la conexión es un auto-lazo, ya existe la arista o el
     *         peso no es válido
     */
    public boolean agregarArista(int idOrigen, int idDestino, int peso) {
        if (idOrigen == idDestino) {
            return false;
        }
        Vertice origen = vertices.get(idOrigen);
        Vertice destino = vertices.get(idDestino);
        if (origen == null || destino == null) {
            return false;
        }
        if (existeArista(idOrigen, idDestino)) {
            return false;
        }
        Arista arista;
        try {
            arista = new Arista(origen, destino, peso);
        } catch (IllegalArgumentException e) {
            return false;
        }
        aristas.add(arista);
        return true;
    }

    /**
     * Elimina la arista que conecta los dos identificadores dados, sin importar el orden.
     *
     * @param idOrigen  identificador de uno de los vértices
     * @param idDestino identificador del otro vértice
     * @return {@code true} si existía la arista y fue eliminada; {@code false} en caso contrario
     */
    public boolean eliminarArista(int idOrigen, int idDestino) {
        return aristas.removeIf(a -> a.conecta(idOrigen, idDestino));
    }

    /**
     * Indica si existe una arista que conecta los dos identificadores dados, sin importar el orden.
     *
     * @param idOrigen  identificador de uno de los vértices
     * @param idDestino identificador del otro vértice
     * @return {@code true} si la conexión existe; {@code false} en caso contrario
     */
    public boolean existeArista(int idOrigen, int idDestino) {
        return aristas.stream().anyMatch(a -> a.conecta(idOrigen, idDestino));
    }

    /**
     * Devuelve los vecinos de un vértice en orden ascendente de identificador.
     *
     * @param idVertice identificador del vértice
     * @return una lista nueva con los vértices vecinos ordenados por identificador ascendente;
     *         lista vacía si el vértice no existe o no tiene vecinos
     */
    public List<Vertice> obtenerVecinos(int idVertice) {
        List<Vertice> vecinos = new ArrayList<>();
        if (!vertices.containsKey(idVertice)) {
            return vecinos;
        }
        for (Arista a : aristas) {
            if (a.getOrigen().getId() == idVertice) {
                vecinos.add(a.getDestino());
            } else if (a.getDestino().getId() == idVertice) {
                vecinos.add(a.getOrigen());
            }
        }
        vecinos.sort(Comparator.comparingInt(Vertice::getId));
        return vecinos;
    }

    /**
     * @return la cantidad de vértices del grafo
     */
    public int contarVertices() {
        return vertices.size();
    }

    /**
     * @return la cantidad de aristas del grafo
     */
    public int contarAristas() {
        return aristas.size();
    }

    /**
     * Actualiza el peso de la arista existente entre los dos identificadores dados.
     *
     * <p>Modifica la arista ya existente; no crea una nueva.</p>
     *
     * @param idOrigen  identificador de uno de los vértices
     * @param idDestino identificador del otro vértice
     * @param nuevoPeso nuevo peso; debe ser un entero positivo
     * @return {@code true} si la arista existe y su peso fue actualizado; {@code false} si no
     *         existe la arista o si el nuevo peso no es válido
     */
    public boolean actualizarPesoArista(int idOrigen, int idDestino, int nuevoPeso) {
        for (Arista a : aristas) {
            if (a.conecta(idOrigen, idDestino)) {
                try {
                    a.setPeso(nuevoPeso);
                } catch (IllegalArgumentException e) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }
}
