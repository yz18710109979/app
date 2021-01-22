package com.jingyou.main.config;

import java.util.Properties;

public class Conf {
    private Conf() {}
    private static Conf conf = new Conf();
    public Properties properties = new Properties();

    public static Conf getInstance() {return conf;}
}
