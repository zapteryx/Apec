package Apec.Components.Gui.Menu.CustomizationMenu;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.ComponentId;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.Components.Gui.GuiIngame.GuiElements.GUIComponent;
import Apec.Components.Gui.GuiIngame.GuiElements.InfoBox;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CustomizationGui extends GuiScreen {

    List<GUIComponent> components = ((GUIModifier)ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER)).GUIComponents;
    List<Integer> xSnapPoints = new ArrayList<Integer>();
    List<Integer> ySnapPoints = new ArrayList<Integer>();

    @Override
    public void initGui() {
        super.initGui();
        final ScaledResolution sr = new ScaledResolution(mc);
        refreshSnapPoints();
        for (GUIComponent component : components) {
            if (!(component instanceof InfoBox)) {
                Vector2f v = component.getAnchorPointPosition();
                this.buttonList.add(new CustomizationGuiButton(component, xSnapPoints, ySnapPoints));
                this.buttonList.add(new CustomizationGuiSlider((int) v.x + 7, (int) v.y + 7,component));
            }
        }
        this.buttonList.add(new CustomizationResetButton(0,sr.getScaledWidth()/2 - 25,0,50,15));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        final ScaledResolution sr = new ScaledResolution(mc);
        for (GUIComponent component : components) {
            if (!(component instanceof InfoBox)) {
                drawRect((int) component.getRealAnchorPoint().x, (int) component.getRealAnchorPoint().y, (int) component.getBoundingPoint().x, (int) component.getBoundingPoint().y, 0x55ffffff);
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);

    }

    @Override
    public void drawDefaultBackground() {

    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        final ScaledResolution sr = new ScaledResolution(mc);
        refreshSnapPoints();
        SaveDeltas();
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        refreshSnapPoints();
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            for (GuiButton guiButton : this.buttonList) {
                if (guiButton.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY) && guiButton instanceof CustomizationGuiSlider) {
                    ((CustomizationGuiSlider) guiButton).userStartedDragging(mouseX, mouseY);
                    return;
                }
            }
            for (GuiButton guiButton : this.buttonList) {
                if (guiButton.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY) && guiButton instanceof CustomizationGuiButton) {
                    ((CustomizationGuiButton) guiButton).userStartedDragging(mouseX, mouseY);
                    return;
                }
            }
            for (GuiButton guiButton : this.buttonList) {
                if (guiButton.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY) && guiButton instanceof CustomizationResetButton) {
                    for (GUIComponent component : components) {
                        component.setDelta_position(new Vector2f(0,0));
                        component.setScale(1);
                    }
                }
            }
        } else if (mouseButton == 1) {
            for (GuiButton guiButton : this.buttonList) {
                if (guiButton.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY) && guiButton instanceof CustomizationGuiButton) {
                    ((CustomizationGuiButton) guiButton).setDeltaToZero();
                    return;
                } else if (guiButton.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY) && guiButton instanceof CustomizationGuiSlider) {
                    ((CustomizationGuiSlider) guiButton).resetScale();
                    return;
                }
            }
        }
    }

    long st = 0;

    @Override
    public void handleKeyboardInput() throws IOException {
        super.handleKeyboardInput();
        int key = Keyboard.getEventKey();
        if ((key == Keyboard.KEY_LEFT || key == Keyboard.KEY_RIGHT || key == Keyboard.KEY_UP || key == Keyboard.KEY_DOWN) && System.currentTimeMillis() - st > 200) {
            st = System.currentTimeMillis();
            for (GuiButton guiButton : this.buttonList) {
                if (guiButton instanceof CustomizationGuiButton) {
                    switch (key) {
                        case Keyboard.KEY_LEFT:
                            ((CustomizationGuiButton) guiButton).fineRepositioning(new Vector2f(-1, 0));
                            break;
                        case Keyboard.KEY_RIGHT:
                            ((CustomizationGuiButton) guiButton).fineRepositioning(new Vector2f(1, 0));
                            break;
                        case Keyboard.KEY_UP:
                            ((CustomizationGuiButton) guiButton).fineRepositioning(new Vector2f(0, -1));
                            break;
                        case Keyboard.KEY_DOWN:
                            ((CustomizationGuiButton) guiButton).fineRepositioning(new Vector2f(0, 1));
                            break;
                    }
                }
            }
        }
    }

    void refreshSnapPoints() {
        xSnapPoints.clear();
        ySnapPoints.clear();
        final ScaledResolution sr = new ScaledResolution(mc);
        for (GUIComponent component : components) {
            if (!(component instanceof InfoBox)) {
                Vector2f pos = component.getRealAnchorPoint();
                Vector2f b_pos = component.getBoundingPoint();
                xSnapPoints.add(sr.getScaledWidth()/2);
                xSnapPoints.add((int) pos.x);
                ySnapPoints.add((int) pos.y);
                xSnapPoints.add((int) b_pos.x);
                xSnapPoints.add((int) b_pos.y);
                xSnapPoints.add((int) (b_pos.x/2));
                xSnapPoints.add((int) (b_pos.y/2));
            }
        }
    }

    private void SaveDeltas() {
        try {
            new File("config/Apec").mkdirs();
            new File("config/Apec/GuiDeltas.txt").createNewFile();
            FileWriter fw = new FileWriter("config/Apec/GuiDeltas.txt");
            String s = "";
            for (int i = 0;i < components.size();i++) {
                s += components.get(i).gUiComponentID.ordinal() + "#" + components.get(i).getDelta_position().x + "@" + components.get(i).getDelta_position().y + "@" + components.get(i).getScale();
                if (i != components.size() - 1) s += "\n";
            }
            fw.write(s);
            fw.close();
        } catch (IOException e) {
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error saving GUI Deltas!");
        }
    }
}
