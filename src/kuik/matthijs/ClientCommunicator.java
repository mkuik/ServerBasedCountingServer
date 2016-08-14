package kuik.matthijs;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class ClientCommunicator implements Runnable
{
	private Thread thread;
	private Socket socket;
	private List<Counter> data;

	public ClientCommunicator(Socket socket, List<Counter> data)
	{
		this.socket = socket;
		this.data = data;
		this.thread = new Thread( this );	
		this.thread.start();
	}

	public void run() {
		JSONObject out = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"), 1024);
			JSONObject in = new JSONObject(reader.readLine());
			Server.addMessage("IN " + in.toString());
			out = new JSONObject();
			JSONObject account = in.getJSONObject("ACCOUNT");
			switch (in.getString("CMD")) {
				case "META": {
					JSONArray array = new JSONArray();
					for (int i = 0; i != data.size(); ++i) {
						JSONObject meta = data.get(i).getServerMeta();
						meta.put("ID", i);
						array.put(meta);
					}
					out.put("ACCOUNT", account);
					out.put("COUNTERS", array);
					write(out.toString());
					break;
				}
				case "STATUS": {
					final int index = in.getInt("ID");
					if (in.has("EDIT")) {
						data.get(index).editCounterValue(in.getInt("EDIT"));
					}
					out.put("MAX", data.get(index).getMaxCounterValue());
					out.put("COUNT", data.get(index).getCounterValue());
					write(out.toString());
					break;
				}
				case "USERS": {
					final int index = in.getInt("ID");
					JSONArray users = new JSONArray();
					for (final User user : data.get(index).getUsers())
						users.put(user.getID(), user.name);
					out.put("USERS", users);
					write(out.toString());
					break;
				}
				case "UPLOAD": {
					final int index = in.getInt("ID");
					File file = new File(data.get(index).getIcon());
					file.getParentFile().mkdirs();
					FileOutputStream fos = new FileOutputStream(file);
					IOUtils.copy(socket.getInputStream(), fos);
					fos.close();
				}
				case "DOWNLOAD": {
					final int index = in.getInt("ID");
					File file = new File(data.get(index).getIconPath());
					try {
						FileInputStream fs = new FileInputStream(file);
						IOUtils.copy(fs, socket.getOutputStream());
					} catch (FileNotFoundException e) {
						Server.addMessage(e.toString());
					}
					break;
				}
				case "CREATE_COUNTER": {
					break;
				}
			}
		} catch (IOException | JSONException e) {
			Server.addMessage(e.toString());
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				Server.addMessage(e.toString());
			}
		}

		if (out != null) Server.addMessage("OUT " + out.toString());
	}

	//bericht lezen
	private String readLine() throws IOException
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
	private void write(final String message) throws IOException
	{
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		PrintWriter writer = new PrintWriter( bufferedWriter, true );
		writer.println( message );
		writer.flush();
		writer.close();
		System.out.println(message);
	}
}
 