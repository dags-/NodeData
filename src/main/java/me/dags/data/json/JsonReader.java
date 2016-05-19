package me.dags.data.json;

import java.io.IOException;
import java.io.InputStream;

import me.dags.data.node.Node;
import me.dags.data.node.NodeParseException;
import me.dags.data.node.NodeReader;

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
                throw NodeParseException.of("Unexpected end of json inputstream!");
            default:
                if (isNumberChar(c)) {
                    return readNumber();
                }
                return readNode();
        }
    }

    @Override
    protected boolean skipChar(char c) throws IOException  {
        return Character.isWhitespace(c);
    }
}