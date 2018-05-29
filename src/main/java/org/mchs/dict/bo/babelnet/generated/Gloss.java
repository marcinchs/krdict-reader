
package org.mchs.dict.bo.babelnet.generated;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import it.uniroma1.lcl.jlt.util.Language;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Gloss {

    @Expose
    private String gloss;
    @Expose
    private String language;
    @Expose
    private String source;
    @Expose
    private String sourceSense;
    @Expose
    private List<Token> tokens;

    public String getGloss() {
        return gloss;
    }

    public void setGloss(String gloss) {
        this.gloss = gloss;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceSense() {
        return sourceSense;
    }

    public void setSourceSense(String sourceSense) {
        this.sourceSense = sourceSense;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    @Override
    public String toString() {
        return  gloss ;

    }
}
