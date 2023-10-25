import java.io.*;
import java.net.*;
import java.util.Objects;

public class Servidor {
    Socket socket;
    DataInputStream entrada;
    DataOutputStream salida;

    int resultado;


    public Servidor(String serverName, int serverPort) {
        try {
            socket = new Socket(serverName, serverPort);

            entrada = new DataInputStream(System.in);
            salida = new DataOutputStream(socket.getOutputStream());


            Thread thread = new Thread(new Mensajes());
            thread.start();

        } catch (IOException error) {
            System.out.println(error);
        }
    }

    public static void main(String[] args) {
        new Servidor("127.0.0.1", 12345);
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
                    if(paquete[0].startsWith("RESOLVER"))
                    {

                        if(Objects.equals(paquete[2], "+"))
                        {
                            result = Integer.parseInt(paquete[1]) + Integer.parseInt(paquete[3]);
                        }

                        if(Objects.equals(paquete[2], "-"))
                        {
                            result = Integer.parseInt(paquete[1]) - Integer.parseInt(paquete[3]);
                        }

                        if(Objects.equals(paquete[2], "*"))
                        {
                            result = Integer.parseInt(paquete[1])  * Integer.parseInt(paquete[3]);
                        }

                        if(Objects.equals(paquete[2], "/"))
                        {
                            result = Integer.parseInt(paquete[1]) / Integer.parseInt(paquete[3]);
                        }

                        String respuesta = "MOSTRAR,"+paquete[1]+","+paquete[2]+","+paquete[3]+","+result;


                        salida.writeUTF(respuesta);


                    }
                }
            } catch (IOException error) {
                System.out.println(error);
            }
        }
    }
}