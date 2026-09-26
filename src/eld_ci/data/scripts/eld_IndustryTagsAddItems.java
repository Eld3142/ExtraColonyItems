package eld_ci.data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.loading.IndustrySpecAPI;

public class eld_IndustryTagsAddItems {

    private static void addIndustryToSpecialItem(String itemId, String industryId) {
        SpecialItemSpecAPI spec = Global.getSettings().getSpecialItemSpec(itemId);
        if (spec == null) return;

        String params = spec.getParams();

        if (params == null || params.trim().isEmpty()) {
            spec.setParams(industryId);
            return;
        }

        for (String id : params.split(",")) {
            if (id.trim().equals(industryId)) {
                return;
            }
        }

        spec.setParams(params + "," + industryId);
    }

    public static void addIndustryTagsToSpecialItem(String itemId, String tags) {
        for (IndustrySpecAPI ind : Global.getSettings().getAllIndustrySpecs()) {
            if (ind.hasTag(tags)) {
                addIndustryToSpecialItem(itemId, ind.getId());
            }
        }
    }

    public eld_IndustryTagsAddItems() {
    }
}
