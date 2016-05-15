package me.dags.data.json;

import me.dags.data.node.Node;
import me.dags.data.node.NodeArray;
import me.dags.data.node.NodeObject;
import me.dags.data.node.NodeReader;

import java.io.IOException;
import java.io.InputStream;

public class JsonReader extends NodeReader  {

    public JsonReader(InputStream in) {
        super(in);
    }

    @Override
    public Node readNode() throws IOException {
        char c = nextToken();
        switch (c) {
            case '[':
                return readArray();
            case '{':
                return readObject();
            case '"':
                return readString();
            case 'f':
            case 'F':
                return readFalse();
            case 't':
            case 'T':
                return readTrue();
            case 'n':
            case 'N':
                return readNull();
            case (char) -1:
                throw new UnsupportedOperationException("Unexpected end!");
        }
        if (isNumber(c)) {
            return readNumber();
        }
        return readNode();
    }

    @Override
    public NodeObject readObject() throws IOException {
        NodeObject object = new NodeObject();
        while (peekToken() != '}') {
            Node key = readNode();
            Node value = readNode();
            object.putValue(key,  value);
        }
        next();
        return object;
    }

    @Override
    public NodeArray readArray() throws IOException {
        NodeArray array = new NodeArray();
        while (peekToken() != ']') {
            Node element = readNode();
            array.add(element);
        }
        next();
        return array;
    }

    @Override
    public Node readString() throws IOException {
        resetBuffer();
        char c = readChar();
        boolean escape = false;
        while (escape || c != '"') {
            appendToBuffer(c);
            escape = c == '\\';
            c = readChar();
        }
        return newNode(bufferToString());
    }

    @Override
    public Node readNumber() throws IOException {
        resetBuffer();
        char c = lastChar();
        boolean isDouble = false;
        while (true) {
            appendToBuffer(c);
            isDouble = isDouble || c == '.' || c == 'E';
            if (!isNumber(c = readChar())) {
                previous();
                break;
            }
        }
        Number number = Double.valueOf(bufferToString());
        return newNode(isDouble ? number : number.longValue());
    }

    @Override
    protected boolean skipChar(char c) throws IOException  {
        return Character.isWhitespace(c);
    }

    private static boolean isNumber(char c) {
        return Character.isDigit(c) || c == '.' || c == '-' || c == 'E' || c == 'e';
    }
}