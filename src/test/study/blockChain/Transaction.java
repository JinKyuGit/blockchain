package test.study.blockChain;

import java.security.*;
import java.util.ArrayList;

public class Transaction {
	
	public String transactionId;
	public PublicKey sender;
	public PublicKey recipient;
	public float value;
	public byte[] signature;
	
	public ArrayList<TransactionInput> inputs = new ArrayList<>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<>();
	
	private static int sequence = 0;
	
	public Transaction(PublicKey sender, PublicKey recipient, float value, ArrayList<TransactionInput> inputs) {
		
		this.sender = sender;
		this.recipient = recipient;
		this.value = value;
		this.inputs = inputs;
		
	}
	
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromkey(this.sender) + StringUtil.getStringFromkey(this.recipient) + Float.toString(this.value);
		this.signature = StringUtil.applyECDSASig(privateKey, data);
	}
	
	public boolean verifySignature() {
		String data = StringUtil.getStringFromkey(this.sender) + StringUtil.getStringFromkey(this.recipient) + Float.toString(this.value);
		return StringUtil.verifyECDSASig(this.sender, data, this.signature);
	}
	/*
	 * 트랜잭션 수행.
	 * 거래는 다음과 같이 이루어진다.
	 * 보낼 사람이 보낼 금액보다 큰 트랜잭션 거래에 대해 우선 삭제.
	 * 보낸 금액 / 보내고 남은 금액으로 각각에게 output 생성.
	 * 이를 처리하기 위해 Wallet에서 send시에 output 정보를 input에 넣고, 미리 삭제함.(중복 제거) => 잔액은 메인 트랜잭션의 out정보를 토대로 계산되기 때문임.
	 */
	public boolean processTransaction() {
		
		boolean result = true;
		
		//검증.
		if(!this.verifySignature()) {
			System.out.println("Transaction Signature failed to verify");
			return false;
		}
		

		
		//메인 트랜잭션에서 output 트랜잭션의 정보를 얻고, 배분을 할 것이므로 기존의 트랜잭션 정보 삭제.
		for(TransactionInput in : this.inputs) {
			in.UTXO = Main.transactions.get(in.transactionOutputId);
			Main.transactions.remove(in.transactionOutputId);
		}
		
		//잔액 = 가진 금액 - 보낼 금액
		float leftOver = this.getInputValue() - this.value;
		
		this.transactionId = this.calcuateHash();
		
		//계산된 금액대로 output 재수행.
		TransactionOutput senderOutput = new TransactionOutput(this.sender, leftOver, transactionId);
		TransactionOutput recipientOutput = new TransactionOutput(this.recipient, this.value, transactionId);
		
		this.outputs.add(senderOutput);
		this.outputs.add(recipientOutput);
		
		//메인 트랜잭션에 넣는다.
		for(TransactionOutput out : this.outputs) {
			Main.transactions.put(out.id, out);
		}
		
		return result;
	}
	
	public float getInputValue() {
		
		float total = 0f;
		
		for(TransactionInput i : this.inputs) {
			
			if(i.UTXO == null) {
				continue;
			}
			total += i.UTXO.value;	
		}
		
		return total;
	}
	
	public String calcuateHash() {

		return StringUtil.getSHA256(StringUtil.getStringFromkey(this.sender) 
				                    + StringUtil.getStringFromkey(this.recipient)
				                    + this.value
				                    + sequence++ );
	
				
	}

}
