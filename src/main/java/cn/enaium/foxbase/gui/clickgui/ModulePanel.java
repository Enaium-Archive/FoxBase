package cn.enaium.foxbase.gui.clickgui;

import cn.enaium.cf4m.CF4M;
import cn.enaium.cf4m.setting.SettingBase;
import cn.enaium.cf4m.setting.settings.*;
import cn.enaium.foxbase.gui.clickgui.setting.SettingElement;
import cn.enaium.foxbase.gui.clickgui.setting.BooleanSettingElement;
import cn.enaium.foxbase.gui.clickgui.setting.ValueSettingElement;
import cn.enaium.foxbase.utils.ColorUtils;
import cn.enaium.foxbase.utils.FontUtils;
import cn.enaium.foxbase.utils.Render2DUtils;

import java.awt.*;
import java.util.ArrayList;

/**
 * Project: FoxBase
 * -----------------------------------------------------------
 * Copyright © 2020 | Enaium | All rights reserved.
 */
public class ModulePanel {

    private Object module;
    private boolean hovered;

    private boolean displaySettingElement;

    private ArrayList<SettingElement> settingElements;

    public ModulePanel(Object module) {
        this.module = module;
        this.settingElements = new ArrayList<>();
        ArrayList<SettingBase> settings = CF4M.getInstance().module.getSettings(this.module);
        if (settings != null) {
            for (SettingBase setting : settings) {
                if (setting instanceof EnableSetting) {
                    this.settingElements.add(new BooleanSettingElement(setting));
                } else if (setting instanceof IntegerSetting || setting instanceof DoubleSetting || setting instanceof FloatSetting || setting instanceof LongSetting || setting instanceof ModeSetting) {
                    this.settingElements.add(new ValueSettingElement(setting));
                }
            }
        }
    }

    public void render(int mouseX, int mouseY, float delta, double x, double y, double width, double height) {
        this.hovered = Render2DUtils.isHovered(mouseX, mouseY, x, y, width, height);
        int color = ColorUtils.BG;
        if (CF4M.getInstance().module.isEnable(this.module)) {
            color = ColorUtils.TOGGLE;
        }
        if (this.hovered) {
            color = ColorUtils.SELECT;
        }

        Render2DUtils.drawRectWH(x, y, width, height, color);
        FontUtils.drawHVCenteredString(CF4M.getInstance().module.getName(this.module), x + width / 2, y + height / 2, Color.WHITE.getRGB());
        if (this.displaySettingElement) {
            double SettingElementY = y;
            for (SettingElement settingElement : settingElements) {
                settingElement.render(mouseX, mouseY, delta, x + width, SettingElementY, getWidestSetting(), height);
                SettingElementY += height;
            }
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (this.hovered) {
            if (button == 0) {
                CF4M.getInstance().module.enable(this.module);
            } else if (button == 1) {
                this.displaySettingElement = !displaySettingElement;
            }
        }

        for (SettingElement settingElement : settingElements) {
            settingElement.mouseClicked(mouseX, mouseY, button);
        }
    }

    private int getWidestSetting() {
        int width = 0;
        for (SettingBase m : CF4M.getInstance().module.getSettings()) {
            String name = m.getName();
            if (m instanceof IntegerSetting) {
                name = name + ":" + ((IntegerSetting) m).getCurrent();
            } else if (m instanceof DoubleSetting) {
                name = name + ":" + ((DoubleSetting) m).getCurrent();
            } else if (m instanceof FloatSetting) {
                name = name + ":" + ((FloatSetting) m).getCurrent();
            } else if (m instanceof LongSetting) {
                name = name + ":" + ((LongSetting) m).getCurrent();
            }else if (m instanceof ModeSetting) {
                name = name + ":" + ((ModeSetting) m).getCurrent();
            }
            int cWidth = FontUtils.getStringWidth(
                    name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
            if (cWidth > width) {
                width = cWidth;
            }
        }
        return width;
    }
}