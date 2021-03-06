package by.thmihnea.entity;

import by.thmihnea.TowerDefense;
import by.thmihnea.Util;
import by.thmihnea.arena.Arena;
import com.google.common.base.Predicate;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.List;

public class Skeleton extends EntitySkeleton {

    public Skeleton(World world) {
        super(((CraftWorld)world).getHandle());
        Arena arena = TowerDefense.getInstance().getArenaHandler().getArenaByWorld(this.world.getWorld().getName());
        if (arena == null) return;
        List goalB = (List) Util.getPrivateField("b", PathfinderGoalSelector.class, goalSelector); goalB.clear();
        List goalC = (List) Util.getPrivateField("c", PathfinderGoalSelector.class, goalSelector); goalC.clear();
        List targetB = (List) Util.getPrivateField("b", PathfinderGoalSelector.class, targetSelector); targetB.clear();
        List targetC = (List) Util.getPrivateField("c", PathfinderGoalSelector.class, targetSelector); targetC.clear();

        Predicate<EntityHuman> doAttack = (p) -> (arena.isAttacker((Player) p.getBukkitEntity()));

        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0D, 20, 20, 10.0F));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, 1.0D, false));
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityPlayer.class, 0, true, true, doAttack));

    }

    protected void initAttributes() {
        super.initAttributes();

        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(TowerDefense.cfg.getDouble("mobs.skeletons.hp"));
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(TowerDefense.cfg.getDouble("mobs.skeletons.ms"));
        this.setCustomName("§6Undead Archer");
        this.setCustomNameVisible(true);
    }

    public static org.bukkit.entity.Skeleton spawn(Location location) {
        WorldServer ws = ((CraftWorld)location.getWorld()).getHandle();
        final Skeleton skeleton = new Skeleton(ws.getWorld());

        skeleton.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        skeleton.setEquipment(0, new ItemStack(Items.BOW));
        skeleton.setEquipment(4, new ItemStack(Items.GOLDEN_HELMET));
        ((CraftLivingEntity) skeleton.getBukkitEntity()).setRemoveWhenFarAway(false);
        ws.addEntity(skeleton, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return (org.bukkit.entity.Skeleton) skeleton.getBukkitEntity();
    }
}
