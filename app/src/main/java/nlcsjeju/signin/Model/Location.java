package nlcsjeju.signin.Model;

/**
 * Created by mjkwak on 3/16/2018.
 */

public class Location {
    private String name;
    private String url;
    void Location(String name, String url){
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
