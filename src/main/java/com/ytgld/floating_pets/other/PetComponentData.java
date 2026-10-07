package com.ytgld.floating_pets.other;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.HashSet;

public record PetComponentData(HashSet<String> hashSet) {
    public static final Codec<PetComponentData> CODEC =
            RecordCodecBuilder.create((instance) ->
                    instance.group(Codec.STRING.listOf()
                            .xmap(HashSet::new, ArrayList::new)
                                    .fieldOf("component").forGetter((setSoulData) -> setSoulData.hashSet))
                            .apply(instance, PetComponentData::new));
    public PetComponentData add(String string) {
        this.hashSet.add(string);
        return this;
    }
}