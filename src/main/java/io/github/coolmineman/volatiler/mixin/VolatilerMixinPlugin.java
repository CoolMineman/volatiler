package io.github.coolmineman.volatiler.mixin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;

import io.github.coolmineman.volatiler.VolatilerExtension;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class VolatilerMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
        Map<String, Map<String, String>> volatilerMap = new HashMap<>();

        for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
            Path volatilerPath = modContainer.getPath("volatiler.txt");
            if (Files.isRegularFile(volatilerPath)) {
                try {
					for (String line : Files.readAllLines(volatilerPath)) {
					    String[] a = line.split("\t");
                        volatilerMap.computeIfAbsent(a[0], b -> new HashMap<>()).put(a[1], a[2]);
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
            }
        }
        ((Extensions)((IMixinTransformer)MixinEnvironment.getDefaultEnvironment().getActiveTransformer()).getExtensions()).add(new VolatilerExtension(volatilerMap));
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // stub
    }

    @Override
    public List<String> getMixins() {
        return Collections.emptyList();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // stub
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // stub
    }
    
}
