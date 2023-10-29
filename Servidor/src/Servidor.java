import java.io.*;
import java.net.*;
import java.util.Objects;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class Servidor {
    private Socket socket;
    private DataInputStream entrada;
    private DataOutputStream salida;
    private int resultado;
    private List<Integer> middlewarePorts;

    public Servidor(List<Integer> middlewarePorts) {
        this.middlewarePorts = middlewarePorts;
        try {
            conectarMiddleware();
            Thread thread = new Thread(new Mensajes());
            thread.start();
        } catch (IOException error) {
            System.out.println(error);
        }
    }

    private void conectarMiddleware() throws IOException {
        // Mezcla aleatoriamente los puertos de middleware disponibles
        List<Integer> puertosAleatorios = new ArrayList<>(middlewarePorts);
        Random random = new Random();
        for (int i = puertosAleatorios.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = puertosAleatorios.get(i);
            puertosAleatorios.set(i, puertosAleatorios.get(j));
            puertosAleatorios.set(j, temp);
        }

        for (int port : puertosAleatorios) {
            try {
                socket = new Socket("127.0.0.1", port);
                salida = new DataOutputStream(socket.getOutputStream());
                break; // Si se logra la conexión, sal del bucle
            } catch (IOException e) {
                System.out.println("Middleware en puerto " + port + " no disponible.");
            }
        }

        if (socket == null) {
            throw new IOException("No se pudo conectar a ningún middleware.");
        }
    }

    public static void main(String[] args) {
        List<Integer> middlewarePorts = new ArrayList<>();
        middlewarePorts.add(12346);
        middlewarePorts.add(12347);
        // Agrega más puertos de middleware según sea necesario

        new Servidor(middlewarePorts);
    }

    private class Mensajes implements Runnable {
        @Override
        public void run() {
            try {
                DataInputStream entrada = new DataInputStream(socket.getInputStream());

                while (true) {
                    String mensaje = entrada.readUTF();
                    String[] paquete = mensaje.split(",");
                    int result = 0;
                    if (paquete[0].startsWith("RESOLVER")) {
                        if (Objects.equals(paquete[2], "+")) {
                            result = Integer.parseInt(paquete[1]) + Integer.parseInt(paquete[3]);
                        } else if (Objects.equals(paquete[2], "-")) {
                            result = Integer.parseInt(paquete[1]) - Integer.parseInt(paquete[3]);
                        } else if (Objects.equals(paquete[2], "*")) {
                            result = Integer.parseInt(paquete[1]) * Integer.parseInt(paquete[3]);
                        } else if (Objects.equals(paquete[2], "/")) {
                            result = Integer.parseInt(paquete[1]) / Integer.parseInt(paquete[3]);
                        }

                        String respuesta = "MOSTRAR," + paquete[1] + "," + paquete[2] + "," + paquete[3] + "," + result;

                        salida.writeUTF(respuesta);
                    }
                }
            } catch (IOException error) {
                System.out.println(error);
            }
        }
    }
}
