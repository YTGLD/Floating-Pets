package com.ytgld.floating_pets.mixin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.ytgld.floating_pets.Handler;
import com.ytgld.floating_pets.inventory.IPlayer;
import com.ytgld.floating_pets.inventory.PetsInventory;
import com.ytgld.floating_pets.items.ItemFloatingPets;
import com.ytgld.floating_pets.items.component.IPetComponent;
import com.ytgld.floating_pets.items.component.PetComponentBase;
import net.minecraft.core.Holder;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(Player.class)
public abstract class PlayerMixin implements IPlayer {
    @Shadow
    @Final
    private Inventory inventory;
    @Unique
    protected final AtomicReference<PetsInventory> attributeType$PetsInventory = new AtomicReference<>(new PetsInventory((Player) (Object) this));

    @Inject(method = "readAdditionalSaveData", at = @At(value = "RETURN"))
    private void readAdditionalSaveData(ValueInput p_422427_, CallbackInfo ci) {
        this.attributeType$PetsInventory.get().fromSlots(p_422427_.listOrEmpty("ChestItems", ItemStackWithSlot.CODEC));

    }
    @Inject(method = "addAdditionalSaveData", at = @At(value = "RETURN"))
    private void addAdditionalSaveData(ValueOutput p_421801_, CallbackInfo ci) {
        this.attributeType$PetsInventory.get().storeAsSlots(p_421801_.list("ChestItems", ItemStackWithSlot.CODEC));
    }
    @Unique
    private final Map<ItemStack, Multimap<Holder<Attribute>, AttributeModifier>> itemStackMultimapMap$FloatingPets = new HashMap<>();

    @Unique
    private void pets$$updateAttribute() {
        Player player = (Player) (Object) this;
        PetsInventory inventory = Handler.getItem(player);
        if (inventory != null) {
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (stack.getItem() instanceof ItemFloatingPets itemFloatingPets) {
                    Multimap<Holder<Attribute>, AttributeModifier> doAttribute = itemFloatingPets.doAttribute(stack, player);

                    if (itemFloatingPets instanceof IPetComponent) {
                        HashSet<PetComponentBase> hashSet =IPetComponent.theComponent(stack);
                        if (hashSet !=null &&!hashSet.isEmpty()) {
                            for (PetComponentBase evilGiftBase : hashSet.stream().toList()) {
                                PetComponentBase.AttHolderModify attHolderModify = evilGiftBase.attHolderModify();
                                for (Holder<Attribute> attributeHolder : attHolderModify.multimap().keySet()) {
                                    AttributeModifier modifier = attHolderModify.multimap().get(attributeHolder);
                                    doAttribute.put(attributeHolder, modifier);
                                }
                            }
                        }
                    }
                    itemStackMultimapMap$FloatingPets.getOrDefault(stack, HashMultimap.create()).forEach((attributeHolder, attributeModifier)->{
                        Multimap<Holder<Attribute>, AttributeModifier> modifiers = HashMultimap.create();
                        modifiers.put(attributeHolder,attributeModifier);
                        player.getAttributes().removeAttributeModifiers(modifiers);
                    });

                    player.getAttributes().addTransientAttributeModifiers(doAttribute);

                    itemStackMultimapMap$FloatingPets.put(stack, doAttribute);
                }
            }
        }
    }

    @Inject(method = "tick", at = @At(value = "RETURN"))
    private void tick(CallbackInfo ci) {
        pets$$updateAttribute();
    }
    @Override
    public void floatingPets$updatePetsInventory(ItemStack itemStack) {
        Player player = (Player) (Object) this;
        Multimap<Holder<Attribute>, AttributeModifier> attributeModifiers = itemStackMultimapMap$FloatingPets.remove(itemStack);
        if (attributeModifiers != null) {
            player.getAttributes().removeAttributeModifiers(attributeModifiers);
        }
    }
    @Override
    public AtomicReference<PetsInventory> getPetsInventory() {
        return attributeType$PetsInventory;
    }
}
