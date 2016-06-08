package core.base;

import java.util.HashMap;

/**
 * @author Tyrael
 * @version 1.0 <br>
 *          <br>
 *          <b>Class Overview</b> <br>
 *          <br>
 *          Represents an abstract class for forming common parameters content
 *          and headers of a web service message, every webservice must use a
 *          derived class from this abstract and override the makeRequestParams
 *          and makeRequestHeaders <br>
 * @since April 2014
 */

@SuppressWarnings("unused")
public abstract class BaseParam implements Param {

    /**
     * The set of key-value headers for the webservice message
     */
    private final HashMap<String, String> headers;

    public BaseParam() {
        this.headers = new HashMap<>();
    }

    /**
     * This method forms the body of the request in byte array state
     *
     * @return a byte array of request body
     */
    public abstract byte[] makeRequestBody();

    /**
     * This method forms a set of key-value headers applied for all the
     * webservice request, derived class should override this method to append
     * needed values
     *
     * @return a set of key-value parameters for the request
     */
    public HashMap<String, String> makeRequestHeaders() {
        return headers;
    }

    /**
     * This method forms the value of content type header, if the value is not empty (null or BLANK),
     * the Content-Type header will be removed and replace by this value. This is Volley dependent
     * issue
     *
     * @return value of Content-Type header
     */
    public abstract String makeBodyContentType();
}
