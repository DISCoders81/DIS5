
/**
 * @author joantomaspape
 * This class represents a page in the database
 */


public class Page{

	int pageId;
	int LSN;
	//String data;
	
	public Page(int pageId){
		this.pageId = pageId;
	}
	
	public void write(String data){
		//write data to page
	}
	
}
