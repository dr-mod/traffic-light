package org.drmod.parsers.parser;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.type.TypeReference;
import org.drmod.parsers.ProjectStatus;
import org.drmod.parsers.Status;
import org.drmod.parsers.exception.ResponseException;
import org.drmod.parsers.mapper.JenkinsMapper;
import org.drmod.parsers.model.JenkinsModel;

import java.io.IOException;

public class JenkinsParser implements Parser {

    private static final int TIMEOUT = 5000;

    private String name;
    private String url;

    public JenkinsParser(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public ProjectStatus call() throws Exception {
        Status status;

        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpGet httpGet = new HttpGet(url);

            HttpParams params = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
            HttpResponse httpResponse = httpClient.execute(httpGet);

            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new ResponseException();
            }

            status = JenkinsMapper.parse(httpResponse.getEntity().getContent(), new TypeReference<JenkinsModel>() {});
        } catch (IOException | IllegalStateException e) {
            status = Status.CONNECTION_ERROR;
        } catch (ResponseException e) {
            status = Status.RESPONSE_ERROR;
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return new ProjectStatus(name, status);
    }

}
