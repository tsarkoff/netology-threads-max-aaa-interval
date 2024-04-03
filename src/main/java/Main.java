import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    private static final int STRINGS_COUNT = 25;
    private static final int STRING_CHARS_COUNT = 30_000;
    private static final String CHARS = "aab";

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[STRINGS_COUNT];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText(CHARS, STRING_CHARS_COUNT);
        }

        List<Future<Integer>> tasks = new ArrayList<>();
        final ExecutorService threadPool = Executors.newFixedThreadPool(STRINGS_COUNT);
        long startTs = System.currentTimeMillis(); // start time
        for (String text : texts) {
            tasks.add(
                    threadPool.submit(
                            () -> {
                                int maxSize = 0;
                                for (int i = 0; i < text.length(); i++) {
                                    for (int j = 0; j < text.length(); j++) {
                                        if (i >= j) {
                                            continue;
                                        }
                                        boolean bFound = false;
                                        for (int k = i; k < j; k++) {
                                            if (text.charAt(k) == 'b') {
                                                bFound = true;
                                                break;
                                            }
                                        }
                                        if (!bFound && maxSize < j - i) {
                                            maxSize = j - i;
                                        }
                                    }
                                }
                                System.out.println(text.substring(0, 100) + " -> " + maxSize);
                                return maxSize;
                            }
                    ));
        }

        int maxInterval = 0;
        for (Future<Integer> task : tasks) {
            Integer currInterval = task.get();
            if (maxInterval < currInterval) {
                maxInterval = currInterval;
            }
        }
        threadPool.shutdown();

        System.out.println("Max 'aaa' interval from all strings is : " + maxInterval);
        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
