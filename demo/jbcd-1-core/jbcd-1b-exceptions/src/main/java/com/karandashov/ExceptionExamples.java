package com.karandashov;

public class ExceptionExamples {

    public void throwBaseCheckedException() throws Exception {
        // Checked exception обязан быть указан в throws или пойман в catch.
        throw new Exception("Checked exception must be declared or caught");
    }

    public void throwBaseUncheckedException() {
        // RuntimeException компилятор не заставляет указывать в сигнатуре метода.
        throw new RuntimeException("Runtime exception may be thrown without declaration");
    }

    public void throwCustomCheckedException() throws DocumentReadException {
        // Свое checked-исключение удобно для ошибок, с которыми вызывающий код должен разобраться.
        throw new DocumentReadException("Document file can not be read");
    }

    public void throwCustomUncheckedException() {
        // Свое unchecked-исключение удобно для ошибок программной логики или валидации.
        throw new DocumentValidationException("Document number is invalid");
    }

    public String catchCheckedException() {
        try {
            throwCustomCheckedException();
            return "success";
        } catch (DocumentReadException e) {
            return "checked: " + e.getMessage();
        }
    }

    public String catchUncheckedException() {
        try {
            throwCustomUncheckedException();
            return "success";
        } catch (DocumentValidationException e) {
            return "unchecked: " + e.getMessage();
        }
    }
}
