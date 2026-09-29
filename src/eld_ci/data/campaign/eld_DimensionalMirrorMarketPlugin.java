package eld_ci.data.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.submarkets.BaseSubmarketPlugin;
import com.fs.starfarer.api.util.WeightedRandomPicker;

import java.util.*;

public class eld_DimensionalMirrorMarketPlugin extends BaseSubmarketPlugin {

    protected int RuinsLevel() {
        if (market.hasCondition(Conditions.RUINS_VAST)) {
            return 4;
        } else if (market.hasCondition(Conditions.RUINS_EXTENSIVE)) {
            return 3;
        } else if (market.hasCondition(Conditions.RUINS_WIDESPREAD)) {
            return 2;
        } else if (market.hasCondition(Conditions.RUINS_SCATTERED)) {
            return 1;
        } else return 0;
    }

    public void updateCargoPrePlayerInteraction() {
        if (!(this.sinceLastCargoUpdate < 30.0F)) {
            this.sinceLastCargoUpdate = 0.0F;
            this.updateCargo();
        }
    }

    public void updateCargoForce() {
        this.sinceLastCargoUpdate = 0.0F;
        this.updateCargo();
    }

    public void updateCargo() {
        CargoAPI cargo = this.getCargo();

        for (CargoStackAPI stack : cargo.getStacksCopy()) {
            float qty = stack.getSize();
            cargo.removeItems(stack.getType(), stack.getData(), qty);
        }

        cargo.removeEmptyStacks();
        this.addSpecialItems();
        this.addAICores();
        cargo.sort();
    }

    protected void addSpecialItems() {
        CargoAPI cargo = this.getCargo();
        HashMap<String, Boolean> SpecialItemsList = new HashMap();

        for (SpecialItemSpecAPI spec : Global.getSettings().getAllSpecialItemSpecs()) {
            String[] ItemTags = new String[]{"eld_market"};

            for (String tags : ItemTags) {
                if (spec.hasTag(tags)) {
                    SpecialItemsList.put(spec.getId(), true);
                }
            }
        }

        WeightedRandomPicker<String> randomItemPicker = new WeightedRandomPicker(this.itemGenRandom);

        for(Map.Entry<String, Boolean> item : SpecialItemsList.entrySet()) {
            randomItemPicker.add((String)item.getKey());
        }

        int itemPickerNum = Math.round(randomItemPicker.getTotal() / 5.0F * RuinsLevel());

        for(int i = 0; i < itemPickerNum; ++i) {
            if (!randomItemPicker.isEmpty()) {
                String itemID = randomItemPicker.pickAndRemove();
                int quantity = this.itemGenRandom.nextInt(RuinsLevel()) + 1;
                cargo.addSpecial(new SpecialItemData(itemID, (String)null), quantity);
            }
        }

    }

    protected void addAICores() {
        CargoAPI cargo = this.getCargo();
        Random random = new Random();
        int quantity = random.nextInt(2 * RuinsLevel()) + 1;
        cargo.addCommodity("gamma_core", (float)quantity);
        cargo.addCommodity("beta_core", (float)quantity);
        cargo.addCommodity("alpha_core", (float)quantity);

    }

    public boolean isIllegalOnSubmarket(CargoStackAPI stack, SubmarketPlugin.TransferAction action) {
        return action == TransferAction.PLAYER_SELL;
    }

    public boolean isIllegalOnSubmarket(String commodityId, SubmarketPlugin.TransferAction action) {
        return action == TransferAction.PLAYER_SELL;
    }

    public boolean isIllegalOnSubmarket(FleetMemberAPI member, SubmarketPlugin.TransferAction action) {
        return action == TransferAction.PLAYER_SELL;
    }

    public float getTariff() {
        return 1.0f;
    }

    public String getIllegalTransferText(FleetMemberAPI member, SubmarketPlugin.TransferAction action) {
        return "They are not interested";
    }

    public String getIllegalTransferText(CargoStackAPI stack, SubmarketPlugin.TransferAction action) {
        return "They are not interested";
    }

}
