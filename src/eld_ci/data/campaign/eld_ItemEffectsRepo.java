package eld_ci.data.campaign;

import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.InstallableIndustryItemPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.*;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import eld_ci.data.ids.eld_CI_Items;

import java.util.*;

public class eld_ItemEffectsRepo {

    public static void addItemEffectsToVanillaRepo() {
        ItemEffectsRepo.ITEM_EFFECTS.putAll(ITEM_EFFECTS);
    }

    public static float ORGANICAL_NANOFORGE_QUALITY_BONUS = 0.3f;
    public static int ORGANICAL_NANOFORGE_PROD = 5;
    public static int ORGANICAL_NANOFORGE_ORGANICS = 10;
    public static float ORGANICAL_NANOFORGE_SHORTAGE_HAZARD = 0.5f;

    public static float OVERCLOCKED_SCANNER_ACCESS_BONUS = 0.5f;
    public static int OVERCLOCKED_SCANNER_METALS = 10;
    public static int OVERCLOCKED_SCANNER_STABILITY_LOSS = 5;

    public static int MONITORING_BULB_STABILITY_BONUS = 5;
    public static int MONITORING_BULB_MARINES = 10;
    public static float MONITORING_BULB_ACCESS_LOSS = 0.5f;

    protected static boolean hasShortage(Industry industry, String commodities) {
        int Demand = industry.getDemand(commodities).getQuantity().getModifiedInt();
        float v = industry.getMarket().getCommodityData(commodities).getAvailable();
        float f = 1f - v / (float) Demand;
        return f > 0;
    }

    public static Map<String, InstallableItemEffect> ITEM_EFFECTS = new HashMap<String, InstallableItemEffect>() {
        {
            ItemEffectsRepo.ITEM_EFFECTS.put("eld_organical_nanoforge", new BoostIndustryInstallableItemEffect(
                    eld_CI_Items.ORGANICAL_NANOFORGE, ORGANICAL_NANOFORGE_PROD, 0) {
                public void apply(Industry industry) {
                    super.apply(industry);
                    industry.getMarket().getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD)
                            .modifyFlat(spec.getId(), ORGANICAL_NANOFORGE_QUALITY_BONUS, Misc.ucFirst(spec.getName().toLowerCase()));

                    if (industry instanceof BaseIndustry) {
                        BaseIndustry b = (BaseIndustry) industry;
                        b.demand(7, Commodities.ORGANICS, ORGANICAL_NANOFORGE_ORGANICS, Misc.ucFirst(spec.getName().toLowerCase()));

                        if (!hasShortage(industry, Commodities.ORGANICS)) {
                            industry.getMarket().removeCondition("pollution");
                        } else {
                            industry.getMarket().getHazard().modifyFlat(spec.getId(), ORGANICAL_NANOFORGE_SHORTAGE_HAZARD,
                                    Misc.ucFirst(spec.getName().toLowerCase()) + " organics shortage");
                        }
                    }
                }

                public void unapply(Industry industry) {
                    super.unapply(industry);
                    industry.getMarket().getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodifyFlat(spec.getId());

                    if (industry instanceof BaseIndustry) {
                        BaseIndustry b = (BaseIndustry) industry;
                        b.demand(7, Commodities.ORGANICS, 0, null);
                        industry.getMarket().getHazard().unmodifyFlat(spec.getId());

                        if (b.getSpecialItem() != null &&
                                industry.getMarket().hasCondition("habitable") &&
                                !industry.getMarket().hasCondition("pollution")) {
                            industry.getMarket().addCondition("pollution");
                        }
                    }
                }

                protected void addItemDescriptionImpl(Industry industry,
                                                      TooltipMakerAPI text,
                                                      SpecialItemData data,
                                                      InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode,
                                                      String pre, float pad) {

                    text.addPara(pre + "Increases ship and weapon production quality by %s. " +
                            "Increases production by %s units. " +
                            "Remove and prevent pollution from the planet as long as demand of organics by %s units is met. " +
                            "Otherwise add pollution to the world.",
                            pad, Misc.getHighlightColor(),
                            "" + (int) Math.round(ORGANICAL_NANOFORGE_QUALITY_BONUS * 100f) + "%",
                            "" + (int) ORGANICAL_NANOFORGE_PROD,
                            "" + (int) ORGANICAL_NANOFORGE_ORGANICS);
                }
            });

            ItemEffectsRepo.ITEM_EFFECTS.put("eld_overclocked_scanner", new BaseInstallableItemEffect(
                    eld_CI_Items.OVERCLOCKED_SCANNER) {
                public void apply(Industry industry) {
                    industry.getMarket().getAccessibilityMod().modifyFlat(spec.getId(),
                            OVERCLOCKED_SCANNER_ACCESS_BONUS, Misc.ucFirst(spec.getName().toLowerCase()));

                    if (industry instanceof BaseIndustry) {
                        BaseIndustry b = (BaseIndustry) industry;
                        b.demand(7, Commodities.METALS, OVERCLOCKED_SCANNER_METALS, Misc.ucFirst(spec.getName().toLowerCase()));

                        if (hasShortage(industry, Commodities.METALS)) {
                            industry.getMarket().getStability().modifyFlat(spec.getId(), -OVERCLOCKED_SCANNER_STABILITY_LOSS,
                                    Misc.ucFirst(spec.getName().toLowerCase()) + " metals shortage");
                        }
                    }
                }

                public void unapply(Industry industry) {
                    industry.getMarket().getAccessibilityMod().unmodifyFlat(spec.getId());

                    if (industry instanceof BaseIndustry) {
                        BaseIndustry b = (BaseIndustry) industry;
                        b.demand(7, Commodities.METALS, 0, null);
                        industry.getMarket().getStability().unmodifyFlat(spec.getId());
                    }
                }

                protected void addItemDescriptionImpl(Industry industry,
                                                      TooltipMakerAPI text,
                                                      SpecialItemData data,
                                                      InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode,
                                                      String pre, float pad) {

                    text.addPara(pre + "Increases colony accessibility by %s. " +
                            "Reduce stability by %s if demand of metals by %s units isn't met.",
                            pad, Misc.getHighlightColor(),
                            "" + (int) Math.round(OVERCLOCKED_SCANNER_ACCESS_BONUS * 100f) + "%",
                            "" + (int) OVERCLOCKED_SCANNER_STABILITY_LOSS,
                            "" + (int) OVERCLOCKED_SCANNER_METALS);
                }
            });

            ItemEffectsRepo.ITEM_EFFECTS.put("eld_monitoring_bulb", new BaseInstallableItemEffect(
                    eld_CI_Items.MONITORING_BULB) {
                public void apply(Industry industry) {
                    industry.getMarket().getStability().modifyFlat(spec.getId(),
                            MONITORING_BULB_STABILITY_BONUS, Misc.ucFirst(spec.getName().toLowerCase()));

                    if (industry instanceof BaseIndustry) {
                        BaseIndustry b = (BaseIndustry) industry;
                        b.demand(7, Commodities.MARINES, MONITORING_BULB_MARINES, Misc.ucFirst(spec.getName().toLowerCase()));

                        if (hasShortage(industry, Commodities.MARINES)) {
                            industry.getMarket().getAccessibilityMod().modifyFlat(spec.getId(), -MONITORING_BULB_ACCESS_LOSS,
                                    Misc.ucFirst(spec.getName().toLowerCase()) + " marines shortage");
                        }
                    }
                }

                public void unapply(Industry industry) {
                    industry.getMarket().getStability().unmodifyFlat(spec.getId());

                    if (industry instanceof BaseIndustry) {
                        BaseIndustry b = (BaseIndustry) industry;
                        b.demand(7, Commodities.MARINES, 0, null);
                        industry.getMarket().getAccessibilityMod().unmodifyFlat(spec.getId());
                    }
                }

                protected void addItemDescriptionImpl(Industry industry,
                                                      TooltipMakerAPI text,
                                                      SpecialItemData data,
                                                      InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode,
                                                      String pre, float pad) {

                    text.addPara(pre + "Increases colony stability by %s. " +
                                    "Reduce accessibility by %s if demand of marines by %s units isn't met.",
                            pad, Misc.getHighlightColor(),
                            "" + (int) MONITORING_BULB_STABILITY_BONUS,
                            "" + (int) Math.round(MONITORING_BULB_ACCESS_LOSS * 100f) + "%",
                            "" + (int) MONITORING_BULB_MARINES);
                }
            });


        }
    };

    public eld_ItemEffectsRepo() {
    }


}
