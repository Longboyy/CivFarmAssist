package com.github.longboyy.civfarmassist;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import vg.civcraft.mc.civmodcore.playersettings.PlayerSettingAPI;
import vg.civcraft.mc.civmodcore.playersettings.gui.MenuSection;
import vg.civcraft.mc.civmodcore.playersettings.impl.BooleanSetting;

import java.util.UUID;

public class CFASettingsManager {

    private final CivFarmAssist plugin;
    private BooleanSetting autoReplant;

    public CFASettingsManager(CivFarmAssist plugin){
        this.plugin = plugin;
        initSettings();
    }

    private void initSettings(){
        MenuSection menu = PlayerSettingAPI.getMainMenu().createMenuSection(
                "CivFarmAssist",
                "CivFarmAssist related settings",
                new ItemStack(Material.IRON_HOE)
        );
        autoReplant = new BooleanSetting(plugin, true, "Auto Replant", "cfaAutoReplant", "Whether or not auto replant is enabled");
        PlayerSettingAPI.registerSetting(autoReplant, menu);
    }

    public boolean getAutoReplant(UUID uuid){
        return autoReplant.getValue(uuid);
    }
}
