package com.example.grafosjava.model;

/**
 * Almacena los valores de una matriz cuadrada de adyacencia.
 *
 * <p>La matriz representa un grafo no dirigido y ponderado: el valor {@code 0}
 * en la celda {@code (i, j)} indica que no hay arista entre los vértices
 * {@code i} e {@code j}, mientras que un entero positivo representa el peso
 * de la arista. La matriz siempre es cuadrada y su diagonal es cero (no se
 * permiten lazos).</p>
 *
 * <p>La validación de simetría, diagonal y rango de valores se centraliza en
 * {@code ValidadorMatriz}; esta clase se limita a almacenar y exponer los
 * valores con control de índices y copias profundas.</p>
 *
 * <p>Responsabilidad: integrante A. Pertenece al modelo y no depende de
 * JavaFX ni de SmartGraph.</p>
 */
public class MatrizAdyacencia {

    /** Valores de la matriz cuadrada ({@code 0} = sin arista, entero positivo = peso). */
    private final int[][] matriz;

    /** Dimensión de la matriz (cantidad de vértices representados). */
    private final int cantidadVertices;

    /**
     * Crea una matriz de adyacencia cuadrada de tamaño
     * {@code cantidadVertices × cantidadVertices}, inicializada con ceros
     * (sin aristas).
     *
     * @param cantidadVertices número de vértices; debe ser un entero positivo ({@code > 0})
     * @throws IllegalArgumentException si {@code cantidadVertices} no es positivo
     */
    public MatrizAdyacencia(int cantidadVertices) {
        if (cantidadVertices <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de vértices debe ser un entero positivo (mayor que 0).");
        }
        this.cantidadVertices = cantidadVertices;
        this.matriz = new int[cantidadVertices][cantidadVertices];
    }

    /**
     * @return la cantidad de vértices que representa la matriz (dimensión de un lado)
     */
    public int getCantidadVertices() {
        return cantidadVertices;
    }

    /**
     * Devuelve el valor almacenado en la celda {@code (fila, columna)}.
     *
     * @param fila    índice de fila (base 0)
     * @param columna índice de columna (base 0)
     * @return el valor en la celda: {@code 0} (sin arista) o un entero positivo (peso)
     * @throws IllegalArgumentException si los índices están fuera del rango
     *         {@code [0, cantidadVertices - 1]}
     */
    public int getValor(int fila, int columna) {
        validarIndices(fila, columna);
        return matriz[fila][columna];
    }

    /**
     * Asigna un valor a la celda {@code (fila, columna)}.
     *
     * <p>El valor debe ser {@code 0} (ausencia de arista) o un entero positivo
     * (peso de la arista). Los valores negativos no son válidos.</p>
     *
     * @param fila    índice de fila (base 0)
     * @param columna índice de columna (base 0)
     * @param valor   valor a asignar; debe ser {@code >= 0}
     * @throws IllegalArgumentException si los índices están fuera de rango
     *         o si el valor es negativo
     */
    public void setValor(int fila, int columna, int valor) {
        validarIndices(fila, columna);
        if (valor < 0) {
            throw new IllegalArgumentException(
                    "El valor de la celda debe ser 0 (sin arista) o un entero positivo (peso). No se admiten negativos.");
        }
        matriz[fila][columna] = valor;
    }

    /**
     * Devuelve una copia profunda de la matriz interna para que otra clase
     * no pueda alterar los valores sin control.
     *
     * @return una nueva matriz bidimensional con los mismos valores
     */
    public int[][] getMatriz() {
        int[][] copia = new int[cantidadVertices][cantidadVertices];
        for (int i = 0; i < cantidadVertices; i++) {
            System.arraycopy(matriz[i], 0, copia[i], 0, cantidadVertices);
        }
        return copia;
    }

    /**
     * Verifica que los índices fila y columna estén dentro del rango válido.
     *
     * @param fila    índice de fila
     * @param columna índice de columna
     * @throws IllegalArgumentException si algún índice está fuera de rango
     */
    private void validarIndices(int fila, int columna) {
        if (fila < 0 || fila >= cantidadVertices) {
            throw new IllegalArgumentException(
                    "El índice de fila " + fila + " está fuera de rango [0, "
                            + (cantidadVertices - 1) + "].");
        }
        if (columna < 0 || columna >= cantidadVertices) {
            throw new IllegalArgumentException(
                    "El índice de columna " + columna + " está fuera de rango [0, "
                            + (cantidadVertices - 1) + "].");
        }
    }
}
