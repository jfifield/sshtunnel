package org.programmerplanet.sshtunnel.util;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class EncryptionUtil {

	private static final String ALGORITHM = "DES";

	public static String encrypt(String source, String keyString) {
		try {
			Key key = getKey(keyString);
			Cipher desCipher = Cipher.getInstance(ALGORITHM);
			desCipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] cleartext = source.getBytes();
			byte[] ciphertext = desCipher.doFinal(cleartext);
			return byteArrayToHexString(ciphertext);
		} catch (Exception e) {
			return null;
		}
	}

	public static String decrypt(String source, String keyString) {
		try {
			Key key = getKey(keyString);
			Cipher desCipher = Cipher.getInstance(ALGORITHM);
			byte[] ciphertext = hexStringToByteArray(source);
			desCipher.init(Cipher.DECRYPT_MODE, key);
			byte[] cleartext = desCipher.doFinal(ciphertext);
			return new String(cleartext);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String generateKeyString() {
		Key key = generateKey();
		byte[] bytes = key.getEncoded();
		String keyString = byteArrayToHexString(bytes);
		return keyString;
	}

	private static Key generateKey() {
		try {
			KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
			Key key = kg.generateKey();
			return key;
		} catch (Exception e) {
			return null;
		}
	}

	private static Key getKey(String keyString) {
		try {
			byte[] bytes = hexStringToByteArray(keyString);
			DESKeySpec pass = new DESKeySpec(bytes);
			SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
			SecretKey s = skf.generateSecret(pass);
			return s;
		} catch (Exception e) {
			return null;
		}
	}

	private static String byteArrayToHexString(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			String s = Integer.toHexString(0x00FF & b);
			sb.append(s);
			if (i + 1 < bytes.length) {
				sb.append("-");
			}
		}
		return sb.toString();
	}

	private static byte[] hexStringToByteArray(String str) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		StringTokenizer st = new StringTokenizer(str, "-", false);
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			int i = Integer.parseInt(s, 16);
			bos.write((byte)i);
		}
		return bos.toByteArray();
	}

}
