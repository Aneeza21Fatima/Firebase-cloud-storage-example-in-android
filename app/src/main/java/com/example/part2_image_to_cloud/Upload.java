package com.example.part2_image_to_cloud;


public class Upload {
    private String title;
    private String m_image_url;
    private int imageWidth;
    private int imageHeight;

    public Upload(String name, String url){
        if(name.trim().equals("")){
            name="Notice";
        }
        title=name;
        m_image_url=url;

    }

    public void setName(String name) {
        title = name;
    }

    public void setImage_url(String image_url) {
        m_image_url = image_url;
    }

    public String getImage_url() {
        return m_image_url;
    }

    public String getName() {
        return title;
    }

    public Upload() {

    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }
}
