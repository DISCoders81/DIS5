import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;


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

		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader("logSequenceNumber"));
			this.logSequenceNumber = Integer.parseInt(reader.readLine());
		} catch (FileNotFoundException e)
		{
			this.logSequenceNumber = 0;
		} catch (NumberFormatException e)
		{
			e.printStackTrace();
			return;
		} catch (IOException e)
		{
			e.printStackTrace();
			return;
		} finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

	}


	public static PersistanceManager getInstance() {

		return instance;
	}


	private synchronized void incrementTaId() {

		currentTransactionId++;
	}


	private synchronized void incrementLSN() {

		FileWriter writer = null;

		try
		{
			writer = new FileWriter("logSequenceNumber");
			writer.write(Integer.toString(logSequenceNumber + 1));
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (writer != null)
			{
				try
				{
					writer.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		this.logSequenceNumber++;
	}


	public Transaction beginTransaction() {

		incrementTaId();

		log(currentTransactionId, LogType.BOT);
		return new Transaction(currentTransactionId);
	}


	public synchronized void commit(Transaction ta) {

		// commit the given transaction

		log(ta.getTaId(), LogType.COMMIT);
		ta.commit();
	}


	public synchronized void write(Transaction ta, int pageId, String data) {

		incrementLSN();

		// write the data to a file
		// one file for each page
		// LSN also into the file for redoing

		log(ta.getTaId(), LogType.WRITE, pageId, data);

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

		FileWriter writer = null;

		try
		{
			writer = new FileWriter("page" + page.getPageId());
			writer.write("LSN:" + Integer.toString(page.getLsn()) + "|");
			writer.write("taId:" + Integer.toString(page.getTa().getTaId()) + "|");

			writer.write("data:" + page.getData());

			System.out.println("Stored: " + "\nPageNr " + page.getPageId() + "\nLSN " + page.getLsn() + "\ntaId " + page.getTa().getTaId() + "\nWith Data: " + page.getData() + "\n--------------------");
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (writer != null)
				{
					writer.close();
				}
			} catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}


	/**
	 * This method logs all writing actions. There is another log method used
	 * for BOT and Write. Both methods are the same apart from the given
	 * parameters.
	 * 
	 * @param taId
	 * @param type
	 * @param pageId
	 * @param data
	 */
	private synchronized void log(int taId, LogType type, int pageId, String data) {

		FileWriter writer = null;

		try
		{

			writer = new FileWriter("TransactionLog", true);
			writer.write("LSN:" + Integer.toString(logSequenceNumber) + "|");
			writer.write("taId:" + Integer.toString(taId) + "|");

			switch (type) {
				case COMMIT: {
					System.err.println("This method is not used to log committing operations");
					return;
				}
				case BOT: {
					System.err.println("This method is not used to log begin of transaction operations");
					return;
				}
				case WRITE: {
					writer.write("data:" + data + "|");
					writer.write("pageId:" + pageId);
					break;
				}
			}

			writer.write("\n");
			System.out.println("LOGGED: " + "\nPageNr " + pageId + "\nLSN " + logSequenceNumber + "\ntaId " + taId + "\nWith Data: " + data + "\n--------------------");

		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (writer != null)
				{
					writer.close();
				}
			} catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}

	}


	/**
	 * This method is used for the logging of BOT and Commit actions. There is
	 * another log method for Writing actions which is the same apart from the
	 * given parameters.
	 * 
	 * @param taId
	 * @param type
	 */
	private synchronized void log(int taId, LogType type) {

		FileWriter writer = null;

		try
		{

			writer = new FileWriter("TransactionLog", true);
			writer.write("LSN:" + Integer.toString(logSequenceNumber) + "|");
			writer.write("taId:" + Integer.toString(taId) + "|");

			switch (type) {
				case COMMIT: {
					writer.write("data:committed");
					System.out.println("LOGGED: " + "\nLSN " + logSequenceNumber + "\ntaId " + taId + "\nCOMMITTED\n--------------------");
					break;
				}
				case BOT: {
					writer.write("data:BOT");
					System.out.println("LOGGED: " + "\nLSN " + logSequenceNumber + "\ntaId " + taId + "\nBEGIN OF TRANSACTION \n--------------------");
					break;
				}
				case WRITE: {
					// This is not used for write operations
					System.err.println("This method is not used to log writing operations");
					return;
				}
			}

			writer.write("\n");

		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (writer != null)
				{
					writer.close();
				}
			} catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}

	}


	public void crashRecovery() {

		Integer[] winnerTAs = determineWinnerTaIds();
		if (winnerTAs != null)
		{
			redoTransactions(winnerTAs);
		}
	}


	/**
	 * Reads all of the logs in order to determine which of the logged
	 * transactions have been committed
	 * 
	 * @return all of those transaction's ids
	 */
	private Integer[] determineWinnerTaIds() {

		BufferedReader reader = null;
		ArrayList<Integer> winnerTas = new ArrayList<Integer>();

		try
		{
			reader = new BufferedReader(new FileReader("TransactionLog"));
			String line = reader.readLine();
			String[] values;

			while (line != null)
			{

				values = line.split("\\|");

				switch (values[2].split("\\:")[1]) {
					case "committed": {
						winnerTas.add(Integer.parseInt(values[1].split("\\:")[1]));
						break;
					}
					case "BOT": {
						// BOT
						break;
					}
					default: {
						// data
						break;
					}
				}

				line = reader.readLine();

			}

		} catch (FileNotFoundException e)
		{
			System.err.println("No TransactionLog File found. Crash Recovery is impossible\nSkipping Crash Recovery..");
			return null;
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}

		}
		return (Integer[]) winnerTas.toArray(new Integer[winnerTas.size()]);

	}


	private void redoTransactions(Integer[] taIds) {

		BufferedReader reader = null;

		try
		{
			reader = new BufferedReader(new FileReader("TransactionLog"));
			String line = reader.readLine();
			String[] values;

			while (line != null)
			{
				values = line.split("\\|");

				// Go through all Log entries
				if (values.length != 3)
				{
					for (int currentTaId : taIds)
					{
						// find those that are write operations belonging to the
						// winner transactions
						if (currentTaId == Integer.parseInt(values[1].split("\\:")[1]))
						{
							// compare lsn of the write operation to lsn of the
							// page
							if (!compareLSNToPageLSN(Integer.parseInt(values[0].split("\\:")[1]), Integer.parseInt(values[3].split("\\:")[1])))
							{
								// the operation has to be redone
								redoWrite(values);
							}
						}
					}
				}

				line = reader.readLine();
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}


	private void redoWrite(String[] values) {

		FileWriter writer = null;
		String newLSN = values[0].split("\\:")[1];
		String ta = values[1].split("\\:")[1];
		String data = values[2].split("\\:")[1];
		String pageId = values[3].split("\\:")[1];

		try
		{
			writer = new FileWriter("page" + pageId);

			writer.write("LSN:" + newLSN + "|");
			writer.write("taId:" + ta + "|");
			writer.write("data:" + data);

			System.out.println("Redone!: " + "\nPageNr " + pageId + "\nLSN " + newLSN + "\ntaId " + ta + "\nWith Data: " + data + "\n--------------------");

		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				writer.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

	}


	/**
	 * returns true if LSN is smaller than PageLsn -> in this case the write
	 * action does not have to be redone; And returns false in case the
	 * operation has to be redone;
	 * 
	 * @param lsn
	 * @param pageId
	 * @return
	 */
	private boolean compareLSNToPageLSN(int lsn, int pageId) {

		BufferedReader reader = null;

		String[] line;
		try
		{
			reader = new BufferedReader(new FileReader("page" + pageId));
			line = reader.readLine().split("\\|");
		} catch (FileNotFoundException e)
		{
			System.err.println("File >>page" + pageId + ">> does not exist and will be created..");
			return false;
		} catch (Exception e)
		{
			System.err.println("File >>page" + pageId + ">> is corrupted!");
			return false;
		} finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return (Integer.parseInt(line[0].split("\\:")[1]) < lsn);
	}
}
