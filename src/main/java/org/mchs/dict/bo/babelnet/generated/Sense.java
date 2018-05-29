
package org.mchs.dict.bo.babelnet.generated;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Sense {

    @Expose
    private Boolean bKeySense;
    @Expose
    private Long frequency;
    @Expose
    private String fullLemma;
    @Expose
    private Long idSense;
    @Expose
    private String language;
    @Expose
    private String pos;
    @Expose
    private Pronunciations pronunciations;
    @Expose
    private String senseKey;
    @Expose
    private String simpleLemma;
    @Expose
    private String source;
    @Expose
    private SynsetID synsetID;

    public Boolean getBKeySense() {
        return bKeySense;
    }

    public void setBKeySense(Boolean bKeySense) {
        this.bKeySense = bKeySense;
    }

    public Long getFrequency() {
        return frequency;
    }

    public void setFrequency(Long frequency) {
        this.frequency = frequency;
    }

    public String getFullLemma() {
        return fullLemma;
    }

    public void setFullLemma(String fullLemma) {
        this.fullLemma = fullLemma;
    }

    public Long getIdSense() {
        return idSense;
    }

    public void setIdSense(Long idSense) {
        this.idSense = idSense;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public Pronunciations getPronunciations() {
        return pronunciations;
    }

    public void setPronunciations(Pronunciations pronunciations) {
        this.pronunciations = pronunciations;
    }

    public String getSenseKey() {
        return senseKey;
    }

    public void setSenseKey(String senseKey) {
        this.senseKey = senseKey;
    }

    public String getSimpleLemma() {
        return simpleLemma;
    }

    public void setSimpleLemma(String simpleLemma) {
        this.simpleLemma = simpleLemma;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public SynsetID getSynsetID() {
        return synsetID;
    }

    public void setSynsetID(SynsetID synsetID) {
        this.synsetID = synsetID;
    }

}
