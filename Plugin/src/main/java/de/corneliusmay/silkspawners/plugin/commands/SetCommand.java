package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.completers.EntityTabCompleter;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SetCommand extends SilkSpawnersCommand {

    public SetCommand() {
        super("set", true, new EntityTabCompleter());
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if(args.length != 1) return invalidSyntax(sender);
        if(!(sender instanceof Player player)) {
            sendMessage(sender, "PLAYERS_ONLY");
            return false;
        }

        Spawner newSpawner = new Spawner(plugin, EntityType.fromName(args[0]));
        if(!newSpawner.isValid()) {
            sendMessage(sender, "ENTITY_NOT_FOUND", args[0]);
            return false;
        }

        if(!player.hasPermission(getPermissionString() + "." + newSpawner.getEntityType().getName()) && !sender.hasPermission(getPermissionString() + ".*")) {
            sendMessage(sender, "INSUFFICIENT_ENTITY_PERMISSION", newSpawner.serializedName());
            return false;
        }

        Block block = plugin.getNmsHandler().getTargetBlock(player);
        Spawner spawner = new Spawner(plugin, block);
        if(!spawner.isValid()) {
            sendMessage(sender, "INVALID_TARGET");
            return false;
        }

        newSpawner.setSpawnerBlockType(block);
        sendMessage(sender, "SUCCESS", newSpawner.serializedName());
        return true;
    }
}
