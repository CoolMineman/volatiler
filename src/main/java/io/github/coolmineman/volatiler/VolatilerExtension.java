package io.github.coolmineman.volatiler;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.ext.IExtension;
import org.spongepowered.asm.mixin.transformer.ext.ITargetClassContext;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class VolatilerExtension implements IExtension {
    private final Format timeFormat = new SimpleDateFormat("HH:mm:ss");

    private final Map<String, Map<String, String>> volatilerMap;

    public VolatilerExtension(Map<String, Map<String, String>> volatilerMap) {
        this.volatilerMap = volatilerMap;
    }

    @Override
    public boolean checkActive(MixinEnvironment environment) {
        return true;
    }

    @Override
    public void preApply(ITargetClassContext context) {
        // stub
    }

    @Override
    public void postApply(ITargetClassContext context) {
        MappingResolver mr = FabricLoader.getInstance().getMappingResolver();
        String interClass = mr.unmapClassName("intermediary", replaceSlashesWithDots(context.getClassInfo().getName()));
        Map<String, String> interFields = volatilerMap.get(interClass.replaceFirst("net.minecraft.", ""));
        if (interFields != null) {
            List<String> currentFields = new ArrayList<>();
            for (Map.Entry<String, String> entry : interFields.entrySet()) {
                currentFields.add(mr.mapFieldName("intermediary", interClass, entry.getKey(), entry.getValue()));
            }
            for (FieldNode field : context.getClassNode().fields) {
                if (currentFields.contains(field.name)) {
                    println("Making " + context.getClassInfo().getName() + "'s field " + field.name + " volatile.");
                    field.access |= Opcodes.ACC_VOLATILE;
                }
            }
        }
    }

    private static String replaceSlashesWithDots(String cname) {
        return cname.replace('/', '.');
    }

    private void println(String string) {
        System.out.println("[" + timeFormat.format(new Date()) + "] [main/INFO] (Volatiler) " + string);
    }

    @Override
    public void export(MixinEnvironment env, String name, boolean force, ClassNode classNode) {
        // stub
    }
    
}
