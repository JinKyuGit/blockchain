package test.study.blockChain;

import java.util.ArrayList;
import java.util.Date;

public class Block {

	public String hash;
	public String previousHash;
	private String data;
	private long timestamp;
	private int nonce;
	public ArrayList<Transaction> transactions = new ArrayList<>();
	
	public Block(String previousHash) {
		this.previousHash = previousHash;
		this.timestamp = new Date().getTime();
		this.hash = this.calculateHash();
		this.nonce = 0;
	}
	
	public String calculateHash() {
		
		String calculatedHash = StringUtil.getSHA256(this.previousHash + this.data + Integer.toString(this.nonce) + this.timestamp);
		return calculatedHash;
		
	}
	/*
	 * 채굴과정 -> target 은 0으로 이루어진 String
	 * hash값의 처음이 000...(target의 길이만큼) 으로 시작될때까지 반복.
	 * nonce의 값을 변화시키면서 하기 때문에 언젠간 됨.
	 * 채굴 과정을 길게 하기 위한 트릭.
	 */
	
	public void mineBlock(int difficulty) { // 난이도 1 = 0.., 난이도 2 = 00..
		
		String target = new String(new char[difficulty]).replace('\0', '0');
		
		while(!this.hash.substring(0, difficulty).equals(target)) {
				
			this.nonce++;
			//해쉬값 계산.
			this.hash = this.calculateHash(); // 이전 블록 해쉬값 + 특정 데이터 + nonce
			
		}	
		System.out.println("Block is mined!");		
	}
	
	public boolean addTransaction(Transaction transaction) {
		
		boolean result = true;
		
		//최초거래는 제외.
		if(!"0".equals(this.previousHash)) {
			if(!transaction.processTransaction()) {
				System.out.println("processTranscation is failed");
				return false;
			}
		}
		
		
		this.transactions.add(transaction);
		
		//System.out.println(StringUtil.getStringFromkey("addTransaction() recipient : "+transaction.outputs.get(0).recipient));
		
		return result;
	}
	
}
