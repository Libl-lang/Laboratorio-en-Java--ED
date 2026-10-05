package com.example.grafosjava.model;

/**
 * Matriz de adyacencia (datos puros) del Integrante A.
 *
 * <p>Guarda el tamaño {@code n} y los valores 0/1 de la matriz. Solo permite
 * consultar el tamaño y el valor de una celda (i, j). No aplica reglas de
 * validación ni conoce JavaFX ni SmartGraph.</p>
 */
public class MatrizAdyacencia {

    private final int[][] valores;

    /**
     * Crea la matriz a partir de un arreglo bidimensional de valores enteros.
     *
     * @param valores valores de la matriz; se copian para no exponer el arreglo original
     */
    public MatrizAdyacencia(int[][] valores) {
        this.valores = new int[valores.length][];
        for (int i = 0; i < valores.length; i++) {
            this.valores[i] = valores[i].clone();
        }
    }

    /**
     * Cantidad de filas. Para una matriz cuadrada equivale al tamaño {@code n}.
     *
     * @return número de filas
     */
    public int n() {
        return valores.length;
    }

    /**
     * Cantidad de columnas (largo de la primera fila).
     *
     * @return número de columnas
     */
    public int columnas() {
        return valores.length == 0 ? 0 : valores[0].length;
    }

    /**
     * Valor de la celda (i, j), con índices base 0.
     *
     * @param i fila
     * @param j columna
     * @return 0 o 1 (o el valor ingresado, que ValidadorMatriz revisa)
     */
    public int get(int i, int j) {
        return valores[i][j];
    }
}