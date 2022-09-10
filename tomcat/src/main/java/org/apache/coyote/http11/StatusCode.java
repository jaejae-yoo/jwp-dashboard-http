package org.apache.coyote.http11;

public enum StatusCode {

    OK(200, " 200 OK"),
    MOVED_TEMPORARILY(302, " 302 Found"),
    UNAUTHORIZED(401, " 401 Unauthorized");

    private final int status;
    private final String statusMessage;

    StatusCode(final int status, final String statusMessage) {
        this.status = status;
        this.statusMessage = statusMessage;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
