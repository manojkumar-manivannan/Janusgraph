package org.example;

import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import com.opencsv.CSVReader;

public class DataGenerator {
    public static class NodeData {
        private final String label;
        Map<String, Object> properties;

        private NodeData(String label, Map<String, Object> properties) {
            this.label = label;
            this.properties = properties;
        }

        public String getLabel() {
            return label;
        }

        public Map<String, Object> getProperties() {
            return properties;
        }
    }

    public static class EdgeData {
        private final String label;
        private final String from;
        private final String to;
        Map<String, Object> properties;

        private EdgeData(String label, String from, String to, Map<String, Object> properties) {
            this.label = label;
            this.from = from;
            this.to = to;
            this.properties = properties;
        }

        public String getLabel() {
            return label;
        }

        public String getOutVertex() {
            return from;
        }

        public String getInVertex() {
            return to;
        }

        public Map<String, Object> getProperties() {
            return properties;
        }
    }

    private final String nodePath;
    private final String edgePath;

    DataGenerator(String nodePath, String edgePath) {
        this.nodePath = nodePath;
        this.edgePath = edgePath;
    }

    public NodeData createNode(String[] fields) {
        String label = fields[2];
        Map<String, Object> properties = new HashMap<>();
        properties.put("identity", fields[0]);
        properties.put("type", fields[2]);
        properties.put("code", fields[3]);
        properties.put("desc", fields[5]);
        if (Objects.equals(fields[1], "airport")) {
            properties.put("icao", fields[4]);
            properties.put("region", fields[6]);
            properties.put("runways", Integer.parseInt(fields[7]));
            properties.put("longest", Integer.parseInt(fields[8]));
            properties.put("elev", Integer.parseInt(fields[9]));
            properties.put("country", fields[10]);
            properties.put("city", fields[11]);
            properties.put("lat", Double.parseDouble(fields[12]));
            properties.put("lon", Double.parseDouble(fields[13]));
        }
        return new NodeData(label, properties);
    }

    public EdgeData createEdge(String[] fields) {
        String label = fields[3];
        String from = fields[1];
        String to = fields[2];
        Map<String, Object> properties = new HashMap<>();
        properties.put("identity", fields[0]);
        if (Objects.equals(label, "route")) {
            properties.put("dist", Integer.parseInt(fields[4]));
        }
        return new EdgeData(label, from, to, properties);
    }

    public Iterator<NodeData> generateNodes() {
        return new Iterator<>() {
            private CSVReader reader;
            private String[] fields;

            {
                // Initialize the CSV reader and skip the first line (header)
                try {
                    reader = new CSVReader(new InputStreamReader(Files.newInputStream(Paths.get(nodePath))));
                    reader.readNext(); // Skip header
                    reader.readNext(); // Skip Irrelevant Node
                } catch (Exception e) {
                    e.printStackTrace();
                    closeReader();
                }
            }

            @Override
            public boolean hasNext() {
                try {
                    fields = reader.readNext();
                    if (fields == null) {
                        closeReader();
                        return false;
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    closeReader();
                    return false;
                }
            }

            @Override
            public NodeData next() {
                if (fields == null) {
                    throw new IllegalStateException("No more elements available.");
                }
                return createNode(fields);
            }

            // Close the CSV reader when done
            private void closeReader() {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public Iterator<EdgeData> generateEdges() {
        return new Iterator<>() {
            private CSVReader reader;
            private String[] fields;

            {
                // Initialize the CSV reader and skip the first line (header)
                try {
                    reader = new CSVReader(new InputStreamReader(Files.newInputStream(Paths.get(edgePath))));
                    reader.readNext(); // Skip header
                } catch (Exception e) {
                    e.printStackTrace();
                    closeReader();
                }
            }

            @Override
            public boolean hasNext() {
                try {
                    fields = reader.readNext();
                    if (fields == null) {
                        closeReader();
                        return false;
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    closeReader();
                    return false;
                }
            }

            @Override
            public EdgeData next() {
                if (fields == null) {
                    throw new IllegalStateException("No more elements available.");
                }
                return createEdge(fields);
            }

            // Close the CSV reader when done
            private void closeReader() {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}

