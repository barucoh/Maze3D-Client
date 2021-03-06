package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkHandler extends Observable {
	ObjectOutputStream serverOutputStream;
	ObjectInputStream serverInputStream;
	ExecutorService requestsToServer;
	ExecutorService responseFromServer;
	Object objRecieved, objToSend;
	Socket socket;

	Model model;
	
	public boolean online = false;
	
	public NetworkHandler(Model model, Socket socket) { //, ObjectInputStream serverInputStream) {
		try {
			this.serverOutputStream = new ObjectOutputStream(socket.getOutputStream());
			this.serverInputStream = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//this.serverInputStream = serverInputStream;
		this.socket = socket;
		this.model = model;
		this.online = true;
		responseFromServer = Executors.newFixedThreadPool(5);
		requestsToServer = Executors.newFixedThreadPool(5);
		recieveFromServer();
	}
	
	public void recieveFromServer() {
		requestsToServer.execute(new Runnable() {
			@Override
			public void run() {
				while (online)
				{
					//synchronized (this) {
						try{
							objRecieved = serverInputStream.readObject();
							updateModel(objRecieved);
							//setChanged();
							//notifyObservers(objRecieved);
						}catch (ClassNotFoundException ex) {
							ex.printStackTrace();
						}catch(SocketException ex){
							updateModel("EXIT");
							online = false;
						}catch(IOException ex){
							ex.printStackTrace();
						}
					//}
				}
			}
		});
	}
	
	public void sendToServer(Object objToSend) {
		requestsToServer.execute(new Runnable() {
			@Override
			public void run() {
				//synchronized (this) {
					try{
						serverOutputStream.writeObject(objToSend);
						serverOutputStream.flush();
					}catch(IOException ex){
						ex.printStackTrace();
					}
				//}
			}
		});
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public void updateModel(Object objRecieved) {
		model.updateFromServer(objRecieved);
	}
	
	public void terminateClient() {
		this.online = false;
		this.requestsToServer.shutdownNow();
		this.responseFromServer.shutdownNow();
	}
}
