package me.dags.data.node;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import me.dags.data.StringUtils;

public abstract class NodeWriter implements Closeable {

    private final Writer writer;
    private int indents = 0;

    protected NodeWriter(OutputStream outputStream) {
        this.writer = new OutputStreamWriter(outputStream, StringUtils.UTF_8);
    }

    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        writer.flush();
        writer.close();
    }

    public final String write(Node node) throws IOException {
        writeRoot(node);
        return writer.toString();
    }

    protected void writeRoot(Node node) throws IOException {
        writeNode(node);
    }

    protected void writeNode(Node node) throws IOException {
        if (node.isNodeArray()) {
            writeArray(node.asNodeArray());
        } else if (node.isNodeObject()) {
            writeObject(node.asNodeObject());
        } else {
            writePrimitive(node);
        }
    }

    protected void writeObject(NodeObject node) throws IOException {
        if (node.empty()) {
            writeEmptyObject();
        } else {
            writeObjectEntries(node);
        }
    }

    protected void writeEmptyObject() throws IOException {
        append(beginObject());
        append(endObject());
    }

    protected void writeObjectEntries(NodeObject node) throws IOException {
        append(beginObject());
        append(lineBreak());
        incIndents();
        Iterator<Map.Entry<Node, Node>> iterator = node.entries().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Node, Node> entry = iterator.next();
            appendIndent();
            writeKeyValuePair(entry.getKey(), entry.getValue());
            if (iterator.hasNext()) {
                append(elementSeparator());
                append(lineBreak());
            }
        }
        decIndents();
        append(lineBreak());
        appendIndent();
        append(endObject());
    }

    protected void writeKeyValuePair(Node key, Node value) throws IOException {
        writeNode(key);
        append(keySeparator(key, value));
        append(padding());
        writeNode(value);
    }

    protected void writeArray(NodeArray node) throws IOException {
        if (node.empty()) {
            writeEmptyArray();
        } else if (node.primitiveList()) {
            writePrimitiveArray(node);
        } else {
            writeComplexArray(node);
        }
    }

    protected void writeEmptyArray() throws IOException {
        append(beginArray());
        append(endArray());
    }

    protected void writePrimitiveArray(NodeArray node) throws IOException {
        append(beginArray());
        Iterator<Node> iterator = node.values().iterator();
        while (iterator.hasNext()) {
            Node element = iterator.next();
            writeNode(element);
            if (iterator.hasNext()) {
                append(arraySeparator(element));
                append(padding());
            }
        }
        append(endArray());
    }

    protected void writeComplexArray(NodeArray node) throws IOException {
        append(beginArray());
        append(lineBreak());
        incIndents();
        Iterator<Node> iterator = node.values().iterator();
        while (iterator.hasNext()) {
            Node element = iterator.next();
            appendIndent();
            writeNode(element);
            if (iterator.hasNext()) {
                append(arraySeparator(element));
                append(lineBreak());
            }
        }
        decIndents();
        append(lineBreak());
        appendIndent();
        append(endArray());
    }

    protected void writePrimitive(Node node) throws IOException {
        Object value = node.asObject();
        if (value == null) {
            append("null");
        } else if (value instanceof String) {
            writeString(value.toString());
        } else {
            append(value.toString());
        }
    }

    protected void writeString(String string) throws IOException {
        append(StringUtils.escapeString(string));
    }

    protected final void append(String s) throws IOException {
        writer.append(s);
    }

    protected final void appendIndent() throws IOException {
        for (int i = indents; i > 0; i--) {
            append(indent());
        }
    }

    private void incIndents() {
        indents++;
    }

    private void decIndents() {
        indents--;
    }

    public abstract String indent();

    public abstract String beginArray();

    public abstract String endArray();

    public abstract String beginObject();

    public abstract String endObject();

    public abstract String keySeparator(Node key, Node value);

    public abstract String arraySeparator(Node value);

    public abstract String elementSeparator();

    public abstract String padding();

    public abstract String lineBreak();
}
