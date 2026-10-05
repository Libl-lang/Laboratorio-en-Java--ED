package com.example.grafosjava.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * Panel de entrada de la matriz (Integrante A).
 *
 * <p>Ofrece un área de texto para escribir la matriz (una fila por línea,
 * valores 0 o 1 separados por espacios) y los botones "Generar grafo" y
 * "Limpiar". No valida por su cuenta: el botón "Generar grafo" envía el texto
 * al {@link Consumer} configurado. En la Etapa 6 (juntos) ese consumer se
 * conectará con {@code GrafoServicio}. Este panel no conoce SmartGraph.</p>
 */
public class PanelEntradaMatriz extends VBox {

    private final TextArea areaMatriz = new TextArea();
    private final Button botonGenerar = new Button("Generar grafo");
    private final Button botonLimpiar = new Button("Limpiar");
    private Consumer<String> alGenerarGrafo = texto -> {
    };

    public PanelEntradaMatriz() {
        setSpacing(10);
        setPadding(new Insets(12));

        Label etiqueta = new Label("Matriz de adyacencia");
        etiqueta.setStyle("-fx-font-weight: bold;");

        areaMatriz.setPromptText("Escriba la matriz: una fila por línea, valores 0 o 1 separados por espacios.\n"
                + "Ejemplo:\n0 1 0\n1 0 1\n0 1 0");
        areaMatriz.setPrefRowCount(6);
        VBox.setVgrow(areaMatriz, Priority.ALWAYS);

        HBox botones = new HBox(10, botonLimpiar, botonGenerar);
        botones.setAlignment(Pos.CENTER_RIGHT);

        botonGenerar.setOnAction(e -> alGenerarGrafo.accept(areaMatriz.getText()));
        botonLimpiar.setOnAction(e -> areaMatriz.clear());

        getChildren().addAll(etiqueta, areaMatriz, botones);
    }

    /**
     * Define qué hacer con el texto cuando el usuario pulsa "Generar grafo".
     *
     * @param callback acción que recibe el texto de la matriz; en la Etapa 6
     *                 se conectará con {@code GrafoServicio}
     */
    public void setAlGenerarGrafo(Consumer<String> callback) {
        this.alGenerarGrafo = callback == null ? texto -> {
        } : callback;
    }

    /**
     * Devuelve el texto escrito por el usuario.
     *
     * @return texto actual del área de entrada
     */
    public String getTextoMatriz() {
        return areaMatriz.getText();
    }

    /**
     * Limpia el área de texto.
     */
    public void limpiar() {
        areaMatriz.clear();
    }
}