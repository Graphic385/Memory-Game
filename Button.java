import com.raylib.Raylib.Rectangle;
import com.raylib.Raylib.Color;
import com.raylib.Raylib.Vector2;
import static com.raylib.Raylib.DrawRectangleRounded;
import static com.raylib.Raylib.IsMouseButtonDown;
import static com.raylib.Raylib.GetMousePosition;
import static com.raylib.Raylib.MOUSE_BUTTON_LEFT;

public class Button {
    private Rectangle rect;
    private Color normalColor;
    private Color hoverColor;
    private Color pressedColor;
    private boolean wasMouseDown = false;
    private boolean clicked = false;

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

    public void draw() {
        Vector2 mouse = GetMousePosition();
        boolean mouseOver = mouse.x() >= rect.x() && mouse.x() <= rect.x() + rect.width() &&
                mouse.y() >= rect.y() && mouse.y() <= rect.y() + rect.height();
        boolean mouseDown = IsMouseButtonDown(MOUSE_BUTTON_LEFT);
        if (mouseOver && mouseDown) {
            DrawRectangleRounded(rect, 0.2f, 10, pressedColor);
        } else if (mouseOver) {
            DrawRectangleRounded(rect, 0.2f, 10, hoverColor);
        } else {
            DrawRectangleRounded(rect, 0.2f, 10, normalColor);
        }
    }

    public boolean isClicked() {
        return clicked;
    }
}
