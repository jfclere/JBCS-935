/** Send an HTTP request with chunked body.
 *
 */
import java.io.OutputStream;
import java.net.Socket;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class ClientChunk {
    int verboseLevel = Integer.parseInt(System.getProperty("verboseLevel", "1"));
    int chunkLevel = Integer.parseInt(System.getProperty("chunkLevel", "1"));
    String CRLF = "\r\n";
    String host = System.getProperty("host", "localhost");
    String hostname = System.getProperty("hostname", "localhost");
    String port = System.getProperty("port", "8000");
    int postwait = Integer.parseInt(System.getProperty("postwait", "6"));
    // the path of the url to connect to
    // String path = System.getProperty("path", "/snoop/SnoopServlet");
    String path = System.getProperty("path", "/myapp/BigField");

    String HTTP_HEADER = 
	"POST " + path + " HTTP/1.1" + CRLF +
	"User-Agent: " + this.getClass().getName() + " (chunked-test)" + CRLF +
	"Content-Type: application/x-www-form-urlencoded" + CRLF +
	"Connection: Close" + CRLF +
	"Host: " + hostname + CRLF;
    String HTTP_HEADER_CHUNKED =
	"Transfer-Encoding: chunked" + CRLF;
    String HTTP_HEADER_END =
	CRLF;
    
    /** ftp://ftp.isi.edu/in-notes/rfc2616.txt */
    void sendHeaderAndChunkedBody(OutputStream out, String[] chunks) throws Exception {
	int length = 0;
	out.write(HTTP_HEADER.getBytes());
	if (verboseLevel > 0) System.err.write(HTTP_HEADER.getBytes());
        if (chunkLevel > 0) {
            out.write(HTTP_HEADER_CHUNKED.getBytes());
	    if (verboseLevel > 0) System.err.write(HTTP_HEADER_CHUNKED.getBytes());
        } else {
	    for (int i = 0; i < chunks.length; i++) {
	        String chunkData   = chunks[i];
                length = length + chunkData.length(); 
            }
            String HTTP_CONTENT = "Content-Length: " + length + CRLF;
            out.write(HTTP_CONTENT.getBytes());
	    if (verboseLevel > 0) System.err.write(HTTP_CONTENT.getBytes());
        }
        out.write(HTTP_HEADER_END.getBytes());
	Thread.currentThread().sleep(postwait*1000);
	for (int i = 0; i < chunks.length; i++) {
	    String chunkData   = chunks[i];
	    String chunkSize = Integer.toHexString(chunks[i].length());
            if (chunkLevel > 0) {
	        out.write((chunkSize + CRLF).getBytes());
	        if (verboseLevel > 0) System.err.write((chunkSize + CRLF).getBytes());
	        out.write((chunkData + CRLF).getBytes());
	        if (verboseLevel > 0) System.err.write((chunkData + CRLF).getBytes());
            } else {
	        out.write((chunkData).getBytes());
	        if (verboseLevel > 0) System.err.write((chunkData).getBytes());
            }
	    out.flush();
	}
	// last-chunk
        if (chunkLevel > 0)
	    out.write(("0" + CRLF).getBytes());
	// trailer
        out.write(CRLF.getBytes());
	out.flush();
    }
    // XXX Should not be named sendChunked when it also reads response???
    String sendChunkedRequest(String[] chunks) throws Exception {
	if (verboseLevel > 0) {
           if (chunkLevel > 0)
             System.err.println("Using chunk");
           else
             System.err.println("NOT Using chunk");
        }
	if (verboseLevel > 0) System.err.println("// Sending request to host " + host + " port " + port);
        Socket socket = new Socket(host, Integer.parseInt(port));
	sendHeaderAndChunkedBody(socket.getOutputStream(), chunks);
	if (verboseLevel > 0) System.err.println("// Reading response from host " + host + " port " + port);
	ByteArrayOutputStream b = new ByteArrayOutputStream();
	readFully(socket.getInputStream(), b);
	socket.close();
	return b.toString();
    }
    void readFully(InputStream in, OutputStream out) throws IOException {
	BufferedInputStream bin = new BufferedInputStream(in);
	for (int c = bin.read(); c!= -1; c = bin.read()) {
	    if (verboseLevel > 0) {System.err.print((char) c);}
	    out.write(c);
	}
	out.flush();
    }
    public static void main(String[] args) throws Exception {
        ClientChunk echoClient = new ClientChunk();
	if (args.length != 0) {
            
	    System.err.println("Usage: java [-Dhost=host] [-Dport=port] [-Dhostname=hostname] [-Dpath=path] [-Dpostwait=wait] [-DchunkLevel=0/1] " + echoClient.getClass().getName());
            System.exit(1);
	} else {
	    echoClient.sendChunkedRequest(new String[]{"a=0", "&b=1"});
	}
    }
}
