package test.study.blockChain;

import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;


public class Main {

	
	public static ArrayList<Block> blockChain = new ArrayList<>();
	
	public static HashMap<String, TransactionOutput> transactions = new HashMap<>();
	
	public static final int difficulty = 2;
	
	public static ArrayList<Wallet> walletList = new ArrayList<>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		Wallet base = new Wallet("base");
		Wallet walletA = new Wallet("A");
		Wallet walletB = new Wallet("B");
		
		
		walletList.add(base);
		walletList.add(walletA);
		walletList.add(walletB);
		
		System.out.println("base publicKey : "+StringUtil.getStringFromkey(base.publicKey));
		System.out.println("A publicKey : "+StringUtil.getStringFromkey(walletA.publicKey));
		System.out.println("B publicKey : "+StringUtil.getStringFromkey(walletB.publicKey));
		System.out.println("=================================");
		
		//첫번째 거래를 기록할 블록 생성.
		Block baseBlock = new Block("0");
		
		//첫번째 블록에 기록할 트랜잭션 생성.
		TransactionOutput baseOutput = new TransactionOutput(walletA.publicKey, 100f, "0");
		Transaction firstTs = new Transaction(base.publicKey, walletA.publicKey, 100f, null); // 별도의 input은 기록할 필요 없음.
		firstTs.outputs.add(baseOutput);
		firstTs.generateSignature(base.privateKey);
		transactions.put(baseOutput.id, baseOutput);
		baseBlock.addTransaction(firstTs);
		addBlock(baseBlock);
		
		System.out.println("base balance : "+ base.getBalance());
		System.out.println("A balance : "+ walletA.getBalance());
		System.out.println("B balance : "+ walletB.getBalance());
		System.out.println("=================================");
		
		//이후 거래는 일반거래.

		send(walletA, walletB, 35f);

		System.out.println("base balance : "+ base.getBalance());
		System.out.println("A balance : "+ walletA.getBalance());
		System.out.println("B balance : "+ walletB.getBalance());
		System.out.println("=================================");
		
		
		send(walletB, base, 30f);

		System.out.println("base balance : "+ base.getBalance());
		System.out.println("A balance : "+ walletA.getBalance());
		System.out.println("B balance : "+ walletB.getBalance());
		System.out.println("=================================");
		
		
		send(walletA, base, 25f);

		System.out.println("base balance : "+ base.getBalance());
		System.out.println("A balance : "+ walletA.getBalance());
		System.out.println("B balance : "+ walletB.getBalance());
		System.out.println("=================================");
	
		
		//거래 검증.
		proof();
	}
	
	//거래.
	public static void send(Wallet sender, Wallet recipient, float value) {
		
		System.out.println(sender.name+" send "+value+" to "+recipient.name);
		Block lastBlock = blockChain.get(blockChain.size()-1);		
		Block newBlock = new Block(lastBlock.hash);
		newBlock.addTransaction(sender.send(recipient.publicKey, value));
		addBlock(newBlock);
	}
	
	//블록 추가
	public static void addBlock(Block block) {
		
		block.mineBlock(difficulty);
		blockChain.add(block);
	}
	/*
	 * 블록을 순회하며 트랜잭션 조회.
	 * 
	 */
	public static void proof() {
		
		
		System.out.println("chain tracking");
		System.out.println("=========");
		
		for(Block block : blockChain) {
			
			ArrayList<Transaction> tran = block.transactions;
			
			for(Transaction loop : tran) {
				for(TransactionOutput out : loop.outputs) {
					
					String recipient = getName(out.recipient);
					
					System.out.println("recipient : "+recipient+", recept : "+out.value);
					
				}
			}
			System.out.println("=========");
			
		}
		
	}
	
	public static String getName(PublicKey publicKey) {
		
		String targetName = StringUtil.getStringFromkey(publicKey);
		
		for(int i = 0; i < walletList.size(); i++) {
			
			if(targetName.equals(StringUtil.getStringFromkey(walletList.get(i).publicKey))) {
				return walletList.get(i).name;
			}
			
		}
		return "";
		
	}
	


}
