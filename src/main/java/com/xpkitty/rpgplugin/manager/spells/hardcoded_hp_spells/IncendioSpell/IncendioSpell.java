package com.xpkitty.rpgplugin.manager.spells.hardcoded_hp_spells.IncendioSpell;

import com.xpkitty.rpgplugin.Rpg;
import com.xpkitty.rpgplugin.manager.spells.HpSpell;
import com.xpkitty.rpgplugin.manager.spells.enum_list.SpellList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

public class IncendioSpell extends HpSpell{
    Rpg rpg;
    UUID uuid;
    SpellList type;

    public IncendioSpell(Rpg rpg, SpellList type, UUID uuid, Vector dir) {
        super(rpg, type, uuid, dir);
        this.rpg=rpg;
        this.type=type;
        this.uuid=uuid;
        onStart(Bukkit.getPlayer(uuid),dir);
    }

    @Override
    public void onStart(Player player, Vector dir) {
        IncendioRunnable task = new IncendioRunnable(rpg,player,dir);
        task.start();
    }
}
