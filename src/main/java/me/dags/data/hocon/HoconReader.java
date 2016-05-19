package me.dags.data.hocon;

import java.io.IOException;
import java.io.InputStream;

import me.dags.data.StringUtils;
import me.dags.data.node.Node;
import me.dags.data.node.NodeObject;
import me.dags.data.node.NodeParseException;
import me.dags.data.node.NodeReader;

/*
 * NB not fully compliant with hocon spec
 * - Doesn't parse keys 'key1.key2.key3:' as nested objects
 * - Probably doesn't do other stuff either
 */
public class HoconReader extends NodeReader {

    private boolean root = true;

    public HoconReader(InputStream inputStream) {
        super(inputStream);
    }

    @Override
    public Node readNode() throws IOException {
        if (root) {
            return readRoot();
        }
        char c = nextToken();
        switch (c) {
            case '{':
                return readObject();
            case '[':
                return readArray();
            case '"':
                return readString();
            case ',':
            case ':':
            case '=':
                return readNode();
            case (char) -1:
                throw NodeParseException.of("Unexpected end of hocon inputstream!");
            default:
                return readPrimitive();
        }
    }

    @Override
    protected Node readNumber() throws IOException {
        return newNode(Double.NaN);
    }

    @Override
    protected Node readFalse() throws IOException {
        return Node.FALSE;
    }

    @Override
    protected Node readTrue() throws IOException {
        return Node.TRUE;
    }

    @Override
    protected Node readNull() throws IOException {
        return Node.NULL;
    }

    private Node readRoot() throws IOException {
        root = false;
        char c = peekToken();
        if (c == '{' || c == '[') {
            return readNode();
        }
        NodeObject object = new NodeObject();
        while (peekToken() != (char) -1) {
            Node key = readNode();
            Node value = readNode();
            object.putValue(key,  value);
        }
        return object;
    }

    private Node readPrimitive() throws IOException {
        String input = readRaw();
        if (StringUtils.isNumber(input)) {
            Double d = Double.valueOf(input);
            if (input.contains(".") || input.contains("e") || input.contains("E")) {
                return newNode(d);
            }
            return newNode(d.longValue());
        } else if (input.equalsIgnoreCase("true")) {
            return Node.TRUE;
        } else if (input.equalsIgnoreCase("false")) {
            return Node.FALSE;
        } else if (input.equalsIgnoreCase("null")) {
            return Node.NULL;
        } else {
            return newNode(input);
        }
    }

    private String readRaw() throws IOException {
        resetBuffer();
        char c = lastChar();
        while(!breakRaw(c)) {
            appendToBuffer(c);
            c = readChar();
        }
        previous();
        return bufferToString();
    }

    private boolean breakRaw(char c) {
        return !Character.isAlphabetic(c) && !Character.isDigit(c) && c != '.' && c != '-';
    }
}
