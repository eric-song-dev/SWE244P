package assignment3_3;

public class CountUtils {

    private static int totalSentMessageCount = 0;
    private static int totalReceivedMessageCount = 0;

    public static int getTotalSentMessageCount() {
        return totalSentMessageCount;
    }

    public static int getTotalReceivedMessageCount() {
        return totalReceivedMessageCount;
    }

    public static void addSentMessageCount(int newSentMessageCount) {
        totalSentMessageCount += newSentMessageCount;
    }

    public static void addReceivedMessageCount(int newReceivedMessageCount) {
        totalReceivedMessageCount += newReceivedMessageCount;
    }
}
