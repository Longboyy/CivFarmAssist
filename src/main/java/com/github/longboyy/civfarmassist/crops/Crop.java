package com.github.longboyy.civfarmassist.crops;

import com.github.longboyy.civfarmassist.CFAConfigManager;
import com.github.longboyy.civfarmassist.CivFarmAssist;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import vg.civcraft.mc.civmodcore.itemHandling.ItemMap;
import vg.civcraft.mc.civmodcore.util.ConfigParsing;

import java.util.ArrayList;
import java.util.List;

public class Crop {

    private final boolean dropSeedsWhenRipe;
    private final boolean harvestIsSeed;
    private final boolean autoReplant;
    private final ItemStack crop;
    private final ItemStack plantedOn;
    private final CropDrop seed;
    private final CropDrop harvest;

    // VALID DIRECTIONS ARE RELATIVE TO THE CROP - FOR EXAMPLE `DOWN` MEANS SEARCH BELOW CROP FOR `plantedOn`
    private final List<BlockFace> validDirections;

    public Crop(ItemStack crop, ItemStack plantedOn, CropDrop seed, CropDrop harvest, List<BlockFace> validDirections,
                boolean dropSeedsWhenRipe, boolean harvestIsSeed, boolean autoReplant){
        this.crop = crop;
        this.plantedOn = plantedOn;
        this.seed = seed;
        this.harvest = harvest;
        this.validDirections = validDirections;
        this.dropSeedsWhenRipe = dropSeedsWhenRipe;
        this.harvestIsSeed = harvestIsSeed;
        this.autoReplant = autoReplant;
    }

    public boolean isDropSeedsWhenRipe(){
        return dropSeedsWhenRipe;
    }

    public boolean isHarvestAlsoSeed(){
        return harvestIsSeed;
    }

    public boolean isAutoReplant(){
        return autoReplant;
    }

    public ItemStack getCrop() {
        return crop;
    }

    public ItemStack getPlantedOn() {
        return plantedOn;
    }

    public CropDrop getSeed() {
        return seed;
    }

    public CropDrop getHarvest() {
        return harvest;
    }

    public List<BlockFace> getValidDirections() {
        return validDirections;
    }

    public static Crop parseCrop(ConfigurationSection config){
        if(config == null){
            return null;
        }

        ItemStack cropItem = CFAConfigManager.parseFirstItem(config.getConfigurationSection("crop"));

        cropItem.setType(convertToCrop(cropItem.getType()));
        ItemStack plantedOnItem = CFAConfigManager.parseFirstItem(config.getConfigurationSection("plantedOn"));

        CropDrop harvestDrop = CropDrop.parseCropDrop(config.getConfigurationSection("harvest"));
        if(harvestDrop == null){
            return null;
        }

        boolean harvestIsSeed = config.getBoolean("harvestIsSeed", false);
        boolean ripeSeed = config.getBoolean("ripeSeeds", false);
        boolean autoReplant = config.getBoolean("autoReplant", true);

        CropDrop seedDrop = CropDrop.parseCropDrop(config.getConfigurationSection("seed"));
        if (seedDrop == null) {
            return null;
        }

        List<String> directions = config.getStringList("validDirections");

        List<BlockFace> blockDirections = new ArrayList<>();
        if(directions.size() > 0){
            for(String direction : directions){
                BlockFace face = BlockFace.valueOf(direction);
                blockDirections.add(face);
            }
        }else{
            blockDirections.add(BlockFace.DOWN);
        }

        return new Crop(cropItem, plantedOnItem, seedDrop, harvestDrop, blockDirections, ripeSeed, harvestIsSeed, autoReplant);
    }

    private static Material convertToCrop(Material material){
        switch(material){
            case COCOA_BEANS:
                return Material.COCOA;
            case CARROT:
                return Material.CARROTS;
            case POTATO:
                return Material.POTATOES;
            case BEETROOT:
                return Material.BEETROOTS;
            default:
                return material;
        }
    }

}
