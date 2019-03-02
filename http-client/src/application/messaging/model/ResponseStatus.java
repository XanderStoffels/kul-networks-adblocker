package application.messaging.model;

public class ResponseStatus {
    private String httpVersion;
    private int statusCode;
    private String statusMessage;

    public ResponseStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isSuccessful() {
        return (statusCode >= 200 && statusCode < 300);
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
