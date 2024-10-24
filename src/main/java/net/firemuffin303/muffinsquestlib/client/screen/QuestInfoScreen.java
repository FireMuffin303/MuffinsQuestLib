package net.firemuffin303.muffinsquestlib.client.screen;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.client.MuffinsQuestLibClient;
import net.firemuffin303.muffinsquestlib.common.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.firemuffin303.muffinsquestlib.common.quest.QuestType;
import net.firemuffin303.muffinsquestlib.common.quest.data.QuestData;
import net.firemuffin303.muffinsquestlib.common.registry.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuestInfoScreen extends Screen {
    public static final Identifier QUEST_SCREEN_TEXTURE = MuffinsQuestLib.modId("textures/gui/quest_screen.png");
    @Nullable
    private QuestInstance questInstance;

    private QuestCancelButton questCancelButton;
    private final List<QuestDataButtonWidget> questDataButtonWidgets = new ArrayList<>();
    private boolean shouldScroll;
    private float scrollAmount = 0;
    private float scrollScale = 0;

    public QuestInfoScreen() {
        super(Text.translatable("quest_info_screen.title"));
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - 176) /2;
        int j = (this.height - 166) /2;
        Objects.requireNonNull(this.client);
        int h = (int) ((this.scrollAmount * 102)-j)*-1;
        if(this.client.player != null){
            if(((PlayerQuestData.PlayerQuestDataAccessor)this.client.player).questLib$getData().hasQuest()){
                this.questInstance = ((PlayerQuestData.PlayerQuestDataAccessor)this.client.player).questLib$getData().getQuestInstance();
                if(this.client.player != null){
                    if(((PlayerQuestData.PlayerQuestDataAccessor)this.client.player).questLib$getData().hasQuest()){
                        this.questInstance = ((PlayerQuestData.PlayerQuestDataAccessor)this.client.player).questLib$getData().getQuestInstance();
                        for(QuestType<?> questType: this.questInstance.getQuest().questTypes.keySet().stream().toList()) {
                            this.scrollScale += this.questInstance.getQuestData(questType).size();
                        }
                    }
                }
            }
        }

        questCancelButton = this.addDrawableChild(new QuestCancelButton(i+40,j+135,button -> {
            if(((PlayerQuestData.PlayerQuestDataAccessor)this.client.player).questLib$getData().getQuestInstance() != null){
                PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
                ClientPlayNetworking.send(MuffinsQuestLib.modId("clear_quest_c2s"), packetByteBuf);
                ((PlayerQuestData.PlayerQuestDataAccessor)this.client.player).questLib$getData().getQuestInstance().setState(QuestInstance.State.FAIL);
                this.close();
            }
        }));

        this.shouldScroll = ((PlayerQuestData.PlayerQuestDataAccessor)this.client.player).questLib$getData().hasQuest() && this.scrollScale > 1;

    }

    

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        Objects.requireNonNull(this.client);
        Objects.requireNonNull(this.client.player);
        this.renderForeground(context,mouseX,mouseY);
        this.questCancelButton.active = ((PlayerQuestData.PlayerQuestDataAccessor)this.client.player).questLib$getData().getQuestInstance() != null;


    }

    @Override
    public void renderBackground(DrawContext context) {
        int i = (this.width - 176) /2;
        int j = (this.height - 166) /2;
        super.renderBackground(context);
        int k = (int)(87.0F * this.scrollAmount);
        context.drawTexture(QUEST_SCREEN_TEXTURE,i,j,0,0,176,166);
        context.drawTexture(QUEST_SCREEN_TEXTURE,i+156,j+25+k,this.shouldScroll ? 176 : 188,48,12,15);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (this.shouldScroll) {
            float f = (float)amount / this.scrollScale;
            this.scrollAmount = MathHelper.clamp(this.scrollAmount - f, 0.0F, 1.0F);
        }

        return true;
    }

    public void renderForeground(DrawContext context,int mouseX,int mouseY){
        int i = (this.width - 176) /2;
        int j = (this.height - 166) /2;
        this.renderInfo(context,i, j);

        context.drawText(this.textRenderer,this.title,i+8,j+12,0x0b0b0b,false);
    }

    private void renderInfo(DrawContext context,int x,int y){
        Objects.requireNonNull(this.client);

        int j = (this.height - 166) /2;
        y = (int) (this.scrollScale*(this.scrollAmount * 14)-y)*-1;
        if(this.client.player != null){
            if(((PlayerQuestData.PlayerQuestDataAccessor)this.client.player).questLib$getData().getQuestInstance() == null){
                context.drawText(this.textRenderer,Text.translatable("quest_info_screen.no_quest"),x+12,y+70,0xffffff,false);
            }else{
                context.enableScissor(0,j+25,this.width,j+127);

                Objects.requireNonNull(questInstance);

                int progressY = y+68;
                List<QuestType<?>> questTypes = questInstance.getQuest().questTypes.keySet().stream().toList();
                for(QuestType<?> questType : questTypes){
                    for(int k = 0; k < questInstance.getQuest().getQuests(questType).size() ; k++) {
                        QuestData questData = questInstance.getQuest().getQuests(questType).get(k);
                        int current = questInstance.getProgressType(questType).get(k);
                        int requireAmount = questData.getRequirementAmount();

                        context.drawTexture(QUEST_SCREEN_TEXTURE,x+8,progressY,0,166,145,19);
                        int color = current >= requireAmount ? 5635925 : 11141120;
                        Text text = Text.translatable("quest_info_screen.progress", Text.translatable(questData.toString()).getString() ,current,requireAmount);
                        context.drawText(this.textRenderer,text.asOrderedText(), x + 32,progressY+6,color,false );
                        progressY += 19;
                    }
                }

                context.drawItemWithoutEntity(new ItemStack(ModItems.QUEST_PAPER_ITEM,1),x+10,y+26);
                context.drawText(this.textRenderer,Text.translatable("quest_info_screen.quest_name", ""),x+32,y+30,0xffffff,false);

                Text description = Text.translatable(questInstance.getQuest().description);
                this.textRenderer.getWidth(description);

                context.drawText(this.textRenderer,description,x+12,y+46,0xffffff,false);
                context.drawText(this.textRenderer,Text.of((questInstance.time/20)/60+":"+ (questInstance.time/20)%60 ),x+12,y+58,0xffffff,false);


                context.drawText(this.textRenderer,Text.translatable("quest_info_screen.quest_reward"),x+12,progressY+8,0xffffff,false);

                int i = x+12;
                for(ItemStack itemStack: questInstance.getRewards()){
                    context.drawItemWithoutEntity(itemStack,i,progressY+18);
                    context.drawItemInSlot(this.textRenderer,itemStack,i,progressY+18);

                    i += 18;
                }

                context.disableScissor();
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(super.keyPressed(keyCode,scanCode,modifiers)){
            return true;
        }else if(this.client.options.inventoryKey.matchesKey(keyCode,scanCode) || MuffinsQuestLibClient.QUEST_INFO_BTN.matchesKey(keyCode,scanCode)){
            this.close();
            return true;
        }
        return true;
    }

    public static class QuestDataButtonWidget extends PressableWidget {
        int current;
        int maxProgress;

        public QuestDataButtonWidget(int i, int j, QuestData questData,int current,int maxProgress) {
            super(i, j, 160, 19, Text.translatable("quest_info_screen.progress", Text.translatable(questData.toString()).getString() ,current,maxProgress));
            this.active = false;
            this.current = current;
            this.maxProgress = maxProgress;
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            context.drawTexture(QUEST_SCREEN_TEXTURE,this.getX(),this.getY(),0,166,this.getWidth(),this.getHeight());
            //int i = this.active ? 16777215 : 10526880;
            int i = this.current >= this.maxProgress ? Formatting.GREEN.getColorValue() : Formatting.DARK_RED.getColorValue();
            context.drawText(minecraftClient.textRenderer,this.getMessage().asOrderedText(), getX() + 24,getY()+6,i,false );


        }

        @Override
        public void onPress() {

        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
            this.appendNarrations(builder);
        }
    }

    private static class QuestCancelButton extends ButtonWidget {
        protected QuestCancelButton(int x, int y, PressAction onPress) {
            super(x, y, 93, 16, Text.translatable("quest_info_screen.cancel_quest"), onPress, DEFAULT_NARRATION_SUPPLIER);
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            int u = 0;
            int v = this.active ? this.hovered ? 211 : 195 : 227;
            int textColor = this.active ? this.hovered ? 0xffffff : 0x0b0b0b : 0x5d2c2c;

            context.drawTexture(QuestInfoScreen.QUEST_SCREEN_TEXTURE,this.getX(),this.getY(),u,v,this.getWidth(),this.getHeight());
            context.drawText(MinecraftClient.getInstance().textRenderer, this.getMessage(),this.getX()+22,this.getY()+4,textColor,false);
        }
    }
}
