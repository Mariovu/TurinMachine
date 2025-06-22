package com.example.turinmachine;

import java.util.List;
import java.util.Scanner;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private static String input;
    private static List<String> pasos;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("¿Deseas (1) ingresar una cadena o (2) generar aleatoriamente una válida?");
        String option = scanner.nextLine();

        if (option.equals("2")) {
            int n = (int)(Math.random() * 10 + 1);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) sb.append('0');
            for (int i = 0; i < n; i++) sb.append('1');
            input = sb.toString();
            System.out.println("Cadena generada: " + input);
        } else {
            System.out.println("Ingrese la cadena (solo 0s y 1s, máximo 1000 caracteres):");
            input = scanner.nextLine();

            if (input.length() > 1000) {
                System.out.println("Error: La cadena excede los 1000 caracteres");
                return;
            }
            if (!input.matches("[01]*")) {
                System.out.println("Error: La cadena solo puede contener 0s y 1s");
                return;
            }
        }

        MaquinaTuring tm = new MaquinaTuring(input);
        try {
            pasos = tm.simular("salida.txt");
            System.out.println("Resultado guardado en salida.txt");

            if (input.length() <= 10) {
                launch(args); // Lanzar animación JavaFX
            }
        } catch (Exception e) {
            System.err.println("Error durante la simulación: " + e.getMessage());
        }
    }

    @Override
    public void start(Stage primaryStage) {
        TuringAnimation animation = new TuringAnimation(pasos);
        animation.start(primaryStage);
    }

}