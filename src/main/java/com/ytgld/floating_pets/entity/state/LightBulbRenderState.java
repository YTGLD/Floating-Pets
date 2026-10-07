package com.ytgld.floating_pets.entity.state;

import com.ytgld.floating_pets.entity.AttackBlood;
import com.ytgld.floating_pets.entity.LightBulb;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class LightBulbRenderState extends EntityRenderState {
    public LightBulb entity;
    public float partialTick;
    public final ItemStackRenderState item;
    public LightBulbRenderState() {
        this.item = new ItemStackRenderState();
    }
}
