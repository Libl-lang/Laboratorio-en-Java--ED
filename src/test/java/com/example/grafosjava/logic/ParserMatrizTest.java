package com.example.grafosjava.logic;

import com.example.grafosjava.model.MatrizAdyacencia;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del Integrante A para {@link ParserMatriz}.
 *
 * <p>Sección 7 del documento: "Texto con letras o filas de distinto largo"
 * debe producir un error de formato.</p>
 */
class ParserMatrizTest {

    private final ParserMatriz parser = new ParserMatriz();

    @Test
    void textoValidoSeConvierteEnMatriz() {
        MatrizAdyacencia matriz = parser.parsear("0 1 0\n1 0 1\n0 1 0");

        assertEquals(3, matriz.n());
        assertEquals(3, matriz.columnas());
        assertEquals(0, matriz.get(0, 0));
        assertEquals(1, matriz.get(0, 1));
        assertEquals(1, matriz.get(1, 0));
        assertEquals(0, matriz.get(1, 1));
    }

    @Test
    void aceptaEspaciosMultiplesTabulacionesYSaltoDeLineaFinal() {
        MatrizAdyacencia matriz = parser.parsear("  0\t1  \n 1  0 \n");

        assertEquals(2, matriz.n());
        assertEquals(1, matriz.get(0, 1));
        assertEquals(1, matriz.get(1, 0));
    }

    @Test
    void valorMayorQueUnoSeParseaPorqueLaRegla0O1EsDelValidador() {
        // Sección 7: "Matriz con un valor 2 en una celda" la comprueba ValidadorMatriz,
        // no el parser: el 2 es numérico y debe llegar a la validación.
        MatrizAdyacencia matriz = parser.parsear("2 0\n0 1");

        assertEquals(2, matriz.get(0, 0));
    }

    @Test
    void caracteresNoNumericosLanzanErrorDeFormato() {
        FormatoMatrizInvalidoException excepcion = assertThrows(FormatoMatrizInvalidoException.class,
                () -> parser.parsear("0 a 0\n0 0 0\n0 0 0"));

        assertTrue(excepcion.getMessage().contains("no es un número"));
        assertTrue(excepcion.getMessage().contains("(0, 1)"));
    }

    @Test
    void filaVaciaLanzaErrorDeFormato() {
        FormatoMatrizInvalidoException excepcion = assertThrows(FormatoMatrizInvalidoException.class,
                () -> parser.parsear("0 1\n\n1 0"));

        assertTrue(excepcion.getMessage().contains("está vacía"));
    }

    @Test
    void filasConDistintaCantidadDeValoresLanzanErrorDeFormato() {
        FormatoMatrizInvalidoException excepcion = assertThrows(FormatoMatrizInvalidoException.class,
                () -> parser.parsear("0 1 0\n1 0"));

        assertTrue(excepcion.getMessage().contains("distinta cantidad de valores"));
    }

    @Test
    void textoVacioSoloEspaciosONullLanzanErrorDeFormato() {
        assertThrows(FormatoMatrizInvalidoException.class, () -> parser.parsear(""));
        assertThrows(FormatoMatrizInvalidoException.class, () -> parser.parsear("   \n "));
        assertThrows(FormatoMatrizInvalidoException.class, () -> parser.parsear(null));
    }
}