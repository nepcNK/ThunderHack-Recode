package thunder.hack.modules.render;

import thunder.hack.modules.Module;
import thunder.hack.setting.Setting;

public class Fullbright extends Module {
    public Fullbright() {
        super("Fullbright", "Fullbright", Category.RENDER);
    }

    public Setting<Integer> brightness = new Setting<>("Brightness", 15, 0, 15);

}
