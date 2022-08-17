package com.sidpatchy.ostrich;

import com.sidpatchy.albatross.File.AlbatrossLanguageManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ElytraBlockerRunnable implements Runnable{
    // Parameters related to language handling.
    boolean elytraAllowedInRegion;
    boolean elytraAllowedInClaims;

    private final Ostrich plugin;
    private final Player player;

    public ElytraBlockerRunnable(Ostrich plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {

        StateFlag regionElytraBlockFlag = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get(plugin.getElytraFlagName());

        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        // Options related to GriefPrevention
        boolean griefPreventionBypass;
        if (plugin.isGriefPreventionEnabled()) {
            StateFlag gpClaimElytraFlag = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get(plugin.getGpElytraFlagName());

            if (!set.testState(localPlayer, gpClaimElytraFlag)) {
                griefPreventionBypass = false;
                elytraAllowedInClaims = false;
                disableElytra();
            }
            else { griefPreventionBypass = true; }
        }
        else { griefPreventionBypass = true; }

        if (!set.testState(localPlayer, regionElytraBlockFlag) && !griefPreventionBypass) {
            elytraAllowedInRegion = true;
            disableElytra();
        }

    }

    private void disableElytra() {

        AlbatrossLanguageManager languageManager = new AlbatrossLanguageManager(plugin.getFallbackLanguageString(), plugin);
        String chatMessage;

        // Decide which chat message should be sent.
        if (!elytraAllowedInRegion) {
            // elytra is not allowed in region but is allowed in claims
            if (elytraAllowedInClaims) {
                chatMessage = languageManager.getLocalizedString("elytraAllowedInClaims", player);
            }
            // elytra is not allowed is region and is not allowed in claims
            else {
                chatMessage = languageManager.getLocalizedString("elytraNotAllowedInRegion", player);
            }
        }
        // elytra is allowed in region but is not allowed in claims.
        else if (!elytraAllowedInClaims) {
            chatMessage = languageManager.getLocalizedString("elytraNotAllowedInClaims", player);
        }
        else {
            // disableelytra() shouldn't have been called if this is the case.
            return;
        }

        // Disable elytra.
        player.setGliding(false);
        player.setVelocity(new Vector(0, 0, 0));

        // Send chat message to player.
        player.sendMessage(chatMessage);
    }
}
