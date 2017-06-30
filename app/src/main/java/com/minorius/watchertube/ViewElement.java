package com.minorius.watchertube;

/**
 * Created by minorius on 22.06.2017.
 */

public class ViewElement {

    private String title;
    private String description;
    private String videoUrl;
    private String imageUrl;
    private String duration;

    public ViewElement(String title, String description, String videoUrl, String imageUrl, String duration) {
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.imageUrl = imageUrl;
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "ViewElement{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
