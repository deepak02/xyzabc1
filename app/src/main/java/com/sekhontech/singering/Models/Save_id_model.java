package com.sekhontech.singering.Models;

/**
 * Created by ST_004 on 14-04-2017.
 */

public class Save_id_model  {
    public String id;




  public Save_id_model()
    {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Save_id_model)) return false;

        Save_id_model that = (Save_id_model) o;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;

    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
