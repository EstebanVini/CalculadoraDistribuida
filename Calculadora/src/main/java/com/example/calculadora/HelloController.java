package com.example.calculadora;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;


import java.io.*;
import java.net.*;


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


    private Socket socket;
    private DataInputStream entrada;
    private DataOutputStream salida;

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
        String paquetePorMandar = "RESOLVER"+","+n1+","+operacion+","+n2;

        n1 = "";
        n2 = "";
        operacion = "";
        pantalla.setText("");

        try
        {
            salida.writeUTF(paquetePorMandar);
        } catch (IOException error)
        {
            System.out.println(error);
        }

    }


    public void initialize()
    {

        // Start a new thread for socket communication.
        Thread socketThread = new Thread(() -> {
            try {

                socket = new Socket("127.0.0.1", 12347);
                entrada = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                salida = new DataOutputStream(socket.getOutputStream());

                while (true) {

                    temp = entrada.readUTF();
                    String messageParts[] = temp.split(",");
                    if(temp.startsWith("MOSTRAR"))
                    {
                        String resultado = messageParts[1]+" "+messageParts[2]+" "+messageParts[3]+" = "+messageParts[4];
                        historialResultados.add(resultado);
                        Platform.runLater(() -> {
                            Label resultLabel = new Label(resultado);
                            historial.getChildren().add(resultLabel);
                        });
                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        socketThread.setDaemon(true);
        socketThread.start();
    }

}