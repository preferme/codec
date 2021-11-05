package hl.nio.codec.pack.annotation;

import hl.nio.codec.Category;
import hl.nio.codec.Version;

public interface PackageAware {

    void setVersion(Version version);

    void setCategory(Category category);

    void setTypeId(int typeId);

}
