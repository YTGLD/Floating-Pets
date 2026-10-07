package com.ytgld.floating_pets.items;

import com.ytgld.floating_pets.FloatingPets;
import com.ytgld.floating_pets.items.items.Agreement;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class InitItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FloatingPets.MODID);
    public static final DeferredItem<@NotNull Item> FloatingPetsBook_ = register("book",
            (Identifier)-> new FloatingPetsBook(new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM,Identifier))));
    public static final DeferredItem<@NotNull Item> Agreement_ = register("agreement",
            (Identifier)-> new Agreement(new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM,Identifier))));




    public static DeferredItem<@NotNull Item> register(String name, Function<Identifier, ? extends Item> func) {
        return ITEMS.register(name,func);
    }


    public static final DeferredItem<Item> Reactor_ = register("reactor",
            (Identifier)-> new Item(new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM,Identifier))));
    public static final DeferredItem<Item> ReactorIn_ = register("reactor_in",
            (Identifier)-> new Item(new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM,Identifier))));

}
