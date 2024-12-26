package net.firemuffin303.muffinsquestlib.client.config;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Pair;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.NotNull;

import java.io.*;
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


    public static List<Pair<String, List<ModOption<?>>>> OPTIONS = List.of(
            new Pair<>("hud_options",List.of(SHOW_HUD,ICON_POSITION)),
            new Pair<>("quest_mob_options",List.of(QUEST_MOB_STYLE)));


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

                    if(jsonObject.getAsJsonPrimitive(QUEST_MOB_STYLE.getId()) != null){
                        QUEST_MOB_STYLE.setValue(QuestMobShown.byId(jsonObject.getAsJsonPrimitive(QUEST_MOB_STYLE.getId()).getAsInt()));
                    }

                    if(jsonObject.getAsJsonObject(ICON_POSITION.getId()) != null){
                        JsonObject iconPosition = jsonObject.getAsJsonObject(ICON_POSITION.getId());
                        JsonPrimitive xJSON = iconPosition.getAsJsonPrimitive("x");
                        JsonPrimitive yJSON = iconPosition.getAsJsonPrimitive("y");
                        if(xJSON != null && yJSON != null){
                            float x = MathHelper.clamp(xJSON.getAsFloat(),0.0f,1.0f);
                            float y = MathHelper.clamp(yJSON.getAsFloat(),0.0f,1.0f);
                            ICON_POSITION.setValue(new Vec2f(x,y));
                        }

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

        String config = GSON.toJson(getJsonObject());

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

    private static @NotNull JsonObject getJsonObject() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(SHOW_HUD.id,SHOW_HUD.getCurrentValue());

        JsonObject iconPosition = new JsonObject();
        iconPosition.addProperty("x",ICON_POSITION.getCurrentValue().x);
        iconPosition.addProperty("y",ICON_POSITION.getCurrentValue().y);

        jsonObject.add(ICON_POSITION.getId(), iconPosition);
        jsonObject.addProperty(QUEST_MOB_STYLE.id,QUEST_MOB_STYLE.getCurrentValue().getId());
        return jsonObject;
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

    public enum QuestMobShown implements TranslatableOption {
        NONE(0,"none"),
        ICON(1,"icon"),
        OUTLINE(2,"outline"),
        ICON_AND_OUTLINE(3,"icon_and_outline");

        final int id;
        final String key;

        QuestMobShown(int id,String key){
            this.id = id;
            this.key = key;
        }

        public boolean isIcon(){
            return this == ICON || this == ICON_AND_OUTLINE;
        }

        public boolean isOutline(){
            return this == OUTLINE || this == ICON_AND_OUTLINE;
        }

        @Override
        public int getId() {
            return this.id;
        }

        @Override
        public String getTranslationKey() {
            return this.key;
        }

        public static QuestMobShown byId(int id){
            List<QuestMobShown> questMobShown = List.of(values());
            int i = MathHelper.clamp(id,0,questMobShown.size()-1);
            return questMobShown.get(i);
        }
    }
}
