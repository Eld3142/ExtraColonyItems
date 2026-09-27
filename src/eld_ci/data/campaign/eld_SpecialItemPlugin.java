package eld_ci.data.campaign;

import com.fs.starfarer.api.campaign.impl.items.GenericSpecialItemPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class eld_SpecialItemPlugin extends GenericSpecialItemPlugin {

    @Override
    protected void addInstalledInSection(TooltipMakerAPI tooltip, float pad) {
        String list = "All Industry";
        String [] array = new String[1];
        array[0] = "All Industry";
        tooltip.addPara(list, pad, Misc.getGrayColor(), Misc.getBasePlayerColor(), array);
    }

}
