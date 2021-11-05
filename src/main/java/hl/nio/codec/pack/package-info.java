/**
 * package structure ::
 * | 2 Byte  | 2 Byte   | 2 Byte | 2 Byte     | {DataLength} Byte | 4 Byte                |
 * | Version | Category | TypeId | DataLength | PackageData       | CRC32 for PackageData |
 *
 * description ::
 * Version  : 0x0100(1.0)
 * Category	: 0x0200(UserCustom), 0x0100(BuildIn)
 *
 */
package hl.nio.codec.pack;