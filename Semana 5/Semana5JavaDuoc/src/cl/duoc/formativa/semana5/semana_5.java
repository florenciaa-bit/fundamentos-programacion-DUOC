package cl.duoc.formativa.semana5;

import java.util.*;
import java.util.List;
import java.util.ArrayList;

public class semana_5 {

    private static int TOTAL_ENTRADAS_VENDIDAS = 0;
    private static double TOTAL_INGRESOS = 0.0;
    private static int TOTAL_ESTUDIANTES = 0;
    
    private final String nombreTeatro = "Teatro Moro";
    private final int FILAS = 4, COLS = 6;
    private final boolean [][] ocupado = new boolean[FILAS][COLS];
    
    private final Map<Character, String>sectorPorFila = Map.of(
        'A', "VIP",
        'B', "Platea Baja",  
        'C', "Platea Alta",  
        'D', "General"
    );
    private final Map<Character, Integer>precioPorFila = Map.of(
        'A', 300000,
        'B', 220000, 
        'C', 180000, 
        'D', 120000
    );
    
    private final int capacidadSala = FILAS * COLS;
    private int entradasDisponibles = capacidadSala;
    
    private final List<Venta> ventas = new ArrayList<>();
    private int secuenciaVenta = 1000;
    
    private final Scanner sc = new Scanner(System.in);
    
    private static class Ticket {
        int personaN;
        String personaNombre;
        String asiento;
        String sector;
        int precioBase;
        boolean estudiante;
        boolean tercera;
        double totalIndividual;
        
        public String etiquetaCaso() {
        if (tercera) return "Adulto mayor (15%)";
        if (estudiante) return "Estudiante (10%)";
        return "Sin descuento";
                
      }
    }
    
    private static class Venta {
        int numero;
        String tipo;
        String nombreGrupo;
        boolean aplicoPromosSector;
        List<Ticket> tickets = new ArrayList<>();
        double descuentoSector;
        double totalFinal;
        List<String> detallePromos = new ArrayList<>();
    }
    
    public void ejecutar(){
        boolean seguir = true;
        while (seguir){
        limpiar();
        System.out.println("==========MENU PRINCIPAL  - " + nombreTeatro + "==========");
        System.out.println("1) Comprar entrada");
        System.out.println("2) Salir");
        System.out.println("Seleccione una opcion: ");
        int op = leerEnteroSeguro();
        
        if (op == 1){
            menuCompra();
        }else if (op == 2){
            System.out.println("Gracias por usar el sistema!");
            seguir = false;
        } else {
            System.out.println("Opcion invalida.");
            pausar();
         }
        }
    }
    
    private void menuCompra(){
        boolean volver = false;
        while (!volver) {
            limpiar();
            System.out.println("===== SISTEMA DE COMPRAS - " + nombreTeatro + " =====");
            System.out.println("1) Venta de entradas");
            System.out.println("2) Promociones");
            System.out.println("3) Busqueda de entradas");
            System.out.println("4) Eliminacion de entradas");
            System.out.println("5) Volver");
            System.out.print("Seleccione: ");
            int op = leerEnteroSeguro();
            
            switch (op) { 
                case 1 -> flujoVenta();
                case 2 -> mostrarPromociones();
                case 3 -> buscarVentas();
                case 4 -> eliminarVenta();
                case 5 -> volver = true;
                default -> { System.out.println("Opcion invalida."); pausar();}
                
            }
        }
    }
    
    private void flujoVenta() {
        limpiar();
        System.out.println("=== VENTA DE ENTRADAS - " + nombreTeatro + " ===");
        System.out.println("Disponibles: " + entradasDisponibles + "/" + capacidadSala);
        if (entradasDisponibles <= 0) { System.out.println("No hay disponibilidad."); pausar(); return; }

        System.out.print("Cuantas entradas desea comprar? (1-10): ");
        int n = leerRango(1, 10);
        if (n > entradasDisponibles) { System.out.println("No hay tantas disponibles."); pausar(); return; }
        
        if (n == 1) {
            ventaIndividual();
        } else {
            boolean aplicarPromosSector = preguntaSi ("Desea usar promociones por sector? (SI/NO): ");
            ventaGrupal(n, aplicarPromosSector);
        }
    }
    
     private void ventaIndividual() {
        sc.nextLine();
        System.out.print("Nombre de la persona: ");
        String nombre = sc.nextLine().trim();

        Ticket t = seleccionarAsientoYDescuentos(1, nombre);
        if (t == null) return;

        Venta v = new Venta();
        v.numero = ++secuenciaVenta;
        v.tipo = "Individual";
        v.aplicoPromosSector = false;
        v.tickets.add(t);
        v.descuentoSector = 0.0;
        v.totalFinal = t.totalIndividual;

        ventas.add(v);
        entradasDisponibles -= 1;
        actualizarEstadisticasPostVenta(v);

        imprimirBoleta(v);
    }

     private void ventaGrupal(int n, boolean aplicarPromosSector){
         System.out.print("Nombre del grupo: ");
         String grupo = sc.nextLine(). trim();
         
         List<Ticket> tickets = new ArrayList<>();
         for (int i = 1; i <= n; i++) {
         System.out.println("\n=== VENTA DE ENTRADAS - Persona #" + i + "===") ;
         System.out.print("Nombre: ");
         String nombre = sc.nextLine(). trim();
         
         Ticket t = seleccionarAsientoYDescuentos(i, nombre);
         if (t == null) {System.out.println("Compra cancelada."); return; }
         tickets.add(t);
         }
    
     
     double subtotalIndividual = tickets.stream().mapToDouble(t -> t.totalIndividual).sum();
    
     double ahorroSector = 0.0;
        List<String> detallePromos = new ArrayList<>();
        
        if (aplicarPromosSector) {
        Map<String, List<Ticket>> porSector = new HashMap<>();
        for (Ticket t : tickets) porSector.computeIfAbsent(t.sector, k -> new ArrayList<>()).add(t);
        
        for (Map.Entry<String, List<Ticket>> e : porSector.entrySet()) {
                String sector = e.getKey();
                List<Ticket> lista = e.getValue();
                int cuenta = lista.size();
                double subSector = lista.stream().mapToDouble(t -> t.totalIndividual).sum();
                double promo = 0.0;
                if (cuenta >= 4) { promo = 0.10; detallePromos.add(sector + ": " + cuenta + " asientos → 10%"); }
                else if (cuenta >= 2) { promo = 0.05; detallePromos.add(sector + ": " + cuenta + " asientos → 5%"); }
                double descuento = subSector * promo;
                ahorroSector += descuento;
        }
    }
        Venta v = new Venta();
        v.numero = ++secuenciaVenta;
        v.tipo = "Grupal";
        v.nombreGrupo = grupo;
        v.aplicoPromosSector = aplicarPromosSector;
        v.tickets = tickets;
        v.descuentoSector = ahorroSector;
        v.totalFinal = subtotalIndividual - ahorroSector;
        v.detallePromos = detallePromos;

        ventas.add(v);
        entradasDisponibles -= n;
        actualizarEstadisticasPostVenta(v);

        imprimirBoleta(v);
    }    
      
    private Ticket seleccionarAsientoYDescuentos(int personaN, String nombrePersona){
        while (true) {
        imprimirPlano();
            System.out.print("Seleccione asiento (A1..D6) o 'X' para cancelar: ");
            String code = sc.next().trim().toUpperCase();
            sc.nextLine();
            if (code.equals("X")) return null;   
            
            if (!validaAsiento(code)) { System.out.println("Asiento invalido."); continue; }
            int fila = code.charAt(0) - 'A';
            int col = Integer.parseInt(code.substring(1)) - 1;
            if (ocupado[fila][col]) { System.out.println("Asiento ocupado."); continue; }
            
            ocupado[fila][col] = true;
            
            char filaChar = (char)('A' + fila);
            String sector = sectorPorFila.get(filaChar);
            int base = precioPorFila.get(filaChar);
            
            System.out.print("Edad (0-120): ");
            int edad = leerRango(0, 120);
            sc.nextLine();
            boolean tercera = (edad >= 60);
            
            
            boolean estudiante = false;
            if (!tercera) {
                estudiante = preguntaSi("Es estudiante? (SI/NO): ");
            }
            
           double factor = 1.0;
           if (tercera) factor *= 0.85; 
           else if (estudiante) factor *= 0.90;
           
           Ticket t = new Ticket();
            t.personaN = personaN;
            t.personaNombre = nombrePersona;
            t.asiento = code;
            t.sector = sector;
            t.precioBase = base;
            t.estudiante = estudiante;
            t.tercera = tercera;
            t.totalIndividual = base * factor;
            return t;
        }
    }
  
    private void buscarVentas(){
        boolean volverMenuCompras = false;
        while (!volverMenuCompras) {
            limpiar();
            System.out.println("=== BUSQUEDA DE ENTRADAS - " + nombreTeatro + " ===");
            if (ventas.isEmpty()) {
                System.out.println("No hay ventas registradas.");
                volverMenuCompras = preguntaSi("\n¿Desea volver al menú de compras? (SI/NO): ");
                continue;
            }
            
            System.out.println("Buscar por:");
            System.out.println("1) N de venta");
            System.out.println("2) Ubicacion/Sector (VIP/Platea Baja/Platea Alta/General)");
            System.out.println("3) Tipo de persona (E=estudiante / T=tercera edad)");
            System.out.print("Opcion: ");
            int op = leerEnteroSeguro();
            
            List<Venta> res = new ArrayList<>();
            
            switch (op) {
                case 1 -> {
                    System.out.print("N venta: ");
                    int n = leerEnteroSeguro();
                    for (Venta v : ventas) if (v.numero == n) res.add(v);
                }
                 case 2 -> {
                    sc.nextLine();
                    System.out.print("Sector: ");
                    String s = sc.nextLine().trim();
                    for (Venta v : ventas)
                        for (Ticket t : v.tickets)
                            if (t.sector.equalsIgnoreCase(s)) { res.add(v); break; }
                }
                case 3 -> {
                    System.out.print("Tipo (E/T): ");
                    String t = sc.next().trim().toUpperCase();
                    for (Venta v : ventas)
                        for (Ticket tk : v.tickets) {
                            if (t.equals("E") && tk.estudiante) { res.add(v); break; }
                            if (t.equals("T") && tk.tercera) { res.add(v); break; }
                        }
            }
           
                default -> System.out.println("Opcion invalida");
            }
            if (res.isEmpty()) {
                System.out.println("\nSin coincidencias.");
            } else {
                for (Venta v : res) imprimirBoleta(v);
            }
            
            volverMenuCompras = preguntaSi("\nDesea volver al menú de compras? (SI/NO): "); 
        }
    }
    
    private void eliminarVenta() {
        limpiar();
        System.out.println("=== ELIMINAR ENTRADA - " + nombreTeatro + " ===");
        if (ventas.isEmpty()) { System.out.println("No hay ventas."); pausar(); return; }
        System.out.print("N° de venta a eliminar: ");
        int n = leerEnteroSeguro();
        
        Venta encontrada = null;
        for (Venta v : ventas) if (v.numero == n) { encontrada = v; break; }
        if (encontrada == null) { System.out.println("No existe."); pausar(); return; }
        
        for (Ticket t : encontrada.tickets) {
            int fila = t.asiento.charAt(0) - 'A';
            int col = Integer.parseInt(t.asiento.substring(1)) - 1;
            ocupado[fila][col] = false;
        }
        
        TOTAL_ENTRADAS_VENDIDAS -= encontrada.tickets.size();
        TOTAL_INGRESOS -= encontrada.totalFinal;
        for (Ticket t : encontrada.tickets) if (t.estudiante) TOTAL_ESTUDIANTES--;

        entradasDisponibles += encontrada.tickets.size();
        ventas.remove(encontrada);
        System.out.println("Venta #" + n + " eliminada.");
        pausar();
    }
    
    private void mostrarPromociones() {
        limpiar();
        System.out.println("=== PROMOCIONES - " + nombreTeatro + " ===");
        System.out.println("* Descuentos individuales (aplican en compras individuales y grupales):");
        System.out.println("   - Estudiantes: 10%.");
        System.out.println("   - Tercera edad (>=60): 15%.");
        System.out.println("\n* Descuentos por sector (solo si el cliente decide usarlos, en compras grupales):");
        System.out.println("   - 2 o 3 asientos en el mismo sector = 5% al subtotal de ese sector.");
        System.out.println("   - 4 o mas asientos en el mismo sector = 10% al subtotal de ese sector.");
        System.out.println("\nSectores y precios:");
        System.out.println("   A: VIP           $" + precioPorFila.get('A'));
        System.out.println("   B: Platea Baja  $" + precioPorFila.get('B'));
        System.out.println("   C: Platea Alta  $" + precioPorFila.get('C'));
        System.out.println("   D: General      $" + precioPorFila.get('D'));
        pausar();
    }
    
      private void imprimirBoleta(Venta v) {
        limpiar();
        System.out.println("================================================");
        System.out.println(" " + nombreTeatro + " - BOLETA #" + v.numero + " (" + v.tipo + ")");
        if ("Grupal".equals(v.tipo)) System.out.println(" Grupo: " + v.nombreGrupo);
        System.out.println("================================================");
        System.out.printf("%-9s %-20s %-10s %-18s %-10s%n",
                "Persona","Asiento/Sector","Base","Caso (descuento)","Total");
        System.out.println("------------------------------------------------");
        for (Ticket t : v.tickets) {
            String personaTag = "N*" + t.personaN + " " + t.personaNombre;
            System.out.printf("%-9s %-20s $%-9d %-18s $%-9.0f%n",
                    personaTag, t.asiento + " / " + t.sector, t.precioBase, t.etiquetaCaso(), t.totalIndividual);
        }
        double subtotalIndividual = v.tickets.stream().mapToDouble(t -> t.totalIndividual).sum();
        System.out.println("------------------------------------------------");
        System.out.printf("Subtotal (indiv):     $%.0f%n", subtotalIndividual);
        System.out.printf("Descuento sector:     $%.0f %s%n",
                v.descuentoSector, v.aplicoPromosSector ? "" : "(no aplicado)");

        if (!v.detallePromos.isEmpty()) {
            System.out.println("Detalle promociones:");
            for (String d : v.detallePromos) System.out.println(" - " + d);
        }

        System.out.println("------------------------------------------------");
        System.out.printf("TOTAL A PAGAR:        $%.0f%n", v.totalFinal);
        System.out.println("Entradas compradas:   " + v.tickets.size());
        String listaAsientos = String.join(",", v.tickets.stream().map(t -> t.asiento).toList());
        System.out.println("Asientos comprados:   " + listaAsientos);
        System.out.println("================================================\n");
        pausar();
    }
      
      private void imprimirPlano() {
        System.out.println("\n--- PLANO (X=ocupado) ---");
        for (int f = 0; f < FILAS; f++) {
            char row = (char) ('A' + f);
            System.out.print(row + " ");
            for (int c = 0; c < COLS; c++) {
                System.out.print(ocupado[f][c] ? "[X]" : "[ ]");
            }
            String sector = sectorPorFila.get(row);
            int precio = precioPorFila.get(row);
            System.out.println("   " + sector + " ($" + precio + ")");
        }
        System.out.println("   1  2  3  4  5  6\n");
    }
      
      private boolean validaAsiento(String code) {
        if (code.length() < 2 || code.length() > 3) return false;
        char filaC = code.charAt(0);
        if (filaC < 'A' || filaC > 'D') return false;
        int col;
        try { col = Integer.parseInt(code.substring(1)); }
        catch (NumberFormatException e) { return false; }
        return col >= 1 && col <= COLS;
    }
      
      private int leerEnteroSeguro() {
        while (true) {
            if (sc.hasNextInt()) return sc.nextInt();
            System.out.print(" (numero) >> ");
            sc.next();
        }
    }
      
       private int leerRango(int min, int max) {
        int x;
        do {
            x = leerEnteroSeguro();
            if (x < min || x > max) System.out.print(" Fuera de rango [" + min + "-" + max + "], reintente: ");
        } while (x < min || x > max);
        return x;
    }
       
      private boolean preguntaSi(String prompt) {
    System.out.print(prompt);
    String r = sc.next().trim().toLowerCase(); // lee "si" / "no"
    sc.nextLine(); // <-- limpia el salto de línea pendiente
    return r.equals("si") || r.equals("s");
    }
 
       
       private void actualizarEstadisticasPostVenta(Venta v) {
        TOTAL_INGRESOS += v.totalFinal;
        TOTAL_ENTRADAS_VENDIDAS += v.tickets.size();
        for (Ticket t : v.tickets) if (t.estudiante) TOTAL_ESTUDIANTES++;
    }
       
       private void pausar() {
        System.out.print("(Enter para continuar) ");
        sc.nextLine();
    }
       
       private void limpiar() {
        System.out.print("\033[H\033[2J"); System.out.flush();
    }
    
}
