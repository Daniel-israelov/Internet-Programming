package clientserver;

import java.io.InputStream;
import java.io.OutputStream;

//-->>> DONE <<<--

/**
 * A functional Interface that lets the server read & execute client task request<br>
 * and return a response.
 */
public interface IHandler {
    void handleClient(InputStream fromClient, OutputStream toClient);
    void resetClassFields();
}
