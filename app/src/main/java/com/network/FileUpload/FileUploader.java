package com.network.FileUpload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.network.exception.ErrorCode;
import com.network.exception.ServerInternalException;

import android.util.Log;


/**
 *
 */
public class FileUploader {
    private static final int BUFFER_SIZE = 4096;
    private static final String TAG = "FileUploader";
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @param requestURL the request url
     * @throws IOException
     */
    public FileUploader(String requestURL)
            throws IOException {
        this.charset = "UTF-8";

        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true);    // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setConnectTimeout(10*1000);
        httpConn.setReadTimeout(10*1000);
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, this.charset),
                true);
    }




    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    public void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);

        String contentType = URLConnection.guessContentTypeFromName(fileName);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        writer.append("Content-Type: " + contentType);
        writer.append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        int i = 0;
        int sendLen = 0;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
            sendLen += bytesRead;
            Log.d(TAG,"addFilePart sendLen="+sendLen+",current bytesRead="+bytesRead);
        }
        outputStream.flush();
        inputStream.close();
        writer.flush();
    }

    public void addFilePart(String fieldName, File uploadFile, int offset, int len)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"")
            .append(fieldName)
            .append("\"; filename=\"")
            .append(fileName).append("\"")
            .append(LINE_FEED);

        writer.append("Content-Type: application/octet-stream").append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        long skipped = inputStream.skip(offset);
        if(skipped != offset){
            throw new IOException("failed to skip offset " + offset +",skipped="+skipped);
        }

        int size = len > BUFFER_SIZE ? BUFFER_SIZE : len;
        int remain = len;
        byte[] buffer = new byte[size];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
            remain -= bytesRead;

            if (remain <= 0) {
                Log.d(TAG, "break when remain = "+remain);
                break;
            }

            if (remain < BUFFER_SIZE && remain > 0) {
                buffer = new byte[remain];
            }
        }
        outputStream.flush();
        inputStream.close();
//        writer.append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a header field to the request.
     *
     * @param name  - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Completes the request and receives response from the server.
     *
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public String execute() throws IOException, ServerInternalException {
        StringBuilder response = new StringBuilder("");
        writer.append(LINE_FEED).append("--").append(boundary).append("--").append(LINE_FEED);
        writer.flush();
        writer.close();

        // checks server's status code first
        int status = httpConn.getResponseCode();
        Log.d(TAG,"execute httpConn getResponseCode="+status);

        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            httpConn.disconnect();
        } else {

            throw new ServerInternalException(ErrorCode.getServerCode(status),
                    "Server returned non-OK status: " + status);
        }
        return response.toString();
    }


    /*MultipartUtility multipart = new MultipartUtility(requestURL, charset);

    // In your case you are not adding form data so ignore this
    //                This is to add parameter values
    for (int i = 0; i < myFormDataArray.size(); i++) {
        multipart.addFormField(myFormDataArray.get(i).getParamName(),
                myFormDataArray.get(i).getParamValue());
    }


    //add your file here.
    //                This is to add file content
    for (int i = 0; i < myFileArray.size(); i++) {
        multipart.addFilePart(myFileArray.getParamName(),
                new File(myFileArray.getFileName()));
    }

    List<String> response = multipart.finish();
    Debug.e(TAG, "SERVER REPLIED:");
    for (String line : response) {
        Debug.e(TAG, "Upload Files Response:::" + line);
// get your server response here.
        responseString = line;
    }*/

}

