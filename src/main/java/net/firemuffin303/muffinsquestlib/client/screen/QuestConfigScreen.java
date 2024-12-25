package net.firemuffin303.muffinsquestlib.client.screen;

import com.mojang.logging.LogUtils;
import net.firemuffin303.muffinsquestlib.client.config.ModConfig;
import net.firemuffin303.muffinsquestlib.integration.ModMenuConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class QuestConfigScreen extends Screen {
    private float scrollAmount = 0;
    private float scrollScale = 5f;
    protected final Screen parent;

    public QuestConfigScreen(Screen parent) {
        super(Text.translatable("screen.quest_config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        GridWidget gridWidget = new GridWidget().setRowSpacing(10);

        int row = 1;
        for(int i = 0; i < ModConfig.OPTIONS.size(); i++){
            Map.Entry<String,List<ModConfig.ModOption<?>>> entry = ModConfig.OPTIONS.entrySet().stream().toList().get(i);
            String key = entry.getKey();
            List<ModConfig.ModOption<?>> modOptions = entry.getValue();
            GridWidget.Adder adder = new GridWidget().setRowSpacing(5).createAdder(1);
            adder.add(new TextWidget(Text.translatable("category.quest_lib."+key).formatted(Formatting.BOLD,Formatting.YELLOW,Formatting.UNDERLINE),this.textRenderer));

            GridWidget.Adder options = new GridWidget().setColumnSpacing(10).setRowSpacing(5).createAdder(2);
            options.add(EmptyWidget.ofWidth(310));
            options.add(EmptyWidget.ofWidth(80));

            for(ModConfig.ModOption<?> modOption : modOptions){
                TextWidget textWidget = new TextWidget(Text.translatable(modOption.getId()),this.textRenderer);
                ButtonWidget buttonWidget = ButtonWidget.builder(Text.translatable(modOption.getId()+"."+modOption.getValueAsString()),button -> {
                    modOption.changeValue(Screen.hasShiftDown());
                    button.setMessage(Text.translatable(modOption.getId()+"."+modOption.getValueAsString()));

                }).width(80).build();
                options.add(textWidget,Positioner.create().marginTop(6));
                options.add(buttonWidget);
            }

            gridWidget.add(adder.getGridWidget(),row,0);
            gridWidget.add(options.getGridWidget(),row+1,0);
            row += 2;
        }

        gridWidget.setX(35);
        gridWidget.setY(40);
        gridWidget.refreshPositions();
        gridWidget.forEachChild(this::addDrawableChild);

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE,button -> {
            ModConfig.save();
            this.close();
        }).dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 14, 16777215);
        context.setShaderColor(0.125F, 0.125F, 0.125F, 1.0F);
        if(this.client != null && this.client.world == null){
            context.drawTexture(OPTIONS_BACKGROUND_TEXTURE, 0, 36, 0.0F, 0.0F, this.width, this.height - 72, 32, 32);
        }
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        super.render(context,mouseX,mouseY,delta);


    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        float f = (float) (amount / this.scrollScale);
        this.scrollAmount = MathHelper.clamp(this.scrollAmount - f, 0.0f,1.0f);
        return true;
    }


    protected int getScrollbarPositionX() {
        return this.width / 2 + 124;
    }

    @Override
    public void removed() {
        ModConfig.save();
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
