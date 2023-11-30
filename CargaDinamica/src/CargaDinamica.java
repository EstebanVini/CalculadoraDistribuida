import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class CargaDinamica {

    private static int getRandomPort(List<Integer> availablePorts) {
        if (!availablePorts.isEmpty()) {
            int randomIndex = new Random().nextInt(availablePorts.size());
            int serverPort = availablePorts.get(randomIndex);
            return serverPort;
        }
        return -1; // No hay puertos disponibles
    }

    public static void main(String[] args) {
        try {
            List<Integer> availablePorts = new ArrayList<>();
            availablePorts.add(12345);
            availablePorts.add(12346);
            availablePorts.add(12347);

            int serverPort = getRandomPort(availablePorts);
            Socket socket = null;



            while (socket == null) {
                serverPort = getRandomPort(availablePorts);
                try {
                    socket = new Socket("127.0.0.1", serverPort);
                } catch (Exception e) {
                    System.out.println("No se pudo conectar al middleware en el puerto " + serverPort + ". Reintentando...");
                }
            }

            System.out.println("Conexión establecida con middleware en el puerto " + serverPort);

            DataInputStream entrada = new DataInputStream(socket.getInputStream());
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());


            // Enviar mensajes desde la consola al servidor
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Ingreser Huella Servidor: ");

                String inputConsole = scanner.nextLine();

                // si el imput es de exactamente 64 caracteres, se guarda en una variable
                if (inputConsole.length() == 64) {
                    String huella = inputConsole;
                    System.out.println("Seleccionar un tipo de operación: \n");
                    System.out.println("1. SUMA \n");
                    System.out.println("2. RESTA \n");
                    System.out.println("3. MULTIPLICACION \n");
                    System.out.println("4. DIVISION \n");
                    System.out.print("Ingresar Seleccion: ");
                    String tipo = scanner.nextLine();
                    switch (tipo) {
                        case "1":
                            tipo = "SUMA";
                            break;
                        case "2":
                            tipo = "RESTA";
                            break;
                        case "3":
                            tipo = "MULTIPLICACION";
                            break;
                        case "4":
                            tipo = "DIVISION";
                            break;
                        default:
                            tipo = "SUMA";
                            break;
                    }


                    String mensaje = "Cambio de tipo de operacion," + tipo + "," + huella + "," + Integer.toString(serverPort);
                    salida.writeUTF(mensaje);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
