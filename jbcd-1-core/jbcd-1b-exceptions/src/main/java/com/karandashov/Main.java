package com.karandashov;

public class Main {

    public static void main(String[] args) {
        ExceptionExamples examples = new ExceptionExamples();

        try {
            examples.throwBaseCheckedException();
        } catch (Exception e) {
            // Checked exception нельзя проигнорировать: компилятор требует catch или throws.
            System.out.println("Caught base checked exception: " + e.getMessage());
        }

        try {
            examples.throwBaseUncheckedException();
        } catch (RuntimeException e) {
            // Unchecked exception тоже можно поймать, но компилятор не заставляет это делать.
            System.out.println("Caught base unchecked exception: " + e.getMessage());
        }

        System.out.println(examples.catchCheckedException());
        System.out.println(examples.catchUncheckedException());
    }
}
