package com.example.grafosjava.ui;

import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartStylableNode;
import com.example.grafosjava.logic.ConversorSmartGraph;
import com.example.grafosjava.model.Arista;
import com.example.grafosjava.model.GrafoModelo;
import com.example.grafosjava.model.ResultadoCaminoCorto;
import com.example.grafosjava.model.Vertice;

import javafx.application.Platform;
import javafx.scene.layout.StackPane;

import java.util.List;

/**
 * Componente visual que envuelve el {@link SmartGraphPanel} y coordina la
 * visualizacion del grafo junto con el resaltado de rutas.
 *
 * <p>Utiliza {@link ConversorSmartGraph} como adaptador para convertir el
 * {@link GrafoModelo} en la estructura de SmartGraph. Es el unico componente
 * de la capa {@code ui} que interactua directamente con SmartGraph para
 * aplicar estilos de resaltado a vertices y aristas.</p>
 *
 * <p>Responsabilidad: integrante B (visualizacion).</p>
 */
public class PanelGrafo extends StackPane {

    /** Adaptador que convierte el modelo a SmartGraph y gestiona el panel. */
    private final ConversorSmartGraph conversor;

    /** Modelo actualmente visualizado (para poder limpiar resaltados). */
    private GrafoModelo modeloActual;

    /** Indica si {@code SmartGraphPanel.init()} ya se llamo. */
    private boolean inicializado = false;

    /** Contador de reintentos para {@code init()}. */
    private int intentosInit = 0;

    /** Numero maximo de reintentos para {@code init()}. */
    private static final int MAX_INTENTOS_INIT = 20;

        /** Panel de SmartGraph en escena (para detectar cambios). */
    private SmartGraphPanel<Vertice, Arista> panelActual;

    
    /**
     * Crea un panel grafico con un conversor interno.
     */
    public PanelGrafo() {
        this.conversor = new ConversorSmartGraph();
    }

    /**
     * Crea un panel grafico reutilizando un conversor existente.
     *
     * @param conversor adaptador SmartGraph a utilizar
     */
    public PanelGrafo(ConversorSmartGraph conversor) {
        this.conversor = conversor;
    }

    /**
     * Muestra el grafo en el panel visual.
     *
     * <p>Si es la primera vez o si el modelo cambio, crea el
     * {@link SmartGraphPanel} mediante el conversor y lo agrega al StackPane.
     * Carga los estilos CSS desde el classpath y programa la inicializacion
     * visual con {@link SmartGraphPanel#init()} cuando el panel este en una
     * escena.</p>
     *
     * @param modelo grafo a visualizar
     */
    public void mostrar(GrafoModelo modelo) {
        this.modeloActual = modelo;
        SmartGraphPanel<Vertice, Arista> panel = conversor.crearPanel(modelo);
        if (panel != panelActual) {
            panelActual = panel;
            inicializado = false;
            intentosInit = 0;
        }
        cargarEstilos(panel);
        getChildren().setAll(panel);
        solicitarInit(panel);
    }

    /**
     * Sincroniza el grafo visual con el modelo actualizado.
     *
     * <p>Los cambios de peso, adicion o eliminacion de aristas se reflejan
     * automaticamente mediante {@link ConversorSmartGraph#actualizar(GrafoModelo)}.</p>
     *
     * @param modelo grafo con el estado actual
     */
    public void actualizar(GrafoModelo modelo) {
        this.modeloActual = modelo;
        conversor.actualizar(modelo);
    }

    /**
     * @return el conversor interno (para acceso avanzado si fuera necesario)
     */
    
    /**
     * Resalta visualmente la ruta mas corta devuelta por Dijkstra.
     *
     * <p>Aplica las clases CSS {@code shortest-path-vertex} y
     * {@code shortest-path-edge} a los vertices y aristas que forman parte
     * del camino. Si previamente habia un resaltado, se limpia primero.</p>
     *
     * @param resultado resultado de Dijkstra con el camino a resaltar
     */
    public void resaltarCamino(ResultadoCaminoCorto resultado) {
        SmartGraphPanel<Vertice, Arista> panel = conversor.getPanel();
        if (panel == null || modeloActual == null) {
            return;
        }

        limpiarResaltado();

        if (!resultado.esAlcanzable() || resultado.getCamino().isEmpty()) {
            return;
        }

        List<Integer> camino = resultado.getCamino();

        // Resaltar vertices del camino
        for (Integer id : camino) {
            Vertice v = modeloActual.obtenerVertice(id);
            if (v != null) {
                SmartStylableNode nodo = panel.getStylableVertex(v);
                if (nodo != null) {
                    nodo.addStyleClass("shortest-path-vertex");
                }
            }
        }

        // Resaltar aristas entre vertices consecutivos del camino
        for (int i = 0; i < camino.size() - 1; i++) {
            int idA = camino.get(i);
            int idB = camino.get(i + 1);
            for (Arista a : modeloActual.obtenerAristas()) {
                if (a.conecta(idA, idB)) {
                    SmartStylableNode nodo = panel.getStylableEdge(a);
                    if (nodo != null) {
                        nodo.addStyleClass("shortest-path-edge");
                    }
                    break;
                }
            }
        }

        
            refrescarPanel(panel);
    }

    /**
     * Elimina el resaltado de todos los vertices y aristas del grafo.
     */
    public void limpiarResaltado() {
        SmartGraphPanel<Vertice, Arista> panel = conversor.getPanel();
        if (panel == null || modeloActual == null) {
            return;
        }

        for (Vertice v : modeloActual.obtenerVertices()) {
            SmartStylableNode nodo = panel.getStylableVertex(v);
            if (nodo != null) {
                nodo.removeStyleClass("shortest-path-vertex");
            }
        }
        for (Arista a : modeloActual.obtenerAristas()) {
            SmartStylableNode nodo = panel.getStylableEdge(a);
            if (nodo != null) {
                nodo.removeStyleClass("shortest-path-edge");
            }
        }

        refrescarPanel(panel);
    }

    /**
     * Carga la hoja de estilos CSS desde el classpath para asegurar que
     * las clases personalizadas (incluyendo {@code shortest-path-*})
     * esten aplicadas a los nodos del panel.
     */
    private void cargarEstilos(SmartGraphPanel<Vertice, Arista> panel) {
        java.net.URL cssUrl = getClass().getResource("/smartgraph.css");
        if (cssUrl != null) {
            String css = cssUrl.toExternalForm();
            if (!panel.getStylesheets().contains(css)) {
                panel.getStylesheets().add(css);
            }
        }
    }

    /**
     * Programa {@link SmartGraphPanel#init()} para cuando el panel este
     * en una escena visible con un tamano mayor que cero.
     */
    private void solicitarInit(SmartGraphPanel<Vertice, Arista> panel) {
        if (inicializado || panel == null) {
            return;
        }

        if (panel.getScene() == null) {
            panel.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    Platform.runLater(this::intentarInit);
                }
            });
            return;
        }

        Platform.runLater(this::intentarInit);
    }

    /**
     * Intenta inicializar el panel visual, reintentando si aun no esta listo.
     */
    private void intentarInit() {
        if (inicializado) {
            return;
        }
        SmartGraphPanel<Vertice, Arista> panel = conversor.getPanel();
        if (panel == null) {
            return;
        }
        try {
            panel.init();
            inicializado = true;
        } catch (IllegalStateException e) {
            if (intentosInit < MAX_INTENTOS_INIT) {
                intentosInit++;
                Platform.runLater(this::intentarInit);
            }
        }
    }

    /**
     * Refresca el panel visual con manejo defensivo de la no-inicializacion.
     */
    private void refrescarPanel(SmartGraphPanel<Vertice, Arista> panel) {
        try {
            panel.update();
        } catch (IllegalStateException e) {
            // El panel aun no se ha inicializado; los estilos se aplicaran
            // cuando se llame a init() y luego update().
        }
    }
}




