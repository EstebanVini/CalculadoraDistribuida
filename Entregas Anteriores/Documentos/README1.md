# Entrega 1: Calculadora distribuida

Cómputo Distribuido 

Profesor: Carlos Pérez Leguízamo

	     Sebastián Godínez Borja

Esteban Viniegra Pérez Olagaray 

ID: 0235320

Fecha de entrega: 18 de septiembre del 2023

**Introducción**

El cómputo distribuido ha revolucionado la manera en que se procesan y gestionan datos, permitiendo dividir tareas y procesos en múltiples componentes y máquinas que trabajan de forma simultánea. Esta calculadora distribuida es una representación simplificada de cómo se pueden utilizar estos principios para operaciones básicas. A continuación, se describen en detalle las tecnologías y códigos que componen este proyecto.

**Desarrollo**

El proyecto se divide en 3 partes: \




* **Cliente (Calculadora con interfaz gráfica)**

    Diseño y arquitectura:


    La interfaz de cliente está diseñada para ser intuitiva. La elección de JavaFX como tecnología principal es para aprovechar su capacidad de proporcionar interfaces ricas y dinámicas, además de ser multiplataforma.


    

<p id="gdcalert2" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: inline image link here (to images/image2.png). Store image on your image server and adjust path/filename/extension if necessary. </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert3">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>


![alt_text](images/image2.png "image_tooltip")



    Figura 1: Interfaz gráfica del cliente, es una calculadora sencilla capaz de realizar operaciones y comunicarse con el middelware


    Funcionalidad:


    Cada vez que un usuario ingresa un número o selecciona una operación, esta entrada se almacena temporalmente en variables. Al presionar el botón de igual, se inicia la comunicación con el middleware. El uso de sockets permite que esta comunicación sea en tiempo real. \
 \


<p id="gdcalert3" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: inline image link here (to images/image3.png). Store image on your image server and adjust path/filename/extension if necessary. </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert4">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>


![alt_text](images/image3.png "image_tooltip")



    Figura 2: Funcionalidad del botón igual para mandar la operación \
 \


<p id="gdcalert4" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: inline image link here (to images/image4.png). Store image on your image server and adjust path/filename/extension if necessary. </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert5">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>


![alt_text](images/image4.png "image_tooltip")



    Figura 3: Inicialización de la comunicación entre MOM y cliente utilizando websockets


    El principal desafío aquí es garantizar que los datos se envíen de manera coherente y segura al middleware, y que cualquier interrupción o error en la comunicación sea manejado adecuadamente.

* **Middleware o MOM (Message-Oriented Middleware)**

    Diseño y arquitectura:


    El middleware actúa como puente y buffer. Está diseñado para manejar múltiples conexiones simultáneamente, evitando cuellos de botella y asegurando que los mensajes sean entregados adecuadamente.


    Funcionalidad:


    Al recibir un mensaje del cliente, el middleware puede hacer una de las siguientes acciones:


    Procesar el mensaje si es algo simple (por ejemplo, una solicitud de ping para comprobar la conexión).


    Reenviar el mensaje al servidor si requiere cálculos o procesamiento adicional.


    El middleware utiliza programación basada en hilos para manejar múltiples conexiones. Cuando se establece una conexión, se crea un nuevo hilo dedicado a ese cliente en particular.


    

<p id="gdcalert5" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: inline image link here (to images/image5.png). Store image on your image server and adjust path/filename/extension if necessary. </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert6">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>


![alt_text](images/image5.png "image_tooltip")



    Figura 4: Inicialización de socket para el servidor y creación de hilos y sockets para cada cliente, para poder ser manejados de manera simultánea


    

<p id="gdcalert6" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: inline image link here (to images/image6.png). Store image on your image server and adjust path/filename/extension if necessary. </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert7">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>


![alt_text](images/image6.png "image_tooltip")



    Figura 5: Manejo de clientes y comunicación hacia el servidor

* Servidor

    Diseño:


    El servidor es, en esencia, el motor de cálculo. Está diseñado para ser eficiente y rápido, garantizando tiempos de respuesta mínimos.


    Funcionalidad:


    Cuando el servidor recibe una solicitud de cálculo del middleware, primero descompone y analiza el mensaje para determinar qué operación debe realizar. Posteriormente, ejecuta la operación y devuelve el resultado al middleware.


    Todo el manejo de la operación y la lógica matemática está encapsulada aquí, lo que significa que si en el futuro se quisieran agregar más operaciones o funcionalidades, este sería el lugar para hacerlo.


    

<p id="gdcalert7" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: inline image link here (to images/image7.png). Store image on your image server and adjust path/filename/extension if necessary. </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert8">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>


![alt_text](images/image7.png "image_tooltip")



    Figura 6: Lógica matemática para cada operación


    

<p id="gdcalert8" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: inline image link here (to images/image8.png). Store image on your image server and adjust path/filename/extension if necessary. </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert9">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>


![alt_text](images/image8.png "image_tooltip")



    Figura 7: Inicialización del servidor, creando el socket de comunicación, distribución de datos utilizando hilos y definiendo el puerto por el cuál se va a comunicar


**Concusión**

Cada componente de este proyecto de calculadora distribuida tiene su propio conjunto de desafíos y responsabilidades:



* El cliente se centra en la interacción con el usuario.
* El middleware se encarga de la comunicación y el enrutamiento de mensajes.
* El servidor realiza el cálculo y procesamiento principal.

Hay que asegurar que estos tres componentes trabajen juntos armoniosamente y de manera eficiente es la clave del éxito del proyecto. Además, cada uno de ellos podría escalarse o mejorarse independientemente según las necesidades futuras. Por ejemplo, se podría migrar el servidor a un clúster de servidores para manejar un mayor volumen de cálculos, o se podría mejorar el cliente con una interfaz más avanzada.
