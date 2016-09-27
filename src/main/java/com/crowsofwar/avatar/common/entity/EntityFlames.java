package com.crowsofwar.avatar.common.entity;

import java.util.List;

import com.crowsofwar.avatar.common.entityproperty.EntityPropertyMotion;
import com.crowsofwar.avatar.common.entityproperty.IEntityProperty;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityFlames extends AvatarEntity {
	
	private final IEntityProperty<Vector> propVelocity;
	
	/**
	 * The owner, null client side
	 */
	private EntityPlayer owner;
	
	/**
	 * @param worldIn
	 */
	public EntityFlames(World worldIn) {
		super(worldIn);
		this.propVelocity = new EntityPropertyMotion(this);
		setSize(0.1f, 0.1f);
	}
	
	public EntityFlames(World world, EntityPlayer owner) {
		this(world);
		this.owner = owner;
	}
	
	@Override
	protected void entityInit() {
		
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		// TODO Support saving/loading of EntityFlames
		setDead();
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		setDead();
	}
	
	@Override
	public void onUpdate() {
		Vector velocityPerTick = velocity().dividedBy(20);
		moveEntity(velocityPerTick.x(), velocityPerTick.y(), velocityPerTick.z());
		
		velocity().mul(0.94);
		
		if (velocity().sqrMagnitude() <= 0.5 * 0.5 || isCollided) setDead();
		
		Raytrace.Result raytrace = Raytrace.raytrace(worldObj, velocity(), velocity().copy().normalize(), 0.3,
				true);
		if (raytrace.hitSomething()) {
			EnumFacing sideHit = raytrace.getSide();
			velocity().set(velocity().reflect(new Vector(sideHit)).times(0.5));
		}
		
		if (!worldObj.isRemote) {
			List<Entity> collided = worldObj.getEntitiesInAABBexcluding(this, getEntityBoundingBox(),
					entity -> entity != owner && !(entity instanceof EntityFlames));
			
			for (Entity entity : collided) {
				entity.setFire(3);
			}
		}
		
	}
	
	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {}
	
}
