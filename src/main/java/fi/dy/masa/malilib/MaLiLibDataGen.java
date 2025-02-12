package fi.dy.masa.malilib;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import fi.dy.masa.malilib.datagen.BlockTagDataGenerator;

public class MaLiLibDataGen implements DataGeneratorEntrypoint
{
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator)
    {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(BlockTagDataGenerator::new);
        //pack.addProvider(ItemTagGenerator::new);
    }
}
