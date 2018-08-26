package net.teufel.services.db.security;

import net.teufel.core.security.TokenInfoProvider;

import javax.enterprise.context.Dependent;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Dependent
public class DbTokenInfoProvider implements TokenInfoProvider {

    private static final String ORIGIN = "db";
    private static final String TOKEN_PAYLOAD_KEY = "$Ich$bin$ein$menschlicher$Mensch!$lfs";

    @Override
    public String getOrigin() {
        return ORIGIN;
    }

    @Override
    public boolean isOriginAccepts(String origin) {
        return ORIGIN.equals(origin);
    }

    @Override
    public String getTokenPayloadKey(String origin) {
        return TOKEN_PAYLOAD_KEY;
    }

    @Override
    public Date getExpiration() {
        return toDate(LocalDateTime.now().plusMinutes(180L));
    }

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
