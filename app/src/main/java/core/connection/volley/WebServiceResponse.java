package core.connection.volley;

import java.util.List;
import java.util.Map;

import core.base.BaseResponse;

public class WebServiceResponse extends BaseResponse {

    public WebServiceResponse(byte[] content, Map<String, String> headers, Map<String, List<String>> rawHeaders) {
        super(content, headers, rawHeaders);
    }

}
