package fi.dy.masa.malilib.test.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import fi.dy.masa.malilib.util.input.InputUtils;
import fi.dy.masa.malilib.util.input.ScanCodes;
import fi.dy.masa.malilib.util.log.AnsiLogger;

public class TestKeybindTick implements IClientTickHandler
{
	public static final TestKeybindTick INSTANCE = new TestKeybindTick();
	private static final AnsiLogger LOGGER = new AnsiLogger(TestKeybindTick.class);

	private TestKeybindTick() {}

	@Override
	public void onClientTick(Minecraft mc)
	{
		if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() &&
			MaLiLibConfigs.Debug.KEYBIND_DEBUG.getBooleanValue())
		{
			this.run(mc);
		}
	}

	private void run(Minecraft mc)
	{
		KeyMapping attack = mc.options.keyAttack;
		KeyMapping use = mc.options.keyUse;
		KeyMapping jump = mc.options.keyJump;
		KeyMapping up = mc.options.keyUp;
		InputConstants.Key attackKey = InputUtils.getBoundKey(attack);
		InputConstants.Key useKey = InputUtils.getBoundKey(use);
		InputConstants.Key jumpKey = InputUtils.getBoundKey(jump);
		InputConstants.Key upKey = InputUtils.getBoundKey(up);

		final boolean attackMouse = attackKey.getType() == InputConstants.Type.MOUSE;
		final boolean useMouse = useKey.getType() == InputConstants.Type.MOUSE;
		final boolean jumpMouse = jumpKey.getType() == InputConstants.Type.MOUSE;
		final boolean upMouse = upKey.getType() == InputConstants.Type.MOUSE;

		final int attackCode = attackKey.getValue() - (attackMouse ? ScanCodes.OFFSET_MOUSE : 0);
		final int useCode = useKey.getValue() - (useMouse ? ScanCodes.OFFSET_MOUSE : 0);
		final int jumpCode = jumpKey.getValue() - (jumpMouse ? ScanCodes.OFFSET_MOUSE : 0);
		final int upCode = upKey.getValue() - (upMouse ? ScanCodes.OFFSET_MOUSE : 0);

		if (attack.isDown())
		{
			LOGGER.debug("[ATK] attack.isDown({})", attackCode);
		}
		if (use.isDown())
		{
			LOGGER.debug("[USE] use.isDown({})", useCode);
		}
		if (jump.isDown())
		{
			LOGGER.debug("[JMP] jump.isDown({})", jumpCode);
		}
		if (up.isDown())
		{
			LOGGER.debug("[FWD] up.isDown({})", upCode);
		}

		final boolean attackHeld = attackMouse ? InputUtils.isMouseHeld(attackCode + ScanCodes.OFFSET_MOUSE) : InputUtils.isKeyHeld(attackCode);
		final boolean useHeld = useMouse ? InputUtils.isMouseHeld(useCode + ScanCodes.OFFSET_MOUSE) : InputUtils.isKeyHeld(useCode);
		final boolean jumpHeld = jumpMouse ? InputUtils.isMouseHeld(jumpCode + ScanCodes.OFFSET_MOUSE) : InputUtils.isKeyHeld(jumpCode);
		final boolean upHeld = upMouse ? InputUtils.isMouseHeld(upCode + ScanCodes.OFFSET_MOUSE) : InputUtils.isKeyHeld(upCode);

		if (attackHeld)
		{
			LOGGER.debug("[ATK] attackHeld({}) = true", attackCode);
		}
		if (useHeld)
		{
			LOGGER.debug("[USE] useHeld({}) = true", useCode);
		}
		if (jumpHeld)
		{
			LOGGER.debug("[JMP] jumpHeld({}) = true", jumpCode);
		}
		if (upHeld)
		{
			LOGGER.debug("[FWD] upHeld({}) = true", upCode);
		}

		final boolean attackDown = KeybindMulti.isKeyDown(attackCode);
		final boolean useDown = KeybindMulti.isKeyDown(useCode);
		final boolean jumpDown = KeybindMulti.isKeyDown(jumpCode);
		final boolean upDown = KeybindMulti.isKeyDown(upCode);

		if (attackDown)
		{
			LOGGER.debug("[ATK] attackDown({}) = true", attackCode);
		}
		if (useDown)
		{
			LOGGER.debug("[USE] useDown({}) = true", useCode);
		}
		if (jumpDown)
		{
			LOGGER.debug("[JMP] jumpDown({}) = true", jumpCode);
		}
		if (upDown)
		{
			LOGGER.debug("[FWD] upDown({}) = true", upCode);
		}

		final boolean attackPressed = KeybindMulti.isPressedKey(attackCode);
		final boolean usePressed = KeybindMulti.isPressedKey(useCode);
		final boolean jumpPressed = KeybindMulti.isPressedKey(jumpCode);
		final boolean upPressed = KeybindMulti.isPressedKey(upCode);

		if (attackPressed)
		{
			LOGGER.debug("[ATK] attackPressed({}) = true", attackCode);
		}
		if (usePressed)
		{
			LOGGER.debug("[USE] usePressed({}) = true", useCode);
		}
		if (jumpPressed)
		{
			LOGGER.debug("[JMP] jumpPressed({}) = true", jumpCode);
		}
		if (upPressed)
		{
			LOGGER.debug("[FWD] upPressed({}) = true", upCode);
		}
	}
}
