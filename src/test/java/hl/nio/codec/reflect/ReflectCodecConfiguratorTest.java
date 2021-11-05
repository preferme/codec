package hl.nio.codec.reflect;

import hl.nio.codec.pack.PackageCodec;
import hl.nio.codec.pack.PackageCodecManager;
import hl.nio.codec.useage.LoginRequest;
import hl.nio.codec.util.ByteBufferUtil;
import hl.nio.codec.util.HexUtil;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

class ReflectCodecConfiguratorTest {


    private final ReflectCodecConfigurator configurator = new ReflectCodecConfigurator();
    private final Charset charset = Charset.forName("UTF-8");

    @Test
    void doCofiguration() throws ReflectiveOperationException {

        configurator.setScanPackages("hl.nio.codec.useage");
        configurator.doCofiguration();

        PackageCodecManager manager = configurator.getPackageCodecManager();

        LoginRequest request = new LoginRequest();

        PackageCodec<LoginRequest> codec = manager.getPackageCodec(LoginRequest.class);
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        codec.encodePackage(request, buffer);

        System.out.println(buffer.flip());
        System.out.println(ByteBufferUtil.prettyHexDump(buffer));
        // 0x 0100 -- Version
        // 0x 02000001 -- Category TypeId (LoginRequest)
        // 0x 0010 -- DataLength(16)
        // 0x 00000000 -- sessionId (null)
        // 0x 00000000 -- token (null)
        // 0x 00000000 -- loginName (null)
        // 0x 00000000 -- loginPass (null)
        // 0x ecbb4b55 -- CRC32

        request.setSessionId("session id");
        request.setToken("token");
        request.setLoginName("login name");
        request.setLoginPass("login pass");
        buffer.clear();
        codec.encodePackage(request, buffer);

        System.out.println(buffer.flip());
        System.out.println(ByteBufferUtil.prettyHexDump(buffer));
        // 0x 0100 -- Version
        // 0x 0200 0001 -- Category TypeId (LoginRequest)
        // 0x 0043 -- DataLength (67)
        // 0x 01000403 0000000a 73657373696f6e206964 -- String(10)  sessionId ("session id")
        // 0x 01000403 00000005 746f6b656e -- String(5) token ("token")
        // 0x 01000403 0000000a 6c6f67696e206e616d65 -- String(10) loginName ("login name")
        // 0x 01000403 0000000a 6c6f67696e2070617373 -- String(10) loginPass ("login pass")
        // 0x d5af8ed1 -- CRC32
        System.out.println("sessionId : \t" + new String(HexUtil.decodeHexDump("73657373696f6e206964"),charset));
        System.out.println("token     : \t" + new String(HexUtil.decodeHexDump("746f6b656e"),charset));
        System.out.println("loginName : \t" + new String(HexUtil.decodeHexDump("6c6f67696e206e616d65"),charset));
        System.out.println("loginPass : \t" + new String(HexUtil.decodeHexDump("6c6f67696e2070617373"),charset));

//        System.out.println(ByteBufferUtil.prettyHexDump(buffer));
        long codecKey = buffer.getLong(buffer.position()) >>> 16;
        codec = manager.getPackageCodec(codecKey);
        LoginRequest result = codec.decodePackage(buffer);
        System.out.println(request);
        System.out.println(result);
        System.out.println(result.getSessionId());
        System.out.println(result.getToken());
        System.out.println(result.getLoginName());
        System.out.println(result.getLoginPass());
    }


}