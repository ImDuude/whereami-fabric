package command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.structure.Structure;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WhereAmICommand {

    public static int executeHere(CommandContext<ServerCommandSource> context) {

        ServerPlayerEntity player = context.getSource().getPlayer();
        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getBlockPos();
        ChunkPos center = new ChunkPos(pos);

        boolean found = false;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {

                Chunk chunk = world.getChunk(
                        center.x + dx,
                        center.z + dz,
                        ChunkStatus.STRUCTURE_STARTS
                );

                for (Map.Entry<Structure, StructureStart> entry
                        : chunk.getStructureStarts().entrySet()) {

                    StructureStart start = entry.getValue();
                    if (!start.hasChildren()) continue;

                    if (start.getBoundingBox().contains(pos)) {

                        Identifier id = world.getRegistryManager()
                                .get(RegistryKeys.STRUCTURE)
                                .getId(entry.getKey());

                        context.getSource().sendFeedback(
                                () -> Text.literal("You are inside structure: " + id),
                                false
                        );

                        found = true;
                    }
                }
            }
        }

        if (!found) {
            context.getSource().sendFeedback(
                    () -> Text.literal("You are not inside any known structure."),
                    false
            );
        }

        return 1;
    }


    public static int executeScan(CommandContext<ServerCommandSource> context, int radius) {

        ServerPlayerEntity player = context.getSource().getPlayer();
        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getBlockPos();
        ChunkPos center = new ChunkPos(pos);

        Set<Identifier> foundStructures = new HashSet<>();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {

                Chunk chunk = world.getChunk(
                        center.x + dx,
                        center.z + dz,
                        ChunkStatus.STRUCTURE_STARTS
                );

                chunk.getStructureStarts().forEach((structure, start) -> {
                    if (!start.hasChildren()) return;

                    Identifier id = world.getRegistryManager()
                            .get(RegistryKeys.STRUCTURE)
                            .getId(structure);

                    foundStructures.add(id);
                });
            }
        }

        if (foundStructures.isEmpty()) {
            context.getSource().sendFeedback(
                    () -> Text.literal("No structures found within radius " + radius + "."),
                    false
            );
        } else {
            context.getSource().sendFeedback(
                    () -> Text.literal("Structures found within radius " + radius + ":"),
                    false
            );

            foundStructures.forEach(id ->
                    context.getSource().sendFeedback(
                            () -> Text.literal("- " + id),
                            false
                    )
            );
        }

        return 1;
    }


}