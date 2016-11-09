package me.dags.data.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class NodeArray extends Node {

    static final NodeArray EMPTY = new NodeArray(null);

    public NodeArray() {
        super(new ArrayList<>());
    }

    private NodeArray(Object nop) {
        super(Collections.emptyList());
    }

    @SuppressWarnings("unchecked")
    private List<Node> list() {
        return (List<Node>) get();
    }

    public List<Object> toList() {
        return NodeTypeAdapters.deserialize(this);
    }

    @Override
    public NodeArray asNodeArray() {
        return this;
    }

    @Override
    public boolean isNodeArray() {
        return true;
    }

    @Override
    public boolean isPresent() {
        return this != EMPTY;
    }

    public int count() {
        return list().size();
    }

    public List<Node> values() {
        return list();
    }

    public boolean empty() {
        return list().isEmpty();
    }

    public Node get(int index) {
        if (isPresent() && index < count()) {
            Node node = list().get(index);
            return node != null ? node : Node.NULL;
        }
        return Node.NULL;
    }

    public <T> Stream<T> map(Function<Node, T> mapper) {
        return values().stream().map(mapper);
    }

    public boolean contains(Node key) {
        return contains(key.get());
    }

    public boolean contains(Object value) {
        if (isPresent()) {
            for (Node node : list()) {
                if (node.get().equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void add(Object object) {
        checkEmpty();
        list().add(Node.of(object));
    }

    public void add(Node value) {
        checkEmpty();
        list().add(value);
    }

    public boolean primitiveList() {
        Node first = list().get(0);
        return first != null && first.isPrimitive();
    }

    private void checkEmpty() {
        if (!this.isPresent()) {
            throw new NodeError("Attempted to modify an EMPTY NodeArray!");
        }
    }
}
