package com.ytgld.floating_pets.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.ytgld.floating_pets.FloatingPets;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class Keys {
    public static KeyMapping.Category key = new KeyMapping.Category(Identifier.fromNamespaceAndPath(FloatingPets.MODID,"pets_key"));


    public static final KeyMapping R =
            (new KeyMapping("key.floating_pets.r", InputConstants.KEY_R, key));
    public static final KeyMapping C =
            (new KeyMapping("key.floating_pets.c", InputConstants.KEY_C, key));



}
