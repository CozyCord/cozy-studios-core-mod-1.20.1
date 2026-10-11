package net.cozystudios.cozystudioscore.mixin.compat;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(targets = "net.satisfy.vinery.core.util.WineYears")
public class VineryWineAgingSleepMixin {

    @Shadow @Final
    public static int DAYS_PER_YEAR;

    @Overwrite(remap = false)
    public static int getYear(World world) {
        if (world == null) return 0;
        int daysPerYear = Math.max(1, DAYS_PER_YEAR);
        return (int) (world.getTimeOfDay() / 24000L / daysPerYear);
    }

    @Overwrite(remap = false)
    public static int getDays(World world) {
        if (world == null) return 0;
        return (int) (world.getTimeOfDay() / 24000L);
    }
}
