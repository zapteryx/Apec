package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.DataExtractor;
import Apec.Settings.SettingID;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;

public class XpBar extends GUIComponent {

    public XpBar () {
        super(GUIComponentID.XP_BAR);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr,boolean editingMode) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale,scale,scale);
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.XP_BAR)) {
            GuiIngame gi = Minecraft.getMinecraft().ingameGUI;

            Vector2f StatBar = this.getAnchorPointPosition(sr);
            StatBar = ApecUtils.addVec(StatBar, delta_position);

            mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));

            gi.drawTexturedModalRect((int) StatBar.x/scale, (int) StatBar.y/scale, 0, 30, 182, 5);
            gi.drawTexturedModalRect((int) StatBar.x/scale, (int) StatBar.y/scale, 0, 35, (int) (this.mc.thePlayer.experience * 182f), 5);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, ScaledResolution sr,boolean editingMode) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale,scale,scale);
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.XP_BAR)) {
            Vector2f StatBar = this.getAnchorPointPosition(sr);

            StatBar = ApecUtils.addVec(StatBar, delta_position);

            String XPString = "Lvl " + this.mc.thePlayer.experienceLevel + " XP";
            ApecUtils.drawThiccBorderString(XPString, (int) (StatBar.x/scale + 112 + 70 - mc.fontRendererObj.getStringWidth(XPString)), (int)(StatBar.y/scale - 10), 0x80ff20);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public Vector2f getAnchorPointPosition(ScaledResolution sr) {
        return new Vector2f(sr.getScaledWidth() - 190, 53);
    }

    @Override
    public Vector2f getBoundingPoint() {
        return ApecUtils.addVec(getRealAnchorPoint(new ScaledResolution(mc)),new Vector2f(182*scale,5*scale));
    }

}
