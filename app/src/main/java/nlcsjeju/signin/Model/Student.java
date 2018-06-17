package nlcsjeju.signin.Model;

import nlcsjeju.signin.Util;

/**
 * Created by mjkwak on 2/20/2018.
 */

public class Student {
    private String name;
    private int house;
    private String uid;
    private int location;

    // Default constructor required to use DataSnapshot.getValue()
    public Student(){
    }

    public Student(String uid, String name, int house, int location){
        this.uid = uid;
        this.name = name;
        this.house = house;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public int getHouse() {
        return house;
    }

    public String getUid() {
        return uid;
    }

    public int getLocation() { return location; }
}
