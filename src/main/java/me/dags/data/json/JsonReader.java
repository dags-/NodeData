package me.dags.data.json;

import me.dags.data.node.Node;
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
        if (isNumberChar(c)) {
            return readNumber();
        }
        return readNode();
    }

    @Override
    protected boolean skipChar(char c) throws IOException  {
        return Character.isWhitespace(c);
    }
}