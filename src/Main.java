public class Main {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("API_PORT", "8080"));
        var server = com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress(port), 0);

        server.createContext("/health", exchange -> {
            byte[] resp = "OK".getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(200, resp.length);
            try (var os = exchange.getResponseBody()) { os.write(resp); }
        });

        server.createContext("/version", exchange -> {
            String body = "EmotionalBook API (demo)";
            byte[] resp = body.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(200, resp.length);
            try (var os = exchange.getResponseBody()) { os.write(resp); }
        });

        server.createContext("/time", exchange -> {
            String body = java.time.ZonedDateTime.now().toString();
            byte[] resp = body.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(200, resp.length);
            try (var os = exchange.getResponseBody()) { os.write(resp); }
        });

        // Endpoint: test de connectivité DB
        server.createContext("/db/ping", exchange -> {
            try (var conn = Db.connect(); var st = conn.createStatement(); var rs = st.executeQuery("SELECT 1")) {
                String body = rs.next() ? "DB_OK" : "DB_FAIL";
                byte[] resp = body.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
                exchange.sendResponseHeaders(200, resp.length);
                try (var os = exchange.getResponseBody()) { os.write(resp); }
            } catch (Exception e) {
                String body = "DB_ERROR: " + e.getMessage();
                byte[] resp = body.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
                exchange.sendResponseHeaders(500, resp.length);
                try (var os = exchange.getResponseBody()) { os.write(resp); }
            }
        });

        // Endpoint: liste des émotions (optionnel ?level=primaire|secondaire|tertiaire)
        server.createContext("/emotions", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            String level = null;
            if (query != null) {
                for (String p : query.split("&")) {
                    String[] kv = p.split("=", 2);
                    if (kv.length == 2 && kv[0].equals("level")) {
                        level = java.net.URLDecoder.decode(kv[1], java.nio.charset.StandardCharsets.UTF_8);
                    }
                }
            }
            String sql = "SELECT id,name,level FROM emotion_taxonomy" + (level != null ? " WHERE level=?" : "") + " ORDER BY level,name";
            try (var conn = Db.connect(); var ps = conn.prepareStatement(sql)) {
                if (level != null) ps.setString(1, level);
                try (var rs = ps.executeQuery()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("[");
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) sb.append(',');
                        first = false;
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String lvl = rs.getString("level");
                        sb.append('{')
                          .append("\"id\":").append(id).append(',')
                          .append("\"name\":\"").append(escapeJson(name)).append("\",")
                          .append("\"level\":\"").append(escapeJson(lvl)).append("\"")
                          .append('}');
                    }
                    sb.append("]");
                    byte[] resp = sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
                    exchange.sendResponseHeaders(200, resp.length);
                    try (var os = exchange.getResponseBody()) { os.write(resp); }
                }
            } catch (Exception e) {
                String body = "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}";
                byte[] resp = body.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
                exchange.sendResponseHeaders(500, resp.length);
                try (var os = exchange.getResponseBody()) { os.write(resp); }
            }
        });

        server.setExecutor(java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor());
        server.start();
        System.out.println("API démarrée sur http://0.0.0.0:" + port + " (endpoints: /health, /version, /time, /db/ping, /emotions?level=...)");
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int)c)); else sb.append(c);
            }
        }
        return sb.toString();
    }
}