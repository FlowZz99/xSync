package it.flowzz.xsync.utils;

import com.google.common.collect.Sets;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class PotionSerializer {

    public static String potionToBase64(Collection<PotionEffect> effects) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(effects.size());
            for (PotionEffect effect : effects) {
                dataOutput.writeObject(effect);
            }
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Could not convert effects to base64.", e);
        }
    }

    public static Collection<PotionEffect> potionFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            int size = dataInput.readInt();
            Set<PotionEffect> effects = Sets.newHashSet();
            for (int i = 0; i < size; i++) {
                effects.add((PotionEffect) dataInput.readObject());
            }
            dataInput.close();
            return effects;
        } catch (ClassNotFoundException e) {
            throw new IOException("Could not decode effect.", e);
        }
    }
}
