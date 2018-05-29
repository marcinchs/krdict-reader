
package org.mchs.dict.bo.babelnet.generated;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import it.uniroma1.lcl.babelnet.data.BabelExample;
import it.uniroma1.lcl.kb.Example;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class BabelSynSetFromJson {

    @Expose
    private Boolean bKeyConcept;
    @Expose
    private Boolean bLoaded;
    @Expose
    private List<Category> categories;
    @Expose
    private Domains domains;
    @Expose
    private LinkedHashSet<BabelExample> examples;
    @Expose
    private List<Gloss> glosses;
    @Expose
    private Id id;
    @Expose
    private List<Image> images;
    @Expose
    private LnToCompound lnToCompound;
    @Expose
    private LnToOtherForm lnToOtherForm;
    @Expose
    private MainSenses mainSenses;
    @Expose
    private List<Sense> senses;
    @Expose
    private String synsetType;
    @Expose
    private List<String> targetLangs;
    @Expose
    private Translations translations;
    @Expose
    private List<Object> wnOffsets;

    public Boolean getBKeyConcept() {
        return bKeyConcept;
    }

    public void setBKeyConcept(Boolean bKeyConcept) {
        this.bKeyConcept = bKeyConcept;
    }

    public Boolean getBLoaded() {
        return bLoaded;
    }

    public void setBLoaded(Boolean bLoaded) {
        this.bLoaded = bLoaded;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Domains getDomains() {
        return domains;
    }

    public void setDomains(Domains domains) {
        this.domains = domains;
    }

    public LinkedHashSet<BabelExample> getExamples() {
        return examples;
    }

    public void setExamples(LinkedHashSet<BabelExample> examples) {
        this.examples = examples;
    }

    public List<Gloss> getGlosses() {
        return glosses;
    }

    public void setGlosses(List<Gloss> glosses) {
        this.glosses = glosses;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public LnToCompound getLnToCompound() {
        return lnToCompound;
    }

    public void setLnToCompound(LnToCompound lnToCompound) {
        this.lnToCompound = lnToCompound;
    }

    public LnToOtherForm getLnToOtherForm() {
        return lnToOtherForm;
    }

    public void setLnToOtherForm(LnToOtherForm lnToOtherForm) {
        this.lnToOtherForm = lnToOtherForm;
    }

    public MainSenses getMainSenses() {
        return mainSenses;
    }

    public void setMainSenses(MainSenses mainSenses) {
        this.mainSenses = mainSenses;
    }

    public List<Sense> getSenses() {
        return senses;
    }

    public void setSenses(List<Sense> senses) {
        this.senses = senses;
    }

    public String getSynsetType() {
        return synsetType;
    }

    public void setSynsetType(String synsetType) {
        this.synsetType = synsetType;
    }

    public List<String> getTargetLangs() {
        return targetLangs;
    }

    public void setTargetLangs(List<String> targetLangs) {
        this.targetLangs = targetLangs;
    }

    public Translations getTranslations() {
        return translations;
    }

    public void setTranslations(Translations translations) {
        this.translations = translations;
    }

    public List<Object> getWnOffsets() {
        return wnOffsets;
    }

    public void setWnOffsets(List<Object> wnOffsets) {
        this.wnOffsets = wnOffsets;
    }

}
