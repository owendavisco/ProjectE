package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.TransmuteContainer;
import moze_intel.projecte.gameObjs.tiles.TransmuteTile;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SearchUpdatePKT;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.opengl.GL11;

public class GUITransmute extends GuiContainer
{
	private static final ResourceLocation texture = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/transmute.png");
	private TransmuteTile tile;
	private GuiTextField textBoxFilter;

	int xLocation;
	int yLocation;

	public GUITransmute(InventoryPlayer invPlayer, TransmuteTile tile) 
	{
		super(new TransmuteContainer(invPlayer, tile));
		this.tile = tile;
		this.xSize = 228;
		this.ySize = 196;
	}
	
	@Override
	public void initGui() 
	{
		tile.setPlayer(Minecraft.getMinecraft().thePlayer);
		super.initGui();

		this.xLocation = (this.width - this.xSize) / 2;
		this.yLocation = (this.height - this.ySize) / 2;

		this.textBoxFilter = new GuiTextField(this.fontRendererObj, this.xLocation + 88, this.yLocation + 8, 45, 10);
		this.textBoxFilter.setText(tile.filter);
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		super.drawScreen(par1, par2, par3);
		this.textBoxFilter.drawTextBox();
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
	{
		GL11.glColor4f(1F, 1F, 1F, 1F);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int var1, int var2) 
	{
		this.fontRendererObj.drawString("Transmutation", 6, 8, 4210752);
		String emc = String.format("EMC: %,d", (int) tile.getStoredEmc());
		this.fontRendererObj.drawString(emc, 6, this.ySize - 94, 4210752);

		if (tile.learnFlag > 0)
		{
			this.fontRendererObj.drawString("L", 98, 30, 4210752);
			this.fontRendererObj.drawString("e", 99, 38, 4210752);
			this.fontRendererObj.drawString("a", 100, 46, 4210752);
			this.fontRendererObj.drawString("r", 101, 54, 4210752);
			this.fontRendererObj.drawString("n", 102, 62, 4210752);
			this.fontRendererObj.drawString("e", 103, 70, 4210752);
			this.fontRendererObj.drawString("d", 104, 78, 4210752);
			this.fontRendererObj.drawString("!", 107, 86, 4210752);
			
			tile.learnFlag--;
		}
	}
	
	@Override
	public void updateScreen() 
	{
		super.updateScreen();
		this.textBoxFilter.updateCursorCounter();
	}

	@Override
	protected void keyTyped(char par1, int par2)
	{
		if (this.textBoxFilter.isFocused()) 
		{
			this.textBoxFilter.textboxKeyTyped(par1, par2);

			String srch = this.textBoxFilter.getText().toLowerCase();

			if (!tile.filter.equals(srch)) 
			{
				PacketHandler.sendToServer(new SearchUpdatePKT(srch));
				tile.filter = srch;
				tile.updateOutputs();
			}
		}

		if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode() && !this.textBoxFilter.isFocused())
		{
			this.mc.thePlayer.closeScreen();
		}
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3)
	{
		super.mouseClicked(par1, par2, par3);
		this.textBoxFilter.mouseClicked(par1, par2, par3);
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		tile.learnFlag = 0;
	}
}
