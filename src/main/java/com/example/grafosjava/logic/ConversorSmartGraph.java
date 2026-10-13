package com.example.grafosjava.logic;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graph.InvalidEdgeException;
import com.brunomnsilva.smartgraph.graph.InvalidVertexException;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.example.grafosjava.model.Arista;
import com.example.grafosjava.model.GrafoModelo;
import com.example.grafosjava.model.Vertice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Convierte un {@link GrafoModelo} en la estructura de grafo que requiere
 * SmartGraph y crea/actualiza el panel visual asociado.
 *
 * <p>Utiliza directamente {@link Vertice} como elemento de vértice (<code>V</code>)
 * y {@link Arista} como elemento de arista (<code>E</code>), sin clases auxiliares.
 * La identidad de cada vértice se mantiene mediante un mapa por ID
 * ({@code Map<Integer, Vertex<Vertice>>}) que permite localizar los vértices de
 * SmartGraph a partir de los IDs del modelo.</p>
 *
 * <p>Es el puente entre el modelo (fuente común de datos) y la vista. No implementa
 * el resaltado de recorridos ni la integración en la interfaz: eso corresponde a
 * {@code PanelGrafo}. Tampoco llama a {@link SmartGraphPanel#init()}; la
 * inicialización visual queda a cargo de la interfaz, una vez que el panel esté
 * dentro de una escena visible.</p>
 *
 * <p>Responsabilidad: integrante B. A diferencia del modelo, esta clase sí depende
 * de SmartGraph y de JavaFX, pues su función es precisamente la visualización.</p>
 */
public class ConversorSmartGraph {

    /** Grafo interno de SmartGraph que refleja el estado del modelo. */
    private Graph<Vertice, Arista> grafo;

    /** Correspondencia por ID entre los vértices del modelo y los de SmartGraph. */
    private final Map<Integer, Vertex<Vertice>> verticesPorId;

    /** Panel visual creado a partir del grafo interno. */
    private SmartGraphPanel<Vertice, Arista> panel;

    /**
     * Crea un conversor vacío. El grafo interno y el panel se construyen al
     * invocar {@link #convertir(GrafoModelo)} o {@link #crearPanel(GrafoModelo)}.
     */
    public ConversorSmartGraph() {
        this.verticesPorId = new HashMap<>();
    }

    /**
     * Construye un {@link Graph} de SmartGraph a partir del modelo.
     *
     * <p>Inserta cada vértice una sola vez y cada arista una sola vez, usando la
     * sobrecarga de {@code insertEdge} que recibe objetos {@code Vertex<Vertice>}.
     * La correspondencia por ID se reconstruye desde cero, por lo que llamadas
     * sucesivas reinician la representación interna.</p>
     *
     * <p>Este método solo actualiza los DATOS (el grafo interno y la correspondencia
     * por ID); NO crea ni reemplaza el panel. SmartGraphPanel guarda una referencia
     * final al grafo, por lo que un panel creado antes queda ligado al grafo anterior.
     * Si la interfaz necesita reflejar el nuevo grafo, debe obtener el panel
     * actualizado con {@link #getPanel()} (tras {@link #crearPanel(GrafoModelo)}) y
     * sustituir el anterior en la escena.</p>
     *
     * @param modelo grafo modelo a convertir
     * @return el grafo de SmartGraph equivalente
     * @throws IllegalArgumentException si {@code modelo} es {@code null}
     */
    public Graph<Vertice, Arista> convertir(GrafoModelo modelo) {
        if (modelo == null) {
            throw new IllegalArgumentException("El modelo no puede ser null.");
        }

        // Se construye en variables locales para no dejar el conversor en un
        // estado inconsistente si ocurre un error a mitad del proceso.
        Graph<Vertice, Arista> nuevoGrafo = new GraphEdgeList<>();
        Map<Integer, Vertex<Vertice>> nuevoMapa = new HashMap<>();

        for (Vertice v : modelo.obtenerVertices()) {
            try {
                Vertex<Vertice> sv = nuevoGrafo.insertVertex(v);
                nuevoMapa.put(v.getId(), sv);
            } catch (InvalidVertexException e) {
                throw new IllegalStateException(
                        "No se pudo insertar el vértice con id " + v.getId() + ".", e);
            }
        }

        for (Arista a : modelo.obtenerAristas()) {
            Vertex<Vertice> origen = nuevoMapa.get(a.getOrigen().getId());
            Vertex<Vertice> destino = nuevoMapa.get(a.getDestino().getId());
            if (origen != null && destino != null) {
                try {
                    nuevoGrafo.insertEdge(origen, destino, a);
                } catch (InvalidVertexException | InvalidEdgeException e) {
                    throw new IllegalStateException(
                            "No se pudo insertar la arista " + a.getOrigen().getId()
                                    + "-" + a.getDestino().getId() + ".", e);
                }
            }
        }

        this.grafo = nuevoGrafo;
        this.verticesPorId.clear();
        this.verticesPorId.putAll(nuevoMapa);

        // Nota: este método solo actualiza los DATOS (grafo y correspondencia por ID).
        // No crea ni reemplaza el panel. SmartGraphPanel guarda una referencia final al
        // grafo, por lo que un panel creado antes queda ligado al grafo anterior. Si la
        // UI necesita reflejar el nuevo grafo, debe obtener el panel actualizado con
        // getPanel() (tras llamar a crearPanel()) y sustituir el anterior en la escena.
        return this.grafo;
    }

    /**
     * Convierte el modelo y garantiza un panel asociado al grafo actual.
     *
     * <p>Configura el proveedor de etiquetas de arista para mostrar
     * {@link Arista#getPeso()}. Crea el panel solo cuando es necesario: si aún no
     * existe o si el panel actual quedó ligado a un grafo anterior (por ejemplo,
     * tras una llamada previa a {@link #convertir(GrafoModelo)}). Para detectar esto
     * se compara {@link SmartGraphPanel#getModel()} con el grafo interno actual.</p>
     *
     * <p>NO llama a {@link SmartGraphPanel#init()}: la inicialización visual debe
     * hacerla la interfaz cuando el panel esté en una escena visible. Si este método
     * devuelve un panel NUEVO (distinto del que la interfaz ya mostraba), la interfaz
     * debe sustituir en la escena el panel anterior por el obtenido de
     * {@link #getPanel()} y volver a llamar a {@code init()} sobre el panel nuevo.</p>
     *
     * @param modelo grafo modelo a visualizar
     * @return el panel asociado al grafo actual (sin inicializar)
     * @throws IllegalArgumentException si {@code modelo} es {@code null}
     */
    public SmartGraphPanel<Vertice, Arista> crearPanel(GrafoModelo modelo) {
        convertir(modelo);
        // Creamos el panel si no existe o si el actual quedó ligado a un grafo anterior.
        if (this.panel == null || this.panel.getModel() != this.grafo) {
            this.panel = construirPanel();
        }
        return this.panel;
    }

    /**
     * Sincroniza el grafo interno con el modelo recibido y refresca la vista.
     *
     * <p>Actualiza la estructura (vértices y aristas añadidos o eliminados), pero
     * NO elimina ni reinserta aristas cuyo peso cambió: el proveedor de etiquetas y
     * {@code panel.update()} ya refrescan sus etiquetas existentes.</p>
     *
     * <p>Si se invoca antes de {@link #crearPanel(GrafoModelo)}, inicializa la
     * representación interna mediante {@link #convertir(GrafoModelo)}; en ese caso
     * no hay panel que refrescar, de modo que la vista no se actualiza hasta que se
     * cree un panel.</p>
     *
     * @param modelo grafo modelo con el estado actual
     * @throws IllegalArgumentException si {@code modelo} es {@code null}
     */
    public void actualizar(GrafoModelo modelo) {
        if (modelo == null) {
            throw new IllegalArgumentException("El modelo no puede ser null.");
        }

        if (grafo == null) {
            // actualizar() llamado antes de crear el panel: solo inicializamos la
            // representación interna (no hay vista que refrescar todavía).
            convertir(modelo);
        } else {
            sincronizar(modelo);
        }

        refrescarVista();
    }

    /**
     * @return el panel creado, o {@code null} si aún no se ha creado
     */
    public SmartGraphPanel<Vertice, Arista> getPanel() {
        return panel;
    }

    /**
     * Crea el panel visual a partir del grafo interno actual, con colocación circular y
     * el proveedor de etiquetas que muestra {@link Arista#getPeso()}.
     *
     * @return un panel nuevo (sin inicializar)
     */
    private SmartGraphPanel<Vertice, Arista> construirPanel() {
        SmartGraphPanel<Vertice, Arista> nuevo =
                new SmartGraphPanel<>(grafo, new SmartCircularSortedPlacementStrategy());
        // El peso se muestra como etiqueta; panel.update() refresca su texto sola.
        nuevo.setEdgeLabelProvider(a -> String.valueOf(a.getPeso()));
        return nuevo;
    }

    /**
     * Refresca la vista solo cuando el panel ya puede hacerlo.
     *
     * <p>SmartGraphPanel.update() exige que el panel esté en una escena y que se haya
     * llamado a init(). Como el estado de inicialización es privado en la biblioteca, se
     * comprueba getScene() y, ante la falta de inicialización, se captura
     * IllegalStateException de forma defensiva. En todos los casos el grafo interno ya
     * quedó sincronizado antes de invocar este método.</p>
     *
     * <p>Limitación (verificada en el fuente de SmartGraph 2.3.0): init() NO crea nodos
     * de elementos nuevos; solo update() lo hace mediante insertNodes(). Por ello, los
     * cambios estructurales realizados antes de init() permanecen en el grafo pero se
     * dibujan recién tras un update() posterior a init(). La interfaz debe llamar a
     * actualizar() después de init() para reflejarlos en la vista.</p>
     */
    private void refrescarVista() {
        if (panel == null || panel.getScene() == null) {
            // Sin panel o el panel todavía no está en una escena: los datos ya quedaron
            // sincronizados en el grafo; la vista se refrescará cuando se muestre e
            // inicialice el panel.
            return;
        }
        try {
            panel.update();
        } catch (IllegalStateException e) {
            // El panel está en escena pero aún no se llamó a init(). El grafo ya está
            // sincronizado; la interfaz debe invocar actualizar() tras init() para que
            // los cambios estructurales se dibujen (ver limitación documentada).
        }
    }

    /**
     * Ajusta el grafo interno de SmartGraph para que coincida con el modelo:
     * elimina vértices y aristas que ya no existen y agrega los nuevos, manteniendo
     * la correspondencia por ID. Las aristas existentes no se modifican (su peso se
     * refleja solo con {@code panel.update()}).
     */
    private void sincronizar(GrafoModelo modelo) {
        // --- Vértices: eliminar los que ya no están en el modelo ---
        // (SmartGraph elimina también las aristas incidentes al quitar un vértice.)
        Set<Integer> idsModelo = new HashSet<>();
        for (Vertice v : modelo.obtenerVertices()) {
            idsModelo.add(v.getId());
        }

        List<Vertex<Vertice>> verticesObsoletos = new ArrayList<>();
        for (Vertex<Vertice> sv : grafo.vertices()) {
            if (!idsModelo.contains(sv.element().getId())) {
                verticesObsoletos.add(sv);
            }
        }
        for (Vertex<Vertice> sv : verticesObsoletos) {
            grafo.removeVertex(sv);
            verticesPorId.remove(sv.element().getId());
        }

        // --- Vértices: agregar los nuevos y refrescar la correspondencia ---
        for (Vertice v : modelo.obtenerVertices()) {
            if (!verticesPorId.containsKey(v.getId())) {
                try {
                    Vertex<Vertice> sv = grafo.insertVertex(v);
                    verticesPorId.put(v.getId(), sv);
                } catch (InvalidVertexException e) {
                    throw new IllegalStateException(
                            "No se pudo insertar el vértice con id " + v.getId() + ".", e);
                }
            }
        }

        // --- Aristas: conexiones actuales del modelo (clave canónica) ---
        Set<String> conexionesModelo = new HashSet<>();
        for (Arista a : modelo.obtenerAristas()) {
            conexionesModelo.add(claveConexion(a.getOrigen().getId(), a.getDestino().getId()));
        }

        // --- Aristas: eliminar las cuya conexión ya no existe ---
        List<Edge<Arista, Vertice>> aristasObsoletas = new ArrayList<>();
        for (Edge<Arista, Vertice> e : grafo.edges()) {
            Arista a = e.element();
            if (!conexionesModelo.contains(claveConexion(a.getOrigen().getId(), a.getDestino().getId()))) {
                aristasObsoletas.add(e);
            }
        }
        for (Edge<Arista, Vertice> e : aristasObsoletas) {
            try {
                grafo.removeEdge(e);
            } catch (InvalidEdgeException ex) {
                throw new IllegalStateException("No se pudo eliminar una arista obsoleta.", ex);
            }
        }

        // --- Aristas: agregar las nuevas o reemplazar instancias distintas ---
        // Se indexan las aristas actuales de SmartGraph por su conexión para comparar
        // también la identidad de la instancia Arista (no solo sus extremos).
        Map<String, Edge<Arista, Vertice>> aristasGrafo = new HashMap<>();
        for (Edge<Arista, Vertice> e : grafo.edges()) {
            Arista a = e.element();
            aristasGrafo.put(claveConexion(a.getOrigen().getId(), a.getDestino().getId()), e);
        }
        for (Arista a : modelo.obtenerAristas()) {
            String clave = claveConexion(a.getOrigen().getId(), a.getDestino().getId());
            Edge<Arista, Vertice> existente = aristasGrafo.get(clave);
            if (existente != null && existente.element() == a) {
                // Misma instancia: si solo cambió el peso, panel.update() refresca la
                // etiqueta. No se elimina ni se reinserta.
                continue;
            }
            Vertex<Vertice> origen = verticesPorId.get(a.getOrigen().getId());
            Vertex<Vertice> destino = verticesPorId.get(a.getDestino().getId());
            if (origen == null || destino == null) {
                // Extremos inexistentes: no se puede (re)insertar la arista.
                continue;
            }
            if (existente != null) {
                // Misma conexión con otra instancia de Arista (p. ej. se eliminó y se
                // volvió a agregar en el modelo): reemplazamos la arista en SmartGraph.
                // No se usa replace() porque deja inconsistente el mapa interno.
                try {
                    grafo.removeEdge(existente);
                } catch (InvalidEdgeException ex) {
                    throw new IllegalStateException(
                            "No se pudo reemplazar la arista " + a.getOrigen().getId()
                                    + "-" + a.getDestino().getId() + ".", ex);
                }
            }
            try {
                grafo.insertEdge(origen, destino, a);
            } catch (InvalidVertexException | InvalidEdgeException ex) {
                throw new IllegalStateException(
                        "No se pudo insertar la arista " + a.getOrigen().getId()
                                + "-" + a.getDestino().getId() + ".", ex);
            }
        }
    }

    /**
     * Clave canónica e independiente del orden para una conexión no dirigida.
     *
     * @param idA identificador de un extremo
     * @param idB identificador del otro extremo
     * @return la clave {@code "menor-mayor"} de la conexión
     */
    private static String claveConexion(int idA, int idB) {
        int menor = Math.min(idA, idB);
        int mayor = Math.max(idA, idB);
        return menor + "-" + mayor;
    }
}
