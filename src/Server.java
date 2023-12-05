import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple HTTP Server running on the local network.
 */
public class Server
{
    public static void main(String[] args)
    {
        Server server = new Server();
        try
        {
            server.run(args);
        } catch (IllegalArgumentException e)
        {
            System.out.println(e);
        }
    }

    /**
     * Start, set up, and run the server.
     *
     * @param args the command line arguments
     * @throws IllegalArgumentException if too few or too many arguments are provided
     */
    public void run(String[] args) throws IllegalArgumentException
    {
        if (args.length != 1)
        {
            throw new IllegalArgumentException("Must include Server IP address as the first argument.");
        }

        try
        {
            Map<String, HttpHandler> routingMap = setupRoutingMap();
            HttpServer http = HttpServer.create(new InetSocketAddress(InetAddress.getByName(args[0]), 8080), 0);
            routingMap.forEach(http::createContext);
            http.setExecutor(null);
            http.start();
            System.out.printf("Server running on http://%s:8080", args[0]);
        } catch (IOException e)
        {
            System.out.println(e);
        }
    }

    /**
     * Set up the routing map for the HTTP Server.
     *
     * @return the routing map for the HTTP Server.
     */
    private Map<String, HttpHandler> setupRoutingMap() throws IOException
    {
        Map<String, HttpHandler> routingMap = new HashMap<>();

        byte[] rootBytes = Files.readAllBytes(Paths.get("../public/index.html"));
        routingMap.put("/", new BasicHandler(200, rootBytes));

        return routingMap;
    }

    /**
     * A basic and generic handler for HTTP requests.
     */
    static class BasicHandler implements HttpHandler
    {
        /**
         * The response code for the HTTP response.
         */
        private final int rCode;

        /**
         * The response body for the HTTP response.
         */
        private final byte[] resBody;

        /**
         * Create a BasicHandler.
         *
         * @param rCode   the HTTP response code
         * @param resBody the HTTP response body
         */
        public BasicHandler(int rCode, byte[] resBody)
        {
            this.rCode = rCode;
            this.resBody = resBody;
        }

        /**
         * Handle the HTTP response.
         *
         * @param ex the exchange containing the request from the
         *           client and used to send the response
         */
        @Override
        public void handle(HttpExchange ex)
        {
            try
            {
                ex.sendResponseHeaders(rCode, resBody.length);
                OutputStream out = ex.getResponseBody();
                out.write(resBody);
                out.close();
            } catch (IOException e)
            {
                System.out.println(e);
            }
        }
    }
}