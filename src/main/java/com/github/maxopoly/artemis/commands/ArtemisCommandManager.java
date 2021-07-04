package com.github.maxopoly.artemis.commands;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import javax.annotation.Nonnull;
import org.bukkit.plugin.Plugin;
import vg.civcraft.mc.civmodcore.commands.CommandManager;

public class ArtemisCommandManager extends CommandManager {

    public ArtemisCommandManager(Plugin plugin) {
        super(plugin);
        init();
    }

    @Override
    public void registerCommands() {
        registerCommand(new ShardTeleportCommand());
    }

    @Override
    public void registerCompletions(@Nonnull CommandCompletions<BukkitCommandCompletionContext> completions) {
        super.registerCompletions(completions);
    }
}
