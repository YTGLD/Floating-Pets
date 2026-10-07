package com.ytgld.floating_pets.items.component;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public abstract class PetComponentBase {
    public abstract Identifier id();
    public abstract Identifier image();
    public AttHolderModify attHolderModify(){
        return new AttHolderModify(new HashMap<>());
    }

    public void tick(Player player, ItemStack stack){

    }
    public void text(ItemStack stack, List<Component> tooltipComponents, TooltipFlag flag){
    }
    protected static Identifier theMixinImage(Identifier identifier){
        return Identifier.fromNamespaceAndPath(identifier.getNamespace(),"textures/components/" + identifier.getPath() + ".png");
    }

    public record AttHolderModify(HashMap<Holder<Attribute> , AttributeModifier> multimap){}

}
