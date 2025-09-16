
package cl.duoc.formativa.semana4;

import java.util.InputMismatchException;
import java.util.Scanner;

public class semana_4 {
    
    private static final int FILAS = 4;
    private static final int COLUMNAS = 6;
    private static final boolean[][] ocupado = new boolean[FILAS][COLUMNAS];
    
    private static final int PRECIO_A = 20000;
    private static final int PRECIO_B = 18000;
    private static final int PRECIO_C = 14000;
    private static final int PRECIO_D = 12000;
    
    public static void ejecutar() {
        Scanner sc = new Scanner(System.in);
        boolean seguir = true;
        
        while (seguir){
            limpiarConsola();
            imprimirBanner("TEATRO MORO - Sistema de Entradas");
            
            String[] menu = { "1) Comprar entrada", "2) Salir" };
            System.out.println("Menu principal:");
            for (int i = 0; i < menu.length; i++){
                System.out.println(menu[i]);
            }
            System.out.print("Seleccione una opcion: ");
            int opcion = leerEnteroSeguro(sc);
            
            if (opcion == 1){
                procesarCompra(sc);
                
             System.out.print("\n¿Desea realizar otra compra? (S/N): ");
             String otra = sc.next().trim().toUpperCase();
             seguir = otra.equals("S");
            } else if (opcion == 2){
                seguir =false;
            } else {
                System.out.println("Opcion invalida. Presione Enter para continuar...");
                sc.nextLine(); sc.nextLine();

            }
        }
        
        System.out.println("\nGracias por usar el sistema. Hasta pronto!" );
        
    }
    
    private static void procesarCompra(Scanner sc) {
        sc.nextLine();
        System.out.print("\nIngrese su nombre: ");
        String nombre = sc.nextLine().trim();
        
        Seleccion sel = seleccionarAsiento(sc);
        
        int edad;
        do{ 
            System.out.print("Ingrese su edad: ");
            edad = leerEnteroSeguro(sc);
            
            if (edad < 0 || edad > 120){
            System.out.println("Edad no valida, intente nuevamente.");
            }  
        }while (edad < 0 || edad > 120);
        
        System.out.print("Es estudiante? (S/N): ");
        boolean esEstudiante = sc.next().trim().equalsIgnoreCase("S");
        
        double descuento = 0.0;
        String etiquetaDescuento = "Sin descuento";
        if (edad >= 60){
            descuento = 0.15;
            etiquetaDescuento = "15% tercera edad";
        } else if (esEstudiante){
            descuento = 0.10;
            etiquetaDescuento = "10% estudiante";
        }
        
        int cantidad;
        do{
            System.out.print("Ingrese cantidad de entradas (1-10): ");
            cantidad = leerEnteroSeguro(sc);
            if (cantidad < 1 || cantidad > 10) {
                System.out.println("Cantidad invalida. Intente nuevamente.");
            }
        } while (cantidad < 1 || cantidad > 10);
        
        double precioUnitario = sel.precioBase * (1.0 - descuento);
        double total = 0.0;
        int cont = 0;
        
        do{
            total += precioUnitario;
            cont++;
        } while (cont < cantidad);
        
        imprimirResumen( 
        nombre,
        sel.asientoCodigo,
        sel.precioBase,
        etiquetaDescuento,
        cantidad,
        total
        );   
    }
    
    private static class Seleccion {
    String asientoCodigo;
    int precioBase;
}
    
    private static Seleccion seleccionarAsiento(Scanner sc){
        Seleccion sel = new Seleccion();
        
        while (true){
            imprimirPlanoConPrecios();
            System.out.print("Seleccione asiesto (ej: A1, B3, C6): ");
            String entrada = sc.next().trim().toUpperCase();
            
            if (entrada.length() < 2){
            System.out.println("Formato invalido. Intente nuevamente.");
            continue;  
            }
            
            char zona = entrada.charAt(0);
            int fila = filaIndex(zona);
            String numeroTxt = entrada.substring(1);
            
            
            int nro;
            try {
                nro = Integer.parseInt(numeroTxt);
            } catch (NumberFormatException e){
                System.out.println("Numero de asientos invalido.");
                continue;
            }
            
            int col = nro - 1;
            if (ocupado[fila][col]){
                System.out.println("Ese asiento ya esta ocupado. Elija otro.");
                continue;
            }
            
            ocupado[fila][col] = true;
            sel.asientoCodigo = ("" + zona + nro);
            sel.precioBase = precioPorZona(zona);
            
            System.out.println("Asiento " + sel.asientoCodigo + " reservado por usted. Precio base: $" + sel.precioBase + "\n");
            break;
            
        }
        
        return sel;   
    }
    
    private static void imprimirPlanoConPrecios(){
        System.out.println("\n----- PLANO DEL TEATRO -----   (X = ocupado)");
        for (int f = 0; f < FILAS; f++) {
            char letra = (char) ('A' + f);
            System.out.print(letra + "  ");
            for (int c = 0; c < COLUMNAS; c++) {
                System.out.print("[" + (ocupado[f][c] ? "X" : " ") + "]");
            }
            System.out.println("   ($" + precioPorZona(letra) + ")");
        }
        System.out.println("    1  2  3  4  5  6\n");
    }
    
    private static int precioPorZona(char zona) {
        zona = Character.toUpperCase(zona);
        if (zona == 'A') return PRECIO_A;
        else if (zona == 'B') return PRECIO_B;
        else if (zona == 'C') return PRECIO_C;
        else if (zona == 'D') return PRECIO_D;
        else return 0;
    }
    
    private static int filaIndex(char letra) {
        letra = Character.toUpperCase(letra);
        if (letra == 'A') return 0;
        if (letra == 'B') return 1;
        if (letra == 'C') return 2;
        if (letra == 'D') return 3;
        return -1;
    }
    
    private static int leerEnteroSeguro(Scanner sc) {
        while (true) {
            try {
                return sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.print("Entrada invalida. Ingrese un numero entero: ");
                sc.next();
                
            }
        }
    }
    
    private static void imprimirResumen(String nombre,
            String asiento, int precioBaseUnidad, String descuentoTxt,
            int cantidad, double total) {
        
        System.out.println("\n=========== RESUMEN DE LA COMPRA ===========");
        System.out.println("Cliente:               " + nombre);
        System.out.println("Ubicacion del asiento: " + asiento);
        System.out.println("Precio base unidad:    $" + precioBaseUnidad);
        System.out.println("Cantidad:              " + cantidad);
        System.out.println("Descuento aplicado:    " + descuentoTxt);
        System.out.println("--------------------------------------------");
        System.out.println("Precio final a pagar:  $" + Math.round(total));
        System.out.println("============================================\n");

    }
    
    private static void imprimirBanner(String titulo) {
        System.out.println("============================================");
        System.out.println(" " + titulo);
        System.out.println("============================================\n");
    }
    
    private static void limpiarConsola(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

}
