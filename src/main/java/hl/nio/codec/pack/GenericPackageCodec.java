package hl.nio.codec.pack;

import hl.nio.codec.Category;
import hl.nio.codec.Version;

public interface GenericPackageCodec<T> extends PackageCodec<T> {

    Class<T> getPackageType();

    Version getVersion();

    Category getCategory();

    short getTypeId();

}
