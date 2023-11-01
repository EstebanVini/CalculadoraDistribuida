import java.net.*;
import java.io.*;
import java.util.*;

public class MOM {
    private List<Integer> puertosDisponibles;
    private List<Socket> middlewareSockets;
    private List<DataOutputStream> middlewareSalidas;
    private ServerSocket servidor;

    private List<Integer> puertosOcupados;

    private static Set<Integer> middlewaresConectados = new HashSet<>();

    public MOM(List<Integer> availablePorts) {
        puertosDisponibles = new ArrayList<>(availablePorts);
        middlewareSockets = new ArrayList<>();
        middlewareSalidas = new ArrayList<>();
        middlewaresConectados = new HashSet<>();
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

    public static Set<Integer> getMiddlewaresConectados() {
        return middlewaresConectados;
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
                        System.err.println("Error al leer mensaje del cliente: " + e);
                        clientes.remove(this);
                        socket.close();
                        return;
                    }

                    // Si el mensaje empieza con "MIDDLEWARECONNECTED", revisa si el puerto del middleware ya está en la lista de middlewares conectados, si no, realiza la conexión con ese middleware
                    if (temp.startsWith("MIDDLEWARECONNECTED")) {
                        String[] parts = temp.split(":");
                        int port = Integer.parseInt(parts[1]);
                        if (!middlewaresConectados.contains(port)) {
                            connectToMiddleware(port);
                        }
                        // Agrega el puerto del otro middleware a la lista de middlewares conectados
                        middlewaresConectados.add(port);
                        continue;
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
                                clientSalida.flush(); // Asegura que los datos se envíen de inmediato
                            } catch (IOException e) {
                                // Manejar la excepción si falla el envío a un cliente.
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