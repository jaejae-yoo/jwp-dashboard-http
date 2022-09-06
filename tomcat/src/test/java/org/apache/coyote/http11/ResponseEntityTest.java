package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.exception.FileNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseEntityTest {

    @DisplayName("존재하지 않는 파일인 경우 예외가 발생한다.")
    @Test
    void notExistFileException() {
        final ResponseEntity responseEntity = new ResponseEntity(StatusCode.OK, "/login.css");
        final HttpHeader httpHeader = new HttpHeader("GET /login.css HTTP/1.1",
                String.join("\r\n",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: 12 ",
                        ""));

        assertThatThrownBy(() -> responseEntity.getResponse(httpHeader))
                .hasMessageContaining("해당 파일을 지원하지않습니다.")
                .isInstanceOf(FileNotFoundException.class);
    }
}
