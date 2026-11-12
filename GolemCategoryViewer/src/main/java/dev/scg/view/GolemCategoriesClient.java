package dev.scg.view;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class GolemCategoriesClient implements ClientModInitializer {

    private static final Logger LOG = LoggerFactory.getLogger("golemcategories");
    private static final String BUNDLED = "/assets/golemcategories/categories.json";

    @Override
    public void onInitializeClient() {
        CategoryView.init(GolemCategoriesClient::load);
        ItemTooltipCallback.EVENT.register(listener());
    }

    /**
     * Fabric's tooltip callback has changed its parameter list between Minecraft versions. Instead of
     * hard-coding it, implement it with a proxy that picks the item stack and the line list out of
     * whatever arguments it is called with.
     */
    @SuppressWarnings("unchecked")
    private static ItemTooltipCallback listener() {
        return (ItemTooltipCallback) Proxy.newProxyInstance(
                ItemTooltipCallback.class.getClassLoader(),
                new Class<?>[]{ItemTooltipCallback.class},
                (proxy, method, args) -> {
                    if (method.getDeclaringClass() == Object.class) {
                        return switch (method.getName()) {
                            case "hashCode" -> System.identityHashCode(proxy);
                            case "equals" -> proxy == args[0];
                            default -> "GolemCategoriesTooltip";
                        };
                    }
                    ItemStack stack = null;
                    List<Component> lines = null;
                    for (Object arg : args == null ? new Object[0] : args) {
                        if (arg instanceof ItemStack s) {
                            stack = s;
                        } else if (arg instanceof List<?> l) {
                            lines = (List<Component>) l;
                        }
                    }
                    if (stack != null && lines != null) {
                        CategoryView.addTooltip(stack, lines);
                    }
                    return null;
                });
    }

    /** config/golem-categories.json wins over the bundled copy, so you can edit categories without rebuilding. */
    private static Map<String, List<String>> load() {
        Path override = FabricLoader.getInstance().getConfigDir().resolve("golem-categories.json");
        try {
            if (Files.exists(override)) {
                try (Reader r = Files.newBufferedReader(override, StandardCharsets.UTF_8)) {
                    LOG.info("Using categories from {}", override);
                    return CategoryData.parse(r);
                }
            }
        } catch (IOException e) {
            LOG.error("Could not read {}, using the bundled categories instead", override, e);
        }
        try (InputStream in = GolemCategoriesClient.class.getResourceAsStream(BUNDLED)) {
            if (in == null) {
                throw new IOException("missing " + BUNDLED);
            }
            return CategoryData.parse(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOG.error("Could not load the bundled categories; no category tooltips will be shown", e);
            return Map.of();
        }
    }
}
