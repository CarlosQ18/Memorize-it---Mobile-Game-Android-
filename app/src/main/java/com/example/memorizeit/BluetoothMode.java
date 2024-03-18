package com.example.memorizeit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class BluetoothMode extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private String address;
    private String NicknamePersonal,NicknameRival;
    private TextView titulo,State,UserHost,UserClient;
    private int rol;

    static final int ESTADO_VACIO = 1;
    static final int ESTADO_CONECTANDO=2;
    static final int ESTADO_CONECTADO=3;
    static final int ESTADO_CONEXION_FALLIDA=4;
    static final int ESTADO_MSG_RECIBIDO=5;
    static final int DISCOVERABLE_DEVICE = 7;

    private ServerClass serverClass;
    private ClientClass clientClass;
    private SendReceive sendReceive;
    private static final String APP_NAME = "MemorizeIt";
    private static final UUID MY_UUID=UUID.fromString("7e6cd11e-a097-44da-8d53-e9a911296f6b");

    private Button[] botones;
    private List<Integer> secuencia = new ArrayList<>();
    private List<Integer> secuenciaPropia = new ArrayList<>();
    private List<Integer> secuenciaRival = new ArrayList<>();

    private int error = 0;
    private int nivel = 1;
    private int contador = 0;

    private boolean mostrandoSecuencia = false;
    private boolean sendingNames = true;
    private boolean printingNames = true;
    private boolean generandoSecuencia = false;
    private boolean ganador = false;

    private int botonElegido;
    private String cadenaNumero;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_mode);

        findViewByIdes();
        context = this;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        rol = this.getIntent().getIntExtra("rol", -1);
        NicknamePersonal = this.getIntent().getStringExtra( "nick" );

        if (rol == 0) {
            UserHost.setText(NicknamePersonal);
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivityForResult(intent, DISCOVERABLE_DEVICE);
        } else if( rol == 1 ) {
            UserClient.setText(NicknamePersonal);
            address = this.getIntent().getStringExtra( "Address" );
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice( address );
            clientClass = new ClientClass(device);
            clientClass.start();
        }

        botones = new Button[9];
        botones[0] = findViewById(R.id.boton00);
        botones[1] = findViewById(R.id.boton01);
        botones[2] = findViewById(R.id.boton02);
        botones[3] = findViewById(R.id.boton03);
        botones[4] = findViewById(R.id.boton04);
        botones[5] = findViewById(R.id.boton05);
        botones[6] = findViewById(R.id.boton06);
        botones[7] = findViewById(R.id.boton07);
        botones[8] = findViewById(R.id.boton08);

        botones[0].setEnabled(false);
        botones[1].setEnabled(false);
        botones[2].setEnabled(false);
        botones[3].setEnabled(false);
        botones[4].setEnabled(false);
        botones[5].setEnabled(false);
        botones[6].setEnabled(false);
        botones[7].setEnabled(false);
        botones[8].setEnabled(false);

        for (Button boton : botones) {
            boton.setOnClickListener(this);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DISCOVERABLE_DEVICE && resultCode != RESULT_CANCELED) {
            serverClass = new ServerClass();
            serverClass.start();
        } else{
            Toast.makeText(this, "Permita que el servidor sea Descubierto", Toast.LENGTH_SHORT).show();
            finish();
        }

    }


    private void findViewByIdes() {

        UserHost = (TextView) findViewById(R.id.userHost);
        UserClient = (TextView) findViewById(R.id.userClient);
        State= (TextView) findViewById(R.id.state);
        titulo = (TextView) findViewById(R.id.titulo);

    }

    Handler handler = new Handler(Looper.myLooper(), new Handler.Callback(){
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case ESTADO_VACIO :
                    handler.removeCallbacksAndMessages(null);
                    finish();
                    break;
                case ESTADO_CONECTANDO:
                    State.setVisibility(View.VISIBLE);
                    State.setText("Creando Partida...");
                    break;
                case ESTADO_CONECTADO:
                    State.setVisibility(View.VISIBLE);
                    State.setText("Conectado");
                    System.out.println("Hola");
                    if (sendingNames == true){
                        sendReceive.write(NicknamePersonal.getBytes());
                        sendingNames = false;
                    }
                    break;
                case ESTADO_CONEXION_FALLIDA:
                    ApagarSockets();
                    error = 1;
                    Intent intent = new Intent(context, UserInputActivity.class);
                    intent.putExtra("nick",NicknamePersonal);
                    intent.putExtra("error", error);
                    State.setText("Conexion Fallida");
                    handler.removeCallbacksAndMessages(null);
                    startActivity(intent);
                    finish();
                    break;

                case ESTADO_MSG_RECIBIDO:

                    byte[] readBuff= (byte[]) msg.obj;
                    String tempMsg=new String(readBuff,0,msg.arg1);

                    if (printingNames){
                        if(rol == 0){
                            UserClient.setText(tempMsg);
                            NicknameRival = tempMsg;
                        }else{
                            UserHost.setText(tempMsg);
                            NicknameRival = tempMsg;
                        }
                        printingNames = false;
                        GenerarSecuencia();
                    }

                    if (generandoSecuencia){
                        if (rol == 1){
                            try {
                                botonElegido = Integer.parseInt(tempMsg);
                                secuencia.remove(secuencia.size()-1);
                                secuencia.add(botonElegido);
                                System.out.println("Global: " + secuencia);
                                generandoSecuencia = false;
                                System.out.println("Lo Hago 1?");
                                PintarBoton();
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }else{
                            try {
                                int temp = botonElegido;
                                botonElegido = Integer.parseInt(tempMsg);
                                secuencia.remove(secuencia.size()-1);
                                secuencia.add(temp);
                                System.out.println("Global: " + secuencia);
                                generandoSecuencia = false;
                                System.out.println("Lo Hago 1?");
                                PintarBoton();
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (tempMsg.contains("-")) {
                        String[] clave = tempMsg.split("-");
                        if (clave[0].equals("index" + Integer.toString(secuenciaRival.size()))) {
                            secuenciaRival.add(Integer.parseInt(clave[1]));
                            System.out.println("Rival: " + secuenciaRival);
                        }
                        
                        if ( (secuenciaPropia.size() == secuencia.size()) && (secuenciaRival.size() == secuencia.size()) ){

                            if ( !(secuenciaPropia.equals(secuencia)) && !(secuenciaRival.equals(secuencia))){
                                secuenciaPropia.clear();
                                secuenciaRival.clear();
                                nivel++;
                                contador = 0;
                                Toast.makeText(context, "Empate - Muerte Súbita", Toast.LENGTH_SHORT).show();
                                GenerarSecuencia();
                            }
                            else if ( !(secuenciaPropia.equals(secuencia)) && (secuenciaRival.equals(secuencia))){
                                ganador = true;
                                Intent winner = new Intent(context, resultsMultipMode.class);
                                winner.putExtra("rol",rol);
                                winner.putExtra("Address",address);
                                winner.putExtra("NicknamePersonal", NicknamePersonal);
                                winner.putExtra("NicknameRival", NicknameRival);
                                winner.putExtra("ganador",NicknameRival);
                                winner.putExtra("nivel",nivel);
                                ApagarSockets();
                                startActivity(winner);
                                finish();
                            }
                            else if ( (secuenciaPropia.equals(secuencia)) && !(secuenciaRival.equals(secuencia))){
                                ganador = true;
                                Intent winner = new Intent(context, resultsMultipMode.class);
                                winner.putExtra("rol",rol);
                                winner.putExtra("Address",address);
                                winner.putExtra("NicknamePersonal", NicknamePersonal);
                                winner.putExtra("NicknameRival", NicknameRival);
                                winner.putExtra("ganador",NicknamePersonal);
                                winner.putExtra("nivel",nivel);
                                ApagarSockets();
                                startActivity(winner);
                                finish();
                            }
                            else{
                                System.out.println(secuenciaPropia);
                                System.out.println(secuenciaRival);
                                secuenciaPropia.clear();
                                secuenciaRival.clear();
                                nivel++;
                                contador = 0;
                                Toast.makeText(context, "Los 2 jugadores adivinaron la secuencia", Toast.LENGTH_SHORT).show();
                                GenerarSecuencia();
                            }

                        }
                    }
                    break;
            }
            return true;
        }
    });

    private void GenerarSecuencia() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        botones[0].setEnabled(true);
        botones[1].setEnabled(true);
        botones[2].setEnabled(true);
        botones[3].setEnabled(true);
        botones[4].setEnabled(true);
        botones[5].setEnabled(true);
        botones[6].setEnabled(true);
        botones[7].setEnabled(true);
        botones[8].setEnabled(true);

        printingNames = false;
        titulo.setText("Nivel: " + nivel);
        botonElegido = generarBotonAleatorio();
        cadenaNumero = String.valueOf(botonElegido);
        secuencia.add(botonElegido);
        sendReceive.write(cadenaNumero.getBytes());
        generandoSecuencia = true;
    }
    private int generarBotonAleatorio() {
        Random random = new Random();
        return random.nextInt(9);
    }
    private void PintarBoton() {

        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Lo Hago 2?");
        Handler pintar = new Handler();
        for (int i = 0; i < secuencia.size(); i++) {
            int botonIndex = secuencia.get(i);
            int delay = (i + 1) * 1000;
            mostrandoSecuencia = true;
            pintar.postDelayed(() -> encenderBoton(botones[botonIndex]), delay);
        }
        pintar.postDelayed(() -> mostrandoSecuencia = false, secuencia.size() * 1000);
    }

    private void encenderBoton(Button boton) {
        boton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFEB3B")));
        Handler encender = new Handler();
        encender.postDelayed(() -> apagarBoton(boton), 500);
    }
    private void apagarBoton(Button boton) {
        generandoSecuencia = false;
        boton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0B658E")));;
    }

    @Override
    public void onClick(View v) {

        if (mostrandoSecuencia) {
            return;
        }

        if (contador < secuencia.size()){
            Button boton = (Button) v;
            encenderBoton(boton);
            int botonIndex = obtenerBotonIndex(boton);
            secuenciaPropia.add(botonIndex);
            System.out.println("Propia: " + secuenciaPropia);
            String secuenciaEnviar = "index" + Integer.toString(contador) + "-" + Integer.toString(botonIndex);
            sendReceive.write(secuenciaEnviar.getBytes());
            contador++;
        }else{
            Toast.makeText(context, "Esperando a que termine " + NicknameRival, Toast.LENGTH_SHORT).show();
        }


        if ( (secuenciaPropia.size() == secuencia.size()) && (secuenciaRival.size() == secuencia.size()) ){
            if ( !(secuenciaPropia.equals(secuencia)) && !(secuenciaRival.equals(secuencia))){
                secuenciaPropia.clear();
                secuenciaRival.clear();
                nivel++;
                contador = 0;
                Toast.makeText(context, "Empate - Muerte Súbita", Toast.LENGTH_SHORT).show();
                GenerarSecuencia();
            }
            else if ( !(secuenciaPropia.equals(secuencia)) && (secuenciaRival.equals(secuencia))){
                ganador = true;
                Intent winner = new Intent(context, resultsMultipMode.class);
                winner.putExtra("rol",rol);
                winner.putExtra("NicknamePersonal", NicknamePersonal);
                winner.putExtra("NicknameRival", NicknameRival);
                winner.putExtra("ganador",NicknameRival);
                winner.putExtra("nivel",nivel);
                ApagarSockets();
                startActivity(winner);
                finish();
            }
            else if ( (secuenciaPropia.equals(secuencia)) && !(secuenciaRival.equals(secuencia))){
                ganador = true;
                Intent winner = new Intent(context, resultsMultipMode.class);
                winner.putExtra("rol",rol);
                winner.putExtra("NicknamePersonal", NicknamePersonal);
                winner.putExtra("NicknameRival", NicknameRival);
                winner.putExtra("ganador",NicknamePersonal);
                winner.putExtra("nivel",nivel);
                ApagarSockets();
                startActivity(winner);
                finish();
            }
            else{
                System.out.println(secuenciaPropia);
                System.out.println(secuenciaRival);
                secuenciaPropia.clear();
                secuenciaRival.clear();
                nivel++;
                contador = 0;
                Toast.makeText(context, "Los 2 jugadores adivinaron la secuencia", Toast.LENGTH_SHORT).show();
                GenerarSecuencia();
            }
        }



    }

    private int obtenerBotonIndex(Button boton) {
        for (int i = 0; i < botones.length; i++) {
            if (boton == botones[i]) {
                return i;
            }
        }
        return -1;
    }

    public synchronized void ApagarSockets(){

        if (serverClass != null) {
            serverClass.cancel();
            serverClass = null;
        }

        if (clientClass != null) {
            clientClass.cancel();
            clientClass = null;
        }

        if (sendReceive != null) {
            sendReceive.cancel();
            sendReceive = null;
        }


    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Quieres salir del juego?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ApagarSockets();
                        error = 0;
                        Intent intent = new Intent(context, UserInputActivity.class);
                        intent.putExtra("nick",NicknamePersonal);
                        intent.putExtra("error",0);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();
    }


    private class ServerClass extends Thread
    {
        private BluetoothServerSocket serverSocket;

        public ServerClass(){
            try {
                serverSocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME,MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            BluetoothSocket socket=null;

            while (socket==null)
            {
                try {
                    Message message=Message.obtain();
                    message.what=ESTADO_CONECTANDO;
                    handler.sendMessage(message);
                    socket=serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message=Message.obtain();
                    message.what=ESTADO_VACIO;
                    handler.sendMessage(message);

                }

                if(socket!=null)
                {
                    Message message=Message.obtain();
                    message.what=ESTADO_CONECTADO;
                    handler.sendMessage(message);
                    sendReceive=new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }

        public void cancel(){
            try{
                serverSocket.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private class ClientClass extends Thread
    {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass (BluetoothDevice device1)
        {
            device=device1;

            try {
                socket=device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {

            try {
                socket.connect();
                Message message=Message.obtain();
                message.what=ESTADO_CONECTADO;
                handler.sendMessage(message);

                sendReceive=new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what=ESTADO_CONEXION_FALLIDA;
                handler.sendMessage(message);
            }
        }

        public void cancel(){
            try{
                socket.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private class SendReceive extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive (BluetoothSocket socket)
        {
            bluetoothSocket=socket;
            InputStream tempIn=null;
            OutputStream tempOut=null;

            try {
                tempIn=bluetoothSocket.getInputStream();
                tempOut=bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream=tempIn;
            outputStream=tempOut;
        }

        public void run()
        {
            byte[] buffer=new byte[1024];
            int bytes;

            while (true)
            {
                try {
                    bytes=inputStream.read(buffer);
                    buffer[bytes] = '\0';
                    handler.obtainMessage(ESTADO_MSG_RECIBIDO,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException f) {
                        f.printStackTrace();
                    }

                    if (ganador == false){
                        Message message=Message.obtain();
                        message.what=ESTADO_CONEXION_FALLIDA;
                        handler.sendMessage(message);
                        break;
                    }
                    e.printStackTrace();

                }
            }
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel(){
            try {
                inputStream.close();
                outputStream.close();
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }

}

