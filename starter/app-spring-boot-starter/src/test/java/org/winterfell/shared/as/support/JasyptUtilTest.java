package org.winterfell.shared.as.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/11/11
 */
public class JasyptUtilTest {

    @Test
    void encrypt() {
        String password = "happy-every-day";  // 加密密钥
        String plaintext = "Demo@1234";  // 要加密的明文

        String encryptedText = JasyptUtil.encrypt(password, plaintext);
        System.out.println("加密后: " + encryptedText);

        String decryptedText = JasyptUtil.decrypt(password, encryptedText);
        System.out.println("解密后: " + decryptedText);
    }

    @Test
    void decrypt() {
    }
}