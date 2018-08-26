package net.teufel.core.security;

import java.util.Date;

public interface TokenInfoProvider {

    String getTokenPayloadKey(String origin);

    String getOrigin();

    boolean isOriginAccepts(String origin);

    Date getExpiration();

}