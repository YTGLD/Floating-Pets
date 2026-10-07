package com.ytgld.floating_pets.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;

public class LightBulb extends PetTamableAnimal{
    public float rotateFloat =  0;
    public float arrowAxis =  0;
    public float oldArrowAxis =  0;
    protected LightBulb(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        oldArrowAxis = arrowAxis;
        arrowAxis = tickCount / 10f;

    }
}
