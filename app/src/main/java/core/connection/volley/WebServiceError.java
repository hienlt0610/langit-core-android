package core.connection.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import core.util.Constant.RequestTarget;


@SuppressWarnings("unused")
public class WebServiceError extends VolleyError {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final RequestTarget target;
    private final NetworkResponse response;

    public WebServiceError(RequestTarget target, VolleyError error) {
        super(error);
        this.target = target;
        this.response = error.networkResponse;
    }

    /**
     * @return the target
     */
    public RequestTarget getRequestTarget() {
        return target;
    }

    /**
     * @return the response
     */
    public NetworkResponse getResponse() {
        return response;
    }

}
