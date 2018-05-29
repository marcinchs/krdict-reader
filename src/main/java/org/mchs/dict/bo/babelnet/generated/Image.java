
package org.mchs.dict.bo.babelnet.generated;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Image {

    @Expose
    private Boolean badImage;
    @Expose
    private List<String> languages;
    @Expose
    private String license;
    @Expose
    private String name;
    @Expose
    private String thumbUrl;
    @Expose
    private String url;
    @Expose
    private String urlSource;

    public Boolean getBadImage() {
        return badImage;
    }

    public void setBadImage(Boolean badImage) {
        this.badImage = badImage;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlSource() {
        return urlSource;
    }

    public void setUrlSource(String urlSource) {
        this.urlSource = urlSource;
    }

}
