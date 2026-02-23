package com.proiectipdp.chess.core;

public record Move(Position from, Position to, Character promotion) {
    public Move(Position from, Position to) {
        this(from, to, null);
    }
}