package com.example.turinmachine;

import java.io.*;
import java.util.*;

public class MaquinaTuring {
    private static final int TAPE_SIZE = 2000;
    private static final int CENTER = 1000;
    private static final char BLANK = 'B';

    private char[] tape;
    private int head;
    private String state;
    private int minUsed;
    private int maxUsed;
    private final int inputLength;
    private Map<String, Map<Character, Transicion>> transitions;
    private List<String> pasosSimulacion;

    public MaquinaTuring(String input) {
        // Inicializar cinta con espacios en blanco
        tape = new char[TAPE_SIZE];
        Arrays.fill(tape, BLANK);

        // Cargar la cadena de entrada en el centro de la cinta
        for (int i = 0; i < input.length(); i++) {
            tape[CENTER + i] = input.charAt(i);
        }

        head = CENTER;  // Cabeza al inicio de la cadena
        state = "q0";   // Estado inicial
        minUsed = CENTER;
        maxUsed = CENTER + input.length() - 1;
        inputLength = input.length();
        pasosSimulacion = new ArrayList<>();
        buildTransitions();
    }

    private void buildTransitions() {
        transitions = new HashMap<>();

        // Transiciones para q0 (estado inicial)
        Map<Character, Transicion> q0Map = new HashMap<>();
        q0Map.put('0', new Transicion("q1", 'X', 'R'));  // Reemplazar 0 con X, mover derecha, ir a q1
        q0Map.put('Y', new Transicion("q3", 'Y', 'R'));  // Si encuentra Y, ir a q3 (para aceptar)
        transitions.put("q0", q0Map);

        // Transiciones para q1 (buscar el final de los 0s)
        Map<Character, Transicion> q1Map = new HashMap<>();
        q1Map.put('0', new Transicion("q1", '0', 'R'));  // Continuar sobre 0s
        q1Map.put('1', new Transicion("q2", 'Y', 'L'));  // Encontró un 1, reemplazar con Y, retroceder
        q1Map.put('Y', new Transicion("q1", 'Y', 'R'));  // Ignorar Ys existentes
        transitions.put("q1", q1Map);

        // Transiciones para q2 (retroceder al último X)
        Map<Character, Transicion> q2Map = new HashMap<>();
        q2Map.put('0', new Transicion("q2", '0', 'L'));  // Continuar retrocediendo sobre 0s
        q2Map.put('X', new Transicion("q0", 'X', 'R'));  // Encontró X, volver a q0 para procesar próximo 0
        q2Map.put('Y', new Transicion("q2", 'Y', 'L'));  // Continuar sobre Ys
        transitions.put("q2", q2Map);

        // Transiciones para q3 (verificar que solo quedan Ys)
        Map<Character, Transicion> q3Map = new HashMap<>();
        q3Map.put('Y', new Transicion("q3", 'Y', 'R'));  // Continuar sobre Ys
        q3Map.put(BLANK, new Transicion("q4", BLANK, 'R'));  // Encontró blanco, aceptar (q4)
        transitions.put("q3", q3Map);
    }

    private String getInstaDescripcion() {
        StringBuilder sb = new StringBuilder();
        for (int i = minUsed; i <= maxUsed; i++) {
            if (i == head) {
                sb.append("[").append(state).append("]");
            }
            sb.append(tape[i]);
        }
        return sb.toString();
    }

    public List<String> simular(String outputFile) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(outputFile))) {
            // Paso inicial
            String desc = getInstaDescripcion();
            pasosSimulacion.add(desc);
            out.println(desc);

            if (inputLength <= 10) {
                System.out.println(desc);
            }

            while (!state.equals("q4")) {
                Map<Character, Transicion> stateTransitions = transitions.get(state);

                // Si no hay transiciones para este estado, rechazar
                if (stateTransitions == null) {
                    pasosSimulacion.add("Rechazada");
                    break;
                }

                char currentSymbol = tape[head];
                Transicion t = stateTransitions.get(currentSymbol);

                // Si no hay transición para este símbolo, rechazar
                if (t == null) {
                    pasosSimulacion.add("Rechazada");
                    break;
                }

                // Aplicar la transición
                tape[head] = t.writeSymbol;   // Escribir nuevo símbolo
                state = t.nextState;           // Cambiar estado

                // Mover cabeza
                if (t.direction == 'R') {
                    head++;
                    if (head > maxUsed) maxUsed = head;  // Expandir cinta si es necesario
                } else {
                    head--;
                    if (head < minUsed) minUsed = head;  // Expandir cinta si es necesario
                }

                // Registrar paso
                desc = getInstaDescripcion();
                pasosSimulacion.add(desc);
                out.println(desc);

                if (inputLength <= 10) {
                    System.out.println(desc);
                }
            }

            // Si llegamos a q4, aceptar
            if (state.equals("q4")) {
                pasosSimulacion.add("Aceptada");
            }

            // Guardar resultado final
            String result = pasosSimulacion.get(pasosSimulacion.size() - 1);
            out.println(result);

            if (inputLength <= 10) {
                System.out.println(result);
            }

            return pasosSimulacion;
        }
    }
}