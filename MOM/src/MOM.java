import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

public class MOM {
    private List<Integer> puertosDisponibles;
    private Set<String> mensajesConocidos;

    public MOM(List<Integer> availablePorts) {
        puertosDisponibles = new ArrayList<>(availablePorts);
        mensajesConocidos = new HashSet<>();
    }

    public void startMiddleware() {
        while (!puertosDisponibles.isEmpty()) {
            int index = new Random().nextInt(puertosDisponibles.size());
            int port = puertosDisponibles.remove(index);

            try {
                ServerSocket servidor = new ServerSocket(port);
                System.out.println("Middleware en puerto " + port + " está corriendo");
                startMiddlewareInstance(servidor);
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
                    System.out.println("Nuevo Nodo aceptado en puerto " + servidor.getLocalPort());

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
        availablePorts.add(12347);// Agrega los puertos disponibles aquí

        MOM server = new MOM(availablePorts);
        server.startMiddleware();
    }

    private class ManejadorDeClientes implements Runnable {
        private Socket socket;
        private DataInputStream entrada;
        private DataOutputStream salida;
        private List<ManejadorDeClientes> clientes;

        public ManejadorDeClientes(Socket socket, List<ManejadorDeClientes> clientes) {
            this.socket = socket;
            this.clientes = clientes;
        }

        @Override
        public void run() {
            try {
                entrada = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                salida = new DataOutputStream(socket.getOutputStream());

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
                                    client.salida.writeUTF(temp);
                                } catch (IOException e) {
                                    // Manejar la excepción si falla el envío a un cliente.
                                    e.printStackTrace();
                                }
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
