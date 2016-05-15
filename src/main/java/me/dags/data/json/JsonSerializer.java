package me.dags.data.json;

import me.dags.data.node.Node;
import me.dags.data.node.NodeSerializer;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public final class JsonSerializer implements NodeSerializer {

    private static final JsonSerializer PRETTY = new JsonSerializer(true);
    private static final JsonSerializer COMPACT = new JsonSerializer(false);

    private final boolean pretty;

    private JsonSerializer(boolean pretty) {
        this.pretty = pretty;
    }

    @Override
    public boolean prettyPrint() {
        return pretty;
    }

    @Override
    public Node deserialize(InputStream inputStream) {
        try (JsonReader reader = new JsonReader(inputStream)) {
            return reader.readNode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Node.NULL;
    }

    @Override
    public Node deserialize(Path path) {
        if (Files.exists(path)) {
            try (JsonReader reader = new JsonReader(Files.newInputStream(path))) {
                return reader.readNode();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Node.NULL;
    }

    @Override
    public Node deserialize(File file) {
        if (file.exists()) {
            try (JsonReader reader = new JsonReader(new FileInputStream(file))) {
                return reader.readNode();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Node.NULL;
    }

    @Override
    public Node deserialize(URL url) {
        try (JsonReader reader = new JsonReader(url.openConnection().getInputStream())) {
            return reader.readNode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Node.NULL;
    }

    @Override
    public Node deserialize(String in) {
        try (JsonReader reader = new JsonReader(new ByteArrayInputStream(in.getBytes("UTF-8")))) {
            return reader.readNode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Node.NULL;
    }

    @Override
    public String serialize(Node node, boolean compact) {
        try (JsonWriter writer = new JsonWriter(new StringWriter(), compact)) {
            return writer.write(node);
        } catch (IOException e) {
            return "{}";
        }
    }

    @Override
    public void serialize(Node node, File out, boolean compact) {
        try {
            out.getParentFile().mkdirs();
            out.createNewFile();
            try (JsonWriter writer = new JsonWriter(new FileWriter(out), compact)) {
                writer.write(node);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serialize(Node node, Path out, boolean compact) {
        try {
            if (!Files.exists(out)) {
                Files.createDirectories(out.getParent());
                Files.createFile(out);
            }
            try (JsonWriter writer = new JsonWriter(Files.newBufferedWriter(out, Charset.forName("UTF-8")), compact)) {
                writer.write(node);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JsonSerializer pretty() {
        return PRETTY;
    }

    public static JsonSerializer compact() {
        return COMPACT;
    }
}
