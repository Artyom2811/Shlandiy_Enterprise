package util;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class RestService {
    //    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final CloseableHttpClient httpClient = HttpClients.createMinimal();

    public String getBodyFromGetRequest(String linkOfRequest) {
        HttpGet request = new HttpGet(linkOfRequest);
        String answer = null;
        // add request headers
        try (CloseableHttpResponse response = httpClient.execute(request)) {

            // Get HttpResponse Status
//            System.out.println(response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
//            Header headers = entity.getContentType();
//            System.out.println(headers);

            if (entity != null) {
                // return it as a String
//                String result = EntityUtils.toString(entity);
//                System.out.println(result);
                answer = EntityUtils.toString(entity);
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }
}