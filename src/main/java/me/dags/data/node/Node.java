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
        if (isPresent()) {
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            if (value instanceof String) {
                return ((String) value).equalsIgnoreCase("true");
            }
        }
        return false;
    }

    public Number asNumber() {
        if (isPresent()) {
            if (value instanceof Number) {
                return (Number) value;
            }
            if (value instanceof String && StringUtils.isNumber((String) value)) {
                return Double.parseDouble((String) value);
            }
        }
        return Double.NaN;
    }

    public String asString() {
        if (isPresent()) {
            if (value instanceof String) {
                return StringUtils.unEscapeString(value.toString());
            }
            return value.toString();
        }
        return "null";
    }

    public NodeArray asNodeArray() {
        return NodeArray.EMPTY;
    }

    public NodeObject asNodeObject() {
        return NodeObject.EMPTY;
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

    public boolean equalTo(Object other) {
        return value == other;
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
        if (object instanceof Node) {
            return (Node) object;
        }
        return NodeTypeAdapters.serialize(object);
    }
}
