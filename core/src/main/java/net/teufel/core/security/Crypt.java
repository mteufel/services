package net.teufel.core.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class Crypt {

    private final String keyString;

    public Crypt(final String key) {
        this.keyString = key;
    }

    private SecretKeySpec getKeyAes() throws Exception {
        // byte-Array erzeugen
        byte[] key = keyString.getBytes("UTF-8");
        // aus dem Array einen Hash-Wert erzeugen mit MD5 oder SHA
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key);
        // nur die ersten 128 bit nutzen
        key = Arrays.copyOf(key, 16);
        // der fertige Schluessel
        return new SecretKeySpec(key, "AES");
    }

    public String encrypt(String value) throws Exception {
        // Verschluesseln
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, getKeyAes());
        byte[] encrypted = cipher.doFinal(value.getBytes());

        // bytes zu Base64-String konvertieren (dient der Lesbarkeit)
        return Base64.getMimeEncoder().encodeToString(encrypted);
    }

    public String decrypt(String value) throws Exception {
        // BASE64 String zu Byte-Array konvertieren
        byte[] crypted2 = Base64.getMimeDecoder().decode(value);

        // Entschluesseln
        Cipher cipher2 = Cipher.getInstance("AES");
        cipher2.init(Cipher.DECRYPT_MODE, getKeyAes());
        byte[] cipherData2 = cipher2.doFinal(crypted2);
        return new String(cipherData2);
    }

}
