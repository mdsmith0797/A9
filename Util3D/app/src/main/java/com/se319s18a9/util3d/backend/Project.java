package com.se319s18a9.util3d.backend;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Mason Green on 3/6/2018.
 */

public class Project implements Comparable<Project>{
    public String name;
    public Date created;
    public Date modified;
    public ArrayList<String> utilities;


    public Project(String name, Date created, Date modified, ArrayList<String> utilities){
        this.name = name;
        this.created = created;
        this.modified = modified;
        this.utilities = utilities;
    }

    public Project(String name){
        this.name = name;
        this.created = null;
        this.modified = null;
        this.utilities = null;
    }

    @Override
    public String toString(){
        return name;
    }

    public Date getCreated(){
        return created;
    }
    public Date getModified(){
        return modified;
    }
    public String getName(){
        return name;
    }
    @Override
    public int compareTo(@NonNull Project project) {
        if(project.modified.before(this.modified)){
            return -1;
        } else if(project.modified.after(this.modified)){
            return 1;
        } else{
            return 0;
        }
    }
}