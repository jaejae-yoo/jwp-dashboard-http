package nextstep.jwp.presentation;

import java.io.IOException;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    private static final String LOGIN_URL = "/login.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String AUTHORIZED_URL = "/401.html";
    private static final String USER_ATTRIBUTE_KEY = "user";


    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if (httpRequest.hasJSESSIONID()) {
            final Optional<Session> session = SessionManager.findSession(httpRequest.getJSESSIONID());
            if (session.isPresent() && session.get().hasAttribute(USER_ATTRIBUTE_KEY)) {
                return redirect(httpRequest, httpResponse);
            }
        }
        final String account = httpRequest.getBodyValue(ACCOUNT);
        final String password = httpRequest.getBodyValue(PASSWORD);
        return authentication(httpRequest, account, password);
    }

    private HttpResponse redirect(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final HttpHeader httpHeader = new HttpHeader().startLine(StatusCode.MOVED_TEMPORARILY)
                .contentType(httpRequest.getUrl());
        httpHeader.location(REDIRECT_URL);

        return httpResponse.header(httpHeader).body(new HttpBody());
    }

    private HttpResponse authentication(final HttpRequest httpRequest, final String account, final String password)
            throws IOException {
        final Optional<User> findUser = InMemoryUserRepository.findByAccount(account);
        if (findUser.isPresent() && findUser.get().checkPassword(password)) {
            final User user = findUser.get();
            LOGGER.info(user.toString());
            return assignCookie(httpRequest, user);
        }

        final HttpBody httpBody = HttpBody.createByUrl(AUTHORIZED_URL);
        final HttpHeader httpHeader = defaultHeader(StatusCode.MOVED_TEMPORARILY, httpBody, AUTHORIZED_URL);
        httpHeader.location(AUTHORIZED_URL);

        return new HttpResponse(httpHeader, httpBody);
    }

    private HttpResponse assignCookie(final HttpRequest httpRequest, final User user) throws IOException {
        final Session session = SessionManager.add(HttpCookie.makeJSESSIONID());
        session.addAttribute(USER_ATTRIBUTE_KEY, user);

        final HttpBody httpBody = HttpBody.createByUrl(REDIRECT_URL);
        final HttpHeader httpHeader = new HttpHeader().startLine(StatusCode.MOVED_TEMPORARILY)
                .cookie(session.getId())
                .contentType(httpRequest.getUrl())
                .contentLength(httpBody.getBody().getBytes().length)
                .location(REDIRECT_URL);

        return new HttpResponse(httpHeader, httpBody);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if (httpRequest.hasJSESSIONID()) {
            final Optional<Session> session = SessionManager.findSession(httpRequest.getJSESSIONID());
            if (session.isPresent() && session.get().hasAttribute(USER_ATTRIBUTE_KEY)) {
                return redirect(httpRequest, httpResponse);
            }
        }
        final HttpBody httpBody = HttpBody.createByUrl(LOGIN_URL);
        final HttpHeader httpHeader = defaultHeader(StatusCode.OK, httpBody, LOGIN_URL);
        return httpResponse.header(httpHeader).body(httpBody);
    }
}
