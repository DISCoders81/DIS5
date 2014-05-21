import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;


/**
 * @author joantomaspape
 * This class is the Persistance Manager of the Database. Here all Transactions are logged and persisted.
 */

public class PersistanceManager{

	private static PersistanceManager instance = new PersistanceManager();
	
	private int currentTransactionId = 0;
	private int logSequenceNumber = 0;
	
	private PersistanceManager(){}
	
	public static PersistanceManager getInstance(){
		return instance;
	}
	
	public int beginTransaction(){
		currentTransactionId++;
		return currentTransactionId;
	}
	
	public void commit(int transactionId){
		// commit the transaction with given id
	}
	
	public void write(int taId, int pageId, String data){
		this.logSequenceNumber++;
				
		// write the data to a file
		// one file for each page
		// LSN also into the file for redoing
		
		Properties prop = new Properties();
		OutputStream output = null;
		
		try{
			output = new FileOutputStream("page" + pageId);
			prop.setProperty("LSN", Integer.toString(logSequenceNumber));
			prop.setProperty("taId", Integer.toString(taId));
			prop.setProperty("data", data);
			prop.store(output, 
					"Store Page Number: " + pageId 
					+ "\nLSN: " + logSequenceNumber 
					+ "\nTaId: " + taId 
					+ "\nData: " + data);
			System.out.println("Logged: "
							+ "\nPageNr "+ pageId
							+ "\nLSN " + logSequenceNumber
							+ "\ntaId " + taId
							+ "\nWith Data: " + data 
							+ "\n--------------------");
		}
		catch(IOException e){
			e.printStackTrace();
		}
		finally{
			try{
				if(output!=null){
				output.close();
				}
			} catch (IOException ex){
				ex.printStackTrace();
			}
		
		}
		
	}
	
}
