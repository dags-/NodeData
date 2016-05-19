package me.dags.data.mapping;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.dags.data.node.Node;

public class ObjectMapper {

    private final Serializer serializer = new Serializer(this);
    private final Deserializer deserializer = new Deserializer(this);
    private final Map<Class<?>, MappedClass<?>> mappings = new ConcurrentHashMap<>();

    final CollectionFactory collectionFactory;

    public ObjectMapper() {
        this(CollectionFactory.DEFAULT);
    }

    public ObjectMapper(CollectionFactory collectionFactory) {
        this.collectionFactory = collectionFactory;
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

    @SuppressWarnings("unchecked")
    <T> MappedClass<T> getMapping(Class<T> type) {
        MappedClass<T> mapping = (MappedClass<T>) mappings.get(type);
        if (mapping == null) {
            mappings.put(type, mapping = new MappedClass<>(type));
        }
        return mapping;
    }
}
