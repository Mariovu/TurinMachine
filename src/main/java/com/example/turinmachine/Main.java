package com.example.turinmachine;

import java.util.Scanner;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("¿Deseas (1) ingresar una cadena o (2) generar aleatoriamente una válida?");
        String option = scanner.nextLine();

        if (option.equals("2")) {
            // Generar cadena aleatoria del tipo 0^n1^n
            int n = (int)(Math.random() * 10 + 1); // n entre 1 y 10
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
            tm.simular("salida.txt");
            System.out.println("Resultado guardado en salida.txt");
        } catch (Exception e) {
            System.err.println("Error durante la simulación: " + e.getMessage());
        }
    }
}
