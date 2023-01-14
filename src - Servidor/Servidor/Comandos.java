package Servidor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Comandos {

    private static String currentDatabase = "";

    private static String obtenerPrimerToken(String comando) {
        return comando.split(" ")[0];
    }

    private static String obtenerSegundoToken(String comando) {
        return comando.split(" ")[1];
    }

    private static String obtenerTercerToken(String comando) {
        return comando.split(" ")[2];
    }

    public static void ejecutarComando(String comando) {
        System.out.println(comando);
        Map<String, Runnable> acciones = new HashMap<>();
        acciones.put("create_database", () -> {
            String nombre = comando.substring(comando.indexOf(" ") + 1);
            System.out.println(nombre);
            crearBaseDeDatos(nombre);
        });
        acciones.put("drop_database", () -> {
            String nombreBD = comando.substring(comando.indexOf(" ") + 1);
            eliminarBaseDeDatos(nombreBD);
        });
        acciones.put("use_database", () -> seleccionarBaseDeDatos(comando));
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
        String ruta = "C:\\Users\\Carlos\\Documents\\BD";
        File carpetaBD = new File(ruta+"\\"+nombreBD);
        if (!carpetaBD.exists()) {
            carpetaBD.mkdir();
            System.out.println("La base de datos " + nombreBD + " ha sido creada exitosamente.");
        } else {
            System.out.println("Error: la base de datos " + nombreBD + " ya existe.");
        }
    }

    private static void eliminarBaseDeDatos(String nombreBD) {
        File carpetaBD = new File(nombreBD);
        if (carpetaBD.exists()) {
            carpetaBD.delete();
            System.out.println("La base de datos " + nombreBD + " ha sido eliminada exitosamente.");
        } else {
            System.out.println("Error: la base de datos " + nombreBD + " no existe.");
        }
    }

    private static void seleccionarBaseDeDatos(String comando) {
        String nombreBD = obtenerSegundoToken(comando);
        currentDatabase = nombreBD;
    }

    private static void crearTabla(String comando) {
        String nombreTabla = obtenerSegundoToken(comando);
        String atributos = obtenerTercerToken(comando);
        File tabla = new File(currentDatabase + "\\" + nombreTabla + ".txt");
        try {
            tabla.createNewFile();
            try (FileWriter escritor = new FileWriter(tabla)) {
                escritor.write(atributos);
            }
            System.out.println("Tabla " + nombreTabla + " creada en la base de datos " + currentDatabase);
        } catch (IOException e) {
            System.out.println("Error al crear la tabla: " + e.getMessage());
        }
    }

    private static void agregarAtributos(String comando) {
        try {
            String nombreTabla = obtenerSegundoToken(comando);
            String nuevosAtributos = obtenerTercerToken(comando);
            File tabla = new File(currentDatabase + "\\" + nombreTabla + ".txt");
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
        File tabla = new File(currentDatabase + "\\" + nombreTabla + ".txt");
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
        String rutaTabla = currentDatabase + "\\" + nombreTabla + ".txt";
        try {
            FileWriter fw = new FileWriter(rutaTabla, true);
            fw.write(valores + "\n"); 
            fw.close();
        } catch (IOException e) {
            System.out.println("Error al insertar en la tabla " + nombreTabla + ": " + e.getMessage());
        }
    }

}
