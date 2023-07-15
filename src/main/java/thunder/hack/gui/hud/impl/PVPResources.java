package thunder.hack.gui.hud.impl;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import thunder.hack.events.impl.Render2DEvent;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.hud.HudElement;
import thunder.hack.modules.Module;
import thunder.hack.modules.client.HudEditor;
import thunder.hack.utility.render.Render2DEngine;

import java.util.ArrayList;
import java.util.List;

public class PVPResources extends HudElement {

    public PVPResources() {
        super("PVPResources","PAwdwad",60,60);
    }

    @Subscribe
    public void onRender2D(Render2DEvent e) {
        super.onRender2D(e);
        int y_offset1 = 30;
        float max_width = 50;



        Render2DEngine.drawGradientBlurredShadow(e.getMatrixStack(),getPosX() + 1, getPosY() + 1, max_width - 2, 18 + y_offset1, 10, HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(180), HudEditor.getColor(90));
        Render2DEngine.renderRoundedGradientRect(e.getMatrixStack(), HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(180), HudEditor.getColor(90),getPosX() - 0.5f, getPosY() - 0.5f, max_width + 1, 21 + y_offset1, HudEditor.hudRound.getValue());
        Render2DEngine.drawRound(e.getMatrixStack(),getPosX(), getPosY(), max_width, 20 + y_offset1, HudEditor.hudRound.getValue(), HudEditor.plateColor.getValue().getColorObject());

        Render2DEngine.horizontalGradient(e.getMatrixStack(),getPosX() + 2, getPosY() + 24.5, getPosX() + 26, getPosY() + 25,Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(),0).getRGB(), HudEditor.textColor.getValue().getColorObject().getRGB());
        Render2DEngine.horizontalGradient(e.getMatrixStack(), getPosX() + 26, getPosY() + 24.5, getPosX() + 48, getPosY() + 25, HudEditor.textColor.getValue().getColorObject().getRGB(),Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(),0).getRGB());

        Render2DEngine.verticalGradient(e.getMatrixStack(),getPosX() + 25.5, getPosY() + 2, getPosX() + 26, getPosY() + 23,Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(),0).getRGB(), HudEditor.textColor.getValue().getColorObject().getRGB());
        Render2DEngine.verticalGradient(e.getMatrixStack(), getPosX() + 25.5, getPosY() + 23, getPosX() + 26, getPosY() + 48, HudEditor.textColor.getValue().getColorObject().getRGB(),Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(),0).getRGB());



        int totemCount = getItemCount(Items.TOTEM_OF_UNDYING);
        int xpCount = getItemCount(Items.EXPERIENCE_BOTTLE);
        int crystalCount = getItemCount(Items.END_CRYSTAL);
        int gappleCount = getItemCount(Items.ENCHANTED_GOLDEN_APPLE);

        List<ItemStack> list = new ArrayList<>();

        if (totemCount > 0) list.add(new ItemStack(Items.TOTEM_OF_UNDYING, totemCount));
        if (xpCount > 0) list.add(new ItemStack(Items.EXPERIENCE_BOTTLE, xpCount));
        if (crystalCount > 0) list.add(new ItemStack(Items.END_CRYSTAL, crystalCount));
        if (gappleCount > 0) list.add(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE,gappleCount));


        for (int i = 0; i < list.size(); ++i) {
            int offsetX = i % 2 * 25;
            int offsetY = i / 2 * 25;
            e.getContext().drawItem(list.get(i),(int) (getPosX() + offsetX + 4), (int) (getPosY() + offsetY + 2));
          //  e.getContext().drawItemInSlot(mc.textRenderer,list.get(i),(int) (getPosX() + offsetX + 4), (int) (getPosY() + offsetY + 4));
            FontRenderers.sf_bold_mini.drawCenteredString(e.getMatrixStack(), String.valueOf(list.get(i).getCount()),(int) (getPosX() + offsetX + 12), (int) (getPosY() + offsetY + 16),HudEditor.textColor.getValue().getColor());
        }
    }


    public int getItemCount(Item item) {
        if (mc.player == null) {
            return 0;
        }
        int n = 0;
        int n2 = 44;
        for (int i = 0; i <= n2; ++i) {
            ItemStack itemStack = mc.player.getInventory().getStack(i);
            if (itemStack.getItem() != item) continue;
            n += itemStack.getCount();
        }
        return n;
    }
}
