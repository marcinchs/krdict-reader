
package org.mchs.dict.bo.babelnet.generated;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Pronunciations {

    @Expose
    private List<Object> audios;
    @Expose
    private List<Object> transcriptions;

    public List<Object> getAudios() {
        return audios;
    }

    public void setAudios(List<Object> audios) {
        this.audios = audios;
    }

    public List<Object> getTranscriptions() {
        return transcriptions;
    }

    public void setTranscriptions(List<Object> transcriptions) {
        this.transcriptions = transcriptions;
    }

}
