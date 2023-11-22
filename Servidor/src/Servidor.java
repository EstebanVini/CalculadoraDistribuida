import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Servidor {
    Socket socket;
    DataInputStream entrada;
    DataOutputStream salida;

    int resultado;

    String huella;

    List<String> Cola = new ArrayList<String>();

    public String GenerarHuella(int puerto) {
        try {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
            String formattedDateTime = now.format(formatter);

            // Utiliza el puerto y la fecha actual para generar una huella digital
            String huellaRaw = formattedDateTime + puerto;

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(huellaRaw.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

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

                huella = GenerarHuella(serverPort);
                System.out.println("Huella digital: " + huella);

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
            return serverPort;
        }
        return -1; // No hay puertos disponibles
    }

    public static void main(String[] args) {
        List<Integer> availablePorts = new ArrayList<>();
        availablePorts.add(12345);
        availablePorts.add(12346);
        availablePorts.add(12347);

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
                    String[] Origen = new String[]{paquete[5]};

                    System.out.println("Origen: " + Origen[0]);

                    if (paquete[0].startsWith("ACK")){
                        System.out.println("ACK");
                    }else {
                        try {
                            String acuseRecibo = "ACK," + paquete[7] + "," + huella + "," + Origen[0];
                            salida.writeUTF(acuseRecibo);
                        } catch (IOException error) {
                            System.out.println("No se pudo enviar el acuse de recibo.");
                        }


                    }


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

                        String respuesta = "MOSTRAR," + paquete[1] + "," + paquete[2] + "," + paquete[3] + "," + result + "," + Origen[0];

                        salida.writeUTF(respuesta);
                    }
                }
            } catch (IOException error) {
                System.out.println("La conexión con el middleware se ha cerrado.");
            }
        }
    }
}
