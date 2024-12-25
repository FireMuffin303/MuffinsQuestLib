package net.firemuffin303.muffinsquestlib.client.config;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.firemuffin303.muffinsquestlib.client.screen.QuestConfigScreen;
import net.firemuffin303.muffinsquestlib.integration.ModMenuConfig;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Vec2f;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ModConfig {
    protected static File OPTION_FILE = null;
    public static final Gson GSON = (new GsonBuilder()).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();

    //Quest Hud Configuration
    public static ModOption<Boolean> SHOW_HUD = new ModOption<>("config.quest_lib.show_hud",true,List.of(Boolean.TRUE,Boolean.FALSE));
    public static ModOption<Vec2f> ICON_POSITION = new ModOption<>("config.quest_lib.icon_position",new Vec2f(0.0f,0.0f), List.of());

    //Quest Mob Configuration
    public static ModOption<QuestMobShown> QUEST_MOB_STYLE = new ModOption<>("config.quest_lib.quest_mob_style",QuestMobShown.ICON,List.of(QuestMobShown.values()));


    public static Map<String, List<ModOption<?>>> OPTIONS = Map.of(
            "hud_options",List.of(SHOW_HUD,ICON_POSITION),
            "quest_mob_options",List.of(QUEST_MOB_STYLE));


    public static void load(){
        if(OPTION_FILE == null){
            OPTION_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(),"muffins_questlib.json");

            try{
                if(!OPTION_FILE.exists()){
                    save();
                }

                if(OPTION_FILE.exists()){
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(OPTION_FILE));
                    JsonObject jsonObject = JsonParser.parseReader(bufferedReader).getAsJsonObject();

                    if(jsonObject.getAsJsonPrimitive(SHOW_HUD.id) != null){
                        SHOW_HUD.setValue(jsonObject.getAsJsonPrimitive(SHOW_HUD.id).getAsBoolean());
                    }

                }


            }catch (FileNotFoundException exception){
                LogUtils.getLogger().error("Couldn't load Muffin's Quest Lib configuration file.");
                LogUtils.getLogger().error("Reverting to use default settings");
                exception.printStackTrace();
            }
        }
    }

    public static void save(){
        if(OPTION_FILE == null){
            OPTION_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(),"muffins_questlib.json");
        }

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(SHOW_HUD.id,SHOW_HUD.getCurrentValue());
        jsonObject.addProperty(QUEST_MOB_STYLE.id,QUEST_MOB_STYLE.getCurrentValue().asString());

        String config = GSON.toJson(jsonObject);

        try{
            FileWriter fileWriter = new FileWriter(OPTION_FILE);
            try {
                fileWriter.write(config);
            }catch (Throwable var){
                try {
                    fileWriter.close();
                }catch (Throwable var2){
                    var.addSuppressed(var2);
                }
                throw var;
            }
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }


    public static class ModOption<T>{
        private String id;
        private T defaultValue;
        private T currentValue;
        private List<T> values;

        public ModOption(String id, T defaultValue,List<T> values){
            this.id = id;
            this.defaultValue = defaultValue;
            this.currentValue = defaultValue;
            this.values = values;
        }

        public void setValue(T value){
            this.currentValue = value;
        }

        public void changeValue(boolean isShiftDown){
            if(this.values.isEmpty()) return;

            int i = this.values.indexOf(this.currentValue);
            if(isShiftDown){
                this.currentValue = this.values.get((i - 1) < 0 ? this.values.size() - 1 : i - 1);
            }else{
                this.currentValue = this.values.get((i + 1) >= this.values.size() ? 0 : i + 1);
            }

        }

        public String getId() {
            return id;
        }

        public T getDefaultValue() {
            return defaultValue;
        }

        public T getCurrentValue(){
            return this.currentValue;
        }

        public String getValueAsString(){
            return this.currentValue.toString().toLowerCase(Locale.ROOT);
        }
    }

    public enum QuestMobShown implements StringIdentifiable {
        NONE("none"),
        ICON("icon"),
        OUTLINE("outline"),
        ICON_AND_OUTLINE("icon_and_outline");

        final String id;

        QuestMobShown(String id){
            this.id = id;
        }

        public boolean isIcon(){
            return this == ICON || this == ICON_AND_OUTLINE;
        }

        public boolean isOutline(){
            return this == OUTLINE || this == ICON_AND_OUTLINE;
        }

        @Override
        public String asString() {
            return this.id;
        }
    }
}
