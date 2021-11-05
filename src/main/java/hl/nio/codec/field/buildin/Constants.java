package hl.nio.codec.field.buildin;

public interface Constants {
    
    short PRIMITIVE_BOOLEAN = (short)(0x0101);
    short PRIMITIVE_CHARACTER = (short)(0x0102);
    short PRIMITIVE_BYTE = (short)(0x0103);
    short PRIMITIVE_SHORT = (short)(0x0104);
    short PRIMITIVE_INTEGER = (short)(0x0105);
    short PRIMITIVE_LONG = (short)(0x0106);
    short PRIMITIVE_FLOAT = (short)(0x0107);
    short PRIMITIVE_DOUBLE = (short)(0x0108);

    short PRIMITIVE_OBJECT_BOOLEAN = (short)(0x0201);
    short PRIMITIVE_OBJECT_CHARACTER = (short)(0x0202);
    short PRIMITIVE_OBJECT_BYTE = (short)(0x0203);
    short PRIMITIVE_OBJECT_SHORT = (short)(0x0204);
    short PRIMITIVE_OBJECT_INTEGER = (short)(0x0205);
    short PRIMITIVE_OBJECT_LONG = (short)(0x0206);
    short PRIMITIVE_OBJECT_FLOAT = (short)(0x0207);
    short PRIMITIVE_OBJECT_DOUBLE = (short)(0x0208);

    short ARRAY = (short)(0x0300);

    short CHAR_BUFFER = (short)(0x0401);
    short SEGMENT = (short)(0x0402);
    short STRING = (short)(0x0403);
    short STRING_BUFFER = (short)(0x0404);
    short STRING_BUILDER = (short)(0x0405);

    short DATE = (short)(0x0501);
    short CALENDAR = (short)(0x0502);
    short LOCAL_DATE = (short)(0x0503);
    short LOCAL_TIME = (short)(0x0504);
    short LOCAL_DATE_TIME = (short)(0x0505);

    short ARRAY_LIST = (short)(0x0601);
    short LINKED_LIST = (short)(0x0602);
    short STACK = (short)(0x0603);
    short VECTOR = (short)(0x0604);
    short COPY_ON_WRITE_ARRAY_LIST = (short)(0x0605);
    short HASH_SET = (short)(0x0606);
    short LINKED_HASH_SET = (short)(0x0607);
    short TREE_SET = (short)(0x0608);

    short HASH_MAP = (short)(0x0701);
    short HASH_TABLE = (short)(0x0702);
    short IDENTITY_HASH_MAP = (short)(0x0703);
    short LINKED_HASH_MAP = (short)(0x0704);
    short PROPERTIES = (short)(0x0705);
    short TREE_MAP = (short)(0x0706);
    short WEAK_HASH_MAP = (short)(0x0707);
    short CONCURRENT_HASH_MAP = (short)(0x0708);
    short CONCURRENT_SKIP_LIST_MAP = (short)(0x0709);

    short ATOMIC_INTEGER = (short)(0x0801);
    short ATOMIC_LONG = (short)(0x0802);
    short BIG_INTEGER = (short)(0x0803);
    short BIG_DECIMAL = (short)(0x0804);

}
