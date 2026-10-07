package com.ytgld.floating_pets.entity;

import com.ytgld.floating_pets.Handler;
import com.ytgld.floating_pets.inventory.PetsInventory;
import com.ytgld.floating_pets.items.component.IPetComponent;
import com.ytgld.floating_pets.items.component.PetComponentBase;
import com.ytgld.floating_pets.other.DataReg;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class PetTamableAnimal extends TamableAnimal {
    protected PetTamableAnimal(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }
    @Override
    public void tick() {
        this.setNoGravity(true);
        if (isMove()) {
            this.setPos(
                    this.getX() + this.getDeltaMovement().x,
                    this.getY() + this.getDeltaMovement().y,
                    this.getZ() + this.getDeltaMovement().z
            );
        }
        super.tick();
        if (this.getOwner() instanceof Player owner) {
            if (owner.isDeadOrDying()) {
                this.discard();
            }
            float offset = (float) Math.sin(this.getId());
            offset = Math.abs(offset);
            offset += 1;
            Vec3 currentPos = this.position();
            float speed = (float) (0.25f * offset - (8 - owner.position().distanceTo(currentPos)) / 10f);
            if (speed < 0) {
                speed = 0;
            }
            double desiredDistance = 2;
            Vec3 targetPos = owner.position().add(0, 3, 1);

            Vec3 forward = owner.getLookAngle();
            Vec3 direction = forward.scale(-1).normalize();

            Vec3 newTargetPos = targetPos.add(direction.scale(desiredDistance));

            this.setDeltaMovement(newTargetPos.subtract(currentPos).normalize().scale(speed));
        }
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
        if (source.is(DamageTypes.IN_WALL)) {
            return true;
        }
        return super.isInvulnerableTo(level, source);
    }
    public boolean isMove(){
        return true;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return this;
    }
    @Override
    public void move(MoverType moverType, Vec3 delta) {
        super.move(moverType, delta);
    }
}
