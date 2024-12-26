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
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

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
            Pair<String,List<ModConfig.ModOption<?>>> entry = ModConfig.OPTIONS.get(i);
            String key = entry.getLeft();
            List<ModConfig.ModOption<?>> modOptions = entry.getRight();
            GridWidget.Adder adder = new GridWidget().setRowSpacing(5).createAdder(1);
            adder.add(new TextWidget(Text.translatable("category.quest_lib."+key).formatted(Formatting.BOLD,Formatting.YELLOW,Formatting.UNDERLINE),this.textRenderer));

            GridWidget.Adder options = new GridWidget().setColumnSpacing(10).setRowSpacing(5).createAdder(2);
            options.add(EmptyWidget.ofWidth(280));
            options.add(EmptyWidget.ofWidth(120));

            for(ModConfig.ModOption<?> modOption : modOptions){
                TextWidget textWidget = new TextWidget(Text.translatable(modOption.getId()),this.textRenderer);
                Widget widget;
                if(modOption == ModConfig.ICON_POSITION){
                    //This is stupid, but whatever.
                    textWidget = new TextWidget(Text.translatable(modOption.getId()+".x"),this.textRenderer);
                    widget = new SliderWidget(0,0,120,20,Text.literal(String.format("x : %.02f",ModConfig.ICON_POSITION.getCurrentValue().x)),ModConfig.ICON_POSITION.getCurrentValue().x) {
                        @Override
                        protected void updateMessage() {
                            String sliderText = String.format("x : %.02f",this.value);
                            this.setMessage(Text.literal(sliderText));
                        }

                        @Override
                        protected void applyValue() {
                            ModConfig.ICON_POSITION.setValue(new Vec2f((float) this.value,ModConfig.ICON_POSITION.getCurrentValue().y));
                        }
                    };

                    options.add(textWidget,Positioner.create().marginTop(6));
                    options.add(widget);

                    textWidget = new TextWidget(Text.translatable(modOption.getId()+".y"),this.textRenderer);

                    widget = new SliderWidget(0,0,120,20,Text.literal(String.format("y : %.02f",ModConfig.ICON_POSITION.getCurrentValue().y)),ModConfig.ICON_POSITION.getCurrentValue().y) {
                        @Override
                        protected void updateMessage() {
                            String sliderText = String.format("y : %.02f",this.value);
                            this.setMessage(Text.literal(sliderText));
                        }

                        @Override
                        protected void applyValue() {

                            ModConfig.ICON_POSITION.setValue(new Vec2f(ModConfig.ICON_POSITION.getCurrentValue().x,(float)this.value));
                        }
                    };

                    options.add(textWidget,Positioner.create().marginTop(6));
                    options.add(widget);

                }else{
                    widget = ButtonWidget.builder(Text.translatable(modOption.getId()+"."+modOption.getValueAsString()),button -> {
                        modOption.changeValue(Screen.hasShiftDown());
                        button.setMessage(Text.translatable(modOption.getId()+"."+modOption.getValueAsString()));
                    }).width(80).build();

                    options.add(textWidget,Positioner.create().marginTop(6));
                    options.add(widget,Positioner.create().alignRight());
                }
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
