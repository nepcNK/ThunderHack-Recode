package thunder.hack.modules.combat;

import com.google.common.eventbus.Subscribe;
import thunder.hack.Thunderhack;
import thunder.hack.events.impl.EventSync;
import thunder.hack.events.impl.PlayerUpdateEvent;
import thunder.hack.events.impl.Render3DEvent;
import thunder.hack.modules.Module;
import thunder.hack.modules.client.HudEditor;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render3DEngine;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import thunder.hack.utility.PlaceUtility;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

public class AutoWeb extends Module {
    public AutoWeb() {
        super("AutoWeb", Category.COMBAT);
    }

    private Setting<Boolean> rotate = new Setting<>("Rotate", true);
    private  Setting<Boolean> strictDirection = new Setting<>("StrictDirection", false);
    private  Setting<Integer> actionShift = new Setting<>("ActionShift", 2, 1, 2);
    private  Setting<Integer> actionInterval = new Setting<>("ActionInterval", 0, 0, 10);
    private  Setting<Float> placeRange = new Setting<>("TargetRange", 3.5F, 1f, 6f);
    private  Setting<Boolean> head = new Setting<>("Head", true);
    private  Setting<Boolean> toggelable = new Setting<>("DisableWhenDone", false);


    private int tickCounter = 0;

    private ConcurrentHashMap<BlockPos, Long> renderPoses = new ConcurrentHashMap<>();

    @Subscribe
    public void onRender3D(Render3DEvent event) {
        renderPoses.forEach((pos, time) -> {
            if (System.currentTimeMillis() - time > 500) {
                renderPoses.remove(pos);
            } else {
                Render3DEngine.drawBoxOutline(new Box(pos), HudEditor.getColor(0), 2);
            }
        });
    }

    @Subscribe
    public void onPlayerUpdate(PlayerUpdateEvent event) {
        if (!mc.player.isOnGround()) return;

        if (tickCounter < actionInterval.getValue()) {
            tickCounter++;
        }

        PlayerEntity nearestTarget = getNearestTarget();

        if (nearestTarget == null || tickCounter < actionInterval.getValue()) {
            return;
        }

        BlockPos feetPos = new BlockPos((int) Math.floor(nearestTarget.getX() + (nearestTarget.getX() - nearestTarget.prevX)), (int) Math.floor(nearestTarget.getY()), (int) Math.floor(nearestTarget.getZ() + (nearestTarget.getZ() - nearestTarget.prevZ)));

        int blocksPlaced = 0;

        while (blocksPlaced < actionShift.getValue()) {
            BlockPos nextPos = PlaceUtility.canPlaceBlock(feetPos,true) ? feetPos : head.getValue() ? PlaceUtility.canPlaceBlock(feetPos.up(),true) ? feetPos.up() : null : null;
            if (nextPos != null) {
                if (PlaceUtility.place(Blocks.COBWEB, nextPos, rotate.getValue(),  strictDirection.getValue(),true) != null) {
                    blocksPlaced++;
                    PlaceUtility.ghostBlocks.put(nextPos, System.currentTimeMillis());
                    renderPoses.put(nextPos, System.currentTimeMillis());
                    tickCounter = 0;
                } else {
                    break;
                }
            } else {
                if (toggelable.getValue()) {
                    toggle();
                }
                break;
            }
        }
    }

    private PlayerEntity getNearestTarget() {
        return mc.world.getPlayers()
                .stream()
                .filter(e -> e != mc.player)
                .filter(e -> !e.isDead())
                .filter(e -> !Thunderhack.friendManager.isFriend(e.getEntityName()))
                .filter(e -> e.getHealth() > 0)
                .filter(e -> mc.player.distanceTo(e) <= placeRange.getValue())
                .filter(this::isValidBase)
                .min(Comparator.comparing(e -> mc.player.distanceTo(e)))
                .orElse(null);
    }

    private boolean isValidBase(PlayerEntity player) {
        BlockPos basePos = BlockPos.ofFloored(player.getPos()).down();

        Block baseBlock = mc.world.getBlockState(basePos).getBlock();

        return !(baseBlock instanceof AirBlock) && !(baseBlock instanceof FluidBlock);
    }
}
