package me.dags.data.node;

import me.dags.data.StringUtils;

public class Node {

    public static final Node NULL = new Node(new Object());
    public static final Node TRUE = new Node(true);
    public static final Node FALSE = new Node(false);

    private final Object value;

    protected Node(Object o) {
        this.value = o;
    }

    Object get() {
        return value;
    }

    public Object asObject() {
        return isPresent() ? value : null;
    }

    public Boolean asBoolean() {
        if (isPresent() && value instanceof Boolean) {
            return (Boolean) value;
        }
        return false;
    }

    public Number asNumber() {
        if (isPresent() && value instanceof Number) {
            return (Number) value;
        }
        return Double.NaN;
    }

    public String asString() {
        if (isPresent() && value instanceof String) {
            return StringUtils.unEscapeString(value.toString());
        }
        return "null";
    }

    public NodeArray asNodeArray() {
        return null;
    }

    public NodeObject asNodeObject() {
        return null;
    }

    public boolean isPresent() {
        return this != NULL;
    }

    public boolean isPrimitive() {
        return !isNodeObject() && !isNodeArray();
    }

    public boolean isNodeObject() {
        return false;
    }

    public boolean isNodeArray() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Node)) {
            return false;
        }
        Node node = (Node) other;
        return this == node || (this.isPresent() && node.isPresent() && get().equals(node.get()));
    }

    @Override
    public int hashCode() {
        return this == NULL ? super.hashCode() : get().hashCode();
    }

    @Override
    public String toString() {
        return isPresent() ? value.toString() : "null";
    }

    public static Node of(Object object) {
        if (object == null) {
            return NULL;
        }
        return NodeTypeAdapters.serialize(object);
    }
}
