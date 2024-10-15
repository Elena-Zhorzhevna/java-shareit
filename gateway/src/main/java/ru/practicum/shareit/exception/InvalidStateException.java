package ru.practicum.shareit.exception;

public class InvalidStateException extends RuntimeException {
    public InvalidStateException() {
        super("Invalid type of state");
    }
}