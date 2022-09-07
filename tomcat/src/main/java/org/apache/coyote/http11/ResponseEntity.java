package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.catalina.Session;
import org.apache.coyote.http11.exception.FileNotFoundException;

public class ResponseEntity {

    private static final String RESOURCE_PATH = "static";
    private static final String DEFAULT_EXTENSION = "html";
    private static final String EXTENSION_DELIMITER = ".";

    private final StatusCode statusCode;
    private final String path;
    private String body;
    private String cookie;

    public ResponseEntity(final StatusCode statusCode, final String path) {
        this.statusCode = statusCode;
        this.path = path;
    }

    public ResponseEntity body(final String body) {
        this.body = body;
        return this;
    }

    public ResponseEntity setCookie(final Session session) {
        this.cookie = session.getId();
        return this;
    }

    public String getResponse(final HttpHeader httpHeader) throws IOException {
        if (this.body == null) {
            this.body = getContent(path);
        }

        final String header = httpHeader.getResponseHeader(statusCode, path, body.getBytes().length, cookie);
        return String.join("\r\n", header, body);
    }

    protected String getContent(final String path) throws FileNotFoundException, IOException {
        final URL resource = getClass()
                .getClassLoader()
                .getResource(RESOURCE_PATH + path + getExtension(path));

        if (resource == null) {
            throw new FileNotFoundException();
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private String getExtension(final String path) {
        if (path.contains(EXTENSION_DELIMITER)) {
            return "";
        }
        return EXTENSION_DELIMITER + DEFAULT_EXTENSION;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }
}
