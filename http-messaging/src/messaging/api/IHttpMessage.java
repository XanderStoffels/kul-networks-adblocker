package messaging.api;


public interface IHttpMessage {
    IHttpHeaders getHeaders();

    void setHttpVersion(String httpVersion);
    String getHttpVersion();

    void setBody(byte[] body);
    byte[] getBody();
}
