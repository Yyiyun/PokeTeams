package io.github.tsecho.poketeams;

import com.google.inject.Inject;
import com.pixelmonmod.pixelmon.Pixelmon;
import io.github.tsecho.poketeams.apis.PlaceholderAPI;
import io.github.tsecho.poketeams.commands.Base;
import io.github.tsecho.poketeams.configuration.ConfigManager;
import io.github.tsecho.poketeams.eventlisteners.*;
import io.github.tsecho.poketeams.utilities.Tasks;
import io.github.tsecho.poketeams.utilities.Utils;
import io.github.tsecho.poketeams.utilities.WorldInfo;
import lombok.Getter;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.nio.file.Path;

@Plugin(id = PokeTeams.ID, 
		name = PokeTeams.NAME, 
		authors = PokeTeams.AUTHORS, 
		description = PokeTeams.DESCRIPTION,
		version = PokeTeams.VERSION,
		dependencies = {@Dependency(id = Pixelmon.MODID, version = Pixelmon.VERSION), 
					    @Dependency(id = "placeholderapi", optional = true)})

public class PokeTeams {

	public static final String ID = "poketeams";
	public static final String NAME = "PokeTeams";
	public static final String VERSION = "4.0.1";
	public static final String AUTHORS = "TSEcho";
	public static final String DESCRIPTION = "Teams plugin with Pixelmon Reforged Support";

	@Getter
	private static PokeTeams instance;
	
	@Inject
	@Getter
	private Logger logger;

	@Inject 
	@ConfigDir(sharedRoot = false)
	private Path dir;
	
	@Inject
	@Getter
	private PluginContainer container;

	@Listener
	public void onPreInit(GamePreInitializationEvent e) {
		instance = this;
		ConfigManager.setup(dir);
	}
	
	@Listener
	public void onInit(GameInitializationEvent e) {
		PlaceholderAPI.getInstance();
		Sponge.getCommandManager().register(instance, Base.build(), "poketeams", "teams", "team");
		Sponge.getEventManager().registerListener(this, MessageChannelEvent.Chat.class, Order.FIRST, new ChatListener());
		Sponge.getEventManager().registerListeners(this, new ConnectionListener());
		Pixelmon.EVENT_BUS.register(new WildBattleListener());
		Pixelmon.EVENT_BUS.register(new CatchPokemonListener());
		Pixelmon.EVENT_BUS.register(new PlayerBattleListener());
	}
	
	@Listener
	public void onStart(GameStartedServerEvent e) {
		WorldInfo.init();
		/* Temporary method here to transition from player names to UUIDs */
		Utils.moveToUUID();
		new Tasks();
	}
	
	@Listener
	public void onReload(GameReloadEvent e) {
		ConfigManager.load();
		logger.info("PokeTeams has been reloaded!");
	}
}
