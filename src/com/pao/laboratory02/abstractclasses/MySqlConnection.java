package com.pao.laboratory02.abstractclasses;

/** Subclasă concretă — implementare MySQL. */
public class MySqlConnection extends DBConnection {

    public MySqlConnection(String url) {
        super(url);
    }

    @Override
    public void connectToDb() {
        System.out.println("[MySQL] Conectare la: " + getUrl());
        System.out.println("[MySQL] Driver: com.mysql.cj.jdbc.Driver");
    }
}
