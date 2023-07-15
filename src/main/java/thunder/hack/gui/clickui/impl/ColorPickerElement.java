package thunder.hack.gui.clickui.impl;

import net.minecraft.client.gui.DrawContext;
import thunder.hack.gui.clickui.AbstractElement;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.setting.Setting;
import thunder.hack.utility.math.MathUtil;
import thunder.hack.utility.render.Render2DEngine;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class ColorPickerElement extends AbstractElement {

    private float hue;
    private float saturation;
    private float brightness;
    private int alpha;

    private boolean afocused;
    private boolean hfocused;
    private boolean sbfocused;

    private float spos, bpos, hpos, apos;

    private Color prevColor;
    
    private boolean firstInit;

    private Setting colorSetting;
    public ColorSetting getColorSetting() {
        return (ColorSetting) colorSetting.getValue();
    }

    public ColorPickerElement(Setting setting) {
        super(setting);
        this.colorSetting = setting;
        prevColor = getColorSetting().getColorObject();
        updatePos();
        
        double cx = x + 4;
        double cy = y + 17;
        double cw = width - 34;
        double ch = height - 20;
        
        firstInit = true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        MatrixStack matrixStack = context.getMatrices();

        Render2DEngine.drawRound(matrixStack,(float) x + 5, (float) y + 2, (float) width - 10, (float) height - 4, 4, new Color(bgcolor));
        FontRenderers.getRenderer().drawString(matrixStack, setting.getName(), (int) x + 8, (int) y + 7, new Color(-1).getRGB());

        Render2DEngine.drawBlurredShadow(matrixStack,(int) (x + width - 25), (int) (y + 5), 14, 6, 10, getColorSetting().getColorObject());
        Render2DEngine.drawRound(matrixStack,(float) (x + width - 25), (float) (y + 5), 14, 6, 1, getColorSetting().getColorObject());

        if(!getColorSetting().isCycle()){
            FontRenderers.getRenderer().drawString(matrixStack,"R", (int) (x + width - 35), (int) (y + 7), new Color(0x484848).getRGB());
        } else {
            FontRenderers.getRenderer().drawString(matrixStack,"R", (int) (x + width - 35), (int) (y + 7), new Color(-1).getRGB());
        }


        renderPicker(matrixStack,mouseX, mouseY, getColorSetting().getColorObject());
    }

    private void renderPicker(MatrixStack matrixStack,int mouseX, int mouseY, Color color) {
        double cx = x + 6;
        double cy = y + 16;
        double cw = width - 38;
        double ch = height - 20;

        if (prevColor != getColorSetting().getColorObject()) {
            updatePos();
            prevColor = getColorSetting().getColorObject();
        }

        if (firstInit) {
            spos = (float) ((cx + cw) - (cw - (cw * saturation)));
            bpos = (float) ((cy + (ch - (ch * brightness))));
            hpos = (float) ((cy + (ch - 3 + ((ch - 3) * hue))));
            apos = (float) ((cy + (ch - 3 - ((ch - 3) * (alpha / 255f)))));
            firstInit = false;
        }

        spos = Render2DEngine.scrollAnimate(spos, (float) ((cx + cw) - (cw - (cw * saturation))), .6f);
        bpos = Render2DEngine.scrollAnimate(bpos, (float) (cy + (ch - (ch * brightness))), .6f);
        hpos = Render2DEngine.scrollAnimate(hpos, (float) (cy + (ch - 3 + ((ch - 3) * hue))), .6f);
        apos = Render2DEngine.scrollAnimate(apos, (float) (cy + (ch - 3 - ((ch - 3) * (alpha / 255f)))), .6f);

        Color colorA = Color.getHSBColor(hue, 0.0F, 1.0F), colorB = Color.getHSBColor(hue, 1.0F, 1.0F);
        Color colorC = new Color(0, 0, 0, 0), colorD = new Color(0, 0, 0);

        Render2DEngine.horizontalGradient(matrixStack,(float) cx + 2, (float) cy, cx + cw, cy + ch, colorA.getRGB(), colorB.getRGB());
        Render2DEngine.verticalGradient(matrixStack,cx + 2, cy, cx + cw, cy + ch, colorC.getRGB(), colorD.getRGB());

        for (float i = 1f; i < ch - 2f; i += 1f) {
            float curHue = (float) (1f / (ch / i));
            Render2DEngine.drawRect(matrixStack,(float) (cx + cw + 4), (float) (cy + i), 8, 1, Color.getHSBColor(curHue, 1f, 1f));
        }

        Render2DEngine.drawRect(matrixStack, (float) (cx + cw + 17), (float) (cy + 1f), 8f, (float) (ch - 3), new Color(0xFFFFFFFF));

        Render2DEngine.verticalGradient(matrixStack,cx + cw + 17, cy + 0.8, cx + cw + 25, cy + ch - 2,
                new Color(color.getRed(), color.getGreen(), color.getBlue(), 255).getRGB(),
                new Color(0, 0, 0, 0).getRGB());

        Render2DEngine.drawRect(matrixStack, (float) (cx + cw + 3), hpos + 0.5f, 10, 1, Color.WHITE);
        Render2DEngine.drawRect(matrixStack,(float) (cx + cw + 16), apos + 0.5f, 10, 1, Color.WHITE);
        Render2DEngine.drawRound(matrixStack,spos, bpos, 3, 3, 1.5f, new Color(-1));

        Color value = Color.getHSBColor(hue, saturation, brightness);

        if (sbfocused) {
            saturation = (float) ((MathUtil.clamp((float) (mouseX - cx), 0f, (float) cw)) / cw);
            brightness = (float) ((ch - MathUtil.clamp((float) (mouseY - cy), 0, (float) ch)) / ch);
            value = Color.getHSBColor(hue, saturation, brightness);
            setColor(new Color(value.getRed(), value.getGreen(), value.getBlue(), alpha));
        }

        if (hfocused) {
            hue = (float) -((ch - MathUtil.clamp((float) (mouseY - cy), 0, (float) ch)) / ch);
            value = Color.getHSBColor(hue, saturation, brightness);
            setColor(new Color(value.getRed(), value.getGreen(), value.getBlue(), alpha));
        }

        if (afocused) {
            alpha = (int) (((ch - MathUtil.clamp((float) (mouseY - cy), 0, (float) ch)) / ch) * 255);
            setColor(new Color(value.getRed(), value.getGreen(), value.getBlue(), alpha));
        }
    }

    private void updatePos() {
        float[] hsb = Color.RGBtoHSB(getColorSetting().getColorObject().getRed(), getColorSetting().getColorObject().getGreen(), getColorSetting().getColorObject().getBlue(), null);
        hue = -1 + hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
        alpha = getColorSetting().getColorObject().getAlpha();
    }

    private void setColor(Color color) {
        getColorSetting().setColor(color.getRGB());
        prevColor = color;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        double cx = x + 4;
        double cy = y + 17;
        double cw = width - 34;
        double ch = height - 20;

        if (Render2DEngine.isHovered(mouseX, mouseY, cx + cw + 17, cy, 8, ch) && button == 0)
            afocused = true;

        else if (Render2DEngine.isHovered(mouseX, mouseY, cx + cw + 4, cy, 8, ch) && button == 0)
            hfocused = true;

        else if (Render2DEngine.isHovered(mouseX, mouseY, cx, cy, cw, ch) && button == 0)
            sbfocused = true;

        else if (Render2DEngine.isHovered(mouseX, mouseY, (float) (x + width - 35), (float) (y + 7), 10, 10) && button == 0)
            getColorSetting().setCycle(!getColorSetting().isCycle());
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        hfocused = false;
        afocused = false;
        sbfocused = false;
    }

    @Override
    public void onClose() {
        hfocused = false;
        afocused = false;
        sbfocused = false;
    }

}
