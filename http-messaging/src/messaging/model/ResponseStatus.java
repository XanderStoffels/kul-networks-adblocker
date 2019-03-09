package messaging.model;

public class ResponseStatus {
    private int statusCode;
    private String statusMessage;

    public ResponseStatus(int statusCode, String message) {
        this.statusCode = statusCode;
        this.setStatusMessage(message);
    }

    public boolean isSuccessful() {
        return (statusCode >= 200 && statusCode < 300);
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
