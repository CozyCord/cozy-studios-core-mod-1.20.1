package net.cozystudios.cozystudioscore.mixin;

import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.PathAwareEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.cozystudios.cozystudioscore.entity.ai.LanternAvoidGoal;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {

    @Inject(method = "initGoals", at = @At("TAIL"))
    private void addLanternAvoidGoal(CallbackInfo ci) {
        MobEntity self = (MobEntity) (Object) this;
        if (self instanceof Monster && self instanceof PathAwareEntity pathMob) {
            GoalSelector selector = ((MobEntityAccessor) self).getGoalSelector();
            selector.add(9, new LanternAvoidGoal(pathMob, 1.0, 25));
        }
    }
}