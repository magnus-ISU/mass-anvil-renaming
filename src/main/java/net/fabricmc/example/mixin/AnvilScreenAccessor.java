package net.fabricmc.example.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;

@Mixin(AnvilScreen.class)
public interface AnvilScreenAccessor {
	@Accessor
	TextFieldWidget getNameField();
}