package io.github.noeppi_noeppi.mods.torment.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.github.noeppi_noeppi.libx.util.LazyValue;
import io.github.noeppi_noeppi.libx.util.ResourceList;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class RegistryList<T extends IForgeRegistryEntry<? extends T>> {
    
    private final IForgeRegistry<? extends T> registry;
    
    private final List<Supplier<Stream<T>>> suppliers = new ArrayList<>();
    private List<T> elements = null;
    private LazyValue<Set<T>> elementSet = null;

    public RegistryList(IForgeRegistry<? extends T> registry) {
        this.registry = registry;
    }
    
    @SafeVarargs
    public final RegistryList<T> add(T... elems) {
        checkNotResolved();
        suppliers.add(() -> Arrays.stream(elems));
        return this;
    }
    
    public final RegistryList<T> add(ResourceLocation... elems) {
        checkNotResolved();
        suppliers.add(() -> Arrays.stream(elems).flatMap(elem -> Optional.ofNullable(this.registry.getValue(elem)).stream()));
        return this;
    }
    
    public final RegistryList<T> add(ResourceList elems) {
        checkNotResolved();
        suppliers.add(() -> this.registry.getKeys().stream().filter(elems).map(this.registry::getValue));
        return this;
    }
    
    public T get(int index) {
        resolve();
        return this.elements.get(index);
    }
    
    public int size() {
        resolve();
        return this.elements.size();
    }
    
    public boolean contains(T elem) {
        resolve();
        return this.elementSet.get().contains(elem);
    }
    
    @Nullable
    public T random() {
        return this.random(new Random());
    }
    
    @Nullable
    public T random(Random random) {
        resolve();
        if (this.elements.isEmpty()) {
            return null;
        } else {
            return this.elements.get(random.nextInt(this.elements.size()));
        }
    }

    private void checkNotResolved() {
        if (this.elements != null) {
            throw new IllegalStateException("Can't modify to a registry list after it has been resolved.");
        }
    }
    
    private void resolve() {
        if (this.elements == null) {
            //noinspection UnstableApiUsage
            this.elements = this.suppliers.stream().flatMap(Supplier::get).collect(ImmutableList.toImmutableList());
            this.elementSet = new LazyValue<>(() -> ImmutableSet.copyOf(this.elements));
            this.suppliers.clear();
        }
    }
}
