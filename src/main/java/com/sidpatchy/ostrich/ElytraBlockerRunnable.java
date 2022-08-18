package com.sidpatchy.ostrich;

import com.sidpatchy.albatross.File.AlbatrossLanguageManager;
import com.sidpatchy.ostrich.Util.GriefPrevention.Claims;
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
    private final Ostrich plugin;
    private final Player player;

    public ElytraBlockerRunnable(Ostrich plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {

        AlbatrossLanguageManager languageManager = new AlbatrossLanguageManager(plugin.getFallbackLanguageString(), plugin);

        StateFlag regionElytraFlag = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get(plugin.getElytraFlagName());

        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);


        // This took an embarrassing amount of time to figure out because I was initially forgetting to check if the
        // player was even in a claim.
        if (set.testState(localPlayer, regionElytraFlag)) {

            if (player.hasPermission("ostrich.bypass.elytra")) { return; }

            // Elytra are allowed in the region
            if (plugin.isGriefPreventionEnabled()) {
                Claims claims = new Claims();

                if (claims.isPlayerInClaim(player)) {
                    // Player is inside a claim

                    if (claims.getClaimFromPlayer(player).isAdminClaim() &&
                            player.hasPermission("ostrich.griefprevention.adminclaims.elytra")) {
                        return;
                    }

                    StateFlag claimElytraFlag = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get(plugin.getGpElytraFlagName());
                    if (!set.testState(localPlayer, claimElytraFlag)) {
                        // elytra are not allowed in claims
                        disableElytra(languageManager.getLocalizedString("elytraNotAllowedInClaims", player));
                    }
                    else {
                        // elytra is allowed in claims, check if membership is required.
                        shouldPlayerReallyBeAllowToElytraInClaim(player);
                    }
                } // else, player is not inside a claim, do nothing
            } //else, elytra is allowed
        }
        else {
            // Elytra are not allowed in the region.
            if (plugin.isGriefPreventionEnabled()) {
                Claims claims = new Claims();

                StateFlag claimElytraFlag = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get(plugin.getGpElytraFlagName());
                boolean elytraAllowedInClaims = set.testState(localPlayer, claimElytraFlag);

                if (claims.isPlayerInClaim(player)) {
                    // Player is inside a claim
                    if (!elytraAllowedInClaims) {
                        // elytra is not allowed in claims
                        disableElytra(languageManager.getLocalizedString("elytraNotAllowedInClaims", player));
                    }
                    else {
                        // elytra is allowed in claims, check if membership is required.
                        shouldPlayerReallyBeAllowToElytraInClaim(player);
                    }
                }
                else {
                    // Player is not inside a claim, disable elytra.
                    if (elytraAllowedInClaims) {
                        disableElytra(languageManager.getLocalizedString("elytraAllowedInClaims", player));
                    }
                    else {
                        disableElytra(languageManager.getLocalizedString("elytraNotAllowedInRegion", player));
                    }
                }
            }
            else {
                // gp is not enabled and elytra is not allowed
                disableElytra(languageManager.getLocalizedString("elytraNotAllowedInRegion", player));
            }
        }
    }

    private void disableElytra(String chatMessage) {

        AlbatrossLanguageManager languageManager = new AlbatrossLanguageManager(plugin.getFallbackLanguageString(), plugin);

        // Disable elytra.
        player.setGliding(false);
        player.setVelocity(new Vector(0, 0, 0));

        // Send chat message to player.
        player.sendMessage(chatMessage);
    }
    /**
     * Creative name, I know.
     *
     * Checks if player is required to be a member of a claim to fly in it.
     * @param player player to check
     */
    private void shouldPlayerReallyBeAllowToElytraInClaim(Player player) {
        AlbatrossLanguageManager languageManager = new AlbatrossLanguageManager(plugin.getFallbackLanguageString(), plugin);

        // Check if the player must be a member in order to fly inside claims.
        if (player.hasPermission("ostrich.griefprevention.requireClaimMembership.elytra")) {
            Claims claims = new Claims();

            if (!claims.isPlayerMemberInClaim(player, claims.getClaimFromPlayer(player))) {
                // player is not a member of the claim
                disableElytra(languageManager.getLocalizedString("claimMembershipRequiredToElytra", player));
            } // else, player is a member of the claim, do nothing.
        } // else, player does not require membership, do nothing.
    }
}
