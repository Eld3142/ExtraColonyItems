package eld_ci.data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import eld_ci.data.campaign.eld_ItemEffectsRepo;
import eld_ci.data.ids.eld_CI_Items;

import static eld_ci.data.scripts.eld_IndustryTagsAddItems.addIndustryTagsToSpecialItem;

public class Eld_CI_ModPlugin extends BaseModPlugin {

    /*This method is run right at the end of starsectors loading.
    * It is most useful for loading data that only really needs to be setup once. */
    @Override
    public void onApplicationLoad() throws Exception {

        super.onApplicationLoad();
        new eld_ItemEffectsRepo();

        addIndustryTagsToSpecialItem(eld_CI_Items.ORGANICAL_NANOFORGE, "heavyindustry");
        addIndustryTagsToSpecialItem(eld_CI_Items.OVERCLOCKED_SCANNER, "waystation");
        addIndustryTagsToSpecialItem(eld_CI_Items.MONITORING_BULB, "station");

        addIndustryTagsToSpecialItem(eld_CI_Items.DIMENSIONAL_MIRROR, "techmining");
        addIndustryTagsToSpecialItem(eld_CI_Items.OMNI_CORE, "industry");

    }

    /*This method is run in two cases:
    * - At the end of the creation of a new save
    * - When an existing save finished loading
    * This method is most useful for adding transient listeners/scripts and for enabling mid-save compatibility,
    * like adding star systems to an existing save if the mod was just added. */
    @Override
    public void onGameLoad(boolean newGame) {

    }

    /*Runs when a save is created.
    * This method specifically runs before procedural generation, so any base-game procedural content is not accessible yet.
    * It is recommended to start placing your modded star systems from here,
    * as starsectors procgen will avoid placing stars and hyperspace storms nearby existing systems, preventing overlap.*/
    @Override
    public void onNewGame() {

    }

    /*Runs after onNewGame, after the economy has finished loading.
    * This method can be useful for accessing other mods star systems, assuming those have placed their systems in onNewGame. */
    @Override
    public void onNewGameAfterEconomyLoad() {

    }

    @Override
    public void onNewGameAfterProcGen() {

    }

    @Override
    public void onNewGameAfterTimePass() {

    }


}
