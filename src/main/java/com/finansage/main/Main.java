// Package declaration: Organizes our code into a unique namespace.
// This prevents naming conflicts and is a fundamental part of Java's structure.
package com.finansage.main;

/**
 * The main entry point for the FinanSage application.
 * The 'public' access modifier means this class is visible from any other class.
 * The 'class' keyword is the blueprint for creating objects.
 */
public class Main {

    /**
     * The main method. This is a special method that the Java Virtual Machine (JVM)
     * looks for to start the execution of the program.
     *
     * 'public': It can be called from anywhere.
     * 'static': It belongs to the Main class itself, not to an instance of the class.
     * This allows the JVM to run it without creating a Main object.
     * 'void': This method does not return any value.
     * 'String[] args': An array of strings that can be used to pass command-line arguments.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        // System.out is a standard output stream.
        // println prints the provided string to the console, followed by a new line.
        System.out.println("Welcome to FinanSage - Your Personal Finance Manager!");
    }
}

