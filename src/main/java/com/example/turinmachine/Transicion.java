package com.example.turinmachine;

class Transicion {
    String nextState;
    char writeSymbol;
    char direction;

    public Transicion(String nextState, char writeSymbol, char direction) {
        this.nextState = nextState;
        this.writeSymbol = writeSymbol;
        this.direction = direction;
    }
}