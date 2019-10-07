package com.test.todo;

import android.text.Editable;

public class Task {

    private String _id;
    private String createdBy;
    private String dateCreated;
    private int status;
    private String task;
    private String image;
    private String type;
    private String useremail;

    public Task() {
    }

    public Task(String _id, String createdBy, String dateCreated, int status, String task, String type, String useremail, String image) {
        this._id = _id;
        this.createdBy = createdBy;
        this.dateCreated = dateCreated;
        this.status = status;
        this.task = task;
        this.type = type;
        this.useremail = useremail;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int isStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUseremail(String s) {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }
}
