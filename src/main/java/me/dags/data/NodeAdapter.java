package me.dags.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import me.dags.data.Provider.Reader;
import me.dags.data.Provider.Writer;
import me.dags.data.hocon.HoconReader;
import me.dags.data.hocon.HoconWriter;
import me.dags.data.json.JsonReader;
import me.dags.data.json.JsonWriter;
import me.dags.data.node.Node;
import me.dags.data.node.NodeReader;
import me.dags.data.node.NodeTypeAdapter;
import me.dags.data.node.NodeTypeAdapters;
import me.dags.data.node.NodeWriter;

public class NodeAdapter {

    private static final Reader JSON_READER = i -> new JsonReader(i);
    private static final Reader HOCON_READER = i -> new HoconReader(i);
    private static final Writer JSON_WRITER = i -> new JsonWriter(i, false);
    private static final Writer HOCON_WRITER = i -> new HoconWriter(i, false);
    private static final Writer JSON_WRITER_COMPACT = i -> new JsonWriter(i, true);
    private static final Writer HOCON_WRITER_COMPACT = i -> new HoconWriter(i, true);

    private final Provider.Reader readerProvider;
    private final Provider.Writer writerProvider;

    private NodeAdapter(Builder builder) {
        this.readerProvider = builder.reader;
        this.writerProvider = builder.writer;
    }

    public NodeAdapter(Provider.Reader reader, Provider.Writer writer) {
        this.readerProvider = reader;
        this.writerProvider = writer;
    }

    public Node from(InputStream inputStream) {
        try (NodeReader reader = readerProvider.provide(inputStream)) {
            return reader.readNode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Node.NULL;
    }

    public Node from(Path path) {
        if (Files.exists(path)) {
            try (NodeReader reader = readerProvider.provide(Files.newInputStream(path))) {
                return reader.readNode();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Node.NULL;
    }

    public Node from(File file) {
        if (file.exists()) {
            try (NodeReader reader = readerProvider.provide(new FileInputStream(file))) {
                return reader.readNode();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Node.NULL;
    }

    public Node from(URL url) {
        try (NodeReader reader = readerProvider.provide(url.openConnection().getInputStream())) {
            return reader.readNode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Node.NULL;
    }

    public Node from(String in) {
        try (NodeReader reader = readerProvider.provide(new ByteArrayInputStream(in.getBytes("UTF-8")))) {
            return reader.readNode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Node.NULL;
    }

    public <T> T from(Node node, Class<T> type) {
        NodeTypeAdapter<T> adapter = NodeTypeAdapters.of(type);
        if (adapter != null) {
            return adapter.fromNode(node);
        }
        return null;
    }

    public <T> T from(InputStream inputStream, Class<T> type) {
        return from(from(inputStream), type);
    }

    public <T> T from(Path path, Class<T> type) {
        return from(from(path), type);
    }

    public <T> T from(File file, Class<T> type) {
        return from(from(file), type);
    }

    public <T> T from(URL url, Class<T> type) {
        return from(from(url), type);
    }

    public <T> T from(String string, Class<T> type) {
        return from(from(string), type);
    }

    public String to(Node node) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (NodeWriter writer = writerProvider.provide(out)) {
            writer.write(node);
            writer.flush();
            return out.toString("UTF-8");
        } catch (IOException e) {
            return "{}";
        }
    }

    public void to(Node node, OutputStream out) {
        try (NodeWriter writer = writerProvider.provide(out)) {
            writer.write(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void to(Node node, File out) {
        try {
            out.getParentFile().mkdirs();
            out.createNewFile();
            try (NodeWriter writer = writerProvider.provide(new FileOutputStream(out))) {
                writer.write(node);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void to(Node node, Path out) {
        try {
            if (!Files.exists(out)) {
                Files.createDirectories(out.getParent());
                Files.createFile(out);
            }
            try (NodeWriter writer = writerProvider.provide(Files.newOutputStream(out))) {
                writer.write(node);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static NodeAdapter json() {
        return builder().readJson().writeJson().build();
    }

    public static NodeAdapter jsonCompact() {
        return builder().readJson().writeJsonCompact().build();
    }

    public static NodeAdapter hocon() {
        return builder().readHocon().writeHocon().build();
    }

    public static NodeAdapter hoconCompact() {
        return builder().readHocon().writeHoconCompact().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Provider.Reader reader = JSON_READER;
        private Provider.Writer writer = JSON_WRITER;

        public Builder readJson() {
            reader = JSON_READER;
            return this;
        }

        public Builder readHocon() {
            reader = HOCON_READER;
            return this;
        }

        public Builder writeJson() {
            writer = JSON_WRITER;
            return this;
        }

        public Builder writeJsonCompact() {
            writer = JSON_WRITER_COMPACT;
            return this;
        }

        public Builder writeHocon() {
            writer = HOCON_WRITER;
            return this;
        }

        public Builder writeHoconCompact() {
            writer = HOCON_WRITER_COMPACT;
            return this;
        }

        public NodeAdapter build() {
            return new NodeAdapter(this);
        }
    }
}
