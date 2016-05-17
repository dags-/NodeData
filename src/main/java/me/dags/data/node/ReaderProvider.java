package me.dags.data.node;

import java.io.InputStream;

import me.dags.data.hocon.HoconReader;
import me.dags.data.json.JsonReader;

public abstract class ReaderProvider {

    public static final ReaderProvider JSON = new Json();
    public static final ReaderProvider HOCON = new Hocon();

    public abstract NodeReader get(InputStream inputStream);

    private static class Hocon extends ReaderProvider {

        @Override
        public NodeReader get(InputStream inputStream) {
            return new HoconReader(inputStream);
        }
    }

    private static class Json extends ReaderProvider {

        @Override
        public NodeReader get(InputStream inputStream) {
            return new JsonReader(inputStream);
        }
    }
}
