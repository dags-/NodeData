package me.dags.data.hocon;

import me.dags.data.StringUtils;
import me.dags.data.node.Node;
import me.dags.data.node.NodeArray;
import me.dags.data.node.NodeWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

/**
 * @author dags <dags@dags.me>
 */
public class HoconWriter extends NodeWriter {

    private final StringUtils stringUtils = new StringUtils();
    private final boolean compact;
    private final String indentSpaces;
    private final String lineBreak;
    private final String padding;

    public HoconWriter(OutputStream outputStream, boolean compact) {
        super(outputStream);
        this.compact = compact;
        indentSpaces = compact ? "" : "  ";
        lineBreak = compact ? "" : "\n";
        padding = compact ? "" : " ";
    }

    @Override
    public void writeRoot(Node node) throws IOException {
        if (node.isNodeObject()) {
            Iterator<Map.Entry<Node, Node>> iterator = node.asNodeObject().entries().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Node, Node> entry = iterator.next();
                appendIndent();
                writeKeyValuePair(entry.getKey(), entry.getValue());
                if (iterator.hasNext()) {
                    append(elementSeparator());
                    append(lineBreak());
                }
            }
        } else {
            writeNode(node);
        }
    }

    @Override
    public void writeString(String string) throws IOException {
        append(stringUtils.safeString(string));
    }

    @Override
    public void writePrimitiveArray(NodeArray node) throws IOException {
        super.writeComplexArray(node);
    }

    @Override
    public String indent() {
        return indentSpaces;
    }

    @Override
    public String beginArray() {
        return "[";
    }

    @Override
    public String endArray() {
        return "]";
    }

    @Override
    public String beginObject() {
        return "{";
    }

    @Override
    public String endObject() {
        return "}";
    }

    @Override
    public String keySeparator(Node key, Node value) {
        return key.isPrimitive() && value.isNodeObject() ? "" : ":" + padding;
    }

    @Override
    public String elementSeparator() {
        return compact ? "," : "";
    }

    @Override
    public String arraySeparator(Node value) {
        return value.isNodeObject() || value.isNodeArray() || value.isPrimitive() ? "" : ",";
    }

    @Override
    public String padding() {
        return "";
    }

    @Override
    public String lineBreak() {
        return lineBreak;
    }
}
