package me.dags.data.node;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dags <dags@dags.me>
 */
public class NodeAdapters {

    private static final Map<Class<?>, Entry<?>> map = new HashMap<>();

    public static <T> void register(Class<T> type, NodeAdapter<T> nodeSerializer) {
        map.put(type, new Entry<>(type, nodeSerializer));
    }

    @SuppressWarnings("unchecked")
    public static <T> NodeAdapter<T> of(Class<T> clazz) {
        Entry<?> entry = map.get(clazz);
        if (entry != null && entry.type.equals(clazz)) {
            return ((Entry<T>) entry).nodeSerializer;
        }
        return null;
    }

    public static Node serialize(Map<?, ?> map) {
        NodeObject object = new NodeObject();
        map.entrySet().forEach(e -> object.put(e.getKey(), e.getValue()));
        return object;
    }

    public static Node serialize(Iterable<?> iterable) {
        NodeArray nodeArray = new NodeArray();
        for (Object object : iterable) {
            nodeArray.add(object);
        }
        return nodeArray;
    }

    public static Node serialize(Object[] array) {
        NodeArray nodeArray = new NodeArray();
        for (Object object : array) {
            nodeArray.add(object);
        }
        return nodeArray;
    }

    @SuppressWarnings("unchecked")
    public static Node serialize(Object object) {
        NodeAdapter serializer = NodeAdapters.of(object.getClass());
        if (serializer != null) {
            return serializer.toNode(object);
        }
        if (object instanceof Iterable) {
            return serialize((Collection) object);
        }
        if (object instanceof Map) {
            return serialize((Map) object);
        }
        if (object instanceof Object[]) {
            return serialize((Object[]) object);
        }
        return new Node(object);
    }

    private static class Entry<T> {

        private final Class<T> type;
        private final NodeAdapter<T> nodeSerializer;

        private Entry(Class<T> type, NodeAdapter<T> nodeSerializer) {
            this.type = type;
            this.nodeSerializer = nodeSerializer;
        }
    }
}
