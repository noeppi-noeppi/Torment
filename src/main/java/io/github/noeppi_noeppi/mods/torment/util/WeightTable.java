package io.github.noeppi_noeppi.mods.torment.util;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class WeightTable<T> {
    
    private final List<Pair<Integer, T>> entries;
    private final int totalWeight;

    private WeightTable(ImmutableList<Pair<Integer, T>> entries, int totalWeight) {
        this.entries = entries;
        this.totalWeight = totalWeight;
    }
    
    @Nullable
    public T random() {
        return random(new Random());
    }
    
    @Nullable
    public T random(Random random) {
        if (this.totalWeight <= 0) return null;
        int num = random.nextInt(this.totalWeight);
        for (Pair<Integer, T> pair : this.entries) {
            if (num < pair.getLeft()) {
                return pair.getRight();
            }
        }
        return null;
    }
    
    public boolean isEmpty() {
        return totalWeight <= 0;
    }
    
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }
    
    public static <T> WeightTable<T> empty() {
        return new WeightTable<>(ImmutableList.of(), 0);
    }
    
    public static class Builder<T> {
        
        private final ImmutableList.Builder<Pair<Integer, T>> entries = ImmutableList.builder();
        private int totalWeight = 0;
        
        private Builder() {
            
        }
        
        public Builder<T> add(T element, int weight) {
            entries.add(Pair.of(totalWeight + weight, element));
            totalWeight += weight;
            return this;
        }
        
        public WeightTable<T> build() {
            return new WeightTable<>(entries.build(), totalWeight);
        }
    }
}
