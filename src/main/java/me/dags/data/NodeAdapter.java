package me.dags.data;

import me.dags.data.node.*;

import java.io.*;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.stream.Stream;

public class NodeAdapter {

    private final ReaderProvider readerProvider;
    private final WriterProvider writerProvider;

    public NodeAdapter(ReaderProvider reader, WriterProvider writer) {
        this.readerProvider = reader;
        this.writerProvider = writer;
    }

    public Node from(InputStream inputStream) {
        try (NodeReader reader = readerProvider.get(inputStream)) {
            return reader.readNode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Node.NULL;
    }

    public Stream<Node> fromDir(Path dir, String extension) {
        if (Files.isDirectory(dir)) {
            try {
                PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*" + extension);
                return Files.list(dir)
                        .filter(matcher::matches)
                        .map(this::from);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Stream.empty();
    }

    public Stream<Node> fromDir(File dir, String extension) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                return Stream.of(files).filter(f -> f.getName().endsWith(extension)).map(this::from);
            }
        }
        return Stream.empty();
    }

    public Node from(Path path) {
        if (Files.exists(path)) {
            try (NodeReader reader = readerProvider.get(Files.newInputStream(path))) {
                return reader.readNode();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Node.NULL;
    }

    public Node from(File file) {
        if (file.exists()) {
            try (NodeReader reader = readerProvider.get(new FileInputStream(file))) {
                return reader.readNode();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Node.NULL;
    }

    public Node from(URL url) {
        try (NodeReader reader = readerProvider.get(url.openConnection().getInputStream())) {
            return reader.readNode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Node.NULL;
    }

    public Node from(String in) {
        try (NodeReader reader = readerProvider.get(new ByteArrayInputStream(in.getBytes("UTF-8")))) {
            return reader.readNode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Node.NULL;
    }

    public String to(Node node) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (NodeWriter writer = writerProvider.get(out)) {
            writer.write(node);
            return out.toString("UTF-8");
        } catch (IOException e) {
            return "{}";
        }
    }

    public void to(Node node, OutputStream out) {
        try (NodeWriter writer = writerProvider.get(out)) {
            writer.write(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void to(Node node, File out) {
        try {
            out.getParentFile().mkdirs();
            out.createNewFile();
            try (NodeWriter writer = writerProvider.get(new FileOutputStream(out))) {
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
            try (NodeWriter writer = writerProvider.get(Files.newOutputStream(out))) {
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

        private ReaderProvider reader = ReaderProvider.JSON;
        private WriterProvider writer = WriterProvider.JSON_PRETTY;

        public Builder readJson() {
            reader = ReaderProvider.JSON;
            return this;
        }

        public Builder readHocon() {
            reader = ReaderProvider.HOCON;
            return this;
        }

        public Builder writeJson() {
            writer = WriterProvider.JSON_PRETTY;
            return this;
        }

        public Builder writeJsonCompact() {
            writer = WriterProvider.JSON_COMPACT;
            return this;
        }

        public Builder writeHocon() {
            writer = WriterProvider.HOCON_PRETTY;
            return this;
        }

        public Builder writeHoconCompact() {
            writer = WriterProvider.HOCON_COMAPCT;
            return this;
        }

        public NodeAdapter build() {
            return new NodeAdapter(reader, writer);
        }
    }
}
