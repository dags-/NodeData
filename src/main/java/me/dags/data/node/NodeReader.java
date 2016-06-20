package me.dags.data.node;

import me.dags.data.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * @author dags <dags@dags.me>
 */
public abstract class NodeReader implements Closeable {

    private static final char UNDEFINED = (char) -2;

    private final InputStreamReader reader;
    private char last = UNDEFINED;
    private int pos = 0;

    private char[] buf = new char[4096];
    private int bufPos = 0;

    protected NodeReader(InputStream inputStream) {
        reader = new InputStreamReader(inputStream, StringUtils.UTF_8);
    }

    protected int read() throws IOException {
        return reader.read();
    }

    public Node readNode() throws IOException {
        char c = nextToken();
        switch (c) {
            case '{':
                return readObject();
            case '[':
                return readArray();
            case '"':
                return readString();
            default:
                return readNumber();
        }
    }

    protected NodeObject readObject() throws IOException {
        NodeObject object = new NodeObject();
        while (peekToken() != '}') {
            Node key = readNode();
            Node value = readNode();
            object.putValue(key,  value);
        }
        next();
        return object;
    }

    protected NodeArray readArray() throws IOException {
        NodeArray array = new NodeArray();
        while (peekToken() != ']') {
            Node element = readNode();
            array.add(element);
        }
        next();
        return array;
    }

    protected Node readString() throws IOException {
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

    protected Node readNumber() throws IOException {
        resetBuffer();
        char c = lastChar();
        boolean isDouble = false;
        while (true) {
            appendToBuffer(c);
            isDouble = isDouble || c == '.' || c == 'E';
            if (!isNumberChar(c = readChar())) {
                previous();
                break;
            }
        }
        Number number = Double.valueOf(bufferToString());
        return newNode(isDouble ? number : number.longValue());
    }

    protected boolean skipChar(char c) throws IOException {
        return Character.isWhitespace(c);
    }

    protected boolean isNumberChar(char c) {
        return Character.isDigit(c) || c == '.' || c == '-' || c == 'E' || c == 'e';
    }

    protected Node readFalse() throws IOException {
        skip(4);
        return Node.FALSE;
    }

    protected Node readTrue() throws IOException {
        skip(3);
        return Node.TRUE;
    }

    protected Node readNull() throws IOException {
        skip(3);
        return Node.NULL;
    }

    protected char readChar() throws IOException {
        if (pos == 0) {
            last = (char) read();
        } else {
            pos = 0;
        }
        return last;
    }

    protected char nextToken() throws IOException {
        char c = readChar();
        while (skipChar(c)) {
            c = readChar();
        }
        return c;
    }

    protected char peekToken() throws IOException {
        char c = nextToken();
        previous();
        return c;
    }

    protected char lastChar() {
        return last;
    }

    protected void previous() throws IOException {
        pos = 1;
    }

    protected void next() throws IOException {
        readChar();
    }

    protected void skip(int places) throws IOException {
        while (places-- > 0) {
            next();
        }
    }

    protected Node newNode(Object value) {
        return new Node(value);
    }

    protected void appendToBuffer(char c) {
        if (bufPos >= buf.length) {
            buf = Arrays.copyOf(buf, buf.length * 2);
        }
        buf[bufPos++] = c;
    }

    protected void resetBuffer() {
        bufPos = 0;
    }

    protected String bufferToString() {
        return new String(buf, 0, bufPos);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
