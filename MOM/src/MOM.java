import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;

public class MOM {
    private List<Integer> puertosDisponibles;
    private List<Socket> middlewareSockets;
    private List<DataOutputStream> middlewareSalidas;
    private ServerSocket servidor;
    private Set<String> mensajesConocidos;

    public MOM(List<Integer> availablePorts) {
        puertosDisponibles = new ArrayList<>(availablePorts);
        middlewareSockets = new ArrayList<>();
        middlewareSalidas = new ArrayList<>();
        mensajesConocidos = new HashSet<>();
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
                    System.out.println("Nuevo Middleware aceptado en puerto " + servidor.getLocalPort());

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
        availablePorts.add(12347); // Agrega los puertos disponibles aquí

        MOM server = new MOM(availablePorts);
        server.startMiddleware();
    }

    private void establishMiddlewareConnections() {
        for (int port : puertosDisponibles) {
            if (port != servidor.getLocalPort()) {
                try {
                    Socket socket = new Socket("127.0.0.1", port);
                    middlewareSockets.add(socket);
                    DataOutputStream salidaMiddleware = new DataOutputStream(socket.getOutputStream());
                    middlewareSalidas.add(salidaMiddleware);
                    System.out.println("Conexión establecida con otro Middleware en puerto " + port);
                } catch (IOException e) {
                    System.out.println("Error al intentar conectar a otro Middleware en puerto " + port + ": " + e);
                }
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
                    } catch (IOException e) {
                        // Error al leer desde el cliente, cierra la conexión y elimina el manejador
                        System.err.println("Error al leer mensaje del cliente: " + e);
                        clientes.remove(this);
                        socket.close();
                        return;
                    }

                    // Verifica si el mensaje ya se conoce y evita su reenvío
                    if (!mensajesConocidos.contains(temp)) {
                        mensajesConocidos.add(temp);

                        // Enviar el mensaje a todos los demás clientes y middlewares.
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

                        // Enviar el mensaje a todos los middlewares
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
            } catch (IOException e) {
                System.err.println("Error de E/S en el manejador de clientes: " + e);
            }
        }
    }
}
