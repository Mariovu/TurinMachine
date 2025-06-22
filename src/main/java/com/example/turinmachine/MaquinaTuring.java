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
    private Map<String, Map<Character, Transicion>> transitions;

    public MaquinaTuring(String input) {
        tape = new char[TAPE_SIZE];
        Arrays.fill(tape, BLANK);
        for (int i = 0; i < input.length(); i++) {
            tape[CENTER + i] = input.charAt(i);
        }
        head = CENTER;
        state = "q0";
        minUsed = CENTER;
        maxUsed = CENTER + input.length() - 1;
        buildTransitions();
    }

    private void buildTransitions() {
        transitions = new HashMap<>();

        Map<Character, Transicion> q0Map = new HashMap<>();
        q0Map.put('0', new Transicion("q1", 'X', 'R'));
        q0Map.put('X', new Transicion("q0", 'X', 'R'));
        q0Map.put('Y', new Transicion("q3", 'Y', 'R'));
        transitions.put("q0", q0Map);

        Map<Character, Transicion> q1Map = new HashMap<>();
        q1Map.put('0', new Transicion("q1", '0', 'R'));
        q1Map.put('1', new Transicion("q2", 'Y', 'L'));
        q1Map.put('Y', new Transicion("q1", 'Y', 'R'));
        transitions.put("q1", q1Map);

        Map<Character, Transicion> q2Map = new HashMap<>();
        q2Map.put('0', new Transicion("q2", '0', 'L'));
        q2Map.put('X', new Transicion("q0", 'X', 'R'));
        q2Map.put('Y', new Transicion("q2", 'Y', 'L'));
        transitions.put("q2", q2Map);

        Map<Character, Transicion> q3Map = new HashMap<>();
        q3Map.put('Y', new Transicion("q3", 'Y', 'R'));
        q3Map.put(BLANK, new Transicion("q4", BLANK, 'R'));
        transitions.put("q3", q3Map);
    }

    private String getInstaDescripcion() {
        StringBuilder sb = new StringBuilder();
        for (int i = minUsed; i <= maxUsed; i++) {
            if (i == head) sb.append("(").append(state).append(")");
            sb.append(tape[i]);
        }
        return sb.toString();
    }

    public boolean simular(String outputFile) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(outputFile))) {
            String desc = getInstaDescripcion();
            out.println(desc);
            if (maxUsed - minUsed <= 10) System.out.println(desc);

            while (true) {
                Map<Character, Transicion> stateTransitions = transitions.get(state);
                if (stateTransitions == null) break;

                char currentSymbol = tape[head];
                Transicion t = stateTransitions.get(currentSymbol);
                if (t == null) break;

                tape[head] = t.writeSymbol;
                head += t.direction == 'R' ? 1 : -1;
                state = t.nextState;

                if (head < minUsed) minUsed = head;
                if (head > maxUsed) maxUsed = head;

                desc = getInstaDescripcion();
                out.println(desc);
                if (maxUsed - minUsed <= 10) System.out.println(desc);

                if (state.equals("q4")) break;
            }

            boolean accepted = state.equals("q4");
            String result = accepted ? "Aceptada" : "Rechazada";
            out.println(result);
            if (maxUsed - minUsed <= 10) System.out.println(result);

            return accepted;
        }
    }
}
