package org.example;
public class Main {
    public static void main(String[] args) {
        DataGenerator dataGenerator = new DataGenerator(
                "src/main/resources/nodes.csv",
                "src/main/resources/edges.csv"
        );

        // Storage Type: In-memory
        JanusClient app = new JanusClient(JanusClient.storageBackendType.inmemory, dataGenerator);
        app.runApp();

        // Storage Type: Berkeley
        JanusClient app2 = new JanusClient(JanusClient.storageBackendType.berkeley, dataGenerator);
        app2.runApp();
    }
}
