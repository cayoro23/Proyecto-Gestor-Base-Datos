package Servidor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Comandos {

    private static String currentDatabase = "";
    private static final String ruta = "C:\\Users\\Carlos\\Documents\\BD";
    private static final Map<String, Runnable> acciones = new HashMap<>();

    private static String obtenerPrimerToken(String comando) {
        if (comando.trim().isEmpty() || !comando.contains(" ")) {
            return "";
        }
        System.out.println(comando.split(" ")[0]);
        return comando.split(" ")[0];
    }

    private static String obtenerSegundoToken(String comando) {
        if (comando.split(" ").length < 2) {
            return "";
        }
        System.out.println(comando.split(" ")[1]);
        return comando.split(" ")[1];
    }

    private static String obtenerTercerToken(String comando) {
        return comando.split(" ")[2];
    }

    public static void ejecutarComando(String comando) {
        System.out.println(comando);
        acciones.put("create_database", () -> {
            String nombreBD = comando.substring(comando.indexOf(" ") + 1);
            crearBaseDeDatos(nombreBD);
        });
        acciones.put("drop_database", () -> {
            String nombreBD = comando.substring(comando.indexOf(" ") + 1);
            eliminarBaseDeDatos(nombreBD);
        });
        acciones.put("use_database", () -> {
            seleccionarBaseDeDatos(comando);
            System.out.println("Estás dentro de la base de datos " + currentDatabase);
        });

        acciones.put("create_table", () -> crearTabla(comando));
        acciones.put("add_column", () -> agregarAtributos(comando));
        acciones.put("drop_table", () -> eliminarTabla(comando));
        acciones.put("insert", () -> insertarRegistro(comando));

        Runnable accion = acciones.get(obtenerPrimerToken(comando));
        if (accion != null) {
            accion.run();
        } else {
            System.out.println("Comando no reconocido");
        }

    }

    private static void crearBaseDeDatos(String nombreBD) {
        File carpetaBD = new File(ruta + "\\" + nombreBD);
        if (!carpetaBD.exists()) {
            carpetaBD.mkdir();
            System.out.println("La base de datos " + nombreBD + " ha sido creada exitosamente.");
        } else {
            System.out.println("Error: la base de datos " + nombreBD + " ya existe.");
        }
    }

    private static void eliminarBaseDeDatos(String nombreBD) {
        File carpetaBD = new File(ruta + "\\" + nombreBD);
        if (carpetaBD.exists()) {
            carpetaBD.delete();
            System.out.println("La base de datos " + nombreBD + " ha sido eliminada exitosamente.");
        } else {
            System.out.println("Error: la base de datos " + nombreBD + " no existe.");
        }
    }

    private static void seleccionarBaseDeDatos(String comando) {
        String nombreBD = obtenerSegundoToken(comando);
        if (nombreBD != null) {
            currentDatabase = nombreBD;
            System.out.println("Estás dentro de la base de datos " + currentDatabase);
        } else {
            System.out.println("Error en el comando, verifique la sintaxis");
        }
    }

    private static void crearTabla(String comando) {
        if (currentDatabase.isEmpty()) {
            System.out.println("Error: debe seleccionar una base de datos antes de crear una tabla.");
            return;
        }
        String[] tokens = comando.split(" ");
        String nombreTabla = obtenerSegundoToken(comando);
        String contenidoTabla = comando.substring(comando.indexOf(nombreTabla) + nombreTabla.length());
        File tabla = new File(ruta + "\\" + currentDatabase + "\\" + nombreTabla + ".txt");
        System.out.println(tabla);
        if (tabla.exists()) {
            System.out.println("Error: la tabla " + nombreTabla + " ya existe en la base de datos " + currentDatabase);
        } else {
            try {
                tabla.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(tabla));
                bw.write(contenidoTabla);
                bw.close();
                System.out.println("La tabla " + nombreTabla + " ha sido creada exitosamente en la base de datos " + currentDatabase);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void agregarAtributos(String comando) {
        try {
            String nombreTabla = obtenerSegundoToken(comando);
            String nuevosAtributos = obtenerTercerToken(comando);
            File tabla = new File(ruta + "\\" + currentDatabase + "\\" + nombreTabla + ".txt");
            if (!tabla.exists()) {
                System.out.println("La tabla no existe en la base de datos seleccionada");
                return;
            }
            FileWriter fw = new FileWriter(tabla, true);
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(nuevosAtributos);
                bw.newLine();
            }
            System.out.println("Atributos agregados exitosamente a la tabla " + nombreTabla);
        } catch (IOException e) {
            System.out.println("Error al agregar atributos a la tabla: " + e.getMessage());
        }
    }

    private static void eliminarTabla(String comando) {
        String nombreTabla = obtenerSegundoToken(comando);
        File tabla = new File(ruta + "\\" + currentDatabase + "\\" + nombreTabla + ".txt");
        try {
            if (tabla.delete()) {
                System.out.println("La tabla " + nombreTabla + " ha sido eliminada.");
            } else {
                System.out.println("La tabla " + nombreTabla + " no pudo ser eliminada.");
            }
        } catch (Exception e) {
            System.out.println("Error al eliminar la tabla " + nombreTabla + ": " + e.getMessage());
        }
    }

    private static void insertarRegistro(String comando) {
        String nombreTabla = obtenerSegundoToken(comando);
        String valores = obtenerTercerToken(comando);
        String rutaTabla = ruta + "\\" + currentDatabase + "\\" + nombreTabla + ".txt";
        try {
            FileWriter fw = new FileWriter(rutaTabla, true);
            fw.write(valores + "\n");
            fw.close();
        } catch (IOException e) {
            System.out.println("Error al insertar en la tabla " + nombreTabla + ": " + e.getMessage());
        }
    }

}
