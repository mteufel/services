package net.teufel.services.db;

import net.teufel.core.model.BasicLogin;
import net.teufel.core.security.SecurityHelper;
import net.teufel.services.db.dao.LoginDaoJdbc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Path("/")
public class DbLoginService {

    private static final Logger logger = LoggerFactory.getLogger(DbLoginService.class);

    @Context
    private UriInfo uriInfo;
    @Inject
    private LoginDaoJdbc loginDaoJdbc;
    @Inject
    private SecurityHelper securityHelper;

    @POST
    @Path("/login")
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response authenticate(@FormParam("login") String login,
                                 @FormParam("password") String password,
                                 @FormParam("db") String db) {

        logger.info("authenticateUser aufgerufen --> login=" + login + " db=" + db + " password=***");

        try {

            Map<String, String> extraClaims = new HashMap<>();
            extraClaims.put("_d", db);

            String token = securityHelper.issueToken(login, password, uriInfo.getAbsolutePath().toString(), extraClaims);
            BasicLogin l = loginDaoJdbc.doLogin(token);

            if (l.isLoginSuccessful()) {
                return Response.ok(l, MediaType.APPLICATION_JSON).header(AUTHORIZATION, "Bearer " + token).build();
            } else {
                return Response.status(UNAUTHORIZED).build();
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            return Response.status(INTERNAL_SERVER_ERROR).build();
        }
    }

}
