package com.cuea.notifications;

public class HomeView {
    //this class will hold the objects required in users home screens

    //some variables
    private int imageviewid;
    private String maintitle;
    private String subtitle;

    //create a constructor class to set the values
    public HomeView(int imageviewid, String maintitle, String subtitle) {
        this.imageviewid = imageviewid;
        this.maintitle = maintitle;
        this.subtitle = subtitle;
    }


    // getter method for returning the ID of the imageview
    public int getImageviewid() {
        return imageviewid;
    }

    //getter method for returning the maintitle
    public String getMaintitle() {
        return maintitle;
    }

    //getter method for returning subtitle
    public String getSubtitle() {
        return subtitle;
    }
}
