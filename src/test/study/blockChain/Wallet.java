package test.study.blockChain;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
	
	public PrivateKey privateKey;
	public PublicKey publicKey;
	public String name;

	public HashMap<String, TransactionOutput> outputs = new HashMap<>();
	
	public Wallet(String name) {
		this.generateKeyPair();
		this.name = name;
	}
	
	
	public void generateKeyPair() {
		
		try {
			
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			
			keyGen.initialize(ecSpec, random);
			KeyPair keyPair = keyGen.generateKeyPair();
			
			this.privateKey = keyPair.getPrivate();
			this.publicKey = keyPair.getPublic();
			
			
		} catch(Exception e) {
			System.out.println("keyGen failed");
			throw new RuntimeException(e);
		}

	}
	/*
	 * 잔고.
	 * 잔고는 메인 트랜잭션의 output정보(받은 사람에 대한 기록이 있음)
	 * 를 총합하여 계산되어진다. 즉, 별도의 잔고 변수가 없이 트랜잭션으로 계산되어짐.
	 * 때문에, 잔액 계산시 시간이 다소 소요될 수는 있겠으나, 위조 변조가 거의 불가능해짐. 모든 트랜잭션의 데이터를 수정해야 하기 때문.
	 */
	public float getBalance() {
		
		float balance = 0f;
		
		for(Map.Entry<String, TransactionOutput> entry : Main.transactions.entrySet()) {
			
			TransactionOutput out = entry.getValue();
			
			if(out.isMine(this.publicKey)) {
				this.outputs.put(out.id, out);
				balance += out.value;
			}		
		}

		
		return balance;
	}
	/*
	 * 신규 트랜잭션을 생성하고
	 * output정보를 삭제함.
	 * 정산한 금액에 대한 트랜잭션은 Transaction의 processTransaction()으로 이루어짐.
	 */
	public Transaction send(PublicKey recipient, float value) {
		
		if(this.getBalance() < value) {
			System.out.println("balance is not enough to send");
			return null;
		}
		
		ArrayList<TransactionInput> inputs = new ArrayList<>();
		
		float total  = 0f;
		
		for(Map.Entry<String, TransactionOutput> entry : this.outputs.entrySet()) {
			
			TransactionOutput out = entry.getValue();
			
			total += out.value;
			inputs.add(new TransactionInput(out.id));
			if(total > value) break;
			
		}
		
		//받은 금액 삭제. Trasaction.java의 processTransaction() 에서 송금하고 남은 금액 정산하여 처리.
		for(TransactionInput in : inputs) {
			this.outputs.remove(in.transactionOutputId);
		}
		
		Transaction newTran = new Transaction(this.publicKey, recipient, value, inputs);
		newTran.generateSignature(this.privateKey);
		
		return newTran;
		
		
	}
	
}
