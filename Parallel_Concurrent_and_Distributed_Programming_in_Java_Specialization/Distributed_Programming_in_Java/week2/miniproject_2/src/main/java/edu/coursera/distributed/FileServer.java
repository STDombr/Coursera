package edu.coursera.distributed;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A basic and very limited implementation of a file server that responds to GET
 * requests from HTTP clients.
 */
public final class FileServer {
    /**
     * Main entrypoint for the basic file server.
     *
     * @param socket Provided socket to accept connections on.
     * @param fs A proxy filesystem to serve files from. See the PCDPFilesystem
     *           class for more detailed documentation of its usage.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    public void run(final ServerSocket socket, final PCDPFilesystem fs)
            throws IOException {
        /*
         * Enter a spin loop for handling client requests to the provided
         * ServerSocket object.
         */
        while (true) {
            Socket sc = socket.accept();

            Thread thread = new Thread(() -> {
                try {
                    BufferedReader buffered = new BufferedReader(new InputStreamReader(sc.getInputStream()));

                    String line = buffered.readLine();
                    //assert line != null;
                    //assert line.startsWith("GET");

                    PrintWriter printer = new PrintWriter(sc.getOutputStream());

                    PCDPPath pcdpPath = new PCDPPath(line.split(" ")[1]);
                    String content = fs.readFile(pcdpPath);

                    if (content == null) {
                        printer.write("HTTP/1.0 404 Not Found\r\n");
                        printer.write("Server: FileServer\r\n");
                        printer.write("\r\n");
                    } else {
                        printer.write("HTTP/1.0 200 OK\r\n");
                        printer.write("Server: FileServer\r\n");
                        printer.write("\r\n");
                        printer.write(content);
                    }

                    printer.close();
                }catch (IOException E){}
            });

            thread.start();

        }
    }
}
