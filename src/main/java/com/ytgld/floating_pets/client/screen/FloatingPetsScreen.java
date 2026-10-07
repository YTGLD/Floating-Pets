package com.ytgld.floating_pets.client.screen;

import com.ytgld.floating_pets.FloatingPets;
import com.ytgld.floating_pets.client.Light;
import com.ytgld.floating_pets.client.screen.tool.BookPageFinder;
import com.ytgld.floating_pets.client.screen.tool.RegisterBookPage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class FloatingPetsScreen extends Screen {
    private static final Component TITLE = Component.translatable("book.edit.title");
    public final Player player;
    public float offsetX = 0;
    public float offsetY = 0;
    public boolean dragging = false;
    public double lastMouseX;
    public double lastMouseY;
    public float targetOffsetX;
    public float targetOffsetY;
    public static final float DRAG_SPEED = 0.5f;
    private final List<FloatingPetsPage> floatingPetsPages = new ArrayList<>();
    public boolean isMouseClicked = false; /* * 当前点击的条目。 */
    public FloatingPetsPage lastGuiAdd = null; /* * 当前点击条目的 Item。 */
    public Item lastItem = ItemStack.EMPTY.getItem();
    public int backAlpha = 0;

    public FloatingPetsScreen(Player player) {
        super(TITLE);
        this.player = player;
    }

    @Override
    protected void init() {
        super.init();
        for (RegisterBookPage registerItemConfig : BookPageFinder.getModPlugins()) {
            registerItemConfig.addPage(floatingPetsPages);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        targetOffset();
        float s = 1.2f;
        int xo = (int) ((this.width - 255 * s) / 2);
        int yo = (int) ((this.height - 155 * s) / 2);
        for (FloatingPetsPage ciBookGuiAdd : floatingPetsPages) {
            addItem(ciBookGuiAdd, graphics, xo, yo, mouseX, mouseY);
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(FloatingPets.MODID,
                        "textures/gui/book/black_all.png"),
                (int) ((this.width - 1024 * s) / 2), (int) ((this.height - 1024 * s) / 2),

                0.0F, 0.0F,
                (int) (1024 * s), (int) (1024 * s), (int) (1024 * s), (int) (1024 * s),

                Light.ARGB.color(backAlpha,255,255,255));
        if (lastGuiAdd!=null) {
            int size = 128;
            graphics.blit(RenderPipelines.GUI_TEXTURED, itemImage(lastGuiAdd.item),
                    graphics.guiWidth() / 2 - size / 2, graphics.guiHeight() / 2 - size / 2,
                    0.0F, 0.0F,
                    size, size, size, size,
                    Light.ARGB.color(backAlpha / 3, 255, 255, 255));
        }
        addText(lastGuiAdd, graphics, xo, yo, mouseX,mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 1) {
            if (!isMouseClicked) {
                dragging = true;
                lastMouseX = event.x();
                lastMouseY = event.y();
                FloatingPetsPage clicked = findEntryAt(event.x(), event.y());
                if (clicked != null) {
                    isMouseClicked = true;
                    lastGuiAdd = clicked;
                    lastItem = clicked.item;
                    sound(SoundEvents.BOOK_PAGE_TURN,3);
                }
            }
        } else {
            isMouseClicked = false;
            lastGuiAdd = null;
            lastItem = ItemStack.EMPTY.getItem();
            sound(SoundEvents.BOOK_PAGE_TURN,3);
        }
        return super.mouseClicked(event, doubleClick);
    }
    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        float mouseX = (float) event.x();
        float mouseY = (float) event.y();
        if (dragging) {
            targetOffsetX += (float) (mouseX - lastMouseX) * DRAG_SPEED;
            targetOffsetY += (float) (mouseY - lastMouseY) * DRAG_SPEED;
            int size = 350;
            targetOffsetX = Math.clamp(targetOffsetX, -size, size);
            targetOffsetY = Math.clamp(targetOffsetY, -size, size);
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        dragging = false;
        return super.mouseReleased(event);
    }
    private FloatingPetsPage findEntryAt(double mouseX, double mouseY) {
        float s = 1.2f;
        int xo = (int) ((this.width - 255 * s) / 2);
        int yo = (int) ((this.height - 155 * s) / 2);
        for (FloatingPetsPage floatingPetsPage : floatingPetsPages) {
            int centerX = (int) (xo + 252 / 2f + floatingPetsPage.vecPos.x + offsetX);
            int centerY = (int) (yo + 140 / 2f + floatingPetsPage.vecPos.y + offsetY);
            if (mouseX >= centerX - 10 && mouseX <= centerX + 10 && mouseY >= centerY - 10 && mouseY <= centerY + 10) {
                return floatingPetsPage;
            }
        }
        return null;
    }

    public void targetOffset() {
        offsetX += (targetOffsetX - offsetX) * 0.15f;
        offsetY += (targetOffsetY - offsetY) * 0.15f;
    }
    public static void sound(SoundEvent event, float v){
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(event, 1, v));
    }

    @Override
    public void tick() {
        super.tick();
        if (isMouseClicked){
            if (backAlpha < 255) {
                backAlpha +=15;
                backAlpha = Math.min(255, backAlpha);
            }
        }else if (backAlpha > 0) {
            backAlpha -= 15;
            backAlpha = Math.max(0, backAlpha);
        }
    }

    public void addItem(FloatingPetsPage ciBookGuiAdd, GuiGraphicsExtractor graphics, int windowLeft, int windowTop, int mouseX, int mouseY) {
        int centerX = (int) (windowLeft + 252 / 2f + ciBookGuiAdd.vecPos.x + offsetX);
        int centerY = (int) (windowTop + 140 / 2f + ciBookGuiAdd.vecPos.y + offsetY);
        ItemStack stack = new ItemStack(ciBookGuiAdd.item);
        graphics.blit(RenderPipelines.GUI_TEXTURED,
                ciBookGuiAdd.thePage.identifier,
                centerX - 9, centerY - 9, 0, 0, 18, 18, 18, 18);
        int color = ciBookGuiAdd.colorText;
        int rs = (color >> 16) & 0xFF;
        int gs = (color >> 8) & 0xFF;
        int bs = color & 0xFF;
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(centerX, centerY);
        pose.translate(-8, -8);
        graphics.item(stack, 0, 0);
        pose.popMatrix();

        if (ciBookGuiAdd.arrowDegree !=null){
            pose.pushMatrix();
            pose.translate(centerX, centerY);
            pose.rotate(ciBookGuiAdd.arrowDegree.d);
            pose.translate(-8, -24);
            graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(FloatingPets.MODID,
                    "textures/gui/arrow.png"),0,0,0,0,16,16,16,16);
            pose.popMatrix();
        }
    }
    private static final float TEXT_COLOR_RADIUS = 80f;
    public void addText(
            FloatingPetsPage ciBookGuiAdd,
            GuiGraphicsExtractor graphics,
            int windowLeft,
            int windowTop,
            int mouseX,
            int mouseY
    ) {
        Minecraft mc = Minecraft.getInstance();

        if (!isMouseClicked) {
            return;
        }

        float textX = windowLeft;
        float textY = windowTop;

        graphics.pose().pushMatrix();

        int mainLines = renderColorfulText(
                graphics,
                mc.font,
                ciBookGuiAdd.mainText,
                textX,
                textY,
                ciBookGuiAdd.colorMain,
                mouseX,
                mouseY,
                1.5f
        );

        graphics.pose().popMatrix();

        float currentY = textY + mainLines * mc.font.lineHeight * 1.5f;

        for (Component text : ciBookGuiAdd.text) {

            int lineCount = renderColorfulText(
                    graphics,
                    mc.font,
                    text,
                    textX,
                    currentY,
                    ciBookGuiAdd.colorText,
                    mouseX,
                    mouseY,
                    1.5f
            );

            currentY += lineCount * mc.font.lineHeight * 1.5f;
        }
    }
    private int renderColorfulText(
            GuiGraphicsExtractor graphics,
            Font font,
            Component component,
            float x,
            float y,
            int originalColor,
            float mouseX,
            float mouseY,
            float scale
    ) {
        String text = component.getString();

        float currentX = x;
        float currentY = y;

        float lineHeight = font.lineHeight * scale;

        int lineCount = 1;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '\n') {
                currentX = x;
                currentY += lineHeight;
                lineCount++;
                continue;
            }

            String character = String.valueOf(c);

            float charWidth = font.width(character) * scale;

            float centerX = currentX + charWidth * 0.5f;
            float centerY = currentY + lineHeight * 0.5f;

            float dx = mouseX - centerX;
            float dy = mouseY - centerY;

            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            float proximity = 1.0f - Mth.clamp(
                    distance / TEXT_COLOR_RADIUS,
                    0.0f,
                    1.0f
            );

            int color = makeColorVivid(originalColor, proximity);
            int as = (color >> 24) & 0xFF;
            int rs = (color >> 16) & 0xFF;
            int gs = (color >> 8) & 0xFF;
            int bs = color & 0xFF;

            graphics.text(
                    font,
                    character,
                    (int) currentX,
                    (int) currentY,
                    Light.ARGB.color(Math.min(backAlpha,as),rs,gs,bs),
                    false
            );

            currentX += charWidth;
        }

        return lineCount;
    }
    private static int makeColorVivid(int color, float proximity) {
        int alpha = (color >> 24) & 0xFF;
        int red   = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue  = color & 0xFF;

        float[] hsv = rgbToHsv(red, green, blue);

        float p = proximity * proximity;

        hsv[1] = Mth.lerp(
                p,
                hsv[1],
                1.0f
        );

        hsv[2] = Mth.lerp(
                p,
                hsv[2],
                1.0f
        );

        int[] rgb = hsvToRgb(
                hsv[0],
                hsv[1],
                hsv[2]
        );

        float r = rgb[0];
        float g = rgb[1];
        float b = rgb[2];

        float highlight = p * 0.8f;

        r = Mth.lerp(highlight, r, 255.0f);
        g = Mth.lerp(highlight, g, 255.0f);
        b = Mth.lerp(highlight, b, 255.0f);

        return (alpha << 24)
                | ((int) r << 16)
                | ((int) g << 8)
                | (int) b;
    }
    private static float[] rgbToHsv(int r, int g, int b) {
        float rf = r / 255.0f;
        float gf = g / 255.0f;
        float bf = b / 255.0f;

        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));

        float delta = max - min;

        float h = 0.0f;

        if (delta != 0.0f) {
            if (max == rf) {
                h = ((gf - bf) / delta) % 6.0f;
            } else if (max == gf) {
                h = ((bf - rf) / delta) + 2.0f;
            } else {
                h = ((rf - gf) / delta) + 4.0f;
            }

            h /= 6.0f;

            if (h < 0.0f) {
                h += 1.0f;
            }
        }

        float s = max == 0.0f ? 0.0f : delta / max;

        return new float[]{h, s, max};
    }
    private static int[] hsvToRgb(float h, float s, float v) {
        float r;
        float g;
        float b;

        float hh = h * 6.0f;
        int sector = (int) Math.floor(hh);
        float f = hh - sector;

        float p = v * (1.0f - s);
        float q = v * (1.0f - s * f);
        float t = v * (1.0f - s * (1.0f - f));

        switch (sector % 6) {
            case 0 -> {
                r = v;
                g = t;
                b = p;
            }
            case 1 -> {
                r = q;
                g = v;
                b = p;
            }
            case 2 -> {
                r = p;
                g = v;
                b = t;
            }
            case 3 -> {
                r = p;
                g = q;
                b = v;
            }
            case 4 -> {
                r = t;
                g = p;
                b = v;
            }
            default -> {
                r = v;
                g = p;
                b = q;
            }
        }

        return new int[]{
                Math.round(r * 255.0f),
                Math.round(g * 255.0f),
                Math.round(b * 255.0f)
        };
    }
    public static Identifier itemImage (Item item){
        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        return Identifier.fromNamespaceAndPath(itemId.getNamespace(),
                "textures/item/" + itemId.getPath() + ".png");
    };

    public static final class FloatingPetsPage {
        public final Item item;
        public final Vec2 vecPos;
        public final Component mainText;
        public final List<Component> text;
        public final int colorMain;
        public final int colorText;
        public final ThePage thePage;
        public final ArrowDegree arrowDegree;

        public FloatingPetsPage(Item item, Vec2 vecPos, Component mainText,
                                List<Component> text, int colorMain,
                                int colorText, ThePage thePage, ArrowDegree arrowDegree) {
            this.item = item;
            this.vecPos = vecPos;
            this.mainText = mainText;
            this.text = text;
            this.colorMain = colorMain;
            this.colorText = colorText;
            this.thePage = thePage;
            this.arrowDegree = arrowDegree;
        }
    }

    public static class ArrowDegree{
        public final float d;
        public ArrowDegree( float d){
            this.d = d;

        }
    }
    public enum ThePage {
        BASE(Identifier.fromNamespaceAndPath(FloatingPets.MODID, "textures/gui/book/black_all.png"));
        public final Identifier identifier;

        ThePage(Identifier identifier) {
            this.identifier = identifier;
        }

        public Identifier getIdentifier() {
            return identifier;
        }
    }

}
