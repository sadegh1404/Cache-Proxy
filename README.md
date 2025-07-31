# Simple Java HTTP/HTTPS Proxy Server

This project implements a basic multi-threaded proxy server in Java that can handle both HTTP and HTTPS requests. It acts as an intermediary, forwarding client requests to the appropriate destination servers and relaying the responses back to the clients.

## Features

*   **HTTP Proxying**: For standard HTTP requests, the server parses the request, connects to the target web server, forwards the request, and streams the response back to the client.
*   **HTTPS Tunneling (CONNECT Method)**: For HTTPS requests, which typically use the `CONNECT` method, the proxy establishes a direct TCP tunnel between the client and the target HTTPS server. This allows encrypted data to flow directly between the client and the server without the proxy decrypting it.
*   **Multi-threaded**: Each incoming client connection is handled in a separate thread, allowing the proxy to serve multiple clients concurrently.
*   **Basic Error Handling**: Includes basic error responses for connection issues (e.g., 500 Internal Server Error for HTTP, 502 Bad Gateway for HTTPS).

## How it Works

The proxy operates by listening for incoming client connections on a specified port.

1.  **Connection Acceptance**: When a client connects, a new thread is spawned to handle that client's request.
2.  **Request Type Detection**: The first line of the client's request is read to determine if it's a standard HTTP `GET`, `POST`, etc., or an HTTPS `CONNECT` request.
3.  **HTTP Request Handling**:
    *   The proxy parses the host and path from the HTTP request line.
    *   It establishes a new socket connection to the target web server on port 80.
    *   The entire client request (including headers) is forwarded to the target server.
    *   The response from the target server is then read and streamed back to the client.
4.  **HTTPS Request Handling (CONNECT Method)**:
    *   When a `CONNECT` request is received, the proxy extracts the target host and port (typically 443 for HTTPS).
    *   It then attempts to establish a direct socket connection to the target HTTPS server.
    *   If the connection to the target server is successful, the proxy sends a "HTTP/1.1 200 Connection Established" response to the client. This signals to the client that a tunnel has been created.
    *   Two new threads are then started: one to continuously stream data from the client to the target server, and another to stream data from the target server back to the client. This creates a bidirectional tunnel for encrypted communication. The proxy does not interpret or decrypt the data in this tunnel.

## Prerequisites

*   Java Development Kit (JDK) 8 or higher.

## Building and Running

1.  **Save the files**: Save the provided code as `Main.java` and `HttpsSocket.java` in a directory named `org/sadegh` (to match the package declaration).
2.  **Compile**: Open a terminal or command prompt, navigate to the directory *containing* the `org` folder, and compile the Java files:
    ```bash
    javac org/sadegh/*.java
    ```
3.  **Run**: After successful compilation, run the main class:
    ```bash
    java org.sadegh.Main
    ```
    You should see the output: `Server started on port 8000`.

## Usage

Once the proxy server is running, you need to configure your web browser or application to use it.

**Proxy Settings:**
*   **Proxy Address/Host**: `localhost` or `127.0.0.1`
*   **Proxy Port**: `8000`

### Example Configuration (for Browsers like Chrome/Firefox):

Most browsers use system proxy settings or have their own configuration.

1.  **Windows**:
    *   Go to `Settings` -> `Network & Internet` -> `Proxy`.
    *   Under "Manual proxy setup", turn on "Use a proxy server".
    *   Enter `127.0.0.1` for "Address" and `8000` for "Port".
    *   Save the settings.
2.  **macOS**:
    *   Go to `System Settings` -> `Network`.
    *   Select your active network connection (e.g., Wi-Fi, Ethernet) and click `Details...` or `Advanced...`.
    *   Go to the `Proxies` tab.
    *   Check `Web Proxy (HTTP)` and `Secure Web Proxy (HTTPS)`.
    *   Enter `127.0.0.1` and port `8000` for both.
    *   Click `OK` or `Apply`.
3.  **Linux (GNOME/KDE)**: Proxy settings are usually found in your system's network settings.
    *   Look for "Network Proxy" or "Proxy Settings".
    *   Set "Manual" proxy configuration and enter `127.0.0.1` and port `8000` for both HTTP and HTTPS proxies.
4.  **Curl (Command Line)**:
    ```bash
    curl -x http://localhost:8000 http://example.com
    curl -x http://localhost:8000 https://www.google.com
    ```

After configuring your client, try accessing a website. You should see output in your proxy server's console indicating the connections being handled.

## Limitations

*   **Basic HTTP Parsing**: The HTTP parsing is minimal (only the first line and simple header forwarding). It does not fully support all HTTP/1.1 features like chunked encoding, pipelining, or complex header manipulations.
*   **No Caching or Filtering**: This proxy does not implement any caching mechanisms or content filtering capabilities.
*   **No Authentication**: There is no support for proxy authentication.
*   **Limited Error Handling**: While basic error messages are provided, more robust error handling and logging could be implemented.
*   **Concurrency Issues**: Although multi-threaded, extreme loads or specific network conditions might expose concurrency issues not fully addressed.
*   **No GZIP/Deflate Decompression**: The proxy simply streams raw bytes, it doesn't decompress or re-compress content.