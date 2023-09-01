package server;

import spark.Route;

/**
 * This is a generic handler interface that itself extends the Route interface. It adds the
 * successResponse() and failureResponse() methods. In our implementations, these success and
 * failure responses are serializations of HashMap from String to Object, but if another user
 * wants to serialize in a different way, they are free to do so by implementing their success
 * and failure responses in a different way.
 */
public interface Handler extends Route {
  String successResponse();
  String failureResponse(String errorMessage);
}
