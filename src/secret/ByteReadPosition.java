package secret;

public class ByteReadPosition {

    public final byte readByte;
    public final int imageBytePosition;
    public final int imageBitPosition;

    public ByteReadPosition(byte readByte, int imageBytePosition, int imageBitPosition){
        this.readByte = readByte;
        this.imageBytePosition = imageBytePosition;
        this.imageBitPosition = imageBitPosition;
    }
}
