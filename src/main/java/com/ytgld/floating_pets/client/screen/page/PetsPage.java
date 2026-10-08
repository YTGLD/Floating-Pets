package com.ytgld.floating_pets.client.screen.page;

import com.ytgld.floating_pets.FloatingPets;
import com.ytgld.floating_pets.client.Light;
import com.ytgld.floating_pets.client.screen.FloatingPetsScreen;
import com.ytgld.floating_pets.client.screen.tool.AddBookPage;
import com.ytgld.floating_pets.client.screen.tool.RegisterBookPage;
import com.ytgld.floating_pets.items.InitItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec2;

import java.util.List;

@AddBookPage
public class PetsPage implements RegisterBookPage {
    private final int posOffset = 36;
    private final int color = Light.ARGB.color(255,255,240,180);

    private final float d90 = (float) Math.PI / 2 * 1;
    private final float d180 = (float) Math.PI/ 2* 2;
    private final float d270 = (float) Math.PI / 2* 3;
    private final float d360 = (float) Math.PI / 2 * 4;

    private Component clones(){
        return Component.translatable("floating_pets.book.clones");
    }

    @Override
    public void addPage(List<FloatingPetsScreen.FloatingPetsPage> list) {
        list.add(new FloatingPetsScreen.FloatingPetsPage(Identifier.fromNamespaceAndPath(FloatingPets.MODID,
                "textures/components/back.png"),
                new Vec2(0,0), Component.translatable("floating_pets.book.tip.1"),
                List.of(
                        Component.translatable("floating_pets.book.tip.2"),
                        clones()
                ),
                color,color,
                FloatingPetsScreen.ThePage.BASE,
                new FloatingPetsScreen.ArrowDegree(d90)));
        addIron(list);
        addChaosCube(list);
        addMeat(list);

    }
    private void addMeat(List<FloatingPetsScreen.FloatingPetsPage> list) {
        list.add(new FloatingPetsScreen.FloatingPetsPage(InitItems.BloodMeat_.asItem(),
                new Vec2(posOffset * 3,posOffset * 3),Component.translatable("floating_pets.book.blood_meat.1"),
                List.of(
                        Component.translatable("floating_pets.book.blood_meat.2"),
                        clones()
                ),
                color,color,
                FloatingPetsScreen.ThePage.BASE,
                new FloatingPetsScreen.ArrowDegree(d270)));


        list.add(new FloatingPetsScreen.FloatingPetsPage(Identifier.fromNamespaceAndPath(FloatingPets.MODID,
                "textures/components/furious.png"),
                new Vec2(posOffset * 2,posOffset * 3),Component.translatable("floating_pets.component.furious.name"),
                List.of(
                        Component.translatable("floating_pets.component.furious.tip.1"),
                        Component.translatable("floating_pets.component.furious.tip.2"),
                        Component.translatable("floating_pets.component.furious.tip.give"),
                        clones()
                ),
                color,color,
                FloatingPetsScreen.ThePage.BASE,
                null));

    }
    private void addChaosCube(List<FloatingPetsScreen.FloatingPetsPage> list) {

        list.add(new FloatingPetsScreen.FloatingPetsPage(InitItems.YellowCube_.asItem(),
                new Vec2(posOffset * 2,posOffset),Component.translatable("floating_pets.book.yellow_cube.1"),
                List.of(
                        Component.translatable("floating_pets.book.yellow_cube.2"),
                        clones()
                ),
                color,color,
                FloatingPetsScreen.ThePage.BASE,
                new FloatingPetsScreen.ArrowDegree(d90)));


        list.add(new FloatingPetsScreen.FloatingPetsPage(Identifier.fromNamespaceAndPath(FloatingPets.MODID,
                "textures/components/support.png"),
                new Vec2(posOffset * 3,posOffset),Component.translatable("floating_pets.component.support.name"),
                List.of(
                        Component.translatable("floating_pets.component.support.tip.1"),
                        Component.translatable("floating_pets.component.support.tip.2"),
                        Component.translatable("floating_pets.component.support.tip.give"),
                        clones()
                ),
                color,color,
                FloatingPetsScreen.ThePage.BASE,
                new FloatingPetsScreen.ArrowDegree(d180)));

        list.add(new FloatingPetsScreen.FloatingPetsPage(Identifier.fromNamespaceAndPath(FloatingPets.MODID,
                "textures/components/symbiotic_meatballs.png"),
                new Vec2(posOffset * 3,posOffset * 2),Component.translatable("floating_pets.component.symbiotic_meatballs.name"),
                List.of(
                        Component.translatable("floating_pets.component.symbiotic_meatballs.tip.1"),
                        Component.translatable("floating_pets.component.symbiotic_meatballs.tip.2"),
                        Component.translatable("floating_pets.component.symbiotic_meatballs.tip.give"),
                        clones()
                ),
                color,color,
                FloatingPetsScreen.ThePage.BASE,
                new FloatingPetsScreen.ArrowDegree(d90)));

        list.add(new FloatingPetsScreen.FloatingPetsPage(Identifier.fromNamespaceAndPath(FloatingPets.MODID,
                "textures/components/pill.png"),
                new Vec2(posOffset * 4,posOffset * 2),Component.translatable("floating_pets.component.pill.name"),
                List.of(
                        Component.translatable("floating_pets.component.pill.tip.1"),
                        Component.translatable("floating_pets.component.pill.tip.2"),
                        Component.translatable("floating_pets.component.pill.tip.3"),
                        Component.translatable("floating_pets.component.pill.tip.give"),
                        clones()
                ),
                color,color,
                FloatingPetsScreen.ThePage.BASE,
                null));
    }
    private void addIron(List<FloatingPetsScreen.FloatingPetsPage> list){

        list.add(new FloatingPetsScreen.FloatingPetsPage(InitItems.Agreement_.asItem(),
                new Vec2(posOffset,0),Component.translatable("floating_pets.book.agreement.1"),
                List.of(
                        Component.translatable("floating_pets.book.agreement.2"),
                        clones()
                ),
                color,color,
                FloatingPetsScreen.ThePage.BASE,
                new FloatingPetsScreen.ArrowDegree(d180)));

        list.add(new FloatingPetsScreen.FloatingPetsPage(Identifier.fromNamespaceAndPath(FloatingPets.MODID,
                "textures/components/factory.png"),
                new Vec2(posOffset,posOffset),Component.translatable("floating_pets.component.factory.name"),
                List.of(
                        Component.translatable("floating_pets.component.factory.tip.1"),
                        Component.translatable("floating_pets.component.factory.tip.2"),
                        Component.translatable("floating_pets.component.factory.tip.give"),
                        clones()
                ),
                color,color,
                FloatingPetsScreen.ThePage.BASE,
                new FloatingPetsScreen.ArrowDegree(d270)));

        list.add(new FloatingPetsScreen.FloatingPetsPage(Identifier.fromNamespaceAndPath(FloatingPets.MODID,
                "textures/components/detonator.png"),
                new Vec2(0,posOffset),Component.translatable("floating_pets.component.detonator.name"),
                List.of(
                        Component.translatable("floating_pets.component.detonator.tip.1"),
                        Component.translatable("floating_pets.component.detonator.tip.2"),
                        Component.translatable("floating_pets.component.detonator.tip.3"),
                        Component.translatable("floating_pets.component.detonator.tip.give"),
                        clones()
                ),
                color,color,
                FloatingPetsScreen.ThePage.BASE,
                null));
    }
}
