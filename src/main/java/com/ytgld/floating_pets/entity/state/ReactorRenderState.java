package com.ytgld.floating_pets.entity.state;

import com.ytgld.floating_pets.entity.Reactor;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class ReactorRenderState extends EntityRenderState {
    public Reactor entity;
    public final ItemStackRenderState item;
    public final ItemStackRenderState item2;
    public float partialTick;

    public ReactorRenderState() {
        item = new ItemStackRenderState();
        item2 = new ItemStackRenderState();
    }

}
