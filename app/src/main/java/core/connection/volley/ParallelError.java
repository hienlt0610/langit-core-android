package core.connection.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import core.util.Constant;

public class ParallelError extends VolleyError {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final Constant.RequestTarget target;
    private final String tag;
    private final NetworkResponse response;

    public ParallelError(String tag, Constant.RequestTarget target, VolleyError error) {
        super(error);
        this.target = target;
        this.response = error.networkResponse;
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    /**
     * @return the target
     */
    public Constant.RequestTarget getRequestTarget() {
        return target;
    }

    /**
     * @return the response
     */
    public NetworkResponse getResponse() {
        return response;
    }

}
