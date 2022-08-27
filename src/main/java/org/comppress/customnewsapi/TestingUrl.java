package org.comppress.customnewsapi;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.IOException;
import java.net.URL;

public class TestingUrl {

    public static void main(String[] args) throws FeedException, IOException {

        String url_string = "http://www.augsburger-allgemeine.de/sport/rss";
        URL url = new URL(url_string);

        for (int i = 0; i < 20; i++) {
            try {
                System.out.println("Calling the url " + i);
                callUrl(url);
                // SyndFeedInput input = new SyndFeedInput();
                // SyndFeed feed = input.build(new XmlReader(url));
                /* HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
                System.out.println(sb);
                */
            } catch (Throwable e) {
                //e.printStackTrace();
                //callUrl(url);
                continue;
            }
        }
    }

    public static void callUrl(URL url) throws IOException, FeedException {
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(url));
    }
}
