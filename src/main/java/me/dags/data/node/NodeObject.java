package me.dags.data.node;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class NodeObject extends Node {

    static final NodeObject EMPTY = new NodeObject(null);

    public NodeObject() {
        super(new LinkedHashMap<>());
    }

    private NodeObject(Object nop) {
        super(Collections.emptyMap());
    }

    @SuppressWarnings("unchecked")
    private Map<Node,Node> map() {
        return (Map<Node,Node>) get();
    }

    @Override
    public NodeObject asNodeObject() {
        return this;
    }

    @Override
    public boolean isNodeObject() {
        return true;
    }

    @Override
    public boolean isPresent() {
        return this != EMPTY;
    }

    public boolean empty() {
        return map().isEmpty();
    }

    public Map<Object, Object> toMap() {
        return NodeTypeAdapters.deserialize(this);
    }

    public Collection<Map.Entry<Node, Node>> entries() {
        return map().entrySet();
    }

    public Node get(Object key) {
        if (isPresent()) {
            return Node.NULL;
        }
        Node node = map().get(Node.of(key));
        return node == null ? Node.NULL : node;
    }

    public NodeObject getObject(Object key) {
        if (isPresent()) {
            return NodeObject.EMPTY;
        }
        Node node = get(key);
        return node.isPresent() && node.isNodeObject() ? node.asNodeObject() : NodeObject.EMPTY;
    }

    public NodeArray getArray(Object key) {
        if (isPresent()) {
            return NodeArray.EMPTY;
        }
        Node node = get(key);
        return node.isPresent() && node.isNodeArray() ? node.asNodeArray() : NodeArray.EMPTY;
    }

    public Node getOrPut(Node key, Node value) {
        checkEmpty();
        Node current = get(key);
        if (!current.isPresent()) {
            put(key, current = value);
        }
        return current;
    }

    public Node getOrPut(Object k, Object v) {
        checkEmpty();
        Node key = k instanceof Node ? (Node) k : Node.of(k);
        Node value = v instanceof Node ? (Node) v : Node.of(v);
        return getOrPut(key, value);
    }

    public boolean contains(Node key) {
        return isPresent() && map().containsKey(key);
    }

    public boolean contains(Object key) {
        return isPresent() && map().containsKey(Node.of(key));
    }

    public void put(Object k, Object v) {
        checkEmpty();
        Node key = k instanceof Node ? (Node) k : Node.of(k);
        Node value = v instanceof Node ? (Node) v : Node.of(v);
        putValue(key, value);
    }

    public void putValue(Node k, Node v) {
        checkEmpty();
        map().put(k, v);
    }

    public void ifPresent(Object key, Consumer<Node> valueConsumer) {
        ifPresent(Node.of(key), valueConsumer);
    }

    public void ifPresent(Node key, Consumer<Node> valueConsumer) {
        if (isPresent()) {
            Node node = get(key);
            if (node != null) {
                valueConsumer.accept(node);
            }
        }
    }

    public <T> T map(Object key, Function<Node, T> mapper, T defaultVal) {
        if (isPresent()) {
            Node node = map().get(Node.of(key));
            if (node.isPresent()) {
                return mapper.apply(node);
            }
        }
        return defaultVal;
    }

    private void checkEmpty() {
        if (!this.isPresent()) {
            throw new NodeError("Attempted to modify an EMPTY NodeObject!");
        }
    }
}
