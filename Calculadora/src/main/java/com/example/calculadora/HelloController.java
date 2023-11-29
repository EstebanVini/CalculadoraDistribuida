package com.example.calculadora;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HelloController {
    @FXML
    Label pantalla;
    @FXML
    VBox historial;

    ArrayList<String> historialResultados = new ArrayList<>();

    String n1 = "";
    String n2 = "";
    String operacion = "";

    String temp = "";

    int puertoActual = 0;

    String huella = "";

    public Boolean ServidoresSumaDisponibles = false;

    public Boolean ServidoresRestaDisponibles = false;

    public Boolean ServidoresMultiplicacionDisponibles = false;

    public Boolean ServidoresDivisionDisponibles = false;


    private List<String> colaSuma = new ArrayList<>();

    private List<String> colaResta = new ArrayList<>();


    private List<String> colaMultiplicacion = new ArrayList<>();


    private List<String> colaDivision = new ArrayList<>();

    private List<String> AcusesDeRecibo = new ArrayList<>();

    private List<String> MensajesDePrueba = new ArrayList<>();

    private List<String> MensajesEnviados = new ArrayList<>();

    private List<Socket> middlewareSockets = new ArrayList<>();
    private List<DataInputStream> middlewareEntradas = new ArrayList<>();
    private List<DataOutputStream> middlewareSalidas = new ArrayList();

    private List<Integer> availablePorts = new ArrayList<>();
    private Random random = new Random();

    @FXML
    void button0() {
        if (operacion.isEmpty()) {
            n1 += "0";
            pantalla.setText(n1);
        } else {
            n2 += "0";
            pantalla.setText(n1 + operacion + n2);
        }
    }

    @FXML
    void button1() {
        if (operacion.isEmpty()) {
            n1 += "1";
            pantalla.setText(n1);
        } else {
            n2 += "1";
            pantalla.setText(n1 + operacion + n2);
        }
    }

    @FXML
    void button2() {
        if (operacion.isEmpty()) {
            n1 += "2";
            pantalla.setText(n1);
        } else {
            n2 += "2";
            pantalla.setText(n1 + operacion + n2);
        }
    }

    @FXML
    void button3() {
        if (operacion.isEmpty()) {
            n1 += "3";
            pantalla.setText(n1);
        } else {
            n2 += "3";
            pantalla.setText(n1 + operacion + n2);
        }
    }

    @FXML
    void button4() {
        if (operacion.isEmpty()) {
            n1 += "4";
            pantalla.setText(n1);
        } else {
            n2 += "4";
            pantalla.setText(n1 + operacion + n2);
        }
    }

    @FXML
    void button5() {
        if (operacion.isEmpty()) {
            n1 += "5";
            pantalla.setText(n1);
        } else {
            n2 += "5";
            pantalla.setText(n1 + operacion + n2);
        }
    }

    @FXML
    void button6() {
        if (operacion.isEmpty()) {
            n1 += "6";
            pantalla.setText(n1);
        } else {
            n2 += "6";
            pantalla.setText(n1 + operacion + n2);
        }
    }

    @FXML
    void button7() {
        if (operacion.isEmpty()) {
            n1 += "7";
            pantalla.setText(n1);
        } else {
            n2 += "7";
            pantalla.setText(n1 + operacion + n2);
        }
    }

    @FXML
    void button8() {
        if (operacion.isEmpty()) {
            n1 += "8";
            pantalla.setText(n1);
        } else {
            n2 += "8";
            pantalla.setText(n1 + operacion + n2);
        }
    }

    @FXML
    void button9() {
        if (operacion.isEmpty()) {
            n1 += "9";
            pantalla.setText(n1);
        } else {
            n2 += "9";
            pantalla.setText(n1 + operacion + n2);
        }
    }

    @FXML
    void buttonSuma() {
        operacion = "+";
        pantalla.setText(n1 + operacion);
    }

    @FXML
    void buttonResta() {
        operacion = "-";
        pantalla.setText(n1 + operacion);
    }

    @FXML
    void buttonMultiplicacion() {
        operacion = "*";
        pantalla.setText(n1 + operacion);
    }

    @FXML
    void buttonDivision() {
        operacion = "/";
        pantalla.setText(n1 + operacion);
    }

    @FXML
    void buttonIgual() {
        String tipoOperacion = determinarTipoOperacion();

        String huellaEvento = GenerarHuellaEvento(puertoActual, operacion);
        String paquetePorMandar = "RESOLVER;" + tipoOperacion + ";," + n1 + "," + operacion + "," + n2 + "," + "PUERTOORIGEN," + puertoActual + "," + "EVENTO," + huellaEvento + "," + "HUELLA," + huella;

        // Restablece los valores para la próxima operación
        n1 = "";
        n2 = "";
        operacion = "";
        pantalla.setText("");
        // Verificar si hay servidores disponibles para enviar el mensaje

        VerificarServidoresActivos(tipoOperacion);

        // Si no hay mensajes enviados, se envía el mensaje

            switch (tipoOperacion) {
                case "1":
                    if (ServidoresSumaDisponibles) {
                        EnviarPaquete(paquetePorMandar);
                        MensajesEnviados.add(paquetePorMandar);
                        System.out.println("Se ha enviado el paquete:: " + paquetePorMandar);
                    } else {
                        encolarMensaje(paquetePorMandar, tipoOperacion);
                        System.out.println("Se ha encolado el paquete: " + paquetePorMandar);
                    }
                    break;
                case "2":
                    if (ServidoresRestaDisponibles) {
                        EnviarPaquete(paquetePorMandar);
                        MensajesEnviados.add(paquetePorMandar);
                        System.out.println("Se ha enviado el paquete: " + paquetePorMandar);
                    } else {
                        encolarMensaje(paquetePorMandar, tipoOperacion);
                        System.out.println("Se ha encolado el paquete: " + paquetePorMandar);
                    }
                    break;
                case "3":
                    if (ServidoresMultiplicacionDisponibles) {
                        EnviarPaquete(paquetePorMandar);
                        MensajesEnviados.add(paquetePorMandar);
                        System.out.println("Se ha encolado el paquete: " + paquetePorMandar);
                    } else {
                        encolarMensaje(paquetePorMandar, tipoOperacion);
                        System.out.println("Se ha encolado el paquete: " + paquetePorMandar);
                    }
                    break;
                case "4":
                    if (ServidoresDivisionDisponibles) {
                        EnviarPaquete(paquetePorMandar);
                        MensajesEnviados.add(paquetePorMandar);
                        System.out.println("Se ha encolado el paquete " + paquetePorMandar);
                    } else {
                        encolarMensaje(paquetePorMandar, tipoOperacion);
                        System.out.println("Se ha encolado el paquete: " + paquetePorMandar);
                    }
                    break;

            }
        }


    private String determinarTipoOperacion() {
        switch (operacion) {
            case "+":
                return "1";
            case "-":
                return "2";
            case "*":
                return "3";
            case "/":
                return "4";
            default:
                return "";
        }
    }

    private void encolarMensaje(String mensaje, String tipoOperacion) {
        switch (tipoOperacion) {
            case "1":
                colaSuma.add(mensaje);
                break;
            case "2":
                colaResta.add(mensaje);
                break;
            case "3":
                colaMultiplicacion.add(mensaje);
                break;
            case "4":
                colaDivision.add(mensaje);
                break;
        }
    }


    void EnviarPaquete(String paquetePorMandar) {
        try {
            if (!middlewareSalidas.isEmpty()) {
                DataOutputStream salida = middlewareSalidas.get(random.nextInt(middlewareSalidas.size()));
                salida.writeUTF(paquetePorMandar);
            } else {
                System.out.println("No se ha establecido una conexión con el middleware.");
            }
        } catch (IOException error) {
            System.out.println(error);
        }
    }

    void ResolverCola(int cantidadMinimaServidores, String AcuseDeRecibo) {

        String huellaEventoAcuseRecibo = AcuseDeRecibo.split(",")[1];

        String huellaAcuseRecibo = AcuseDeRecibo.split(",")[2];

        // Verificar si hay más acuses de recibo con la misma huella de evento pero diferente huella digital
        int CantidadSercidores = 0;
        for (String acuseDeRecibo : AcusesDeRecibo) {
            if (acuseDeRecibo.split(",")[1].equals(huellaEventoAcuseRecibo)) {
                CantidadSercidores++;
            }
        }

        AcusesDeRecibo.add(AcuseDeRecibo);

        // Buscar el evento al que pertenece en la lista de mensajes enviados y obtener el tipo de operación
        String tipoOperacion = "";
        for (String mensajeEnviado : MensajesEnviados) {
            if (mensajeEnviado.contains(huellaEventoAcuseRecibo)) {
                tipoOperacion = mensajeEnviado.split(";")[1];
                break;
            }
        }

        // Mandar el siguiente mensaje de la cola si hay suficientes servidores disponibles
        if (CantidadSercidores >= cantidadMinimaServidores) {
            switch (tipoOperacion) {
                case "1":
                    if (!colaSuma.isEmpty()) {
                        ServidoresSumaDisponibles = true;
                        String mensaje = colaSuma.get(0);
                        colaSuma.remove(0);
                        EnviarPaquete(mensaje);
                        MensajesEnviados.add(mensaje);
                        System.out.println("Se ha enviado el paquete: " + mensaje);
                        // Esperar 20 ms para que el servidor pueda recibir el mensaje
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "2":
                    if (!colaResta.isEmpty()) {
                        ServidoresRestaDisponibles = true;
                        String mensaje = colaResta.get(0);
                        colaResta.remove(0);
                        EnviarPaquete(mensaje);
                        MensajesEnviados.add(mensaje);
                        System.out.println("Se ha enviado el paquete: " + mensaje);

                        // Esperar 20 ms para que el servidor pueda recibir el mensaje
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "3":
                    if (!colaMultiplicacion.isEmpty()) {
                        ServidoresMultiplicacionDisponibles = true;
                        String mensaje = colaMultiplicacion.get(0);
                        colaMultiplicacion.remove(0);
                        EnviarPaquete(mensaje);
                        MensajesEnviados.add(mensaje);
                        System.out.println("Se ha enviado el paquete: " + mensaje);

                        // Esperar 20 ms para que el servidor pueda recibir el mensaje
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "4":
                    if (!colaDivision.isEmpty()) {
                        ServidoresDivisionDisponibles = true;
                        String mensaje = colaDivision.get(0);
                        colaDivision.remove(0);
                        EnviarPaquete(mensaje);
                        MensajesEnviados.add(mensaje);
                        System.out.println("Se ha enviado el paquete: " + mensaje);

                        // Esperar 20 ms para que el servidor pueda recibir el mensaje
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }

        } else {
            switch (tipoOperacion) {
                case "1" -> ServidoresSumaDisponibles = false;
                case "2" -> ServidoresRestaDisponibles = false;
                case "3" -> ServidoresMultiplicacionDisponibles = false;
                case "4" -> ServidoresDivisionDisponibles = false;
            }
        }
    }

    public boolean VerificarHuella(String huellaEvento, String huella) {
        for (String mensajeEnviado : MensajesEnviados) {
            if (mensajeEnviado.contains(huellaEvento)) {
                String huellaDigital = mensajeEnviado.split(",")[9];
                if (huellaDigital.equals(huella)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void  VerificarServidoresActivos(String tipoOperacion) {

        // Mandar un mensaje de prueba a todos los servidores disponibles para verificar si están activos y guardar los mensajes de prueba en una lista
        switch (tipoOperacion) {
            case "1":
                for (DataOutputStream salida : middlewareSalidas) {
                    try {
                        String huellaEvento = GenerarHuellaEvento(puertoActual, "+");
                        String mensaje = "RESOLVER;1;," + "1" + "," + "+" + "," + "1" + "," + "PUERTOORIGEN," + puertoActual + "," + "EVENTO," + huellaEvento + "," + "HUELLA," + huella;
                        salida.writeUTF(mensaje);
                        MensajesDePrueba.add(mensaje);
                        MensajesEnviados.add(mensaje);
                        // Esperar 20 ms para que el servidor pueda recibir el mensaje
                        try {
                            Thread.sleep(80);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException error) {
                        System.out.println("No se pudo enviar el mensaje de prueba.");
                    }
                }
                break;
            case "2":
                for (DataOutputStream salida : middlewareSalidas) {
                    try {
                        String huellaEvento = GenerarHuellaEvento(puertoActual, "-");
                        String mensaje = "RESOLVER;2;," + "1" + "," + "-" + "," + "1" + "," + "PUERTOORIGEN," + puertoActual + "," + "EVENTO," + huellaEvento + "," + "HUELLA," + huella;
                        salida.writeUTF(mensaje);
                        MensajesDePrueba.add(mensaje);
                        MensajesEnviados.add(mensaje);
                        // Esperar 20 ms para que el servidor pueda recibir el mensaje
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException error) {
                        System.out.println("No se pudo enviar el mensaje de prueba.");
                    }
                }
                break;
            case "3":
                for (DataOutputStream salida : middlewareSalidas) {
                    try {
                        String huellaEvento = GenerarHuellaEvento(puertoActual, "*");
                        String mensaje = "RESOLVER;3;," + "1" + "," + "*" + "," + "1" + "," + "PUERTOORIGEN," + puertoActual + "," + "EVENTO," + huellaEvento + "," + "HUELLA," + huella;
                        salida.writeUTF(mensaje);
                        MensajesDePrueba.add(mensaje);
                        MensajesEnviados.add(mensaje);
                        // Esperar 20 ms para que el servidor pueda recibir el mensaje
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException error) {
                        System.out.println("No se pudo enviar el mensaje de prueba.");
                    }
                }
                break;
            case "4":
                for (DataOutputStream salida : middlewareSalidas) {
                    try {
                        String huellaEvento = GenerarHuellaEvento(puertoActual, "/");
                        String mensaje = "RESOLVER;4;," + "1" + "," + "/" + "," + "1" + "," + "PUERTOORIGEN," + puertoActual + "," + "EVENTO," + huellaEvento + "," + "HUELLA," + huella;
                        salida.writeUTF(mensaje);
                        MensajesDePrueba.add(mensaje);
                        MensajesEnviados.add(mensaje);
                        // Esperar 20 ms para que el servidor pueda recibir el mensaje
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException error) {
                        System.out.println("No se pudo enviar el mensaje de prueba.");
                    }
                }
                break;
        }
    }

    public boolean VerificarMensajeDePrueba(String huellaEvento) {
        for (String mensajeDePrueba : MensajesDePrueba) {
            if (mensajeDePrueba.contains(huellaEvento)) {
                return true;
            }
        }
        return false;
    }





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

    public String GenerarHuellaEvento(int puertoActual, String operacion){

        try {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
            String formattedDateTime = now.format(formatter);

            // Utiliza el puerto y la fecha actual para generar una huella digital
            String huellaRaw = formattedDateTime + puertoActual + operacion;

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


    public void initialize() {
        availablePorts.add(12345);
        availablePorts.add(12346);
        availablePorts.add(12347);



        Thread socketThread = new Thread(() -> {
            while (true) {
                int port = getRandomPort(availablePorts);
                if (port == -1) {
                    System.out.println("No hay puertos disponibles para conectarse.");
                    break;
                }

                try {
                    Socket socket = new Socket("127.0.0.1", port);
                    DataInputStream entrada = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    DataOutputStream salida = new DataOutputStream(socket.getOutputStream());

                    middlewareSockets.add(socket);
                    middlewareEntradas.add(entrada);
                    middlewareSalidas.add(salida);

                    System.out.println("Conexión establecida con middleware en el puerto " + port);

                    puertoActual = port;

                    huella = GenerarHuella(puertoActual);
                    System.out.println("Huella digital: " + huella);

                    // Encolamos varios mensajes de suma para probar el algoritmo de resolución de colas
                    colaSuma.add("RESOLVER;1;," + "1" + "," + "+" + "," + "1" + "," + "PUERTOORIGEN," + puertoActual + "," + "EVENTO," + GenerarHuellaEvento(puertoActual, "+") + "," + "HUELLA," + huella);
                    colaSuma.add("RESOLVER;1;," + "2" + "," + "+" + "," + "2" + "," + "PUERTOORIGEN," + puertoActual + "," + "EVENTO," + GenerarHuellaEvento(puertoActual, "+") + "," + "HUELLA," + huella);
                    colaSuma.add("RESOLVER;1;," + "3" + "," + "+" + "," + "3" + "," + "PUERTOORIGEN," + puertoActual + "," + "EVENTO," + GenerarHuellaEvento(puertoActual, "+") + "," + "HUELLA," + huella);
                    colaSuma.add("RESOLVER;1;," + "4" + "," + "+" + "," + "4" + "," + "PUERTOORIGEN," + puertoActual + "," + "EVENTO," + GenerarHuellaEvento(puertoActual, "+") + "," + "HUELLA," + huella);
                    colaSuma.add("RESOLVER;1;," + "5" + "," + "+" + "," + "5" + "," + "PUERTOORIGEN," + puertoActual + "," + "EVENTO," + GenerarHuellaEvento(puertoActual, "+") + "," + "HUELLA," + huella);


                    while (true) {
                        temp = entrada.readUTF();
                        String messageParts[] = temp.split(",");
                        if (temp.startsWith("MOSTRAR")) {
                            String huellaEvento = messageParts[6];
                            if (!VerificarMensajeDePrueba(huellaEvento)) {
                                System.out.println(temp + "\n");
                                String resultado = messageParts[1] + " " + messageParts[2] + " " + messageParts[3] + " = " + messageParts[4];
                                if (!historialResultados.isEmpty()) {
                                    if (!resultado.equals(historialResultados.get(historialResultados.size() - 1))) {
                                        historialResultados.add(resultado);
                                        Platform.runLater(() -> {
                                            Label label = new Label(resultado);
                                            historial.getChildren().add(label);
                                        });
                                    }
                                } else {
                                    historialResultados.add(resultado);
                                    Platform.runLater(() -> {
                                        Label label = new Label(resultado);
                                        historial.getChildren().add(label);
                                    });
                                }
                            }
                        }


                        else if (temp.startsWith("ACK")) {
                            AcusesDeRecibo.add(temp);
                            // si la huella del evento es de un mensaje de prueba, no se hace nada
                            String huellaEvento = messageParts[1];
                            if (!VerificarMensajeDePrueba(huellaEvento)) {
                                System.out.println("Acuse de recibo recibido: " + temp + "\n");
                            }
                            ResolverCola(2, temp);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("No se pudo conectar al middleware en el puerto " + port + ". Reintentando...");
                }
            }
        });
        socketThread.setDaemon(true);
        socketThread.start();
    }

    private int getRandomPort(List<Integer> availablePorts) {
        if (!availablePorts.isEmpty()) {
            int randomIndex = random.nextInt(availablePorts.size());
            int serverPort = availablePorts.get(randomIndex);// Elimina el puerto usado
            return serverPort;
        }
        return -1; // No hay puertos disponibles
    }
}