package me.dags.data.mapping;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import me.dags.data.mapping.MappedClass.FieldMapping;
import me.dags.data.node.Node;
import me.dags.data.node.NodeArray;
import me.dags.data.node.NodeObject;

public class Deserializer {

    private static final Map<Class<?>, Function<Object, Object>> types = types();

    static Map<Class<?>, Function<Object, Object>> types() {
        Map<Class<?>, Function<Object, Object>> types = new HashMap<>();
        types.put(boolean.class, o -> ((Boolean) o).booleanValue());
        types.put(byte.class, o -> ((Number) o).byteValue());
        types.put(char.class, o -> ((Character) o).charValue());
        types.put(double.class, o -> ((Number) o).doubleValue());
        types.put(float.class, o -> ((Number) o).floatValue());
        types.put(int.class, o -> ((Number) o).intValue());
        types.put(long.class, o -> ((Number) o).longValue());
        types.put(short.class, o -> ((Number) o).shortValue());
        return Collections.unmodifiableMap(types);
    }

    private final ObjectMapper mapper;

    Deserializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    <T> T deserialize(Node node, Class<T> type) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        if (node.isNodeObject()) {
            return deserializeObject(node.asNodeObject(), type);
        } else {
            Object object = deserializeNode(node, type);
            if (type.isInstance(object)) {
                return type.cast(object);
            }
        }
        return null;
    }

    private Object deserializeNode(Node node, Class<?> type, Type... params) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        if (node.isNodeObject()) {
            if (Map.class.isAssignableFrom(type)) {
                return deserializeMap(node.asNodeObject(), type, params);
            }
            return deserializeObject(node.asNodeObject(), type);
        } else if (node.isNodeArray()) {
            return deserializeArray(node.asNodeArray(), type, params);
        } else if (node.isPrimitive()) {
            Function<Object, Object> func = types.get(type);
            return func == null ? node.asObject() : func.apply(node.asObject());
        }
        return null;
    }

    private <T> T deserializeObject(NodeObject object, Class<T> type) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        MappedClass<T> mapping = mapper.getMapping(type);
        T t = type.newInstance();
        for (FieldMapping field : mapping.fields) {
            Node node = object.get(field.name);
            System.out.println(field.name + ": " + node);
            Object value = deserializeNode(node, field.type(), field.typeArgs());
            field.field.set(t, value);
        }
        return t;
    }

    private Object deserializeMap(NodeObject object, Class<?> type, Type... params) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        Map<Object, Object> map = mapper.collectionFactory.supplyMap(type);
        Class<?> keyType = (Class<?>) params[0];
        Class<?> valueType = (Class<?>) params[1];
        for (Map.Entry<Node, Node> entry : object.entries()) {
            Object key = deserializeNode(entry.getKey(), keyType);
            Object value = deserializeNode(entry.getValue(), valueType);
            map.put(key,  value);
        }
        return map;
    }

    private Object deserializeArray(NodeArray array, Class<?> type, Type... params) throws InstantiationException, IllegalAccessException {
        Collection<Object> collection = type.isArray() ? new ArrayList<>() : mapper.collectionFactory.supplyCollection(type);
        Class<?> element = type.isArray() ? type.getComponentType() : params.length > 0 ? (Class<?>) params[0] : Object.class;
        for (Node node : array.values()) {
            Object value = deserializeNode(node, element);
            collection.add(value);
        }
        Object value = type.isArray() ? toArray(collection, element) : collection;
        return type.cast(value);
    }

    private static Object toArray(Collection<?> collection, Class<?> component) {
        Object array = Array.newInstance(component, collection.size());
        int i = 0;
        for (Object o : collection) {
            Array.set(array, i++, o);
        }
        return array;
    }
}
