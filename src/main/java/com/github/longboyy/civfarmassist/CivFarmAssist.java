package com.github.longboyy.civfarmassist;

import com.github.longboyy.civfarmassist.crops.CropManager;
import com.github.longboyy.civfarmassist.listeners.CropListener;
import org.bukkit.Bukkit;
import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.permission.PermissionType;

import java.util.Arrays;
import java.util.List;

public class CivFarmAssist extends ACivMod {
    private CropManager cropManager;
    private CFAConfigManager configManager;
    private CFASettingsManager settingsManager;

    private boolean nobilityEnabled;

    @Override
    public void onEnable(){
        super.onEnable();
        cropManager = new CropManager(this);
        configManager = new CFAConfigManager(this, cropManager);
        if(!configManager.parse()){
            getLogger().info("Failed to parse crop manager");
            getPluginLoader().disablePlugin(this);
            return;
        }
        settingsManager = new CFASettingsManager(this);
        PermissionType.registerPermission("BREAK_CROPS", Arrays.asList(
                GroupManager.PlayerType.OWNER,
                GroupManager.PlayerType.ADMINS,
                GroupManager.PlayerType.MODS,
                GroupManager.PlayerType.MEMBERS
        ));
        this.nobilityEnabled = getServer().getPluginManager().getPlugin("NobilityItems") != null;
        this.registerListener(new CropListener(this, cropManager));
    }

    public void onDisable(){
        super.onDisable();
    }

    public boolean isNobilityEnabled(){
        return nobilityEnabled;
    }

    public CFASettingsManager getSettingsManager(){
        return settingsManager;
    }

}
