package com.startag.martguy.model;

public class MusicAlbum {

    int image;
    String genrename;
    String origenrename;

    public MusicAlbum( String genrename) {

        this.genrename = genrename;

        String genrelower=genrename.toLowerCase();
        this.origenrename = genrelower.replaceAll("\\s+","");
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getGenrename() {
        return genrename;
    }

    public void setGenrename(String genrename) {
        this.genrename = genrename;
    }

    public String getOrigenrename() {
        return origenrename;
    }

    public void setOrigenrename(String origenrename) {
        this.origenrename = origenrename;
    }
}
