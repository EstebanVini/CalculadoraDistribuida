package com.example.calculadora;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
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


    private List<String> colaSuma = new ArrayList<>();

    private List<String> colaResta = new ArrayList<>();


    private List<String> colaMultiplicacion = new ArrayList<>();


    private List<String> colaDivision = new ArrayList<>();

    private List<String> AcusesDeRecibo = new ArrayList<>();

    private List<String> MensajesEnviados = new ArrayList<>();

    private List<Socket> middlewareSockets = new ArrayList<>();
    private List<DataInputStream> middlewareEntradas = new ArrayList<>();
    private List<DataOutputStream> middlewareSalidas = new ArrayList();

    private List<Integer> availablePorts = new ArrayList<>();
    private Random random = new Random();

    @FXML
    void button0() {
        if(operacion.isEmpty())
        {
            n1 += "0";
            pantalla.setText(n1);
        }
        else
        {
            n2 += "0";
            pantalla.setText(n1+operacion+n2);
        }
    }

    @FXML
    void button1() {
        if(operacion.isEmpty())
        {
            n1 += "1";
            pantalla.setText(n1);
        }
        else
        {
            n2 += "1";
            pantalla.setText(n1+operacion+n2);
        }
    }

    @FXML
    void button2() {
        if(operacion.isEmpty())
        {
            n1 += "2";
            pantalla.setText(n1);
        }
        else
        {
            n2 += "2";
            pantalla.setText(n1+operacion+n2);
        }
    }

    @FXML
    void button3() {
        if(operacion.isEmpty())
        {
            n1 += "3";
            pantalla.setText(n1);
        }
        else
        {
            n2 += "3";
            pantalla.setText(n1+operacion+n2);
        }
    }

    @FXML
    void button4() {
        if(operacion.isEmpty())
        {
            n1 += "4";
            pantalla.setText(n1);
        }
        else
        {
            n2 += "4";
            pantalla.setText(n1+operacion+n2);
        }
    }

    @FXML
    void button5() {
        if(operacion.isEmpty())
        {
            n1 += "5";
            pantalla.setText(n1);
        }
        else
        {
            n2 += "5";
            pantalla.setText(n1+operacion+n2);
        }
    }

    @FXML
    void button6() {
        if(operacion.isEmpty())
        {
            n1 += "6";
            pantalla.setText(n1);
        }
        else
        {
            n2 += "6";
            pantalla.setText(n1+operacion+n2);
        }
    }

    @FXML
    void button7() {
        if(operacion.isEmpty())
        {
            n1 += "7";
            pantalla.setText(n1);
        }
        else
        {
            n2 += "7";
            pantalla.setText(n1+operacion+n2);
        }
    }

    @FXML
    void button8() {
        if(operacion.isEmpty())
        {
            n1 += "8";
            pantalla.setText(n1);
        }
        else
        {
            n2 += "8";
            pantalla.setText(n1+operacion+n2);
        }
    }

    @FXML
    void button9() {
        if(operacion.isEmpty())
        {
            n1 += "9";
            pantalla.setText(n1);
        }
        else
        {
            n2 += "9";
            pantalla.setText(n1+operacion+n2);
        }
    }

    @FXML
    void buttonSuma() {
        operacion = "+";
        pantalla.setText(n1+operacion);
    }

    @FXML
    void buttonResta() {
        operacion = "-";
        pantalla.setText(n1+operacion);
    }

    @FXML
    void buttonMultiplicacion() {
        operacion = "*";
        pantalla.setText(n1+operacion);
    }

    @FXML
    void buttonDivision() {
        operacion = "/";
        pantalla.setText(n1+operacion);
    }

    @FXML
    void buttonIgual() {

        String tipoOperacion = "";

        switch (operacion) {
            case "+":
                tipoOperacion = "1";
                break;
            case "-":
                tipoOperacion = "2";
                break;
            case "*":
                tipoOperacion = "3";
                break;
            case "/":
                tipoOperacion = "4";
                break;
        }

        String huellaEvento = GenerarHuellaEvento(puertoActual, operacion);

        String paquetePorMandar = "RESOLVER;" + tipoOperacion + ";," + n1 + "," + operacion + "," + n2 + "," + "PUERTOORIGEN," + puertoActual + "," + "EVENTO," + huellaEvento + "," + "HUELLA," + huella;

        System.out.println(paquetePorMandar);

        n1 = "";
        n2 = "";
        operacion = "";
        pantalla.setText("");

        // Si no hay mensajes en la cola del tipo de operación, se envía el mensaje
        if (tipoOperacion.equals("1") ) {
            EnviarPaquete(paquetePorMandar);
            System.out.println("Se ha enviado el paquete: " + paquetePorMandar);
            MensajesEnviados.add(paquetePorMandar);
        } else if (tipoOperacion.equals("2") && colaResta.isEmpty()) {
            EnviarPaquete(paquetePorMandar);
            System.out.println("Se ha enviado el paquete: " + paquetePorMandar);
            MensajesEnviados.add(paquetePorMandar);
        } else if (tipoOperacion.equals("3") && colaMultiplicacion.isEmpty()) {
            EnviarPaquete(paquetePorMandar);
            System.out.println("Se ha enviado el paquete: " + paquetePorMandar);
            MensajesEnviados.add(paquetePorMandar);
        } else if (tipoOperacion.equals("4") && colaDivision.isEmpty()) {
            EnviarPaquete(paquetePorMandar);
            System.out.println("Se ha enviado el paquete: " + paquetePorMandar);
            MensajesEnviados.add(paquetePorMandar);
        } else {
            //Encolar el paquete en la cola correspondiente
            switch (tipoOperacion) {
                case "1":
                    colaSuma.add(paquetePorMandar);
                    break;
                case "2":
                    colaResta.add(paquetePorMandar);
                    break;
                case "3":
                    colaMultiplicacion.add(paquetePorMandar);
                    break;
                case "4":
                    colaDivision.add(paquetePorMandar);
                    break;
            }
        }

    }

    void EnviarPaquete(String paquetePorMandar){
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

    void ResolverCola(int cantidadMinimaServidores, String AcuseDeRecibo){

        String[] paquete = AcuseDeRecibo.split(",");
        String huellaEvento = paquete[1];
        System.out.println("Huella de evento del acuse de recibo: " + huellaEvento);
        String huellaOrigen = paquete[2];

        int CantidadDeAcuses = 1;
        // Verificar las huellas de evento de los acuses de recibo guardados anteriormente
        for (int i = 0; i < AcusesDeRecibo.size(); i++) {
            String[] paqueteAcuse = AcusesDeRecibo.get(i).split(",");
            String huellaEventoAcuse = paqueteAcuse[1];
            String huellaOrigenAcuse = paqueteAcuse[2];
            if (huellaEvento.equals(huellaEventoAcuse) && !huellaOrigen.equals(huellaOrigenAcuse)) {
                CantidadDeAcuses++;
            }
        }

        AcusesDeRecibo.add(AcuseDeRecibo);


        if (CantidadDeAcuses >= cantidadMinimaServidores) {


            // Verificar las huellas de evento de los mensajes enviados guardados anteriormente
            for (int i = 0; i < MensajesEnviados.size(); i++) {
                String[] paqueteMensaje = MensajesEnviados.get(i).split(",");
                String huellaEventoMensaje = paqueteMensaje[7];
                System.out.println("Huella de evento del mensaje: " + huellaEventoMensaje);
                String TipoOperacion = paqueteMensaje[0].split(";")[1];
                System.out.println("Tipo de operación: " + TipoOperacion);

                if (huellaEvento.equals(huellaEventoMensaje)) {
                    try {
                        switch (TipoOperacion) {
                            case "1":
                                EnviarPaquete(colaSuma.get(0));
                                System.out.println("Se ha enviado el paquete: " + colaSuma.get(0));
                                colaSuma.remove(0);
                                MensajesEnviados.add(colaSuma.get(0));
                                break;
                            case "2":
                                EnviarPaquete(colaResta.get(0));
                                colaResta.remove(0);
                                MensajesEnviados.add(colaResta.get(0));
                                break;
                            case "3":
                                EnviarPaquete(colaMultiplicacion.get(0));
                                colaMultiplicacion.remove(0);
                                MensajesEnviados.add(colaMultiplicacion.get(0));
                                break;
                            case "4":
                                EnviarPaquete(colaDivision.get(0));
                                colaDivision.remove(0);
                                MensajesEnviados.add(colaDivision.get(0));
                                break;
                        }

                    }
                    catch (IndexOutOfBoundsException e){
                        System.out.println("No hay mensajes en la cola");
                    }

                }
            }
        }

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

        //para hacer pruebas agregaremos 5 mensajes a la cola de suma
        colaSuma.add("RESOLVER;1;," + "1" + "," + "+" + "," + "1" + "," + "PUERTOORIGEN," + puertoActual + "," + "EVENTO," + GenerarHuellaEvento(puertoActual, "+") + "," + "HUELLA," + huella);
        colaSuma.add("RESOLVER;1;," + "2" + "," + "+" + "," + "2" + "," + "PUERTOORIGEN," + puertoActual + "," + "EVENTO," + GenerarHuellaEvento(puertoActual, "+") + "," + "HUELLA," + huella);
        colaSuma.add("RESOLVER;1;," + "3" + "," + "+" + "," + "3" + "," + "PUERTOORIGEN," + puertoActual + "," + "EVENTO," + GenerarHuellaEvento(puertoActual, "+") + "," + "HUELLA," + huella);
        colaSuma.add("RESOLVER;1;," + "4" + "," + "+" + "," + "4" + "," + "PUERTOORIGEN," + puertoActual + "," + "EVENTO," + GenerarHuellaEvento(puertoActual, "+") + "," + "HUELLA," + huella);
        colaSuma.add("RESOLVER;1;," + "5" + "," + "+" + "," + "5" + "," + "PUERTOORIGEN," + puertoActual + "," + "EVENTO," + GenerarHuellaEvento(puertoActual, "+") + "," + "HUELLA," + huella);

        // imprimimos la cola de suma
        System.out.println("Cola de suma:");
        for (int i = 0; i < colaSuma.size(); i++) {
            System.out.println(colaSuma.get(i));
        }
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

                    while (true) {
                        temp = entrada.readUTF();
                        String messageParts[] = temp.split(",");
                        if (temp.startsWith("MOSTRAR")) {
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
                        else if (temp.startsWith("ACK")) {
                            System.out.println("Acuse de recibo recibido: " + temp);
                            ResolverCola(1, temp);
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