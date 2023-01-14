import java.io.*;

public class Sped {
    public static void main(String[] args) throws IOException {
        extractor.saveImagesFromIds(
                extractor.getPostIdsFromTag(
                        "orange",
                        5
                )
        );
    }
}
