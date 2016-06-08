package core.base;

import java.util.List;
import java.util.Map;

/**
 * @author Tyrael
 * @version 1.0 <br>
 *          <br>
 *          <b>Class Overview</b> <br>
 *          <br>
 *          Represents an abstract class for network response, including the data as a
 *          string content and a map of headers <br>
 * @since April 2014
 */

@SuppressWarnings("unused")
public abstract class BaseResponse {

    /**
     * The byte content data returned from the service
     */
    private byte[] content;

    /**
     * The headers key-value set returned from the service
     */
    private Map<String, String> headers;

    /**
     * The headers key-(multiple) values set returned from the service
     */
    private Map<String, List<String>> rawHeaders;

    public BaseResponse(byte[] content, Map<String, String> headers, Map<String, List<String>> rawHeaders) {
        super();
        this.content = content;
        this.headers = headers;
        this.rawHeaders = rawHeaders;
    }

    /**
     * @return the content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * @return the headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * @return the raw headers
     */
    public Map<String, List<String>> getRawHeaders() {
        return rawHeaders;
    }

    /**
     * @param rawHeaders the raw headers to set
     */
    public void setRawHeaders(Map<String, List<String>> rawHeaders) {
        this.rawHeaders = rawHeaders;
    }
}
