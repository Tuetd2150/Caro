package caro.dao;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import caro.bean.Setting;
import caro.values.Value;
public class SettingDao {
    public static Setting LoadSettingInfo() {
        File file = getSettingFile();
        if (!file.exists()) {
            ensureSettingDirectory(file);
            try {
                file.createNewFile();
            } catch (Exception e1) {
                e1.printStackTrace();
                System.out.println("Co loi khi mo file setting.txt!");
                return defaultSetting();
            }
        }
        try {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            String[] rem = reader.readLine().split("[,]");

            Color backgroundColor = new Color(Integer.parseInt(rem[0]), Integer.parseInt(rem[1]), Integer.parseInt(rem[2]));
            rem = reader.readLine().split("[,]");
            Color cellColor = new Color(Integer.parseInt(rem[0]), Integer.parseInt(rem[1]), Integer.parseInt(rem[2]));
            rem = reader.readLine().split("[,]");
            Color xColor = new Color(Integer.parseInt(rem[0]), Integer.parseInt(rem[1]), Integer.parseInt(rem[2]));
            rem = reader.readLine().split("[,]");
            Color oColor = new Color(Integer.parseInt(rem[0]), Integer.parseInt(rem[1]), Integer.parseInt(rem[2]));
            int mode = Integer.parseInt(reader.readLine());

            String aiModelLine = reader.readLine();
            int aiModel = aiModelLine != null
                    ? Integer.parseInt(aiModelLine.trim())
                    : Value.DEFAULT_AI_MODEL;

            Setting setting = new Setting(backgroundColor, cellColor, xColor, oColor, mode, aiModel);
            reader.close();
            return setting;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Co loi khi mo file setting.txt!");
            return defaultSetting();
        }
    }

    private static Setting defaultSetting() {
        return new Setting(
                Value.BACKGROUND_COLOR,
                Value.CELL_COLOR,
                Value.USER_TEXT_COLOR,
                Value.AI_TEXT_COLOR,
                Value.DEFAULT_MODE,
                Value.DEFAULT_AI_MODEL
        );
    }

    public static void SaveSettingInfo(Setting setting) {
        File file = getSettingFile();
        ensureSettingDirectory(file);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e1) {
                System.out.println("File khong ton tai!. Da tao moi file setting.txt!");
            }
        }
        try {
            OutputStreamWriter writer =
                    new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8);
            writer.write(setting.getStringBackgroundColor() + "\n");
            writer.write(setting.getStringCellColor() + "\n");
            writer.write(setting.getStringXColor() + "\n");
            writer.write(setting.getStringOColor() + "\n");
            writer.write(String.valueOf(setting.getMode()) + "\n");
            writer.write(String.valueOf(setting.getAiModel()) + "\n");
            writer.close();
        } catch (Exception e1) {
            System.out.println("Co loi khi luu file setting.txt!");
        }
    }

    private static File getSettingFile() {
        return new File("file\\setting.txt");
    }

    private static void ensureSettingDirectory(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }
}
