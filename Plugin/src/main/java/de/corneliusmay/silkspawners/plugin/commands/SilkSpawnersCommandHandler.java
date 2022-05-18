package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SilkSpawnersCommandHandler implements CommandExecutor {

    @Getter
    private final List<SilkSpawnersCommand> commands;

    @Getter
    private final SilkSpawnersTabCompleter tabCompleter;

    public SilkSpawnersCommandHandler() {
        this.commands = new ArrayList<>();
        this.tabCompleter = new SilkSpawnersTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command c, String s, String[] args) {
        if(args.length < 1) {
            return false;
        }

        SilkSpawnersCommand command = getCommand(args[0]);
        if(command == null) {
            commandSender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + "§eCommand not found. §7Available commands: " + Arrays.toString(getCommands(commandSender).toArray(String[]::new)));
            return false;
        }

        if(commandSender instanceof Player && !commandSender.hasPermission("silkspawners.command." + command.getCommand())) {
            command.insufficientPermission(commandSender);
            return false;
        }

        return command.execute(commandSender, Arrays.copyOfRange(args, 1, args.length));
    }

    public void registerCommand(SilkSpawnersCommand command) {
        if(getCommand(command.getCommand()) != null) return;
        commands.add(command);
    }

    public SilkSpawnersCommand getCommand(String command) {
        return commands.stream().filter(c -> c.getCommand().equals(command)).findFirst().orElse(null);
    }

    public List<String> getCommands(CommandSender cs) {
        return commands.stream().map(SilkSpawnersCommand::getCommand).filter(command -> cs.hasPermission("silkspawners.command." + command)).collect(Collectors.toList());
    }
}
