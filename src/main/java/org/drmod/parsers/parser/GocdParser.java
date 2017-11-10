package org.drmod.parsers.parser;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.type.TypeReference;
import org.drmod.parsers.ProjectStatus;
import org.drmod.parsers.Status;
import org.drmod.parsers.exception.ResponseException;
import org.drmod.parsers.mapper.GocdMapper;
import org.drmod.parsers.model.gocd.Pagination;

import java.io.IOException;

public class GocdParser implements Parser {

    private static final int HTTP_RESPONSE_OK = 200;
    private static final int TIMEOUT = 5000;

    private String name;
    private String url;
    private String basicAuth;

    public GocdParser(String name, String url, String basicAuth) {
        this.name = name;
        this.url = url;
        this.basicAuth = basicAuth;
    }

    public ProjectStatus call() throws Exception {
        Status status;

        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth);

            HttpParams params = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);

            HttpResponse httpResponse = httpClient.execute(httpGet);

            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != HTTP_RESPONSE_OK) {
                throw new ResponseException();
            }

            status = GocdMapper.parse(httpResponse.getEntity().getContent(), new TypeReference<Pagination>() {});
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
