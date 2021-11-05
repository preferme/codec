package hl.nio.codec.pack;


import java.util.HashMap;

public class PackageCodecManager {

    private final HashMap<Class<?>, PackageCodec<?>> codecsByType = new HashMap<>();
    private final HashMap<Long, GenericPackageCodec<?>> codecsByKey = new HashMap<>();

    public static long maskKey(GenericPackageCodec codec) {
        return ((codec.getVersion().value() & 0xFFFFL) << 32) |
                ((codec.getCategory().value() & 0xFFFFL) << 16) |
                (codec.getTypeId() & 0xFFFFL);
    }

    public <T> PackageCodec<T> getPackageCodec(Class<T> klass) {
        return (PackageCodec<T>) codecsByType.get(klass);
    }

    public <T> PackageCodec<T> getPackageCodec(long packageCodecKey) {
        return (PackageCodec<T>) codecsByKey.get(packageCodecKey);
    }

    public <T> boolean contains(Class<T> klass) {
        return codecsByType.containsKey(klass);
    }

    private boolean contains(long packageCodecKey) {
        return codecsByKey.containsKey(packageCodecKey);
    }

    public<T> void regist(PackageCodec<T> codec, Class<T> packageType) {
        if( codec == null ) {
            throw new IllegalArgumentException("[PackageCodecManager][regist] codec can't be null.");
        }
        if (codecsByType.containsKey(packageType)) {
            throw new IllegalArgumentException("[PackageCodecManager][regist] codec["+codec.getClass()+"] has been registed for package[" + packageType.getName() + "].");
        }
        codecsByType.put(packageType, codec);

        if (codec instanceof GenericPackageCodec) {
            if( ((GenericPackageCodec) codec).getPackageType() == null ) {
                throw new IllegalArgumentException("[PackageCodecManager][regist] codec.packageType can't be null.");
            }

            long codecKey = maskKey((GenericPackageCodec) codec);
            if (contains(codecKey)) {
                throw new IllegalArgumentException("[PackageCodecManager][regist] codec[0x"+Long.toHexString(codecKey)+"] has been registed.");
            }
            codecsByKey.put(codecKey, (GenericPackageCodec) codec);
        }

    }

    @Override
    public String toString() {
        return "PackageCodecManager{" +
                "codecsByType=" + codecsByType +
                ", codecsByKey=" + codecsByKey +
                '}';
    }

}
