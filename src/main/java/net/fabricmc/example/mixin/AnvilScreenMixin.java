package net.fabricmc.example.mixin;

import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.SlotActionType;

import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {
	MinecraftClient mc = MinecraftClient.getInstance();

	@Inject(at = @At("HEAD"), method = "keyPressed(III)Z", cancellable = true)
	private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info) {
		if (keyCode == GLFW.GLFW_KEY_ENTER) {
			AnvilScreen mythis = (AnvilScreen)(Object)this;
			int syncId = mythis.getScreenHandler().syncId;

			// Remember what we are naming the item
			String original_text = ((AnvilScreenAccessor)mythis).getNameField().getText();

			// Get the info about the first two slots
			ItemStack first = mythis.getScreenHandler().getSlot(0).getStack();
			ItemStack second = mythis.getScreenHandler().getSlot(1).getStack();
			
			// Find and plan out our two replacement items if we will do them
			List<ItemStack> stacks = mythis.getScreenHandler().getStacks();
			int first_slot = -1;
			int second_slot = -1;
			if (second.getItem() == Items.AIR) {
				second_slot = -2;
			}
			// TODO restrict the range better
			for (int i = 2; i < 39; i++) {
				if (first_slot == -1 && isStackEqual(stacks.get(i), first)) {
					first_slot = i;
					if (second_slot != -1) {
						break;
					}
				} else if (second_slot == -1 && isStackEqual(stacks.get(i), second)) {
					second_slot = i;
					if (first_slot != -1) {
						break;
					}
				}
			}

			// Drop the item we want to craft
			mc.interactionManager.clickSlot(syncId, 2, 1, SlotActionType.THROW, mc.player);

			// Actually move in the two slots, only if both are present
			if (first_slot != -1 && second_slot != -1) {
				mc.interactionManager.clickSlot(syncId, first_slot, 1, SlotActionType.QUICK_MOVE, mc.player);
				if (second_slot != -2) {
					mc.interactionManager.clickSlot(syncId, second_slot, 1, SlotActionType.QUICK_MOVE, mc.player);
				}
			}

			// Set the name to what it was previously
			((AnvilScreenAccessor)mythis).getNameField().setText(original_text);
		}
	}

	private static boolean isStackEqual(ItemStack a, ItemStack b) {
		// Ensure the item types are the same
		if (a.getItem() != b.getItem()) {
			return false;
		}
		// Ensure the names are the same
		if (!a.getName().asString().equals(b.getName().asString())) {
			return false;
		}
		// Ensure the enchantments are the same
		NbtList aEnch = a.getEnchantments();
		NbtList bEnch = b.getEnchantments();
		if (aEnch.size() != bEnch.size()) {
			return false;
		}
		for (NbtElement ench : aEnch) {
			if (!bEnch.contains(ench)) {
				return false;
			}
		}
		return true;
	}
}
