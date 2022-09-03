package org.apache.coyote.http11.exception;

public class FileNotFoundException extends RuntimeException {

    public FileNotFoundException() {
        super("해당 파일을 지원하지않습니다.");
    }
}
