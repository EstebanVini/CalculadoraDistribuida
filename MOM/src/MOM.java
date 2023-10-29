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

    private boolean isMiddleware(Socket socket) {
        try {
            // Configura un mensaje de autenticación específico para middlewares
            String mensajeAutenticacion = "AUTENTICACION_MOM";

            // Abre flujos de entrada y salida para el socket
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
            DataInputStream entrada = new DataInputStream(socket.getInputStream());

            // Envía el mensaje de autenticación al nuevo socket
            salida.writeUTF(mensajeAutenticacion);

            // Espera una respuesta del nuevo socket
            String respuesta = entrada.readUTF();

            // Comprueba si la respuesta coincide con un mensaje de autenticación esperado
            if (respuesta.equals(mensajeAutenticacion)) {
                return true; // El socket conectado es otro middleware
            }
        } catch (IOException e) {
            // Manejar cualquier error de E/S que ocurra al intentar autenticar
            e.printStackTrace();
        }

        return false; // El socket conectado no es otro middleware
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
                    temp = entrada.readUTF();

                    // Verifica si el mensaje ya se conoce y evita su reenvío
                    if (!mensajesConocidos.contains(temp)) {
                        mensajesConocidos.add(temp);

                        // Enviar el mensaje a todos los demás clientes y middlewares.
                        for (ManejadorDeClientes client : clientes) {
                            if (client != this) {
                                try {
                                    DataOutputStream clientSalida = new DataOutputStream(client.socket.getOutputStream());
                                    clientSalida.writeUTF(temp);
                                } catch (IOException e) {
                                    // Manejar la excepción si falla el envío a un cliente.
                                    e.printStackTrace();
                                }
                            }
                        }

                        // Enviar el mensaje a todos los middlewares
                        for (DataOutputStream salidaMiddleware : middlewareSalidas) {
                            try {
                                salidaMiddleware.writeUTF(temp);
                            } catch (IOException e) {
                                // Manejar la excepción si falla el envío a un middleware.
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}
