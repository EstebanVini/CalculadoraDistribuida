import java.net.*;
import java.io.*;
import java.util.*;

public class MOM {
    private List<Integer> puertosDisponibles;
    private List<Socket> middlewareSockets;
    private List<DataOutputStream> middlewareSalidas;
    private ServerSocket servidor;

    private List<Integer> puertosOcupados;

    private  Set<Integer> middlewaresConectados = new HashSet<>();

    public MOM(List<Integer> availablePorts) {
        puertosDisponibles = new ArrayList<>(availablePorts);
        middlewareSockets = new ArrayList<>();
        middlewareSalidas = new ArrayList<>();
        puertosOcupados = new ArrayList<>(availablePorts);
    }

    public void startMiddleware() {
        while (!puertosDisponibles.isEmpty()) {
            int index = new Random().nextInt(puertosDisponibles.size());
            int port = puertosDisponibles.remove(index);

            try {
                servidor = new ServerSocket(port);
                System.out.println("Middleware en puerto " + port + " está corriendo");
                startMiddlewareInstance(servidor);
                establishMiddlewareConnections();
                puertosOcupados.remove(Integer.valueOf(port));
                return; // Exit after successfully starting a middleware
            } catch (IOException e) {
                System.out.println("Error al intentar iniciar en puerto " + port + ": " + e);
            }
        }

        System.out.println("No se pudo iniciar el middleware en ningún puerto disponible.");
    }

    private void startMiddlewareInstance(ServerSocket servidor) {
        List<ManejadorDeClientes> clientes = new ArrayList<>();

        new Thread(() -> {
            try {
                while (true) {
                    Socket socket = servidor.accept();
                    System.out.println("Nuevo Cliente aceptado en puerto " + servidor.getLocalPort());

                    ManejadorDeClientes clientHandler = new ManejadorDeClientes(socket, clientes);
                    clientes.add(clientHandler);

                    Thread thread = new Thread(clientHandler);
                    thread.start();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }).start();
    }

    public static void main(String[] args) {
        List<Integer> availablePorts = new ArrayList<>();
        availablePorts.add(12345);
        availablePorts.add(12346);
        availablePorts.add(12347);

        MOM server = new MOM(availablePorts);

        // Agrega un apagado limpio para manejar la desconexión
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.cleanShutdown();
        }));

        server.startMiddleware();
    }

    private void establishMiddlewareConnections() {
        for (int port : puertosOcupados) {
            if (port != servidor.getLocalPort()) {
                try {
                    Socket socket = new Socket("127.0.0.1", port);
                    middlewareSockets.add(socket);
                    DataOutputStream salidaMiddleware = new DataOutputStream(socket.getOutputStream());
                    middlewareSalidas.add(salidaMiddleware);
                    System.out.println("Conexión establecida con otro Middleware en puerto " + port);

                    // Envía un mensaje para informar al otro middleware de la conexión
                    salidaMiddleware.writeUTF("MIDDLEWARECONNECTED:" + servidor.getLocalPort());

                    // Agrega el puerto del otro middleware a la lista de middlewares conectados
                    middlewaresConectados.add(port);
                } catch (IOException e) {
                    System.out.println("Error al intentar conectar a otro Middleware en puerto " + port + ": " + e);
                }
            }
        }
    }


    // conectarse a un middleware específico
    public void connectToMiddleware(int port) {
        try {
            Socket socket = new Socket("127.0.0.1", port);
            middlewareSockets.add(socket);
            DataOutputStream salidaMiddleware = new DataOutputStream(socket.getOutputStream());
            middlewareSalidas.add(salidaMiddleware);
            System.out.println("Conexión establecida con otro Middleware en puerto " + port);

            // Envía un mensaje para informar al otro middleware de la conexión
            salidaMiddleware.writeUTF("MIDDLEWARECONNECTED:" + servidor.getLocalPort());

            // Agrega el puerto del otro middleware a la lista de middlewares conectados
            middlewaresConectados.add(port);
        } catch (IOException e) {
            System.out.println("Error al intentar conectar a otro Middleware en puerto " + port + ": " + e);
        }
    }

    // Método para eliminar un middleware desconectado de la lista de middlewares conectados
    public void removeDisconnectedMiddleware(int port) {
        middlewaresConectados.remove(port);
        System.out.println("Middleware en puerto " + port + " desconectado.");
    }

    // Realiza un apagado limpio
    public void cleanShutdown() {

        // Informar a otros middlewares sobre la desconexión antes de cerrar la conexión.
        for (int connectedPort : middlewaresConectados) {
            try {
                // Create a socket to the other middleware and send the DISCONNECT message.
                Socket disconnectSocket = new Socket("127.0.0.1", connectedPort);
                DataOutputStream disconnectOutput = new DataOutputStream(disconnectSocket.getOutputStream());
                disconnectOutput.writeUTF("DISCONNECT:" + servidor.getLocalPort());
                disconnectOutput.close();
                disconnectSocket.close();
            } catch (IOException e) {
                System.err.println("Error al enviar mensaje de desconexión a otro middleware: " + e);
            }
        }

        // Cierra otras conexiones y realiza cualquier limpieza necesaria.
        // Luego cierra el servidor y otros recursos.

        try {
            if (servidor != null) {
                servidor.close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar el servidor: " + e);
        }

        // Cierra las conexiones de los middlewares
        for (Socket middlewareSocket : middlewareSockets) {
            try {
                middlewareSocket.close();
            } catch (IOException e) {
                System.err.println("Error al cerrar una conexión de middleware: " + e);
            }
        }
    }

    private class ManejadorDeClientes implements Runnable {
        private Socket socket;
        private List<ManejadorDeClientes> clientes;

        public ManejadorDeClientes(Socket socket, List<ManejadorDeClientes> clientes) {
            this.socket = socket;
            this.clientes = clientes;
        }

        @Override
        public void run() {
            try {
                DataInputStream entrada = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

                String temp = "";
                while (true) {
                    try {
                        temp = entrada.readUTF();
                        System.out.println("Mensaje recibido: " + temp);
                    } catch (IOException e) {
                        // Error al leer desde el cliente, cierra la conexión y elimina el manejador
                        clientes.remove(this);
                        socket.close();
                        return;
                    }

                    if (temp.startsWith("MIDDLEWARECONNECTED")) {
                        String[] parts = temp.split(":");
                        int port = Integer.parseInt(parts[1]);

                        if (!middlewaresConectados.contains(port)) {
                            // Realiza la conexión con ese middleware si no está conectado.
                            connectToMiddleware(port);
                        }

                        // Agrega el puerto del otro middleware a la lista de middlewares conectados
                        middlewaresConectados.add(port);
                        continue;
                    } else if (temp.startsWith("DISCONNECT")) {
                        String[] parts = temp.split(":");
                        int port = Integer.parseInt(parts[1]);
                        // Remove the disconnected middleware's port from the list of connected middlewares.
                        removeDisconnectedMiddleware(port);
                    } else if (temp.startsWith("RESOLVER")) {
                        String[] parts = temp.split(",");
                        int sourcePort = Integer.parseInt(parts[5]);
                        if (sourcePort == socket.getLocalPort()) {
                            // Si el puerto de origen es el mismo que el del middleware actual, reenvía el mensaje a todos los middlewares conectados
                            for (DataOutputStream salidaMiddleware : middlewareSalidas) {
                                try {
                                    salidaMiddleware.writeUTF(temp);
                                    salidaMiddleware.flush(); // Asegura que los datos se envíen de inmediato
                                } catch (IOException e) {
                                    // Manejar la excepción si falla el envío a un middleware.
                                    System.err.println("Error al enviar mensaje a un middleware: " + e);
                                }
                            }
                        }
                    } else if (temp.startsWith("MOSTRAR")) {
                        String[] parts = temp.split(",");
                        int sourcePort = Integer.parseInt(parts[6]);
                        if (sourcePort == socket.getLocalPort()) {
                            // Si el puerto de origen es el mismo que el del middleware actual, reenvía el mensaje a todos los middlewares conectados
                            for (DataOutputStream salidaMiddleware : middlewareSalidas) {
                                try {
                                    salidaMiddleware.writeUTF(temp);
                                    salidaMiddleware.flush(); // Asegura que los datos se envíen de inmediato
                                } catch (IOException e) {
                                    // Manejar la excepción si falla el envío a un middleware.
                                    System.err.println("Error al enviar mensaje a un middleware: " + e);
                                }
                            }
                        }
                    }
                    for (ManejadorDeClientes client : clientes) {
                        if (client != this) {
                            try {
                                DataOutputStream clientSalida = new DataOutputStream(client.socket.getOutputStream());
                                clientSalida.writeUTF(temp);
                                clientSalida.flush();
                            } catch (IOException e) {
                                System.err.println("Error al enviar mensaje a un cliente: " + e);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error de E/S en el manejador de clientes: " + e);
            }
        }
    }
}
