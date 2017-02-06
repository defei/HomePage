package org.codelogger.homepage.bean;

/**
 * Created by defei on 2/5/17.
 */
public class Label {

    private String text;

    private String color;

    private String imgUrl;

    /**
     * {@linkplain Label#text}
     */
    public String getText() {
        return text;
    }

    /**
     * {@linkplain Label#text}
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * {@linkplain Label#color}
     */
    public String getColor() {
        return color;
    }

    /**
     * {@linkplain Label#color}
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * {@linkplain Label#imgUrl}
     */
    public String getImgUrl() {
        return imgUrl;
    }

    /**
     * {@linkplain Label#imgUrl}
     */
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
