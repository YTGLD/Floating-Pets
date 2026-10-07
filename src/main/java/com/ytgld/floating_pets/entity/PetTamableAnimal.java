package com.ytgld.floating_pets.entity;

import com.ytgld.floating_pets.Handler;
import com.ytgld.floating_pets.inventory.PetsInventory;
import com.ytgld.floating_pets.items.InitItems;
import com.ytgld.floating_pets.items.component.IPetComponent;
import com.ytgld.floating_pets.items.component.PetComponentBase;
import com.ytgld.floating_pets.items.component.PetComponents;
import com.ytgld.floating_pets.items.component.components.Factory;
import com.ytgld.floating_pets.other.DataReg;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class PetTamableAnimal extends TamableAnimal {
    protected PetTamableAnimal(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }
    @Override
    public void tick() {
        this.setPos(
                this.getX() + this.getDeltaMovement().x,
                this.getY() + this.getDeltaMovement().y,
                this.getZ() + this.getDeltaMovement().z
        );
        super.tick();
        if (this.getOwner() instanceof Player owner) {
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
    private void give(Item item , String string, int max, PetComponentBase componentBase){
        if (this.getOwner() instanceof Player player) {
            PetsInventory petsInventory = Handler.getItem(player);
            if (petsInventory != null) {
                for (int i = 0; i < petsInventory.getContainerSize(); i++) {
                    ItemStack stack = petsInventory.getItem(i);
                    if (IPetComponent.isHasComponent(stack, componentBase)) {
                        return;
                    }
                    if (stack.is(item)) {
                        CompoundTag compoundTag = stack.get(DataReg.tag);
                        if (compoundTag == null) {
                            stack.set(DataReg.tag,new CompoundTag());
                        }
                        if (compoundTag != null) {
                            if (compoundTag.getIntOr(string, 0) > max) {
                                IPetComponent.addComponent(stack,componentBase);
                            }
                        }
                    }
                }
            }
        }
    }
    private void addTagToStack(Item item ,String string,PetComponentBase componentBase){
        if (this.getOwner() instanceof Player player) {
            PetsInventory petsInventory = Handler.getItem(player);
            if (petsInventory != null) {
                for (int i = 0; i < petsInventory.getContainerSize(); i++) {
                    ItemStack stack = petsInventory.getItem(i);
                    if (IPetComponent.isHasComponent(stack, componentBase)) {
                        return;
                    }
                    if (stack.is(item)) {
                        CompoundTag compoundTag = stack.get(DataReg.tag);
                        if (compoundTag == null) {
                            stack.set(DataReg.tag,new CompoundTag());
                        }
                        if (compoundTag != null) {
                            compoundTag.putInt(string,compoundTag.getIntOr(string,0) + 1);
                        }
                    }
                }
            }
        }
    }
    public void addNumber(Item item,String s,int max,PetComponentBase componentBase){
        addTagToStack(item ,s, componentBase);
        give(item, s,max, componentBase);
    }

    @Override
    public void move(MoverType moverType, Vec3 delta) {
        super.move(moverType, delta);
    }
}
