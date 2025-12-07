package exceptions;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ProxyServiceException extends RuntimeException {

    public ProxyServiceException(String message) {
        super(message);
    }

    public ProxyServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
