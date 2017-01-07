package lk.ac.mrt.distributed;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Lasantha on 07-Jan-17.
 */
public class FileNamesGenerator {

    private static String[] fileNames = {
            "Adventures of Tintin",
            "Jack and Jill",
            "Glee",
            "The Vampire Diarie",
            "King Arthur",
            "Windows XP",
            "Harry Potter",
            "Kung Fu Panda",
            "Lady Gaga",
            "Twilight",
            "Windows 8",
            "Mission Impossible",
            "Turn Up The Music",
            "Super Mario",
            "American Pickers",
            "Microsoft Office 2010",
            "Happy Feet",
            "Modern Family",
            "American Idol",
            "Hacking for Dummies"
    };

    public static ArrayList<String> getRandomFileNames() {
        ArrayList<String> files = new ArrayList<>();

        Random random = new Random();
        int count = 3 + random.nextInt(3);

        // shuffle 10 times
        for (int i = 0; i < 10; i++) {
            int a = random.nextInt(fileNames.length);
            int b = random.nextInt(fileNames.length);
            String temp = fileNames[a];
            fileNames[a] = fileNames[b];
            fileNames[b] = temp;
        }

        for (int i = 0; i < count; i++) {
            files.add(new String(fileNames[i]));
        }

        return files;
    }
}
