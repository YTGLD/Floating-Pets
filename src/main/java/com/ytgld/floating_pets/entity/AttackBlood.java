package com.ytgld.floating_pets.entity;

import com.ytgld.floating_pets.FloatingPets;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AttackBlood extends ThrowableItemProjectile {
    private LivingEntity target;
    public final List<Vec3> trailPositions = new ArrayList<>();
    public float damages = 4;
    public float addDamgae = 0;
    public float speeds = 2;
    public boolean follow;

    public AttackBlood(EntityType<? extends AttackBlood> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);

    }

    @Override
    public boolean isInWater() {
        return false;
    }

    @Override
    public boolean onGround() {
        return false;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
    }

    public void setTarget(LivingEntity target) {

        this.target = target;
    }

    public List<Vec3> getTrailPositions() {
        return trailPositions;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.ENDER_PEARL;
    }

    @Override
    public @NotNull ItemStack getItem() {
        return Items.ENDER_PEARL.getDefaultInstance();
    }
    @Override
    public float getXRot() {
        return 0;
    }

    @Override
    public void move(MoverType type, Vec3 movement) {

    }

    @Override
    public float getYRot() {
        return 0;
    }
    public int live = 50;

    public boolean canSee = true;

    public void setCanSee(boolean canSee) {
        this.canSee = canSee;
    }

    public void attack(){
        Vec3 playerPos = this.position().add(0, 0.75, 0);
        int range = 2;
        if (canSee) {
            List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(playerPos.x - range, playerPos.y - range, playerPos.z - range, playerPos.x + range, playerPos.y + range, playerPos.z + range));
            for (LivingEntity entity : entities) {
                if (this.getOwner() != null) {
                    if (!entity.is(this.getOwner()) && this.getOwner() instanceof Player player) {
                        Identifier entitys = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
                        if (!entitys.getNamespace().equals(FloatingPets.MODID)) {
                            if (entity.isAlive()) {
                                entity.setInvulnerableTime(0);

                                if (entity instanceof OwnableEntity ownableEntity) {
                                    if (ownableEntity.getOwner() != null) {
                                        if (ownableEntity.getOwner().is(this.getOwner())) {
                                            setCanSee(false);
                                            return;
                                        }
                                    }
                                }
                                float damageDoomsdayJudgment = (float) (damages + addDamgae + player.getMaxHealth() / 10 + player.getAttributeValue(Attributes.ATTACK_DAMAGE) / 10);
                                entity.hurt(this.getOwner().damageSources().playerAttack(player), damageDoomsdayJudgment);
                                if (follow) {
                                    this.level().addParticle(ParticleTypes.SONIC_BOOM, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
                                }
                                setCanSee(false);
                            }else {
                                setCanSee(false);

                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void tick() {
        this.setPos(
                this.getX() + this.getDeltaMovement().x,
                this.getY() + this.getDeltaMovement().y,
                this.getZ() + this.getDeltaMovement().z
        );

        this.tickCount++;



        this.setNoGravity(true);
        this.noPhysics = true;
        this.move(
                MoverType.SELF,
                this.getDeltaMovement()
        );
        if (canSee) {
            if (this.tickCount > 100) {
                if (follow) {
                    this.level().addParticle(ParticleTypes.SONIC_BOOM,this.getX(),this.getY(),this.getZ(),0,0,0);
                }
                setCanSee(false);
            }
        }

        float s = 0.175F;
        if (canSee) {
            if (target != null) {
                Vec3 targetPos = target.position().add(0, 1, 0);
                Vec3 currentPos = this.position();
                Vec3 direction = targetPos.subtract(currentPos).normalize();

                // 获取当前运动方向
                Vec3 currentDirection = this.getDeltaMovement().normalize();

                // 计算目标方向与当前方向之间的夹角
                double angle = Math.acos(currentDirection.dot(direction)) * (180.0 / Math.PI);

                // 如果夹角超过10度，则限制方向
                if (angle > 10) {
                    // 计算旋转后的新方向
                    double angleLimit = Math.toRadians(10); // 将10度转为弧度

                    // 根据正弦法则计算限制后的方向
                    Vec3 limitedDirection = currentDirection.scale(Math.cos(angleLimit)) // 计算缩放因子
                            .add(direction.normalize().scale(Math.sin(angleLimit))); // 根据目标方向进行调整

                    this.setDeltaMovement(limitedDirection.x * (0.125f + s), limitedDirection.y * (0.125f + s), limitedDirection.z * (0.125f + s));
                } else {
                    this.setDeltaMovement(direction.x * (0.125f + s), direction.y * (0.125f + s), direction.z * (0.125f + s));
                }
            }
        }else {
            this.setDeltaMovement(0,0,0);
        }
        if (canSee) {
            trailPositions.add(new Vec3(this.getX(), this.getY(), this.getZ()));
        }
        if (!trailPositions.isEmpty()) {
            if (trailPositions.size() > 16||!canSee) {
                trailPositions.removeFirst();
            }
        }
        if (!canSee) {
            live--;
        }
        if (live<= 0) {
            this.discard();
        }
        this.setNoGravity(true);


        attack();
    }
}
