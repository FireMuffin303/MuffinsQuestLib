package net.firemuffin303.muffinsquestlib.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.firemuffin303.muffinsquestlib.client.screen.QuestConfigScreen;

public class ModMenuConfig implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return QuestConfigScreen::new;
    }
}
