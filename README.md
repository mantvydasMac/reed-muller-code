# Overview
An implementation of the Reed-Muller RM(1, m) code and decoding using fast hadamard transform. Encodes a bit vector, simulates sending a bit vector message through a faulty channel (message bits randomly flipped), decodes the vector.

The program GUI contains three scenarios:
- Asks the user to input a bit vector of required length. Encodes the vector, sends it through the channel, decodes the message, displays where bits were flipped.
- Takes ASCII text input and converts it to binary. Sends both an encoded and unencoded message through the channel. Compares received text.
- Converts a 24 bit format BMP image file to binary. Sends both an encoded and unencoded image through the channel. Compares the images.

# Additional implementations
To realize the scenarios, additional features are implemented:
- Simple matrix class, with concatenation and static methods for getting identity, hadamard matrices, kronecher product calculation
- ASCII to binary conversion and vice versa
- BMP to binary conversion that splits the BMP byte array to preserve image header data
- Bit vector class that wraps native Java BitSet to track length
