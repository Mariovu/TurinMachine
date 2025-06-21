package com.example.turinmachine;

import java.io.*;
import java.util.*;

class Transition {
    String nextState;
    char writeSymbol;
    char direction;

    public Transition(String nextState, char writeSymbol, char direction) {
        this.nextState = nextState;
        this.writeSymbol = writeSymbol;
        this.direction = direction;
    }
}

public class TuringMachine {
    private static final int TAPE_SIZE = 2000;
    private static final int CENTER = 1000;
    private static final char BLANK = 'B';

    private char[] tape;
    private int head;
    private String state;
    private int minUsed;
    private int maxUsed;
    private Map<String, Map<Character, Transition>> transitions;

    public TuringMachine(String input) {
        // Inicializar cinta
        tape = new char[TAPE_SIZE];
        Arrays.fill(tape, BLANK);

        // Escribir cadena de entrada en la cinta
        for (int i = 0; i < input.length(); i++) {
            tape[CENTER + i] = input.charAt(i);
        }

        // Inicializar variables
        head = CENTER;
        state = "q0";
        minUsed = CENTER;
        maxUsed = CENTER + input.length() - 1;

        // Construir tabla de transiciones
        buildTransitions();
    }

    private void buildTransitions() {
        transitions = new HashMap<>();

        // Transiciones para q0
        Map<Character, Transition> q0Map = new HashMap<>();
        q0Map.put('0', new Transition("q1", 'X', 'R'));
        q0Map.put('X', new Transition("q0", 'X', 'R'));
        q0Map.put('Y', new Transition("q3", 'Y', 'R'));
        transitions.put("q0", q0Map);

        // Transiciones para q1
        Map<Character, Transition> q1Map = new HashMap<>();
        q1Map.put('0', new Transition("q1", '0', 'R'));
        q1Map.put('1', new Transition("q2", 'Y', 'L'));
        q1Map.put('Y', new Transition("q1", 'Y', 'R'));
        transitions.put("q1", q1Map);

        // Transiciones para q2
        Map<Character, Transition> q2Map = new HashMap<>();
        q2Map.put('0', new Transition("q2", '0', 'L'));
        q2Map.put('X', new Transition("q0", 'X', 'R'));
        q2Map.put('Y', new Transition("q2", 'Y', 'L'));
        transitions.put("q2", q2Map);

        // Transiciones para q3
        Map<Character, Transition> q3Map = new HashMap<>();
        q3Map.put('Y', new Transition("q3", 'Y', 'R'));
        q3Map.put(BLANK, new Transition("q4", BLANK, 'R'));
        transitions.put("q3", q3Map);
    }

    private String getInstantDescription() {
        StringBuilder sb = new StringBuilder();
        for (int i = minUsed; i <= maxUsed; i++) {
            if (i == head) {
                sb.append("(").append(state).append(")");
            }
            sb.append(tape[i]);
        }
        return sb.toString();
    }

    public boolean simulate(String outputFile) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(outputFile))) {
            // Escribir descripción inicial
            String desc = getInstantDescription();
            out.println(desc);
            if (maxUsed - minUsed <= 10) {
                System.out.println(desc);
            }

            // Ejecutar pasos de la máquina
            while (true) {
                Map<Character, Transition> stateTransitions = transitions.get(state);
                if (stateTransitions == null) break;

                char currentSymbol = tape[head];
                Transition t = stateTransitions.get(currentSymbol);
                if (t == null) break;

                // Aplicar transición
                tape[head] = t.writeSymbol;

                // Mover cabeza
                if (t.direction == 'R') head++;
                else if (t.direction == 'L') head--;

                // Actualizar estado
                state = t.nextState;

                // Actualizar límites usados
                if (head < minUsed) minUsed = head;
                if (head > maxUsed) maxUsed = head;

                // Escribir descripción
                desc = getInstantDescription();
                out.println(desc);
                if (maxUsed - minUsed <= 10) {
                    System.out.println(desc);
                }

                // Verificar estado de aceptación
                if (state.equals("q4")) break;
            }

            // Determinar si la cadena fue aceptada
            boolean accepted = state.equals("q4");
            String result = accepted ? "Aceptada" : "Rechazada";
            out.println(result);
            if (maxUsed - minUsed <= 10) {
                System.out.println(result);
            }

            return accepted;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Leer cadena de entrada
        System.out.println("Ingrese la cadena (solo 0s y 1s, máximo 1000 caracteres):");
        String input = scanner.nextLine();

        // Validar entrada
        if (input.length() > 1000) {
            System.out.println("Error: La cadena excede los 1000 caracteres");
            return;
        }
        if (!input.matches("[01]*")) {
            System.out.println("Error: La cadena solo puede contener 0s y 1s");
            return;
        }

        // Crear y ejecutar máquina de Turing
        TuringMachine tm = new TuringMachine(input);
        try {
            tm.simulate("salida.txt");
            System.out.println("Resultado guardado en salida.txt");
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }
}