package me.dags.data.json;

import java.io.IOException;
import java.io.OutputStream;

import me.dags.data.StringUtils;
import me.dags.data.node.Node;
import me.dags.data.node.NodeWriter;

public class JsonWriter extends NodeWriter {

    private final String indentSpaces;
    private final String lineBreak;
    private final String padding;

    public JsonWriter(OutputStream outputStream) {
        this(outputStream, false);
    }

    public JsonWriter(OutputStream outputStream, boolean compact) {
        super(outputStream);
        indentSpaces = compact ? "" : "  ";
        lineBreak = compact ? "" : "\n";
        padding = compact ? "" : " ";
    }

    protected void swriteKeyValuePair(Node key, Node value) throws IOException {
        if (!key.isPresent() || key.isBoolean() || key.isNumber()) {
            append("\"");
            writeNode(key);
            append("\"");
        } else {
            writeNode(key);
        }
        append(keySeparator(key, value));
        append(padding());
        writeNode(value);
    }

    @Override
    public void writeString(String string) throws IOException {
        append("\"" + StringUtils.escapeString(string) + "\"");
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
        return ":";
    }

    @Override
    public String elementSeparator() {
        return ",";
    }

    @Override
    public String lineBreak() {
        return lineBreak;
    }

    @Override
    public String padding() {
        return padding;
    }

    @Override
    public String indent() {
        return indentSpaces;
    }

    @Override
    public String arraySeparator(Node value) {
        return ",";
    }
}