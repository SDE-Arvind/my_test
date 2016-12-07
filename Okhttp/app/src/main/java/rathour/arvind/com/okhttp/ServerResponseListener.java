package rathour.arvind.com.okhttp;

/**
 * This is the interface for getting the server response from the NetworkManagerTask.
 */
public interface ServerResponseListener {

    /**
     * Fetches response received from the server.
     * This method also calls various methods based on the call purpose of the server request to
     * handle the server response.
     *
     * @param iResponse response received from the server.
     * @param iCallPurpose call purpose for the server request.
     */
    void responseFromServer(Object iResponse, int iCallPurpose);

    /**
     * Fetches the response code from the server and if any exception occurs.
     * Handles any error that occurs.
     *
     * @param iResponseCode response code from the server.
     * @param iException exception while making request to the server.
     */
    void errorOccurred(int iResponseCode, Exception iException, int iCallPurpose);
}
