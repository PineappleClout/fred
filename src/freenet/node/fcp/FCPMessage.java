package freenet.node.fcp;

import java.io.IOException;
import java.io.OutputStream;

import freenet.node.Node;
import freenet.support.SimpleFieldSet;

public abstract class FCPMessage {

	public void send(OutputStream os) throws IOException {
		SimpleFieldSet sfs = getFieldSet();
		sfs.setEndMarker(getEndString());
		String msg = sfs.toString();
		os.write((getName()+"\n").getBytes("UTF-8"));
		os.write(msg.getBytes("UTF-8"));
	}

	String getEndString() {
		return "EndMessage";
	}
	
	public abstract SimpleFieldSet getFieldSet();

	public abstract String getName();
	
	public static FCPMessage create(String name, SimpleFieldSet fs) throws MessageInvalidException {
		if(name.equals(ClientHelloMessage.name))
			return new ClientHelloMessage(fs);
		if(name.equals(ClientGetMessage.name))
			return new ClientGetMessage(fs);
		if(name.equals(ClientPutMessage.name))
			return new ClientPutMessage(fs);
		if(name.equals(GenerateSSKMessage.name))
			return new GenerateSSKMessage();
		if(name.equals("Void"))
			return null;
//		if(name.equals("ClientPut"))
//			return new ClientPutFCPMessage(fs);
		// TODO Auto-generated method stub
		return null;
	}

	/** Do whatever it is that we do with this type of message. 
	 * @throws MessageInvalidException */
	public abstract void run(FCPConnectionHandler handler, Node node) throws MessageInvalidException;

}
