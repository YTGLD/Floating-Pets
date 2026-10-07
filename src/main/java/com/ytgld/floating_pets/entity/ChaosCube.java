package com.ytgld.floating_pets.entity;

import com.ytgld.floating_pets.Handler;
import com.ytgld.floating_pets.client.Light;
import com.ytgld.floating_pets.inventory.PetsInventory;
import com.ytgld.floating_pets.items.InitItems;
import com.ytgld.floating_pets.items.component.IPetComponent;
import com.ytgld.floating_pets.items.component.PetComponents;
import com.ytgld.floating_pets.items.items.YellowCube;
import com.ytgld.floating_pets.other.DataReg;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChaosCube extends PetTamableAnimal {
    public final List<Vec3> trailPositions = new ArrayList<>();
    public float xAxis,yAxis,zAxis;
    public static final int maxLast = 25;
    public int color = Light.ARGB.color(0,0,0,100);

    public float rotateFloat =  0;
    public float arrowAxis =  0;
    public float oldArrowAxis =  0;

    public int cooldown = 0;
    public final int cooldownTime = 100;

    @Override
    public void tick() {
        super.tick();
        if (getOwner() instanceof Player player) {
            if (IPetComponent.isHasComponent(player, PetComponents.support.get())) {
                int range = 8;
                Vec3 playerPos = this.position();
                List<LivingEntity> entities = this.level().getEntitiesOfClass(
                        LivingEntity.class,
                        new AABB(
                                playerPos.x - range, playerPos.y - range, playerPos.z - range,
                                playerPos.x + range, playerPos.y + range, playerPos.z + range
                        )
                );
                for (LivingEntity living : entities){
                    if (living.is(player) && player.tickCount % 10 == 1) {
                        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,100,0,false,false));
                    }
                }
            }
            this.setNoGravity(true);
            xAxis += 0.1f;
            yAxis += 0.1f;
            zAxis += 0.1f;
            if (rotateFloat < 20) {
                rotateFloat += 0.5f;
            }
            oldArrowAxis = arrowAxis;
            arrowAxis += rotateFloat / 15f;

            trailPositions.add(this.position());
            if (trailPositions.size() > maxLast) {
                trailPositions.removeFirst();
            }
            doHeal(player);
        }
        clear();
        dis();
    }
    public void doHeal(Player player){
        if (player.level().isClientSide()){
            return;
        }
        boolean sm = IPetComponent.isHasComponent(player, PetComponents.symbiotic_meatballs.get());
        int range = 8;
        if (sm) {
            range *= 2;
        }
        Vec3 pos = this.position();
        List<LivingEntity> entities = this.level().getEntitiesOfClass(
                LivingEntity.class,
                new AABB(
                        pos.x - range, pos.y - range, pos.z - range,
                        pos.x + range, pos.y + range, pos.z + range
                )
        );
        if (cooldown > 0) {
            cooldown--;
        }
        int c = cooldownTime;
        if (IPetComponent.isHasComponent(player, PetComponents.support.get())) {
            c /= 2;
        }
        if (cooldown <= 0) {
            for (LivingEntity living : entities) {
                if (sm && living instanceof OwnableEntity ownableEntity) {
                    if (ownableEntity.getOwner() instanceof Player player1 && player.is(player1)) {
                        float heal = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
                        living.heal(heal);
                    }
                }

                if (living.is(player)) {
                    float heal = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    player.heal(heal);
                }
                cooldown = c;
            }
        }
    }

    public ChaosCube(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }
    public boolean canLive = true;
    private void clear(){
        if (canLive) {
            if (this.getOwner() != null && this.getOwner() instanceof Player player) {
                PetsInventory chestInventory = Handler.getItem(player);
                if (chestInventory != null) {
                    if (!player.level().isClientSide()) {
                        for (int i = 0; i < chestInventory.getContainerSize(); i++) {
                            ItemStack stack = chestInventory.getItem(i);
                            if (stack.is(InitItems.YellowCube_)) {
                                canLive = true;
                                CompoundTag compoundTag = stack.get(DataReg.tag);
                                if (compoundTag != null) {
                                    if (!compoundTag.getBooleanOr(YellowCube.YELLOW_CUBE, false)) {
                                        canLive = false;
                                    }
                                }
                                return;
                            }else {
                                canLive = false;
                            }

                        }
                    }
                }
            }
        }
        if (!canLive){
            this.discard();
        }
    }

    public void dis(){
        Vec3 playerPos = this.position();
        int range = 10;
        List<ChaosCube> list = this.level().getEntitiesOfClass(ChaosCube.class, new AABB(playerPos.x - range, playerPos.y - range, playerPos.z - range, playerPos.x + range, playerPos.y + range, playerPos.z + range));
        for (ChaosCube chaosCube : list){
            if (chaosCube.getOwner()!= null &&this.getOwner()!=null) {
                if (!chaosCube.is(this)){
                    if (chaosCube.getOwner().is(this.getOwner())){
                        chaosCube.discard();
                        return;
                    }
                }
            }
        }
    }
    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }
    @Override
    public boolean isInWater() {
        return false;
    }
    @Override
    public boolean onGround() {
        return false;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.MAGMA_CUBE_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.MAGMA_CUBE_DEATH;
    }
    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, (double)0.23F)
                .add(Attributes.ATTACK_DAMAGE, (double)3.0F)
                .add(Attributes.MAX_HEALTH, (double)50.0f)
                .add(Attributes.ATTACK_DAMAGE, (double)5)
                .add(Attributes.ARMOR, (double)2.0F)
                .add(Attributes.BOUNCINESS,1);


    }
    public record Vec3Last(Vec3 target , Vec3 me){}
}
