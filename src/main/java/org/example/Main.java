package org.example;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        long startTs = System.currentTimeMillis(); // start time

        Callable logic = () -> {
            String text = generateText("aab", 30_000);
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
        };

        List<FutureTask> futureTasks = new ArrayList<>();

        for (int i = 0; i < 25; i++) {
            FutureTask<Integer> futureTask=new FutureTask<>(logic);
            Thread thread =new Thread(futureTask);
            futureTasks.add(futureTask);
            thread.start();
        }

        int max=0;
        for (FutureTask futureTask : futureTasks) {
            max=Math.max(max,(int) futureTask.get()); // зависаем, ждём когда поток объект которого лежит в thread завершится
        }
        System.out.println("Максимальный интервал значений: " + max);
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