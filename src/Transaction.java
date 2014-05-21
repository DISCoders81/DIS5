
public class Transaction {
	public boolean committed;
	private int taId;
	
	public Transaction(int taId) {
		this.taId = taId;
	}
	
	public int getTaId() {
		return taId;
	}
	public void setTaId(int taId) {
		this.taId = taId;
	}
}
