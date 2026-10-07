package com.ytgld.floating_pets.client.screen.page;

import com.ytgld.floating_pets.client.screen.FloatingPetsScreen;
import com.ytgld.floating_pets.client.screen.tool.AddBookPage;
import com.ytgld.floating_pets.client.screen.tool.RegisterBookPage;
import com.ytgld.floating_pets.items.InitItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec2;

import java.util.List;

@AddBookPage
public class PetsPage implements RegisterBookPage {
    private final int posOffset = 36;

    private final float d90 = (float) Math.PI / 2 * 1;
    private final float d180 = (float) Math.PI/ 2* 2;
    private final float d270 = (float) Math.PI / 2* 3;
    private final float d360 = (float) Math.PI / 2 * 4;
    @Override
    public void addPage(List<FloatingPetsScreen.FloatingPetsPage> list) {
        list.add(new FloatingPetsScreen.FloatingPetsPage(InitItems.FloatingPetsBook_.asItem(),new Vec2(0,0),Component.literal("11111111111"),
                List.of(Component.translatable("2222222222")),0xffffffff,0xffffffff,
                FloatingPetsScreen.ThePage.BASE,
                new FloatingPetsScreen.ArrowDegree(d90)));

        list.add(new FloatingPetsScreen.FloatingPetsPage(Items.GRASS_BLOCK.asItem(),new Vec2(posOffset,0),Component.literal("11111111111"),
                List.of(Component.translatable("2222222222")),0xffffffff,0xffffffff,
                FloatingPetsScreen.ThePage.BASE,
                new FloatingPetsScreen.ArrowDegree(d180)));
    }
}
