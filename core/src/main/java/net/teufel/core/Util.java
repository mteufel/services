package net.teufel.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.UserCredentialsDataSourceAdapter;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class);

    public static UserCredentialsDataSourceAdapter getDataSource(String user, String password, String dsConfigName) {
        String dsConfig = "oracle-ds";
        if(dsConfigName != null && !dsConfigName.isEmpty()){
            dsConfig = dsConfigName;
        }

        try {
            InitialContext context = new InitialContext();
            logger.info("Using Datasource = " + dsConfig + " ConfName=" + dsConfigName);
            DataSource ds = (DataSource) context.lookup("java:jboss/datasources/" + dsConfig);
            if (ds == null) {
                logger.error("No Datasource found");

            } else {
                UserCredentialsDataSourceAdapter adapter = new UserCredentialsDataSourceAdapter();
                adapter.setTargetDataSource(ds);
                adapter.setUsername(user);
                adapter.setPassword(password);
                return adapter;
            }
        } catch (NamingException e) {
            logger.error("Error while creating a datasource for the user: " + user, e);
        }
        return null;
    }

    /**
     * NVL-Funktion für Java. Gibt einen leeren String zurück, falls der übergebene Parameter-String Null ist. Ansonsten wird der
     * übergebenen String wieder zurückgegeben.
     *
     * @param value {@link String} der auf Null geprüft werden soll
     * @return {@link String} der auf Null geprüft wurde
     */
    public static String nvl(String value) {
        return nvl(value, "");
    }

    /**
     * NVL-Funktion für Java. Gibt den übergebenen Alternativ-String zurück, falls der übergebene Parameter-String Null ist. Ansonsten wird
     * der übergebenen String wieder zurückgegeben.
     *
     * @param value       {@link String} der auf Null geprüft werden soll
     * @param alternative {@link String} der als Alternative zurückgegeben wird , falls der erste Parameter null ist
     * @return {@link String} der auf Null geprüft wurde
     */
    public static String nvl(String value, String alternative) {
        if (value == null) {
            return alternative;
        }
        return value;
    }

}
