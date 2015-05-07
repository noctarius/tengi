package com.noctarius.tengi.cbor.model;

public enum MajorType {

    /**
     * An unsigned integer.  The 5-bit additional information
     * is either the integer itself (for additional information values 0
     * through 23) or the length of additional data.  Additional
     * information 24 means the value is represented in an additional
     * uint8_t, 25 means a uint16_t, 26 means a uint32_t, and 27 means a
     * uint64_t.  For example, the integer 10 is denoted as the one byte
     * 0b000_01010 (major type 0, additional information 10).  The
     * integer 500 would be 0b000_11001 (major type 0, additional
     * information 25) followed by the two bytes 0x01f4, which is 500 in
     * decimal.
     */
    UnsignedInteger,

    /**
     * A negative integer.  The encoding follows the rules
     * for unsigned integers (major type 0), except that the value is
     * then -1 minus the encoded unsigned integer.  For example, the
     * integer -500 would be 0b001_11001 (major type 1, additional
     * information 25) followed by the two bytes 0x01f3, which is 499 in
     * decimal.
     */
    NegativeInteger,

    /**
     * A byte string.  The string's length in bytes is
     * represented following the rules for positive integers (major type
     * 0).  For example, a byte string whose length is 5 would have an
     * initial byte of 0b010_00101 (major type 2, additional information
     * 5 for the length), followed by 5 bytes of binary content.  A byte
     * string whose length is 500 would have 3 initial bytes of
     * 0b010_11001 (major type 2, additional information 25 to indicate a
     * two-byte length) followed by the two bytes 0x01f4 for a length of
     * 500, followed by 500 bytes of binary content.
     */
    ByteString,

    /**
     * A text string, specifically a string of Unicode
     * characters that is encoded as UTF-8 [RFC3629].  The format of this
     * type is identical to that of byte strings (major type 2), that is,
     * as with major type 2, the length gives the number of bytes.  This
     * type is provided for systems that need to interpret or display
     * human-readable text, and allows the differentiation between
     * unstructured bytes and text that has a specified repertoire and
     * encoding.  In contrast to formats such as JSON, the Unicode
     * characters in this type are never escaped.  Thus, a newline
     * character (U+000A) is always represented in a string as the byte
     * 0x0a, and never as the bytes 0x5c6e (the characters "\" and "n")
     * or as 0x5c7530303061 (the characters "\", "u", "0", "0", "0", and
     * "a").
     */
    TextString,

    /**
     * An array of data items.  Arrays are also called lists,
     * sequences, or tuples.  The array's length follows the rules for
     * byte strings (major type 2), except that the length denotes the
     * number of data items, not the length in bytes that the array takes
     * up.  Items in an array do not need to all be of the same type.
     * For example, an array that contains 10 items of any type would
     * have an initial byte of 0b100_01010 (major type of 4, additional
     * information of 10 for the length) followed by the 10 remaining
     * items.
     */
    Array,

    /**
     * A map of pairs of data items.  Maps are also called
     * tables, dictionaries, hashes, or objects (in JSON).  A map is
     * comprised of pairs of data items, each pair consisting of a key
     * that is immediately followed by a value.  The map's length follows
     * the rules for byte strings (major type 2), except that the length
     * denotes the number of pairs, not the length in bytes that the map
     * takes up.  For example, a map that contains 9 pairs would have an
     * initial byte of 0b101_01001 (major type of 5, additional
     * information of 9 for the number of pairs) followed by the 18
     * remaining items.  The first item is the first key, the second item
     * is the first value, the third item is the second key, and so on.
     * A map that has duplicate keys may be well-formed, but it is not
     * valid, and thus it causes indeterminate decoding.
     */
    Map,

    /**
     * Optional semantic tagging of other major types.
     */
    SemanticTag,

    /**
     * Floating-point numbers and simple data types that need
     * no content, as well as the "break" stop code.
     */
    SimpleType

}
