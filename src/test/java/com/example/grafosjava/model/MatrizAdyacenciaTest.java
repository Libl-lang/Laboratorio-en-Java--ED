package com.example.grafosjava.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para {@link MatrizAdyacencia}.
 *
 * <p>Cubren el contrato de la sección 5.4 del documento de acuerdos:
 * constructor, getValor, setValor, getMatriz y validaciones.</p>
 */
class MatrizAdyacenciaTest {

    @Test
    void constructorCreaMatrizDeCeros() {
        MatrizAdyacencia m = new MatrizAdyacencia(3);

        assertEquals(3, m.getCantidadVertices());
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(0, m.getValor(i, j),
                        "La celda (" + i + ", " + j + ") debería ser 0 por defecto.");
            }
        }
    }

    @Test
    void constructorRechazaCantidadNoPositiva() {
        assertThrows(IllegalArgumentException.class, () -> new MatrizAdyacencia(0));
        assertThrows(IllegalArgumentException.class, () -> new MatrizAdyacencia(-1));
    }

    @Test
    void setValorYGetValorFuncionanCorrectamente() {
        MatrizAdyacencia m = new MatrizAdyacencia(3);

        m.setValor(0, 1, 4);
        m.setValor(0, 2, 2);

        assertEquals(4, m.getValor(0, 1));
        assertEquals(2, m.getValor(0, 2));
        assertEquals(0, m.getValor(0, 0), "La celda no modificada debe ser 0.");
    }

    @Test
    void setValorPermiteEnterosPositivosComoPeso() {
        MatrizAdyacencia m = new MatrizAdyacencia(2);

        m.setValor(0, 1, 1);
        m.setValor(0, 1, 99);
        m.setValor(1, 0, 50);

        assertEquals(99, m.getValor(0, 1));
        assertEquals(50, m.getValor(1, 0));
    }

    @Test
    void setValorRechazaNegativo() {
        MatrizAdyacencia m = new MatrizAdyacencia(2);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> m.setValor(0, 1, -1));
        assertTrue(ex.getMessage().contains("negativos"));
    }

    @Test
    void getValorYSetValorRechazanIndicesFueraDeRango() {
        MatrizAdyacencia m = new MatrizAdyacencia(3);

        assertThrows(IllegalArgumentException.class, () -> m.getValor(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> m.getValor(0, 3));
        assertThrows(IllegalArgumentException.class, () -> m.setValor(3, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> m.setValor(0, -1, 1));
    }

    @Test
    void getMatrizDevuelveCopiaProfunda() {
        MatrizAdyacencia m = new MatrizAdyacencia(3);
        m.setValor(0, 1, 5);
        m.setValor(1, 0, 5);

        int[][] copia = m.getMatriz();

        // Modificar la copia no debe afectar la matriz interna.
        copia[0][1] = 999;
        assertEquals(5, m.getValor(0, 1), "La matriz interna no debe modificarse.");

        // La copia tiene las dimensiones correctas.
        assertEquals(3, copia.length);
        assertEquals(3, copia[0].length);
    }

    @Test
        void getMatrizDevuelveCopiaIndependienteParaCadaLlamada() {
        MatrizAdyacencia m = new MatrizAdyacencia(2);
        m.setValor(0, 1, 7);

        int[][] copia1 = m.getMatriz();
        int[][] copia2 = m.getMatriz();

        assertNotSame(copia1, copia2, "Cada llamada debe devolver una nueva instancia.");
    }

    @Test
    void matriz1x1EsValida() {
        MatrizAdyacencia m = new MatrizAdyacencia(1);
        assertEquals(1, m.getCantidadVertices());
        assertEquals(0, m.getValor(0, 0));
    }
}
