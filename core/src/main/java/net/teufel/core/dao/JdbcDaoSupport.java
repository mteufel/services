package net.teufel.core.dao;

import net.teufel.core.Util;
import net.teufel.core.model.BasicLogin;
import net.teufel.core.security.SecurityHelper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

public class JdbcDaoSupport {

    private DataSource dataSource = null;
    private BasicLogin login = null;

    @Inject
    HttpServletRequest request;

    @Inject
    protected SecurityHelper securityHelper;

    @Inject @ConfigurationValue("de.hama.db")
    private String dbConfigHama;


    @PostConstruct
    public void postConstruct() {
        this.login = securityHelper.getLoginFromRequest(this.request, BasicLogin.class);
        if (login != null) {
            this.dataSource = Util.getDataSource(login.getUser(), login.getPassword(), dbConfigHama);
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public BasicLogin getLogin() {
        return login;
    }

    public JdbcTemplate getJdbcTemplate() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(getDataSource());
        return jdbcTemplate;
    }

}
