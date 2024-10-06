package org.example;

import org.example.DataGenerator.NodeData;
import org.example.DataGenerator.EdgeData;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.Cardinality;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.schema.JanusGraphManagement;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

public class JanusClient {
    private JanusGraph graph;
    private GraphTraversalSource g;
    private final DataGenerator dataGenerator;
    private final String storageBackend;

    public JanusClient(storageBackendType storageBacked, DataGenerator dataGenerator) {
        this.storageBackend = storageBacked.value;
        this.dataGenerator = dataGenerator;
    }

    public enum storageBackendType {
        inmemory("inmemory"),
        berkeley("berkeleyje"),
        foundationdb("foundationdb");

        private final String value;

        storageBackendType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public void openGraph() {
        if (this.storageBackend == null) {
            System.out.println("Storage backend not set");
            return;
        } else if (this.storageBackend.equals(storageBackendType.inmemory.getValue())) {
            graph = JanusGraphFactory.build()
                    .set("storage.backend", "inmemory")
                    .open();
        } else if (this.storageBackend.equals(storageBackendType.berkeley.getValue())) {
            graph = JanusGraphFactory.build()
                    .set("storage.backend", "berkeleyje")
                    .set("storage.directory", "./data/berkeleyje")
                    .open();
        }
        // Add other storage backends here
        else if (this.storageBackend.equals(storageBackendType.foundationdb.getValue())) {
            graph = JanusGraphFactory.build()
                    .set("storage.backend", "org.janusgraph.diskstorage.foundationdb.FoundationDBStoreManager")
                    .set("cache.db-cache", "false")
                    .set("cache.db-cache-clean-wait", "20")
                    .set("cache.db-cache-time", "180000")
                    .set("cache.db-cache-size", "0.5")
                    .set("query.batch", "true")
                    .set("storage.fdb.isolation-level", "read_committed_with_write")
                    .open();
        }
        else {
            System.out.println("Storage backend not supported");
            return;
        }
        System.out.println("Storage backend: " + this.storageBackend+"\n");
        g = graph.traversal();
    }
    public void dropGraph() {
        try {
            JanusGraphFactory.drop(graph);
        } catch (Exception e) {
            System.out.println("Error dropping graph: " + e.getMessage());
        }
    }
    public void closeGraph() throws Exception {
        try {
            if (g != null)
                g.close();
            if (graph != null)
                graph.close();
        } finally {
            g = null;
            graph = null;
        }
    }

    public void createSchema() {
        JanusGraphManagement mgmt = graph.openManagement();
        try {
            createProperties(mgmt);
            createVertexLabels(mgmt);
            createEdgeLabels(mgmt);
            createIndices(mgmt);
            mgmt.commit();
        } catch (Exception e) {
            mgmt.rollback();
        }
    }

    public void createVertexLabels(JanusGraphManagement mgmt) {
        mgmt.makeVertexLabel("airport").make();
        mgmt.makeVertexLabel("country").make();
        mgmt.makeVertexLabel("continent").make();
    }

    public void createEdgeLabels(JanusGraphManagement mgmt) {
        mgmt.makeEdgeLabel("route").directed().multiplicity(Multiplicity.MULTI).make();
        mgmt.makeEdgeLabel("contains").directed().multiplicity(Multiplicity.MULTI).make();
    }

    public void createProperties(JanusGraphManagement mgmt) {
        mgmt.makePropertyKey("city").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("lat").dataType(Double.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("lon").dataType(Double.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("dist").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("identity").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("type").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("code").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("icao").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("desc").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("region").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("runways").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("longest").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("elev").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("country").dataType(String.class).cardinality(Cardinality.SINGLE).make();
    }

    public void createIndices(JanusGraphManagement mgmt) {
        mgmt.buildIndex("Idx_comidx_Vertex_identity_unique", Vertex.class)
                .addKey(mgmt.getPropertyKey("identity"))
                .unique()
                .buildCompositeIndex();
        mgmt.buildIndex("Idx_comidx_Vertex_type_airport", Vertex.class)
                .addKey(mgmt.getPropertyKey("type"))
                .indexOnly(mgmt.getVertexLabel("airport"))
                .buildCompositeIndex();

        mgmt.buildIndex("Idx_comidx_Vertex_code", Vertex.class)
                .addKey(mgmt.getPropertyKey("code"))
                .buildCompositeIndex();

        mgmt.buildIndex("Idx_comidx_Vertex_icao", Vertex.class)
                .addKey(mgmt.getPropertyKey("icao"))
                .buildCompositeIndex();

        mgmt.buildIndex("Idx_comidx_Vertex_country", Vertex.class)
                .addKey(mgmt.getPropertyKey("country"))
                .buildCompositeIndex();

        mgmt.buildIndex("Idx_comidx_Vertex_city", Vertex.class)
                .addKey(mgmt.getPropertyKey("city"))
                .buildCompositeIndex();

        mgmt.buildIndex("Idx_comidx_Edge_identity", Edge.class)
                .addKey(mgmt.getPropertyKey("identity"))
                .buildCompositeIndex();

    }

    public void addNodes() {
        try {
            Iterator<NodeData> nodes = dataGenerator.generateNodes();
            g.tx().open();
            while (nodes.hasNext()) {
                NodeData node = nodes.next();
                Vertex vertex = graph.addVertex(node.getLabel());
                for (Map.Entry<String, Object> entry : node.getProperties().entrySet()) {
                    vertex.property(entry.getKey(), entry.getValue());
                }
            }
            g.tx().commit();
        } catch (Exception e) {
            g.tx().rollback();
        }
    }

    public void addEdges() {
        try {
            Iterator<EdgeData> edges = dataGenerator.generateEdges();
            g.tx().open();
            while (edges.hasNext()) {
                EdgeData edgeData = edges.next();
                Vertex outVertex = g.V().has("identity", edgeData.getOutVertex()).next();
                Vertex inVertex = g.V().has("identity", edgeData.getInVertex()).next();
                Edge e = outVertex.addEdge(edgeData.getLabel(), inVertex);
                for (Map.Entry<String, Object> entry : edgeData.getProperties().entrySet()) {
                    e.property(entry.getKey(), entry.getValue());
                }
            }
            g.tx().commit();
        } catch (Exception e) {
            g.tx().rollback();
        }
    }

    public void createElements() {
        try {
            // Log time to create elements
            long startTime = System.currentTimeMillis();
            addNodes();
            addEdges();
            long endTime = System.currentTimeMillis();
            System.out.println("Time to load: " + (endTime - startTime) + "ms");
        } catch (Exception e) {
            g.tx().rollback();
            System.out.println("Error creating elements");
        }
    }

    public void readElements() {
        try {
            // Get the length of the vertices and edges
            long vertexCount = g.V().count().next();
            long edgeCount = g.E().count().next();
            System.out.println("Number of vertices: " + vertexCount);
            System.out.println("Number of edges: " + edgeCount);
        } catch (Exception e) {
            g.tx().rollback();
        }
    }
    public void printStorageSize() {
        if (this.storageBackend.equals(storageBackendType.berkeley.getValue())) {
            File storageDir = new File("./data/berkeleyje");
            long size = getFolderSize(storageDir);
            System.out.println("Storage size: " + formatSize(size));
        } else {
            // System.out.println("Storage size calculation not supported for backend: " + this.storageBackend);
        }
    }

    private long getFolderSize(File folder) {
        long length = 0;
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    length += file.length();
                } else {
                    length += getFolderSize(file);
                }
            }
        }
        return length;
    }

    private String formatSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return size / 1024 + " KB";
        } else if (size < 1024 * 1024 * 1024) {
            return size / (1024 * 1024) + " MB";
        } else {
            return size / (1024 * 1024 * 1024) + " GB";
        }
    }
    public void runApp() {
        try {
            openGraph();
            createSchema();
            createElements();
            readElements();
            printStorageSize();
            dropGraph();
            closeGraph();
            System.out.println("-------------------------");
        } catch (Exception e) {
            System.out.println("Error running graph" + e.getMessage());
        }
    }
}

