public class Transaction {
	private boolean	committed	= false;
	private int		taId;


	public Transaction(int taId) {

		this.taId = taId;
	}


	public int getTaId() {

		return taId;
	}


	public void setTaId(int taId) {

		this.taId = taId;
	}


	public void write(int pageId, String data) {

		PersistanceManager.getInstance().write(this, pageId, data);
	}


	public void commit() {

		if (!committed)
		{
			committed = true;
		}
	}


	public boolean getCommitted() {

		return committed;
	}
}
