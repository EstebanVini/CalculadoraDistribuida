import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class CargaDinamica {

    public static void main(String[] args) {
        try {
            int puerto = 12347;
            Socket socket = new Socket("127.0.0.1", puerto);
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


                    String mensaje = "Cambio de tipo de operacion," + tipo + "," + huella + "," + Integer.toString(puerto);
                    salida.writeUTF(mensaje);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
