package net.teufel.services.db.dao;

import net.teufel.core.Util;
import net.teufel.core.dao.JdbcDaoSupport;
import net.teufel.core.model.BasicLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import javax.enterprise.context.RequestScoped;
import java.sql.ResultSet;
import java.sql.SQLException;

@RequestScoped
public class LoginDaoJdbc extends JdbcDaoSupport  {

    private static final Logger logger = LoggerFactory.getLogger(LoginDaoJdbc.class);

    public BasicLogin doLogin(String token) {

        logger.info("doLogin start");
        BasicLogin login = securityHelper.getLoginFromToken(token, BasicLogin.class);

        try {
            logger.info("doLogin vor tryDatabaseLogin");
            login = tryDatabaseLogin(login);

            if (login.getLogonTime() == null) {
                login.setLoginSuccessful(false);
                logger.error("doLogin no success");
            }

        }  catch (DataAccessException dataAccessException) {

            login.setLoginSuccessful(false);
            logger.error("doLogin dataAccessException: " + dataAccessException.getMessage());
        }

        logger.info("exit doLogin success=" + login.isLoginSuccessful());
        return login;

    }

    private BasicLogin tryDatabaseLogin(BasicLogin login) throws DataAccessException {

        String db = Util.nvl(login.getDb(), "oracle-ds");

        login.setLoginSuccessful(false);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(Util.getDataSource(login.getUser(), login.getPassword(), db));


        jdbcTemplate.query("select to_char(sysdate, 'DD.MM.YYYY HH24:MI:SS') as LOGON_TIME from dual ", new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet result) throws SQLException {
                login.setLoginSuccessful(true);
                login.setLogonTime(result.getString("LOGON_TIME"));
            }
        });

        return login;
    }


}
