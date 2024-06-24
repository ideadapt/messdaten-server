package services;

public class ProtocolNotSupportedException extends RuntimeException{

    public ProtocolNotSupportedException(String protocol) {
        super("Not supported protocol-type: " + protocol);
    }
}
