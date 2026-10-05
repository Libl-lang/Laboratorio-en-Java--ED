package com.example.grafosjava.logic;

import com.example.grafosjava.model.MatrizAdyacencia;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del Integrante A para {@link ValidadorMatriz}, con los casos de la
 * tabla de la sección 7 del documento (revisión cruzada).
 */
class ValidadorMatrizTest {

    private final ValidadorMatriz validador = new ValidadorMatriz();

    private MatrizAdyacencia matriz(int[][] valores) {
        return new MatrizAdyacencia(valores);
    }

    @Test
    void matriz3x3ConAristasEn01Y02EsValida() {
        // Sección 7: 3×3 con unos en (0,1), (1,0), (0,2), (2,0).
        MatrizAdyacencia m = matriz(new int[][]{
                {0, 1, 1},
                {1, 0, 0},
                {1, 0, 0}
        });

        ResultadoValidacion resultado = validador.validar(m);

        assertTrue(resultado.esValida());
        assertTrue(resultado.getErrores().isEmpty());
    }

    @Test
    void matrizCon3FilasY2ColumnasNoEsCuadrada() {
        // Sección 7: "Matriz con 3 filas y 2 columnas → la matriz no es cuadrada".
        MatrizAdyacencia m = matriz(new int[][]{
                {0, 1},
                {1, 0},
                {0, 1}
        });

        ResultadoValidacion resultado = validador.validar(m);

        assertFalse(resultado.esValida());
        assertTrue(resultado.getErrores().get(0).contains("no es cuadrada"));
    }

    @Test
    void valorDistintoDe0O1SeReportaConFilaYColumna() {
        // Sección 7: "Matriz con un valor 2 en una celda". El espejo también vale 2
        // para no sumar un error de simetría; ambas celdas deben reportarse.
        MatrizAdyacencia m = matriz(new int[][]{
                {0, 2, 0},
                {2, 0, 0},
                {0, 0, 0}
        });

        ResultadoValidacion resultado = validador.validar(m);
        List<String> errores = resultado.getErrores();

        assertFalse(resultado.esValida());
        assertEquals(2, errores.size(), "Cada celda con 2 se reporta con su fila y columna: " + errores);
        assertTrue(errores.stream().anyMatch(e -> e.contains("(0, 1)")));
        assertTrue(errores.stream().anyMatch(e -> e.contains("(1, 0)")));
        assertTrue(errores.stream().allMatch(e -> e.contains("0 o 1")));
    }

    @Test
    void matrizCon1En01Y0En10NoEsSimetrica() {
        // Sección 7: "Un 1 en (0,1) y un 0 en (1,0)".
        MatrizAdyacencia m = matriz(new int[][]{
                {0, 1},
                {0, 0}
        });

        ResultadoValidacion resultado = validador.validar(m);

        assertFalse(resultado.esValida());
        assertEquals(1, resultado.getErrores().size());
        assertTrue(resultado.getErrores().get(0).contains("simétric"));
    }

    @Test
    void diagonalCon1En11NoEsValida() {
        // Sección 7: "Un 1 en la diagonal, en (1,1)".
        MatrizAdyacencia m = matriz(new int[][]{
                {0, 0},
                {0, 1}
        });

        ResultadoValidacion resultado = validador.validar(m);

        assertFalse(resultado.esValida());
        assertEquals(1, resultado.getErrores().size());
        assertTrue(resultado.getErrores().get(0).contains("diagonal"));
        assertTrue(resultado.getErrores().get(0).contains("(1, 1)"));
    }

    @Test
    void matriz2x2SinAristasEsValida() {
        MatrizAdyacencia m = matriz(new int[][]{
                {0, 0},
                {0, 0}
        });

        assertTrue(validador.validar(m).esValida());
    }

    @Test
    void matrizDeUnSoloVerticeSinLazoEsValida() {
        MatrizAdyacencia m = matriz(new int[][]{{0}});

        assertTrue(validador.validar(m).esValida());
    }

    @Test
    void reportaTodosLosErroresNoSoloElPrimero() {
        // 4×3: rompe las cuatro reglas a la vez (no cuadrada, valor 5, no
        // simétrica en dos pares, diagonal con 1). Debe reportar TODO.
        MatrizAdyacencia m = matriz(new int[][]{
                {0, 1, 0},
                {0, 1, 5},
                {0, 0, 0},
                {0, 0, 0}
        });

        ResultadoValidacion resultado = validador.validar(m);
        List<String> errores = resultado.getErrores();

        assertEquals(5, errores.size(), "Deben reportarse todos los errores: " + errores);
        assertTrue(errores.stream().anyMatch(e -> e.contains("no es cuadrada")));
        assertTrue(errores.stream().anyMatch(e -> e.contains("0 o 1")));
        assertTrue(errores.stream().anyMatch(e -> e.contains("no es simétrica")));
        assertTrue(errores.stream().anyMatch(e -> e.contains("diagonal")));
    }

    @Test
    void reportaTodasLasViolacionesDeSimetria() {
        // Cuatro pares asimétricos distintos ((0,1), (0,3), (1,2), (2,3)) en una 4×4.
        MatrizAdyacencia m = matriz(new int[][]{
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1},
                {1, 0, 0, 0}
        });

        ResultadoValidacion resultado = validador.validar(m);
        List<String> errores = resultado.getErrores();

        assertEquals(4, errores.size(), "Cada violación de simetría debe reportarse por separado: " + errores);
        assertTrue(errores.stream().allMatch(e -> e.contains("no es simétrica")));
    }
}