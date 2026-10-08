package com.ytgld.floating_pets.entity;

import com.ytgld.floating_pets.Handler;
import com.ytgld.floating_pets.inventory.PetsInventory;
import com.ytgld.floating_pets.items.InitItems;
import com.ytgld.floating_pets.items.component.IPetComponent;
import com.ytgld.floating_pets.items.component.PetComponents;
import com.ytgld.floating_pets.items.component.components.ironn.Factory;
import com.ytgld.floating_pets.items.items.Agreement;
import com.ytgld.floating_pets.other.DataReg;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;


public class Reactor extends PetTamableAnimal {
    public Reactor(EntityType<? extends Reactor> type, Level level) {
        super(type, level);
    }
    public boolean isInReactor =  false;
    public float rotateFloat =  0;
    public float arrowAxis =  0;
    public float oldArrowAxis =  0;
    @Override
    public void tick() {
        super.tick();
        if (!(getOwner() instanceof Player player)) {
            return;
        }

        int time = 20;
        int range = 8;
        if (IPetComponent.isHasComponent(player, PetComponents.factory.get())) {
            time /= 4;
            range += 4;
        }
        Vec3 playerPos = this.position();
        List<ItemEntity> entities = this.level().getEntitiesOfClass(
                ItemEntity.class,
                new AABB(
                        playerPos.x - range, playerPos.y - range, playerPos.z - range,
                        playerPos.x + range, playerPos.y + range, playerPos.z + range
                )
        );
        isInReactor = !entities.isEmpty();
        if (isInReactor) {
            if (rotateFloat < 20) {
                rotateFloat +=0.5f;
            }
        }else {
            if (rotateFloat > 0) {
                rotateFloat -= 0.5f;
            }
        }
        oldArrowAxis = arrowAxis;
        arrowAxis += rotateFloat / 10f;

        if (time < 1) {
            time = 1;
        }
        for (ItemEntity entity : entities) {
            if (entity.tickCount > 10) {
                if (entity.tickCount % time != 1) {
                    continue;
                }

                ItemStack stack = entity.getItem();

                if (stack.isEmpty()) {
                    continue;
                }

                if (!(this.level() instanceof ServerLevel level)) {
                    continue;
                }
                SingleRecipeInput input = new SingleRecipeInput(stack);

                Optional<RecipeHolder<SmeltingRecipe>> recipe = level.recipeAccess()
                        .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level);

                if (recipe.isEmpty()) {
                    continue;
                }
                ItemStack result = recipe.get().value().assemble(input);
                if (result.isEmpty() || result.is(stack.getItem())) {
                    continue;
                } else {
                    if (tickCount % 20 == 1) {
                        level.playSound(null, this.blockPosition(), SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS, 1, 1);
                    }
                }
                stack.shrink(1);
                ItemEntity resultEntity = new ItemEntity(
                        level,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        result.copy()
                );
                ExperienceOrb experienceOrb = new ExperienceOrb(level, resultEntity.getX(), resultEntity.getY(), resultEntity.getZ(),
                        (int) (recipe.get().value().experience() + 1) * 3);
                level.addFreshEntity(experienceOrb);

                resultEntity.setDeltaMovement(entity.getDeltaMovement());
                level.playSound(null, resultEntity.blockPosition(), SoundEvents.LAVA_POP, SoundSource.BLOCKS, 1, 1);
                level.addFreshEntity(resultEntity);

                IPetComponent.addNumber(player,InitItems.Agreement_.asItem(),Factory.tag,640,PetComponents.factory.get());
                if (stack.isEmpty()) {
                    entity.discard();
                }
            }
        }
        dis();
        clear();
    }
    @Override
    public void move(MoverType type, Vec3 movement) {
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
                            if (stack.is(InitItems.Agreement_)) {
                                canLive = true;
                                CompoundTag compoundTag = stack.get(DataReg.tag);
                                if (compoundTag != null) {
                                    if (!compoundTag.getBooleanOr(Agreement.chestHasReactor, false)) {
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
        List<Reactor> list = this.level().getEntitiesOfClass(Reactor.class, new AABB(playerPos.x - range, playerPos.y - range, playerPos.z - range, playerPos.x + range, playerPos.y + range, playerPos.z + range));
        for (Reactor reactor : list){
            if (reactor.getOwner()!= null &&this.getOwner()!=null) {
                if (!reactor.is(this)){
                    if (reactor.getOwner().is(this.getOwner())){
                        reactor.discard();
                        return;
                    }
                }
            }
        }
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
        return SoundEvents.IRON_GOLEM_REPAIR;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.BEACON_DEACTIVATE;
    }
    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }
    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return this;
    }
}
