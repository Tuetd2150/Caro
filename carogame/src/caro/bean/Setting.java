package caro.bean;
import caro.bo.ai.AIConfig;
import caro.dao.SettingDao;
import caro.values.Value;

import java.awt.Color;
public class Setting {
    private Color backgroundColor;
    private Color cellColor;
    private Color xColor;
    private Color oColor;
    private int mode;
    private int aiModel;

    public Setting(Color backgroundColor, Color cellColor, Color xColor, Color oColor, int mode) {
        this(backgroundColor, cellColor, xColor, oColor, mode, Value.DEFAULT_AI_MODEL);
    }

    public Setting(Color backgroundColor, Color cellColor, Color xColor, Color oColor, int mode, int aiModel) {
        super();
        this.backgroundColor = backgroundColor;
        this.cellColor = cellColor;
        this.xColor = xColor;
        this.oColor = oColor;
        this.mode = mode;
        this.aiModel = aiModel;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public String getStringBackgroundColor() {
        String result = String.valueOf(backgroundColor.getRed())+","+String.valueOf(backgroundColor.getGreen())+","+String.valueOf(backgroundColor.getBlue());
        return result;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        SettingDao.SaveSettingInfo(this);
    }

    public Color getCellColor() {
        return cellColor;
    }

    public String getStringCellColor() {
        String result = String.valueOf(cellColor.getRed())+","+String.valueOf(cellColor.getGreen())+","+String.valueOf(cellColor.getBlue());
        return result;
    }

    public void setCellColor(Color cellColor) {
        this.cellColor = cellColor;
        SettingDao.SaveSettingInfo(this);
    }

    public Color getxColor() {
        return xColor;
    }

    public String getStringXColor() {
        String result = String.valueOf(xColor.getRed())+","+String.valueOf(xColor.getGreen())+","+String.valueOf(xColor.getBlue());
        return result;
    }

    public void setxColor(Color xColor) {
        this.xColor = xColor;
        SettingDao.SaveSettingInfo(this);
    }

    public Color getoColor() {
        return oColor;
    }

    public String getStringOColor() {
        String result = String.valueOf(oColor.getRed())+","+String.valueOf(oColor.getGreen())+","+String.valueOf(oColor.getBlue());
        return result;
    }

    public void setoColor(Color oColor) {
        this.oColor = oColor;
        SettingDao.SaveSettingInfo(this);
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
        SettingDao.SaveSettingInfo(this);
    }

    public int getAiModel() {
        return aiModel;
    }

    public AIConfig getAiConfig() {
        return AIConfig.fromCode(aiModel);
    }

    public void setAiModel(int aiModel) {
        this.aiModel = aiModel;
        SettingDao.SaveSettingInfo(this);
    }

    public String getAiModelDisplayName() {
        return getAiConfig().getDisplayName();
    }
}
