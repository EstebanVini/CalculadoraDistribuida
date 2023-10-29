import java.io.*;
import java.net.*;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Servidor {
    Socket socket;
    DataInputStream entrada;
    DataOutputStream salida;

    int resultado;

    public Servidor(List<Integer> availablePorts) {
        while (true) {
            int serverPort = getRandomPort(availablePorts);
            if (serverPort == -1) {
                System.out.println("No hay puertos disponibles para conectarse.");
                break;
            }

            try {
                socket = new Socket("127.0.0.1", serverPort);

                entrada = new DataInputStream(System.in);
                salida = new DataOutputStream(socket.getOutputStream());

                Thread thread = new Thread(new Mensajes());
                thread.start();

                System.out.println("Conexión establecida con middleware en el puerto " + serverPort);
                // Esperar a que la conexión se cierre antes de intentar una nueva conexión
                thread.join();
            } catch (IOException | InterruptedException error) {
                System.out.println("No se pudo conectar al middleware en el puerto " + serverPort + ". Reintentando...");
            }
        }
    }

    private int getRandomPort(List<Integer> availablePorts) {
        if (!availablePorts.isEmpty()) {
            int randomIndex = new Random().nextInt(availablePorts.size());
            int serverPort = availablePorts.get(randomIndex);
            availablePorts.remove(randomIndex); // Elimina el puerto usado
            return serverPort;
        }
        return -1; // No hay puertos disponibles
    }

    public static void main(String[] args) {
        List<Integer> availablePorts = new ArrayList<>();
        availablePorts.add(12345);
        availablePorts.add(12346);
        availablePorts.add(12347);// Agrega los puertos disponibles aquí

        new Servidor(availablePorts);
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
                        }

                        if (Objects.equals(paquete[2], "-")) {
                            result = Integer.parseInt(paquete[1]) - Integer.parseInt(paquete[3]);
                        }

                        if (Objects.equals(paquete[2], "*")) {
                            result = Integer.parseInt(paquete[1]) * Integer.parseInt(paquete[3]);
                        }

                        if (Objects.equals(paquete[2], "/")) {
                            result = Integer.parseInt(paquete[1]) / Integer.parseInt(paquete[3]);
                        }

                        String respuesta = "MOSTRAR," + paquete[1] + "," + paquete[2] + "," + paquete[3] + "," + result;

                        salida.writeUTF(respuesta);
                    }
                }
            } catch (IOException error) {
                System.out.println("La conexión con el middleware se ha cerrado.");
            }
        }
    }
}
