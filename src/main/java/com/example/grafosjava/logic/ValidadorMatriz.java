package com.example.grafosjava.logic;

import com.example.grafosjava.model.MatrizAdyacencia;

import java.util.ArrayList;
import java.util.List;

/**
 * Aplica las cuatro reglas de la matriz (Integrante A) y reporta TODOS los
 * errores encontrados, con fila y columna, no solo el primero:
 *
 * <ol>
 *   <li>Cuadrada: la cantidad de filas es igual a la cantidad de columnas.</li>
 *   <li>Valores válidos: cada celda es 0 o 1.</li>
 *   <li>Simétrica: la celda (i, j) es igual a la celda (j, i).</li>
 *   <li>Diagonal en cero: todas las celdas (i, i) valen 0.</li>
 * </ol>
 */
public class ValidadorMatriz {

    /**
     * Valida la matriz aplicando todas las reglas en el orden indicado arriba.
     *
     * @param matriz matriz a validar
     * @return resultado con la lista completa de errores (vacía si es válida)
     */
    public ResultadoValidacion validar(MatrizAdyacencia matriz) {
        List<String> errores = new ArrayList<>();

        int filas = matriz.n();
        int columnas = matriz.columnas();

        // 1. Cuadrada.
        if (filas != columnas) {
            errores.add("La matriz no es cuadrada: tiene " + filas + " filas y " + columnas + " columnas.");
        }

        int limite = Math.min(filas, columnas);

        // 2. Valores válidos (0 o 1), con fila y columna.
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                int valor = matriz.get(i, j);
                if (valor != 0 && valor != 1) {
                    errores.add("La celda (" + i + ", " + j + ") vale " + valor + "; debe ser 0 o 1.");
                }
            }
        }

        // 3. Simétrica.
        for (int i = 0; i < limite; i++) {
            for (int j = i + 1; j < limite; j++) {
                int a = matriz.get(i, j);
                int b = matriz.get(j, i);
                if (a != b) {
                    errores.add("La celda (" + i + ", " + j + ") vale " + a + " y la (" + j + ", " + i
                            + ") vale " + b + "; la matriz no es simétrica.");
                }
            }
        }

        // 4. Diagonal en cero.
        for (int i = 0; i < limite; i++) {
            int valor = matriz.get(i, i);
            if (valor != 0) {
                errores.add("La diagonal debe ser 0: la celda (" + i + ", " + i + ") vale " + valor + ".");
            }
        }

        return ResultadoValidacion.conErrores(errores);
    }
}