import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import javax.imageio.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class extractor {
    //the rule34 pages are always https://rule34.xxx/index.php?page=post&s=list&tags=<tag>
    //the posts are https://rule34.xxx/index.php?page=post&s=view&id=<id>

    // get all ids from a given url
    private static String[] getPostIdsFromPage(String pageUrl) throws IOException {
        final Document doc = Jsoup.connect(pageUrl).get();

        // finds just the thumbnail shit
        final Elements thumbnails = doc.getElementsByAttributeValue("class", "thumb");
        String[] listOfIds = new String[42];

        for (int i = 0; i < thumbnails.size(); i++) {
            // gets the id for the posts
            Element htmlOfThumb = thumbnails.get(i).getElementsByTag("a").first();
            String idOfThumb = htmlOfThumb.id();
            idOfThumb = idOfThumb.substring(1);

            listOfIds[i] = idOfThumb;
        }

        return listOfIds;
    }

    //give a list without any null values
    private static String[] cleaned(String[] bigListOfIds) {
        int trueLength = 0;
        for (int idIndex = 0; true; idIndex++) {
            if (bigListOfIds[idIndex] == null) {
                trueLength = idIndex;
                break;
            }
        }

        return Arrays.copyOfRange(bigListOfIds, 0, trueLength);
    }

    //give all ids from a rule34 tag
    public static String[] getPostIdsFromTag(String tag, int limit) throws IOException {
        final String urlStarter = "https://rule34.xxx/index.php?page=post&s=list&tags=";
        final String pageUrlStarter = "&pid=";
        final int itemsPerPage = 42;
        final int pageLimit = limit / 42;
        String pageUrl;
        String[] bigListOfIds = new String[limit];

        for (int pageNum = 0; pageNum <= pageLimit; pageNum++) {
            //gets ids
            int postNumber = pageNum * itemsPerPage;
            pageUrl = urlStarter + tag + pageUrlStarter + postNumber;
            String[] listOfIds = extractor.getPostIdsFromPage(pageUrl);

            //stores id in the big list
            for (int idIndex = 0; idIndex < 42; idIndex++) {
                try {
                    // stops if it has run out of posts
                    if (listOfIds[idIndex] == null) {
                        return cleaned(bigListOfIds);
                    }

                    bigListOfIds[postNumber + idIndex] = listOfIds[idIndex];
                } catch (ArrayIndexOutOfBoundsException e) {
                    // stop if has enough ids
                    return bigListOfIds;
                }
            }
        }
        return null;
    }

    //saves as jpg to src folder
    private static void saveImage(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destinationFile);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }

    private static String getImageUrlFromPost(String postUrl) throws IOException {
        final Document doc = Jsoup.connect(postUrl).get();
        final Element imageElement = doc.getElementById("image");
        return imageElement.attr("src");
    }

    public static void saveImagesFromIds(String[] listOfIds) throws IOException {
        final String postUrlStart = "https://rule34.xxx/index.php?page=post&s=view&id=";
        final String fileType = ".jpg";
        for (String ids : listOfIds) {
            String postUrl = postUrlStart + ids;
            String imageFilename = ids + ".jpg";
            saveImage(getImageUrlFromPost(postUrl), imageFilename);
        }
    }
}





