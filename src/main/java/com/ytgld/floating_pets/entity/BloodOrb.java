package com.ytgld.floating_pets.entity;

import com.ytgld.floating_pets.Handler;
import com.ytgld.floating_pets.inventory.PetsInventory;
import com.ytgld.floating_pets.items.InitItems;
import com.ytgld.floating_pets.items.component.IPetComponent;
import com.ytgld.floating_pets.items.component.PetComponents;
import com.ytgld.floating_pets.items.component.components.blood.Furious;
import com.ytgld.floating_pets.items.items.BloodMeat;
import com.ytgld.floating_pets.other.AttReg;
import com.ytgld.floating_pets.other.DataReg;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BloodOrb extends PetTamableAnimal {


    public final List<Vec3> trailPositions = new ArrayList<>();
    public boolean canLive = true;


    public BloodOrb(EntityType<? extends BloodOrb> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
        this.setNoGravity(true);
    }

    public void dis(){
        Vec3 playerPos = this.position();
        int range = 10;
        List<BloodOrb> imperialHematomas = this.level().getEntitiesOfClass(BloodOrb.class, new AABB(playerPos.x - range, playerPos.y - range, playerPos.z - range, playerPos.x + range, playerPos.y + range, playerPos.z + range));
        for (BloodOrb imperialHematoma : imperialHematomas){
            if (imperialHematoma.getOwner()!= null &&this.getOwner()!=null) {
                if (!imperialHematoma.is(this)){
                    if (imperialHematoma.getOwner().is(this.getOwner())){
                        imperialHematoma.discard();
                        return;
                    }
                }
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.ATTACK_DAMAGE, 5)
                .add(Attributes.MAX_HEALTH, 10)
                .add(AttReg.blood_attack_time, 20)
                .add(Attributes.ARMOR, 4);
    }
    public void hurtaTTACK(){
        int time = (int) (this.getAttributeValue(AttReg.blood_attack_time));

        if (this.getOwner() instanceof Player player && this.getTarget() instanceof LivingEntity living) {
            if (player.position().distanceTo(living.position()) < 45) {
                if (this.tickCount % time ==1) {
                    AttackBlood attackBlood = new AttackBlood(
                            Entitys.AttackBlood_.get(),
                            living.level(),
                            (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE)
                    );
                    attackBlood.setPos(this.position().add(0, 0, 0));
                    attackBlood.setOwner(player);
                    attackBlood.setTarget(living);
                    attackBlood.setFreshEntity(this);
                    this.level().addFreshEntity(attackBlood);
                }
            }
        }
    }


    @Override
    public void tick() {
        super.tick();
        this.getAttributes().addTransientAttributeModifiers(Furious.attributeModifierMultimap(this));
        this.setNoGravity(true);
        if (!(this.getOwner() instanceof Player player)) {
            this.discard();
            return;
        }
        if (this.level().isClientSide()) {
            trailPositions.add(this.position());
            if (trailPositions.size() > 20) {
                trailPositions.removeFirst();
            }
        }
        Vec3 playerPos = this.position().add(0, 0.75, 0);
        int range = 20;
        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(playerPos.x - range, playerPos.y - range, playerPos.z - range, playerPos.x + range, playerPos.y + range, playerPos.z + range));
        for (LivingEntity entity : entities){
            if (entity instanceof Targeting targeting && targeting.getTarget() != null) {
                if (targeting.getTarget().is(player) && !entity.is(player)) {
                    setTarget(entity);
                }
            }
        }
        if (this.getTarget() != null) {
            if (this.getTarget().position().distanceTo(this.position()) > 30) {
                setTarget(null);
            }
        }


        clear();
        hurtaTTACK();
        dis();
    }


    private void clear(){
        if (canLive) {
            if (this.getOwner() != null && this.getOwner() instanceof Player player) {
                PetsInventory petsInventory = Handler.getItem(player);
                if (petsInventory != null) {
                    if (!player.level().isClientSide()) {
                        for (int i = 0; i < petsInventory.getContainerSize(); i++) {
                            ItemStack stack = petsInventory.getItem(i);
                            if (stack.is(InitItems.BloodMeat_)) {
                                canLive = true;
                                CompoundTag compoundTag = stack.get(DataReg.tag);
                                if (compoundTag != null) {
                                    if (!compoundTag.getBooleanOr(BloodMeat.BLOOD_MEAT, false)) {
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
    @Override
    public boolean isFood(ItemStack pStack) {
        return false;
    }
    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.RESPAWN_ANCHOR_CHARGE;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (this.getOwner() instanceof Player player && IPetComponent.isHasComponent(player,PetComponents.furious.get())) {
            for (int i = 0; i < 8; i++) {
                AttackBlood attackBlood = new AttackBlood(Entitys.AttackBlood_.get(), level(), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                attackBlood.setPos(this.position());
                attackBlood.setOwner(player);
                attackBlood.setTarget(this.getTarget());
                attackBlood.setDeltaMovement(new Vec3(Math.cos(i) / 10f, 0, Math.sin(i) / 10f));
                attackBlood.setFreshEntity(this);
                this.level().addFreshEntity(attackBlood);
            }
        }

    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RESPAWN_ANCHOR_SET_SPAWN;
    }
    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
       return null;
    }
}

