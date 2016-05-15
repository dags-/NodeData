package me.dags.data.node;

import sun.nio.cs.StreamDecoder;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author dags <dags@dags.me>
 */
public abstract class NodeReader implements Closeable {

    private static final char UNDEFINED = (char) -2;

    private final StreamDecoder decoder;
    private char last = UNDEFINED;
    private int pos = 0;

    private char[] buf = new char[4096];
    private int bufPos = 0;

    protected NodeReader(InputStream inputStream) {
        this.decoder = StreamDecoder.forInputStreamReader(inputStream, this, Charset.forName("UTF-8"));
    }

    public int read() throws IOException {
        return decoder.read();
    }

    public abstract Node readNode() throws IOException;

    public abstract NodeObject readObject() throws IOException;

    public abstract NodeArray readArray() throws IOException;

    public abstract Node readString() throws IOException;

    public abstract Node readNumber() throws IOException;

    protected abstract boolean skipChar(char c) throws IOException;

    public Node readFalse() throws IOException {
        skip(4);
        return Node.FALSE;
    }

    public Node readTrue() throws IOException {
        skip(3);
        return Node.TRUE;
    }

    public Node readNull() throws IOException {
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
        decoder.close();
    }
}
