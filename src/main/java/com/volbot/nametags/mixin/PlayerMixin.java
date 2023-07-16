package com.volbot.nametags.mixin;

import java.util.List;
import java.util.Optional;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity {

	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "getName", at = @At(value = "RETURN"), cancellable=true)
    private void getName(CallbackInfoReturnable<Text> info) {
		Optional<TrinketComponent> option = TrinketsApi.getTrinketComponent(this);
		if(option.isEmpty()) {
			info.cancel();
		}
 		TrinketComponent trinkets = option.get();
		if(trinkets==null) {
			info.cancel();
		}
		List<Pair<SlotReference,ItemStack>> list = trinkets.getEquipped(Items.NAME_TAG);
		if(list.isEmpty()) {
			info.cancel();
		}
		for(Pair<SlotReference,ItemStack> pair : list) {
			ItemStack nametag = pair.getRight();
			if(!nametag.hasCustomName()) {
				info.cancel();
			} else {
				info.setReturnValue(nametag.getName());
			}
		}
		info.cancel();
    }
}