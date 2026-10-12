package com.example.grafosjava.model;

/**
 * Representa un nodo (vértice) del grafo.
 *
 * <p>La identidad del vértice está determinada únicamente por su {@code id}, el cual
 * es inmutable. El {@code nombre} es una etiqueta visible y nunca se utiliza como
 * identificador.</p>
 *
 * <p>Responsabilidad: integrante B. Esta clase pertenece al modelo y no depende de
 * JavaFX ni de SmartGraph.</p>
 */
public class Vertice {

    /** Identificador estable e inmutable del vértice. */
    private final int id;

    /** Nombre visible del vértice. */
    private String nombre;

    /**
     * Crea un vértice con un identificador estable y un nombre visible.
     *
     * @param id     identificador del vértice; no se modifica tras la construcción
     * @param nombre nombre visible; no puede ser {@code null}, vacío ni contener
     *               únicamente espacios en blanco (p. ej. {@code "   "}). Los espacios
     *               entre palabras sí están permitidos (p. ej. {@code "Vertice A"}).
     * @throws IllegalArgumentException si el nombre no cumple las reglas del contrato
     */
    public Vertice(int id, String nombre) {
        this.id = id;
        setNombre(nombre);
    }

    /**
     * @return el identificador inmutable del vértice
     */
    public int getId() {
        return id;
    }

    /**
     * @return el nombre visible del vértice
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Actualiza el nombre visible del vértice aplicando la misma validación que el constructor.
     *
     * @param nombre nuevo nombre; no puede ser {@code null}, vacío ni contener
     *               únicamente espacios en blanco
     * @throws IllegalArgumentException si el nombre no cumple las reglas del contrato
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre del vértice no puede ser null, vacío ni contener solo espacios en blanco.");
        }
        this.nombre = nombre;
    }
}
