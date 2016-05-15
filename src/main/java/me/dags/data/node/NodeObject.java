package me.dags.data.node;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class NodeObject extends Node {

    public static final NodeObject EMPTY = new NodeObject(null);

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
}
