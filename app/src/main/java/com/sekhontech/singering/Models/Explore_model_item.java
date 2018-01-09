package com.sekhontech.singering.Models;

import java.io.Serializable;

/**
 * Created by ST_004 on 19-08-2016.
 */
public class Explore_model_item implements Serializable {

    public String id;
    public String uid;
    public String title;
    public String description;
    public String first_name;
    public String last_name;
    public String name;
    public String tag;
    public String art;
    public String buy;
    public String record;
    public String release;
    public String license;
    public String size;
    public String download;
    public String time;
    public String likes;
    public String downloads;
    public String views;
    public String Public;

    public String imageART;

    public String filedownloaded_path;

   // public Bitmap bitmap;

    public String getFiledownloaded_path() {
        return filedownloaded_path;
    }

    public void setFiledownloaded_path(String filedownloaded_path) {
        this.filedownloaded_path = filedownloaded_path;
    }

    /* public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

    */
    public boolean check = false;


    public Explore_model_item() {
    }


    public Explore_model_item(String id, String uid, String title, String description, String first_name, String last_name, String name,
                              String tag, String art, String buy, String record, String release, String license, String size,
                              String download, String time, String likes, String downloads, String views,String Public,boolean check) {
        this.id = id;
        this.uid = uid;
        this.title = title;
        this.description = description;
        this.first_name = first_name;
        this.last_name = last_name;
        this.name = name;
        this.tag = tag;
        this.art = art;
        this.buy = buy;
        this.record = record;
        this.release = release;
        this.license = license;
        this.size = size;
        this.download = download;
        this.time = time;
        this.likes = likes;
        this.downloads = downloads;
        this.views = views;
        this.Public=Public;
        this.check=check;
    }

    public String getImageART() {
        return imageART;
    }

    public void setImageART(String imageART) {
        this.imageART = imageART;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public String getBuy() {
        return buy;
    }

    public void setBuy(String buy) {
        this.buy = buy;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getDownloads() {
        return downloads;
    }

    public void setDownloads(String downloads) {
        this.downloads = downloads;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getPublic() {
        return Public;
    }

    public void setPublic(String aPublic) {
        Public = aPublic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Explore_model_item)) return false;

        Explore_model_item that = (Explore_model_item) o;

        if (!getId().equals(that.getId())) return false;
        if (!getUid().equals(that.getUid())) return false;
        if (!getTitle().equals(that.getTitle())) return false;
        if (!getDescription().equals(that.getDescription())) return false;
        if (!getFirst_name().equals(that.getFirst_name())) return false;
        if (!getLast_name().equals(that.getLast_name())) return false;
        if (!getName().equals(that.getName())) return false;
        if (!getTag().equals(that.getTag())) return false;
        if (!getArt().equals(that.getArt())) return false;
        if (!getBuy().equals(that.getBuy())) return false;
        if (!getRecord().equals(that.getRecord())) return false;
        if (!getRelease().equals(that.getRelease())) return false;
        if (!getLicense().equals(that.getLicense())) return false;
        return getSize().equals(that.getSize());

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        return result;
    }
}
