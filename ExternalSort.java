import java.io.*;
import java.util.*;

public class ExternalSort {
    public static void mergeSortExternal(String[] fileNames, String outputFileName, int bufferSize, boolean reverse, String dataType) {
        try {
            List<Value> values = new ArrayList<>();
            for (String fileName : fileNames) {
                BufferedReader reader = new BufferedReader(new FileReader(fileName));
                char[] chunk = new char[bufferSize];
                int bytesRead;

                while ((bytesRead = reader.read(chunk)) > 0) {
                    String chunkData = new String(chunk, 0, bytesRead);
                    String[] lines = chunkData.split("\n");

                    for (String line : lines) {
                        if (!line.trim().isEmpty()) {
                            if (dataType.equals("-i")) {
                                int value = Integer.parseInt(line);
                                values.add(new Value(value));
                            } else if (dataType.equals("-s")) {
                                values.add(new Value(line));
                            }
                        }
                    }
                }
            }
            values = mergeSort(values, reverse, dataType);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
                for (Value readerAndValue : values) {
                    if (dataType.equals("-i")) {
                        writer.write(Integer.toString(readerAndValue.getIntValue()));
                    } else if (dataType.equals("-s")) {
                        writer.write(readerAndValue.getStringValue());
                    }
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Comparator<Value> getComparator(boolean reverse, String dataType) {
        Comparator<Value> comparator = null;

        if (dataType.equals("-i")) {
            comparator = Comparator.comparingInt(Value::getIntValue);
        } else if (dataType.equals("-s")) {
            comparator = Comparator.comparing(Value::getStringValue);
        }
        return reverse ? comparator.reversed() : comparator;
    }

    private static List<Value> mergeSort(List<Value> values, boolean reverse, String dataType) {
        if (values.size() <= 1) {
            return values;
        }

        int middle = values.size() / 2;
        List<Value> left = values.subList(0, middle);
        List<Value> right = values.subList(middle, values.size());

        left = mergeSort(left, reverse, dataType);
        right = mergeSort(right, reverse, dataType);

        return merge(left, right, reverse, dataType);
    }

    private static List<Value> merge(List<Value> left, List<Value> right, boolean reverse, String dataType) {
        List<Value> result = new ArrayList<>();
        int leftIdx = 0, rightIdx = 0;

        while (leftIdx < left.size() && rightIdx < right.size()) {
            if (getComparator(reverse, dataType).compare(left.get(leftIdx), right.get(rightIdx)) <= 0) {
                result.add(left.get(leftIdx));
                leftIdx++;
            } else {
                result.add(right.get(rightIdx));
                rightIdx++;
            }
        }

        result.addAll(left.subList(leftIdx, left.size()));
        result.addAll(right.subList(rightIdx, right.size()));

        return result;
    }

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Недостаточно аргументов");
            System.exit(1);
        }

        boolean reverse = false;
        String dataType = "";
        int index = 0;
        if (args[index].equals("-a")) {
            index++;
        }
        if (args[index].equals("-d")) {
            reverse = true;
            index++;
        }
        if (args[index].equals("-s")) {
            dataType = "-s";
            index++;
        }
        if (args[index].equals("-i")) {
            dataType = "-i";
            index++;
        }

        args = Arrays.copyOfRange(args, index, args.length);

        int bufferSize = 8192;
        String outputFileName = args[0];
        String[] inputFiles = Arrays.copyOfRange(args, 1, args.length);
        if (inputFiles.length < 1) {
            System.out.println("Не менее 1 входного файла должно быть");
            System.exit(1);
        }
        mergeSortExternal(inputFiles, outputFileName, bufferSize, reverse, dataType);
    }

    static class Value {
        private int intValue;
        private String stringValue;

        public Value(int intValue) {
            this.intValue = intValue;
            this.stringValue = null;
        }

        public Value(String stringValue) {
            this.stringValue = stringValue;
            this.intValue = 0;
        }
        public int getIntValue() {
            return intValue;
        }
        public String getStringValue() {
            return stringValue;
        }

    }
}
