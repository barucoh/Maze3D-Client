package client;

import java.io.PrintWriter;
import java.net.Socket;

import model.MyModelClient;
import model.NetworkHandler;
import presenter.Presenter;
import view.MazeWindow;

public class RunClient {
	public static void main(String [] args) throws Exception{
		System.out.println("Client Side");
		Socket theServer = new Socket("localhost",5400);
		System.out.println("connected to server!");
		
		PrintWriter outToServer = new PrintWriter(theServer.getOutputStream());
		//ObjectInputStream inFromServer = new ObjectInputStream(theServer.getInputStream());
    	
		MazeWindow window = new MazeWindow();
		MyModelClient model = new MyModelClient();
		
		NetworkHandler networkHandler = new NetworkHandler(model, theServer); //outToServer);//, inFromServer);
		model.setNetworkHandler(networkHandler);
		
		Presenter presenter = new Presenter(model, window);
		model.addObserver(presenter);
		window.addObserver(presenter);
				
		window.start();
		theServer.close();
		
		
		
		
		/*
		System.out.println(in.readLine());// ok
		
		BufferedInputStream maggieFromFile=new BufferedInputStream(new FileInputStream("resources/Maggie.gif"));
		BufferedOutputStream maggieToServer=new BufferedOutputStream(theServer.getOutputStream());
	
		byte[] myBuffer=new byte[100];
		int bytesRead;
		while((bytesRead=maggieFromFile.read(myBuffer))!=-1){
			maggieToServer.write(myBuffer, 0, bytesRead);
		}		
		
		maggieToServer.flush();		
		maggieFromFile.close();
		
		BufferedReader asciiMaggieFromServer=new BufferedReader(new InputStreamReader(theServer.getInputStream()));
		String line;
		while(!(line=asciiMaggieFromServer.readLine()).equals("done")){
			System.out.println(line);
		}
		
		outToServer.println("exit");
		outToServer.flush();
	
		asciiMaggieFromServer.close();
		outToServer.close();
		
		theServer.close();*/
	}
}
