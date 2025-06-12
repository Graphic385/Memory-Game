package main;

import static com.raylib.Raylib.DrawRectangleRounded;
import static com.raylib.Raylib.GetMousePosition;
import static com.raylib.Raylib.IsMouseButtonDown;
import static com.raylib.Raylib.MOUSE_BUTTON_LEFT;

import com.raylib.Raylib.Color;
import com.raylib.Raylib.Rectangle;
import com.raylib.Raylib.Vector2;

public class Button {
    private Rectangle rect;
    private Color normalColor;
    private Color hoverColor;
    private Color pressedColor;
    private boolean wasMouseDown = false;
    private boolean clicked = false;
    private float outlineWidth = 0f;
    private Color outlineColor = null;
    private boolean hoverEnabled = true;
    private com.raylib.Raylib.Texture iconTexture = null;
    private float iconScale = 1.0f;
    private String buttonText = null;
    private int textSize = 24;
    private Color textColor = com.raylib.Colors.WHITE;
    private boolean isHighlighted = false;
    private Color highlightColor = null;
    private long highlightEndTime = 0;

    public Button(float x, float y, float width, float height, Color normal, Color hover, Color pressed) {
        rect = new Rectangle();
        rect.x(x);
        rect.y(y);
        rect.width(width);
        rect.height(height);
        normalColor = normal;
        hoverColor = hover;
        pressedColor = pressed;
    }

    public Button(float x, float y, float width, float height, Color normal) {
        rect = new Rectangle();
        rect.x(x);
        rect.y(y);
        rect.width(width);
        rect.height(height);
        normalColor = normal;
        hoverColor = normal;
        pressedColor = normal;
    }

    public Button(float x, float y, float width, float height) {
        rect = new Rectangle();
        rect.x(x);
        rect.y(y);
        rect.width(width);
        rect.height(height);
        // Default button colors
        normalColor = com.raylib.Colors.BLACK;
        hoverColor = new Color();
        hoverColor.r((byte) 204);
        hoverColor.g((byte) 204);
        hoverColor.b((byte) 204);
        hoverColor.a((byte) 255);
        pressedColor = new Color();
        pressedColor.r((byte) 170);
        pressedColor.g((byte) 170);
        pressedColor.b((byte) 170);
        pressedColor.a((byte) 255);
    }

    public void update() {
        Vector2 mouse = GetMousePosition();
        boolean mouseOver = mouse.x() >= rect.x() && mouse.x() <= rect.x() + rect.width() &&
                mouse.y() >= rect.y() && mouse.y() <= rect.y() + rect.height();
        boolean mouseDown = IsMouseButtonDown(MOUSE_BUTTON_LEFT);
        clicked = false;
        if (mouseOver && mouseDown && !wasMouseDown) {
            clicked = true;
        }
        wasMouseDown = mouseDown;
    }

    public void addOutline(float width, Color color) {
        this.outlineWidth = width;
        this.outlineColor = color;
    }

    public void disableOutline() {
        this.outlineWidth = 0f;
        this.outlineColor = null;
    }

    public void disableHover() {
        this.hoverEnabled = false;
    }

    public void addImageIcon(com.raylib.Raylib.Texture texture, float scale) {
        this.iconTexture = texture;
        this.iconScale = scale;
    }

    public void setText(String text) {
        this.buttonText = text;
    }

    public void setText(String text, int size, Color color) {
        this.buttonText = text;
        this.textSize = size;
        this.textColor = color;
    }

    public void setPosition(float x, float y) {
        rect.x(x);
        rect.y(y);
    }

    /**
     * Temporarily highlights the button with the given color for the specified
     * duration (in milliseconds).
     */
    public void highlight(Color color, int durationMs) {
        this.isHighlighted = true;
        this.highlightColor = color;
        this.highlightEndTime = System.currentTimeMillis() + durationMs;
    }

    /**
     * Highlights the button with the given color for just one draw call (frame).
     * The highlight will be cleared automatically after draw().
     */
    public void highlightOnce(Color color) {
        this.isHighlighted = true;
        this.highlightColor = color;
        this.highlightEndTime = -1; // Special value to indicate one-frame highlight
    }

    public void draw() {
        Vector2 mouse = GetMousePosition();
        boolean mouseOver = mouse.x() >= rect.x() && mouse.x() <= rect.x() + rect.width() &&
                mouse.y() >= rect.y() && mouse.y() <= rect.y() + rect.height();
        boolean mouseDown = IsMouseButtonDown(MOUSE_BUTTON_LEFT);
        // Handle highlight timing
        if (isHighlighted) {
            if (highlightEndTime == -1) {
                // One-frame highlight: clear after this draw
                isHighlighted = false;
                highlightColor = null;
                highlightEndTime = 0;
            } else if (System.currentTimeMillis() > highlightEndTime) {
                isHighlighted = false;
                highlightColor = null;
            }
        }
        // Draw outline if set
        if (outlineWidth > 0 && outlineColor != null) {
            Rectangle outlineRect = new Rectangle();
            outlineRect.x(rect.x() - outlineWidth);
            outlineRect.y(rect.y() - outlineWidth);
            outlineRect.width(rect.width() + 2 * outlineWidth);
            outlineRect.height(rect.height() + 2 * outlineWidth);
            DrawRectangleRounded(outlineRect, 0.2f, 10, outlineColor);
        }
        // Draw highlight if active
        if (isHighlighted && highlightColor != null) {
            DrawRectangleRounded(rect, 0.2f, 10, highlightColor);
        } else if (mouseOver && mouseDown) {
            DrawRectangleRounded(rect, 0.2f, 10, pressedColor);
        } else if (mouseOver && hoverEnabled) {
            DrawRectangleRounded(rect, 0.2f, 10, hoverColor);
        } else {
            DrawRectangleRounded(rect, 0.2f, 10, normalColor);
        }
        // Draw icon if set
        if (iconTexture != null) {
            float buttonW = rect.width();
            float buttonH = rect.height();
            float texW = iconTexture.width();
            float texH = iconTexture.height();
            // Calculate scale so that 1.0 means 'fit inside button', never larger
            float scaleToFit = Math.min(buttonW / texW, buttonH / texH);
            float finalScale = scaleToFit * iconScale;
            // Prevent scaling larger than button
            if (finalScale > scaleToFit)
                finalScale = scaleToFit;
            float iconW = texW * finalScale;
            float iconH = texH * finalScale;
            float iconX = rect.x() + (buttonW - iconW) / 2f;
            float iconY = rect.y() + (buttonH - iconH) / 2f;
            Vector2 iconPos = new Vector2().x(iconX).y(iconY);
            com.raylib.Raylib.DrawTextureEx(iconTexture, iconPos, 0f, finalScale,
                    com.raylib.Colors.WHITE);
        }
        // Draw text if set
        if (buttonText != null) {
            int textWidth = com.raylib.Raylib.MeasureText(buttonText, textSize);
            float textX = rect.x() + (rect.width() - textWidth) / 2f;
            float textY = rect.y() + (rect.height() - textSize) / 2f;
            com.raylib.Raylib.DrawText(buttonText, (int) textX, (int) textY, textSize, textColor);
        }
    }

    public boolean isClicked() {
        return clicked;
    }
}
