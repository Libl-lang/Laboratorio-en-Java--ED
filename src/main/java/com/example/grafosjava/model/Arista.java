package com.example.grafosjava.model;

/**
 * Representa una conexión no dirigida entre dos vértices, con un peso asociado.
 *
 * <p>Responsabilidad: integrante B. Esta clase pertenece al modelo y no depende de
 * JavaFX ni de SmartGraph.</p>
 */
public class Arista {

    /** Vértice de origen de la conexión. */
    private final Vertice origen;

    /** Vértice de destino de la conexión. */
    private final Vertice destino;

    /** Peso de la conexión. */
    private int peso;

    /**
     * Crea una arista no dirigida entre dos vértices con un peso.
     *
     * @param origen  vértice de origen; no puede ser {@code null}
     * @param destino vértice de destino; no puede ser {@code null} ni ser el mismo
     *                vértice que {@code origen}
     * @param peso    peso de la arista; debe ser un entero positivo ({@code > 0}).
     *                El valor {@code 0} representa ausencia de conexión y no es válido aquí.
     * @throws IllegalArgumentException si algún vértice es {@code null}, si ambos vértices
     *         son el mismo, o si el peso no es un entero positivo
     */
    public Arista(Vertice origen, Vertice destino, int peso) {
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("El origen y el destino de la arista no pueden ser null.");
        }
        if (origen.getId() == destino.getId()) {
            throw new IllegalArgumentException("No se permiten conexiones de un vértice consigo mismo.");
        }
        this.origen = origen;
        this.destino = destino;
        setPeso(peso);
    }

    /**
     * @return el vértice de origen de la conexión
     */
    public Vertice getOrigen() {
        return origen;
    }

    /**
     * @return el vértice de destino de la conexión
     */
    public Vertice getDestino() {
        return destino;
    }

    /**
     * @return el peso de la conexión
     */
    public int getPeso() {
        return peso;
    }

    /**
     * Actualiza el peso de la arista aplicando la misma validación que el constructor.
     *
     * @param peso nuevo peso; debe ser un entero positivo ({@code > 0})
     * @throws IllegalArgumentException si el peso no es un entero positivo
     */
    public void setPeso(int peso) {
        if (peso <= 0) {
            throw new IllegalArgumentException("El peso de la arista debe ser un entero positivo (mayor que 0).");
        }
        this.peso = peso;
    }

    /**
     * Indica si la arista conecta los dos identificadores dados, reconociendo la conexión
     * independientemente del orden de los identificadores.
     *
     * <p>Por ejemplo, una arista entre los vértices 2 y 5 se reconoce tanto con
     * {@code (2, 5)} como con {@code (5, 2)}.</p>
     *
     * @param idA identificador de uno de los vértices
     * @param idB identificador del otro vértice
     * @return {@code true} si la arista une ambos identificadores en cualquier orden
     */
    public boolean conecta(int idA, int idB) {
        return (origen.getId() == idA && destino.getId() == idB)
                || (origen.getId() == idB && destino.getId() == idA);
    }
}
