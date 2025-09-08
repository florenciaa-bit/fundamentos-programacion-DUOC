package cl.duoc.sumativajava.codigo;

import java.util.Scanner;

public class Sumativa1 {
    public static void ejecutar(){
       Scanner sc = new Scanner(System.in);
       
        System.out.print("Bienvenido a Ticket.cl, por favor ingrese su nombre: ");
        String nombre = sc.nextLine();   
        
        int tipo;
        boolean entradaValida;
        String tipoEntrada = "";
        int precioEstudiante = 0;    
        int precioGeneral = 0;
        
        do {
            System.out.println("Hola " + nombre + ", por favor seleccione su tipo de entrada:");
            System.out.println("1) VIP");
            System.out.println("2) Platea Baja");
            System.out.println("3) Platea Alta");
            System.out.println("4) Palco");
            tipo = sc.nextInt();
            
            entradaValida = (tipo >=1  && tipo <=4);
            
            if (!entradaValida) {
            System.out.println("Opcion invalida. Intente nuevamente.");
            }  
         } while (!entradaValida);
             
        switch (tipo) {
            case 1: 
                tipoEntrada = "VIP";
                precioEstudiante = 20000;
                precioGeneral = 35000;
                break;
            case 2:
                tipoEntrada = "Platea Baja";
                precioEstudiante = 15000;
                precioGeneral = 25000;
                break;
            case 3:
                tipoEntrada = "Platea Alta";
                precioEstudiante = 10000;
                precioGeneral = 15000;
                break;
            case 4:
                tipoEntrada = "Palco";
                precioEstudiante = 5000;
                precioGeneral = 11000;
                break;  
        }
    
        int tarifa;
        boolean tarifaValida;
        String textoTarifa;
        boolean esEstudiante;
        
        do {
            System.out.println("Por favor seleccione la Tarifa:");
            System.out.println("1) Estudiante");
            System.out.println("2) Publico General");
            tarifa = sc.nextInt();
            
            tarifaValida = (tarifa >= 1 && tarifa <= 2);
            if (!tarifaValida){
                System.out.println("Opcion invalida. Intente nuevamente.");
            }
        } while (!tarifaValida);
        
        int precioBase;
        if (tarifa == 1) {
            esEstudiante = true;
            textoTarifa = "Estudiante";
            precioBase = precioEstudiante;
        } else {
            esEstudiante = false;
            textoTarifa = "Publico General";
            precioBase = precioGeneral;
        }
        
        int total = precioBase;
        
        System.out.println("Tipo de entrada: " + tipoEntrada);
        System.out.println("Tarifa = " + textoTarifa);
        System.out.println("Total = " + total);
        System.out.println("Gracias por su compra, disfrute la funcion.");
        
     
    }
    
}
