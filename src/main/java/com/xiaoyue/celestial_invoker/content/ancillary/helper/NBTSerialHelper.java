package com.xiaoyue.celestial_invoker.content.ancillary.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Supplier;

public class NBTSerialHelper {

    private static final Map<Class<?>, NBTSaver<?>> SAVERS = new HashMap<>();
    private static final Map<Class<?>, NBTLoader<?>> LOADERS = new HashMap<>();
    private static final Map<Class<?>, NBTFactory<?>> FACTORIES = new HashMap<>();
    private static final Map<String, Class<?>> TYPE_NAMES = new HashMap<>();

    private static final String NULL_MARKER = "_null";
    private static final String TYPE_MARKER = "_type";
    private static final String LIST_SIZE_MARKER = "_size";
    private static final String MAP_ENTRY_PREFIX = "entry_";

    static {
        registerPrimitives();
        registerMinecraftTypes();
        registerCollections();
        registerForgeTypes();
    }

    @SuppressWarnings("unchecked")
    public static <A, B> B cast(A a) {
        return ((B) a);
    }

    public static <T> void register(Class<T> type, NBTSaver<T> saver, NBTLoader<T> loader) {
        register(type, saver, loader, null);
    }

    public static <T> void register(Class<T> type, NBTSaver<T> saver, NBTLoader<T> loader, @Nullable NBTFactory<T> factory) {
        SAVERS.put(type, saver);
        LOADERS.put(type, loader);
        if (factory != null) {
            FACTORIES.put(type, factory);
        }
        TYPE_NAMES.put(type.getName(), type);
    }

    public static <T extends INBTSerializable<?>> void registerINBTSerializable(Class<T> type, NBTFactory<T> factory) {
        register(type, (tag, id, value) -> {
                    CompoundTag valueTag = new CompoundTag();
                    valueTag.put("data", value.serializeNBT());
                    tag.put(id, valueTag);
                    tag.putString(id + TYPE_MARKER, type.getName());
                }, (tag, id) -> {
                    if (!tag.contains(id)) return null;
                    CompoundTag valueTag = tag.getCompound(id);
                    T instance = factory.create();
                    instance.deserializeNBT(cast(valueTag.get("data")));
                    return instance;
                },
                factory
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> void save(CompoundTag tag, String id, @Nullable T value) {
        if (value == null) {
            tag.putBoolean(id + NULL_MARKER, true);
            return;
        }
        Class<?> valueClass = value.getClass();
        NBTSaver<T> saver = (NBTSaver<T>) SAVERS.get(valueClass);
        if (saver != null) {
            saver.save(tag, id, value);
        } else {
            for (Map.Entry<Class<?>, NBTSaver<?>> entry : SAVERS.entrySet()) {
                if (entry.getKey().isInstance(value)) {
                    ((NBTSaver<T>) entry.getValue()).save(tag, id, value);
                    return;
                }
            }
            saveViaReflection(tag, id, value);
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T load(CompoundTag tag, String id, Class<T> type) {
        if (tag.contains(id + NULL_MARKER) && tag.getBoolean(id + NULL_MARKER)) {
            return null;
        }
        if (!tag.contains(id)) {
            return null;
        }
        NBTLoader<T> loader = (NBTLoader<T>) LOADERS.get(type);
        if (loader != null) {
            return loader.load(tag, id);
        }
        if (tag.contains(id + TYPE_MARKER)) {
            String typeName = tag.getString(id + TYPE_MARKER);
            Class<?> storedType = TYPE_NAMES.get(typeName);
            if (storedType != null && type.isAssignableFrom(storedType)) {
                NBTLoader<?> storedLoader = LOADERS.get(storedType);
                if (storedLoader != null) {
                    return (T) storedLoader.load(tag, id);
                }
            }
        }
        return loadViaReflection(tag, id, type);
    }

    public static <T> T loadOrDefault(CompoundTag tag, String id, Class<T> type, T defaultValue) {
        T value = load(tag, id, type);
        return value == null ? defaultValue : value;
    }

    public static <T> Supplier<T> saveAndGetLoader(CompoundTag tag, String id, @Nullable T value) {
        save(tag, id, value);
        return () -> {
            if (value == null) return null;
            return load(tag, id, cast(value.getClass()));
        };
    }

    public static void saveAll(CompoundTag tag, Map<String, Object> values) {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            save(tag, entry.getKey(), entry.getValue());
        }
    }

    public static Map<String, Object> loadAll(CompoundTag tag, Map<String, Class<?>> fieldTypes) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Class<?>> entry : fieldTypes.entrySet()) {
            result.put(entry.getKey(), load(tag, entry.getKey(), entry.getValue()));
        }
        return result;
    }

    private static void saveViaReflection(CompoundTag tag, String id, Object value) {
        try {
            Class<?> clazz = value.getClass();
            CompoundTag objectTag = new CompoundTag();
            for (Field field : clazz.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) ||
                        Modifier.isTransient(modifiers)) {
                    continue;
                }
                field.setAccessible(true);
                Object fieldValue = field.get(value);
                save(objectTag, field.getName(), fieldValue);
            }
            tag.put(id, objectTag);
            tag.putString(id + TYPE_MARKER, clazz.getName());
        } catch (Exception e) {
            throw new RuntimeException("Objects cannot be serialized: " + value.getClass(), e);
        }
    }

    @Nullable
    private static <T> T loadViaReflection(CompoundTag tag, String id, Class<T> type) {
        try {
            if (!tag.contains(id) || !tag.contains(id + TYPE_MARKER)) {
                return null;
            }
            String typeName = tag.getString(id + TYPE_MARKER);
            Class<?> clazz = Class.forName(typeName);
            if (!type.isAssignableFrom(clazz)) {
                return null;
            }
            @SuppressWarnings("unchecked")
            T instance = (T) clazz.getDeclaredConstructor().newInstance();
            CompoundTag objectTag = tag.getCompound(id);
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (objectTag.contains(field.getName())) {
                    field.setAccessible(true);
                    Object fieldValue = load(objectTag, field.getName(), field.getType());
                    field.set(instance, fieldValue);
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Unable to deserialize objects: " + type.getName(), e);
        }
    }

    public static boolean supportsType(Class<?> type) {
        if (SAVERS.containsKey(type)) return true;

        for (Class<?> supportedType : SAVERS.keySet()) {
            if (supportedType.isAssignableFrom(type)) {
                return true;
            }
        }

        return false;
    }

    private static void registerPrimitives() {
        register(int.class, CompoundTag::putInt, CompoundTag::getInt);
        register(Integer.class, CompoundTag::putInt, CompoundTag::getInt);
        register(float.class, CompoundTag::putFloat, CompoundTag::getFloat);
        register(Float.class, CompoundTag::putFloat, CompoundTag::getFloat);
        register(double.class, CompoundTag::putDouble, CompoundTag::getDouble);
        register(Double.class, CompoundTag::putDouble, CompoundTag::getDouble);
        register(long.class, CompoundTag::putLong, CompoundTag::getLong);
        register(Long.class, CompoundTag::putLong, CompoundTag::getLong);
        register(boolean.class, CompoundTag::putBoolean, CompoundTag::getBoolean);
        register(Boolean.class, CompoundTag::putBoolean, CompoundTag::getBoolean);
        register(String.class, CompoundTag::putString, CompoundTag::getString);
        register(byte.class, CompoundTag::putByte, CompoundTag::getByte);
        register(Byte.class, CompoundTag::putByte, CompoundTag::getByte);
        register(short.class, CompoundTag::putShort, CompoundTag::getShort);
        register(Short.class, CompoundTag::putShort, CompoundTag::getShort);
        register(int[].class, CompoundTag::putIntArray, CompoundTag::getIntArray);
        register(byte[].class, CompoundTag::putByteArray, CompoundTag::getByteArray);
        register(long[].class, CompoundTag::putLongArray, CompoundTag::getLongArray);
    }

    private static void registerMinecraftTypes() {
        register(ItemStack.class, (tag, id, stack) ->
                tag.put(id, stack.save(new CompoundTag())), (tag, id) -> {
            if (!tag.contains(id, Tag.TAG_COMPOUND)) return ItemStack.EMPTY;
            return ItemStack.of(tag.getCompound(id));
        });
        register(BlockPos.class, (tag, id, pos) -> {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt("x", pos.getX());
            posTag.putInt("y", pos.getY());
            posTag.putInt("z", pos.getZ());
            tag.put(id, posTag);
        }, (tag, id) -> {
            if (!tag.contains(id, Tag.TAG_COMPOUND)) return null;
            CompoundTag posTag = tag.getCompound(id);
            return new BlockPos(posTag.getInt("x"), posTag.getInt("y"), posTag.getInt("z"));
        });
        register(Vec3.class, (tag, id, vec) -> {
            CompoundTag vecTag = new CompoundTag();
            vecTag.putDouble("x", vec.x);
            vecTag.putDouble("y", vec.y);
            vecTag.putDouble("z", vec.z);
            tag.put(id, vecTag);
        }, (tag, id) -> {
            if (!tag.contains(id, Tag.TAG_COMPOUND)) return null;
            CompoundTag vecTag = tag.getCompound(id);
            return new Vec3(vecTag.getDouble("x"), vecTag.getDouble("y"), vecTag.getDouble("z"));
        });
        register(ResourceLocation.class, (tag, id, location) ->
                        tag.putString(id, location.toString()),
                (tag, id) -> {
                    if (!tag.contains(id, Tag.TAG_STRING)) return null;
                    return new ResourceLocation(tag.getString(id));
                });
        register(UUID.class, CompoundTag::putUUID, CompoundTag::getUUID);
        register(Direction.class, (tag, id, direction) -> tag.putString(id, direction.getName()),
                (tag, id) -> {
                    if (!tag.contains(id, Tag.TAG_STRING)) return null;
                    return Direction.byName(tag.getString(id));
                });
        register(AABB.class, (tag, id, aabb) -> {
            CompoundTag aabbTag = new CompoundTag();
            aabbTag.putDouble("minX", aabb.minX);
            aabbTag.putDouble("minY", aabb.minY);
            aabbTag.putDouble("minZ", aabb.minZ);
            aabbTag.putDouble("maxX", aabb.maxX);
            aabbTag.putDouble("maxY", aabb.maxY);
            aabbTag.putDouble("maxZ", aabb.maxZ);
            tag.put(id, aabbTag);
        }, (tag, id) -> {
            if (!tag.contains(id, Tag.TAG_COMPOUND)) return null;
            CompoundTag aabbTag = tag.getCompound(id);
            return new AABB(
                    aabbTag.getDouble("minX"),
                    aabbTag.getDouble("minY"),
                    aabbTag.getDouble("minZ"),
                    aabbTag.getDouble("maxX"),
                    aabbTag.getDouble("maxY"),
                    aabbTag.getDouble("maxZ"));
        });
    }

    private static void registerCollections() {
        register(List.class, (tag, id, list) -> {
            ListTag listTag = new ListTag();
            for (Object o : list) {
                CompoundTag entryTag = new CompoundTag();
                save(entryTag, "value", o);
                listTag.add(entryTag);
            }
            tag.put(id, listTag);
            tag.putInt(id + LIST_SIZE_MARKER, list.size());
        }, (tag, id) -> {
            if (!tag.contains(id, Tag.TAG_LIST)) return new ArrayList<>();
            ListTag listTag = tag.getList(id, Tag.TAG_COMPOUND);
            List<Object> list = new ArrayList<>();
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag entryTag = listTag.getCompound(i);
                list.add(load(entryTag, "value", Object.class));
            }
            return list;
        });
        register(Map.class, (tag, id, map) -> {
            CompoundTag mapTag = new CompoundTag();
            int index = 0;
            for (Object e : map.keySet()) {
                if (e instanceof String key) {
                    CompoundTag entryTag = new CompoundTag();
                    save(entryTag, "key", key);
                    save(entryTag, "value", map.get(e));
                    mapTag.put(MAP_ENTRY_PREFIX + index, entryTag);
                    index++;
                }
            }
            tag.put(id, mapTag);
            tag.putInt(id + "_entryCount", index);
        }, (tag, id) -> {
            if (!tag.contains(id, Tag.TAG_COMPOUND)) return new HashMap<>();
            CompoundTag mapTag = tag.getCompound(id);
            int entryCount = tag.getInt(id + "_entryCount");
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < entryCount; i++) {
                String entryKey = MAP_ENTRY_PREFIX + i;
                if (mapTag.contains(entryKey)) {
                    CompoundTag entryTag = mapTag.getCompound(entryKey);
                    String key = load(entryTag, "key", String.class);
                    Object value = load(entryTag, "value", Object.class);
                    map.put(key, value);
                }
            }
            return map;
        });
        register(Set.class, (tag, id, set) -> {
            ListTag listTag = new ListTag();
            for (Object item : set) {
                CompoundTag entryTag = new CompoundTag();
                save(entryTag, "value", item);
                listTag.add(entryTag);
            }
            tag.put(id, listTag);
        }, (tag, id) -> {
            if (!tag.contains(id, Tag.TAG_LIST)) return new HashSet<>();
            ListTag listTag = tag.getList(id, Tag.TAG_COMPOUND);
            Set<Object> set = new HashSet<>();
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag entryTag = listTag.getCompound(i);
                set.add(load(entryTag, "value", Object.class));
            }
            return set;
        });
    }

    private static void registerForgeTypes() {
        register(Block.class, (tag, id, block) -> {
            ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);
            if (key != null) {
                tag.putString(id, key.toString());
            }
        }, (tag, id) -> {
            if (!tag.contains(id, Tag.TAG_STRING)) return null;
            ResourceLocation key = new ResourceLocation(tag.getString(id));
            return ForgeRegistries.BLOCKS.getValue(key);
        });
        register(Item.class, (tag, id, item) -> {
            ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
            if (key != null) {
                tag.putString(id, key.toString());
            }
        }, (tag, id) -> {
            if (!tag.contains(id, Tag.TAG_STRING)) return null;
            ResourceLocation key = new ResourceLocation(tag.getString(id));
            return ForgeRegistries.ITEMS.getValue(key);
        });
    }

    public static <T> void saveTypedList(CompoundTag tag, String id, List<T> list, Class<T> elementType) {
        ListTag listTag = new ListTag();
        for (T element : list) {
            CompoundTag elementTag = new CompoundTag();
            save(elementTag, "value", element);
            listTag.add(elementTag);
        }
        tag.put(id, listTag);
        tag.putString(id + TYPE_MARKER, elementType.getName());
    }

    public static <T> List<T> loadTypedList(CompoundTag tag, String id, Class<T> elementType) {
        if (!tag.contains(id, Tag.TAG_LIST)) return new ArrayList<>();
        ListTag listTag = tag.getList(id, Tag.TAG_COMPOUND);
        List<T> list = new ArrayList<>();
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag elementTag = listTag.getCompound(i);
            T element = load(elementTag, "value", elementType);
            if (element != null) {
                list.add(element);
            }
        }

        return list;
    }

    public static <K, V> void saveTypedMap(CompoundTag tag, String id, Map<K, V> map, Class<K> keyType, Class<V> valueType) {
        CompoundTag mapTag = new CompoundTag();
        int index = 0;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            save(entryTag, "key", entry.getKey());
            save(entryTag, "value", entry.getValue());
            mapTag.put(MAP_ENTRY_PREFIX + index, entryTag);
            index++;
        }
        tag.put(id, mapTag);
        tag.putInt(id + "_entryCount", index);
        tag.putString(id + "_keyType", keyType.getName());
        tag.putString(id + "_valueType", valueType.getName());
    }

    public static <K, V> Map<K, V> loadTypedMap(CompoundTag tag, String id, Class<K> keyType, Class<V> valueType) {
        if (!tag.contains(id, Tag.TAG_COMPOUND)) return new HashMap<>();
        CompoundTag mapTag = tag.getCompound(id);
        int entryCount = tag.getInt(id + "_entryCount");
        Map<K, V> map = new HashMap<>();
        for (int i = 0; i < entryCount; i++) {
            String entryKey = MAP_ENTRY_PREFIX + i;
            if (mapTag.contains(entryKey)) {
                CompoundTag entryTag = mapTag.getCompound(entryKey);
                K key = load(entryTag, "key", keyType);
                V value = load(entryTag, "value", valueType);
                if (key != null) {
                    map.put(key, value);
                }
            }
        }
        return map;
    }

    @FunctionalInterface
    public interface NBTSaver<T> {
        void save(CompoundTag tag, String id, T value);
    }

    @FunctionalInterface
    public interface NBTLoader<T> {
        @Nullable
        T load(CompoundTag tag, String id);
    }

    @FunctionalInterface
    public interface NBTFactory<T> {
        T create();
    }
}
