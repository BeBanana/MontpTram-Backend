package eu.bebanana;

import spark.Spark;

public class Application {

    public static void main(String[] args) {
        Spark.secure("/home/xavier/cert/keystore.p12", "1234", null, "1234");
        Basics.init(Env.getInstance().slackHook);

    }

}


