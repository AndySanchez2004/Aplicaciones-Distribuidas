package ec.edu.uteq.distribuidas;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class NodoDistribuido {

    private final int id;
    private final int puerto;

    private int relojLamport = 0;

    private final Set<Integer> nodosActivos = new HashSet<>();
    private final Map<Integer, Integer> puertos = new HashMap<>();

    private final AtomicBoolean coordinador = new AtomicBoolean(false);
    private volatile boolean activo = true;

    public NodoDistribuido(int id, int puerto) {
        this.id = id;
        this.puerto = puerto;

        puertos.put(1, 9001);
        puertos.put(2, 9002);
        puertos.put(3, 9003);

        nodosActivos.add(1);
        nodosActivos.add(2);
        nodosActivos.add(3);
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Error: Faltan argumentos.");
            System.err.println("Uso: java NodoDistribuido <id_nodo> <puerto>");
            System.exit(1);
        }

        int id = Integer.parseInt(args[0]);
        int puerto = Integer.parseInt(args[1]);

        new NodoDistribuido(id, puerto).start();
    }

    public void start() {
        System.out.println("Nodo " + id + " iniciado en puerto " + puerto);

        Executors.newSingleThreadExecutor().submit(this::heartbeatTask);

        Executors.newSingleThreadExecutor().submit(this::server);

        if (id == 3) {
            coordinador.set(true);
            System.out.println("Nodo " + id + " es COORDINADOR inicial");
        }
    }

    // ---------------- SERVER ----------------
    private void server() {
        try (ServerSocket ss = new ServerSocket(puerto)) {

            while (true) {
                Socket socket = ss.accept();
                new Thread(() -> handle(socket)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handle(Socket socket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {

            String msg;

            while ((msg = in.readLine()) != null) {

                // TOKEN
                if (msg.startsWith("TOKEN:")) {
                    if (!msg.split(":")[1].equals("123")) {
                        out.println("TOKEN INVALIDO");
                        continue;
                    }
                    out.println("OK AUTH");
                    continue;
                }

                // LAMPORT MESSAGE
                if (msg.startsWith("MSG")) {
                    relojLamport++;
                    out.println("NODO " + id + " ACK Lamport=" + relojLamport);
                }

                // HEARTBEAT
                if (msg.equals("HB")) {
                    nodosActivos.add(1);
                }

                // ELECTION (BULLY)
                if (msg.equals("ELECTION")) {
                    if (id > 1) {
                        out.println("OK");
                    }
                }

                if (msg.equals("COORDINATOR")) {
                    coordinador.set(true);
                }
            }

        } catch (Exception e) {
            System.out.println("Nodo " + id + " caído cliente");
        }
    }

    // ---------------- HEARTBEAT ----------------
    private void heartbeatTask() {
        while (true) {
            try {
                Thread.sleep(3000);

                for (int nodo : puertos.keySet()) {
                    if (nodo != id) {
                        send(nodo, "HB");
                    }
                }

            } catch (Exception ignored) {}
        }
    }

    // ---------------- SEND ----------------
    private void send(int nodo, String msg) {
        try (Socket s = new Socket("localhost", puertos.get(nodo));
             PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {

            out.println(msg);

        } catch (Exception e) {
            System.out.println("Nodo " + nodo + " caído detectado por " + id);

            nodosActivos.remove(nodo);

            if (coordinador.get() && nodo == 3) {
                startElection();
            }
        }
    }

    // ---------------- BULLY ----------------
    private void startElection() {

        System.out.println("Nodo " + id + " inicia ELECTION");

        for (int nodo : nodosActivos) {
            if (nodo > id) {
                send(nodo, "ELECTION");
            }
        }

        coordinador.set(true);
        System.out.println("Nodo " + id + " es nuevo COORDINADOR");
    }
}
