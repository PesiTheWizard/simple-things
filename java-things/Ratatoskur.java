import java.awt.*;
import java.awt.event.*;
import java.net.Socket;
import java.io.*;

public class Ratatoskur extends Frame implements ActionListener
{
	Label l_url = new Label("Server:");
	Label l_port = new Label("Port:");
	TextField urltovisit = new TextField(30);
	TextField portnumber = new TextField(6);
	Label l_req = new Label("Request:");
	TextArea requesttext = new TextArea("",0,0,TextArea.SCROLLBARS_NONE);
	Button activate = new Button("Send");
	Label l_error = new Label("[ - - - Error Message space - - - ]");//setText(String)
	Button custr1 = new Button("Basic HTTP/1.0");
	Button custr2 = new Button("Basic Gopher");
	/*MenuBar hzbr = new MenuBar();
	Menu stuff = new Menu("stuff");*/

	final String CRLF = "" + (char)13 + (char)10;

	public static void main(String args[])
	{
		Ratatoskur hlíðskjálf = new Ratatoskur();
		hlíðskjálf.addWindowListener(new CloseWindow());
	}

	public Ratatoskur()
	{
		super("Ratatöskur:Manual TCP requests");
		this.addWindowListener(new CloseWindow());
		this.setSize(485,300);//485,315 with the MenuBar
		this.setLayout(new FlowLayout());
		setBackground(new Color(209,210,200));
		this.setResizable(false);

		/*hzbr.add(stuff);
		this.setMenuBar(hzbr);*/

		activate.addActionListener(this);
		custr1.addActionListener(this);
		custr2.addActionListener(this);

		this.add(l_url);
		this.add(urltovisit);
		this.add(l_port);
		this.add(portnumber);
		this.add(l_req);
		this.add(custr1);
		this.add(custr2);
		this.add(requesttext);
		this.add(activate);
		this.add(l_error);

		this.show();
	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == activate)
		{
			sendrequest();
		}
		else if(e.getSource() == custr1)
		{
			portnumber.setText("80");
			requesttext.setText("GET / HTTP/1.0"+CRLF+CRLF);
		}
		else if(e.getSource() == custr2)
		{
			portnumber.setText("70");
			requesttext.setText(CRLF);
		}
	}

	private void sendrequest()
	{
		int port;
		try
		{
			port = Integer.parseInt(portnumber.getText());
		}
		catch(NumberFormatException e)
		{
			l_error.setText("Port number fail");
			System.out.println(e);
			return;
		}
		String host = urltovisit.getText();
		String req = requesttext.getText();
		Socket iteng;
		String curl;
		try
		{
			iteng = new Socket(host,port);
		}
		catch(IllegalArgumentException e)
		{
			l_error.setText("No such port");
			System.out.println(e);
			return;
		}
		catch(IOException e)
		{
			l_error.setText("Couldn't connect");
			System.out.println(e);
			return;
		}
		curl = iteng.getInetAddress().getHostAddress();
		BufferedReader in;
		PrintWriter out;
		String line;
		try
		{
			in = new BufferedReader(new InputStreamReader(iteng.getInputStream()));
			out = new PrintWriter(iteng.getOutputStream(), true);
			out.print(req);
			out.flush();
			while((line = in.readLine()) != null)
			{
				System.out.println(line);
			}
			in.close();
			out.close();
			l_error.setText("success at:"+curl);
		}
		catch(IOException e)
		{
			l_error.setText("fail at:"+curl);
			System.out.println(e);
			return;
		}
		//
	}
}

class CloseWindow extends WindowAdapter
{
	public void windowClosing(WindowEvent e)
	{
		System.exit(0);
	}
}
