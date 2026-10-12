package com.example.grafosjava.logic;

import com.example.grafosjava.model.MatrizAdyacencia;

import java.util.ArrayList;
import java.util.List;

/**
 * Aplica las reglas de validación de la matriz de adyacencia y reporta
 * todos los errores encontrados.
 *
 * <p>Las reglas son:</p>
 * <ol>
 *   <li><b>Cuadrada y no vacía</b>: la cantidad de filas equals la cantidad de
 *       columnas y es mayor que cero.</li>
 *   <li><b>Valores válidos</b>: cada celda es {@code 0} (sin arista) o un
 *       entero positivo (peso). No se admiten negativos.</li>
 *   <li><b>Simétrica</b>: {@code matriz[i][j] == matriz[j][i]} para toda
 *       {@code i != j}.</li>
 *   <li><b>Diagonal en cero</b>: {@code matriz[i][i] == 0} para toda {@code i}
 *       (no se permiten lazos).</li>
 * </ol>
 *
 * <p>Responsabilidad: integrante A. Pertenece a la capa de lógica y no
 * depende de JavaFX ni de SmartGraph.</p>
 */
public class ValidadorMatriz {

    /**
     * Valida la matriz aplicando todas las reglas en el orden indicado.
     *
     * <p>Se reportan <b>todos</b> los errores encontrados, no solo el primero,
     * con la ubicación (fila, columna) de cada problema.</p>
     *
     * @param matriz matriz a validar
     * @return resultado con la lista completa de errores (vacía si es válida)
     */
    public ResultadoValidacion validar(MatrizAdyacencia matriz) {
        List<String> errores = new ArrayList<>();

        int n = matriz.getCantidadVertices();

        // 1. Cuadrada y no vacía.
        if (n == 0) {
            errores.add("La matriz no puede ser vacía.");
        }

        // 2. Valores válidos (>= 0: 0 = sin arista, positivo = peso).
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int valor = matriz.getValor(i, j);
                if (valor < 0) {
                    errores.add("La celda (" + i + ", " + j + ") vale " + valor
                            + "; debe ser 0 (sin arista) o un entero positivo (peso).");
                }
            }
        }

        // 3. Simétrica: matriz[i][j] == matriz[j][i].
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int a = matriz.getValor(i, j);
                int b = matriz.getValor(j, i);
                if (a != b) {
                    errores.add("La celda (" + i + ", " + j + ") vale " + a
                            + " y la celda (" + j + ", " + i + ") vale " + b
                            + "; la matriz no es simétrica.");
                }
            }
        }

        // 4. Diagonal en cero (no se permiten lazos).
        for (int i = 0; i < n; i++) {
            int valor = matriz.getValor(i, i);
            if (valor != 0) {
                errores.add("La diagonal debe ser 0: la celda (" + i + ", " + i
                        + ") vale " + valor + "; no se permiten lazos.");
            }
        }

        return ResultadoValidacion.conErrores(errores);
    }
}
