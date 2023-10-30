import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MOM {
    private List<Integer> availablePorts;
    private List<Socket> middlewareSockets;
    private List<DataOutputStream> middlewareSalidas;
    private ServerSocket servidor;
    private Set<String> mensajesConocidos;

    public MOM(List<Integer> availablePorts) {
        this.availablePorts = availablePorts;
        middlewareSockets = new ArrayList<>();
        middlewareSalidas = new ArrayList<>();
        mensajesConocidos = new HashSet<>();
    }

    public void startMiddleware() {
        while (!availablePorts.isEmpty()) {
            int index = new Random().nextInt(availablePorts.size());
            int port = availablePorts.remove(index);

            try {
                servidor = new ServerSocket(port);
                System.out.println("Middleware en puerto " + port + " está corriendo");
                startMiddlewareInstance(servidor, port);
                return; // Exit after successfully starting a middleware
            } catch (IOException e) {
                System.out.println("Error al intentar iniciar en puerto " + port + ": " + e);
            }
        }

        System.out.println("No se pudo iniciar el middleware en ningún puerto disponible.");
    }

    private void startMiddlewareInstance(ServerSocket servidor, int myPort) {
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

                    // Establecer una conexión bidireccional con el middleware anterior en el anillo
                    int previousPort = myPort - 1;
                    if (previousPort <= 0) {
                        previousPort += availablePorts.size();
                    }
                    connectToPreviousMiddleware(previousPort);
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }).start();
    }

    private void connectToPreviousMiddleware(int previousPort) {
        if (middlewareSockets.size() < 2) {
            try {
                Socket socket = new Socket("127.0.0.1", previousPort);
                middlewareSockets.add(socket);
                DataOutputStream salidaMiddleware = new DataOutputStream(socket.getOutputStream());
                middlewareSalidas.add(salidaMiddleware);
                System.out.println("Conexión establecida con otro Middleware en puerto " + previousPort);
            } catch (IOException e) {
                System.out.println("Error al intentar conectar al Middleware en puerto " + previousPort + ": " + e);
            }
        }
    }

    public static void main(String[] args) {
        List<Integer> availablePorts = new ArrayList<>();
        availablePorts.add(12345);
        availablePorts.add(12346);
        availablePorts.add(12347);// Agrega los puertos disponibles aquí

        MOM server = new MOM(availablePorts);
        server.startMiddleware();
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
                DataInputStream entrada = new DataInputStream(socket.getInputStream());

                while (true) {
                    String mensaje = entrada.readUTF();

                    // Verifica si el mensaje ya se conoce y evita su reenvío
                    if (!mensajesConocidos.contains(mensaje)) {
                        mensajesConocidos.add(mensaje);

                        // Enviar el mensaje a todos los demás clientes y middlewares.
                        for (ManejadorDeClientes client : clientes) {
                            if (client != this) {
                                try {
                                    DataOutputStream clientSalida = new DataOutputStream(client.socket.getOutputStream());
                                    clientSalida.writeUTF(mensaje);
                                } catch (IOException e) {
                                    // Manejar la excepción si falla el envío a un cliente.
                                    System.out.println("Error al enviar a un cliente: " + e);
                                }
                            }
                        }

                        // Enviar el mensaje a todos los middlewares
                        for (DataOutputStream salidaMiddleware : middlewareSalidas) {
                            try {
                                salidaMiddleware.writeUTF(mensaje);
                            } catch (IOException e) {
                                // Manejar la excepción si falla el envío a un middleware.
                                System.out.println("Error al enviar a un middleware: " + e);
                            }
                        }
                    }
                }
            } catch (IOException error) {
                System.out.println("Error de comunicación con el cliente: " + error);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error al cerrar el socket: " + e);
                }
            }
        }
    }
}
