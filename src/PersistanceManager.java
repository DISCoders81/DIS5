import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Properties;


/**
 * @author joantomaspape This class is the Persistance Manager of the Database.
 *         Here all Transactions are logged and persisted.
 */

public class PersistanceManager {

	private static PersistanceManager	instance				= new PersistanceManager();

	private int							currentTransactionId	= 0;
	private int							logSequenceNumber		= 0;
	private Hashtable<Integer, Page>	buffer					= new Hashtable<Integer, Page>();


	private PersistanceManager() {

	}


	public static PersistanceManager getInstance() {

		return instance;
	}


	private synchronized void incrementTaId() {

		currentTransactionId++;
	}


	public Transaction beginTransaction() {

		incrementTaId();
		System.out.println("Begin of Transaction Nr: " + currentTransactionId);
		return new Transaction(currentTransactionId);
	}


	public synchronized void commit(Transaction ta) {

		// commit the given transaction
		System.out.println("Transaction Nr: " + currentTransactionId
				+ "was committed!");
		ta.commit();
	}


	public synchronized void write(Transaction ta, int pageId, String data) {

		this.logSequenceNumber++;

		// write the data to a file
		// one file for each page
		// LSN also into the file for redoing

		log(ta.getTaId(), pageId, data);

		Page page = new Page(pageId, logSequenceNumber, data, ta);
		buffer.put(pageId, page);
		System.out.println("Buffer Size: " + buffer.size());

		if (buffer.size() > 5)
		{
			Object[] allValues = buffer.values().toArray();
			for (int i = 0; i < allValues.length; i++)
			{
				Page currentPage = (Page) allValues[i];
				if (currentPage.checkForCommit())
				{
					savePage(currentPage);
					buffer.remove(currentPage.getPageId());

				}
			}
		}

	}


	private synchronized void savePage(Page page) {

		Properties prop = new Properties();
		OutputStream output = null;

		try
		{
			output = new FileOutputStream("page" + page.getPageId());
			prop.setProperty("LSN", Integer.toString(page.getLsn()));
			prop.setProperty("taId", Integer.toString(page.getTa().getTaId()));
			prop.setProperty("data", page.getData());
			prop.store(output, "Page_Data");
			System.out.println("Stored: " + "\nPageNr " + page.getPageId()
					+ "\nLSN " + page.getLsn() + "\ntaId "
					+ page.getTa().getTaId() + "\nWith Data: " + page.getData()
					+ "\n--------------------");
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (output != null)
				{
					output.close();
				}
			} catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}


	private synchronized void log(int taId, int pageId, String data) {

		Properties prop = new Properties();
		OutputStream output = null;

		try
		{
			output = new FileOutputStream("log_page" + pageId);
			prop.setProperty("LSN", Integer.toString(logSequenceNumber));
			prop.setProperty("taId", Integer.toString(taId));
			prop.setProperty("data", data);
			prop.store(output, "Log Eintrag");
			System.out.println("Logged: " + "\nPageNr " + pageId + "\nLSN "
					+ logSequenceNumber + "\ntaId " + taId + "\nWith Data: "
					+ data + "\n--------------------");
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (output != null)
				{
					output.close();
				}
			} catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}
}
