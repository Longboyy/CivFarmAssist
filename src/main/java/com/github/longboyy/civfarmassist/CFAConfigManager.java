package com.github.longboyy.civfarmassist;

import com.github.longboyy.civfarmassist.crops.Crop;
import com.github.longboyy.civfarmassist.crops.CropManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.civmodcore.CoreConfigManager;
import vg.civcraft.mc.civmodcore.util.ConfigParsing;

import java.util.List;

public class CFAConfigManager extends CoreConfigManager {

    private final CropManager cropManager;

    public CFAConfigManager(ACivMod plugin, CropManager cropManager) {
        super(plugin);
        this.cropManager = cropManager;
    }

    @Override
    protected boolean parseInternal(ConfigurationSection config){
        parseCrops(config.getConfigurationSection("crops"));
        return true;
    }

    private void parseCrops(ConfigurationSection config){
        if(config == null){
            logger.warning("No crops found in config");
            return;
        }

        for(String key : config.getKeys(false)){
            if(!config.isConfigurationSection(key)){
                logger.warning("Ignoring invalid entry " + key + " at " + config.getCurrentPath());
                continue;
            }

            ConfigurationSection current = config.getConfigurationSection(key);
            Crop crop = Crop.parseCrop(current);
            if(crop == null){
                logger.warning("Failed to parse crop " + key + " at " + config.getCurrentPath());
                continue;
            }

            logger.info("Parsed crop " + key + " at " + config.getCurrentPath());
            cropManager.registerCrop(crop);
        }
    }

    public static ItemStack parseFirstItem(ConfigurationSection config) {
        if (config == null) {
            return null;
        }

        for (String key : config.getKeys(false)) {
            ConfigurationSection current = config.getConfigurationSection(key);
            List<ItemStack> list = ConfigParsing.parseItemMapDirectly(current).getItemStackRepresentation();
            return list.isEmpty() ? null : list.get(0);
        }

        return null;
    }
}
