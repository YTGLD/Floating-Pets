package com.ytgld.floating_pets.items.component.components;

import com.ytgld.floating_pets.FloatingPets;
import com.ytgld.floating_pets.entity.ChaosCube;
import com.ytgld.floating_pets.items.InitItems;
import com.ytgld.floating_pets.items.component.IPetComponent;
import com.ytgld.floating_pets.items.component.PetComponentBase;
import com.ytgld.floating_pets.items.component.PetComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.HashMap;
import java.util.List;

public class Pill extends PetComponentBase{
    public static boolean give(ItemStack me, ItemStack other){
        if (me.is(InitItems.YellowCube_.asItem())) {
            if (other.is(Items.NETHER_STAR)) {
                if (IPetComponent.addComponent(me, PetComponents.pill.get())) {
                    other.shrink(1);
                }
                return true;
            }
        }
        return false;
    }

    public static void event(LivingDamageEvent.Post event){
        if (event.getEntity() instanceof Player player) {
            if (IPetComponent.isHasComponent(player,PetComponents.pill.get())){
                float damage =  event.getHealthDamage();
                if (damage > 8) {
                    Vec3 playerPos = player.position().add(0, 1, 0);
                    int range = 10;
                    List<ChaosCube> entities = player.level().getEntitiesOfClass(ChaosCube.class,
                            new AABB(playerPos.x - range, playerPos.y - range,
                                    playerPos.z - range, playerPos.x + range,
                                    playerPos.y + range, playerPos.z + range));
                    entities.stream()
                            .filter((cube -> cube.getOwner() instanceof Player))
                            .distinct()
                            .findFirst().ifPresent((cube)->{
                                if (cube.getOwner()!= null && cube.getOwner().is(player)) {
                                    cube.hurt(cube.damageSources().genericKill(),damage);
                                    player.getCooldowns().addCooldown(InitItems.YellowCube_.asItem().getDefaultInstance(),100);
                                    if (!player.level().isClientSide()) {
                                        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,200,0));
                                        player.addEffect(new MobEffectInstance(MobEffects.SPEED,200,0));
                                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,200,0));
                                    }
                                }
                            });
                }
            }
        }
    }
    @Override
    public Identifier id() {
        return Identifier.fromNamespaceAndPath(FloatingPets.MODID,"pill");
    }

    @Override
    public Identifier image() {
        return PetComponentBase.theMixinImage(id());
    }

    @Override
    public PetComponentBase.AttHolderModify attHolderModify() {
        PetComponentBase.AttHolderModify attHolderModify = new PetComponentBase.AttHolderModify(new HashMap<>());

        attHolderModify.multimap().put(Attributes.MAX_HEALTH,
                new AttributeModifier(this.id(),
                        4, AttributeModifier.Operation.ADD_VALUE));

        return attHolderModify;
    }
    @Override
    public void text(ItemStack stack, List<Component> tooltipComponents, TooltipFlag flag) {
        super.text(stack, tooltipComponents, flag);
        tooltipComponents.add(Component.translatable("floating_pets.component.pill.tip.1").withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.translatable("floating_pets.component.pill.tip.2").withStyle(ChatFormatting.GOLD));
    }
}
