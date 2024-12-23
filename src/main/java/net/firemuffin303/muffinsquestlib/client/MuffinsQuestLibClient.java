package net.firemuffin303.muffinsquestlib.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.firemuffin303.muffinsquestlib.client.screen.QuestInfoScreen;
import net.firemuffin303.muffinsquestlib.common.item.QuestTooltipComponent;
import net.firemuffin303.muffinsquestlib.common.item.QuestTooltipData;
import net.firemuffin303.muffinsquestlib.common.network.PacketHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class MuffinsQuestLibClient implements ClientModInitializer {

    public static final KeyBinding QUEST_INFO_BTN = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.muffins_questlib.questinfo",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_O,
                    "category.muffins_questlib"));

    @Override
    public void onInitializeClient() {
        PacketHandler.clientInit();
        HudRenderCallback.EVENT.register(ModHudRender::init);

        ClientTickEvents.END_CLIENT_TICK.register(client ->{
            while (QUEST_INFO_BTN.wasPressed()){
                client.setScreen(new QuestInfoScreen());
            }
        });

        TooltipComponentCallback.EVENT.register(tooltipData -> {
            if(tooltipData instanceof QuestTooltipData questTooltipData){
                return new QuestTooltipComponent(questTooltipData);
            }
            return null;
        });
    }
}
