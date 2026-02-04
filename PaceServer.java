import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PaceServer {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8020), 0);

        server.createContext("/", PaceServer::handleIndex);
        server.createContext("/pace", PaceServer::handlePace);
        server.createContext("/kmh", PaceServer::handleKmh);

        server.setExecutor(null);
        server.start();
        System.out.println("Server kjorer paa http://<pi-ip>:8020");
    }

    private static void handleIndex(HttpExchange ex) throws IOException {
        // Serve bare "/" som index, alt annet 404
        if (!ex.getRequestURI().getPath().equals("/")) {
            send(ex, "Not found", 404, "text/plain; charset=utf-8");
            return;
        }

        byte[] body = Files.readAllBytes(Path.of("index.html"));
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        ex.sendResponseHeaders(200, body.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(body);
        }
    }

    private static void handlePace(HttpExchange ex) throws IOException {
        Map<String, String> q = query(ex.getRequestURI());
        String pace = q.get("value");

        String response;
        try {
            if (pace == null) throw new IllegalArgumentException("missing value");
            double kmh = PaceConverter.paceToKmh(pace);
            response = String.format(java.util.Locale.US, "%.2f", kmh);
            send(ex, response, 200, "text/plain; charset=utf-8");
        } catch (Exception e) {
            send(ex, "error", 400, "text/plain; charset=utf-8");
        }
    }

    private static void handleKmh(HttpExchange ex) throws IOException {
        Map<String, String> q = query(ex.getRequestURI());
        String kmh = q.get("value");

        try {
            if (kmh == null) throw new IllegalArgumentException("missing value");
            String response = PaceConverter.kmhToPace(kmh);
            send(ex, response, 200, "text/plain; charset=utf-8");
        } catch (Exception e) {
            send(ex, "error", 400, "text/plain; charset=utf-8");
        }
    }

    private static void send(HttpExchange ex, String body, int status, String contentType) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", contentType);
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static Map<String, String> query(URI uri) {
        Map<String, String> map = new HashMap<>();
        String raw = uri.getRawQuery(); // beholder %XX encoding
        if (raw == null) return map;

        for (String p : raw.split("&")) {
            String[] kv = p.split("=", 2);
            if (kv.length == 2) {
                String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                String val = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                map.put(key, val);
            }
        }
        return map;
    }
}
