package test.study.blockChain;

import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.google.gson.GsonBuilder;

public class StringUtil {

	public static String getSHA256(String input) {
		
		try {
			
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();
			
			for(int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) {
					hexString.append("0");
				}else {
					hexString.append(hex);
				}
				
			}
			
			return hexString.toString();
			
		} catch (Exception e) {
			
			new Exception();
			return "";
		}
	}
	
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		
		Signature dsa;
		byte[] output = new byte[0];
		
		try {
			
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}

		return output;
	}
	
	public static boolean verifyECDSASig(PublicKey publickey, String data, byte[] signature) {
		
		try {
			
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publickey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public static String getJson(Object o) {
		return new String(new GsonBuilder().setPrettyPrinting().create().toJson(o));
	}
	
	//키 스트링화
	public static String getStringFromkey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	public static String getMerkleRoot(ArrayList<Transaction> transactions) {
		
		int count = transactions.size();
		
		List<String> previousTreeLayer = new ArrayList<>();
		
		for(Transaction t : transactions) {
			previousTreeLayer.add(t.transactionId);
		}
		
		List<String> treeLayer = previousTreeLayer;
		
		while(count > 1) {
			
			treeLayer = new ArrayList<String>();
			
			for(int i = 1; i < previousTreeLayer.size(); i+=2) {
				treeLayer.add(StringUtil.getSHA256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
			
		}
		
		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		return merkleRoot;
		
	}
	
}
