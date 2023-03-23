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
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.util.Vector;

import java.io.IOException;

public class ElytraBlockerRunnable implements Runnable {

    private final Ostrich plugin;
    private final Player player;
    private final AlbatrossLanguageManager languageManager;

    public ElytraBlockerRunnable(Ostrich plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        languageManager = new AlbatrossLanguageManager(plugin.getFallbackLanguageString(), plugin);
    }

    @Override
    public void run() {

        StateFlag regionelytraFlag = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get(plugin.getElytraFlagName());
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        if (player.hasPermission("ostrich.bypass.elytra") || player.hasPermission("ostrich.bypass.*")) {
            return;
        }

        if (set.testState(localPlayer, regionelytraFlag)) {
            if (plugin.isGriefPreventionEnabled()) {
                try {
                    handlePlayerInGriefPreventionClaim(set, localPlayer);
                } catch (IOException | InvalidConfigurationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else {
            if (plugin.isGriefPreventionEnabled()) {
                try {
                    handleNonelytraRegionWithGriefPrevention(set, localPlayer);
                } catch (IOException | InvalidConfigurationException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                try {
                    disableelytra(languageManager.getLocalizedString("elytraNotAllowedInRegion", player));
                } catch (IOException | InvalidConfigurationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     *
     * @param set the region that the player is in
     * @param localPlayer a WorldGuard LocalPlayer
     */
    private void handlePlayerInGriefPreventionClaim(ApplicableRegionSet set, LocalPlayer localPlayer) throws IOException, InvalidConfigurationException {

        Claims claims = new Claims();

        if (!claims.isPlayerInClaim(player)) {
            return;
        }

        if (claims.getClaimFromPlayer(player).isAdminClaim() &&
                (player.hasPermission("ostrich.griefprevention.adminclaims.elytra") || player.hasPermission("ostrich.griefprevention.adminclaims.*"))) {
            return;
        }

        StateFlag claimelytraFlag = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get(plugin.getGpElytraFlagName());

        if (!set.testState(localPlayer, claimelytraFlag)) {
            disableelytra(languageManager.getLocalizedString("elytraNotAllowedInClaims", player));
        }
        else {
            shouldPlayerReallyBeAllowToFlyInClaim(player, claims);
        }
    }

    /**
     *
     * @param set the region that the player is in
     * @param localPlayer a WorldGuard LocalPlayer
     */
    private void handleNonelytraRegionWithGriefPrevention(ApplicableRegionSet set, LocalPlayer localPlayer) throws IOException, InvalidConfigurationException {

        Claims claims = new Claims();
        StateFlag claimelytraFlag = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get(plugin.getGpElytraFlagName());
        boolean elytraAllowedInClaims = set.testState(localPlayer, claimelytraFlag);

        if (claims.isPlayerInClaim(player)) {
            handlePlayerInGriefPreventionClaim(set, localPlayer);
        }
        else {
            handlePlayerNotInGriefPreventionClaim(elytraAllowedInClaims);
        }
    }


    /**
     * Determines what message should be sent if the player is not in a GriefPrevention claim.
     * @param elytraAllowedInClaims
     */
    private void handlePlayerNotInGriefPreventionClaim(boolean elytraAllowedInClaims) throws IOException, InvalidConfigurationException {

        if (elytraAllowedInClaims) {
            disableelytra(languageManager.getLocalizedString("elytraAllowedInClaims", player));
        } else {
            disableelytra(languageManager.getLocalizedString("elytraNotAllowedInRegion", player));
        }
    }

    /**
     * Creative name, I know.
     *
     * Checks if player is required to be a member of a claim to fly in it.
     * @param player the player to check
     * @param claims an instance of Claims
     */
    private void shouldPlayerReallyBeAllowToFlyInClaim(Player player, Claims claims) throws IOException, InvalidConfigurationException {

        if (player.hasPermission("ostrich.griefprevention.requireClaimMembership.elytra") || player.hasPermission("ostrich.griefprevention.requireClaimMembership.*")) {

            if (!claims.isPlayerMemberInClaim(player, claims.getClaimFromPlayer(player))) {
                disableelytra(languageManager.getLocalizedString("claimMembershipRequiredToFly", player));
            }
        }
    }

    /**
     * Disable gliding on the user.
     *
     * @param chatMessage message to send to the player. Should already be translated by the language manager.
     */
    private void disableelytra(String chatMessage) {

        // Disable Elytra.
        player.setGliding(false);
        player.setVelocity(new Vector(0, 0, 0));

        // Prevent fall damage if enabled.
        if (plugin.getElytraPreventFallDamage()) {
            int blockY = player.getWorld().getHighestBlockAt(player.getLocation()).getY();
            float distanceFromGround = (float) (player.getLocation().getY() - blockY);
            player.setFallDistance((distanceFromGround + 10) * -1);
        }

        // Send chat message to player.
        player.sendMessage(chatMessage);
    }
}