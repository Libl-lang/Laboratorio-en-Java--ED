package com.example.grafosjava.logic;

import com.example.grafosjava.model.MatrizAdyacencia;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para {@link ValidadorMatriz}.
 *
 * <p>Se basan en los casos de la sección 11 del documento, adaptados al
 * contrato actual: los valores pueden ser enteros positivos (pesos), no
 * solo 0 o 1.</p>
 */
class ValidadorMatrizTest {

    private final ValidadorMatriz validador = new ValidadorMatriz();

    private MatrizAdyacencia matriz(int[][] valores) {
        int n = valores.length;
        MatrizAdyacencia m = new MatrizAdyacencia(n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                m.setValor(i, j, valores[i][j]);
            }
        }
        return m;
    }

    @Test
    void matrizSimetricaValidaConPesosEsValida() {
        // Sección 11: "Matriz simétrica válida 3×3 con peso 4 entre V0–V1
        // y peso 2 entre V0–V2".
        MatrizAdyacencia m = matriz(new int[][]{
                {0, 4, 2},
                {4, 0, 0},
                {2, 0, 0}
        });

        ResultadoValidacion resultado = validador.validar(m);

        assertTrue(resultado.esValida());
        assertTrue(resultado.getErrores().isEmpty());
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
    void valorNegativoEsPreventidoPorSetValorNoPorValidador() {
        // setValor ya rechaza valores negativos (probado en MatrizAdyacenciaTest),
        // por lo que no se puede construir una matriz con valores negativos
        // a traves de la API publica. El check de negativos en ValidadorMatriz
        // es defensa en profundidad.
        MatrizAdyacencia m = new MatrizAdyacencia(2);
        assertThrows(IllegalArgumentException.class, () -> m.setValor(0, 1, -1));
    }

    @Test
    void matrizAsimetricaSeDetecta() {
        MatrizAdyacencia m = matriz(new int[][]{
                {0, 4, 0},
                {2, 0, 0},
                {0, 0, 0}
        });

        ResultadoValidacion resultado = validador.validar(m);

        assertFalse(resultado.esValida());
        assertTrue(resultado.getErrores().stream().anyMatch(e -> e.contains("simétric")));
    }

    @Test
    void diagonalConValorDistintoDeCeroSeReporta() {
        MatrizAdyacencia m = matriz(new int[][]{
                {0, 0, 0},
                {0, 5, 0},
                {0, 0, 0}
        });

        ResultadoValidacion resultado = validador.validar(m);

        assertFalse(resultado.esValida());
        assertTrue(resultado.getErrores().get(0).contains("diagonal"));
        assertTrue(resultado.getErrores().get(0).contains("(1, 1)"));
    }

        @Test
    void reportaTodosLosErroresNoSoloElPrimero() {
        // 4×4 con: diagonal != 0 (3 en (0,0), 5 en (3,3)) y asimetría
        // ((0,1)=1 vs (1,0)=0). No hay valores negativos (setValor los
        // rechaza) pero el validador también controla la diagonal y la
        // simetría de forma independiente.
        MatrizAdyacencia m = matriz(new int[][]{
                {3, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 1, 0, 1},
                {0, 0, 1, 5}
        });

        ResultadoValidacion resultado = validador.validar(m);

        assertFalse(resultado.esValida());
        List<String> errores = resultado.getErrores();
        assertTrue(errores.stream().anyMatch(e -> e.contains("diagonal")));
        assertTrue(errores.stream().anyMatch(e -> e.contains("simétric")));
    }

    @Test
    void reportaTodasLasViolacionesDeSimetriaPorSeparado() {
        // 4×4 con 4 pares asimétricos distintos.
        MatrizAdyacencia m = matriz(new int[][]{
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1},
                {1, 0, 0, 0}
        });

        ResultadoValidacion resultado = validador.validar(m);

        List<String> errores = resultado.getErrores();
        assertTrue(errores.size() >= 4,
                "Deben reportarse todos los pares asimétricos: " + errores);
        assertTrue(errores.stream().allMatch(e -> e.contains("no es simétrica")));
    }

    @Test
    void matrizConPesosAltosSimetricaEsValida() {
        MatrizAdyacencia m = matriz(new int[][]{
                {0, 100, 50},
                {100, 0, 75},
                {50, 75, 0}
        });

        assertTrue(validador.validar(m).esValida());
    }
}
