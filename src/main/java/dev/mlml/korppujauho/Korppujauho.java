package dev.mlml.korppujauho;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public class Korppujauho implements ModInitializer {
    private void registerSafezones() {
        // Block hitting players
        AttackEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            // We only care about players attacking players
            if (!(entity instanceof PlayerEntity)) {
                return ActionResult.PASS;
            }

            // PVP is disabled in the north, but allowed in the south
            if (player.getPos().getZ() < 0 || entity.getPos().getZ() < 0) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });


        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (!(entity instanceof PlayerEntity)) {
                return true;
            }

            if (source.getAttacker() instanceof PlayerEntity && source.getAttacker().getUuid() != entity.getUuid()) {
                PlayerEntity attacker = (PlayerEntity) source.getAttacker();
                if (entity.getPos().getZ() < 0 || attacker.getPos().getZ() < 0) {
                    return false;
                }
            }

            return true;
        });
    }

    @Override
    public void onInitialize() {
        registerSafezones();
    }
}
