package core.connection.volley;

import java.util.List;
import java.util.Map;

import core.base.BaseResponse;
import core.util.Constant.RequestTarget;

public class QueueResponse extends BaseResponse {

    private final RequestTarget target;

    public QueueResponse(byte[] content, Map<String, String> headers, Map<String, List<String>> rawHeaders,
                         RequestTarget target) {
        super(content, headers, rawHeaders);
        this.target = target;
    }

    /**
     * @return the target
     */
    public RequestTarget getRequestTarget() {
        return target;
    }
}
