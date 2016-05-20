package me.dags.data.mapping;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import me.dags.data.NodeAdapter;
import me.dags.data.node.Node;

public class ObjectMapper {

    private final Serializer serializer = new Serializer(this);
    private final Deserializer deserializer = new Deserializer(this);
    private final Map<Class<?>, MappedClass<?>> mappings = new ConcurrentHashMap<>();

    private final NodeAdapter adapter;
    final CollectionFactory collectionFactory;

    public ObjectMapper() {
        this(null, CollectionFactory.DEFAULT);
    }

    private ObjectMapper(Builder builder) {
        this(builder.adapter, builder.collections);
    }

    private ObjectMapper(NodeAdapter adapter, CollectionFactory collectionFactory) {
        this.collectionFactory = collectionFactory;
        this.adapter = adapter;
    }

    public Node toNode(Object in) {
        try {
            return serializer.serialize(in);
        } catch (Exception e) {
            return Node.NULL;
        }
    }

    public <T> T fromNode(Node node, Class<T> type) {
        try {
            return deserializer.deserialize(node, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ObjectMapper bind(Class<?> type) {
        getMapping(type);
        return this;
    }

    public <T> Optional<T> from(InputStream inputStream, Class<T> type) throws IOException {
        if (!this.adapterIsPresent()) {
            return Optional.empty();
        }
        return from(adapter.from(inputStream), type);
    }

    public <T> Optional<T> from(Path path, Class<T> type) {
        if (!this.adapterIsPresent()) {
            return Optional.empty();
        }
        return from(adapter.from(path), type);
    }

    public <T> Optional<T> from(File file, Class<T> type) {
        if (!this.adapterIsPresent()) {
            return Optional.empty();
        }
        return from(adapter.from(file), type);
    }

    public <T> Optional<T> from(URL url, Class<T> type) {
        if (!this.adapterIsPresent()) {
            return Optional.empty();
        }
        return from(adapter.from(url), type);
    }

    public <T> Optional<T> from(String string, Class<T> type) {
        if (!this.adapterIsPresent()) {
            return Optional.empty();
        }
        return from(adapter.from(string), type);
    }

    public void to(Node node, OutputStream out) throws IOException {
        if (adapterIsPresent()) {
            adapter.to(node, out);
        }
    }

    public void to(Node node, File out) {
        if (adapterIsPresent()) {
            adapter.to(node, out);
        }
    }

    public void to(Node node, Path out) {
        if (adapterIsPresent()) {
            adapter.to(node, out);
        }
    }

    public void to(Object object, OutputStream out) throws IOException {
        if (adapterIsPresent()) {
            adapter.to(toNode(object), out);
        }
    }

    public void to(Object object, File out) {
        if (adapterIsPresent()) {
            adapter.to(toNode(object), out);
        }
    }

    public void to(Object object, Path out) {
        if (adapterIsPresent()) {
            adapter.to(toNode(object), out);
        }
    }

    private <T> Optional<T> from(Node node, Class<T> type) {
        if (node != Node.NULL) {
            return Optional.ofNullable(fromNode(node, type));
        }
        return Optional.empty();
    }

    private boolean adapterIsPresent() {
        return adapter != null;
    }

    @SuppressWarnings("unchecked")
    <T> MappedClass<T> getMapping(Class<T> type) {
        MappedClass<T> mapping = (MappedClass<T>) mappings.get(type);
        if (mapping == null) {
            mappings.put(type, mapping = new MappedClass<>(type));
        }
        return mapping;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private NodeAdapter adapter = null;
        private CollectionFactory collections = CollectionFactory.DEFAULT;

        public Builder adapter(NodeAdapter adapter) {
            if (adapter != null) {
                this.adapter = adapter;
            }
            return this;
        }

        public Builder collectionFacotry(CollectionFactory factory) {
            if (collections != null) {
                this.collections = factory;
            }
            return this;
        }

        public ObjectMapper build() {
            return new ObjectMapper(this);
        }
    }
}
