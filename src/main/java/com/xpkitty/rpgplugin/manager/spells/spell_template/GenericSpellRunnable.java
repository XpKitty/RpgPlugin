package com.xpkitty.rpgplugin.manager.spells.spell_template;

import com.xpkitty.rpgplugin.Rpg;
import com.xpkitty.rpgplugin.manager.AbilityScores;
import com.xpkitty.rpgplugin.manager.MiscPlayerManager;
import com.xpkitty.rpgplugin.manager.spells.spell_elements.CustomParticle;
import com.xpkitty.rpgplugin.manager.spells.spell_elements.SpellFunction;
import com.xpkitty.rpgplugin.manager.spells.spell_elements.SpellTarget;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class GenericSpellRunnable extends BukkitRunnable {


    Location loc;
    Vector dir;
    Player player;
    Rpg rpg;
    float speed;
    double dist = 0;
    double t = 0;
    int flyDistance;
    SpellFunction function;
    SpellTarget target;

    int spellStrength;

    String val1;
    String val2;
    String val3;

    CustomParticle particle;

    public GenericSpellRunnable(Rpg rpg, Player player, SpellFunction function, float speed, int flyDistance, SpellTarget targets, String val1, String val2, String val3, CustomParticle particle, int spellStrength) {
        this.player = player;
        this.rpg = rpg;
        this.dir = player.getLocation().getDirection().normalize();
        this.loc = player.getLocation();
        this.speed=speed;
        this.target=targets;
        this.flyDistance=flyDistance;
        this.function=function;
        this.val1=val1;
        this.val2=val2;
        this.val3=val3;
        this.spellStrength=spellStrength;

        this.particle=particle;

        if(function.equals(SpellFunction.ENTITY_KNOCKBACK)) {
            player.getWorld().playSound(player.getLocation(),Sound.ENTITY_DRAGON_FIREBALL_EXPLODE,2.0f,2.0f);
        }
    }

    public void start() {
        runTaskTimer(rpg,0,1);
    }


    boolean flipped = false;
    boolean isRun = false;

    @Override
    public void run() {

        // SPEED IS QUARTER-BLOCKS PER TICK

        int speedCount = (int) (speed*4);

        for(int i = 0; i<speedCount; i++) {

            t += 0.25;

            if(!flipped) {
                dist += 0.25;
            } else {
                dist-=0.25;
            }
            dist=t;

            double x = dir.getX() * dist;
            double y = dir.getY() * dist + 1.5;
            double z = dir.getZ() * dist;
            loc.add(x,y,z);

            System.out.println(x + " " + y + " " + z);




            if(loc.getBlock().getType().equals(Material.BARRIER)) {
                flipped = true;
                player.sendMessage("FLIPPING");
            }

            // IF SPELL HITS BLOCK
            if (!loc.getBlock().getType().equals(Material.AIR)) {
                this.cancel();

                //SET FIRE FUNCTION
                if (function.equals(SpellFunction.SET_FIRE)) {

                    Location blockLoc = loc.getBlock().getLocation();
                    blockLoc.setY(blockLoc.getY() + 1);
                    if (blockLoc.getBlock().getType().equals(Material.AIR)) {
                        if (val1.equalsIgnoreCase("true")) {
                            blockLoc.getBlock().setType(Material.SOUL_FIRE);
                        } else {
                            blockLoc.getBlock().setType(Material.FIRE);
                        }
                    }
                }


                //SET WATER FUNCTION
                if (function.equals(SpellFunction.SET_WATER)) {

                    Location blockLoc = loc.getBlock().getLocation();
                    blockLoc.setY(blockLoc.getY() + 1);
                    if (blockLoc.getBlock().getType().equals(Material.AIR)) {
                        blockLoc.getBlock().setType(Material.WATER);
                        blockLoc.getBlock().getState().update();
                    }
                    player.getWorld().playSound(loc, Sound.ENTITY_PLAYER_SPLASH, 5.0f, 1.0f);



                //OPEN DOOR FUNCTION
                } else if(function.equals(SpellFunction.DOOR_OPEN)) {
                    if(loc.getBlock().getType().toString().contains("DOOR")) {
                        BlockState blockState = loc.getBlock().getState();
                        Openable openable = (Openable) blockState.getBlockData();
                        if (!openable.isOpen()) {
                            openable.setOpen(true);
                            blockState.setBlockData(openable);
                            blockState.update();
                        }
                    }


                //CLOSE DOOR FUNCTION
                } else if(function.equals(SpellFunction.DOOR_CLOSE)) {
                    if(loc.getBlock().getType().toString().contains("DOOR")) {
                        BlockData blockState = loc.getBlock().getBlockData();
                        if(blockState instanceof Door) {
                            ((Door) blockState).setOpen(false);
                            loc.getBlock().setBlockData(blockState);
                        }
                    }



                //PLAY FIZZLE SOUND
                } else {
                    if (!function.equals(SpellFunction.ENTITY_KNOCKBACK)) {
                        player.getWorld().playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH, 5.0f, 1.0f);
                    }
                }
                //loc.getWorld().spawnParticle(Particle.FLAME,loc,0,0.2,0,0,5);



            } else if(!loc.getBlock().getType().equals(Material.BARRIER)) {

                Entity e = null;
                HashMap<UUID, Double> entities = new HashMap<>();
                boolean foundEntity = false;

                // IF SPELL HITS ENTITY
                for (Entity en : loc.getChunk().getEntities()) {
                    if (en.getLocation().distance(loc) < 1.75) {
                        if (en instanceof LivingEntity) {
                            foundEntity = true;
                            entities.put(en.getUniqueId(), en.getLocation().distance(loc));
                        }
                    }
                }

                if(foundEntity) {
                    double distance = 10;
                    UUID id = null;
                    for (UUID uuid : entities.keySet()) {
                        if (entities.get(uuid) < distance) {
                            distance = entities.get(uuid);
                            id = uuid;
                        }
                    }


                    for (Entity en : loc.getChunk().getEntities()) {
                        if (en.getUniqueId().equals(id)) {
                            e = en;
                        }
                    }
                }

                if(foundEntity && e instanceof LivingEntity) {

                    // IF ENTITY IS NOT CASTER OR WAS CASTER BUT SPELL BOUNCED OF SHIELD
                    if (!e.equals(player) || t>9) {
                        boolean affectsEntity = false;

                        if(target.getEntityTypes()!=null) {
                            if(target.getEntityTypes().contains(e.getType())) {
                                affectsEntity=true;
                            }
                        }
                        if(target.getAffectsAllLivingEntities()) {
                            affectsEntity=true;
                        }

                        // SPELL EFFECT - TEST IF ENTITY IS TARGET
                        if (affectsEntity && !isRun) {

                            isRun=true;
                            this.cancel();

                            //? MAYBE REMOVE SYS_OUT
                            System.out.println(function.name());

                            //DAMAGE ENTITY FUNCTION
                            if (function.equals(SpellFunction.ENTITY_DAMAGE)) {
                                ((LivingEntity) e).damage(Double.parseDouble(val1));

                            }

                            //ENTITY STUN FUNCTION
                            if (function.equals(SpellFunction.ENTITY_STUN)) {
                                int stunTime = Integer.parseInt(val1);
                                double damage = Double.parseDouble(val3);


                                LivingEntity le = (LivingEntity) e;

                                if (le.getHealth() >= 5) le.damage(damage);

                                if(val2.equals("true")) {
                                    le.setSwimming(true);
                                }

                                BukkitTask task = rpg.getServer().getScheduler().runTaskLater(rpg, () -> {
                                    le.setSwimming(false);
                                }, stunTime);

                                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, stunTime, 4, false, false, true));
                                le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, stunTime, 4, false, false, true));

                            }

                            //ENTITY DISARM FUNCTION
                            if(function.equals(SpellFunction.ENTITY_DISARM)) {

                                LivingEntity target = (LivingEntity) e;
                                int disarmStrengthBase = Integer.parseInt(val1);

                                boolean isKnockBack = false;
                                if(Objects.equals(val2, "true")) {
                                    isKnockBack=true;
                                }

                                boolean disarm = false;
                                Random random = new Random();

                                int targetResist = 5;
                                int roll = random.nextInt(6);
                                int mod = 0;

                                if(target instanceof Player) {
                                    Player targetPlayer = (Player) target;

                                    int STR = MiscPlayerManager.getAbilityScoreModifier(rpg,targetPlayer, AbilityScores.STR);
                                    int CON = MiscPlayerManager.getAbilityScoreModifier(rpg,targetPlayer, AbilityScores.CON);

                                    mod += STR + CON;
                                }

                                player.sendMessage();

                                int targetPower = roll+targetResist+mod;
                                int casterPower = spellStrength+disarmStrengthBase;

                                player.sendMessage( targetPower +" target?caster " + casterPower);

                                if(casterPower>targetPower) {
                                    disarm=true;
                                }


                                player.sendMessage("DISARM: " + disarm);

                                if(target.getEquipment() != null && disarm) {
                                    ItemStack handItem = target.getEquipment().getItemInMainHand();
                                    ItemStack offHandItem = target.getEquipment().getItemInOffHand();

                                    ItemStack item = new ItemStack(Material.AIR);

                                    if(handItem.getType()!=Material.AIR) {
                                        target.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
                                        item = handItem;
                                    } else if(offHandItem.getType()!=Material.AIR) {
                                        target.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
                                        item = offHandItem;
                                    }

                                    if(handItem.getType()!=Material.AIR) {
                                        Item dropItem = target.getWorld().dropItem(target.getLocation().add(0, 1, 0), item);
                                        dropItem.setVelocity(dir);
                                    }
                                }

                                if(isKnockBack) {
                                    target.setVelocity(dir.add(dir));
                                }
                            }


                            //ENTITY KNOCKBACK FUNCTION EG FLIPENDO
                            if(function.equals(SpellFunction.ENTITY_KNOCKBACK)) {

                                dir=player.getLocation().getDirection().normalize();
                                dir.multiply(dir);
                                dir.multiply(Float.parseFloat(val1));
                                dir.multiply(-1);
                                if(val2=="true") {
                                    dir = dir.setY(0.25);
                                }
                                e.setVelocity(dir);
                            }




                            //ENTITY POTION FUNCTION
                            if(function.equals(SpellFunction.ENTITY_POTION_EFFECT)) {
                                String effect = val1;
                                int effectLength = Integer.parseInt(val2);
                                int amplifier = Integer.parseInt(val3);

                                for(PotionEffectType effectType : PotionEffectType.values()) {
                                    if(effectType.getName().equalsIgnoreCase(effect)) {
                                        if(e instanceof LivingEntity) {
                                            ((LivingEntity) e).addPotionEffect(new PotionEffect(effectType,effectLength,amplifier,true,false,true));
                                        }
                                    }
                                }

                            }

                            //SET FIRE FUNCTION
                            if(function.equals(SpellFunction.SET_FIRE)) {

                                LivingEntity livingEntity = (LivingEntity) e;
                                livingEntity.setFireTicks(Integer.parseInt(val2));
                                livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1.0f, 1.0f);
                            }

                            //SET WATER FUNCTION
                            if(function.equals(SpellFunction.SET_WATER)) {

                                LivingEntity livingEntity = (LivingEntity) e;
                                livingEntity.setFireTicks(0);
                                livingEntity.setVisualFire(false);
                                livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 1.0f, 1.0f);
                            }



                            cancel();
                        }
                    }
                }
            }


            //PARTICLE

            if(particle.getCount()>-1) {
                if (particle.getType().equals(Particle.REDSTONE)) {
                    loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 0, 0.2, 0, 0, 5, new Particle.DustOptions(particle.getColor(), particle.getSize()), true);
                    loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 0, 0.2, 0, 0, 5, new Particle.DustOptions(particle.getColor(), particle.getSize()), true);
                } else {
                    loc.getWorld().spawnParticle(particle.getType(), loc, particle.getCount(), 0, 0, 0, 5);
                }
            }

            loc.subtract(x, y, z);

            if (t >= flyDistance) {
                this.cancel();
            }
        }
    }

}
