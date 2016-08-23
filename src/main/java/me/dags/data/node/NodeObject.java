package me.dags.data.node;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

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

    public Collection<Map.Entry<Node, Node>> entries() {
        return map().entrySet();
    }

    public Node get(Node key) {
        return map().get(key);
    }

    public Node get(Object key) {
        return map().get(Node.of(key));
    }

    public Node getOrPut(Node key, Node value) {
        Node current = get(key);
        if (!current.isPresent()) {
            put(key, current = value);
        }
        return current;
    }

    public Node getOrPut(Object k, Object v) {
        Node key = k instanceof Node ? (Node) k : Node.of(k);
        Node value = v instanceof Node ? (Node) v : Node.of(v);
        return getOrPut(key, value);
    }

    public boolean contains(Node key) {
        return map().containsKey(key);
    }

    public boolean contains(Object key) {
        return map().containsKey(Node.of(key));
    }

    public void put(Object k, Object v) {
        Node key = k instanceof Node ? (Node) k : Node.of(k);
        Node value = v instanceof Node ? (Node) v : Node.of(v);
        putValue(key, value);
    }

    public void putValue(Node k, Node v) {
        map().put(k, v);
    }

    public void ifPresent(Object key, Consumer<Node> valueConsumer) {
        ifPresent(Node.of(key), valueConsumer);
    }

    public void ifPresent(Node key, Consumer<Node> valueConsumer) {
        Node node = get(key);
        if (node != null) {
            valueConsumer.accept(node);
        }
    }

    public <T> T map(Object key, Function<Node, T> mapper, T defaultVal) {
        Node node = map().get(Node.of(key));
        if (node.isPresent()) {
            return mapper.apply(node);
        }
        return defaultVal;
    }
}
