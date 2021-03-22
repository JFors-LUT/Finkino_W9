package com.example.finkino_w9;

public class Teatteri {
    private int id;
    private String name;

    public Teatteri(int koodi, String nimi){
        id = koodi;
        name = nimi;
    }

    public String getName(){ return name; }

    public int getId() { return id; }
}

