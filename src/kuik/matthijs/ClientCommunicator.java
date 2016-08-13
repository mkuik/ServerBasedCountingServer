package kuik.matthijs;

import jdk.nashorn.internal.parser.JSONParser;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ClientCommunicator implements Runnable
{
	private Thread thread;
	private Socket socket;
	private Server server;
	private final String text = "De Leidse Vereniging voor Studenten Catena gaat eind Januari een grote feestweek geven. Tijdens deze feestweek worden veel mensen verwacht omdat, deze week de vereniging open is voor alle studenten in leiden. Niet alleen leden van deze vereniging. Om aan de eisen van de brandweer te voldoen mag de vereniging een beperkt aantal mensen in het pand houden. Hierdoor ontstaan rijen wat veel irritatie en geluidsoverlast veroorzaakt. Nu stelt de vereniging voor om het aantal mensen in het pand digitaal bij te houden en dit getal beschikbaar te stellen aan het publiek om aan te geven of het pand op dat moment vol zit. Zo kunnen studenten van te voren zien of er een rij voor de ingang kan zijn.\n" +
			"De deur dienst van de vereniging houd het aantal mensen in het band bij door een analoge teller. Bij de voordeur wordt een groep binnen gelaten in een tussenruimte. Deze groep wordt geteld en mag daarna naar binnen doorlopen. De teller wordt vervangen door een app die iedereen met een android telefoon (versie 2.1 en hoger) kan installeren en gebruiken. Deze app zal over de zelfde functionaliteit beschikken als een analoge teller. De app zal de waarde bij houden op een java server. Deze server zal beschikbaar zijn via het lokale netwerk van het pand. Zo kunnen meerdere app gebruikers het huidig aantal mensen in het pand bijhouden.\n" +
			"\n" +
			"De app kan het aantal mensen bijhouden, een waarschuwing geven als het maximum aantal mensen in het pand bereikt is, automatisch de locatie van de server in het lokale netwerk vinden, de rechten van gebruikers aanpassen, en de instellingen van de server aanpassen.";


	public ClientCommunicator( Socket socket, Server server )
	{
		this.server = server;
		this.socket = socket;

		this.thread = new Thread( this );	
		this.thread.start();
	}
	
	public void run()
	{
		try {
			String client = readFromClient();
			if (client.isEmpty()) return;

			System.out.println(client);
			JSONObject json = new JSONObject(client);
			boolean anonymous = false;
			String clientName = "unknown";

			try { clientName = json.getString("user"); }
			catch (JSONException e) { anonymous = true; }

			if (!anonymous) {
				try {
					final Integer value = json.getInt("subtotal");
					Data.editCounterValue(value);
				} catch (JSONException ignore) {
				}

				if (Data.isNewUser(clientName))
					Data.addUser(new User(clientName));
			}

			try {
				switch (json.getString("function")) {
					case "status":
						json.put("count", Data.getCounterValue());
						json.put("max", Data.getMaxCounterValue());
						break;
					case "users":
						if (!anonymous) {
							JSONArray users = new JSONArray();
							for (final User user : Data.getUsers())
								users.put(user.getID(), user.name);
							json.put("users", users);
						}
						break;
				}
			} catch (JSONException e) {}
			json.put("hostname", InetAddress.getLocalHost().getHostName());

			try {
				json.getString("icon");
				json.put("icon", Data.getIcon());
			} catch (JSONException | IOException e) {
				System.out.println(e.toString());
			}

			try {
				json.getString("primary_color");
				json.put("primary_color", Data.getPrimaryColor());
			} catch (JSONException e) {
				System.out.println(e.toString());
			}

			try {
				json.getString("secondary_color");
				json.put("secondary_color", Data.getSecondayColor());
			} catch (JSONException e) {
				System.out.println(e.toString());
			}

			server.addMessage(json.toString());
			writeToClient(json.toString());
		} catch (IOException | JSONException e) {
			System.out.println(e.toString());
		}
		System.out.println("exited client communication");
	}
	

	//bericht lezen
	private String readFromClient() throws IOException
	{
		InputStream inputStream = socket.getInputStream();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		String response = "";
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1){
			byteArrayOutputStream.write(buffer, 0, bytesRead);
			response += byteArrayOutputStream.toString("UTF-8");
		}
		return response;
	}
	
	
	//bericht schrijven
	private void writeToClient(final String message)
	{
		OutputStreamWriter outputStreamWriter = null;
		
		try
		{
			outputStreamWriter = new OutputStreamWriter( socket.getOutputStream() );
		}
		
		catch (IOException e2)
		{
			e2.printStackTrace();
		}
		
		if( outputStreamWriter != null )
		{
			BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
			PrintWriter writer = new PrintWriter( bufferedWriter, true );
			writer.println( message );
			writer.flush();
			writer.close();
			System.out.println(message);
		}
	}
}
 