package org.comppress.customnewsapi;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TwitterMain {

    // Old Access Keys
    //1544755270257098755-EhrD8hrtfHWXiBY7ahIZnf2HB2jMDB
    //SyuOk5XUvD20k79B8y2dqgq1D0K56BriNY7M2PeGje5m0

    // OAuth 2.0 Client ID and Client Secret
    //NjBWbndMbEtjZ0gtNHIwT1FhLXk6MTpjaQ
    //IWnQ1hwh_9D-Jt9KRC3XC_SeGR6e-eZeTw9LF6DrQY7Txke_N_
    public static void main(String[] args) throws TwitterException {
        var twitter = Twitter.newBuilder()
                .oAuthConsumer("Yuevz5VBlOywZcqT1yxrkfeJt", "bMR9VSQiAwQ4Wh3BxC8Rqjthk0Z3nVpP2lmQsjyxTophjrblMe")
                .oAuthAccessToken("1544755270257098755-DrFlJJrKeunFbBFQ4qC3h856g09ouo", "Q3IqmZCMEmXWbLYm0cizFoLQWYiWFZK7aRAQ6R1p42h4N")
                .build();
        twitter.v1().tweets().updateStatus("Hello Twitter API!");
    }
}
