package net.firemuffin303.muffinsquestlib.mixin.questEntity;

import com.mojang.logging.LogUtils;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.QuestEntityData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Debug(export = true)
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @Shadow @Final protected EntityRenderDispatcher dispatcher;

    @Unique private final Identifier QUEST_ICON = new Identifier(MuffinsQuestLib.MOD_ID,"textures/item/quest_paper.png");

    @Inject(method = "render", at = @At(value = "HEAD"))
    public void questLib$showQuestIcon(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci){
        if(entity instanceof QuestEntityData.QuestEntityDataAccessor accessor){
            if(accessor.getQuestEntityData().isQuestMarked()){
                showQuestIcon(entity,matrices,vertexConsumers,light,accessor);
            }
        }
    }

    @Unique
    private void showQuestIcon(T entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, QuestEntityData.QuestEntityDataAccessor accessor){
        matrices.push();
        matrices.translate(0f,entity.getNameLabelHeight() + 0.15f,0f);
        matrices.multiply(this.dispatcher.getRotation());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
        matrices.scale(0.35f,0.35f,1f);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getItemEntityTranslucentCull(QUEST_ICON));
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        Matrix3f matrix3f = matrices.peek().getNormalMatrix();
        vertex(vertexConsumer,matrix4f,-1f,1f,-1,1,0f,matrix3f,0f,1f);
        matrices.pop();

        //Player Icon
        matrices.push();
        matrices.translate(0f,entity.getNameLabelHeight() +0.15f,0f);
        matrices.multiply(this.dispatcher.getRotation());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
        matrices.scale(0.15f,0.15f,1f);
        ClientPlayNetworkHandler clientPlayNetworkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if(clientPlayNetworkHandler != null){
            PlayerListEntry playerListEntry = clientPlayNetworkHandler.getPlayerListEntry(accessor.getQuestEntityData().getPlayerUUID());
            VertexConsumer playerIcon = vertexConsumers.getBuffer(RenderLayer.getItemEntityTranslucentCull(playerListEntry == null ? DefaultSkinHelper.getTexture() : playerListEntry.getSkinTexture()));
            Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
            Matrix3f matrix3f2 = matrices.peek().getNormalMatrix();
            vertex(playerIcon,matrix4f2,0.25f,2.25f,-2.25f,-0.25f,0.01f,matrix3f2,0.125f,0.25f);
        }
        matrices.pop();
    }

    @Unique
    private void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f,float minPosX,float maxPosX,float minPosY,float maxPosY,float z,Matrix3f matrix3f,float minTexture,float maxTexture){
        vertexConsumer.vertex(matrix4f,minPosX,minPosY,z).color(1f,1f,1f,1f).texture(minTexture,maxTexture).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f,1f,1f,1f).next();
        vertexConsumer.vertex(matrix4f,maxPosX,minPosY,z).color(1f,1f,1f,1f).texture(maxTexture,maxTexture).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f,1f,1f,1f).next();
        vertexConsumer.vertex(matrix4f,maxPosX,maxPosY,z).color(1f,1f,1f,1f).texture(maxTexture,minTexture).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f,1f,1f,1f).next();
        vertexConsumer.vertex(matrix4f,minPosX,maxPosY,z).color(1f,1f,1f,1f).texture(minTexture,minTexture).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f,1f,1f,1f).next();
    }


}
