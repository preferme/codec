package hl.nio.codec;

import hl.nio.codec.Category;
import hl.nio.codec.CodecException;
import hl.nio.codec.Version;
import hl.nio.codec.bean.BeanCodecSupport;
import hl.nio.codec.pack.annotation.PackageAware;
import hl.nio.codec.field.ObjectableFieldCodec;
import hl.nio.codec.pack.GenericPackageCodec;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

/**
 * package structure ::
 * | 2 Byte  | 2 Byte   | 2 Byte | 2 Byte     | {DataLength} Byte | 4 Byte                |
 * | Version | Category | TypeId | DataLength | MessageData       | CRC32 for MessageData |
 *
 * @param <T>
 */
public class GenericComboCodecV10<T> extends BeanCodecSupport<T> implements ObjectableFieldCodec<T>, GenericPackageCodec<T>, PackageAware {

    public final Version version = Version.V1_0;
    public final short DATA_LENGTH_PLACEHOLDER = 0;
    public final int PACKAGE_HEADER_LENGTH = 8;
    public final int MIN_PACKAGE_LENGTH = PACKAGE_HEADER_LENGTH + 4;

    private static final ThreadLocal<CRC32> CRC32 = ThreadLocal.withInitial(()->new CRC32());

    private Category category = Category.UserCustom;
    private short typeId;

    @Override
    public void setVersion(Version version) {
        if (this.version != version) {
            throw new IllegalArgumentException("[GenericReflectComboCodecV1_0][setVersion] version() in @Package must be " + this.version +" .");
        }
    }
    @Override
    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public void setTypeId(int typeId) {
        if (typeId == 0) {
            throw new IllegalArgumentException("[GenericReflectComboCodecV1_0][setTypeId] typeId() or value() in @Package must be set at class[" + type.getName() + "].");
        }
        if (typeId > Short.MAX_VALUE || typeId < Short.MIN_VALUE) {
            throw new IllegalArgumentException("[GenericReflectComboCodecV1_0][setTypeId] The value of typeId() or value() in @Package must in short range at class[" + type.getName() + "].");
        }
        this.typeId = (short) typeId;
    }

    @Override
    public Class<T> getPackageType() {
        return type;
    }

    @Override
    public Class<T> getFieldType() {
        return type;
    }

    @Override
    public int fieldKey() {
        return ((category.value()&0xFFFF) << 16) | (typeId & 0xFFFF);
    }

    public void encodePackage(T value, ByteBuffer out) throws CodecException {
        // Version
        out.putShort(version.value());
        // Category
        out.putShort(category.value());
        // TypeId
        out.putShort(typeId);
        // DataLength
        int lengthIndex = out.position();
        out.putShort(DATA_LENGTH_PLACEHOLDER);
        // PackageData
        int dataIndex = out.position();
        encodeData(value, out);
        // set DataLength
        short dataLength = (short) (out.position() - dataIndex);
        out.putShort(lengthIndex, dataLength);
        // checksum CRC32
        out.flip().position(dataIndex);
        CRC32 crc32 = CRC32.get();
        crc32.reset();
        crc32.update(out);
        long checksum = crc32.getValue();
        out.limit(out.capacity());
        out.putInt((int)checksum);
    }

    public T decodePackage(ByteBuffer in) throws CodecException {
        if (in.remaining() < MIN_PACKAGE_LENGTH) {
            throw new CodecException("[PackageMessageCodec][decode] Insufficient data in the buffer. " + in.remaining());
        }
        // Version
        Version version = Version.valueOf(in.getShort());
        if (this.version != version) {
            throw new CodecException("[PackageMessageCodec][decode] Wrong version. " + version);
        }
        // Category
        Category category = Category.valueOf(in.getShort());
        if (this.category != category) {
            throw new CodecException("[PackageMessageCodec][decode] Wrong category. " + category);
        }
        // TypeId
        short typeId = in.getShort();
        if (this.typeId != typeId) {
            throw new CodecException("[PackageMessageCodec][decode] Wrong typeId. 0x" + Integer.toHexString(typeId));
        }
        // DataLength
        short dataLength = in.getShort();
        if (in.remaining() < dataLength + 4) {
            throw new CodecException("[PackageMessageCodec][decode] Not enough data in buffer. " + in.remaining() + "/" + dataLength);
        }
        // checksum CRC32 validate
        int dataIndex = in.position();
        int limit = in.limit();
        CRC32 crc32 = CRC32.get();
        crc32.reset();
        in.limit(dataIndex + dataLength);
        crc32.update(in);
        in.position(dataIndex);
        in.limit(limit);
        long checksum = crc32.getValue();
        long dataChecksum = (long)in.getInt(dataIndex + dataLength) & 0xffffffffL;
        if (checksum != dataChecksum) {
            throw new CodecException("[PackageMessageCodec][decode] Wrong CRC data. 0x" + Long.toHexString(dataChecksum) + "/0x" + Long.toHexString(checksum));
        }
        // PackageData
        return decodeData(in);
    }

    @Override
    public Version getVersion() {
        return version;
    }

    @Override
    public Category getCategory() {
        return category;
    }

    @Override
    public short getTypeId() {
        return typeId;
    }

    public void setTypeId(short typeId) {
        this.typeId = typeId;
    }

}
