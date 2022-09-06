package nextstep.jwp.presentation;

import java.io.IOException;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.StatusCode;

public class StaticResourceController implements Controller {

    @Override
    public ResponseEntity run(final HttpHeader httpHeader, final HttpBody httpBody) {
        return new ResponseEntity(StatusCode.OK, httpHeader.getUrl());
    }
}
