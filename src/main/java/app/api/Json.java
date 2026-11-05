package app.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Small JSON reader/writer for the JDK HTTP API, avoiding a second application framework. */
final class Json {
    private Json() {}

    static Object parse(String source) {
        Parser parser = new Parser(source == null ? "" : source);
        Object result = parser.value();
        parser.space();
        if (parser.index != parser.source.length()) throw new IllegalArgumentException("JSON no válido.");
        return result;
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> object(String source) {
        Object result = parse(source);
        if (!(result instanceof Map<?, ?>)) throw new IllegalArgumentException("Se esperaba un objeto JSON.");
        return (Map<String, Object>) result;
    }

    static String stringify(Object value) {
        if (value == null) return "null";
        if (value instanceof String text) return quote(text);
        if (value instanceof Number || value instanceof Boolean) return value.toString();
        if (value instanceof Map<?, ?> map) {
            List<String> fields = new ArrayList<>();
            map.forEach((key, item) -> fields.add(quote(String.valueOf(key)) + ":" + stringify(item)));
            return "{" + String.join(",", fields) + "}";
        }
        if (value instanceof Iterable<?> values) {
            List<String> items = new ArrayList<>();
            values.forEach(item -> items.add(stringify(item)));
            return "[" + String.join(",", items) + "]";
        }
        throw new IllegalArgumentException("Tipo JSON no admitido.");
    }

    private static String quote(String value) {
        StringBuilder out = new StringBuilder("\"");
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\b' -> out.append("\\b");
                case '\f' -> out.append("\\f");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default -> { if (ch < 0x20) out.append(String.format("\\u%04x", (int) ch)); else out.append(ch); }
            }
        }
        return out.append('"').toString();
    }

    private static final class Parser {
        private final String source;
        private int index;
        Parser(String source) { this.source = source; }
        void space() { while (index < source.length() && Character.isWhitespace(source.charAt(index))) index++; }
        Object value() {
            space();
            if (index >= source.length()) throw new IllegalArgumentException("JSON vacío.");
            return switch (source.charAt(index)) {
                case '{' -> object();
                case '[' -> array();
                case '"' -> string();
                case 't' -> literal("true", Boolean.TRUE);
                case 'f' -> literal("false", Boolean.FALSE);
                case 'n' -> literal("null", null);
                default -> number();
            };
        }
        Map<String, Object> object() {
            index++;
            Map<String, Object> values = new LinkedHashMap<>();
            space();
            if (take('}')) return values;
            do {
                space();
                if (index >= source.length() || source.charAt(index) != '"') throw new IllegalArgumentException("Clave JSON no válida.");
                String key = string();
                space();
                if (!take(':')) throw new IllegalArgumentException("Falta ':' en JSON.");
                values.put(key, value());
                space();
                if (take('}')) return values;
            } while (take(','));
            throw new IllegalArgumentException("Objeto JSON incompleto.");
        }
        List<Object> array() {
            index++;
            List<Object> values = new ArrayList<>();
            space();
            if (take(']')) return values;
            do {
                values.add(value());
                space();
                if (take(']')) return values;
            } while (take(','));
            throw new IllegalArgumentException("Arreglo JSON incompleto.");
        }
        String string() {
            index++;
            StringBuilder value = new StringBuilder();
            while (index < source.length()) {
                char ch = source.charAt(index++);
                if (ch == '"') return value.toString();
                if (ch != '\\') { value.append(ch); continue; }
                if (index >= source.length()) break;
                char escaped = source.charAt(index++);
                switch (escaped) {
                    case '"', '\\', '/' -> value.append(escaped);
                    case 'b' -> value.append('\b');
                    case 'f' -> value.append('\f');
                    case 'n' -> value.append('\n');
                    case 'r' -> value.append('\r');
                    case 't' -> value.append('\t');
                    case 'u' -> {
                        if (index + 4 > source.length()) throw new IllegalArgumentException("Escape Unicode incompleto.");
                        value.append((char) Integer.parseInt(source.substring(index, index + 4), 16));
                        index += 4;
                    }
                    default -> throw new IllegalArgumentException("Escape JSON no válido.");
                }
            }
            throw new IllegalArgumentException("Cadena JSON incompleta.");
        }
        Object number() {
            int start = index;
            while (index < source.length() && "-+0123456789.eE".indexOf(source.charAt(index)) >= 0) index++;
            if (start == index) throw new IllegalArgumentException("Valor JSON no válido.");
            String value = source.substring(start, index);
            try { return value.contains(".") || value.contains("e") || value.contains("E") ? Double.valueOf(value) : Long.valueOf(value); }
            catch (NumberFormatException error) { throw new IllegalArgumentException("Número JSON no válido."); }
        }
        Object literal(String text, Object result) {
            if (!source.startsWith(text, index)) throw new IllegalArgumentException("Literal JSON no válido.");
            index += text.length();
            return result;
        }
        boolean take(char expected) {
            if (index < source.length() && source.charAt(index) == expected) { index++; return true; }
            return false;
        }
    }
}
