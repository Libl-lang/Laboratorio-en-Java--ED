package com.example.grafosjava.logic;

import com.example.grafosjava.model.MatrizAdyacencia;

import java.util.ArrayList;
import java.util.List;

/**
 * Convierte el texto ingresado por el usuario en una {@link MatrizAdyacencia}
 * (Integrante A).
 *
 * <p>Detecta errores de formato: caracteres no numéricos, filas vacías, filas
 * con distinta cantidad de valores y texto vacío. La regla de valores 0 o 1 la
 * revisa {@link ValidadorMatriz}, no este parser.</p>
 */
public class ParserMatriz {

    /**
     * Parsea el texto de la matriz: una fila por línea, valores separados por
     * espacios.
     *
     * @param texto texto tal como lo escribió el usuario
     * @return la matriz construida
     * @throws FormatoMatrizInvalidoException si el texto no tiene formato válido
     */
    public MatrizAdyacencia parsear(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new FormatoMatrizInvalidoException(
                    "No se ingresó ninguna matriz. Escriba una fila por línea, con valores 0 o 1 separados por espacios.");
        }

        String[] lineas = texto.trim().split("\\r?\\n|\\r");
        List<int[]> filas = new ArrayList<>();
        int anchoEsperado = -1;

        for (int i = 0; i < lineas.length; i++) {
            String contenido = lineas[i].trim();
            if (contenido.isEmpty()) {
                throw new FormatoMatrizInvalidoException(
                        "La fila " + i + " está vacía. Escriba una fila por línea.");
            }

            String[] tokens = contenido.split("\\s+");
            int[] fila = new int[tokens.length];
            for (int j = 0; j < tokens.length; j++) {
                try {
                    fila[j] = Integer.parseInt(tokens[j]);
                } catch (NumberFormatException e) {
                    throw new FormatoMatrizInvalidoException(
                            "La celda (" + i + ", " + j + ") contiene '" + tokens[j] + "', que no es un número.");
                }
            }

            if (anchoEsperado == -1) {
                anchoEsperado = fila.length;
            } else if (fila.length != anchoEsperado) {
                throw new FormatoMatrizInvalidoException(
                        "Las filas tienen distinta cantidad de valores: la fila 0 tiene " + anchoEsperado
                                + " y la fila " + i + " tiene " + fila.length + ".");
            }

            filas.add(fila);
        }

        int[][] valores = new int[filas.size()][];
        for (int i = 0; i < filas.size(); i++) {
            valores[i] = filas.get(i);
        }
        return new MatrizAdyacencia(valores);
    }
}