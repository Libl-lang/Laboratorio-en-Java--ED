package com.example.grafosjava.ui;

import com.example.grafosjava.logic.GrafoServicio;
import com.example.grafosjava.logic.ResultadoValidacion;
import com.example.grafosjava.model.Arista;
import com.example.grafosjava.model.GrafoModelo;
import com.example.grafosjava.model.MatrizAdyacencia;
import com.example.grafosjava.model.ResultadoCaminoCorto;
import com.example.grafosjava.model.Vertice;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

/**
 * Controlador principal de la interfaz grafica.
 *
 * <p>Coordina la capa {@code ui} con la capa {@code logic}
 * ({@link GrafoServicio}) y con el componente visual
 * ({@link PanelGrafo}). Administra el estado del {@link GrafoModelo},
 * mantiene sincronizadas la vista del grafo, la lista de pesos y la
 * matriz de adyacencia, y orquesta el resaltado de rutas de Dijkstra.</p>
 *
 * <p>Responsabilidad: integrante B (interfaz).</p>
 */
public class ControladorPrincipal implements javafx.fxml.Initializable {

    @FXML private Spinner<Integer> spinnerVertices;
    @FXML private Button btnGenerate;
    @FXML private Button btnDijkstra;
    @FXML private GridPane gridMatrizEditor;
    @FXML private StackPane stackGrafo;
    @FXML private TableView<Arista> tablaPesos;
    @FXML private ComboBox<Integer> comboOrigen;
    @FXML private ComboBox<Integer> comboDestino;
    @FXML private Label lblResultado;
    @FXML private Label lblStatus;

    /** Servicio de logica para validar, convertir y ejecutar algoritmos. */
    private final GrafoServicio servicio = new GrafoServicio();

    /** Panel visual del grafo (envuelve ConversorSmartGraph). */
    private PanelGrafo panelGrafo;

    /** Modelo de datos actual. */
    private GrafoModelo modeloActual;

    /** Matriz de TextFields del editor, indexada por [fila][columna]. */
    private TextField[][] camposMatriz;

    /** Evita actualizaciones recursivas al sincronizar la matriz. */
    private boolean sincronizandoMatriz = false;

    /** Tamano actual de la matriz de edicion. */
    private int tamanoMatriz = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        spinnerVertices.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 5));
        btnGenerate.setDisable(true);
        btnDijkstra.setDisable(true);
        panelGrafo = new PanelGrafo();
        stackGrafo.getChildren().add(panelGrafo);
        inicializarTablaPesos();
    }

    /**
     * Inicializa las columnas de la tabla de aristas con proveedores de
     * valores y celda editable para el peso.
     */
    private void inicializarTablaPesos() {
        tablaPesos.setEditable(true);

        TableColumn<Arista, Integer> colOrigen = new TableColumn<>("Origen");
        colOrigen.setPrefWidth(80);
        colOrigen.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().getOrigen().getId()));
        colOrigen.setCellFactory(col -> new TableCell<Arista, Integer>() {
            @Override protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);
                setText(empty || id == null ? "" : String.valueOf(id));
            }
        });

        TableColumn<Arista, Integer> colDestino = new TableColumn<>("Destino");
        colDestino.setPrefWidth(80);
        colDestino.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().getDestino().getId()));
        colDestino.setCellFactory(col -> new TableCell<Arista, Integer>() {
            @Override protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);
                setText(empty || id == null ? "" : String.valueOf(id));
            }
        });

        TableColumn<Arista, Integer> colPeso = new TableColumn<>("Peso");
        colPeso.setPrefWidth(80);
        colPeso.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().getPeso()));
        colPeso.setCellFactory(TextFieldTableCell.forTableColumn(
                new IntegerStringConverter()));
        colPeso.setEditable(true);
        colPeso.setOnEditCommit(this::onEditarPeso);

                tablaPesos.getColumns().setAll(colOrigen, colDestino, colPeso);
    }

    /**
     * Crea una nueva matriz de TextFields segun el tamano indicado.
     * El diagonal se fija a 0 y se deshabilita. Las celdas simetricas
     * (i,j) y (j,i) se mantienen sincronizadas.
     */
    @FXML
    private void onCreateMatrix() {
        int n = spinnerVertices.getValue();
        if (n <= 0) {
            lblStatus.setText("El numero de vertices debe ser positivo.");
            return;
        }
        tamanoMatriz = n;
        gridMatrizEditor.getChildren().clear();
        camposMatriz = new TextField[n][n];

        // Cabecera de esquina
        Label corner = new Label("ID");
        corner.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        gridMatrizEditor.add(corner, 0, 0);

        // Cabeceras de fila y columna
        for (int i = 0; i < n; i++) {
            Label colHeader = new Label(String.valueOf(i));
            colHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
            gridMatrizEditor.add(colHeader, i + 1, 0);
            Label rowHeader = new Label(String.valueOf(i));
            rowHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
            gridMatrizEditor.add(rowHeader, 0, i + 1);
        }

        // Crear TextFields
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                TextField tf = new TextField();
                tf.setPrefColumnCount(3);
                tf.setAlignment(Pos.CENTER);

                StringConverter<Integer> converter = new IntegerStringConverter();
                UnaryOperator<TextFormatter.Change> filtro = change -> {
                    String texto = change.getControlNewText();
                    if (texto.isEmpty()) return change;
                    try {
                        int val = Integer.parseInt(texto);
                        return val >= 0 ? change : null;
                    } catch (NumberFormatException e) {
                        return null;
                    }
                };
                tf.setTextFormatter(new TextFormatter<>(converter, 0, filtro));

                final int fi = i, fj = j;

                // Auto-sincronizar celda simetrica
                tf.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (sincronizandoMatriz) return;
                    if (fi != fj && fi < n && fj < n) {
                        sincronizandoMatriz = true;
                        camposMatriz[fj][fi].setText(newVal);
                        sincronizandoMatriz = false;
                    }
                });

                tf.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) {
                        aplicarCambioMatriz(fi, fj, tf.getText());
                    }
                });

                if (i == j) {
                    tf.setText("0");
                    tf.setDisable(true);
                } else if (i > j) {
                    tf.setDisable(true);
                }

                camposMatriz[i][j] = tf;
                gridMatrizEditor.add(tf, j + 1, i + 1);
            }
        }

        // Limpiar modelo y vistas
        modeloActual = null;
        tablaPesos.getItems().clear();
        comboOrigen.getItems().clear();
        comboDestino.getItems().clear();
        btnGenerate.setDisable(false);
        btnDijkstra.setDisable(true);
        lblResultado.setText("");
        
        }

    /**
     * Lee los valores de la matriz de TextFields como {@link MatrizAdyacencia}.
     * Valores no numericos se tratan como 0.
     */
    private MatrizAdyacencia leerMatrizDesdeEditor() {
        if (camposMatriz == null || camposMatriz.length == 0) return null;
        int n = camposMatriz.length;
        MatrizAdyacencia m = new MatrizAdyacencia(n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                String texto = camposMatriz[i][j].getText().trim();
                int valor = 0;
                try {
                    valor = Integer.parseInt(texto);
                } catch (NumberFormatException e) {
                    valor = 0;
                }
                m.setValor(i, j, valor);
            }
        }
        return m;
    }

    /**
     * Valida la matriz ingresada y construye el modelo de grafo.
     * Muestra el grafo en la vista y habilita los controles de Dijkstra.
     */
    @FXML
    private void onGenerateGraph() {
        MatrizAdyacencia matriz = leerMatrizDesdeEditor();
        if (matriz == null) {
            lblStatus.setText("Cree una matriz primero.");
            return;
        }

        ResultadoValidacion validacion = servicio.validarMatriz(matriz);
        if (!validacion.esValida()) {
            lblStatus.setText("Matriz invalida: " + validacion.getErroresComoTexto());
            return;
        }

        GrafoModelo grafo = servicio.convertirMatrizAGrafo(matriz);
        modeloActual = grafo;

        panelGrafo.mostrar(grafo);
        panelGrafo.limpiarResaltado();
        actualizarTablaPesos();
        actualizarListaCombinada();

        btnDijkstra.setDisable(false);
        lblResultado.setText("");
        lblStatus.setText("Grafo generado: " + grafo.contarVertices()
                + " vertices, " + grafo.contarAristas() + " aristas.");
    }

    /**
     * Aplica el cambio de una celda de matriz al modelo. Actualiza
     * aristas de forma incremental: agregar, eliminar o modificar peso.
     */
    private void aplicarCambioMatriz(int fila, int col, String texto) {
        if (modeloActual == null) return;

        int valor;
        try {
            valor = Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            camposMatriz[fila][col].setText("0");
            valor = 0;
        }
        if (valor < 0) {
            camposMatriz[fila][col].setText("0");
            valor = 0;
        }
        if (fila == col) return;

        int pesoActual = 0;
        for (Arista a : modeloActual.obtenerAristas()) {
            if (a.conecta(fila, col)) {
                pesoActual = a.getPeso();
                break;
            }
        }

        if (pesoActual > 0) {
            if (valor == 0) {
                modeloActual.eliminarArista(fila, col);
            } else if (valor != pesoActual) {
                modeloActual.actualizarPesoArista(fila, col, valor);
            }
        } else if (valor > 0) {
            modeloActual.agregarArista(fila, col, valor);
        }

        panelGrafo.actualizar(modeloActual);
        actualizarTablaPesos();
        actualizarListaCombinada();
    }

    /**
     * Manejador de edicion de peso en la tabla de aristas.
     */
    private void onEditarPeso(TableColumn.CellEditEvent<Arista, Integer> event) {
        Arista arista = event.getRowValue();
        int nuevoPeso = event.getNewValue();

        if (nuevoPeso < 0) {
            lblStatus.setText("Error: el peso debe ser positivo o 0 (para eliminar).");
            actualizarTablaPesos();
            return;
        }

        int idO = arista.getOrigen().getId();
        int idD = arista.getDestino().getId();

        if (nuevoPeso == 0) {
            modeloActual.eliminarArista(idO, idD);
        } else {
            modeloActual.actualizarPesoArista(idO, idD, nuevoPeso);
        }

                panelGrafo.actualizar(modeloActual);
        actualizarTablaPesos();
        actualizarMatrizDesdeModelo();
        actualizarListaCombinada();
    }

    /**
     * Actualiza los elementos de la tabla de aristas con los datos del modelo.
     */
    private void actualizarTablaPesos() {
        if (modeloActual == null) {
            tablaPesos.getItems().clear();
            return;
        }
        List<Arista> aristas = modeloActual.obtenerAristas();
        ObservableList<Arista> items = FXCollections.observableArrayList(aristas);
        tablaPesos.setItems(items);
        tablaPesos.refresh();
    }

    /**
     * Actualiza las listas de vertices disponibles para origen y destino.
     */
    private void actualizarListaCombinada() {
        comboOrigen.getItems().clear();
        comboDestino.getItems().clear();
        if (modeloActual == null) return;
        ObservableList<Integer> ids = FXCollections.observableArrayList();
        for (Vertice v : modeloActual.obtenerVertices()) {
            ids.add(v.getId());
        }
        comboOrigen.setItems(ids);
        comboDestino.setItems(ids);
    }

    /**
     * Sincroniza los TextFields de la matriz con los pesos del modelo.
     */
    private void actualizarMatrizDesdeModelo() {
        if (camposMatriz == null || modeloActual == null) return;
        if (sincronizandoMatriz) return;
        sincronizandoMatriz = true;
        MatrizAdyacencia m = servicio.convertirGrafoAMatriz(modeloActual);
        for (int i = 0; i < camposMatriz.length; i++) {
            for (int j = 0; j < camposMatriz[i].length; j++) {
                if (i == j) continue;
                String valorTexto = String.valueOf(m.getValor(i, j));
                if (!camposMatriz[i][j].getText().equals(valorTexto)) {
                    camposMatriz[i][j].setText(valorTexto);
                }
            }
        }
        sincronizandoMatriz = false;
    }

    /**
     * Elimina el resaltado de la ruta mas corta.
     */
    @FXML
    private void onClearHighlight() {
        panelGrafo.limpiarResaltado();
        lblStatus.setText("Resaltado eliminado.");
    }

    /**
     * Ejecuta Dijkstra entre los vertices seleccionados y resalta el
     * camino resultante en la vista del grafo.
     */
    @FXML
    private void onCalcularDijkstra() {
        if (modeloActual == null) {
            lblStatus.setText("Genere un grafo primero.");
            return;
        }

        Integer origen = comboOrigen.getValue();
        Integer destino = comboDestino.getValue();
        if (origen == null || destino == null) {
            lblStatus.setText("Seleccione origen y destino.");
            return;
        }

        ResultadoCaminoCorto resultado = servicio.dijkstra(modeloActual, origen, destino);

        if (resultado.esAlcanzable()) {
            List<Integer> camino = resultado.getCamino();
            panelGrafo.resaltarCamino(resultado);
            lblResultado.setText("Camino: " + camino + "\nDistancia: " + resultado.getDistanciaTotal());
            lblStatus.setText("Camino mas corto resaltado.");
        } else {
            panelGrafo.limpiarResaltado();
            lblResultado.setText("No hay camino entre v" + origen + " y v" + destino + ".");
            lblStatus.setText("No se encontro camino.");
        }
    }
}



