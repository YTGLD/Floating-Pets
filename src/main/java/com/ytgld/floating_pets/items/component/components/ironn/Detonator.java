package com.ytgld.floating_pets.items.component.components.ironn;

import com.ytgld.floating_pets.FloatingPets;
import com.ytgld.floating_pets.entity.Reactor;
import com.ytgld.floating_pets.items.InitItems;
import com.ytgld.floating_pets.items.component.IPetComponent;
import com.ytgld.floating_pets.items.component.PetComponentBase;
import com.ytgld.floating_pets.items.component.PetComponents;
import com.ytgld.floating_pets.other.DataReg;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class Detonator extends PetComponentBase {

    public static final String tag = "giveDetonator";

    @Override
    public Identifier id() {
        return Identifier.fromNamespaceAndPath(FloatingPets.MODID,"detonator");
    }

    @Override
    public Identifier image() {
        return PetComponentBase.theMixinImage(id());
    }
    public static boolean give(ItemStack me, ItemStack other, LivingEntity living){
        if (me.is(InitItems.Agreement_.asItem())) {
            if (other.is(Items.TNT)) {
                if (IPetComponent.isHasComponent(me, PetComponents.detonator.get())) {
                    return false;
                }
                if (addTagAndGive(me, 15)) {
                    other.shrink(1);
                    return true;
                }
            }
        }
        return false;
    }
    private static boolean addTagAndGive(ItemStack stack,int max){
        CompoundTag compoundTag = stack.get(DataReg.tag);
        if (compoundTag == null) {
            stack.set(DataReg.tag,new CompoundTag());
        }
        if (compoundTag != null){
            compoundTag.putInt(tag,compoundTag.getIntOr(tag,0) + 1);
            if (compoundTag.getIntOr(tag, 0) > max) {
                IPetComponent.addComponent(stack,PetComponents.detonator.get());
                return false;
            }
        }
        return true;
    }
    public static void use(Player player){
        if (IPetComponent.isHasComponent(player, PetComponents.detonator.get())) {
            Vec3 playerPos = player.position().add(0, 1, 0);
            int range = 10;
            List<Reactor> entities = player.level().getEntitiesOfClass(Reactor.class,
                    new AABB(playerPos.x - range, playerPos.y - range,
                            playerPos.z - range, playerPos.x + range,
                            playerPos.y + range, playerPos.z + range));
            entities.stream()
                    .filter((reactor -> reactor.getOwner() instanceof Player))
                    .distinct()
                    .findFirst().ifPresent((reactor)->{
                        if (reactor.getOwner()!= null && reactor.getOwner().is(player)) {
                            reactor.level().explode(
                                    player,
                                    reactor.getX(),reactor.getY(),reactor.getZ(),
                                    5,
                                    false, Level.ExplosionInteraction.NONE


                            );
                            player.getCooldowns().addCooldown(InitItems.Agreement_.asItem().getDefaultInstance(), 600);
                            reactor.discard();
                        }
                    });
        }
    }

    @Override
    public PetComponentBase.AttHolderModify attHolderModify() {
        PetComponentBase.AttHolderModify attHolderModify = new PetComponentBase.AttHolderModify(new HashMap<>());

        attHolderModify.multimap().put(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE,
                new AttributeModifier(this.id(),
                        1, AttributeModifier.Operation.ADD_VALUE));

        return attHolderModify;
    }
    @Override
    public void text(ItemStack stack, List<Component> tooltipComponents, TooltipFlag flag) {
        super.text(stack, tooltipComponents, flag);
        tooltipComponents.add(Component.translatable("floating_pets.component.detonator.tip.1").withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.translatable("floating_pets.component.detonator.tip.2").withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.translatable("floating_pets.component.detonator.tip.3").withStyle(ChatFormatting.GOLD));
    }
}

