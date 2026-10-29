package uk.co.yottr.form;

import uk.co.yottr.model.Frequency.FrequencyEnum;
import uk.co.yottr.model.SailingRegion;

/**
 * What an owner is looking for in crew. Every criterion is optional.
 */
public class CrewSearch {

    private SailingRegion region;

    // the rank of the least experienced crew wanted
    private Integer minLevel;

    private FrequencyEnum when;

    public SailingRegion getRegion() {
        return region;
    }

    public void setRegion(SailingRegion region) {
        this.region = region;
    }

    public Integer getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(Integer minLevel) {
        this.minLevel = minLevel;
    }

    public FrequencyEnum getWhen() {
        return when;
    }

    public void setWhen(FrequencyEnum when) {
        this.when = when;
    }
}
