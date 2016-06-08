package core.connection;

import core.base.BaseApplication;
import core.base.BaseProperties;
import core.base.Param;
import core.connection.WebServiceRequester.WebServiceResultHandler;
import core.connection.queue.QueueElement;
import core.connection.request.BackgroundServiceRequest;
import core.connection.request.ParallelServiceRequest;
import core.connection.request.QueueServiceRequest;
import core.connection.request.WebServiceRequest;
import core.util.Constant;
import core.util.Constant.RequestMethod;
import core.util.Constant.RequestTarget;
import core.util.Constant.RequestType;
import core.util.DLog;

@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "unused"})
public class Requester {
    private static final String TAG = "Requester";

    public static boolean startWSRequest(String tag, RequestTarget target,
                                         String[] extras, Param content, WebServiceResultHandler handler) {

        try {
            WebServiceRequest request;
            if (BaseProperties.wsRequester == null)
                BaseProperties.wsRequester = WebServiceRequester
                        .getInstance(BaseApplication.getContext());
            switch (target) {
                case WEBSERVICE_REQUEST:
                    request = new WebServiceRequest(tag, RequestType.HTTP,
                            RequestMethod.POST, Constant.SERVER_URL, target,
                            RequestTarget.build(target, extras), content,
                            BaseProperties.wsRequester, handler);
                    break;
                default:
                    throw new Exception(
                            "Requester: No request target found");
            }
            BaseProperties.wsRequester.startRequest(request);
            DLog.d(TAG, request.getRequestMethod().name().toUpperCase()
                    + " >> " + request.getUrl());
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            DLog.d(TAG, "Request canceled!");
            return false;
        }
    }

    public static boolean startBackgroundRequest(String tag,
                                                 RequestTarget target, String[] extras, Param content) {
        try {
            BackgroundServiceRequest request;
            if (BaseProperties.bgRequester == null)
                BaseProperties.bgRequester = BackgroundServiceRequester
                        .getInstance(BaseApplication.getContext());
            switch (target) {
                case BACKGROUND_REQUEST:
                    request = new BackgroundServiceRequest(tag, RequestType.HTTP,
                            RequestMethod.GET, Constant.SERVER_URL, target,
                            RequestTarget.build(target, extras), content,
                            BaseProperties.bgRequester);
                    break;
                default:
                    throw new Exception(
                            "Requester: No request target found");
            }

            BaseProperties.bgRequester.startRequest(request);
            DLog.d(TAG, request.getRequestMethod().name().toUpperCase()
                    + " >> " + request.getUrl());
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            DLog.d(TAG, "Background request canceled!");
            return false;
        }
    }

    public static boolean startQueueRequest(String tag, RequestTarget target,
                                            String[] extras, QueueElement.Type type, Param content) {
        try {
            QueueServiceRequest request;
            if (BaseProperties.queueRequester == null)
                BaseProperties.queueRequester = QueueServiceRequester
                        .getInstance(BaseApplication.getContext());
            switch (target) {
                case WEBSERVICE_REQUEST:
                    request = new QueueServiceRequest(tag, RequestType.HTTP,
                            RequestMethod.POST, Constant.SERVER_URL, target,
                            RequestTarget.build(target, extras), content,
                            BaseProperties.queueRequester);
                    break;
                default:
                    throw new Exception(
                            "Requester: No request target found");
            }
            BaseProperties.queueRequester.addQueueRequest(new QueueElement(
                    request, type));
            DLog.d(TAG, request.getRequestMethod().name().toUpperCase()
                    + " >> " + request.toString());
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            DLog.d(TAG, "Queue request canceled!");
            return false;
        }
    }

    public static boolean startParallelRequest(String tag, RequestTarget target,
                                               String[] extras, Param content) {
        try {
            ParallelServiceRequest request;
            if (BaseProperties.parallelRequester == null)
                BaseProperties.parallelRequester = ParallelServiceRequester
                        .getInstance(BaseApplication.getContext());
            switch (target) {
                case WEBSERVICE_REQUEST:
                    request = new ParallelServiceRequest(tag, RequestType.HTTP,
                            RequestMethod.POST, Constant.SERVER_URL, target,
                            RequestTarget.build(target, extras), content,
                            BaseProperties.parallelRequester);
                    break;
                default:
                    throw new Exception(
                            "Requester: No request target found");
            }
            ParallelServiceRequester.addRequest(request);
            DLog.d(TAG, request.getRequestMethod().name().toUpperCase()
                    + " >> " + request.toString());
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            DLog.d(TAG, "Parallel request canceled!");
            return false;
        }
    }
}
