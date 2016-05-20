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
import me.dags.data.node.NodeParseException;
import me.dags.data.node.NodeReader;
import me.dags.data.node.NodeTypeAdapter;
import me.dags.data.node.NodeTypeAdapters;
import me.dags.data.node.NodeWriter;

public class NodeAdapter {

    private static final String PARSE_ERROR = "An error occurred whilst parsing from %s: %s\n%s";
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

    public Node from(InputStream inputStream) throws IOException {
        try (NodeReader reader = readerProvider.provide(inputStream)) {
            return reader.readNode();
        }
    }

    public Node from(Path path) {
        try {
            return from(Files.newInputStream(path));
        } catch (IOException e) {
            throw NodeParseException.of(PARSE_ERROR, "Path", path, e.getMessage());
        }
    }

    public Node from(File file) {
        try {
            return from(new FileInputStream(file));
        } catch (IOException e) {
            throw NodeParseException.of(PARSE_ERROR, "File", file, e.getMessage());
        }
    }

    public Node from(URL url) {
        try {
            return from(url.openConnection().getInputStream());
        } catch (IOException e) {
            throw NodeParseException.of(PARSE_ERROR, "URL", url, e.getMessage());
        }
    }

    public Node from(String in) {
        try {
            return from(new ByteArrayInputStream(in.getBytes("UTF-8")));
        } catch (IOException e) {
            throw NodeParseException.of(PARSE_ERROR, "String", in, e.getMessage());
        }
    }

    public <T> T from(Node node, Class<T> type) {
        NodeTypeAdapter<T> adapter = NodeTypeAdapters.of(type);
        if (adapter != null) {
            return adapter.fromNode(node);
        }
        return null;
    }

    public <T> T from(InputStream inputStream, Class<T> type) {
        try {
            return from(from(inputStream), type);
        } catch (IOException e) {
            throw NodeParseException.of(PARSE_ERROR, "InputStream", inputStream, e.getMessage());
        }
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

    public void to(Node node, OutputStream out) throws IOException {
        try (NodeWriter writer = writerProvider.provide(out)) {
            writer.write(node);
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

        public Builder withReader(Provider.Reader provider) {
            if (provider != null) {
                reader = provider;
            }
            return this;
        }

        public Builder withWriter(Provider.Writer provider) {
            if (provider != null) {
                writer = provider;
            }
            return this;
        }

        public NodeAdapter build() {
            return new NodeAdapter(this);
        }
    }
}
