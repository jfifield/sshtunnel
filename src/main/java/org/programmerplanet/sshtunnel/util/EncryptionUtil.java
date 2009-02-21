/*
 * Copyright 2009 Joseph Fifield
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.programmerplanet.sshtunnel.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Simple encryption/decryption utility class.
 * 
 * @author <a href="jfifield@programmerplanet.org">Joseph Fifield</a>
 */
public class EncryptionUtil {

	private static final String ALGORITHM = "DES";

	public static String encrypt(String source, String keyString) {
		try {
			Key key = getKey(keyString);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] clearBytes = source.getBytes();
			byte[] cipherBytes = cipher.doFinal(clearBytes);
			return encode(cipherBytes);
		} catch (Exception e) {
			return null;
		}
	}

	public static String decrypt(String source, String keyString) {
		try {
			Key key = getKey(keyString);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] cipherBytes = decode(source);
			byte[] clearBytes = cipher.doFinal(cipherBytes);
			return new String(clearBytes);
		} catch (Exception e) {
			return null;
		}
	}

	public static String createKeyString() {
		try {
			Key key = createKey();
			byte[] bytes = key.getEncoded();
			String keyString = encode(bytes);
			return keyString;
		} catch (Exception e) {
			return null;
		}
	}

	private static Key createKey() throws GeneralSecurityException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
		Key key = keyGenerator.generateKey();
		return key;
	}

	private static Key getKey(String keyString) throws IOException, GeneralSecurityException {
		byte[] bytes = decode(keyString);
		DESKeySpec keySpec = new DESKeySpec(bytes);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
		SecretKey key = keyFactory.generateSecret(keySpec);
		return key;
	}

	private static String encode(byte[] bytes) {
		BASE64Encoder encoder = new BASE64Encoder();
		String encoded = encoder.encode(bytes);
		return encoded;
	}

	private static byte[] decode(String str) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] decoded = decoder.decodeBuffer(str);
		return decoded;
	}

}
