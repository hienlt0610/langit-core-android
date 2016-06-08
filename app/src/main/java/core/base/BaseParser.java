package core.base;

import core.util.Constant.RequestTarget;

/**
 * @author Tyrael
 * @version 1.0 <br>
 *          <br>
 *          <b>Class Overview</b> <br>
 *          <br>
 *          - Represents an abstract class for performing parsing the content of
 *          a response. The parsing action based on the return format and the
 *          request target that requested<br>
 *          - Every request must have its own parser and extended from this base
 *          parser<br>
 * @since April 2014
 */

@SuppressWarnings({"unused", "ConstantConditions"})
public abstract class BaseParser {
    /**
     * This method perform parsing the response from the webservice based on its
     * return format and request target
     *
     * @param content The content to parse
     * @param target  The target function of the webservice
     * @return The result data after performing parse content
     */
    public static BaseResult parse(String content, RequestTarget target) {
        BaseResult data = null;
        switch (target) {
            // Implement parsing here
            default:
                break;
        }
        return data;
    }

    /**
     * This method is required for the derived class to perform the data parsing
     * of the content <br>
     * <p/>
     * The derived class must implemented this method and return
     * <code>null</code> if there is any error occurs
     */
    public abstract BaseResult parseData(String content);
}
