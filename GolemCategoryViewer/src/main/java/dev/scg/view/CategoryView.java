package dev.scg.view;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/** Adds a colour-coded "Category: ..." line to item tooltips. */
public final class CategoryView {

    private static final int GRAY = 0xAAAAAA;
    private static final int DARK_GRAY = 0x777777;

    private static Supplier<Map<String, List<String>>> source;
    private static volatile CategoryRegistry registry;
    private static volatile List<String> order = List.of();

    private CategoryView() {
    }

    /** The source is read the first time a tooltip needs it, once the item registry is ready. */
    public static void init(Supplier<Map<String, List<String>>> categorySource) {
        source = categorySource;
        registry = null;
    }

    public static void addTooltip(ItemStack stack, List<Component> lines) {
        if (stack.isEmpty()) {
            return;
        }
        CategoryRegistry reg = registry();
        if (reg == null) {
            return;
        }
        String category = reg.categoryOf(stack.getItem());
        if (category == null) {
            lines.add(Component.literal("\u25A1 No category").withColor(DARK_GRAY)
                    .append(Component.literal("  (golems match it only to itself)").withColor(DARK_GRAY)));
            return;
        }
        int rgb = colorOf(category);
        int size = reg.sizes().getOrDefault(category, 0);
        lines.add(Component.literal("\u25A0 ").withColor(rgb)
                .append(Component.literal("Category: ").withColor(GRAY))
                .append(Component.literal(category).withColor(rgb))
                .append(Component.literal("  (" + size + " items)").withColor(DARK_GRAY)));
    }

    /** Same category -> same colour, every run. Hues are spread by the golden ratio so neighbours differ. */
    public static int colorOf(String category) {
        int index = Math.max(0, order.indexOf(category));
        float hue = (index * 0.618034f) % 1.0f;
        return java.awt.Color.HSBtoRGB(hue, 0.55f, 1.0f) & 0xFFFFFF;
    }

    private static CategoryRegistry registry() {
        CategoryRegistry reg = registry;
        if (reg == null && source != null) {
            synchronized (CategoryView.class) {
                if (registry == null) {
                    Map<String, List<String>> raw = source.get();
                    CategoryRegistry built = CategoryRegistry.build(raw);
                    order = new ArrayList<>(raw.keySet());
                    registry = built;
                }
                reg = registry;
            }
        }
        return reg;
    }
}
