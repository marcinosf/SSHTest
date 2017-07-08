package pl.marcinos.ssh;

import java.io.IOException;
import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


public class App 
{
    public static void main( String[] args )
    {
    	JSch ssh = new JSch();
    	String host = "0.0.0.0";
    	String user = "root";
    	String password = "screencast";
    	
    	String cmd = "pwd && ls -la";
    	
    	try {
			Session session = ssh.getSession(user, host, 32768);
			session.setPassword(password);
			JSch.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			Channel channel=session.openChannel("exec");
			((ChannelExec)channel).setCommand(cmd);
			channel.setInputStream(null);
			((ChannelExec)channel).setErrStream(System.err);
			
			InputStream in=channel.getInputStream();
			System.out.println("Executing " + cmd + " on " + user +"@" + host);
			channel.connect();
			
			byte[] tmp=new byte[1024];
			while(true){
			  while(in.available()>0){
			      int i=in.read(tmp, 0, 1024);
			      if(i<0)break;
			      System.out.print(new String(tmp, 0, i));
			  }
			  if(channel.isClosed()){
			    if(in.available()>0) 
			    	continue; 
			    System.out.println("exit-status: " + channel.getExitStatus());
			    if(channel.getExitStatus() != 0) 
			    	System.err.println("Command failed!");
			    else
			    	System.out.println("Command finished successfully.");
			    break;
			  }
			}
			  channel.disconnect();
			  session.disconnect();
			
		} catch (JSchException e) {
			System.err.println("Unexpected exception.\n" + e);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Unexpected IOException!\n" + e);
			e.printStackTrace();
		}
    }
}