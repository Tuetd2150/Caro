package caro.bean;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.Border;

/**
 * Bo góc cho button/panel. Hỗ trợ viền mảnh, nền fill và khử răng cưa.
 */
public class RoundedBorder implements Border {

    private final int radius;
    private final Color borderColor;
    private final Color fillColor;
    private final float thickness;

    public RoundedBorder(int radius) {
        this(radius, Color.BLACK, null, 1f);
    }

    public RoundedBorder(int radius, Color borderColor, Color fillColor) {
        this(radius, borderColor, fillColor, 1.5f);
    }

    public RoundedBorder(int radius, Color borderColor, Color fillColor, float thickness) {
        this.radius = radius;
        this.borderColor = borderColor;
        this.fillColor = fillColor;
        this.thickness = thickness;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        int pad = radius / 2 + 2;
        return new Insets(pad, pad + 2, pad + 1, pad + 2);
    }

    @Override
    public boolean isBorderOpaque() {
        return fillColor != null;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = radius * 2;
        float inset = thickness / 2f;
        float w = width - thickness;
        float h = height - thickness;

        if (fillColor != null) {
            g2.setColor(fillColor);
            g2.fillRoundRect((int) (x + inset), (int) (y + inset), (int) w, (int) h, arc, arc);
        }

        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(thickness));
        g2.drawRoundRect((int) (x + inset), (int) (y + inset), (int) w, (int) h, arc, arc);
        g2.dispose();
    }
}
