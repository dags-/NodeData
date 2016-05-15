package me.dags.data.node;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

public interface NodeSerializer {

    boolean prettyPrint();

    Node deserialize(InputStream inputStream);

    Node deserialize(File file);

    Node deserialize(Path path);

    Node deserialize(String in);

    Node deserialize(URL url);

    String serialize(Node node, boolean compact);

    void serialize(Node node, File out, boolean compact);

    void serialize(Node node, Path out, boolean compact);

    default String serialize(Node node) {
        return serialize(node, !prettyPrint());
    }

    default void serialize(Node node, File out) {
        serialize(node, out, !prettyPrint());
    }

    default void serialize(Node node, Path out) {
        serialize(node, out, !prettyPrint());
    }

    default <T> T deserialize(File file, Class<T> type) {
        return deserialize(deserialize(file), type);
    }

    default <T> T deserialize(InputStream inputStream, Class<T> type) {
        return deserialize(deserialize(inputStream), type);
    }

    default <T> T deserialize(Path path, Class<T> type) {
        return deserialize(deserialize(path), type);
    }

    default <T> T deserialize(String input, Class<T> type) {
        return deserialize(deserialize(input), type);
    }

    default <T> T deserialize(URL url, Class<T> type) {
        return deserialize(deserialize(url), type);
    }

    default <T> T deserialize(Node node, Class<T> type) {
        NodeAdapter<T> adapter = NodeAdapters.of(type);
        if (adapter != null) {
            return adapter.fromNode(node);
        }
        return null;
    }
}
