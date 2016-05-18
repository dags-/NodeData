package me.dags.data.mapping;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import me.dags.data.mapping.MappedClass.FieldMapping;
import me.dags.data.node.Node;
import me.dags.data.node.NodeObject;
import me.dags.data.node.NodeTypeAdapter;
import me.dags.data.node.NodeTypeAdapters;

public class Serializer {

    private final ObjectMapper mapper;

    Serializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    Node serializeObject(Object owner) throws IllegalArgumentException, IllegalAccessException {
        MappedClass<?> mapping = mapper.getMapping(owner.getClass());
        NodeObject object = new NodeObject();
        for (FieldMapping entry : mapping.fields) {
            Object value = entry.field.get(owner);
            Node field = serializeObject(value);
            object.put(entry.name, field);
        }
        return object;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    Node serialize(Object object) throws IllegalArgumentException, IllegalAccessException {
        if (object == null) {
            return Node.NULL;
        }
        NodeTypeAdapter serializer = NodeTypeAdapters.of(object.getClass());
        if (serializer != null) {
            return serializer.toNode(object);
        }
        if (object instanceof Iterable) {
            return NodeTypeAdapters.serialize((Collection) object);
        }
        if (object instanceof Map) {
            return NodeTypeAdapters.serialize((Map) object);
        }
        if (object.getClass().isArray()) {
            Object[] array = new Object[Array.getLength(object)];
            for (int i = 0; i < array.length; i++) {
                array[i] = Array.get(object, i);
            }
            return NodeTypeAdapters.serialize(array);
        }
        if (isPrimitive(object)) {
            return Node.of(object);
        }
        return serialize(object);
    }

    private static boolean isPrimitive(Object object) {
        return String.class.isInstance(object)
                || Boolean.class.isInstance(object)
                || Number.class.isInstance(object);
    }
}
