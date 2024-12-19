package net.firemuffin303.muffinsquestlib.mixin.questEntity;

import com.mojang.logging.LogUtils;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.QuestEntityData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
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

@Debug(export = true)
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @Shadow @Final protected EntityRenderDispatcher dispatcher;

    @Unique private final Identifier QUEST_ICON = new Identifier(MuffinsQuestLib.MOD_ID,"textures/item/quest_paper.png");

    @Inject(method = "render", at = @At(value = "HEAD"))
    public void questLib$showQuestIcon(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci){
        if(entity instanceof QuestEntityData.QuestEntityDataAccessor accessor){
            if(accessor.getQuestEntityData().isQuestMarked()){
                showQuestIcon(entity,matrices,vertexConsumers,light);
            }
        }
    }

    @Unique
    private void showQuestIcon(T entity,MatrixStack matrices,VertexConsumerProvider vertexConsumers,int light){
        matrices.push();
        matrices.translate(0f,entity.getNameLabelHeight() + 0.15f,0f);
        matrices.multiply(this.dispatcher.getRotation());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
        matrices.scale(0.35f,0.35f,1f);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getItemEntityTranslucentCull(QUEST_ICON));
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        Matrix3f matrix3f = matrices.peek().getNormalMatrix();
        vertexConsumer.vertex(matrix4f,-1F,-1F,0F).color(1f,1f,1f,1f).texture(0f,1f).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f,1f,1f,1f).next();
        vertexConsumer.vertex(matrix4f,1F,-1F,0F).color(1f,1f,1f,1f).texture(1f,1f).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f,1f,1f,1f).next();
        vertexConsumer.vertex(matrix4f,1F,1F,0F).color(1f,1f,1f,1f).texture(1f,0f).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f,1f,1f,1f).next();
        vertexConsumer.vertex(matrix4f,-1F,1F,0F).color(1f,1f,1f,1f).texture(0f,0f).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f,1f,1f,1f).next();
        matrices.pop();
    }
}
