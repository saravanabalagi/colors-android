package com.example.colors.exceptions;

/**
 * Created by Zeke on Jul 20, 2016.
 */
public class ColorException extends Exception {
    public ColorException() {}
    public ColorException(String message) { super(message); }
    public ColorException(Throwable cause) { super(cause); }
    public ColorException(String message, Throwable cause) { super(message, cause); }
}