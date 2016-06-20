package me.dags.data.node;

import me.dags.data.hocon.HoconWriter;
import me.dags.data.json.JsonWriter;

import java.io.OutputStream;

public abstract class WriterProvider {

    public static final WriterProvider JSON_PRETTY = new Json(false);
    public static final WriterProvider JSON_COMPACT = new Json(true);
    public static final WriterProvider HOCON_PRETTY = new Hocon(false);
    public static final WriterProvider HOCON_COMAPCT = new Hocon(true);

    public abstract NodeWriter get(OutputStream writer);

    static class Hocon extends WriterProvider {

        private final boolean compact;

        Hocon(boolean compact) {
            this.compact = compact;
        }

        @Override
        public NodeWriter get(OutputStream out) {
            return new HoconWriter(out, compact);
        }
    }

    static class Json extends WriterProvider {

        private final boolean compact;

        Json(boolean compact) {
            this.compact = compact;
        }

        @Override
        public NodeWriter get(OutputStream out) {
            return new JsonWriter(out, compact);
        }
    }
}
