package me.dags.data.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NodeArray extends Node {

    public static final NodeArray EMPTY = new NodeArray(null);

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

    public List<Node> values() {
        return list();
    }

    public boolean empty() {
        return list().isEmpty();
    }

    public Node get(int index) {
        return list().get(index);
    }

    public boolean contains(Node key) {
        return contains(key.get());
    }

    public boolean contains(Object value) {
        for (Node node : list()) {
            if (node.get().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public void add(Object object) {
        list().add(Node.of(object));
    }

    public void add(Node value) {
        list().add(value);
    }

    public boolean primitiveList() {
        Node first = list().get(0);
        return first != null && first.isPrimitive();
    }
}
