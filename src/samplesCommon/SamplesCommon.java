package samplesCommon;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class SamplesCommon {

    public enum sampleImages {
        github("logoLib.bmp"), 
        logoLib("logoLib1.bmp"), 
        bless("bless.png"),
        blessKids("logoPrintBlesskids.png"),
        logoJaquin("logoAgroJarquin.png");
        public String imageName;

        sampleImages(String imageName) {
            this.imageName = imageName;
        }
    }

    public static BufferedImage getImage(sampleImages image) throws IOException {
        URL url = getURL(image.imageName);
        return ImageIO.read(url);

    }

    private static URL getURL(String imageName) {
        String strPath = "img/" + imageName;
        return SamplesCommon.class
                .getClassLoader()
                .getResource(strPath);
    }

}
