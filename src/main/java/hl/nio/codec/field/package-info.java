/**
 * package data structure ::
 * | 2 Byte  | 2 Byte   | 2 Byte | 2 Byte     | {DataLength} Byte | 4 Byte                |
 * | Version | Category | TypeId | DataLength | Message           | CRC32 for Message     |
 *
 * message data structure
 * | 4Byte             | X Byte      |
 * | 2 Byte   | 2 Byte | X Byte      |
 * | Category | TypeId | Fields Data |
 *
 * fields data structeure
 * | X Byte      |
 * | Fields Data |
 *
 * description ::
 * Version  : 0x0100(1.0)
 * Category	: 0x0200(UserCustom), 0x0100(BuildIn)
 */
package hl.nio.codec.field;