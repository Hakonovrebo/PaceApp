import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class PaceServer {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8020), 0);

        server.createContext("/pace", PaceServer::handlePace);
        server.createContext("/kmh", PaceServer::handleKmh);

        server.setExecutor(null);
        server.start();
        System.out.println("Server kjorer paa http://<pi-ip>:8020");
    }

    private static void handlePace(HttpExchange ex) throws IOException {
        Map<String, String> q = query(ex.getRequestURI());
        String pace = q.get("value");

        String response;
        try {
            double kmh = PaceConverter.paceToKmh(pace);
            response = String.format("%.2f", kmh);
        } catch (Exception e) {
            response = "error";
        }

        send(ex, response);
    }

    private static void handleKmh(HttpExchange ex) throws IOException {
        Map<String, String> q = query(ex.getRequestURI());
        String kmh = q.get("value");

        String response;
        try {
            response = PaceConverter.kmhToPace(kmh);
        } catch (Exception e) {
            response = "error";
        }

        send(ex, response);
    }

    private static void send(HttpExchange ex, String body) throws IOException {
        ex.sendResponseHeaders(200, body.length());
        try (OutputStream os = ex.getResponseBody()) {
            os.write(body.getBytes());
        }
    }

    private static Map<String, String> query(URI uri) {
        Map<String, String> map = new HashMap<>();
        if (uri.getQuery() == null) return map;

        for (String p : uri.getQuery().split("&")) {
            String[] kv = p.split("=");
            if (kv.length == 2) map.put(kv[0], kv[1]);
        }
        return map;
    }
}

