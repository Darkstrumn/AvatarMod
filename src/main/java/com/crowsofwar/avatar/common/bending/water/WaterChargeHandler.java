package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.EntityWaterCannon;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;


public class WaterChargeHandler extends TickHandler {

	private static final UUID MOVEMENT_MODIFIER_ID = UUID.fromString
			("dfb6235c-82b6-407e-beaf-a4d8045735a82");


	/**
	 * Gets AbilityData to be used for determining lightning strength. This is normally the
	 * bender's AbilityData, but in the case of redirection, it is the original bender's
	 * AbilityData.
	 */
	@Nullable
	private AbilityData getLightningData(BendingContext ctx) {
        return ctx.getData().getAbilityData("water_cannon");
    }

	@Override
	public boolean tick(BendingContext ctx) {
		AbilityData abilityData = getLightningData(ctx);
		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		int duration = data.getTickHandlerDuration(this);
		double speed = abilityData.getLevel() >= 1 ? 20 : 30;
		float damage = abilityData.getLevel() >= 2 ? 8 : 6;
		float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(duration / 40f);
		applyMovementModifier(entity, MathHelper.clamp(movementMultiplier, 0.1f, 1));
		float size = 1;

		if (world.isRemote) {
			return false;
		}

		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)){
			if (duration >= 40 && duration % 15 == 0 && duration <= 100){
				EntityWaterCannon cannon = new EntityWaterCannon(world);
				if (abilityData == null) {
					return true;
				}
				cannon.setDamage(4);
				size = 0.1F;
				fireCannon(world, entity, damage, speed, size);
				world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_WATER_AMBIENT,
						SoundCategory.PLAYERS, 1, 2);

			}
			if (duration >= 80){
				entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_MODIFIER_ID);
				return  true;
			}

		}
		else if (duration >= 100) {

			if (abilityData == null) {
				return true;
			}
			EntityWaterCannon cannon = new EntityWaterCannon(world);

			if (abilityData.getLevel() == 1){
				cannon.setDamage(11);
				size = 2F;
			}
			if (abilityData.getLevel() == 2){
				cannon.setDamage(12);
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				cannon.setDamage(20);
				size = 2.5F;

			}

			fireCannon(world, entity, damage, speed, size);

			entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_MODIFIER_ID);

			world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_WATER_AMBIENT,
					SoundCategory.PLAYERS, 1, 2);


			return true;

		}


		return false;

	}



	private void fireCannon(World world, EntityLivingBase entity, float damage, double speed,
							float size) {

		EntityWaterCannon cannon = new EntityWaterCannon(world);

		cannon.setOwner(entity);
		cannon.setDamage(10);
		cannon.setSizeMultiplier(size);

		cannon.setPosition(Vector.getEyePos(entity));
		cannon.setEndPos(Vector.getLookRectangular(entity));

		Vector velocity = Vector.getLookRectangular(entity);
		velocity = velocity.normalize().times(speed);
		cannon.setVelocity(velocity);

		world.spawnEntity(cannon);

	}


	private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

		IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes
				.MOVEMENT_SPEED);

		moveSpeed.removeModifier(MOVEMENT_MODIFIER_ID);

		moveSpeed.applyModifier(new AttributeModifier(MOVEMENT_MODIFIER_ID,
				"Water charge modifier", multiplier - 1, 1));

	}



}


