package com.whereami;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import command.WhereAmICommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;

public class WhereAmI implements ModInitializer {
	public static final String MOD_ID = "where-am-i";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(
					CommandManager.literal("whereami")

							// /whereami here
							.then(CommandManager.literal("here")
									.executes(WhereAmICommand::executeHere)
							)

							// /whereami scan <radius>
							.then(CommandManager.literal("scan")
									.then(CommandManager.argument("radius", IntegerArgumentType.integer(0, 10))
											.executes(ctx ->
													WhereAmICommand.executeScan(
															ctx,
															IntegerArgumentType.getInteger(ctx, "radius")
													)
											)
									)
							)
			);
		});

		LOGGER.info("WhereAmI initialisiert");
	}
}