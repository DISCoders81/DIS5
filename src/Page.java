
public class Page {
	int pageId;
	int lsn;
	String data;
	Transaction ta;

	public Page(int pageId, int lsn, String data, Transaction ta) {
		this.pageId = pageId;
		this.lsn = lsn;
		this.data = data;
		this.ta = ta;
	}

	public Transaction getTa() {
		return ta;
	}	
	public int getLsn() {
		return lsn;
	}
	public void setLsn(int lsn) {
		this.lsn = lsn;
	}
	public int getPageId() {
		return pageId;
	}
	public void setPageId(int pageId) {
		this.pageId = pageId;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
}
