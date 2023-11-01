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
        String paquetePorMandar = "RESOLVER" + "," + n1 + "," + operacion + "," + n2 + "," + "ORIGEN" + "," + puertoActual;

        System.out.println(paquetePorMandar);

        n1 = "";
        n2 = "";
        operacion = "";
        pantalla.setText("");

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


    public void initialize() {
        availablePorts.add(12345);
        availablePorts.add(12346);
        availablePorts.add(12347); // Agrega los puertos disponibles aquí

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
            int serverPort = availablePorts.get(randomIndex);
            availablePorts.remove(randomIndex); // Elimina el puerto usado
            return serverPort;
        }
        return -1; // No hay puertos disponibles
    }
}